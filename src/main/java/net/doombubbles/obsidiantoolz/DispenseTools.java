package net.doombubbles.obsidiantoolz;

import net.doombubbles.obsidiantoolz.Enchantments.VeinMinerEnchantment;
import net.doombubbles.obsidiantoolz.mixin.DispenserBlockAccessor;
import net.doombubbles.obsidiantoolz.mixin.DispenserBlockMixin;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.ProjectileDispenserBehavior;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.thrown.ExperienceBottleEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;

import java.util.Objects;

public class DispenseTools {

    private static final String DISPENSER_PROGRESS = "dispenserProgress";

    public static DispenserBehavior dispenseToolBehavior() {
        return (pointer, stack) -> {
            ServerWorld world = pointer.getWorld();
            if (!world.isClient()) {
                BlockPos blockPos = pointer.getBlockPos().offset(pointer.getBlockState().get(DispenserBlock.FACING));
                BlockState blockState = world.getBlockState(blockPos);

                float hardness = blockState.getHardness(world, blockPos);
                if (!blockState.isAir() && hardness >= 0) {
                    float miningSpeedMultiplier = stack.getMiningSpeedMultiplier(blockState);
                    if (miningSpeedMultiplier > 1.0F) {
                        int i = EnchantmentHelper.getLevel(Enchantments.EFFICIENCY, stack);
                        if (i > 0) {
                            miningSpeedMultiplier += (float) (i * i + 1);
                        }
                    }

                    float progress = 0;

                    if (stack.getTag() != null && stack.getTag().contains(DISPENSER_PROGRESS)) {
                        progress = stack.getTag().getFloat(DISPENSER_PROGRESS);
                    }

                    boolean good = (stack.isSuitableFor(blockState) || !blockState.isToolRequired());
                    float damage = miningSpeedMultiplier / (good ? 30 : 100);

                    progress += damage * 4; // 4 game ticks per dispenser fire

                    if (progress >= hardness) {
                        if (good) {
                            LootContext.Builder builder = new LootContext.Builder(world)
                                    .random(world.random)
                                    .parameter(LootContextParameters.ORIGIN, Vec3d.ofCenter(blockPos))
                                    .parameter(LootContextParameters.TOOL, stack);
                            boolean magnet = EnchantmentHelper.getLevel(ObsidianToolzMod.MAGNETIC, stack) > 0;
                            DispenserBlockEntity dispenserBlockEntity = pointer.getBlockEntity();
                            blockState.getDroppedStacks(builder).forEach(itemStack -> {
                                if (magnet) {
                                    itemStack = HopperBlockEntity.transfer(null, dispenserBlockEntity, itemStack, null);
                                    if (!itemStack.isEmpty()) {
                                        Block.dropStack(world, pointer.getBlockPos(), itemStack);
                                    }
                                } else {
                                    Block.dropStack(world, blockPos, itemStack);
                                }
                            });
                            blockState.onStacksDropped(world, blockPos, ItemStack.EMPTY);
                            VeinMinerEnchantment.tryVeinMining(world, blockPos, blockState, null, stack, dispenserBlockEntity);
                        }
                        world.breakBlock(blockPos, false);


                        stack.damage(1, world.random, null);
                        progress = 0;
                    }

                    stack.getOrCreateTag().putFloat(DISPENSER_PROGRESS, progress);
                }


            }
            return stack;
        };
    }


    public static DispenserBehavior dispenseMendBehavior() {
        return (pointer, stack) -> {
            ServerWorld world = pointer.getWorld();
            if (!world.isClient()) {
                BlockPos blockPos = pointer.getBlockPos().offset(pointer.getBlockState().get(DispenserBlock.FACING));
                BlockEntity blockEntity = world.getBlockEntity(blockPos);

                if (blockEntity instanceof Inventory) {
                    stack.decrement(1);
                    Inventory inventory = (Inventory) blockEntity;

                    world.syncWorldEvent(WorldEvents.SPLASH_POTION_SPLASHED, blockPos, PotionUtil.getColor(Potions.WATER));

                    int i = 3 + world.random.nextInt(5) + world.random.nextInt(5);
                    i += 5 + world.random.nextInt(10) + world.random.nextInt(10);
                    i *= 2;


                    for (int slot = 0; slot < inventory.size(); slot++) {
                        ItemStack itemStack = inventory.getStack(slot);
                        if (itemStack != null && !itemStack.isEmpty() && itemStack.isDamaged() && EnchantmentHelper.getLevel(Enchantments.MENDING, itemStack) > 0){
                            int j = Math.min(i, itemStack.getDamage());
                            itemStack.setDamage(itemStack.getDamage() - j);
                            i -= j;
                        }
                        if (i <= 0) {
                            break;
                        }
                    }

                    while (i > 0) {
                        int j = ExperienceOrbEntity.roundToOrbSize(i);
                        i -= j;
                        world.spawnEntity(new ExperienceOrbEntity(world, blockPos.getX() + .5, blockPos.getY() + .5, blockPos.getZ() + .5, j));
                    }


                } else {
                    DispenserBehavior dispenserBehavior = DispenserBlockAccessor.getBehaviors().get(Items.EXPERIENCE_BOTTLE);
                    return dispenserBehavior.dispense(pointer, stack);
                }
            }

            return stack;
        };
    }


    public static boolean isFilter(Inventory inventory) {
        if (inventory instanceof HopperBlockEntity) {
            HopperBlockEntity hopperBlockEntity = (HopperBlockEntity) inventory;
            return hopperBlockEntity.hasCustomName() && Objects.requireNonNull(hopperBlockEntity.getCustomName()).asString().toLowerCase().contains("filter");
        }
        return false;
    }
}
