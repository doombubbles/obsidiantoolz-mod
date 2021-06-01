package net.doombubbles.obsidiantoolz.mixin;

import net.doombubbles.obsidiantoolz.Items.ObsidianStuff;
import net.doombubbles.obsidiantoolz.Items.RocketBoots;
import net.doombubbles.obsidiantoolz.LoreHelper;
import net.doombubbles.obsidiantoolz.ObsidianToolzEnchantment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(ItemStack.class)
public class ItemStackMixin {
	@Inject(at = @At("TAIL"), method = "addEnchantment")
	private void addEnchantment(Enchantment enchantment, int level, CallbackInfo info) {
		ItemStack itemStack = (ItemStack) (Object) this;
		if (enchantment instanceof ObsidianToolzEnchantment) {
			LoreHelper.updateLore(itemStack);
		}
	}

	@Inject(at = @At("HEAD"), method = "isSuitableFor", cancellable = true)
	private void isEffectiveOn(BlockState blockState, CallbackInfoReturnable<Boolean> cI) {
		ItemStack itemStack = (ItemStack)(Object)this;
		Item item = itemStack.getItem();
		if (blockState.getBlock() == Blocks.NETHER_GOLD_ORE) {
			cI.setReturnValue(item == Items.IRON_PICKAXE || item == Items.GOLDEN_PICKAXE || item == Items.DIAMOND_PICKAXE || item == Items.NETHERITE_PICKAXE);
			cI.cancel();
		}
		if (blockState.getBlock() == Blocks.NETHER_QUARTZ_ORE) {
			cI.setReturnValue(item == Items.STONE_PICKAXE || item == Items.IRON_PICKAXE || item == Items.GOLDEN_PICKAXE || item == Items.DIAMOND_PICKAXE || item == Items.NETHERITE_PICKAXE);
			cI.cancel();
		}
	}

	@Inject(at = @At("HEAD"), method = "damage(ILjava/util/Random;Lnet/minecraft/server/network/ServerPlayerEntity;)Z", cancellable = true)
	public void damage(int amount, Random random, @Nullable ServerPlayerEntity player, CallbackInfoReturnable<Boolean> cI) {
		ItemStack itemStack = (ItemStack)(Object)this;
		if (RocketBoots.is(itemStack) && RocketBoots.getRocketBoots(player).getMaxDamage() - RocketBoots.getRocketBoots(player).getDamage() - amount < 1) {
			cI.setReturnValue(false);
			cI.cancel();
		}
		if (ObsidianStuff.is(itemStack) && (random.nextInt(3) > 0 || ((itemStack.getItem() == Items.SHEARS || itemStack.getItem() == Items.SHIELD) && random.nextInt(3) > 0))) {
			cI.setReturnValue(false);
			cI.cancel();
		}
	}

	/*
	@Inject(at = @At("HEAD"), method = "getMaxDamage", cancellable = true)
	private void overrideDurability(CallbackInfoReturnable<Integer> callbackInfoReturnable) {
		ItemStack itemStack = (ItemStack) (Object) this;
		if (itemStack.getName().asString().contains("Obsidian Pickaxe")) {
			System.out.println("YUP");
			callbackInfoReturnable.setReturnValue(9747);
			callbackInfoReturnable.cancel();
		}
	}
	*/

	/*
	@Inject(at = @At("RETURN"), method = "getMiningSpeedMultiplier", cancellable = true)
	private void overrideSpeed(BlockState state, CallbackInfoReturnable<Float> callbackInfoReturnable) {
		ItemStack itemStack = (ItemStack) (Object) this;
		Float returnValue = callbackInfoReturnable.getReturnValue();
		if (ObsidianToolzMod.isObsidian(itemStack)) {
			callbackInfoReturnable.setReturnValue(returnValue * 5);
		}
	}
	*/
}
