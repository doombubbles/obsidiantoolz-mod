package net.doombubbles.obsidiantoolz.mixin;

import net.minecraft.block.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractRedstoneGateBlock.class)
public class AbstractRedstoneGateBlockMixin {

    @Inject(at = @At("HEAD"), method = "getInputLevel", cancellable = true)
    private void getInputLevel(WorldView worldView, BlockPos pos, Direction dir, CallbackInfoReturnable<Integer> cI) {
        BlockState blockState = worldView.getBlockState(pos);
        Block block = blockState.getBlock();
        if (block == Blocks.REDSTONE_TORCH || block == Blocks.REDSTONE_WALL_TORCH) {
            cI.setReturnValue(blockState.get(RedstoneTorchBlock.LIT) ? 15 : 0);
        }
    }

    @Shadow
    private boolean isValidInput(BlockState blockState) {
        return false;
    }
}
