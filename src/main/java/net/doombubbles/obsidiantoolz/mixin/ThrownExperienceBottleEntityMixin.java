package net.doombubbles.obsidiantoolz.mixin;

import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.projectile.thrown.ExperienceBottleEntity;
import net.minecraft.util.hit.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ExperienceBottleEntity.class)
public class ThrownExperienceBottleEntityMixin {

    @Inject(method = "onCollision", at = @At("HEAD"))
    protected void onCollision(HitResult hitResult, CallbackInfo callbackInfo) {
        ExperienceBottleEntity thi = (ExperienceBottleEntity)(Object)this;

        if (!thi.world.isClient) {
            int i = 5 + thi.world.random.nextInt(10) + thi.world.random.nextInt(10);

            while(i > 0) {
                int j = ExperienceOrbEntity.roundToOrbSize(i);
                i -= j;
                thi.world.spawnEntity(new ExperienceOrbEntity(thi.world, thi.getX(), thi.getY(), thi.getZ(), j));
            }
        }
    }
}
