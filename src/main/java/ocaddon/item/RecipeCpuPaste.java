package ocaddon.item;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;
import ocaddon.ModItems;

/**
 * Крафт: термопаста + ЦП -> ЦП с пастой; чистая тряпка + ЦП с пастой -> ЦП без пасты (тряпка становится грязной).
 * Работает в сетке крафта 2x2 прямо в инвентаре.
 */
public class RecipeCpuPaste extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {
    private final boolean apply;

    public RecipeCpuPaste(boolean apply) { this.apply = apply; }

    private boolean isTool(ItemStack s) {
        return apply ? s.getItem() instanceof ItemThermalPaste : s.getItem() == ModItems.RAG_CLEAN;
    }

    @Override
    public boolean matches(InventoryCrafting inv, World w) {
        int count = 0;
        boolean tool = false, cpu = false;
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack s = inv.getStackInSlot(i);
            if (s.isEmpty()) continue;
            count++;
            if (isTool(s)) tool = true;
            else if (ItemThermalPaste.isCpu(s) && ItemThermalPaste.hasPaste(s) != apply) cpu = true;
            else return false;
        }
        return count == 2 && tool && cpu;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack s = inv.getStackInSlot(i);
            if (!s.isEmpty() && ItemThermalPaste.isCpu(s)) {
                ItemStack out = s.copy();
                out.setCount(1);
                if (apply) ItemThermalPaste.applyPaste(out);
                else ItemThermalPaste.removePaste(out);
                return out;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
        NonNullList<ItemStack> ret = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack s = inv.getStackInSlot(i);
            if (s.isEmpty()) continue;
            if (apply && s.getItem() instanceof ItemThermalPaste) {
                if (s.getItemDamage() + 1 <= s.getMaxDamage()) {
                    ItemStack c = s.copy();
                    c.setCount(1);
                    c.setItemDamage(c.getItemDamage() + 1);
                    ret.set(i, c);
                }
            } else if (!apply && s.getItem() == ModItems.RAG_CLEAN) {
                ret.set(i, new ItemStack(ModItems.RAG_DIRTY));
            }
        }
        return ret;
    }

    @Override public boolean canFit(int w, int h) { return w * h >= 2; }
    @Override public ItemStack getRecipeOutput() { return ItemStack.EMPTY; }
}
