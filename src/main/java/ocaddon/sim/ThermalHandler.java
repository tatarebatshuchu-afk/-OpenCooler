package ocaddon.sim;

import li.cil.oc.api.internal.Case;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.Map;
import java.util.WeakHashMap;

public class ThermalHandler {
    private static final int INTERVAL = 10;
    private final Map<TileEntity, ThermalSimulation> sims = new WeakHashMap<>();

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent e) {
        if (e.phase != TickEvent.Phase.END || e.world.isRemote) return;
        if (e.world.getTotalWorldTime() % INTERVAL != 0) return;

        for (TileEntity te : new ArrayList<>(e.world.loadedTileEntityList)) {
            if (te instanceof Case) {
                sims.computeIfAbsent(te, k -> new ThermalSimulation()).step((Case) te, INTERVAL);
            }
        }
    }
}
