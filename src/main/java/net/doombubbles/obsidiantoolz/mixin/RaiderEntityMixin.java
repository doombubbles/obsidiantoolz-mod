package net.doombubbles.obsidiantoolz.mixin;

import net.doombubbles.obsidiantoolz.CustomRaidStuff;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RaiderEntity.class)
public class RaiderEntityMixin {
    @Inject(at = @At("TAIL"), method = "initialize")
    public void addBonusForWave(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, EntityData entityData, NbtCompound entityNbt, CallbackInfoReturnable<EntityData> cir) {
        if (spawnReason == SpawnReason.EVENT) {
            CustomRaidStuff.bonus((RaiderEntity)(Object)this);
        }
    }
}
