package com.parzivail.pswg;

import com.parzivail.pswg.container.SwgBlocks;
import com.parzivail.pswg.entity.ShipEntity;
import com.parzivail.pswg.entity.data.TrackedDataHandlers;
import com.parzivail.pswg.util.Lumberjack;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.entity.FabricEntityTypeBuilder;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class GalaxiesMain implements ModInitializer
{
	public static final ItemGroup Tab = FabricItemGroupBuilder.build(Resources.identifier("blocks"), () -> new ItemStack(Items.APPLE));

	public static final EntityType<ShipEntity> EntityTypeShip = Registry.register(Registry.ENTITY_TYPE, Resources.identifier("ship"), FabricEntityTypeBuilder.create(EntityCategory.MISC, ShipEntity::new).size(EntityDimensions.fixed(1, 1)).build());

	public static final Identifier PacketShipRotation = Resources.identifier("ship_rotation");

	@Override
	public void onInitialize()
	{
		Lumberjack.debug("onInitialize");

		TrackedDataHandlers.register();

		SwgBlocks.register(SwgBlocks.Sand.Tatooine, Resources.identifier("sand_tatooine"));
		SwgBlocks.register(SwgBlocks.Crate.Octagon, Resources.identifier("crate_octagon"));

		ServerSidePacketRegistry.INSTANCE.register(GalaxiesMain.PacketShipRotation, ShipEntity::handleRotationPacket);
	}
}
