package net.doombubbles.obsidiantoolz.mixin;

import net.doombubbles.obsidiantoolz.Items.NetheriteAnvil;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AnvilBlock.class)
public class AnvilBlockMixin extends Block {
    public AnvilBlockMixin(Settings settings) {
        super(settings);
    }

    /*
    private static final BooleanProperty NETHERITE = BooleanProperty.of("netherite");



    @Inject(at = @At("TAIL"), method = "<init>()V")
    private void constructor(CallbackInfo callbackInfo) {
        this.setDefaultState(getStateManager().getDefaultState().with(AnvilBlock.FACING, Direction.NORTH).with(NETHERITE, false));
    }

    @Inject(at = @At("TAIL"), method = "appendProperties")
    private void appendProperties(StateManager.Builder<Block, BlockState> builder, CallbackInfo callbackInfo) {
        builder.add(NETHERITE);
    }
     */

    @Inject(at = @At("RETURN"), method = "getPlacementState", cancellable = true)
    public void getPlacementState(ItemPlacementContext ctx, CallbackInfoReturnable<BlockState> callbackInfoReturnable) {
        Direction direction = ctx.getPlayerFacing().rotateYClockwise();
        ItemStack stack = ctx.getStack();
        if (NetheriteAnvil.itemIs(stack)) {
            if (direction == Direction.NORTH) {
                direction = Direction.SOUTH;
            } else if (direction == Direction.WEST) {
                direction = Direction.EAST;
            }
        } else {
            if (direction == Direction.SOUTH) {
                direction = Direction.NORTH;
            } else if (direction == Direction.EAST) {
                direction = Direction.WEST;
            }
        }
        callbackInfoReturnable.setReturnValue(this.getDefaultState().with(AnvilBlock.FACING, direction));
        callbackInfoReturnable.cancel();
    }



}
