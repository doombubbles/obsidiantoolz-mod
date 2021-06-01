package net.doombubbles.obsidiantoolz.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {
    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile profile) {
        super(world, pos, yaw, profile);
    }

    @Inject(at = @At(value = "FIELD", target = "Lnet/minecraft/server/network/ServerPlayerEntity;enchantmentTableSeed:I"), method = "copyFrom")
    public void myInjection(ServerPlayerEntity serverPlayerEntity_1, boolean boolean_1, CallbackInfo ci)
    {
        this.getInventory().clone(serverPlayerEntity_1.getInventory()); //TODO why does this happen?
    }

}
