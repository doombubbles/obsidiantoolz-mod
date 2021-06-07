package net.doombubbles.obsidiantoolz.mixin;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ScreenHandler.class)
public class ScreenHandlerMixin {

    @Inject(method = "calculateComparatorOutput(Lnet/minecraft/inventory/Inventory;)I", at = @At("RETURN"), cancellable = true)
    private static void calculateComparatorOutput(Inventory inventory, CallbackInfoReturnable<Integer> cir) {
        if (inventory != null) {
            int i = 0;
            float f = 0.0F;

            for(int j = 0; j < inventory.size(); ++j) {
                ItemStack itemStack = inventory.getStack(j);
                if (!itemStack.isEmpty()) {
                    if (itemStack.isDamaged()) {
                        f += 1;
                        f -= (float) itemStack.getDamage() / (float)itemStack.getMaxDamage();
                    } else {
                        f += (float)itemStack.getCount() / (float)Math.min(inventory.getMaxCountPerStack(), itemStack.getMaxCount());
                    }
                    ++i;
                }
            }


            f /= (float)inventory.size();
            int value = MathHelper.floor(f * 14.0F) + (i > 0 ? 1 : 0);

            if (value < cir.getReturnValueI()) {
                cir.setReturnValue(value);
            }
        }
    }

}