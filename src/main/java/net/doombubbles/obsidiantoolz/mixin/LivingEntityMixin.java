package net.doombubbles.obsidiantoolz.mixin;

import net.doombubbles.obsidiantoolz.Enchantments.MagmaWalkerEnchantment;
import net.doombubbles.obsidiantoolz.Enchantments.MagneticEnchantment;
import net.doombubbles.obsidiantoolz.Enchantments.OverprotectionEnchantment;
import net.doombubbles.obsidiantoolz.Items.ObsidianStuff;
import net.doombubbles.obsidiantoolz.ObsidianToolzMod;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashSet;
import java.util.Set;

@Mixin(LivingEntity.class)
public class LivingEntityMixin extends Entity {

    @Shadow
    protected int playerHitTimer;
    @Shadow
    protected PlayerEntity attackingPlayer;

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Shadow
    protected void dropXp() {}

    public Set<BlockPos> magmas = new HashSet<>();

    @Inject(at = @At("TAIL"), method = "drop")
    protected void doGrinding(DamageSource source, CallbackInfo callbackInfo) {
        LivingEntity thisEntity = (LivingEntity) (Object) this;
        if (source.getAttacker() instanceof LivingEntity && thisEntity instanceof MobEntity) {
            int grindLevel = EnchantmentHelper.getEquipmentLevel(ObsidianToolzMod.GRINDING, (LivingEntity) source.getAttacker());
            if (grindLevel > 0) {
                int i = source.getAttacker().world.random.nextInt(grindLevel + 2) - 1;
                if (i < 0) {
                    i = 0;
                }
                for (int j = 0; j < i; j++) {
                    this.dropXp();
                }
            }
        }
    }
    /*
    protected void doSpecialDrops(DamageSource source, int lootingMultiplier, boolean allowDrops, CallbackInfo info) {
        LivingEntity thisEntity = (LivingEntity)(Object)this;
        if ((Object)this instanceof CreeperEntity) {
            if (((CreeperEntity)(Object)this).shouldRenderOverlay() && allowDrops && !source.isProjectile() && !source.getMagic() && !source.isFire() && source.getAttacker() instanceof PlayerEntity) {
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
        if ((Object)this instanceof WitherEntity) {
            int level = 1;
            if (thisEntity.getRandom().nextInt(5) <= lootingMultiplier) {
                level = 2;
            }
            /*
            if (thisEntity.getRandom().nextInt(13) <= lootingMultiplier) {
                level = 3;
            }
            /
            thisEntity.dropStack(EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(ObsidianToolzMod.WITHER_ASPECT, level)));
        }
        if ((Object)this instanceof WitherSkeletonEntity) {
            if (thisEntity.getRandom().nextInt(100) <= lootingMultiplier) {
                thisEntity.dropStack(EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(ObsidianToolzMod.WITHER_ASPECT, 1)));
            }
        }
        if ((Object)this instanceof EnderDragonEntity) {
            if (source.getAttacker() instanceof PlayerEntity) {
                PlayerEntity pE = (PlayerEntity) source.getAttacker();
                if (pE.getMainHandStack().getTag().contains("RepairCost")) {
                    pE.getMainHandStack().getTag().put("RepairCost", NbtInt.of(0));
                }
            }
        }
    }
    */



    public int lastOverprotectionAmount = 0;

    @Inject(at = @At("TAIL"), method = "tick")
    public void tick(CallbackInfo info) {
        LivingEntity entity = (LivingEntity) (Object) this;
        magmas.removeIf(blockPos -> {
            if (entity.world.random.nextInt(60) == 0) {
                if (entity.world.getBlockState(blockPos).getBlock() == Blocks.MAGMA_BLOCK && !entity.world.isClient()) {
                    entity.world.setBlockState(blockPos, Blocks.LAVA.getDefaultState());
                }
                return true;
            }
            return false;
        });
        lastOverprotectionAmount = OverprotectionEnchantment.doOverprotection(entity, lastOverprotectionAmount);

    }


    @Inject(at = @At("TAIL"), method = "applyMovementEffects")
    public void magmaWalker(BlockPos blockPos, CallbackInfo callbackInfo) {
        LivingEntity entity = (LivingEntity) (Object)this;
        int level = EnchantmentHelper.getEquipmentLevel(ObsidianToolzMod.MAGMA_WALKER, entity);
        if (entity.isOnGround() && level > 0) {
            MagmaWalkerEnchantment.doMagmaWalker(blockPos, entity, level, magmas);
        }
    }

    public PlayerEntity mugger = null;

    @Inject(at = @At("HEAD"), method = "damage")
    public void preHurt(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cI) {
        if (source.getAttacker() instanceof PlayerEntity && EnchantmentHelper.getEquipmentLevel(ObsidianToolzMod.MUGGING, (PlayerEntity) source.getAttacker()) > 0) {
            mugger = (PlayerEntity) source.getAttacker();
        }
    }

    @Inject(at = @At("TAIL"), method = "damage")
    public void postHurt(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cI) {
        mugger = null;
    }


    @Shadow
    protected void initDataTracker() {

    }

    @Shadow
    public void readCustomDataFromNbt(NbtCompound nbt) {

    }

    @Shadow
    public void writeCustomDataToNbt(NbtCompound nbt) {

    }

    @Override
    public ItemEntity dropStack(ItemStack stack, float yOffset) {
        if (stack.isEmpty()) {
            return null;
        } else if (this.world.isClient) {
            return null;
        } else {
            if (this.mugger != null && EnchantmentHelper.getLevel(ObsidianToolzMod.MUGGING, mugger.getMainHandStack()) > 1) {
                ItemStack off = mugger.getOffHandStack();
                if (off.getItem() instanceof BlockItem && ((BlockItem) off.getItem()).getBlock() instanceof ShulkerBoxBlock) {
                    DefaultedList<ItemStack> inventory = DefaultedList.ofSize(27, ItemStack.EMPTY);
                    NbtCompound tag = off.getOrCreateTag().getCompound("BlockEntityTag");
                    Inventories.readNbt(tag, inventory);

                    MagneticEnchantment.putInDefaultedList(inventory, stack);

                    NbtCompound newTag = new NbtCompound();
                    Inventories.writeNbt(newTag, inventory);
                    off.putSubTag("BlockEntityTag", newTag);

                    mugger.playerScreenHandler.sendContentUpdates();
                }
            }

            if (stack.getCount() > 0) {
                if (this.mugger != null && mugger.giveItemStack(stack)) {
                    world.playSound(null, mugger.getX(), mugger.getY(), mugger.getZ(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, (world.random.nextFloat() - world.random.nextFloat()) * 1.4F + 2.0F);
                    return null;
                } else {
                    ItemEntity itemEntity = new ItemEntity(this.world, this.getX(), this.getY() + (double)yOffset, this.getZ(), stack);
                    itemEntity.setToDefaultPickupDelay();
                    this.world.spawnEntity(itemEntity);
                    return itemEntity;
                }
            }
            return null;
        }
    }

    @Inject(method = "dropXp", at=@At("HEAD"), cancellable = true)
    public void changeDropXp(CallbackInfo cI) {
        if (mugger != null) {
            cI.cancel();
            if (!this.world.isClient && (this.shouldAlwaysDropXp() || this.playerHitTimer > 0 && this.shouldDropXp() && this.world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT))) {
                int i = this.getXpToDrop(this.attackingPlayer);

                while(i > 0) {
                    int j = ExperienceOrbEntity.roundToOrbSize(i);
                    i -= j;
                    this.world.spawnEntity(new ExperienceOrbEntity(this.world, mugger.getX(), mugger.getY(), mugger.getZ(), j));
                }
            }
        }
    }

    @Shadow
    protected int getXpToDrop(PlayerEntity player) {
        return 0;
    }

    @Shadow
    protected boolean shouldAlwaysDropXp() {
        return false;
    }

    @Shadow
    public Packet<?> createSpawnPacket() {
        return null;
    }

    @Shadow
    protected boolean shouldDropXp() {
        return true;
    }

    @Inject(at = @At("HEAD"), method = "takeKnockback", cancellable = true)
    private void obsidianShield(double strength, double x, double z, CallbackInfo ci) {
        for (ItemStack itemStack : this.getItemsHand()) {
            if (ObsidianStuff.is(itemStack) && itemStack.getItem() == Items.SHIELD) {
                ci.cancel();
            }
        }
    }

}
