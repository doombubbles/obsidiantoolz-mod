package net.doombubbles.obsidiantoolz.mixin;

import net.doombubbles.obsidiantoolz.Items.BugNet;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DispenserBlock.class)
public class DispenserBlockMixin {

    @Inject(method = "getBehaviorForItem", at=@At("HEAD"), cancellable = true)
    private void getBehaviorForItem(ItemStack stack, CallbackInfoReturnable<DispenserBehavior> cI) {
        if (BugNet.is(stack)) {
            cI.setReturnValue(BugNet.dispensorBehavior());
            cI.cancel();
        }
    }
}
