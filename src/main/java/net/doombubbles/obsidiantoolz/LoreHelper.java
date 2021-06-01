package net.doombubbles.obsidiantoolz;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LoreHelper {

    public static void updateLore(ItemStack itemStack) {
        NbtList lore = itemStack.getOrCreateSubTag("display").getList("Lore", 8);
        lore.removeIf(tag -> isLine(tag.asString()));
        Map<Enchantment, Integer> enchantments = EnchantmentHelper.get(itemStack);
        Set<String> loreLines = new HashSet<>();
        for (Enchantment enchantment : enchantments.keySet()) {
            if (enchantment instanceof ObsidianToolzEnchantment) {
                loreLines.add(((ObsidianToolzEnchantment) enchantment).getRealName(enchantments.get(enchantment)));
            }
        }
        for (String loreLine : loreLines) {
            lore.add(0, NbtString.of(format(loreLine)));
        }
        itemStack.getSubTag("display").put("Lore", lore);
    }



    private static boolean isLine(String text) {
        String importantPart = unFormat(text);
        for (ObsidianToolzEnchantment enchantment : ObsidianToolzMod.ENCHANTMENTS) {
            for (int i = 1; i <= 10; i++) {
                if (enchantment.getRealName(i).equals(importantPart)) return true;
            }
        }
        return false;
    }

    private static String format(String text) {
        return "{\"text\":\"" + text + "\",\"color\":\"gray\",\"italic\":false}";
    }

    private static String unFormat(String text) {
        if (!text.contains("{\"text\":\"") || !text.contains("\",\"color\":\"gray\",\"italic\":false}")) {
            return null;
        }
        return text.replace("{\"text\":\"", "").replace("\",\"color\":\"gray\",\"italic\":false}", "");
    }

}
