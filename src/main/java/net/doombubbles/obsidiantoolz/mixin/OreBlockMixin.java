package net.doombubbles.obsidiantoolz.mixin;

import net.doombubbles.obsidiantoolz.ObsidianToolzMod;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.OreBlock;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(OreBlock.class)
public class OreBlockMixin extends Block {
    public OreBlockMixin(Settings settings) {
        super(settings);
    }

    @Shadow
    @Final
    private UniformIntProvider experienceDropped;

    @Inject(at = @At("TAIL"), method = "onStacksDropped")
    public void doGrinding(BlockState state, ServerWorld world, BlockPos pos, ItemStack stack, CallbackInfo ci) {
        if (EnchantmentHelper.getLevel(Enchantments.SILK_TOUCH, stack) == 0) {
            int expLevel = EnchantmentHelper.getLevel(ObsidianToolzMod.EXPERTISE, stack);
            for (int i = 0; i < expLevel; i++) {
                int count = experienceDropped.get(world.random);
                if (count > 0) {
                    this.dropExperience(world, pos, count);
                }
            }
        }
    }
}
