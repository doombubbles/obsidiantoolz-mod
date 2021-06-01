package net.doombubbles.obsidiantoolz.mixin;

import net.doombubbles.obsidiantoolz.CustomRaidStuff;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;
import java.util.UUID;

//@Mixin(Raid.class)
public class RaidMixin {
/*
    @Shadow
    private ServerWorld world;
    @Shadow
    private int wavesSpawned;
    @Shadow
    private Set<UUID> heroesOfTheVillage;
    @Shadow
    private int preRaidTicks;
    @Shadow
    private int postRaidTicks;
    @Shadow
    public int getRaiderCount() {
        return 0;
    }
    @Shadow
    public boolean shouldSpawnMoreGroups() {
        return false;
    }

    @Inject(at = @At("RETURN"), method = "getEnchantmentChance", cancellable = true)
    public void getEnchantmentChance(CallbackInfoReturnable<Float> cI) {
        cI.setReturnValue(cI.getReturnValueF() + .05f * CustomRaidStuff.BANNERS);
    }

    @Inject(at = @At("HEAD"), method = "spawnNextWave")
    private void spawnNextWave(BlockPos pos, CallbackInfo cI) {
        CustomRaidStuff.calculateBanners(pos, world);
    }

    @Inject(at=@At(value = "INVOKE", target = "Lnet/minecraft/entity/raid/Raid;shouldSpawnMoreGroups()Z", ordinal = 0), method = "tick")
    private void tick(CallbackInfo callbackInfo) {
        if (getRaiderCount() == 0 && preRaidTicks == 0 && wavesSpawned > 0 && (postRaidTicks < 2 || shouldSpawnMoreGroups())) {
            CustomRaidStuff.roundEnded(world, heroesOfTheVillage);
        }
    }*/
}
