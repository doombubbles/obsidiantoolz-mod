package net.doombubbles.obsidiantoolz.mixin;

import net.doombubbles.obsidiantoolz.CustomRaidStuff;
import net.minecraft.entity.mob.PillagerEntity;
import net.minecraft.entity.raid.RaiderEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PillagerEntity.class)
public class PillagerEntityMixin {

    @Inject(at = @At("TAIL"), method = "addBonusForWave")
    public void addBonusForWave(int wave, boolean unused, CallbackInfo cI) {
        //CustomRaidStuff.pillagerBonus((RaiderEntity)(Object)this);
    }
}
