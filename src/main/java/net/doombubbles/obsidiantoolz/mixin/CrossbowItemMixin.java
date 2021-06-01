package net.doombubbles.obsidiantoolz.mixin;

import net.doombubbles.obsidiantoolz.ObsidianToolzMod;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Random;

@Mixin(CrossbowItem.class)
public class CrossbowItemMixin {

    @Inject(at = @At("HEAD"), method = "loadProjectile", cancellable = true)
    private static void infinity(LivingEntity shooter, ItemStack crossbow, ItemStack projectile, boolean simulated, boolean creative, CallbackInfoReturnable<Boolean> cI) {
        int level = EnchantmentHelper.getLevel(Enchantments.INFINITY, crossbow);
        if (!projectile.isEmpty() && level > 0 && shooter instanceof ServerPlayerEntity && !((ServerPlayerEntity) shooter).isCreative()) {
            if (!(projectile.getItem() instanceof TippedArrowItem || projectile.getItem() instanceof SpectralArrowItem) || level > 1) {
                projectile.increment(1);
                ((ServerPlayerEntity) shooter).playerScreenHandler.sendContentUpdates();
            }
        }
    }
    /*
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getArrowType(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/item/ItemStack;"), method = "Lnet/minecraft/item/CrossbowItem;loadProjectiles(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;)Z", locals = LocalCapture.CAPTURE_FAILHARD)
    private static void multishots(LivingEntity shooter, ItemStack crossbow, CallbackInfoReturnable<Boolean> cI, int j, boolean bl) {
        System.out.println("j before: " + j);
        j = 1 + 2 * EnchantmentHelper.getLevel(Enchantments.MULTISHOT, crossbow);
        System.out.println("j after: " + j);
    }
    */

    @Inject(at=@At("HEAD"), method = "loadProjectiles", cancellable = true)
    private static void loadProjectiles(LivingEntity shooter, ItemStack crossbow, CallbackInfoReturnable<Boolean> cI) {
        int mLvl = EnchantmentHelper.getLevel(Enchantments.MULTISHOT, crossbow);
        if (mLvl > 1) {
            cI.cancel();
            int j = 1 + mLvl * 2;
            boolean bl = shooter instanceof PlayerEntity && ((PlayerEntity)shooter).getAbilities().creativeMode;
            ItemStack itemStack = shooter.getArrowType(crossbow);
            ItemStack itemStack2 = itemStack.copy();

            for(int k = 0; k < j; ++k) {
                if (k > 0) {
                    itemStack = itemStack2.copy();
                }

                if (itemStack.isEmpty() && bl) {
                    itemStack = new ItemStack(Items.ARROW);
                    itemStack2 = itemStack.copy();
                }

                if (!loadProjectile(shooter, crossbow, itemStack, k > 0, bl)) {
                    cI.setReturnValue(false);
                }
            }
            cI.setReturnValue(true);
        }
    }

    @Shadow
    private static boolean loadProjectile(LivingEntity shooter, ItemStack crossbow, ItemStack itemStack, boolean b, boolean bl) {
        return false;
    }

    @Inject(at = @At("RETURN"), method = "createArrow")
    private static void createArrow(World world, LivingEntity entity, ItemStack crossbow, ItemStack arrow, CallbackInfoReturnable<PersistentProjectileEntity> cI) {
        PersistentProjectileEntity returnValue = cI.getReturnValue();
        if (EnchantmentHelper.getLevel(Enchantments.INFINITY, crossbow) > 0) {
            returnValue.pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY;
        }
        if (EnchantmentHelper.getLevel(ObsidianToolzMod.SPACE, crossbow) > 0) {
            returnValue.setNoGravity(true);
        }
        if (EnchantmentHelper.getLevel(Enchantments.POWER, crossbow) > 0) {
            double dmg = returnValue.getDamage();
            returnValue.setDamage(dmg + .5D + .25D * EnchantmentHelper.getLevel(Enchantments.POWER, crossbow));
        }
    }

    @Inject(at = @At("HEAD"), method = "shootAll")
    private static void shootAll(World world, LivingEntity entity, Hand hand, ItemStack stack, float speed, float divergence, CallbackInfo cI) {
        List<ItemStack> list = getProjectiles(stack);
        if (list.size() > 3) {
            float[] fs = getSoundPitches(entity.getRandom());
            int multishotLvl = EnchantmentHelper.getLevel(Enchantments.MULTISHOT, stack);
            float d = 10.0F / multishotLvl;
            for (int i = 3; i < list.size(); i++) {
                ItemStack itemStack = list.get(i);
                boolean bl = entity instanceof PlayerEntity && ((PlayerEntity)entity).getAbilities().creativeMode;
                if (!itemStack.isEmpty()) {
                    int j = i - 2;
                    if (j % 2 == 0) {
                        j *= -1;
                        j++;
                    }
                    shoot(world, entity, hand, stack, itemStack, fs[i % 3], bl, speed, divergence, j * d);
                }
            }
        }
    }

    @Shadow
    private static void shoot(World world, LivingEntity shooter, Hand hand, ItemStack crossbow, ItemStack projectile, float soundPitch, boolean creative, float speed, float divergence, float simulated) {
    }

    @ModifyArg(method = "shootAll", at=@At(value = "INVOKE", target = "Lnet/minecraft/item/CrossbowItem;shoot(Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/util/Hand;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;FZFFF)V"), index = 7)
    private static float adjustSpeed(World world, LivingEntity shooter, Hand hand, ItemStack crossbow, ItemStack projectile, float soundPitch, boolean creative, float speed, float divergence, float simulated) {
        int spaceLevel = EnchantmentHelper.getLevel(ObsidianToolzMod.SPACE, crossbow);
        if (spaceLevel > 1) {
            return speed * spaceLevel;
        }
        return speed;
    }

    @Shadow
    private static float[] getSoundPitches(Random random) {
        return new float[0];
    }

    @Shadow
    private static List<ItemStack> getProjectiles(ItemStack stack) {
        return null;
    }
}
