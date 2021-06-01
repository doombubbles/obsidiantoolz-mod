package net.doombubbles.obsidiantoolz.mixin;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BucketItem.class)
public class BucketItemMixin {

    @Inject(at=@At("HEAD"), method = "getEmptiedStack", cancellable = true)
    private static void getEmptiedStack(ItemStack stack, PlayerEntity player, CallbackInfoReturnable<ItemStack> cI) {
        if (EnchantmentHelper.getLevel(Enchantments.INFINITY, stack) > 0) {
            cI.setReturnValue(stack.copy());
            cI.cancel();
        }
    }

    @Redirect(at=@At(value = "INVOKE", target = "Lnet/minecraft/item/ItemUsage;exchangeStack(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/ItemStack;)Lnet/minecraft/item/ItemStack;"), method = "use")
    private ItemStack exchangeStack(ItemStack inputStack, PlayerEntity player, ItemStack outputStack) {
        if (EnchantmentHelper.getLevel(Enchantments.INFINITY, inputStack) > 0) {
            inputStack.getOrCreateSubTag("test");
            player.playerScreenHandler.sendContentUpdates();
            inputStack.removeSubTag("test");
            return inputStack;
        }
        return ItemUsage.exchangeStack(inputStack, player, outputStack);
    }
}
