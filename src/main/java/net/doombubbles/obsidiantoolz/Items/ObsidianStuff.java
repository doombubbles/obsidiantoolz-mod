package net.doombubbles.obsidiantoolz.Items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class ObsidianStuff {

    public static boolean is(ItemStack stack) {
        if (stack.getTag() == null || stack.getTag().get("CustomModelData") == null || stack.getTag().getInt("CustomModelData") != 1) {
            return false;
        }
        Item item = stack.getItem();
        return item == Items.DIAMOND_PICKAXE || item == Items.DIAMOND_AXE || item == Items.DIAMOND_SHOVEL
                || item == Items.DIAMOND_SWORD || item == Items.DIAMOND_HOE || item == Items.SHEARS || item == Items.SHIELD;
    }
}
