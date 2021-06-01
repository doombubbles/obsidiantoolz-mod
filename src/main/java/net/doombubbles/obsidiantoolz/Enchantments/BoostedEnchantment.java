package net.doombubbles.obsidiantoolz.Enchantments;

import net.doombubbles.obsidiantoolz.Items.RocketBoots;
import net.doombubbles.obsidiantoolz.ObsidianToolzEnchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;

public class BoostedEnchantment extends ObsidianToolzEnchantment {

    public BoostedEnchantment(Rarity rarity, EnchantmentTarget type, EquipmentSlot[] slotTypes) {
        super(rarity, type, slotTypes);
    }

    @Override
    public int getMinPower(int level) {
        return 30;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public String getRealName(int level) {
        return "Boosted " + toRoman(level);
    }

    @Override
    public boolean doesLevelAboveMaxPower() {
        return true;
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return stack.getItem() instanceof ElytraItem || RocketBoots.is(stack);
    }
}
