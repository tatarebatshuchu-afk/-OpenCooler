package ocaddon.item;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import ocaddon.ModItems;
import ocaddon.OCAddon;

public class ItemRag extends Item {
    private final boolean dirty;

    public ItemRag(String name, boolean dirty) {
        this.dirty = dirty;
        setRegistryName(OCAddon.MODID, name);
        setTranslationKey(OCAddon.MODID + "." + name);
        setCreativeTab(ocaddon.ModTab.TAB);
        setMaxStackSize(1);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World w, EntityPlayer p, EnumHand hand) {
        ItemStack self = p.getHeldItem(hand);

        if (dirty) {
            RayTraceResult r = rayTrace(w, p, true);
            if (r != null && r.typeOfHit == RayTraceResult.Type.BLOCK
                    && w.getBlockState(r.getBlockPos()).getMaterial() == Material.WATER) {
                return new ActionResult<>(EnumActionResult.SUCCESS, new ItemStack(ModItems.RAG_CLEAN));
            }
        } else {
            ItemStack cpu = p.getHeldItem(hand == EnumHand.MAIN_HAND ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
            if (ItemThermalPaste.isCpu(cpu) && ItemThermalPaste.hasPaste(cpu)) {
                if (!w.isRemote) ItemThermalPaste.removePaste(cpu);
                return new ActionResult<>(EnumActionResult.SUCCESS, new ItemStack(ModItems.RAG_DIRTY));
            }
        }
        return new ActionResult<>(EnumActionResult.PASS, self);
    }

    /** Грязная тряпка, брошенная в воду, тоже отмывается. */
    @Override
    public boolean onEntityItemUpdate(net.minecraft.entity.item.EntityItem e) {
        if (dirty && !e.world.isRemote && e.isInWater()) {
            e.setItem(new ItemStack(ModItems.RAG_CLEAN, e.getItem().getCount()));
        }
        return false;
    }

    @Override
    public String getItemStackDisplayName(ItemStack s) {
        return ocaddon.ModTab.tr(getTranslationKey(s) + ".name", dirty ? "Грязная тряпка" : "Чистая тряпка");
    }
}
