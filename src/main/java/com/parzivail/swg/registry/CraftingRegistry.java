package com.parzivail.swg.registry;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.ItemStack;

public class CraftingRegistry
{
	public static void register()
	{
		GameRegistry.addSmelting(ItemRegister.banthaChop, new ItemStack(ItemRegister.banthaSteak, 1), 0.2F);
		GameRegistry.addSmelting(ItemRegister.gizkaChop, new ItemStack(ItemRegister.gizkaSteak, 1), 0.2F);
		GameRegistry.addSmelting(ItemRegister.nerfChop, new ItemStack(ItemRegister.nerfSteak, 1), 0.2F);
		GameRegistry.addSmelting(ItemRegister.mynockWing, new ItemStack(ItemRegister.friedMynockWing, 1), 0.2F);
		GameRegistry.addSmelting(ItemRegister.qrikkiBread, new ItemStack(ItemRegister.qrikkiWaffle, 1), 0.2F);

		GameRegistry.addSmelting(BlockRegister.oreChromium, new ItemStack(ItemRegister.chromiumIngot), 1.0F);
	}
}
