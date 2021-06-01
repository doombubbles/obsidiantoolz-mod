package net.doombubbles.obsidiantoolz.Enchantments;

import net.doombubbles.obsidiantoolz.ObsidianToolzEnchantment;
import net.doombubbles.obsidiantoolz.ObsidianToolzMod;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmeltingRecipe;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;

public class SmeltTouchEnchantment extends ObsidianToolzEnchantment {

    public SmeltTouchEnchantment(Rarity rarity, EnchantmentTarget type, EquipmentSlot[] slotTypes) {
        super(rarity, type, slotTypes);
    }

    @Override
    public int getMinPower(int level) {
        return 15;
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    protected boolean canAccept(Enchantment other) {
        return super.canAccept(other) && other != Enchantments.SILK_TOUCH;
    }

    @Override
    public String getRealName(int level) {
        return "Smelt Touch" + (level > 1 ? " " + toRoman(level) : "");
    }

    public static void doLogic(BlockState blockState, LootContext.Builder lootBuilder, Block block, CallbackInfoReturnable<List<ItemStack>> ci)
    {
        ItemStack tool = lootBuilder.get(LootContextParameters.TOOL);
        Identifier loottableId = block.getLootTableId();
        int level = EnchantmentHelper.getLevel(ObsidianToolzMod.SMELT_TOUCH, tool);
        if(level > 0 && loottableId != LootTables.EMPTY)
        {
            Entity player = lootBuilder.get(LootContextParameters.THIS_ENTITY);
            lootBuilder.parameter(LootContextParameters.BLOCK_STATE, blockState);
            ServerWorld serverworld = lootBuilder.getWorld();
            LootTable loottTable = serverworld.getServer().getLootManager().getTable(loottableId);
            List<ItemStack> stacks = loottTable.generateLoot(lootBuilder.build(LootContextTypes.BLOCK));
            RecipeManager rm = player.world.getRecipeManager();
            Inventory basicInv = new SimpleInventory();

            ItemStack itemToBeChecked = ItemStack.EMPTY;
            Optional<SmeltingRecipe> smeltingResult;
            for(int stacksIndex = 0; stacksIndex < stacks.size(); stacksIndex++)
            {
                itemToBeChecked = stacks.get(stacksIndex);
                basicInv = new SimpleInventory(itemToBeChecked);
                smeltingResult = rm.getFirstMatch(RecipeType.SMELTING, basicInv, player.world);
                if(smeltingResult.isPresent())
                {
                    int count = itemToBeChecked.getCount();
                    if (isValidForFortune(block)) {
                        int fortuneLevel = EnchantmentHelper.getLevel(Enchantments.FORTUNE, tool);
                        if (fortuneLevel > 0) {
                            int i = player.world.random.nextInt(fortuneLevel + 2) - 1;
                            if (i < 0) {
                                i = 0;
                            }
                            count *= (i + 1);
                        }
                        if (level > 1) {
                            int expertiseLevel = EnchantmentHelper.getLevel(ObsidianToolzMod.EXPERTISE, tool);
                            int xp = Math.round(smeltingResult.get().getExperience() * (expertiseLevel + 1));

                            serverworld.spawnEntity(new ExperienceOrbEntity(serverworld, player.getX(), player.getY(), player.getZ(), xp));
                        }
                    }
                    stacks.set(stacksIndex, new ItemStack(smeltingResult.get().getOutput().getItem(), count));
                    //serverworld.spawnEntity(new ExperienceOrbEntity(serverworld, player.getX(), player.getY(), player.getZ(), (int)smeltingResult.get().getExperience() * itemToBeChecked.getCount()));
                    //serverworld.playSound((PlayerEntity)null, blockState.pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.1f, 0.1f);
                    ci.setReturnValue(stacks);
                }
            }
        }
    }

    private static boolean isValidForFortune(Block block)
    {
        switch (Registry.BLOCK.getId(block).toString()) {
            case "minecraft:ancient_debris":
                return true;
            default:
                return false;
        }
    }
}
