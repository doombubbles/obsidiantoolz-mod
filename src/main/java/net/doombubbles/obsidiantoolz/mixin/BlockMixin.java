package net.doombubbles.obsidiantoolz.mixin;


import net.doombubbles.obsidiantoolz.Enchantments.MagneticEnchantment;
import net.doombubbles.obsidiantoolz.Enchantments.VeinMinerEnchantment;
import net.doombubbles.obsidiantoolz.ObsidianToolzMod;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.text.WordUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public class BlockMixin
{
    @Inject(at = @At("HEAD"),method = "onBreak",cancellable = false)
    public void doVeinMine(World world, BlockPos blockPos, BlockState blockState, PlayerEntity pe, CallbackInfo ci)
    {
        VeinMinerEnchantment.tryVeinMining(world, blockPos, blockState, pe);
    }


    /*
    @Inject(at = @At("RETURN"),method = "getDroppedStacks", cancellable = true)
    public void doMagnetic(BlockState state, ServerWorld world, BlockPos pos, @Nullable BlockEntity blockEntity, @Nullable Entity entity, ItemStack stack, CallbackInfoReturnable<List<ItemStack>> ci)
    {
        MagneticEnchantment.doMagnet(entity, ci);
    }
    */

    @Inject(at = @At("HEAD"), method = "dropStacks(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/entity/Entity;Lnet/minecraft/item/ItemStack;)V", cancellable = true)
    private static void dropStacks(BlockState state, World world, BlockPos pos, BlockEntity blockEntity, Entity entity, ItemStack tool, CallbackInfo callbackInfo) {
        if (entity instanceof PlayerEntity && EnchantmentHelper.getEquipmentLevel(ObsidianToolzMod.MAGNETIC, (LivingEntity) entity) > 0 && world instanceof ServerWorld) {
            MagneticEnchantment.doMagnet(state, (ServerWorld) world, pos, blockEntity, entity, tool, callbackInfo, EnchantmentHelper.getEquipmentLevel(ObsidianToolzMod.MAGNETIC, (LivingEntity) entity));
        }
    }

    @Inject(method = "onBreak", at = @At("HEAD"), cancellable = true)
    private void onBroken(World world, BlockPos pos, BlockState blockState, PlayerEntity player, CallbackInfo ci) {
        if (blockState.getBlock() == Blocks.SPAWNER && (EnchantmentHelper.getLevel(ObsidianToolzMod.SPAWNER_TOUCH, player.getMainHandStack()) > 0
                || EnchantmentHelper.getLevel(Enchantments.SILK_TOUCH, player.getMainHandStack()) > 1)) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof MobSpawnerBlockEntity) {
                ItemStack stack = new ItemStack(Blocks.SPAWNER);
                stack.setTag(new NbtCompound());
                NbtCompound tag = new NbtCompound();
                blockEntity.writeNbt(tag);
                stack.getTag().put("spawnerData", tag);
                String name = WordUtils.capitalize(tag.getCompound("SpawnData").get("id").asString().split(":")[1].replace("_", " "));
                stack.setCustomName(ObsidianToolzMod.asName(name + " Spawner"));
                //stack.getOrCreateSubTag("display").put("Name", NbtString.of("{\"text\":\"" + name + " Spawner" + "\",\"italic\":false}"));
                int magnet = EnchantmentHelper.getLevel(ObsidianToolzMod.MAGNETIC, player.getMainHandStack());
                if (magnet > 0) {
                    MagneticEnchantment.magnetStack(player, magnet, stack, world, pos);
                } else {
                    world.spawnEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), stack));
                }
            }
        }
    }

    @Inject(method = "onPlaced", at = @At("HEAD"))
    private void onPlaced(World world, BlockPos blockPos, BlockState blockState, LivingEntity livingEntity, ItemStack itemStack, CallbackInfo ci) {
        BlockEntity blockEntity = world.getBlockEntity(blockPos);
        if (blockEntity instanceof MobSpawnerBlockEntity && itemStack.getTag() != null && itemStack.getTag().contains("spawnerData")) {
            blockEntity.readNbt(itemStack.getTag().getCompound("spawnerData"));
            world.updateListeners(blockPos, world.getBlockState(blockPos), world.getBlockState(blockPos), 3);
        }
    }

}

