package ocaddon.driver;

import li.cil.oc.api.driver.DriverItem;
import li.cil.oc.api.driver.item.Slot;
import li.cil.oc.api.network.EnvironmentHost;
import li.cil.oc.api.network.ManagedEnvironment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import ocaddon.item.ItemCooler;

public class DriverCooler implements DriverItem {
    @Override public boolean worksWith(ItemStack s) { return s.getItem() instanceof ItemCooler; }

    @Override public ManagedEnvironment createEnvironment(ItemStack s, EnvironmentHost host) {
        return new CoolerEnvironment((ItemCooler) s.getItem());
    }

    @Override public String slot(ItemStack s) { return Slot.Card; }

    @Override public int tier(ItemStack s) { return ((ItemCooler) s.getItem()).tier - 1; }

    @Override public NBTTagCompound dataTag(ItemStack s) {
        if (!s.hasTagCompound()) s.setTagCompound(new NBTTagCompound());
        NBTTagCompound t = s.getTagCompound();
        if (!t.hasKey("oc:data")) t.setTag("oc:data", new NBTTagCompound());
        return t.getCompoundTag("oc:data");
    }
}
