package net.doombubbles.obsidiantoolz.mixin;

import net.doombubbles.obsidiantoolz.ObsidianToolzMod;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneOreBlock;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RedstoneOreBlock.class)
public class RedstoneOreBlockMixin extends Block {

    public RedstoneOreBlockMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "onStacksDropped", at = @At("TAIL"))
    private void doExpertise(BlockState state, ServerWorld world, BlockPos pos, ItemStack stack, CallbackInfo ci) {
        if (EnchantmentHelper.getLevel(Enchantments.SILK_TOUCH, stack) == 0) {
            int expLevel = EnchantmentHelper.getLevel(ObsidianToolzMod.EXPERTISE, stack);
            for (int i = 0; i < expLevel; i++) {
                int j = 1 + world.random.nextInt(5);
                this.dropExperience(world, pos, j);
            }
        }
    }



}