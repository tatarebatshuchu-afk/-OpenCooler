package ocaddon.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import ocaddon.OCAddon;

public class ItemCooler extends Item {
    public final int tier, airflow;

    public ItemCooler(int tier, int airflow) {
        this.tier = tier;
        this.airflow = airflow;
        setRegistryName(OCAddon.MODID, "cooler_tier" + tier);
        setTranslationKey(OCAddon.MODID + ".cooler_tier" + tier);
        setCreativeTab(ocaddon.ModTab.TAB);
        setMaxStackSize(16);
    }

    @Override
    public String getItemStackDisplayName(ItemStack s) {
        return ocaddon.ModTab.tr(getTranslationKey(s) + ".name", "Кулер (Тир " + tier + ")");
    }
}
