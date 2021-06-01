package net.doombubbles.obsidiantoolz.Enchantments;

import net.doombubbles.obsidiantoolz.ObsidianToolzEnchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;

public class SoulboundEnchantment extends ObsidianToolzEnchantment {

    public SoulboundEnchantment(Rarity rarity, EnchantmentTarget type, EquipmentSlot[] slotTypes) {
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
        return "Soulbound " + toRoman(level);
    }

    @Override
    public boolean doesLevelAboveMaxPower() {
        return true;
    }
}
