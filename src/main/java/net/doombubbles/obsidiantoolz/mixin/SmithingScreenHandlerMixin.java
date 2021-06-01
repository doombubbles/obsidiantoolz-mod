package net.doombubbles.obsidiantoolz.mixin;

import net.doombubbles.obsidiantoolz.ObsidianToolzMod;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtInt;
import net.minecraft.recipe.SmithingRecipe;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.SmithingScreenHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.Map;

@Mixin(SmithingScreenHandler.class)
public class SmithingScreenHandlerMixin /*extends ForgingScreenHandler*/ {

    /*
    @Final
    @Shadow
    private List<SmithingRecipe> field_25668;

    public SmithingScreenHandlerMixin(ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(type, syncId, playerInventory, context);
    }

    @Override
    public boolean canTakeOutput(PlayerEntity player, boolean present) {
        return (RECIPES.containsKey(this.input.getStack(0).getItem()) && this.input.getStack(1).getItem() == Items.NETHERITE_INGOT)
                || customCombine(this.input.getStack(0), this.input.getStack(1)) != null;
    }

    @Shadow
    protected void onTakeOutput(PlayerEntity player, ItemStack stack) {

    }

    @Shadow
    public boolean canUse(BlockState state) {
        return true;
    }

    @Override
    public void updateResult() {
        ItemStack itemStack = this.input.getStack(0);
        ItemStack itemStack2 = this.input.getStack(1);
        Item item = (Item)RECIPES.get(itemStack.getItem());
        if (itemStack2.getItem() == Items.NETHERITE_INGOT && item != null) {
            ItemStack itemStack3 = new ItemStack(item);
            NbtCompound NbtCompound = itemStack.getTag();
            itemStack3.setTag(NbtCompound != null ? NbtCompound.copy() : null);
            this.output.setStack(0, itemStack3);
        } else {
            this.output.setStack(0, customCombine(itemStack, itemStack2));
        }
    }

    public ItemStack customCombine(ItemStack itemStack1, ItemStack itemStack2) {
        if (itemStack1.getItem() == Items.NETHERITE_SWORD && itemStack2.getItem() == Items.BLAZE_ROD && itemStack1.getSubTag("CustomModelData") == null) {
            ItemStack itemStack3 = itemStack1.copy();
            itemStack3.putSubTag("CustomModelData", NbtInt.of(1));
            if (!itemStack3.getTag().contains("display") || !itemStack3.getSubTag("display").contains("Name")) {
                itemStack3.setCustomName(ObsidianToolzMod.asName("Fiery Greatsword"));
            }
            if (EnchantmentHelper.getLevel(Enchantments.FIRE_ASPECT, itemStack3) < 1) {
                itemStack3.addEnchantment(Enchantments.FIRE_ASPECT, 2);
            }
            return itemStack3;
        }
        if (itemStack1.getItem() == Items.NETHERITE_PICKAXE && itemStack2.getItem() == Items.BLAZE_ROD && itemStack1.getSubTag("CustomModelData") == null) {
            ItemStack itemStack3 = itemStack1.copy();
            itemStack3.putSubTag("CustomModelData", NbtInt.of(1));
            if (!itemStack3.getTag().contains("display") || !itemStack3.getSubTag("display").contains("Name")) {
                itemStack3.setCustomName(ObsidianToolzMod.asName("Molten Pickaxe"));
            }
            if (EnchantmentHelper.getLevel(ObsidianToolzMod.SMELT_TOUCH, itemStack3) < 1) {
                itemStack3.addEnchantment(ObsidianToolzMod.SMELT_TOUCH, 1);
            }
            return itemStack3;
        }

        return ItemStack.EMPTY;
    }*/
}
