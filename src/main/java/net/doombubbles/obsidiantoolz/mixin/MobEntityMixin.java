package net.doombubbles.obsidiantoolz.mixin;

import net.doombubbles.obsidiantoolz.Items.BugNet;
import net.doombubbles.obsidiantoolz.ObsidianToolzMod;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MobEntity.class)
public class MobEntityMixin extends LivingEntityMixin {

    public MobEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "interact", at=@At("HEAD"), cancellable = true)
    public void onInteract(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        ItemStack itemStack = player.getStackInHand(hand);
        PlayerEntity overrode = mugger;
        if (EnchantmentHelper.getLevel(ObsidianToolzMod.MUGGING, itemStack) > 0) {
            mugger = player;
        }
        if (BugNet.is(itemStack) && BugNet.isValid((LivingEntity)(Object)this, itemStack) && BugNet.onInteract((LivingEntity)(Object)this, player, itemStack, hand)) {
            cir.cancel();
            cir.setReturnValue(ActionResult.success(this.world.isClient));
        }
        mugger = overrode;
    }
}
