package net.doombubbles.obsidiantoolz.mixin;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Map;

@Mixin(ExperienceOrbEntity.class)
public class ExperienceOrbEntityMixin {

    @Shadow
    private int amount;

    @Inject(method = "repairPlayerGears", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;setDamage(I)V"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    public void onPlayerCollision(PlayerEntity player, int amount, CallbackInfoReturnable<Integer> cir, Map.Entry entry, ItemStack itemStack, int i) {
        if (EnchantmentHelper.getLevel(Enchantments.MENDING, itemStack) > 1) {
            this.amount += this.getMendingRepairCost(i);
        }
    }

    @Shadow
    private int getMendingRepairCost(int i) {
        return 0;
    }


}
