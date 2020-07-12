package com.parzivail.pswg;

import com.parzivail.pswg.container.*;
import com.parzivail.pswg.entity.ShipEntity;
import com.parzivail.pswg.entity.data.TrackedDataHandlers;
import com.parzivail.pswg.util.Lumberjack;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.command.arguments.DimensionArgumentType;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.command.CommandManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.dimension.DimensionType;

import java.util.Objects;

public class Galaxies implements ModInitializer
{
	public static final ItemGroup Tab = FabricItemGroupBuilder
			.create(Resources.identifier("blocks"))
			.icon(() -> new ItemStack(Items.APPLE))
			.build();

	@Override
	public void onInitialize()
	{
		Lumberjack.debug("onInitialize");

		TrackedDataHandlers.register();

		SwgEntities.Ship.register();

		SwgBlocks.register(SwgBlocks.Sand.Tatooine, Resources.identifier("sand_tatooine"));

		SwgBlocks.register(SwgBlocks.Crate.Octagon, Resources.identifier("crate_octagon"));

		SwgBlocks.register(SwgBlocks.Ore.Chromium, Resources.identifier("ore_chromium"));

		Registry.register(Registry.ITEM, Resources.identifier("lightsaber"), SwgItems.Lightsaber);

		Registry.register(Registry.ITEM, Resources.identifier("a280"), SwgItems.Blaster.A280);

		SwgDimensions.Tatooine.registerDimension();

		CommandRegistrationCallback.EVENT.register(((dispatcher, dedicated) ->
				dispatcher.register(CommandManager.literal("cdim")
						.requires(source -> source.hasPermissionLevel(2) && source.getEntity() != null) // same permission level as tp
						.then(CommandManager.argument("dimension", DimensionArgumentType.dimension())
								.executes((context -> {
									DimensionType dimensionType = DimensionArgumentType.getDimensionArgument(context, "dimension");
									FabricDimensions.teleport(Objects.requireNonNull(context.getSource().getEntity()), dimensionType);
					                                                      return 1;
				                                                      }))))));

		ServerSidePacketRegistry.INSTANCE.register(SwgPackets.C2S.PacketShipRotation, ShipEntity::handleRotationPacket);
		ServerSidePacketRegistry.INSTANCE.register(SwgPackets.C2S.PacketShipControls, ShipEntity::handleControlPacket);
	}
}
