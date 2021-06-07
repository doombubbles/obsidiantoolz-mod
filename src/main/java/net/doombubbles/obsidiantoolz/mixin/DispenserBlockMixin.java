package net.doombubbles.obsidiantoolz.mixin;

import net.doombubbles.obsidiantoolz.DispenseTools;
import net.doombubbles.obsidiantoolz.Items.BugNet;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.item.ExperienceBottleItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MiningToolItem;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(DispenserBlock.class)
public class DispenserBlockMixin {

    @Inject(method = "getBehaviorForItem", at = @At("HEAD"), cancellable = true)
    private void getBehaviorForItem(ItemStack stack, CallbackInfoReturnable<DispenserBehavior> cI) {
        if (BugNet.is(stack)) {
            cI.setReturnValue(BugNet.dispensorBehavior());
        }

        if (stack.getItem() instanceof MiningToolItem) {
            cI.setReturnValue(DispenseTools.dispenseToolBehavior());
        }

        if (stack.getItem() instanceof ExperienceBottleItem) {
            cI.setReturnValue(DispenseTools.dispenseMendBehavior());
        }
    }
}
