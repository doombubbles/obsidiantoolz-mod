package net.doombubbles.obsidiantoolz;

import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class CustomRaidStuff {

    public static int BANNERS = 0;



    public static void pillagerBonus(LivingEntity pillagerEntity) {
        ItemStack crossbow = pillagerEntity.getMainHandStack();
        int powerLevel = (int) (Math.sqrt(BANNERS));
        if (powerLevel > 0) {
            crossbow.addEnchantment(Enchantments.POWER, powerLevel);
        }
    }

    public static void vindicatorBonus(LivingEntity vindicatorEntity) {
        if (BANNERS >= 10) {
            ItemStack axe = new ItemStack(Items.DIAMOND_AXE);
            if (BANNERS >= 50) {
                axe = new ItemStack(Items.NETHERITE_AXE);
            }
            int sharpness = (int) (Math.sqrt(BANNERS));
            if (sharpness > 0) {
                axe.addEnchantment(Enchantments.SHARPNESS, sharpness);
            }

            vindicatorEntity.equipStack(EquipmentSlot.MAINHAND, axe);
        }

        if (BANNERS >= 40) {
            vindicatorEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 20 * 5 * 60, 1));
        } else if (BANNERS >= 30) {
            vindicatorEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 20 * 5 * 60, 1));
        }
    }



    public static void bonus(LivingEntity entity) {
        if (BANNERS >= 20) {
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 20 * 5 * 60, 1));
        } else if (BANNERS >= 10) {
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 20 * 5 * 60, 1));
        }

        if (BANNERS > 0) {
            entity.addStatusEffect(new StatusEffectInstance(new StatusEffectInstance(StatusEffects.ABSORPTION, 20 * 5 * 60, BANNERS / 4)));
        }
    }


    public static void roundEnded(ServerWorld world, Set<UUID> heroesOfTheVillage) {
        Iterator<UUID> var12 = heroesOfTheVillage.iterator();
        while(var12.hasNext()) {
            UUID uUID = var12.next();
            Entity entity = world.getEntity(uUID);
            if (entity instanceof LivingEntity && !entity.isSpectator()) {
                LivingEntity livingEntity = (LivingEntity)entity;
                if (livingEntity instanceof ServerPlayerEntity) {
                    ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)livingEntity;
                    serverPlayerEntity.giveItemStack(new ItemStack(Items.EMERALD, world.random.nextInt(BANNERS + 1)));
                }
            }
        }
    }


    public static void calculateBanners(BlockPos blockPos, ServerWorld world) {
        BANNERS = 0;
        /*
        Set<BlockEntity> banners = world.getEntitiesByType().stream().filter(blockEntity -> blockEntity instanceof BannerBlockEntity).collect(Collectors.toSet());
        for (BlockEntity banner : banners) {
            if (Math.abs(banner.getPos().getX() - blockPos.getX()) < 100 && Math.abs(banner.getPos().getZ() - blockPos.getZ()) < 100) {
                BannerBlockEntity bannerBlockEntity = (BannerBlockEntity) banner;
                Text match = (new TranslatableText("block.minecraft.ominous_banner")).formatted(Formatting.GOLD);
                if (bannerBlockEntity.getCustomName() != null && bannerBlockEntity.getCustomName().asString().equals(match.asString())) {
                    BANNERS++;
                }
            }
        }
        */
        //System.out.println("BlockPos: " + blockPos.toString());
        //System.out.println("I think there are " + BANNERS + " banners");
    }
}
