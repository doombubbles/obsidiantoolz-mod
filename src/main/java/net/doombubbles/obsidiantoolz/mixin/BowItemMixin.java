package net.doombubbles.obsidiantoolz.mixin;

import net.doombubbles.obsidiantoolz.ObsidianToolzMod;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(BowItem.class)
public class BowItemMixin {

    @Shadow
    private int getMaxUseTime(ItemStack stack) {
        return 0;
    }

    @Inject(at=@At("HEAD"), method = "onStoppedUsing")
    public void infinityTwoElectricBoogaloo(ItemStack stack, World world, LivingEntity user, int remainingUseTicks, CallbackInfo cI) {
        if (user instanceof PlayerEntity) {
            PlayerEntity playerEntity = (PlayerEntity)user;
            ItemStack itemStack = playerEntity.getArrowType(stack);
            int level = EnchantmentHelper.getLevel(Enchantments.INFINITY, stack);

            int i = this.getMaxUseTime(stack) - remainingUseTicks;
            float f = BowItem.getPullProgress(i);
            if ((double)f >= 0.1D && (itemStack.getItem() instanceof SpectralArrowItem || itemStack.getItem() instanceof TippedArrowItem)
                    && level > 1 && !playerEntity.isCreative()) {
                itemStack.increment(1);
                ((ServerPlayerEntity) user).playerScreenHandler.sendContentUpdates();
            }
        }
    }

    @Inject(at=@At(value = "INVOKE", target = "Lnet/minecraft/world/World;spawnEntity(Lnet/minecraft/entity/Entity;)Z"), method = "onStoppedUsing", locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    public void infinityTwoElectricBoogalooTwo(ItemStack stack, World world, LivingEntity user, int remainingUseTicks, CallbackInfo ci, PlayerEntity playerEntity, boolean bl,
                                               ItemStack itemStack, int i, float f, boolean bl2, ArrowItem arrowItem, PersistentProjectileEntity persistentProjectileEntity) {
        if (EnchantmentHelper.getLevel(Enchantments.INFINITY, stack) > 1) {
            persistentProjectileEntity.pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY;
        }
        int space = EnchantmentHelper.getLevel(ObsidianToolzMod.SPACE, stack);
        if (space > 1) {
            persistentProjectileEntity.setProperties(playerEntity, playerEntity.getPitch(), playerEntity.getYaw(), 0.0F, f * 3.0F * space, 1.0F);
        }
    }




}
