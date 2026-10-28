package ocaddon.item;

import li.cil.oc.api.Driver;
import li.cil.oc.api.driver.DriverItem;
import li.cil.oc.api.driver.item.Slot;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import ocaddon.OCAddon;

public class ItemThermalPaste extends Item {
    public static final String SUFFIX = " (has ThermalPaste)";

    public ItemThermalPaste() {
        setRegistryName(OCAddon.MODID, "thermal_paste");
        setTranslationKey(OCAddon.MODID + ".thermal_paste");
        setCreativeTab(ocaddon.ModTab.TAB);
        setMaxStackSize(1);
        setMaxDamage(9); // 10 использований (последнее ломает тюбик)
    }

    public static boolean isCpu(ItemStack s) {
        if (s.isEmpty()) return false;
        DriverItem d = Driver.driverFor(s);
        if (d != null && Slot.CPU.equals(d.slot(s))) return true;
        li.cil.oc.api.detail.ItemInfo info = li.cil.oc.api.Items.get(s);
        if (info != null && info.name() != null) {
            String n = info.name().toLowerCase();
            return n.startsWith("cpu") || n.startsWith("apu");
        }
        return false;
    }

    public static boolean hasPaste(ItemStack s) {
        return s.hasTagCompound() && s.getTagCompound().getBoolean("hasPaste");
    }

    public static void applyPaste(ItemStack cpu) {
        NBTTagCompound tag = cpu.hasTagCompound() ? cpu.getTagCompound() : new NBTTagCompound();
        tag.setBoolean("hasPaste", true);
        cpu.setTagCompound(tag);
        cpu.setStackDisplayName(cpu.getDisplayName() + SUFFIX);
    }

    public static void removePaste(ItemStack cpu) {
        NBTTagCompound tag = cpu.getTagCompound();
        if (tag == null) return;
        tag.removeTag("hasPaste");
        if (tag.hasKey("display")) {
            tag.getCompoundTag("display").removeTag("Name");
            if (tag.getCompoundTag("display").isEmpty()) tag.removeTag("display");
        }
        if (tag.isEmpty()) cpu.setTagCompound(null);
    }

    /** Паста в одной руке, ЦП в другой. */
    @Override
    public ActionResult<ItemStack> onItemRightClick(World w, EntityPlayer p, EnumHand hand) {
        ItemStack self = p.getHeldItem(hand);
        ItemStack cpu = p.getHeldItem(hand == EnumHand.MAIN_HAND ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
        if (isCpu(cpu) && !hasPaste(cpu)) {
            if (!w.isRemote) {
                applyPaste(cpu);
                self.damageItem(1, p);
            }
            return new ActionResult<>(EnumActionResult.SUCCESS, self);
        }
        return new ActionResult<>(EnumActionResult.PASS, self);
    }

    @Override
    public String getItemStackDisplayName(ItemStack s) {
        return ocaddon.ModTab.tr(getTranslationKey(s) + ".name", "Тюбик термопасты");
    }
}
