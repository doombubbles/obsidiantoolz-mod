package net.doombubbles.obsidiantoolz.mixin;

import net.doombubbles.obsidiantoolz.ObsidianToolzMod;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ArrowItem.class)
public class ArrowItemMixin {

    @Inject(at=@At("TAIL"), method = "createArrow")
    public void doSpace(World world, ItemStack stack, LivingEntity shooter, CallbackInfoReturnable<ProjectileEntity> info) {
        ProjectileEntity arrow = info.getReturnValue();
        if (EnchantmentHelper.getEquipmentLevel(ObsidianToolzMod.SPACE, shooter) > 0) {
            arrow.setNoGravity(true);
        }
        if (EnchantmentHelper.getEquipmentLevel(Enchantments.PIERCING, shooter) > 0 && arrow instanceof PersistentProjectileEntity) {
            ((PersistentProjectileEntity)arrow).setPierceLevel((byte)EnchantmentHelper.getLevel(Enchantments.PIERCING, shooter.getMainHandStack()));
        }
    }
}
