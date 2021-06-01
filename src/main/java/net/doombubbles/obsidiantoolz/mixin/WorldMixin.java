package net.doombubbles.obsidiantoolz.mixin;

import net.doombubbles.obsidiantoolz.Items.GeodeStuff;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ServerWorld.class)
public class WorldMixin {

    @Shadow
    @Final
    List<ServerPlayerEntity> players;

    @Inject(at = @At(value = "HEAD"), method = "tickTime")
    protected void spelunkerCubes(CallbackInfo callbackInfo) {
        //GeodeStuff.tickCubes();
    }
}
