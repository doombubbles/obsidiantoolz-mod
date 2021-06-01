package net.doombubbles.obsidiantoolz.mixin;

import net.doombubbles.obsidiantoolz.Enchantments.InflationEnchantment;
import net.doombubbles.obsidiantoolz.Enchantments.SmeltTouchEnchantment;
import net.doombubbles.obsidiantoolz.Items.NetheriteAnvil;
import net.doombubbles.obsidiantoolz.ObsidianToolzMod;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.context.LootContext;
import net.minecraft.nbt.NbtInt;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(AbstractBlock.AbstractBlockState.class)
public class AbstractBlockStateMixin {

    @Shadow
    protected BlockState asBlockState() { return null;}

    @Inject(at = @At("HEAD"),method = "getDroppedStacks", cancellable = true)
    public void doSmeltTouch(LootContext.Builder lootBuilder, CallbackInfoReturnable<List<ItemStack>> ci)
    {
        SmeltTouchEnchantment.doLogic(this.asBlockState(), lootBuilder, this.asBlockState().getBlock(), ci);
    }

    @Inject(at = @At("TAIL"),method = "getDroppedStacks", cancellable = true)
    public void doAnvil(LootContext.Builder lootBuilder, CallbackInfoReturnable<List<ItemStack>> ci)
    {
        BlockState state = this.asBlockState();
        if (NetheriteAnvil.blockIs(state)) {
            List<ItemStack> stacks = ci.getReturnValue();
            for (ItemStack stack : stacks) {
                String name = "Netherite Anvil";
                if (stack.getItem() == Items.CHIPPED_ANVIL) {
                    name = "Chipped Netherite Anvil";
                } else if (stack.getItem() == Items.DAMAGED_ANVIL) {
                    name = "Damaged Netherite Anvil";
                }
                stack.setCustomName(ObsidianToolzMod.asName(name));
                stack.putSubTag("CustomModelData", NbtInt.of(1));
            }
            ci.setReturnValue(stacks);
        }
        if (state.getBlock() == Blocks.EMERALD_ORE) {
            InflationEnchantment.doInflation(state, lootBuilder, ci);
        }
    }
}
