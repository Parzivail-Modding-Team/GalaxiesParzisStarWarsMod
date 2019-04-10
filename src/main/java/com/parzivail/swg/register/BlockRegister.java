package com.parzivail.swg.register;

import com.parzivail.swg.Resources;
import com.parzivail.swg.block.BlockTatooineSand;
import com.parzivail.util.component.PBlock;
import net.minecraft.block.Block;
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

	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event)
	{
		IForgeRegistry<Block> r = event.getRegistry();

		r.register(sandTatooine = new BlockTatooineSand());
	}

	@SubscribeEvent
	public static void registerItemBlocks(RegistryEvent.Register<Item> event)
	{
		IForgeRegistry<Item> r = event.getRegistry();

		r.register(sandTatooine.createItemBlock());
	}

	@SubscribeEvent
	public static void registerModels(ModelRegistryEvent event)
	{
		sandTatooine.registerItemModel(Item.getItemFromBlock(sandTatooine));
	}
}
