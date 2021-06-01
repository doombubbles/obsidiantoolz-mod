package net.doombubbles.obsidiantoolz.mixin;

import net.doombubbles.obsidiantoolz.ObsidianToolzMod;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FireworkRocketEntity.class)
public class FireworksEntityMixin {


    @Shadow
    private int lifeTime;

    @Inject(at = @At("TAIL"), method = "<init>(Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/LivingEntity;)V")
    public void FireworkEntity(World world, ItemStack item, LivingEntity shooter, CallbackInfo cI) {
        int level = EnchantmentHelper.getEquipmentLevel(ObsidianToolzMod.BOOSTED, shooter);
        this.lifeTime += level * 10;
    }
}
