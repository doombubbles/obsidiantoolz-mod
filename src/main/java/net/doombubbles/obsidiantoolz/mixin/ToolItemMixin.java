package net.doombubbles.obsidiantoolz.mixin;

import net.doombubbles.obsidiantoolz.Items.ObsidianStuff;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ToolItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ToolItem.class)
public class ToolItemMixin {

	@Inject(at = @At("HEAD"), method = "canRepair", cancellable = true)
	private void overrideRepair(ItemStack stack, ItemStack ingredient, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
		if (ObsidianStuff.is(stack)) {
			if (ingredient.getItem() == Items.OBSIDIAN) {
				callbackInfoReturnable.setReturnValue(true);
			} else {
				callbackInfoReturnable.setReturnValue(false);
			}
			callbackInfoReturnable.cancel();
		}
	}
}
