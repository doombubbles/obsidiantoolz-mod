package net.doombubbles.obsidiantoolz.mixin;

import net.doombubbles.obsidiantoolz.ObsidianToolzMod;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SheepEntity.class)
public class SheepEntityMixin extends LivingEntityMixin {
    public SheepEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    /*
    @Redirect(method = "interactMob", at=@At("Lnet/minecraft/entity/passive/SheepEntity;dropItems()V"))
    public static void redirectDropItems(SheepEntity sheepEntity, PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        int magnetLevel = EnchantmentHelper.getLevel(ObsidianToolzMod.MAGNETIC, itemStack);
        if (magnetLevel < 1) {
            sheepEntity.dropItems();
        } else {
            sheepEntity.setSheared(true);
            int i = 1 + sheepEntity.getRandom().nextInt(3);

            for(int j = 0; j < i; ++j) {
                ItemEntity itemEntity = sheepEntity.dropItem((ItemConvertible)DROPS.get(this.getColor()), 1);
                if (itemEntity != null) {
                    itemEntity.setVelocity(itemEntity.getVelocity().add((double)((this.random.nextFloat() - this.random.nextFloat()) * 0.1F), (double)(this.random.nextFloat() * 0.05F), (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.1F)));
                }
            }
        }
    }
    */

    @Inject(method = "interactMob", at=@At("HEAD"))
    public void interactMob(PlayerEntity player, Hand hand, CallbackInfoReturnable<Boolean> cI) {
        ItemStack itemStack = player.getStackInHand(hand);
        int level = EnchantmentHelper.getLevel(ObsidianToolzMod.MUGGING, itemStack);
        if (level > 0) {
            this.mugger = player;
        }
    }

    @Inject(method = "interactMob", at=@At("TAIL"))
    public void interactMob2(PlayerEntity player, Hand hand, CallbackInfoReturnable<Boolean> cI) {
        this.mugger = null;
    }
}
