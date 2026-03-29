package dev.pswg.container;

import dev.pswg.Gadgets;
import dev.pswg.datagen.DataGenBlock;
import dev.pswg.datagen.DataGenItem;
import dev.pswg.datagen.DataGenItemGroup;
import dev.pswg.item.ArmorItems;
import dev.pswg.item.DyedItems;
import dev.pswg.item.NumberedItems;
import dev.pswg.autoreg.AutoGenerateUtil;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTabOutput;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class GadgetsItemGroups
{

	public static final ResourceKey<CreativeModeTab> DEMOLITIONS_ITEMS_GROUP_KEY = registerGroup("demolitions_items");
	public static final CreativeModeTab DEMOLITIONS_ITEMS_GROUP = FabricCreativeModeTab.builder().icon(() -> new ItemStack(GadgetsItems.THERMAL_DETONATOR_ITEM)).title(Component.translatable("pswg_gadgets.demolitions_items_group")).build();

	private static ResourceKey<CreativeModeTab> registerGroup(String id)
	{
		return ResourceKey.create(Registries.CREATIVE_MODE_TAB, Gadgets.id(id));
	}

	public static void register()
	{
		Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, DEMOLITIONS_ITEMS_GROUP_KEY, DEMOLITIONS_ITEMS_GROUP);

		CreativeModeTabEvents.modifyOutputEvent(DEMOLITIONS_ITEMS_GROUP_KEY).register(itemGroup -> addItems(itemGroup, DataGenItemGroup.DEMOLITIONS_GADGETS));
	}

	public static void addItems(FabricCreativeModeTabOutput itemGroup, DataGenItemGroup group)
	{

		AutoGenerateUtil.consumeAnnotatedFields(DataGenItem.class, GadgetsItems.class, Item.class, (item, dataGenItem) -> {
			if (dataGenItem.itemGroup() == group)
				itemGroup.accept(item);
		});
		AutoGenerateUtil.consumeAnnotatedFields(DataGenItem.class, GadgetsItems.class, DyedItems.class, (dyedItems, dataGenItem) -> {
			for (Item item : dyedItems.values())
				if (dataGenItem.itemGroup() == group)
					itemGroup.accept(item);
		});
		AutoGenerateUtil.consumeAnnotatedFields(DataGenItem.class, GadgetsItems.class, NumberedItems.class, (numberedItems, dataGenItem) -> {
			for (Item item : numberedItems.stream().toList())
				if (dataGenItem.itemGroup() == group)
					itemGroup.accept(item);
		});
		AutoGenerateUtil.consumeAnnotatedFields(DataGenItem.class, GadgetsItems.class, ArmorItems.class, (armorItems, dataGenItem) -> {
			if (dataGenItem.itemGroup() == group)
			{
				itemGroup.accept(armorItems.helmet);
				itemGroup.accept(armorItems.chestplate);
				itemGroup.accept(armorItems.leggings);
				itemGroup.accept(armorItems.boots);
			}
		});
	}

	public static void addBlocks(FabricCreativeModeTabOutput itemGroup, DataGenItemGroup group)
	{
		AutoGenerateUtil.consumeAnnotatedGalaxiesBlocks(DataGenBlock.class, (block, dataGenBlock) -> {
			if(dataGenBlock.itemGroup() == group)
				itemGroup.accept(block);
		});
	}
}
