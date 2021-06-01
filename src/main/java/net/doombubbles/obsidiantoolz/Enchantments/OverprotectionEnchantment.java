package net.doombubbles.obsidiantoolz.Enchantments;

import com.google.common.collect.ImmutableMultimap;
import net.doombubbles.obsidiantoolz.ObsidianToolzEnchantment;
import net.doombubbles.obsidiantoolz.ObsidianToolzMod;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;

import java.util.UUID;

public class OverprotectionEnchantment extends ObsidianToolzEnchantment {
    public OverprotectionEnchantment(Rarity rarity, EnchantmentTarget type, EquipmentSlot[] slotTypes) {
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
        return "Overprotection " + toRoman(level);
    }

    @Override
    public boolean doesLevelAboveMaxPower() {
        return true;
    }

    public static int doOverprotection(LivingEntity livingEntity, int lastAmount) {
        int overprotection = 0;
        for (ItemStack armorItem : livingEntity.getArmorItems()) {
            overprotection += EnchantmentHelper.getLevel(ObsidianToolzMod.OVERPROTECTION, armorItem);
        }
        int armorAlready = livingEntity.getArmor() - lastAmount;
        int amountToGive = armorAlready + overprotection <= 20 ? overprotection : Math.min(overprotection, 20 - armorAlready);
        if (amountToGive != lastAmount) {
            UUID uuid = UUID.fromString("14bb3bd8-676a-4772-940e-18b94266c93f");
            EntityAttributeModifier modifier = new EntityAttributeModifier(uuid, "Overprotection factor", amountToGive, EntityAttributeModifier.Operation.ADDITION);
            ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();

            builder.put(EntityAttributes.GENERIC_ARMOR, modifier);

            livingEntity.getAttributes().addTemporaryModifiers(builder.build());
        }

        return amountToGive;
    }

}
