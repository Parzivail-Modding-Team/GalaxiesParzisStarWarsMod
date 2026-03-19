package dev.pswg.container;

import dev.pswg.Galaxies;
import dev.pswg.autoreg.AutoGenerateUtil;
import dev.pswg.datagen.DataGenBlock;
import dev.pswg.datagen.DataGenItem;
import dev.pswg.datagen.DataGenItemGroup;
import dev.pswg.item.ArmorItems;
import dev.pswg.item.DyedItems;
import dev.pswg.item.NumberedItems;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class GalaxiesItemGroups
{
	public static final ResourceKey<CreativeModeTab> CONSTRUCTION_BLOCK_GROUP_KEY = registerGroup("construction_blocks");
	public static final CreativeModeTab CONSTRUCTION_BLOCK_GROUP = FabricItemGroup.builder().icon(() -> new ItemStack(GalaxiesBlocks.GRAY_IMPERIAL_PANEL_PATTERN_9)).title(Component.translatable("pswg.construction_block_group")).build();

	public static final ResourceKey<CreativeModeTab> WORLDGEN_BLOCK_GROUP_KEY = registerGroup("worldgen_blocks");
	public static final CreativeModeTab WORLDGEN_BLOCK_GROUP = FabricItemGroup.builder().icon(() -> new ItemStack(GalaxiesBlocks.CANYON.block)).title(Component.translatable("pswg.worldgen_block_group")).build();

	public static final ResourceKey<CreativeModeTab> GENERIC_ITEMS_GROUP_KEY = registerGroup("items");
	public static final CreativeModeTab GENERIC_ITEMS_GROUP = FabricItemGroup.builder().icon(() -> new ItemStack(GalaxiesItems.DURASTEEL_ROD)).title(Component.translatable("pswg.item_group")).build();

	public static final ResourceKey<CreativeModeTab> FOOD_ITEMS_GROUP_KEY = registerGroup("food_items");
	public static final CreativeModeTab FOOD_ITEMS_GROUP = FabricItemGroup.builder().icon(() -> new ItemStack(GalaxiesItems.BANTHA_COOKIE)).title(Component.translatable("pswg.food_item_group")).build();

	private static ResourceKey<CreativeModeTab> registerGroup(String id)
	{
		return ResourceKey.create(Registries.CREATIVE_MODE_TAB, Galaxies.id(id));
	}
	public static void register()
	{
		Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, CONSTRUCTION_BLOCK_GROUP_KEY, CONSTRUCTION_BLOCK_GROUP);
		Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, WORLDGEN_BLOCK_GROUP_KEY, WORLDGEN_BLOCK_GROUP);
		Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, GENERIC_ITEMS_GROUP_KEY, GENERIC_ITEMS_GROUP);
		Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, FOOD_ITEMS_GROUP_KEY, FOOD_ITEMS_GROUP);

		ItemGroupEvents.modifyEntriesEvent(CONSTRUCTION_BLOCK_GROUP_KEY).register(itemGroup -> addBlocks(itemGroup, DataGenItemGroup.CONSTRUCTION_BLOCK));
		ItemGroupEvents.modifyEntriesEvent(WORLDGEN_BLOCK_GROUP_KEY).register(itemGroup -> addBlocks(itemGroup, DataGenItemGroup.WORLD_GEN_BLOCK));
		ItemGroupEvents.modifyEntriesEvent(GENERIC_ITEMS_GROUP_KEY).register(itemGroup -> addItems(itemGroup, DataGenItemGroup.ITEMS));
		ItemGroupEvents.modifyEntriesEvent(FOOD_ITEMS_GROUP_KEY).register(itemGroup -> addItems(itemGroup, DataGenItemGroup.FOOD));
	}

	public static void addItems(FabricItemGroupEntries itemGroup, DataGenItemGroup group)
	{

		AutoGenerateUtil.consumeAnnotatedFields(DataGenItem.class, GalaxiesItems.class, Item.class, (item, dataGenItem) -> {
			if (dataGenItem.itemGroup() == group)
				itemGroup.accept(item);
		});
		AutoGenerateUtil.consumeAnnotatedFields(DataGenItem.class, GalaxiesItems.class, DyedItems.class, (dyedItems, dataGenItem) -> {
			for (Item item : dyedItems.values())
				if (dataGenItem.itemGroup() == group)
					itemGroup.accept(item);
		});
		AutoGenerateUtil.consumeAnnotatedFields(DataGenItem.class, GalaxiesItems.class, NumberedItems.class, (numberedItems, dataGenItem) -> {
			for (Item item : numberedItems.stream().toList())
				if (dataGenItem.itemGroup() == group)
					itemGroup.accept(item);
		});
		AutoGenerateUtil.consumeAnnotatedFields(DataGenItem.class, GalaxiesItems.class, ArmorItems.class, (armorItems, dataGenItem) -> {
			if (dataGenItem.itemGroup() == group)
			{
				itemGroup.accept(armorItems.helmet);
				itemGroup.accept(armorItems.chestplate);
				itemGroup.accept(armorItems.leggings);
				itemGroup.accept(armorItems.boots);
			}
		});
	}

	public static void addBlocks(FabricItemGroupEntries itemGroup, DataGenItemGroup group)
	{
		AutoGenerateUtil.consumeAnnotatedGalaxiesBlocks(DataGenBlock.class, (block, dataGenBlock) -> {
			if(dataGenBlock.itemGroup() == group)
				itemGroup.accept(block);
		});
	}
}
