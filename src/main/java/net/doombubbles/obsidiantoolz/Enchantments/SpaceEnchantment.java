package net.doombubbles.obsidiantoolz.Enchantments;

import net.doombubbles.obsidiantoolz.ObsidianToolzEnchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;

public class SpaceEnchantment extends ObsidianToolzEnchantment {

    public SpaceEnchantment(Rarity rarity, EnchantmentTarget type, EquipmentSlot[] slotTypes) {
        super(rarity, type, slotTypes);
    }

    @Override
    public int getMinPower(int level) {
        return 30;
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public String getRealName(int level) {
        return "Space" + (level > 1 ? " " + toRoman(level) : "");
    }
}
