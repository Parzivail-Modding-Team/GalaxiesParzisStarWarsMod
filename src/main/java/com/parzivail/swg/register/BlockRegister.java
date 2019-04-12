package com.parzivail.swg.register;

import com.parzivail.swg.Resources;
import com.parzivail.swg.block.BlockTatooineSand;
import com.parzivail.util.component.PBlock;
import com.parzivail.util.component.PBlockFacing;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

@GameRegistry.ObjectHolder(Resources.MODID)
public class BlockRegister
{
	public static PBlock sandTatooine;
	public static PBlock lightFloorAngledSmall;

	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event)
	{
		IForgeRegistry<Block> r = event.getRegistry();

		r.register(sandTatooine = new BlockTatooineSand());
		r.register(lightFloorAngledSmall = new PBlockFacing("light_floor_angled_small", Material.CIRCUITS));
	}

	@SubscribeEvent
	public static void registerItemBlocks(RegistryEvent.Register<Item> event)
	{
		IForgeRegistry<Item> r = event.getRegistry();

		r.register(sandTatooine.createItemBlock());
		r.register(lightFloorAngledSmall.createItemBlock());
	}

	@SubscribeEvent
	public static void registerModels(ModelRegistryEvent event)
	{
		sandTatooine.registerItemModel(Item.getItemFromBlock(sandTatooine));
		lightFloorAngledSmall.registerItemModel(Item.getItemFromBlock(lightFloorAngledSmall));
	}
}
