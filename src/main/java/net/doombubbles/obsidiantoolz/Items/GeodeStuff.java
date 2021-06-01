package net.doombubbles.obsidiantoolz.Items;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MagmaCubeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtInt;
import net.minecraft.network.Packet;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GeodeStuff {
    public static Team teamForBlock(Block block, World world) {
        if (block == Blocks.GOLD_ORE || block == Blocks.NETHER_GOLD_ORE) {
            return getOrCreateTeam(world, "gold");
        }
        if (block == Blocks.DIAMOND_ORE) {
            return getOrCreateTeam(world,"diamond");
        }
        if (block == Blocks.IRON_ORE) {
            return getOrCreateTeam(world, "iron");
        }
        if (block == Blocks.COAL_ORE) {
            return getOrCreateTeam(world, "coal");
        }
        if (block == Blocks.NETHER_QUARTZ_ORE) {
            return getOrCreateTeam(world, "quartz");
        }
        if (block == Blocks.LAPIS_ORE) {
            return getOrCreateTeam(world, "lapis");
        }
        if (block == Blocks.REDSTONE_ORE) {
            return getOrCreateTeam(world, "redstone");
        }
        if (block == Blocks.EMERALD_ORE) {
            return getOrCreateTeam(world, "emerald");
        }
        if (block == Blocks.ANCIENT_DEBRIS) {
            return getOrCreateTeam(world, "netherite");
        }
        return null;
    }

    public static Team getOrCreateTeam(World world, String name) {
        Team team = world.getScoreboard().getTeam(name);
        if (team != null) {
            return team;
        }
        team = world.getScoreboard().addTeam(name);
        Formatting formatting = null;
        switch (name) {
            case "gold":
                formatting = Formatting.YELLOW;
                break;
            case "diamond":
                formatting = Formatting.AQUA;
                break;
            case "iron":
                formatting = Formatting.GOLD;
                break;
            case "coal":
                formatting = Formatting.BLACK;
                break;
            case "quartz":
                formatting = Formatting.WHITE;
                break;
            case "lapis":
                formatting = Formatting.DARK_BLUE;
                break;
            case "redstone":
                formatting = Formatting.DARK_RED;
                break;
            case "emerald":
                formatting = Formatting.GREEN;
                break;
            case "netherite":
                formatting = Formatting.DARK_GRAY;
                break;
            default:
                break;
        }
        if (formatting != null && team != null) {
            team.setColor(formatting);
        }

        return team;
    }

    public static Map<World, Map<BlockPos, MagmaCubeEntity>> spelunkerCubes = new HashMap<>();

    public static boolean isGeode(ItemStack stack) {
        return stack.getItem() == Items.CLOCK && stack.getTag() != null
                && stack.getTag().get("CustomModelData") != null && stack.getTag().getInt("CustomModelData") == 1;
    }

    public static boolean isGeodeHelmet(ItemStack stack) {
        return stack.getItem() == Items.LEATHER_HELMET && stack.getTag() != null
                && stack.getTag().get("CustomModelData") != null && stack.getTag().getInt("CustomModelData") == 1;
    }

    public static boolean hasGeodeHelmet(PlayerEntity serverPlayerEntity) {
        return getGeodeHelmet(serverPlayerEntity) != ItemStack.EMPTY;
    }

    public static ItemStack getGeodeHelmet(PlayerEntity serverPlayerEntity) {
        for (ItemStack armorItem : serverPlayerEntity.getArmorItems()) {
            if (isGeodeHelmet(armorItem)) {
                return armorItem;
            }
        }
        return ItemStack.EMPTY;
    }

    public static boolean summonSpelunkerCube(World world, BlockPos blockPos) {
        Block block = world.getBlockState(blockPos).getBlock();
        MagmaCubeEntity cube = new MagmaCubeEntity(EntityType.MAGMA_CUBE, world);
        //cube.initialize(world, world.getLocalDifficulty(blockPos), SpawnType.TRIGGERED, null, null);
        //cube.setPos(0, 0, 0);
        //cube.setPos(blockPos.getX() + .5, 0, blockPos.getZ() + .5);
        if (false) { //TODO the nether thing was here
        }
        //cube.teleporting = true; TODO needs teleporting?
        if (world.spawnEntity(cube)) {
            //cube.teleporting = false;
            cube.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, 999999, 0, true, false));
            NbtCompound NbtCompound = new NbtCompound();
            NbtCompound.put("Size", NbtInt.of(1));
            cube.readCustomDataFromNbt(NbtCompound);
            cube.setNoGravity(true);
            cube.setAiDisabled(true);
            cube.setSilent(true);
            cube.setInvulnerable(true);
            cube.setYaw(0);
            cube.setPitch(0);
            cube.setBodyYaw(0);
            cube.setHeadYaw(0);
            cube.refreshPositionAfterTeleport(blockPos.getX() + .5, blockPos.getY(), blockPos.getZ() + .5);
            cube.limbAngle = 0;
            cube.setInvisible(true);
            cube.addScoreboardTag("spelunker");
            Team team = teamForBlock(block, world);
            if (team != null) {
                world.getScoreboard().addPlayerToTeam(cube.getUuidAsString(), team);
            }

            if (spelunkerCubes.get(world).containsKey(blockPos)) {
                spelunkerCubes.get(world).get(blockPos).setGlowing(false);
                spelunkerCubes.get(world).get(blockPos).remove(Entity.RemovalReason.DISCARDED);
            }
            spelunkerCubes.get(world).put(blockPos, cube);
            return true;
        }
        return false;
    }

    public static boolean shouldCubeExist(World world, BlockPos blockPos) {
        Set<PlayerEntity> closeEnoughPlayers = new HashSet<>();
        boolean nearbySpelunker = false;

        BlockState blockState = world.getBlockState(blockPos);
        if (blockState.isAir()) {
            return false;
        }

        for (PlayerEntity player : world.getPlayers()) {
            if (Math.abs(player.getBlockPos().getX() - blockPos.getX()) <= 5 && Math.abs(player.getBlockPos().getY() - blockPos.getY()) <= 5
                    && Math.abs(player.getBlockPos().getZ() - blockPos.getZ()) <= 5) {
                if (hasGeodeHelmet(player)) {
                    nearbySpelunker = true;
                }
                closeEnoughPlayers.add(player);
            }
        }

        if (!nearbySpelunker) {
            return false;
        }

        for (PlayerEntity closeEnoughPlayer : closeEnoughPlayers) {
            HitResult hitResult = closeEnoughPlayer.raycast(7, .1f, false);
            if (hitResult.getType() == HitResult.Type.BLOCK && ((BlockHitResult)hitResult).getBlockPos().equals(blockPos)) {
                return false;
            }

            if (Math.abs(closeEnoughPlayer.getBlockPos().getX() - blockPos.getX()) <= 1 && Math.abs(closeEnoughPlayer.getBlockPos().getY() - blockPos.getY()) <= 1
                    && Math.abs(closeEnoughPlayer.getBlockPos().getZ() - blockPos.getZ()) <= 1) {
                return false;
            }
        }
        return true;
    }

    public static void tickCubes() {
        for (World world : spelunkerCubes.keySet()) {
            Set<BlockPos> badBlocks = new HashSet<>();
            for (BlockPos blockPos : spelunkerCubes.get(world).keySet()) {
                MagmaCubeEntity cube = spelunkerCubes.get(world).get(blockPos);
                if (cube.isRemoved()) {
                    badBlocks.add(blockPos);
                    continue;
                }

                if (!shouldCubeExist(world, blockPos)) {
                    cube.setGlowing(false);
                    cube.remove(Entity.RemovalReason.DISCARDED);
                    continue;
                }

                if (cube.isRemoved()) {
                    badBlocks.add(blockPos);
                }
                cube.setAiDisabled(true);
                cube.refreshPositionAndAngles(blockPos.getX() + .5, blockPos.getY(), blockPos.getZ() + .5, 0, 0);
                cube.setHeadYaw(0);
                cube.setGlowing(true);
            }


            for (BlockPos badBlock : badBlocks) {
                spelunkerCubes.get(world).remove(badBlock);
            }
        }
    }

    public static void tickPlayer(PlayerEntity playerEntity) {
        if (!hasGeodeHelmet(playerEntity)) {
            return;
        }
        if (!spelunkerCubes.containsKey(playerEntity.world)) {
            spelunkerCubes.put(playerEntity.world, new HashMap<>());
        }

        int color = Color.HSBtoRGB((playerEntity.world.getTime() % 360) / 360f, 1, 1);
        getGeodeHelmet(playerEntity).getOrCreateSubTag("display").put("color", NbtInt.of(color));

        playerEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 37, 0, true, false, false));

        for (int x = playerEntity.getBlockPos().getX() - 5; x <= playerEntity.getBlockPos().getX() + 5; x++) {
            for (int y = playerEntity.getBlockPos().getY() - 5; y <= playerEntity.getBlockPos().getY() + 5; y++) {
                for (int z = playerEntity.getBlockPos().getZ() - 5; z <= playerEntity.getBlockPos().getZ() + 5; z++) {
                    BlockPos blockPos = new BlockPos(x, y, z);
                    if (!spelunkerCubes.get(playerEntity.world).containsKey(blockPos) && teamForBlock(playerEntity.world.getBlockState(blockPos).getBlock(), playerEntity.world) != null) {
                        if (shouldCubeExist(playerEntity.world, blockPos)) {
                            summonSpelunkerCube(playerEntity.world, blockPos);
                            System.out.println("Summoning a new cube");
                        }
                    }
                }
            }
        }
    }

}
