package net.doombubbles.obsidiantoolz.Items;

import net.minecraft.block.AnvilBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.Direction;

public class NetheriteAnvil {

    public static boolean itemIs(ItemStack stack) {
        return stack != null && (stack.getItem() == Items.ANVIL || stack.getItem() == Items.CHIPPED_ANVIL || stack.getItem() == Items.DAMAGED_ANVIL)
                && stack.getTag() != null && stack.getTag().get("CustomModelData") != null && stack.getTag().getInt("CustomModelData") == 1;
    }

    public static boolean blockIs(BlockState state) {
        return (state.getBlock() == Blocks.ANVIL || state.getBlock() == Blocks.CHIPPED_ANVIL || state.getBlock() == Blocks.DAMAGED_ANVIL)
                && (state.get(AnvilBlock.FACING) == Direction.SOUTH || state.get(AnvilBlock.FACING) == Direction.EAST);
    }
}
