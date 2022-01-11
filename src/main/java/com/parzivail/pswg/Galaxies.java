package com.parzivail.pswg;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.parzivail.datagen.DataGenHelper;
import com.parzivail.pswg.access.IServerResourceManagerAccess;
import com.parzivail.pswg.component.SwgEntityComponents;
import com.parzivail.pswg.container.*;
import com.parzivail.pswg.data.SwgBlasterManager;
import com.parzivail.pswg.data.SwgLightsaberManager;
import com.parzivail.pswg.entity.data.TrackedDataHandlers;
import com.parzivail.pswg.entity.ship.ShipEntity;
import com.parzivail.pswg.handler.PlayerPacketHandler;
import com.parzivail.pswg.item.blaster.data.BlasterAttachmentDescriptor;
import com.parzivail.pswg.item.blaster.data.BlasterAxialInfo;
import com.parzivail.pswg.item.blaster.data.BlasterCoolingBypassProfile;
import com.parzivail.pswg.item.blaster.data.BlasterHeatInfo;
import com.parzivail.pswg.mixin.MinecraftServerMixin;
import com.parzivail.pswg.screen.BlasterWorkbenchScreenHandler;
import com.parzivail.pswg.screen.LightsaberForgeScreenHandler;
import com.parzivail.pswg.species.SwgSpecies;
import com.parzivail.util.Lumberjack;
import com.parzivail.util.nbt.TagSerializer;
import com.parzivail.util.world.DimensionTeleporter;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.command.argument.DimensionArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.TranslatableText;

import java.util.Objects;

public class Galaxies implements ModInitializer
{
	static
	{
		Lumberjack.setLogHeader(Resources.MODID);
	}

	public static final ItemGroup TabBlocks = FabricItemGroupBuilder
			.create(Resources.id("blocks"))
			.icon(() -> new ItemStack(SwgBlocks.Panel.GrayImperialLightOn1))
			.build();

	public static final ItemGroup TabItems = FabricItemGroupBuilder
			.create(Resources.id("items"))
			.icon(() -> new ItemStack(SwgItems.Food.BlueMilk))
			.build();

	public static final ItemGroup TabBlasters = FabricItemGroupBuilder
			.create(Resources.id("blasters"))
			.icon(() -> new ItemStack(SwgItems.Blaster.Blaster))
			.build();

	public static final ItemGroup TabLightsabers = FabricItemGroupBuilder
			.create(Resources.id("lightsabers"))
			.icon(() -> new ItemStack(SwgItems.Lightsaber.Lightsaber))
			.build();

	@Override
	public void onInitialize()
	{
		Lumberjack.debug("onInitialize");

		Resources.checkVersion();

		TagSerializer.register(BlasterAxialInfo.class, BlasterAxialInfo::fromTag, BlasterAxialInfo::toTag);
		TagSerializer.register(BlasterHeatInfo.class, BlasterHeatInfo::fromTag, BlasterHeatInfo::toTag);
		TagSerializer.register(BlasterCoolingBypassProfile.class, BlasterCoolingBypassProfile::fromTag, BlasterCoolingBypassProfile::toTag);
		TagSerializer.register(BlasterAttachmentDescriptor.class, BlasterAttachmentDescriptor::fromTag, BlasterAttachmentDescriptor::toTag);

		AutoConfig.register(Config.class, JanksonConfigSerializer::new);
		Resources.CONFIG = AutoConfig.getConfigHolder(Config.class);

		TrackedDataHandlers.register();

		SwgRecipeType.register();
		SwgRecipeSerializers.register();

		SwgEntities.register();

		SwgStructures.cleanUpTemporaryFiles();
		SwgStructures.General.register();

		SwgSounds.register();

		SwgBlocks.register();

		SwgItems.register();

//		SwgDimensions.Tatooine.register();

		SwgScreenTypes.register();

		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
			dispatcher.register(CommandManager.literal("cdim")
			                                  .requires(source -> source.hasPermissionLevel(2) && source.getEntity() != null) // same permission level as tp
			                                  .then(CommandManager.argument("dimension", DimensionArgumentType.dimension())
			                                                      .executes(context -> {
				                                                      var world = DimensionArgumentType.getDimensionArgument(context, "dimension");
				                                                      DimensionTeleporter.teleport(Objects.requireNonNull(context.getSource().getEntity()), world);
				                                                      return 1;
			                                                      })));
		});

		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
			dispatcher.register(CommandManager.literal("pswg_species")
			                                  .requires(source -> source.hasPermissionLevel(2)) // same permission level as tp
			                                  .then(CommandManager.argument("players", EntityArgumentType.players())
			                                                      .then(CommandManager.argument("species", StringArgumentType.greedyString())
			                                                                          .executes(context -> {
				                                                                          var players = EntityArgumentType.getPlayers(context, "players");
				                                                                          var species = context.getArgument("species", String.class);

				                                                                          SwgSpecies swgspecies = null;

				                                                                          if (!"minecraft:none".equals(species))
				                                                                          {
					                                                                          try
					                                                                          {
						                                                                          swgspecies = SwgSpeciesRegistry.deserialize(species);
					                                                                          }
					                                                                          catch (Exception e)
					                                                                          {
						                                                                          // ignored
					                                                                          }

					                                                                          if (swgspecies == null)
					                                                                          {
						                                                                          context.getSource().sendFeedback(new TranslatableText(Resources.command("species.invalid"), species), false);
						                                                                          return 0;
					                                                                          }
				                                                                          }

				                                                                          for (var player : players)
				                                                                          {
					                                                                          var pc = SwgEntityComponents.getPersistent(player);
					                                                                          pc.setSpecies(swgspecies);
				                                                                          }

				                                                                          return 1;
			                                                                          }))));
		});

		ServerPlayNetworking.registerGlobalReceiver(SwgPackets.C2S.PacketLightsaberForgeApply, LightsaberForgeScreenHandler::handleSetLighsaberTag);
		ServerPlayNetworking.registerGlobalReceiver(SwgPackets.C2S.PacketBlasterWorkbenchApply, BlasterWorkbenchScreenHandler::handleSetBlasterTag);
		ServerPlayNetworking.registerGlobalReceiver(SwgPackets.C2S.PacketSetOwnSpecies, SwgSpeciesRegistry::handleSetOwnSpecies);
		ServerPlayNetworking.registerGlobalReceiver(SwgPackets.C2S.PacketPlayerLeftClickItem, PlayerPacketHandler::handleLeftClickPacket);
		ServerPlayNetworking.registerGlobalReceiver(SwgPackets.C2S.PacketPlayerItemAction, PlayerPacketHandler::handleItemAction);
		ServerPlayNetworking.registerGlobalReceiver(SwgPackets.C2S.PacketShipFire, ShipEntity::handleFirePacket);
		ServerPlayNetworking.registerGlobalReceiver(SwgPackets.C2S.PacketShipRotation, ShipEntity::handleRotationPacket);
		ServerPlayNetworking.registerGlobalReceiver(SwgPackets.C2S.PacketShipControls, ShipEntity::handleControlPacket);

		if (FabricLoader.getInstance().isDevelopmentEnvironment())
			DataGenHelper.run();
	}

	public static class ResourceManagers
	{
		public static ResourceManagers get(MinecraftServer server)
		{
			var srm = ((MinecraftServerMixin)server).getServerResourceManager();
			return ((IServerResourceManagerAccess)srm).getResourceManagers();
		}

		private final SwgBlasterManager blasterManager;
		private final SwgLightsaberManager lightsaberManager;

		public ResourceManagers(ReloadableResourceManager resourceManager)
		{
			resourceManager.registerReloader(blasterManager = new SwgBlasterManager());
			resourceManager.registerReloader(lightsaberManager = new SwgLightsaberManager());
		}

		public SwgBlasterManager getBlasterManager()
		{
			return blasterManager;
		}

		public SwgLightsaberManager getLightsaberManager()
		{
			return lightsaberManager;
		}
	}
}
