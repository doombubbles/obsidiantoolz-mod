package net.doombubbles.obsidiantoolz.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterials;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PiglinBrain.class)
public class PiglinBrainMixin {

    @Inject(method = "wearsGoldArmor", at=@At("HEAD"), cancellable = true)
    private static void wearsGoldArmor(LivingEntity entity, CallbackInfoReturnable<Boolean> cI) {
        if (entity instanceof ServerPlayerEntity) {
            for (ItemStack armorItem : entity.getArmorItems()) {
                if (armorItem.getItem() instanceof ArmorItem && ((ArmorItem)armorItem.getItem()).getMaterial() == ArmorMaterials.NETHERITE) {
                    cI.setReturnValue(true);
                    cI.cancel();
                }
            }
        }
    }

}
