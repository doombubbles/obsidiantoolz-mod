package net.doombubbles.obsidiantoolz.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;
import java.util.stream.IntStream;

import static net.doombubbles.obsidiantoolz.DispenseTools.isFilter;

@Mixin(HopperBlockEntity.class)
public abstract class HopperBlockEntityMixin {

    @Inject(method = "insert", at = @At("HEAD"), cancellable = true)
    private static void insert(World world, BlockPos pos, BlockState state, Inventory inventory, CallbackInfoReturnable<Boolean> cir) {
        if (isFilter(inventory)) {
            cir.cancel();

            Inventory inventory2 = getOutputInventory(world, pos, state);
            if (inventory2 != null) {
                Direction direction = state.get(HopperBlock.FACING).getOpposite();
                if (!isInventoryFull(inventory2, direction)) {
                    for (int i = 0; i < inventory.size(); ++i) {
                        if (!inventory.getStack(i).isEmpty() && inventory.getStack(i).getCount() > 1) {
                            ItemStack itemStack = inventory.getStack(i).copy();
                            ItemStack itemStack2 = HopperBlockEntity.transfer(inventory, inventory2, inventory.removeStack(i, 1), direction);
                            if (itemStack2.isEmpty()) {
                                inventory2.markDirty();
                                cir.setReturnValue(true);
                                return;
                            }

                            inventory.setStack(i, itemStack);
                        }
                    }

                }
            }
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "canExtract", at = @At("RETURN"), cancellable = true)
    private static void canExtract(Inventory inv, ItemStack stack, int slot, Direction facing, CallbackInfoReturnable<Boolean> cir) {
        if (isFilter(inv) && stack.getCount() <= 1) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "getAvailableSlots", at = @At("HEAD"), cancellable = true)
    private static void getAvailableSlots(Inventory inventory, Direction side, CallbackInfoReturnable<IntStream> cir) {
        if (isFilter(inventory)) {
            cir.setReturnValue(IntStream.range(0, inventory.size()).filter(value -> inventory.getStack(value).getCount() > 1));
        }
    }

    @Inject(method = "canInsert", at = @At("RETURN"), cancellable = true)
    private static void canInsert(Inventory inventory, ItemStack stack, int slot, Direction side, CallbackInfoReturnable<Boolean> cir) {
        if (isFilter(inventory) && inventory.getStack(slot).isEmpty()) {
            cir.setReturnValue(false);
        }
    }

    @Shadow
    private static Inventory getOutputInventory(World world, BlockPos pos, BlockState state) {
        return null;
    }

    @Shadow
    private static boolean isInventoryFull(Inventory inventory, Direction direction) {
        return false;
    }


}