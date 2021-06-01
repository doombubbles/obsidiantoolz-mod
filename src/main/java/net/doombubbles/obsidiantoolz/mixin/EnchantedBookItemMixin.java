package net.doombubbles.obsidiantoolz.mixin;

import net.doombubbles.obsidiantoolz.LoreHelper;
import net.doombubbles.obsidiantoolz.ObsidianToolzEnchantment;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnchantedBookItem.class)
public class EnchantedBookItemMixin {

    @Inject(at = @At("TAIL"), method = "addEnchantment")
    private static void addEnchantment(ItemStack stack, EnchantmentLevelEntry enchantmentInfo, CallbackInfo info) {
        if (enchantmentInfo.enchantment instanceof ObsidianToolzEnchantment) {
            LoreHelper.updateLore(stack);
        }
    }
}
