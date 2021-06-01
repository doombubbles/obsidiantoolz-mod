package net.doombubbles.obsidiantoolz.mixin;

import com.google.common.collect.Lists;
import net.doombubbles.obsidiantoolz.Items.ObsidianStuff;
import net.doombubbles.obsidiantoolz.LoreHelper;
import net.doombubbles.obsidiantoolz.ObsidianToolzEnchantment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperMixin {

    @Inject(at = @At("TAIL"), method = "set")
    private static void set(Map<Enchantment, Integer> enchantments, ItemStack stack, CallbackInfo info) {
        boolean dewIt = false;
        for (Enchantment enchantment : enchantments.keySet()) {
            if (enchantment instanceof ObsidianToolzEnchantment) {
                dewIt = true;
            }
        }
        if (dewIt) {
            LoreHelper.updateLore(stack);
        }
    }

    @Inject(method = "Lnet/minecraft/enchantment/EnchantmentHelper;generateEnchantments(Ljava/util/Random;Lnet/minecraft/item/ItemStack;IZ)Ljava/util/List;", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE_ASSIGN", ordinal = 0, target = "Lnet/minecraft/item/Item;getEnchantability()I"))
    private static void enchantability(Random random, ItemStack stack, int level, boolean hasTreasure, CallbackInfoReturnable<List<EnchantmentLevelEntry>> cir, List<EnchantmentLevelEntry> list, Item item, int i) {
        if (ObsidianStuff.is(stack)) {
            i = 5;
        }
    }


    /**
     * @author doombubbles
     * @reason the default code for this methods is real weird
     */
    @Overwrite
    public static List<EnchantmentLevelEntry> getPossibleEntries(int EnchantmentPower, ItemStack targetItemStack, boolean isTreasureGeneration)
    {
        List<EnchantmentLevelEntry> validEnchants = Lists.newArrayList();
        Item targetItem = targetItemStack.getItem();
        boolean isBook = targetItemStack.getItem() == Items.BOOK;
        Iterator<Enchantment> RegisteredEnchantments = Registry.ENCHANTMENT.iterator();
        while(RegisteredEnchantments.hasNext())
        {
            Enchantment currEnchantment = RegisteredEnchantments.next();
            if(!isBook)
            {
                if(currEnchantment instanceof ObsidianToolzEnchantment && !currEnchantment.isAcceptableItem(targetItemStack))
                {
                    continue;
                } else if(!currEnchantment.type.isAcceptableItem(targetItem))
                {
                    continue;
                }
            }
            if(currEnchantment.isTreasure() && !isTreasureGeneration)
            {
                continue;
            }
            if (!currEnchantment.isAvailableForRandomSelection()) {
                continue;
            }

            if (currEnchantment instanceof ObsidianToolzEnchantment) {
                ObsidianToolzEnchantment ote = (ObsidianToolzEnchantment) currEnchantment;
                int level = ote.getLevelBasedOnPower(EnchantmentPower, new Random());
                if (level > 0) {
                    validEnchants.add(new EnchantmentLevelEntry(currEnchantment, level));
                }
            } else {
                for(int currEnchLvl = currEnchantment.getMaxLevel(); currEnchLvl >= currEnchantment.getMinLevel(); --currEnchLvl)
                {
                    if (EnchantmentPower >= currEnchantment.getMinPower(currEnchLvl))
                    {
                        validEnchants.add(new EnchantmentLevelEntry(currEnchantment, currEnchLvl));
                        break;
                    }
                }
            }


        }
        return validEnchants;
    }

}
