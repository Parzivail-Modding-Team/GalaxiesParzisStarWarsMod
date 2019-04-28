package com.parzivail.swg.register;

import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.swg.block.BlockTatooineSand;
import com.parzivail.util.component.IBlockWithItem;
import com.parzivail.util.component.PBlock;
import com.parzivail.util.component.PBlockFacing;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.ArrayList;
import java.util.List;

public class BlockRegister
{
	public static List<IBlockWithItem> _blocksToRegister;

	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event)
	{
		IForgeRegistry<Block> r = event.getRegistry();
		_blocksToRegister = new ArrayList<>();

		register(r, new BlockTatooineSand());
		register(r, new PBlock("wall_lab"));
		register(r, new PBlock("brick_old"));
		register(r, new PBlock("brick_temple"));
		register(r, new PBlock("brick_temple_fancy"));
		register(r, new PBlock("concrete"));
		register(r, new PBlock("crate"));
		register(r, new PBlock("door_hoth"));
		register(r, new PBlock("metal_black"));
		register(r, new PBlock("metal_caution"));
		register(r, new PBlock("metal_gray"));
		register(r, new PBlock("metal_gray_bevel"));
		register(r, new PBlock("metal_gray_dark"));
		register(r, new PBlock("metal_white"));
		register(r, new PBlock("mud"));
		register(r, new PBlock("ore_chromium"));
		register(r, new PBlock("ore_cortosis"));
		register(r, new PBlock("ore_rubindum"));
		register(r, new PBlock("ore_titanium"));
		register(r, new PBlock("pourstone"));
		register(r, new PBlock("pumice"));
		register(r, new PBlock("salt"));
		register(r, new PBlock("sandstone_oxidized"));
		register(r, new PBlock("sand_irridiated"));
		register(r, new PBlock("sand_oxidized"));
		register(r, new PBlock("sand_rhodocrosite"));
		register(r, new PBlock("slab_stone_temple"));
		register(r, new PBlock("snow_cut"));
		register(r, new PBlock("snow_hardpack"));
		register(r, new PBlock("stab_stone_temple_dark_top"));
		register(r, new PBlock("stab_stone_temple_top"));
		register(r, new PBlock("stone_hoth"));
		register(r, new PBlock("stone_temple"));

		// chair_basic

		register(r, new PBlockFacing("antenna_thin", Material.GROUND).withBoundingBox(7, 0, 7, 2, 48, 2));
		register(r, new PBlockFacing("cable_clamped", Material.GROUND).withBoundingBox(4, 0, 4, 8, 5, 8));
		register(r, new PBlockFacing("cable_ground", Material.GROUND).withBoundingBox(4, 0, 4, 8, 5, 8));
		register(r, new PBlockFacing("console_hoth_1", Material.GROUND).withBoundingBox(0, 0, 0, 16, 32, 16).setLightLevel(0.5f));
		register(r, new PBlockFacing("console_hoth_2", Material.GROUND).withBoundingBox(0, 0, 0, 16, 32, 16).setLightLevel(0.5f));
		register(r, new PBlockFacing("console_hoth_3", Material.GROUND).withBoundingBox(0, 0, 0, 16, 32, 16).setLightLevel(0.5f));
		register(r, new PBlockFacing("console_medical", Material.GROUND).withBoundingBox(2, 0, 2, 12, 16, 12));
		register(r, new PBlockFacing("console_medical_large", Material.GROUND).withBoundingBox(0, 0, 0, 16, 32, 16));
		register(r, new PBlockFacing("crate_hoth", Material.GROUND).withBoundingBox(2, 0, 2, 12, 16, 12));
		register(r, new PBlockFacing("crate_hoth_large", Material.GROUND).withBoundingBox(2, 0, 2, 12, 16, 12));
		register(r, new PBlockFacing("crate_mosespa", Material.GROUND).withBoundingBox(2, 0, 2, 12, 16, 12));
		register(r, new PBlockFacing("crate_villa", Material.GROUND).withBoundingBox(2, 0, 2, 12, 16, 12));
		register(r, new PBlockFacing("crate_yavin", Material.GROUND).withBoundingBox(2, 0, 2, 12, 16, 12));
		register(r, new PBlockFacing("ladder_yavin", Material.GROUND).withBoundingBox(0, 0, 0, 16, 16, 16));
		register(r, new PBlockFacing("light_ceiling_hoth", Material.GROUND).withBoundingBox(4, 11, 4, 8, 5, 8).setLightLevel(1));
		register(r, new PBlockFacing("light_ceiling_hoth_hanging", Material.GROUND).withBoundingBox(2, 0, 2, 12, 8, 12).setLightLevel(1));
		register(r, new PBlockFacing("light_floor_angled_small", Material.GROUND).withBoundingBox(4, 0, 4, 8, 5, 8).setLightLevel(1));
		register(r, new PBlockFacing("light_floor_angled_large", Material.GROUND).withBoundingBox(4, 0, 4, 8, 8, 8).setLightLevel(1));
		register(r, new PBlockFacing("light_floor_runway", Material.GROUND).withBoundingBox(4, 0, 4, 8, 8, 8).withTranslucent().setLightLevel(1));
		register(r, new PBlockFacing("light_vertical", Material.GROUND).setLightLevel(1));
		register(r, new PBlockFacing("light_vertical_2", Material.GROUND).setLightLevel(1));
		register(r, new PBlockFacing("light_wall_angled", Material.GROUND).withBoundingBox(4, 4, 4, 8, 8, 8).setLightLevel(1));
		register(r, new PBlockFacing("light_wall_indicator", Material.GROUND).withBoundingBox(4, 4, 4, 8, 8, 8).setLightLevel(1));
		register(r, new PBlockFacing("light_wall_indicator_cluster", Material.GROUND).withBoundingBox(4, 4, 4, 8, 8, 8).setLightLevel(1));
		register(r, new PBlockFacing("machine_generator_hangar", Material.GROUND));
		register(r, new PBlockFacing("machine_moseisley", Material.GROUND).withBoundingBox(0, 0, 0, 16, 32, 16));
		register(r, new PBlockFacing("machine_spoked", Material.GROUND));
		register(r, new PBlockFacing("panel_hoth", Material.GROUND).withBoundingBox(0, 0, 0, 16, 32, 16).withTranslucent().setLightLevel(0.5f));
		register(r, new PBlockFacing("panel_wall_lock", Material.GROUND).withBoundingBox(4, 4, 4, 8, 8, 8));
		register(r, new PBlockFacing("panel_wall_lock_tall", Material.GROUND).withBoundingBox(4, 4, 4, 8, 8, 8));
		register(r, new PBlockFacing("pipe_ground_vent_large", Material.GROUND).withBoundingBox(0, 0, 0, 16, 16, 16));
		register(r, new PBlockFacing("pipe_ground_vent_round", Material.GROUND).withBoundingBox(4, 4, 4, 8, 8, 8));
		register(r, new PBlockFacing("pipe_ground_vent_small", Material.GROUND).withBoundingBox(4, 4, 4, 8, 8, 8));
		register(r, new PBlockFacing("pipe_wall_round", Material.GROUND).withBoundingBox(4, 4, 4, 8, 8, 8));
		register(r, new PBlockFacing("tank_air", Material.GROUND).withBoundingBox(4, 0, 4, 8, 24, 8));
		register(r, new PBlockFacing("vaporator", Material.GROUND));
		register(r, new PBlockFacing("vaporator_2", Material.GROUND));
	}

	private static void register(IForgeRegistry<Block> r, Block block)
	{
		block.setCreativeTab(StarWarsGalaxy.tab);
		r.register(block);
		if (block instanceof IBlockWithItem)
			_blocksToRegister.add((IBlockWithItem)block);
	}

	@SubscribeEvent
	public static void registerItemBlocks(RegistryEvent.Register<Item> event)
	{
		IForgeRegistry<Item> r = event.getRegistry();

		for (IBlockWithItem ibwi : _blocksToRegister)
			r.register(ibwi.createItemBlock());
	}

	@SubscribeEvent
	public static void registerModels(ModelRegistryEvent event)
	{
		for (IBlockWithItem ibwi : _blocksToRegister)
			ibwi.registerItemModel(Item.getItemFromBlock(ibwi.getBlock()));
	}
}
