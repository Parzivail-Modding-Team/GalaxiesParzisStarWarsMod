package com.parzivail.pswg;

import com.parzivail.pswg.entity.ShipEntity;
import com.parzivail.pswg.util.Lumberjack;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.entity.FabricEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.item.*;
import net.minecraft.util.registry.Registry;

public class GalaxiesMain implements ModInitializer
{
	public static final Block SAND_TATOOINE = new Block(FabricBlockSettings.of(Material.SAND).nonOpaque().build());

	public static final ItemGroup TAB = FabricItemGroupBuilder.build(Resources.identifier("blocks"), () -> new ItemStack(Items.APPLE));

	public static final EntityType<ShipEntity> SHIP = Registry.register(Registry.ENTITY_TYPE, Resources.identifier("ship"), FabricEntityTypeBuilder.create(EntityCategory.MISC, ShipEntity::new).size(EntityDimensions.fixed(1, 1)).build());

	@Override
	public void onInitialize()
	{
		Lumberjack.debug("onInitialize");

		Registry.register(Registry.BLOCK, Resources.identifier("sand_tatooine"), SAND_TATOOINE);
		Registry.register(Registry.ITEM, Resources.identifier("sand_tatooine"), new BlockItem(SAND_TATOOINE, new Item.Settings().group(TAB)));
	}
}
