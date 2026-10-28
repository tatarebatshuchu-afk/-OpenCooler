package ocaddon;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class ModTab {
    public static final CreativeTabs TAB = new CreativeTabs("opencooler") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(ModItems.COOLER1);
        }
    };

    /** Перевод с запасным названием, если lang-файл не подхватился. */
    @SuppressWarnings("deprecation")
    public static String tr(String key, String fallback) {
        if (net.minecraft.util.text.translation.I18n.canTranslate(key))
            return net.minecraft.util.text.translation.I18n.translateToLocal(key);
        return fallback;
    }
}
