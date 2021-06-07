package net.doombubbles.obsidiantoolz.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/*
@Mixin(PersistentProjectileEntity.class)
public class PersistentProjectileEntityMixin {

    @Inject(method = "onEntityHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;ceil(D)I"))
    private void method(EntityHitResult entityHitResult, CallbackInfo ci) {
        ProjectileEntity projectileEntity = (ProjectileEntity)(Object) this;
        Entity entity = entityHitResult.getEntity();
        if (entity.isOnFire() && entity.getType() != EntityType.ENDERMAN && projectileEntity.getScoreboardTags().contains("flame2")) {

        }
    }
}*/
