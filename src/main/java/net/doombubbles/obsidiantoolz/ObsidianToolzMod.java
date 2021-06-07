package net.doombubbles.obsidiantoolz;

import net.doombubbles.obsidiantoolz.Enchantments.*;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.minecraft.block.DispenserBlock;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.List;

public class ObsidianToolzMod implements DedicatedServerModInitializer {

    public static final List<ObsidianToolzEnchantment> ENCHANTMENTS = new ArrayList<>();
    public static ObsidianToolzEnchantment ZEUS;
    public static ObsidianToolzEnchantment VEINMINER;
    public static ObsidianToolzEnchantment TIMBER;
    public static ObsidianToolzEnchantment SMELT_TOUCH;
    public static ObsidianToolzEnchantment GRINDING;
    public static ObsidianToolzEnchantment EXPERTISE;
    public static ObsidianToolzEnchantment SPACE;
    public static ObsidianToolzEnchantment SPAWNER_TOUCH;
    public static ObsidianToolzEnchantment SOULBOUND;
    public static ObsidianToolzEnchantment WITHER_ASPECT;
    public static ObsidianToolzEnchantment PHOTOSYNTHESIS;
    public static ObsidianToolzEnchantment MAGMA_WALKER;
    public static ObsidianToolzEnchantment MAGNETIC;
    public static ObsidianToolzEnchantment BOOSTED;
    public static ObsidianToolzEnchantment INFLATION;
    public static ObsidianToolzEnchantment MUGGING;
    public static ObsidianToolzEnchantment OVERPROTECTION;

    @Override
    public void onInitializeServer() {


        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

		/*
		ZEUS = Registry.register(
				Registry.ENCHANTMENT,
				new Identifier("obsidiantoolz", "zeus"),
				new ZeusEnchantment(
						Enchantment.Rarity.VERY_RARE,
						EnchantmentTarget.WEAPON,
						new EquipmentSlot[] {
								EquipmentSlot.MAINHAND
						}
				)
		);
		ENCHANTMENTS.add(ZEUS);
		*/

        VEINMINER = Registry.register(
                Registry.ENCHANTMENT,
                new Identifier("obsidiantoolz", "veinminer"),
                new VeinMinerEnchantment(
                        Enchantment.Rarity.VERY_RARE,
                        EnchantmentTarget.DIGGER,
                        new EquipmentSlot[]{
                                EquipmentSlot.MAINHAND
                        }
                )
        );
        ENCHANTMENTS.add(VEINMINER);

        TIMBER = Registry.register(
                Registry.ENCHANTMENT,
                new Identifier("obsidiantoolz", "timber"),
                new TimberEnchantment(
                        Enchantment.Rarity.VERY_RARE,
                        EnchantmentTarget.BREAKABLE,
                        new EquipmentSlot[]{
                                EquipmentSlot.MAINHAND
                        }
                )
        );
        ENCHANTMENTS.add(TIMBER);

        SMELT_TOUCH = Registry.register(
                Registry.ENCHANTMENT,
                new Identifier("obsidiantoolz", "smelt_touch"),
                new SmeltTouchEnchantment(
                        Enchantment.Rarity.VERY_RARE,
                        EnchantmentTarget.DIGGER,
                        new EquipmentSlot[]{
                                EquipmentSlot.MAINHAND
                        }
                )
        );
        ENCHANTMENTS.add(SMELT_TOUCH);

        GRINDING = Registry.register(
                Registry.ENCHANTMENT,
                new Identifier("obsidiantoolz", "grinding"),
                new GrindingEnchantment(
                        Enchantment.Rarity.RARE,
                        EnchantmentTarget.WEAPON,
                        new EquipmentSlot[]{
                                EquipmentSlot.MAINHAND
                        }
                )
        );
        ENCHANTMENTS.add(GRINDING);

        EXPERTISE = Registry.register(
                Registry.ENCHANTMENT,
                new Identifier("obsidiantoolz", "expertise"),
                new ExpertiseEnchantment(
                        Enchantment.Rarity.RARE,
                        EnchantmentTarget.DIGGER,
                        new EquipmentSlot[]{
                                EquipmentSlot.MAINHAND
                        }
                )
        );
        ENCHANTMENTS.add(EXPERTISE);

        SPACE = Registry.register(
                Registry.ENCHANTMENT,
                new Identifier("obsidiantoolz", "space"),
                new SpaceEnchantment(
                        Enchantment.Rarity.VERY_RARE,
                        EnchantmentTarget.BOW,
                        new EquipmentSlot[]{
                                EquipmentSlot.MAINHAND
                        }
                )
        );
        ENCHANTMENTS.add(SPACE);

        SOULBOUND = Registry.register(
                Registry.ENCHANTMENT,
                new Identifier("obsidiantoolz", "soulbound"),
                new SoulboundEnchantment(
                        Enchantment.Rarity.VERY_RARE,
                        EnchantmentTarget.VANISHABLE,
                        new EquipmentSlot[]{
                                EquipmentSlot.MAINHAND,
                                EquipmentSlot.OFFHAND,
                                EquipmentSlot.FEET,
                                EquipmentSlot.LEGS,
                                EquipmentSlot.CHEST,
                                EquipmentSlot.HEAD
                        }
                )
        );
        ENCHANTMENTS.add(SOULBOUND);

		/*
		SPAWNER_TOUCH = Registry.register(
				Registry.ENCHANTMENT,
				new Identifier("obsidiantoolz", "spawner_touch"),
				new SpawnerTouchEnchantment(
						Enchantment.Rarity.VERY_RARE,
						EnchantmentTarget.DIGGER,
						new EquipmentSlot[] {
								EquipmentSlot.MAINHAND
						}
				)
		);
		ENCHANTMENTS.add(SPAWNER_TOUCH);
		*/

        WITHER_ASPECT = Registry.register(
                Registry.ENCHANTMENT,
                new Identifier("obsidiantoolz", "wither_aspect"),
                new WitherAspectEnchantment(
                        Enchantment.Rarity.RARE,
                        EnchantmentTarget.WEAPON,
                        new EquipmentSlot[]{
                                EquipmentSlot.MAINHAND
                        }
                )
        );
        ENCHANTMENTS.add(WITHER_ASPECT);

        PHOTOSYNTHESIS = Registry.register(
                Registry.ENCHANTMENT,
                new Identifier("obsidiantoolz", "photosynthesis"),
                new PhotosynthesisEnchantment(
                        Enchantment.Rarity.VERY_RARE,
                        EnchantmentTarget.ARMOR_HEAD,
                        new EquipmentSlot[]{
                                EquipmentSlot.HEAD
                        }
                )
        );
        ENCHANTMENTS.add(PHOTOSYNTHESIS);

        MAGMA_WALKER = Registry.register(
                Registry.ENCHANTMENT,
                new Identifier("obsidiantoolz", "magma_walker"),
                new MagmaWalkerEnchantment(
                        Enchantment.Rarity.VERY_RARE,
                        EnchantmentTarget.ARMOR_FEET,
                        new EquipmentSlot[]{
                                EquipmentSlot.FEET
                        }
                )
        );
        ENCHANTMENTS.add(MAGMA_WALKER);

        MAGNETIC = Registry.register(
                Registry.ENCHANTMENT,
                new Identifier("obsidiantoolz", "magnetic"),
                new MagneticEnchantment(
                        Enchantment.Rarity.VERY_RARE,
                        EnchantmentTarget.BREAKABLE,
                        new EquipmentSlot[]{
                                EquipmentSlot.MAINHAND
                        }
                )
        );
        ENCHANTMENTS.add(MAGNETIC);

        BOOSTED = Registry.register(
                Registry.ENCHANTMENT,
                new Identifier("obsidiantoolz", "boosted"),
                new BoostedEnchantment(
                        Enchantment.Rarity.RARE,
                        EnchantmentTarget.WEARABLE,
                        new EquipmentSlot[]{
                                EquipmentSlot.CHEST
                        }
                )
        );
        ENCHANTMENTS.add(BOOSTED);

        INFLATION = Registry.register(
                Registry.ENCHANTMENT,
                new Identifier("obsidiantoolz", "inflation"),
                new InflationEnchantment(
                        Enchantment.Rarity.VERY_RARE,
                        EnchantmentTarget.DIGGER,
                        new EquipmentSlot[]{
                                EquipmentSlot.MAINHAND
                        }
                )
        );
        ENCHANTMENTS.add(INFLATION);

        MUGGING = Registry.register(
                Registry.ENCHANTMENT,
                new Identifier("obsidiantoolz", "mugging"),
                new MuggingEnchantment(
                        Enchantment.Rarity.VERY_RARE,
                        EnchantmentTarget.WEAPON,
                        new EquipmentSlot[]{
                                EquipmentSlot.MAINHAND
                        }
                )
        );
        ENCHANTMENTS.add(MUGGING);

        OVERPROTECTION = Registry.register(
                Registry.ENCHANTMENT,
                new Identifier("obsidiantoolz", "overprotection"),
                new OverprotectionEnchantment(
                        Enchantment.Rarity.VERY_RARE,
                        EnchantmentTarget.ARMOR,
                        new EquipmentSlot[]{
                                EquipmentSlot.FEET,
                                EquipmentSlot.LEGS,
                                EquipmentSlot.CHEST,
                                EquipmentSlot.HEAD
                        }
                )
        );
        ENCHANTMENTS.add(OVERPROTECTION);
    }

    public static Text asName(String realString) {
        return new LiteralText(realString).setStyle(Style.EMPTY.withItalic(false));
    }
}
