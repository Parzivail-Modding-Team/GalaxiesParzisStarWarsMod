package dev.pswg.container;

import dev.pswg.Gadgets;
import dev.pswg.datagen.DataGenBlock;
import dev.pswg.datagen.DataGenItem;
import dev.pswg.datagen.DataGenItemGroup;
import dev.pswg.item.ArmorItems;
import dev.pswg.item.DyedItems;
import dev.pswg.item.NumberedItems;
import dev.pswg.autoreg.AutoGenerateUtil;
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

public class GadgetsItemGroups
{

	public static final RegistryKey<ItemGroup> DEMOLITIONS_ITEMS_GROUP_KEY = registerGroup("demolitions_items");
	public static final ItemGroup DEMOLITIONS_ITEMS_GROUP = FabricItemGroup.builder().icon(() -> new ItemStack(GadgetsItems.THERMAL_DETONATOR_ITEM)).displayName(Text.translatable("pswg_gadgets.demolitions_items_group")).build();

	private static RegistryKey<ItemGroup> registerGroup(String id)
	{
		return RegistryKey.of(RegistryKeys.ITEM_GROUP, Gadgets.id(id));
	}

	public static void register()
	{
		Registry.register(Registries.ITEM_GROUP, DEMOLITIONS_ITEMS_GROUP_KEY, DEMOLITIONS_ITEMS_GROUP);

		ItemGroupEvents.modifyEntriesEvent(DEMOLITIONS_ITEMS_GROUP_KEY).register(itemGroup -> addItems(itemGroup, DataGenItemGroup.DemolitionsGadgets));
	}

	public static void addItems(FabricItemGroupEntries itemGroup, DataGenItemGroup group)
	{

		AutoGenerateUtil.consumeAnnotatedFields(DataGenItem.class, GadgetsItems.class, Item.class, (item, dataGenItem) -> {
			if (dataGenItem.itemGroup() == group)
				itemGroup.add(item);
		});
		AutoGenerateUtil.consumeAnnotatedFields(DataGenItem.class, GadgetsItems.class, DyedItems.class, (dyedItems, dataGenItem) -> {
			for (Item item : dyedItems.values())
				if (dataGenItem.itemGroup() == group)
					itemGroup.add(item);
		});
		AutoGenerateUtil.consumeAnnotatedFields(DataGenItem.class, GadgetsItems.class, NumberedItems.class, (numberedItems, dataGenItem) -> {
			for (Item item : numberedItems.stream().toList())
				if (dataGenItem.itemGroup() == group)
					itemGroup.add(item);
		});
		AutoGenerateUtil.consumeAnnotatedFields(DataGenItem.class, GadgetsItems.class, ArmorItems.class, (armorItems, dataGenItem) -> {
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
