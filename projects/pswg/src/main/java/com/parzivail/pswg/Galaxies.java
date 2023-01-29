package com.parzivail.pswg;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.parzivail.pswg.api.PswgAddon;
import com.parzivail.pswg.api.PswgContent;
import com.parzivail.pswg.character.SwgSpecies;
import com.parzivail.pswg.component.SwgEntityComponents;
import com.parzivail.pswg.container.*;
import com.parzivail.pswg.data.SwgSpeciesManager;
import com.parzivail.pswg.entity.data.TrackedDataHandlers;
import com.parzivail.pswg.entity.ship.ShipEntity;
import com.parzivail.pswg.item.jetpack.JetpackItem;
import com.parzivail.pswg.screen.BlasterWorkbenchScreenHandler;
import com.parzivail.pswg.screen.LightsaberForgeScreenHandler;
import com.parzivail.util.Lumberjack;
import com.parzivail.util.network.PlayerPacketHandler;
import com.parzivail.util.world.DimensionTeleporter;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.impl.entrypoint.EntrypointUtils;
import net.minecraft.command.argument.DimensionArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import net.minecraft.util.registry.Registry;

import java.util.Objects;

public class Galaxies implements ModInitializer
{
	public static final Lumberjack LOG = new Lumberjack(Resources.MODID);

	public static final ItemGroup TabBlocks = FabricItemGroup
			.builder(Resources.id("blocks"))
			.icon(() -> new ItemStack(SwgBlocks.Panel.GrayImperialLightOn1))
			.build();

	public static final ItemGroup TabItems = FabricItemGroup
			.builder(Resources.id("items"))
			.icon(() -> new ItemStack(SwgItems.Armor.Stormtrooper.helmet))
			.build();

	public static final ItemGroup TabBlasters = FabricItemGroup
			.builder(Resources.id("blasters"))
			.icon(() -> new ItemStack(Registry.ITEM.get(Resources.id("blaster_a280"))))
			.build();

	public static final ItemGroup TabLightsabers = FabricItemGroup
			.builder(Resources.id("lightsabers"))
			.icon(() -> new ItemStack(SwgItems.Lightsaber.Lightsaber))
			.build();

	@Override
	public void onInitialize()
	{
		Galaxies.LOG.debug("onInitialize");

		AutoConfig.register(Config.class, JanksonConfigSerializer::new);
		Resources.CONFIG = AutoConfig.getConfigHolder(Config.class);

		if (!Resources.CONFIG.get().disableUpdateCheck)
			Resources.checkVersion();

		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(SwgSpeciesManager.INSTANCE);

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
		SwgDimensions.register();

		SwgScreenTypes.register();

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher.register(CommandManager.literal("cdim")
			                                  .requires(source -> source.hasPermissionLevel(2) && source.getEntity() != null) // same permission level as tp
			                                  .then(CommandManager.argument("dimension", DimensionArgumentType.dimension())
			                                                      .executes(context -> {
				                                                      var world = DimensionArgumentType.getDimensionArgument(context, "dimension");
				                                                      DimensionTeleporter.teleport(Objects.requireNonNull(context.getSource().getEntity()), world);
				                                                      return 1;
			                                                      })));

			dispatcher.register(CommandManager.literal("pswg_species")
			                                  .requires(source -> source.hasPermissionLevel(2)) // same permission level as tp
			                                  .then(CommandManager.argument("players", EntityArgumentType.players())
			                                                      .then(CommandManager.argument("species", StringArgumentType.greedyString())
			                                                                          .executes(context -> {
				                                                                          var players = EntityArgumentType.getPlayers(context, "players");
				                                                                          var species = context.getArgument("species", String.class);

				                                                                          SwgSpecies swgspecies = null;

				                                                                          if (!"minecraft:none" .equals(species))
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
						                                                                          context.getSource().sendFeedback(Text.translatable(Resources.command("species.invalid"), species), false);
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

		ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register((player, joined) -> ServerPlayNetworking.send(player, SwgPackets.S2C.SyncSpecies, SwgSpeciesManager.INSTANCE.createPacket()));

		ServerPlayNetworking.registerGlobalReceiver(SwgPackets.C2S.LightsaberForgeApply, LightsaberForgeScreenHandler::handleSetLighsaberTag);
		ServerPlayNetworking.registerGlobalReceiver(SwgPackets.C2S.BlasterWorkbenchApply, BlasterWorkbenchScreenHandler::handleSetBlasterTag);
		ServerPlayNetworking.registerGlobalReceiver(SwgPackets.C2S.SetOwnSpecies, SwgSpeciesRegistry::handleSetOwnSpecies);
		ServerPlayNetworking.registerGlobalReceiver(SwgPackets.C2S.PlayerLeftClickItem, PlayerPacketHandler::handleLeftClickPacket);
		ServerPlayNetworking.registerGlobalReceiver(SwgPackets.C2S.PlayerItemAction, PlayerPacketHandler::handleItemAction);
		ServerPlayNetworking.registerGlobalReceiver(SwgPackets.C2S.ShipFire, ShipEntity::handleFirePacket);
		ServerPlayNetworking.registerGlobalReceiver(SwgPackets.C2S.ShipRotation, ShipEntity::handleRotationPacket);
		ServerPlayNetworking.registerGlobalReceiver(SwgPackets.C2S.ShipControls, ShipEntity::handleControlPacket);
		ServerPlayNetworking.registerGlobalReceiver(SwgPackets.C2S.JetpackControls, JetpackItem::handeControlPacket);

		Galaxies.LOG.info("Loading PSWG addons via pswg-addon");
		EntrypointUtils.invoke("pswg-addon", PswgAddon.class, PswgAddon::onPswgReady);

		Galaxies.LOG.info("Baking PSWG addon content");
		PswgContent.bake();

		SwgItems.registerAddons();
	}
}
