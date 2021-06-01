package net.doombubbles.obsidiantoolz.mixin;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TridentEntity.class)
public class TridentEntityMixin {
    @Shadow
    private ItemStack tridentStack;

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;isThundering()Z"), method = "onEntityHit")
    private boolean onEntityHit(World world) {
        if (EnchantmentHelper.getLevel(Enchantments.CHANNELING, this.tridentStack) > 1) {
            return true;
        }
        return world.isThundering();
    }
}
