package net.doombubbles.obsidiantoolz.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class AbstractFurnaceBlockEntityMixin extends BlockEntity {

    @Shadow protected DefaultedList<ItemStack> inventory;

    @Shadow public abstract ItemStack getStack(int slot);

    @Shadow
    int burnTime;
    @Final
    @Shadow
    private RecipeType<? extends AbstractCookingRecipe> recipeType;

    public AbstractFurnaceBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    boolean beingInfinitiedRightNow = false;

    @Inject(at=@At("HEAD"), method= "tick(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/block/entity/AbstractFurnaceBlockEntity;)V")
    private static void preTick(World world, BlockPos pos, BlockState state, AbstractFurnaceBlockEntity blockEntity, CallbackInfo ci) {
        AbstractFurnaceBlockEntityMixin furnace = (AbstractFurnaceBlockEntityMixin)(Object)blockEntity;
        ItemStack itemStack = blockEntity.getStack(1);
        boolean yup = itemStack.getItem() == Items.LAVA_BUCKET && EnchantmentHelper.getLevel(Enchantments.INFINITY, itemStack) > 0;
        if (furnace.beingInfinitiedRightNow && !yup) {
            furnace.burnTime = 0;
            furnace.beingInfinitiedRightNow = false;
            return;
        }
        if (yup && !furnace.getStack(0).isEmpty()) {
            int i = blockEntity.getMaxCountPerStack();
            Recipe<?> recipe = furnace.world.getRecipeManager().getFirstMatch(furnace.recipeType, (Inventory) furnace, furnace.world).orElse( null);
            if (furnace.burnTime <= 1 && canAcceptRecipeOutput(recipe, furnace.inventory, i) && furnace.getFuelTime(itemStack) > 0) {
                itemStack.increment(1);
                furnace.beingInfinitiedRightNow = true;
            }
        }
    }

    @Shadow
    private static boolean canAcceptRecipeOutput(@Nullable Recipe<?> recipe, DefaultedList<ItemStack> slots, int count) {
        return true;
    }

    @Shadow
    protected int getFuelTime(ItemStack itemStack) {
        return 0;
    }

    @Shadow
    private boolean isBurning() {
        return false;
    }
}
