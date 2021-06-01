package net.doombubbles.obsidiantoolz.mixin;

import net.doombubbles.obsidiantoolz.Items.ObsidianStuff;
import net.minecraft.block.BlockState;
import net.minecraft.block.MagmaBlock;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MagmaBlock.class)
public class MagmaBlockMixin {

    @Inject(method = "onSteppedOn", at=@At("HEAD"), cancellable = true)
    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity, CallbackInfo ci) {
        for (ItemStack itemStack : entity.getItemsHand()) {
            if (ObsidianStuff.is(itemStack) && itemStack.getItem() == Items.SHIELD) {
                ci.cancel();
                return;
            }
        }
    }
}
