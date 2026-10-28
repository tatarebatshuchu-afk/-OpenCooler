package ocaddon;

import net.minecraft.item.Item;
import ocaddon.item.*;

public class ModItems {
    public static final ItemCooler COOLER1 = new ItemCooler(1, 10);
    public static final ItemCooler COOLER2 = new ItemCooler(2, 25);
    public static final ItemCooler COOLER3 = new ItemCooler(3, 50);
    public static final ItemThermalPaste PASTE = new ItemThermalPaste();
    public static final ItemRag RAG_CLEAN = new ItemRag("rag_clean", false);
    public static final ItemRag RAG_DIRTY = new ItemRag("rag_dirty", true);

    public static final Item[] ALL = { COOLER1, COOLER2, COOLER3, PASTE, RAG_CLEAN, RAG_DIRTY };
}
