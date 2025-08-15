package dev.pswg.container;

import dev.pswg.Gadgets;
import dev.pswg.block.DyedBlocks;
import dev.pswg.block.StoneProducts;
import dev.pswg.datagen.DataGenBlock;
import dev.pswg.datagen.DataGenItemGroup;
import dev.pswg.util.AutoGenerateUtil;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.Block;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class GadgetsItemGroups
{
	public static final RegistryKey<ItemGroup> CONSTRUCTION_BLOCK_GROUP_KEY = registerGroup("construction_blocks");
	public static final ItemGroup CONSTRUCTION_BLOCK_GROUP = FabricItemGroup.builder().icon(() -> new ItemStack(GadgetsBlocks.BLACK_IMPERIAL_PANEL_SECTIONAL)).displayName(Text.translatable("pswg_gadgets.construction_block_group")).build();

	public static final RegistryKey<ItemGroup> WORLDGEN_BLOCK_GROUP_KEY = registerGroup("worldgen_blocks");
	public static final ItemGroup WORLDGEN_BLOCK_GROUP = FabricItemGroup.builder().icon(() -> new ItemStack(GadgetsBlocks.CANYON.block)).displayName(Text.translatable("pswg_gadgets.worldgen_block_group")).build();

	private static RegistryKey<ItemGroup> registerGroup(String id)
	{
		return RegistryKey.of(RegistryKeys.ITEM_GROUP, Gadgets.id(id));
	}

	public static void register()
	{
		Registry.register(Registries.ITEM_GROUP, CONSTRUCTION_BLOCK_GROUP_KEY, CONSTRUCTION_BLOCK_GROUP);
		Registry.register(Registries.ITEM_GROUP, WORLDGEN_BLOCK_GROUP_KEY, WORLDGEN_BLOCK_GROUP);

		ItemGroupEvents.modifyEntriesEvent(CONSTRUCTION_BLOCK_GROUP_KEY).register(itemGroup -> addBlocks(itemGroup, DataGenItemGroup.ConstructionBlock));
		;
		ItemGroupEvents.modifyEntriesEvent(WORLDGEN_BLOCK_GROUP_KEY).register(itemGroup -> addBlocks(itemGroup, DataGenItemGroup.WorldGenBlock));
	}

	public static void addBlocks(FabricItemGroupEntries itemGroup, DataGenItemGroup group)
	{
		AutoGenerateUtil.consumeAnnotatedFields(DataGenBlock.class, GadgetsBlocks.class, Block.class, (block, dataGenBlock) ->
		{
			if (dataGenBlock.itemGroup() == group)
				itemGroup.add(block);
		});
		AutoGenerateUtil.consumeAnnotatedFields(DataGenBlock.class, GadgetsBlocks.class, StoneProducts.class, (products, dataGenBlock) ->
		{
			if (dataGenBlock.itemGroup() == group)
			{
				itemGroup.add(products.block);
				itemGroup.add(products.slab);
				itemGroup.add(products.stairs);
				itemGroup.add(products.wall);
			}
		});
		AutoGenerateUtil.consumeAnnotatedFields(DataGenBlock.class, GadgetsBlocks.class, DyedBlocks.class, (blocks, dataGenBlock) ->
		{
			if (dataGenBlock.itemGroup() == group)
			{
				for (Block block : blocks.values())
				{
					itemGroup.add(block);
				}
			}
		});
	}
}
