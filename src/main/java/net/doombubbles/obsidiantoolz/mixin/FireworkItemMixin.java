package net.doombubbles.obsidiantoolz.mixin;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.FireworkItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FireworkItem.class)
public class FireworkItemMixin {

    @Inject(at = @At("HEAD"), method = "use")
    public void use(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cI) {
        if (user.isFallFlying()) {
            ItemStack handStack = user.getStackInHand(hand);
            boolean dewIt = false;
            for (ItemStack itemStack : user.getArmorItems()) {
                if (itemStack.getItem() instanceof ElytraItem && EnchantmentHelper.getLevel(Enchantments.INFINITY, itemStack) > 0) {
                    dewIt = true;
                }
            }
            if (!world.isClient && !user.isCreative() && dewIt) {
                handStack.increment(1);
            }
        }
    }
}
