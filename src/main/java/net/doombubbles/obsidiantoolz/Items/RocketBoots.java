package net.doombubbles.obsidiantoolz.Items;

import net.doombubbles.obsidiantoolz.ObsidianToolzMod;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

public class RocketBoots {

    public static final int DEFAULT_TIME = 30;

    public static ItemStack getRocketBoots(ServerPlayerEntity entity) {
        for (ItemStack armorItem : entity.getArmorItems()) {
            if (is(armorItem)) {
                return armorItem;
            }
        }
        return ItemStack.EMPTY;
    }

    public static boolean is(ItemStack armorItem) {
        return armorItem.getItem() == Items.LEATHER_BOOTS && armorItem.getTag() != null && armorItem.getTag().get("CustomModelData") != null && armorItem.getTag().getInt("CustomModelData") == 1;
    }

    public static int getMaxRocketBootsNumber(ItemStack rocketBoots) {
        return DEFAULT_TIME + 5 * EnchantmentHelper.getLevel(ObsidianToolzMod.BOOSTED, rocketBoots);
    }


    public static int tick(ServerPlayerEntity serverPlayerEntity, int rocketBoots) {
        ItemStack boots = getRocketBoots(serverPlayerEntity);
        if (boots != ItemStack.EMPTY && !serverPlayerEntity.isCreative() && !serverPlayerEntity.isSpectator() && boots.getMaxDamage() - boots.getDamage() > 1) {
            if (serverPlayerEntity.getAbilities().getFlySpeed() != .03f) {
                serverPlayerEntity.getAbilities().setFlySpeed(0.03f);
                serverPlayerEntity.sendAbilitiesUpdate();
            }

            if (serverPlayerEntity.getAbilities().flying) {
                rocketBoots--;
                if (rocketBoots < 0) {
                    rocketBoots = 0;
                    if (serverPlayerEntity.getAbilities().allowFlying) {
                        serverPlayerEntity.getAbilities().allowFlying = false;
                        serverPlayerEntity.getAbilities().flying = false;
                        serverPlayerEntity.sendAbilitiesUpdate();
                    }
                } else {
                    if (serverPlayerEntity.getRandom().nextInt(100) == 0) {
                        boots.damage(1, serverPlayerEntity.getRandom(), serverPlayerEntity);
                    }

                    double x1 = serverPlayerEntity.getX() + .25 * Math.cos(Math.PI * serverPlayerEntity.bodyYaw / 180);
                    double z1 = serverPlayerEntity.getZ() + .25 * Math.sin(Math.PI * serverPlayerEntity.bodyYaw / 180);
                    double x2 = serverPlayerEntity.getX() - .25 * Math.cos(Math.PI * serverPlayerEntity.bodyYaw / 180);
                    double z2 = serverPlayerEntity.getZ() - .25 * Math.sin(Math.PI * serverPlayerEntity.bodyYaw / 180);
                    double y = serverPlayerEntity.getY();
                    for (ServerPlayerEntity player : serverPlayerEntity.getServerWorld().getPlayers()) {
                        if (player == serverPlayerEntity) {
                            continue;
                        }
                        serverPlayerEntity.getServerWorld().spawnParticles(player, ParticleTypes.FLAME,false , x1, y - .5, z1, 1, 0, 0, 0, 0);
                        serverPlayerEntity.getServerWorld().spawnParticles(player, ParticleTypes.FLAME, false, x2, y - .5, z2, 1, 0, 0, 0, 0);
                    }
                    serverPlayerEntity.getServerWorld().spawnParticles(serverPlayerEntity, ParticleTypes.FLAME, false, x1, y - .1, z1, 1, 0, 0, 0, 0);
                    serverPlayerEntity.getServerWorld().spawnParticles(serverPlayerEntity, ParticleTypes.FLAME, false, x2, y - .1, z2, 1, 0, 0, 0, 0);
                    serverPlayerEntity.getServerWorld().playSound(null, serverPlayerEntity.getX(), serverPlayerEntity.getY(), serverPlayerEntity.getZ(), SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE,
                            SoundCategory.PLAYERS, .5f, 1.75f);
                }
            }
            if (serverPlayerEntity.isOnGround()) {
                rocketBoots = getMaxRocketBootsNumber(boots);
            } /*else if (rocketBoots < 30 && !serverPlayerEntity.getAbilities().flying) {
                rocketBoots = 0;
                if (serverPlayerEntity.getAbilities().allowFlying) {
                    serverPlayerEntity.getAbilities().allowFlying = false;
                    serverPlayerEntity.sendAbilitiesUpdate();
                }
            }*/

            if (!serverPlayerEntity.getAbilities().allowFlying && rocketBoots == getMaxRocketBootsNumber(boots)) {
                serverPlayerEntity.getAbilities().allowFlying = true;
                serverPlayerEntity.sendAbilitiesUpdate();
            }
        } else if (serverPlayerEntity.getAbilities().getFlySpeed() != .05f && !serverPlayerEntity.isSpectator()) {
            NbtCompound tag = new NbtCompound();
            serverPlayerEntity.getAbilities().setFlySpeed(0.05f);
            serverPlayerEntity.sendAbilitiesUpdate();
        } else if (!serverPlayerEntity.isCreative() && !serverPlayerEntity.isSpectator() && serverPlayerEntity.getAbilities().allowFlying) {
            serverPlayerEntity.getAbilities().allowFlying = false;
            serverPlayerEntity.getAbilities().flying = false;
            serverPlayerEntity.sendAbilitiesUpdate();
        }

        return rocketBoots;
    }
}
