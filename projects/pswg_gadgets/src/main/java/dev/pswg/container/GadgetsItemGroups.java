package dev.pswg.container;

import dev.pswg.Gadgets;
import dev.pswg.datagen.DataGenBlock;
import dev.pswg.datagen.DataGenItem;
import dev.pswg.datagen.DataGenItemGroup;
import dev.pswg.util.gen.GadgetsGenUtil;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTabOutput;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;

public class GadgetsItemGroups
{

	private static ResourceKey<CreativeModeTab> registerGroup(String id)
	{
		return ResourceKey.create(Registries.CREATIVE_MODE_TAB, Gadgets.id(id));
	}

	public static void register()
	{
		CreativeModeTabEvents.modifyOutputEvent(GalaxiesItemGroups.CONSTRUCTION_BLOCK_GROUP_KEY).register(itemGroup -> addItems(itemGroup, DataGenItemGroup.CONSTRUCTION_BLOCK));
		CreativeModeTabEvents.modifyOutputEvent(GalaxiesItemGroups.WORLDGEN_BLOCK_GROUP_KEY).register(itemGroup -> addItems(itemGroup, DataGenItemGroup.WORLD_GEN_BLOCK));
		CreativeModeTabEvents.modifyOutputEvent(GalaxiesItemGroups.GENERIC_ITEMS_GROUP_KEY).register(itemGroup -> addItems(itemGroup, DataGenItemGroup.ITEMS));
		CreativeModeTabEvents.modifyOutputEvent(GalaxiesItemGroups.FOOD_GROUP_KEY).register(itemGroup -> addItems(itemGroup, DataGenItemGroup.FOOD));
		CreativeModeTabEvents.modifyOutputEvent(GalaxiesItemGroups.GEAR_GROUP_KEY).register(itemGroup -> addItems(itemGroup, DataGenItemGroup.GEAR));
	}

	public static void addItems(FabricCreativeModeTabOutput itemGroup, DataGenItemGroup group)
	{
		GadgetsGenUtil.consumeAnnotatedGadgetsItems(DataGenItem.class, (item, dataGenItem) -> {
			if (dataGenItem.itemGroup() == group)
				itemGroup.accept(item);
		});

		GadgetsGenUtil.consumeAnnotatedGadgetsBlocks(DataGenBlock.class, (block, dataGenBlock) -> {
			if(dataGenBlock.itemGroup() == group)
				itemGroup.accept(block);
		});
	}
}
