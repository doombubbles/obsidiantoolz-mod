package net.doombubbles.obsidiantoolz.mixin;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MilkBucketItem;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MilkBucketItem.class)
public class MilkBucketItemMixin {

    @Inject(at=@At("HEAD"), method = "finishUsing")
    public void finishUsing(ItemStack stack, World world, LivingEntity user, CallbackInfoReturnable<ItemStack> cI) {
        if (EnchantmentHelper.getLevel(Enchantments.INFINITY, stack) > 0 && !((PlayerEntity)user).getAbilities().creativeMode) {
            stack.increment(1);
            ((ServerPlayerEntity) user).playerScreenHandler.sendContentUpdates();
        }
    }

    @Inject(at=@At("TAIL"), method = "finishUsing", cancellable = true)
    public void afterFinishUsing(ItemStack stack, World world, LivingEntity user, CallbackInfoReturnable<ItemStack> cI) {
        if (EnchantmentHelper.getLevel(Enchantments.INFINITY, stack) > 0 && !((PlayerEntity)user).getAbilities().creativeMode) {
            cI.setReturnValue(stack.copy());
            cI.cancel();
            ((ServerPlayerEntity) user).playerScreenHandler.sendContentUpdates();
        }
    }
}
