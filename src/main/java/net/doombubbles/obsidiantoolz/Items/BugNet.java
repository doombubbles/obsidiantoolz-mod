package net.doombubbles.obsidiantoolz.Items;

import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.FallibleItemDispenserBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MagmaCubeEntity;
import net.minecraft.entity.mob.PillagerEntity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.*;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.List;

public class BugNet {

    public static boolean isValid(LivingEntity entity, ItemStack bugNet) {
        return entity instanceof PassiveEntity || entity instanceof WaterCreatureEntity || (entity instanceof PillagerEntity && entity.getStackInHand(entity.getActiveHand()).isEmpty())
                || (entity instanceof SlimeEntity && ((SlimeEntity) entity).isSmall()) || (entity instanceof MagmaCubeEntity && ((MagmaCubeEntity) entity).isSmall());
    }

    public static boolean is(ItemStack stack) {
        return stack.getItem() == Items.CARROT_ON_A_STICK && stack.getTag() != null
                && stack.getTag().get("CustomModelData") != null && stack.getTag().getInt("CustomModelData") == 1;
    }


    public static boolean onInteract(LivingEntity entity, PlayerEntity playerEntity, ItemStack bugNet, Hand hand) {
        ItemStack eggItemStack;
        SpawnEggItem spawnEggItem = null;
        for (SpawnEggItem eggItem : SpawnEggItem.getAll()) {
            if (eggItem.isOfSameEntityType(null, entity.getType())) {
                spawnEggItem = eggItem;
                break;
            }
        }
        if (spawnEggItem == null) return false;
        eggItemStack = new ItemStack(spawnEggItem);
        if (eggItemStack.isEmpty()) return false;
        eggItemStack.getOrCreateTag();
        NbtCompound entityTag = new NbtCompound();
        entity.saveNbt(entityTag);
        eggItemStack.putSubTag("EntityTag", entityTag);
        eggItemStack.getSubTag("EntityTag").remove("Pos");
        eggItemStack.getSubTag("EntityTag").remove("Motion");
        /*
        if (entity instanceof VillagerEntity) {
            VillagerEntity villagerEntity = (VillagerEntity) entity;
            String profession = villagerEntity.getVillagerData().getProfession().toString();
            String name = profession.substring(0, 1).toUpperCase() + profession.substring(1) + " Villager Spawn Egg";
            eggItemStack.setCustomName(ObsidianToolzMod.asName(name));
        }
         */
        if (entityTag.contains("CustomName")) {
            eggItemStack.getOrCreateSubTag("display").put("Name", NbtString.of(entityTag.getString("CustomName")));
        }
        NbtList lore = eggItemStack.getOrCreateSubTag("display").getList("Lore", NbtElement.STRING_TYPE);
        lore.add(NbtString.of("\"(+NBT)\""));
        eggItemStack.getOrCreateSubTag("display").put("Lore", lore);
        entity.dropStack(eggItemStack);
        if (playerEntity != null && hand != null) {
            playerEntity.swingHand(hand, true);
            bugNet.damage(1, playerEntity, p -> p.sendToolBreakStatus(hand));
        } else {
            bugNet.damage(1, entity.getRandom(), null);
        }
        entity.world.playSound(null, entity.getBlockPos(), SoundEvents.ITEM_HOE_TILL, SoundCategory.BLOCKS, 1.0F, 2.0F);
        entity.remove(Entity.RemovalReason.DISCARDED);
        return true;
    }

    public static DispenserBehavior dispensorBehavior() {
        return new FallibleItemDispenserBehavior() {
            @Override
            protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
                World world = pointer.getWorld();
                if (!world.isClient()) {
                    this.setSuccess(false);
                    BlockPos blockPos = pointer.getBlockPos().offset(pointer.getBlockState().get(DispenserBlock.FACING));
                    List<LivingEntity> list = world.getNonSpectatingEntities(LivingEntity.class, new Box(blockPos));

                    for (LivingEntity livingEntity : list) {
                        if (livingEntity.isAlive() && isValid(livingEntity, stack) && onInteract(livingEntity, null, stack, null)) {
                            this.setSuccess(true);
                        }
                    }
                }

                return stack;
            }
        };
    }
}
