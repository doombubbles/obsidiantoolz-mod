package net.doombubbles.obsidiantoolz.Enchantments;

import net.doombubbles.obsidiantoolz.ObsidianToolzEnchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;

public class ZeusEnchantment extends ObsidianToolzEnchantment {

    public ZeusEnchantment(Rarity rarity, EnchantmentTarget type, EquipmentSlot[] slotTypes) {
        super(rarity, type, slotTypes);
    }

    @Override
    public int getMinPower(int level) {
        return 1;
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public void onTargetDamaged(LivingEntity user, Entity target, int level) {
        if (target instanceof LivingEntity && user.world instanceof ServerWorld) {
            BlockPos blockPos = new BlockPos(target.getPos());
            if (user.world.isSkyVisible(blockPos.add(0, 1, 0))) {
                LightningEntity lightningEntity = new LightningEntity(EntityType.LIGHTNING_BOLT, user.world);
                lightningEntity.setPos((double)blockPos.getX() + 0.5D, (double)blockPos.getY(), (double)blockPos.getZ() + 0.5D);
                lightningEntity.setCosmetic(level < 2);
                lightningEntity.setChanneler(user instanceof ServerPlayerEntity ? (ServerPlayerEntity)user : null);
                lightningEntity.setSilent(true);
                user.world.spawnEntity(lightningEntity);
                user.playSound(SoundEvents.ITEM_TRIDENT_THUNDER, 1.0f, 1.0F);
                user.playSound(SoundEvents.ENTITY_LIGHTNING_BOLT_IMPACT, 1.0f, 1.0F);
            }
        }
        super.onTargetDamaged(user, target, level);
    }

    @Override
    public boolean isTreasure() {
        return true;
    }

    @Override
    public String getRealName(int level) {
        return "Zeus" + (level > 1 ? " " + toRoman(level) : "");
    }
}
