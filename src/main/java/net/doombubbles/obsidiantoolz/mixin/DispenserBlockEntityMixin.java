package net.doombubbles.obsidiantoolz.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.ShearsItem;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(DispenserBlockEntity.class)
public class DispenserBlockEntityMixin extends LootableContainerBlockEntity {

    @Shadow
    private DefaultedList<ItemStack> inventory;

    @Shadow
    @Final
    private static Random RANDOM;

    protected DispenserBlockEntityMixin(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @Inject(method = "chooseNonEmptySlot", at = @At("HEAD"), cancellable = true)
    private void method(CallbackInfoReturnable<Integer> cir) {
        this.checkLootInteraction(null);
        int i = -1;
        int j = 1;

        for (int k = 0; k < inventory.size(); ++k) {
            if (!inventory.get(k).isEmpty() && RANDOM.nextInt(j) == 0 && (inventory.get(k).getItem() instanceof MiningToolItem) ) {
                i = k;
                j++;
            }
        }

        if (i != -1) {
            cir.setReturnValue(i);
        }
    }

    @Shadow
    protected DefaultedList<ItemStack> getInvStackList() {
        return null;
    }

    @Shadow
    protected void setInvStackList(DefaultedList<ItemStack> list) {

    }

    @Shadow
    protected Text getContainerName() {
        return null;
    }

    @Shadow
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return null;
    }

    @Shadow
    public int size() {
        return 0;
    }
}