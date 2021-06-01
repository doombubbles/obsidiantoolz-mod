package net.doombubbles.obsidiantoolz;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;

import java.util.Random;

public class ObsidianToolzEnchantment extends Enchantment {

    public String getRealName(int level) {
        return "";
    }

    protected ObsidianToolzEnchantment(Rarity rarity, EnchantmentTarget type, EquipmentSlot[] slotTypes) {
        super(rarity, type, slotTypes);
    }

    public String toRoman(int num) {
        if (num < 1 || num > 10) {
            return "enchantment.level." + num;
        } else {
            String ret = "";
            if (num == 4 || num == 9) {
                ret += "I";
            }
            if (num >= 4 && num < 9) {
                ret += "V";
                num -= 5;
            }
            if (num > 0 && num < 5) {
                ret += "I";
            }
            if (num > 1 && num < 5) {
                ret += "I";
            }
            if (num > 2 && num < 5) {
                ret += "I";
            }
            if (num == 10) {
                ret += "X";
            }

            return ret;
        }
    }

    public boolean doesLevelAboveMaxPower() {
        return false;
    }

    public int getLevelBasedOnPower(int EnchantmentPower, Random random) {
        if (doesLevelAboveMaxPower()) {
            if (EnchantmentPower >= getMinPower(0)) {
                int level = getMinLevel();
                for (int i = getMinLevel(); i < getMaxLevel(); i++) {
                    boolean doIncrease = true;
                    for (int j = 0; j < i; j++) {
                        doIncrease &= random.nextBoolean();
                    }
                    if (doIncrease) level++;
                }
                return level;
            }

        } else {
            for(int currEnchLvl = getMaxLevel(); currEnchLvl >= getMinLevel(); --currEnchLvl)
            {
                if (EnchantmentPower >= getMinPower(currEnchLvl))
                {
                    return currEnchLvl;
                }
            }
        }
        return 0;
    }
}
