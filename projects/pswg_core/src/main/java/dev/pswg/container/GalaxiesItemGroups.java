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
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;

public class GalaxiesItemGroups
{
	public static final RegistryKey<ItemGroup> CONSTRUCTION_BLOCK_GROUP_KEY = registerGroup("construction_blocks");
	public static final ItemGroup CONSTRUCTION_BLOCK_GROUP = FabricItemGroup.builder().icon(() -> new ItemStack(GalaxiesBlocks.GRAY_IMPERIAL_PANEL_PATTERN_9)).displayName(Text.translatable("pswg.construction_block_group")).build();

	public static final RegistryKey<ItemGroup> WORLDGEN_BLOCK_GROUP_KEY = registerGroup("worldgen_blocks");
	public static final ItemGroup WORLDGEN_BLOCK_GROUP = FabricItemGroup.builder().icon(() -> new ItemStack(GalaxiesBlocks.CANYON.block)).displayName(Text.translatable("pswg.worldgen_block_group")).build();

	public static final RegistryKey<ItemGroup> GENERIC_ITEMS_GROUP_KEY = registerGroup("items");
	public static final ItemGroup GENERIC_ITEMS_GROUP = FabricItemGroup.builder().icon(() -> new ItemStack(GalaxiesItems.DURASTEEL_ROD)).displayName(Text.translatable("pswg.item_group")).build();

	public static final RegistryKey<ItemGroup> FOOD_ITEMS_GROUP_KEY = registerGroup("food_items");
	public static final ItemGroup FOOD_ITEMS_GROUP = FabricItemGroup.builder().icon(() -> new ItemStack(GalaxiesItems.BANTHA_COOKIE)).displayName(Text.translatable("pswg.food_item_group")).build();

	private static RegistryKey<ItemGroup> registerGroup(String id)
	{
		return RegistryKey.of(RegistryKeys.ITEM_GROUP, Galaxies.id(id));
	}
	public static void register()
	{
		Registry.register(Registries.ITEM_GROUP, CONSTRUCTION_BLOCK_GROUP_KEY, CONSTRUCTION_BLOCK_GROUP);
		Registry.register(Registries.ITEM_GROUP, WORLDGEN_BLOCK_GROUP_KEY, WORLDGEN_BLOCK_GROUP);
		Registry.register(Registries.ITEM_GROUP, GENERIC_ITEMS_GROUP_KEY, GENERIC_ITEMS_GROUP);
		Registry.register(Registries.ITEM_GROUP, FOOD_ITEMS_GROUP_KEY, FOOD_ITEMS_GROUP);

		ItemGroupEvents.modifyEntriesEvent(CONSTRUCTION_BLOCK_GROUP_KEY).register(itemGroup -> addBlocks(itemGroup, DataGenItemGroup.ConstructionBlock));
		ItemGroupEvents.modifyEntriesEvent(WORLDGEN_BLOCK_GROUP_KEY).register(itemGroup -> addBlocks(itemGroup, DataGenItemGroup.WorldGenBlock));
		ItemGroupEvents.modifyEntriesEvent(GENERIC_ITEMS_GROUP_KEY).register(itemGroup -> addItems(itemGroup, DataGenItemGroup.Items));
		ItemGroupEvents.modifyEntriesEvent(FOOD_ITEMS_GROUP_KEY).register(itemGroup -> addItems(itemGroup, DataGenItemGroup.Food));
	}

	public static void addItems(FabricItemGroupEntries itemGroup, DataGenItemGroup group)
	{

		AutoGenerateUtil.consumeAnnotatedFields(DataGenItem.class, GalaxiesItems.class, Item.class, (item, dataGenItem) -> {
			if (dataGenItem.itemGroup() == group)
				itemGroup.add(item);
		});
		AutoGenerateUtil.consumeAnnotatedFields(DataGenItem.class, GalaxiesItems.class, DyedItems.class, (dyedItems, dataGenItem) -> {
			for (Item item : dyedItems.values())
				if (dataGenItem.itemGroup() == group)
					itemGroup.add(item);
		});
		AutoGenerateUtil.consumeAnnotatedFields(DataGenItem.class, GalaxiesItems.class, NumberedItems.class, (numberedItems, dataGenItem) -> {
			for (Item item : numberedItems.stream().toList())
				if (dataGenItem.itemGroup() == group)
					itemGroup.add(item);
		});
		AutoGenerateUtil.consumeAnnotatedFields(DataGenItem.class, GalaxiesItems.class, ArmorItems.class, (armorItems, dataGenItem) -> {
			if (dataGenItem.itemGroup() == group)
			{
				itemGroup.add(armorItems.helmet);
				itemGroup.add(armorItems.chestplate);
				itemGroup.add(armorItems.leggings);
				itemGroup.add(armorItems.boots);
			}
		});
	}

	public static void addBlocks(FabricItemGroupEntries itemGroup, DataGenItemGroup group)
	{
		AutoGenerateUtil.consumeAnnotatedGalaxiesBlocks(DataGenBlock.class, (block, dataGenBlock) -> {
			if(dataGenBlock.itemGroup() == group)
				itemGroup.add(block);
		});
	}
}
