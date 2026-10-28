package ocaddon.sim;

import li.cil.oc.api.Driver;
import li.cil.oc.api.driver.DriverItem;
import li.cil.oc.api.driver.item.Slot;
import li.cil.oc.api.internal.Case;
import li.cil.oc.api.internal.Rotatable;
import li.cil.oc.api.machine.Machine;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import ocaddon.item.ItemCooler;
import ocaddon.item.ItemThermalPaste;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ThermalSimulation {
    public static final double ROOM = 25.0;
    public static final double THROTTLE_AT = 90.0;
    public static final double OVERHEAT_AT = 100.0;
    public static final double COEF_NO_PASTE = 0.2, COEF_PASTE = 1.0;
    public static final double BACK_BLOCKED_PENALTY = 0.6;

    // --- тюнинг ---
    private static final double HEAT_GAIN = 1.5;   // равновесная dT = HEAT_GAIN * heat / conductance
    private static final double RATE = 0.05;        // скорость нагрева/остывания за тик
    private static final double PASSIVE = 0.10;
    private static final double PER_CFM = 0.002;

    private static final Map<String, Double> TEMPS = new ConcurrentHashMap<>();
    public static double temperatureOf(String addr) { return TEMPS.getOrDefault(addr, ROOM); }

    private static final Logger LOG = LogManager.getLogger("OpenCooler");
    private int logTimer = 0;

    private double temp = ROOM;
    private double lastCpuTime = 0;

    public double getTemp() { return temp; }

    /** Вызывать раз в dt тиков. */
    public void step(Case c, int dt) {
        TileEntity te = (TileEntity) c;
        if (!(te instanceof IInventory)) return;
        IInventory inv = (IInventory) te;
        Machine m = c.machine();
        if (m == null || !m.isRunning()) {
            temp = Math.max(ROOM, temp - 0.2 * dt);
            return;
        }

        double ct = m.cpuTime();
        double cpuLoad = Math.min(1.0, Math.max(0, (ct - lastCpuTime) / (dt * 0.05)));
        lastCpuTime = ct;
        double gpuLoad = cpuLoad; // прямой нагрузки GPU в API нет

        int coolerLimit = Math.max(1, Math.min(3, caseTier(c) + 1));
        double heat = 0, airflow = 0;
        double coeff = COEF_NO_PASTE;
        int coolers = 0;

        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack s = inv.getStackInSlot(i);
            if (s.isEmpty()) continue;

            if (s.getItem() instanceof ItemCooler) {
                if (coolers++ < coolerLimit) airflow += ((ItemCooler) s.getItem()).airflow;
                continue;
            }
            DriverItem d = Driver.driverFor(s);
            if (d == null) continue;
            String slot = d.slot(s);
            int tier = Math.max(0, d.tier(s));

            if (Slot.CPU.equals(slot)) {
                double base = 1.0 + 0.5 * tier;
                heat += base * (0.5 + 1.5 * cpuLoad);
                if (ItemThermalPaste.hasPaste(s)) coeff = COEF_PASTE;
            } else if (Slot.Card.equals(slot) && isGpu(s)) {
                heat += 0.1 + 3.9 * gpuLoad * gpuLoad;
            } else if (Slot.Memory.equals(slot)) {
                heat += 0.2;
            } else if (Slot.HDD.equals(slot) || "eeprom".equals(slot)) {
                heat += 0.1;
            }
        }

        EnumFacing back = ((Rotatable) c).facing().getOpposite();
        BlockPos bp = te.getPos().offset(back);
        if (!te.getWorld().isAirBlock(bp)) airflow *= (1.0 - BACK_BLOCKED_PENALTY);

        double conductance = (PASSIVE + airflow * PER_CFM) * (0.4 + 0.6 * coeff);
        double dT = (heat * HEAT_GAIN - (temp - ROOM) * conductance) * RATE;
        temp = Math.max(ROOM, temp + dT * dt);

        logTimer += dt;
        if (logTimer >= 200) {
            logTimer = 0;
            LOG.info(String.format("case %s: temp=%.1fC heat=%.2f airflow=%.1f coolers=%d paste=%s load=%.2f",
                    te.getPos(), temp, heat, airflow, coolers, coeff == COEF_PASTE, cpuLoad));
        }

        String addr = m.node().address();
        if (addr != null) TEMPS.put(addr, temp);

        if (temp >= OVERHEAT_AT) {
            m.crash("Overheat: Component temperature exceeded 100°C!");
        } else if (temp >= THROTTLE_AT) {
            double k = (temp - THROTTLE_AT) / (OVERHEAT_AT - THROTTLE_AT);
            m.pause(0.05 + 0.45 * k);
        }
    }

    private static boolean isGpu(ItemStack s) {
        String n = s.getItem().getRegistryName() == null ? "" : s.getItem().getRegistryName().toString();
        return n.contains("graphicscard") || s.getTranslationKey().toLowerCase().contains("graphicscard");
    }

    /** 0 = tier1 ... 2 = tier3. */
    private static int caseTier(Case c) {
        try {
            Object t = c.getClass().getMethod("tier").invoke(c);
            return Math.min(2, ((Number) t).intValue());
        } catch (Throwable e) { return 0; }
    }
}
