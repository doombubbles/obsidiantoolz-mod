package net.doombubbles.obsidiantoolz.mixin;

import net.doombubbles.obsidiantoolz.ObsidianToolzMod;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreeperEntity.class)
public class CreeperEntityMixin {

    @Inject(at = @At("HEAD"), method = "dropEquipment")
    protected void doSpecialDrops(DamageSource source, int lootingMultiplier, boolean allowDrops, CallbackInfo info) {
        LivingEntity thisEntity = (LivingEntity) (Object) this;
        if (((CreeperEntity) (Object) this).shouldRenderOverlay() && allowDrops && !source.isProjectile() && !source.isMagic() && !source.isFire() && source.getAttacker() instanceof PlayerEntity) {
            if (thisEntity.getRandom().nextInt(10) <= lootingMultiplier) {
                thisEntity.dropStack(EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(ObsidianToolzMod.ZEUS, 1)));
                if (!thisEntity.world.isClient) {
                    thisEntity.world.playSound(
                            null, // Player - if non-null, will play sound for every nearby player *except* the specified player
                            new BlockPos(thisEntity.getPos()), // The position of where the sound will come from
                            SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, // The sound that will play, in this case, the sound the anvil plays when it lands.
                            SoundCategory.MASTER, // This determines which of the volume sliders affect this sound
                            1f, //Volume multiplier, 1 is normal, 0.5 is half volume, etc
                            1f // Pitch multiplier, 1 is normal, 0.5 is half pitch, etc
                    );
                }
            }
        }

    }
}
