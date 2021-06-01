package net.doombubbles.obsidiantoolz.mixin;

import net.doombubbles.obsidiantoolz.Items.GeodeStuff;
import net.doombubbles.obsidiantoolz.Items.RocketBoots;
import net.doombubbles.obsidiantoolz.ObsidianToolzMod;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Arm;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin extends LivingEntity {

    @Shadow
    public HungerManager hungerManager;
    @Shadow
    private int totalExperience;

    private int rocketBoots = 30;

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(at = @At("HEAD"), method = "tick")
    public void tick(CallbackInfo info) {
        PlayerEntity playerEntity = (PlayerEntity)(Object)this;
        int pLevel = EnchantmentHelper.getEquipmentLevel(ObsidianToolzMod.PHOTOSYNTHESIS, playerEntity);
        if (pLevel > 0 && playerEntity.world.isSkyVisible(new BlockPos(playerEntity.getPos())) && playerEntity.world.isDay()) {
            if (playerEntity.world.random.nextInt(600) < pLevel) {
                hungerManager.add(1 , 1f);
            }
        }
        if (playerEntity instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) playerEntity;
            rocketBoots = RocketBoots.tick(serverPlayerEntity, rocketBoots);
        }
        //GeodeStuff.tickPlayer(playerEntity);
    }

    boolean pre = false;

    @Inject(at=@At("HEAD"), method = "handleFallDamage")
    public void preHandleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) (Object)this;
        if (RocketBoots.getRocketBoots(serverPlayerEntity) != ItemStack.EMPTY) {
            pre = serverPlayerEntity.getAbilities().allowFlying;
            serverPlayerEntity.getAbilities().allowFlying = false;
        }
    }

    @Inject(at=@At("TAIL"), method = "handleFallDamage")
    public void postHandleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) (Object)this;
        if (RocketBoots.getRocketBoots(serverPlayerEntity) != ItemStack.EMPTY) {
            serverPlayerEntity.getAbilities().allowFlying = pre;
        }
    }



    public void dropXp(PlayerEntity player, CallbackInfoReturnable<Integer> cI) {
        if (!this.world.getGameRules().getBoolean(GameRules.KEEP_INVENTORY) && !this.isSpectator()) {
            cI.setReturnValue(totalExperience / 2);
        }
    }

    @Shadow
    public Iterable<ItemStack> getArmorItems() {
        return null;
    }

    @Shadow
    public ItemStack getEquippedStack(EquipmentSlot slot) {
        return null;
    }

    @Shadow
    public void equipStack(EquipmentSlot slot, ItemStack stack) {

    }

    @Shadow
    public Arm getMainArm() {
        return null;
    }
}
