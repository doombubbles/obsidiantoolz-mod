package net.doombubbles.obsidiantoolz.mixin;
import net.doombubbles.obsidiantoolz.ObsidianToolzMod;
import net.minecraft.block.BlockState;
import net.minecraft.block.SpawnerBlock;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SpawnerBlock.class)
public class SpawnerBlockMixin {

    @Inject(method = "onStacksDropped", at = @At(value = "HEAD", target = "Lnet/minecraft/block/Block;dropExperience(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;I)V"), cancellable = true)
    private void onStacksDropper(BlockState state, ServerWorld world, BlockPos pos, ItemStack stack, CallbackInfo ci) {
        if (EnchantmentHelper.getLevel(ObsidianToolzMod.SPAWNER_TOUCH, stack) > 0 || EnchantmentHelper.getLevel(Enchantments.SILK_TOUCH, stack) > 1) {
            ci.cancel();
        }
    }
}