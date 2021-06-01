package net.doombubbles.obsidiantoolz.mixin;

import net.doombubbles.obsidiantoolz.LoreHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GrindstoneScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GrindstoneScreenHandler.class)
public class GrindstoneContainerMixin {

    @Inject(at = @At("TAIL"), method = "grind")
    private void grind(ItemStack item, int damage, int amount, CallbackInfoReturnable<ItemStack> info) {
        ItemStack realItemStack = info.getReturnValue();
        LoreHelper.updateLore(realItemStack);
    }
}
