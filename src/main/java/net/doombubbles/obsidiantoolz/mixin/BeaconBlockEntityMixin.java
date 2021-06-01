package net.doombubbles.obsidiantoolz.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Iterator;
import java.util.List;

@Mixin(BeaconBlockEntity.class)
public class BeaconBlockEntityMixin extends BlockEntity {

    public BeaconBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }


    private static int getScore(Block block) {
        if (block == Blocks.NETHERITE_BLOCK) return 10;
        if (block == Blocks.DIAMOND_BLOCK) return 5;
        if (block == Blocks.EMERALD_BLOCK) return 3;
        if (block == Blocks.GOLD_BLOCK) return 2;
        return 1;
    }

    private static double calculate(BlockPos pos, int level, World world) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();

        double d = 10.0;

        int netheriteLayers = 0;

        for(int i = 1; i <= level; i++) {
            int j = y - i;
            if (j < 0) {
                break;
            }

            Block worstBlock = Blocks.NETHERITE_BLOCK;

            for(int k = x - i; k <= x + i; ++k) {
                for(int l = z - i; l <= z + i; ++l) {
                    Block block = world.getBlockState(new BlockPos(k, j, l)).getBlock();
                    if (getScore(worstBlock) > getScore(block)) {
                        worstBlock = block;
                    }
                }
            }

            d += 10 * getScore(worstBlock);
            if (worstBlock == Blocks.NETHERITE_BLOCK) {
                netheriteLayers++;
            }
        }

        d *= Math.pow(2, netheriteLayers);

        return d;
    }

    /**
     * @author doombubbles
     * @reason the default beacon code becomes super slow when the distance is long.
     * that code needs to not run one way or another.
     */
    @Overwrite
    private static void applyPlayerEffects(World world, BlockPos pos, int beaconLevel, @Nullable StatusEffect primaryEffect, @Nullable StatusEffect secondaryEffect) {
        if (!world.isClient && primaryEffect != null) {
            double distance = calculate(pos, beaconLevel, world);

            int amplifier = 0;
            if (beaconLevel >= 4 && primaryEffect == secondaryEffect) {
                amplifier = 1;
            }

            int duration = (9 + beaconLevel * 2) * 20;

            for (PlayerEntity player : world.getPlayers()) {
                if (Math.abs(player.getX() - pos.getX()) < distance && Math.abs(player.getZ() - pos.getZ()) < distance && !player.isSpectator()) {
                    player.addStatusEffect(new StatusEffectInstance(primaryEffect, duration, amplifier, true, true));

                    if (beaconLevel >= 4 && primaryEffect != secondaryEffect && secondaryEffect != null) {
                        player.addStatusEffect(new StatusEffectInstance(secondaryEffect, duration, 0, true, true));
                    }
                }
            }
        }
    }
}
