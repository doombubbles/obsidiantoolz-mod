package net.doombubbles.obsidiantoolz.Enchantments;

import net.doombubbles.obsidiantoolz.ObsidianToolzEnchantment;
import net.doombubbles.obsidiantoolz.ObsidianToolzMod;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ShearsItem;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Queue;

public class VeinMinerEnchantment extends ObsidianToolzEnchantment {

    public VeinMinerEnchantment(Rarity rarity, EnchantmentTarget type, EquipmentSlot[] slotTypes) {
        super(rarity, type, slotTypes);
    }

    @Override
    public int getMinPower(int level) {
        return 30;
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public String getRealName(int level) {
        return "Veinminer" + (level > 1 ? " " + toRoman(level) : "");
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return super.isAcceptableItem(stack) && stack.getItem() instanceof PickaxeItem;
    }

    public static void tryVeinMining(World world, BlockPos blockPos, BlockState blockState, PlayerEntity pe, ItemStack tool, Inventory inventory) {
        if (world.isClient) return;

        if (tool == null && pe != null) {
            tool = pe.getMainHandStack();
        }
        if (tool == ItemStack.EMPTY || !tool.hasEnchantments()) {
            return;
        }
        int VeinMinerLvl = EnchantmentHelper.getLevel(ObsidianToolzMod.VEINMINER, tool);
        int TimberLvl = EnchantmentHelper.getLevel(ObsidianToolzMod.TIMBER, tool);

        Block currentType = blockState.getBlock();
        int brokenBlocks = 0;

        if (TimberLvl > 0) {
            if (isValidBlockForAxe(blockState) && tool.getItem() instanceof AxeItem) {
                brokenBlocks = doVeinMiner(blockState, world, blockPos, 99, pe, tool, TimberLvl, inventory);
            }
            if (isValidBlockForShears(blockState) && tool.getItem() instanceof ShearsItem) {
                brokenBlocks = doVeinMiner(blockState, world, blockPos, 99, pe, tool, TimberLvl, inventory);
            }
        } else if (VeinMinerLvl > 0) {
            if (isValidBlockForPick(blockState)) {
                brokenBlocks = doVeinMiner(blockState, world, blockPos, 99, pe, tool, VeinMinerLvl, inventory);
            }
        }
        if (pe != null) {
            pe.increaseStat(Stats.MINED.getOrCreateStat(currentType), brokenBlocks);
        }
    }

    private static boolean isValidBlockForPick(BlockState block) {
        switch (Registry.BLOCK.getId(block.getBlock()).toString()) {
            case "minecraft:copper_ore":
            case "minecraft:gold_ore":
            case "minecraft:iron_ore":
            case "minecraft:coal_ore":
            case "minecraft:lapis_ore":
            case "minecraft:diamond_ore":
            case "minecraft:redstone_ore":
            case "minecraft:emerald_ore":
            case "minecraft:deepslate_copper_ore":
            case "minecraft:deepslate_gold_ore":
            case "minecraft:deepslate_iron_ore":
            case "minecraft:deepslate_coal_ore":
            case "minecraft:deepslate_lapis_ore":
            case "minecraft:deepslate_diamond_ore":
            case "minecraft:deepslate_redstone_ore":
            case "minecraft:deepslate_emerald_ore":
            case "minecraft:nether_quartz_ore":
            case "minecraft:nether_gold_ore":
            case "minecraft:ancient_debris":
            case "minecraft:glowstone":
                return true;
            default:
                return false;
        }
    }

    private static boolean isValidBlockForAxe(BlockState block) {
        String path = Registry.BLOCK.getId(block.getBlock()).getPath();
        if (isValidBlockForShears(block)) {
            return true;
        }
        if (path.endsWith("_log") || path.endsWith("_stem"))// yes this is bad
        {
            if (!path.contains("stripped")) {
                return true;
            }
        }
        return false;
    }

    private static boolean isValidBlockForShears(BlockState block) {
        String path = Registry.BLOCK.getId(block.getBlock()).getPath();
        if (path.endsWith("_leaves") || path.endsWith("_wart_block"))// yes this is bad
        {
            return true;
        }
        return false;

    }

    public static ArrayList<BlockPos> getSameBlocks(World w, BlockPos source, Block type, int level, ItemStack tool) {
        ArrayList<BlockPos> t = new ArrayList<>();
        for (BlockPos pos : new BlockPos[]{source.up().south(), source.up().east(), source.up().west(), source.up().north(),
                source.up(), source.down(), source.north(), source.east(), source.south(), source.west(),
                source.down().north(), source.down().east(), source.down().west(), source.down().south()}) {
            Block block = w.getBlockState(pos).getBlock()
                    ;/*
            String path1 = Registry.BLOCK.getId(block).getPath();
            String path2 = Registry.BLOCK.getId(type).getPath();
            */
            if (block == type/* || path1.contains(path2) || path2.contains(path1)*/) {
                t.add(pos);
            } else if (level > 1) {
                if (tool.getItem() instanceof PickaxeItem && isValidBlockForPick(w.getBlockState(pos))) {
                    t.add(pos);
                } else if (tool.getItem() instanceof AxeItem && isValidBlockForAxe(w.getBlockState(pos))) {
                    t.add(pos);
                } else if (tool.getItem() instanceof ShearsItem && isValidBlockForShears(w.getBlockState(pos))) {
                    t.add(pos);
                }
            }
        }

        return t;
    }

    private static int doVeinMiner(BlockState blockState, World world, BlockPos blockPos, int maxBlocks, PlayerEntity pe, ItemStack tool, int level, Inventory inventory) {
        int blocksBroken = 0;
        Queue<BlockPos> toBreak = new UniqueQueue<>();//new LinkedList<BlockPos>();
        toBreak.addAll(getSameBlocks(world, blockPos, blockState.getBlock(), level, tool));
        while (!toBreak.isEmpty() && blocksBroken <= (maxBlocks)) {
            BlockPos currPos = toBreak.remove();
            if (toBreak.size() < 50)
                toBreak.addAll(getSameBlocks(world, currPos, blockState.getBlock(), level, tool));

            // int maxDistance = ModInit.config.MaxDistanceFromPlayer;
            // boolean tofar = Math.abs(currPos.getX() - pe.getX()) < maxDistance && Math.abs(currPos.getZ() - pe.getZ()) < maxDistance;

            if (inventory != null && EnchantmentHelper.getLevel(ObsidianToolzMod.MAGNETIC, tool) > 0) {
                LootContext.Builder builder = new LootContext.Builder((ServerWorld) world)
                        .random(world.random)
                        .parameter(LootContextParameters.ORIGIN, Vec3d.ofCenter(blockPos))
                        .parameter(LootContextParameters.TOOL, tool);
                blockState.getDroppedStacks(builder).forEach(itemStack -> {
                    itemStack = HopperBlockEntity.transfer(null, inventory, itemStack, null);
                    if (!itemStack.isEmpty()) {
                        Block.dropStack(world, blockPos, tool);
                    }
                });
                blockState.onStacksDropped((ServerWorld) world, blockPos, ItemStack.EMPTY);
            } else {
                Block.dropStacks(world.getBlockState(currPos), world, currPos, null, pe, tool);
            }
            world.breakBlock(currPos, false);

            if (pe != null) {
                tool.damage(1, pe, (playerEntity_1) -> {
                    playerEntity_1.sendToolBreakStatus(pe.getActiveHand());
                });
            }
            if (tool.getMaxDamage() - tool.getDamage() < 10) {
                break;
            }
            blocksBroken++;
            world.setBlockState(currPos, Blocks.AIR.getDefaultState());
        }

        if (pe != null) {
            pe.addExhaustion(0.005F * blocksBroken);
        }

        return blocksBroken;
    }


    public static class UniqueQueue<T> extends PriorityQueue<T> {

        private static final long serialVersionUID = 2163057383757900549L;

        @Override
        public boolean offer(T e) {

            if (this.contains(e)) {
                return false;
            }
            return super.offer(e);
        }
    }
}
