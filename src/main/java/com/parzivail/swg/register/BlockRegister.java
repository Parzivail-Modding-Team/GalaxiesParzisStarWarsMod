package com.parzivail.swg.register;

import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.swg.block.BlockTatooineSand;
import net.minecraft.block.Block;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

@GameRegistry.ObjectHolder(StarWarsGalaxy.MODID)
public class BlockRegister
{
	@GameRegistry.ObjectHolder("sand_tatooine")
	public static final Block sandTatooine = null;

	@SubscribeEvent
	public static void register(RegistryEvent.Register<Block> event)
	{
		IForgeRegistry<Block> r = event.getRegistry();

		r.register(new BlockTatooineSand());
	}
}
