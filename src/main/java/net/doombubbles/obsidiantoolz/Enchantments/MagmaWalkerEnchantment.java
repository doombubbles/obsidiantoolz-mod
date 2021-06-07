package net.doombubbles.obsidiantoolz.Enchantments;

import net.doombubbles.obsidiantoolz.ObsidianToolzEnchantment;
import net.minecraft.block.*;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class MagmaWalkerEnchantment extends ObsidianToolzEnchantment {

    public MagmaWalkerEnchantment(Rarity rarity, EnchantmentTarget type, EquipmentSlot[] slotTypes) {
        super(rarity, type, slotTypes);
    }

    @Override
    public int getMaxLevel() {
        return 2;
    }

    @Override
    protected boolean canAccept(Enchantment other) {
        return super.canAccept(other) && other != Enchantments.DEPTH_STRIDER && other != Enchantments.FROST_WALKER;
    }

    @Override
    public boolean isTreasure() {
        return true;
    }

    @Override
    public String getRealName(int level) {
        return "Magma Walker " + toRoman(level);
    }

    public static void doMagmaWalker(BlockPos blockPos, LivingEntity entity, int level, Collection<BlockPos> magmas) {
        BlockState blockState = Blocks.MAGMA_BLOCK.getDefaultState();
        float f = (float)Math.min(16, 2 + level);
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        Iterator var7 = BlockPos.iterate(blockPos.add((double)(-f), -1.0D, (double)(-f)), blockPos.add((double)f, -1.0D, (double)f)).iterator();

        while(var7.hasNext()) {
            BlockPos blockPos2 = (BlockPos)var7.next();
            if (blockPos2.isWithinDistance(entity.getPos(), (double)f)) {
                mutable.set(blockPos2.getX(), blockPos2.getY() + 1, blockPos2.getZ());
                BlockState blockState2 = entity.world.getBlockState(mutable);
                if (blockState2.isAir()) {
                    BlockState blockState3 = entity.world.getBlockState(blockPos2);
                    if (blockState3.getMaterial() == Material.LAVA && (Integer)blockState3.get(FluidBlock.LEVEL) == 0 && blockState.canPlaceAt(entity.world, blockPos2) && entity.world.canPlace(blockState, blockPos2, ShapeContext.absent())) {
                        magmas.add(blockPos2.toImmutable());
                        entity.world.setBlockState(blockPos2, blockState);
                    }
                }
            }
        }
    }
}
