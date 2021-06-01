package net.doombubbles.obsidiantoolz.Enchantments;

import net.doombubbles.obsidiantoolz.ObsidianToolzEnchantment;
import net.doombubbles.obsidiantoolz.ObsidianToolzMod;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PickaxeItem;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class InflationEnchantment extends ObsidianToolzEnchantment {

    public InflationEnchantment(Rarity rarity, EnchantmentTarget type, EquipmentSlot[] slotTypes) {
        super(rarity, type, slotTypes);
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public boolean isTreasure() {
        return true;
    }

    @Override
    protected boolean canAccept(Enchantment other) {
        return super.canAccept(other) && other != Enchantments.FORTUNE && other != Enchantments.SILK_TOUCH;
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return super.isAcceptableItem(stack) && stack.getItem() instanceof PickaxeItem;
    }

    @Override
    public String getRealName(int level) {
        return "Inflation " + toRoman(level);
    }

    public static void doInflation(BlockState state, LootContext.Builder lootBuilder, CallbackInfoReturnable<List<ItemStack>> ci) {
        ItemStack tool = lootBuilder.get(LootContextParameters.TOOL);
        int inflationRate = EnchantmentHelper.getLevel(ObsidianToolzMod.INFLATION,tool);
        if (inflationRate > 0) {
            List<ItemStack> originalStacks = ci.getReturnValue();
            List<ItemStack> newStacks = new ArrayList<>();
            int init = 1;

            int i = new Random().nextInt((inflationRate * 3) + 2) - 1;
            if (i < 0) {
                i = 0;
            }

            int newAfter = init * (i + 1);

            for (int j = 0; j < newAfter; j++) {
                for (ItemStack originalStack : originalStacks) {
                    if (originalStack.getItem() == Items.EMERALD) {
                        newStacks.add(originalStack.copy());
                    }
                }
            }
            ci.setReturnValue(newStacks);
        }
    }
}
