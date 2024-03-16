package com.parzivail.pswg;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.parzivail.pswg.api.PswgAddon;
import com.parzivail.pswg.api.PswgContent;
import com.parzivail.pswg.character.SwgSpecies;
import com.parzivail.pswg.component.PlayerData;
import com.parzivail.pswg.container.*;
import com.parzivail.pswg.entity.ship.ShipEntity;
import com.parzivail.pswg.features.blasters.workbench.BlasterWorkbenchScreenHandler;
import com.parzivail.pswg.features.lightsabers.addon.AddonLightsaberManager;
import com.parzivail.pswg.features.lightsabers.data.LightsaberDescriptor;
import com.parzivail.pswg.features.lightsabers.forge.LightsaberForgeScreenHandler;
import com.parzivail.pswg.item.jetpack.JetpackItem;
import com.parzivail.util.Lumberjack;
import com.parzivail.util.client.model.compat.FmlCompat;
import com.parzivail.util.data.pack.ModDataHelper;
import com.parzivail.util.network.PlayerPacketHandler;
import com.parzivail.util.world.DimensionTeleporter;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.command.argument.DimensionArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;

import java.util.Objects;

public class Galaxies implements ModInitializer
{
	public static final Lumberjack LOG = new Lumberjack(Resources.MODID);

	public static final ItemGroup TabBlocks = FabricItemGroup
			.builder()
			.displayName(Text.translatable(Resources.tab("blocks")))
			.icon(() -> new ItemStack(SwgBlocks.Panel.GrayImperialLight1))
			.build();
	public static final RegistryKey<ItemGroup> TabBlocksKey = RegistryKey.of(RegistryKeys.ITEM_GROUP, Resources.id("blocks"));

	public static final ItemGroup TabItems = FabricItemGroup
			.builder()
			.displayName(Text.translatable(Resources.tab("items")))
			.icon(() -> new ItemStack(SwgItems.Armor.Stormtrooper.helmet))
			.build();
	public static final RegistryKey<ItemGroup> TabItemsKey = RegistryKey.of(RegistryKeys.ITEM_GROUP, Resources.id("items"));

	public static final ItemGroup TabBlasters = FabricItemGroup
			.builder()
			.displayName(Text.translatable(Resources.tab("blasters")))
			.icon(() -> new ItemStack(Registries.ITEM.get(SwgItems.getBlasterRegistrationId(Resources.id("a280")))))
			.build();
	public static final RegistryKey<ItemGroup> TabBlastersKey = RegistryKey.of(RegistryKeys.ITEM_GROUP, Resources.id("blasters"));

	public static final ItemGroup TabLightsabers = FabricItemGroup
			.builder()
			.displayName(Text.translatable(Resources.tab("lightsabers")))
			.icon(() -> new ItemStack(Registries.ITEM.get(SwgItems.getLightsaberRegistrationId(Resources.id("luke_rotj")))))
			.build();
	public static final RegistryKey<ItemGroup> TabLightsabersKey = RegistryKey.of(RegistryKeys.ITEM_GROUP, Resources.id("lightsabers"));

	@Override
	public void onInitialize()
	{
		Galaxies.LOG.debug("onInitialize");

		if (FmlCompat.isForge())
			Galaxies.LOG.error("PSWG seems to be running on Forge, which is unsupported and not recommended. Here be dragons!");

		AutoConfig.register(Config.class, JanksonConfigSerializer::new);
		Resources.CONFIG = AutoConfig.getConfigHolder(Config.class);

		Resources.checkVersion();

		SwgRecipeType.register();
		SwgRecipeSerializers.register();

		SwgEntities.register();

		SwgStructures.cleanUpTemporaryFiles();
		SwgStructures.General.register();

		SwgSounds.register();

		SwgBlocks.register();

		SwgItems.register();

		SwgSensorTypes.register();

		SwgMemoryModuleTypes.register();

		//		SwgDimensions.Tatooine.register();
		SwgDimensions.register();

		SwgScreenTypes.register();
		SwgParticleTypes.register();

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

				                                                                          if (!SwgSpeciesRegistry.METASPECIES_NONE.toString().equals(species))
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
						                                                                          context.getSource().sendFeedback(() -> Text.translatable(Resources.command("species.invalid"), species), false);
						                                                                          return 0;
					                                                                          }
				                                                                          }

				                                                                          for (var player : players)
				                                                                          {
					                                                                          var pc = PlayerData.getPersistentPublic(player);
					                                                                          pc.setCharacter(swgspecies);
				                                                                          }

				                                                                          return 1;
			                                                                          }))));
		});

		ServerPlayNetworking.registerGlobalReceiver(SwgPackets.C2S.LightsaberForgeApply, LightsaberForgeScreenHandler::handleSetLighsaberTag);
		ServerPlayNetworking.registerGlobalReceiver(SwgPackets.C2S.BlasterWorkbenchApply, BlasterWorkbenchScreenHandler::handleSetBlasterTag);
		ServerPlayNetworking.registerGlobalReceiver(SwgPackets.C2S.SetOwnSpecies, SwgSpeciesRegistry::handleSetOwnSpecies);
		ServerPlayNetworking.registerGlobalReceiver(SwgPackets.C2S.RequestCustomizeSelf, SwgSpeciesRegistry::handleRequestCustomizeSelf);
		ServerPlayNetworking.registerGlobalReceiver(SwgPackets.C2S.PlayerLeftClickItem, PlayerPacketHandler::handleLeftClickPacket);
		ServerPlayNetworking.registerGlobalReceiver(SwgPackets.C2S.PlayerItemAction, PlayerPacketHandler::handleItemAction);
		ServerPlayNetworking.registerGlobalReceiver(SwgPackets.C2S.ShipFire, ShipEntity::handleFirePacket);
		ServerPlayNetworking.registerGlobalReceiver(SwgPackets.C2S.ShipRotation, ShipEntity::handleRotationPacket);
		ServerPlayNetworking.registerGlobalReceiver(SwgPackets.C2S.ShipControls, ShipEntity::handleControlPacket);
		ServerPlayNetworking.registerGlobalReceiver(SwgPackets.C2S.JetpackControls, JetpackItem::handeControlPacket);
		ServerPlayNetworking.registerGlobalReceiver(SwgPackets.C2S.TogglePatrolPosture, PlayerPacketHandler::handleTogglePatrolPosture);

		Galaxies.LOG.info("Loading PSWG addons via pswg-addon");
		FabricLoader.getInstance().invokeEntrypoints("pswg-addon", PswgAddon.class, PswgAddon::onPswgStarting);
		FabricLoader.getInstance().invokeEntrypoints("pswg-addon", PswgAddon.class, PswgAddon::onPswgReady);

		Galaxies.LOG.info("Loading PSWG addons via datapack instantiation");
		ModDataHelper.withResources(ResourceType.SERVER_DATA, resourceManager -> {
			AddonLightsaberManager.INSTANCE.load(resourceManager);
			// TODO: blasters, etc.
		});

		for (var data : AddonLightsaberManager.INSTANCE.getData().values())
		{
			var descriptor = new LightsaberDescriptor(data.identifier(), data.owner(), data.bladeColor(), data.bladeType());

			if (data.unstable())
				descriptor.unstable();

			for (var entry : data.bladeLengthCoefficients().entrySet())
				descriptor.bladeLength(entry.getKey(), entry.getValue());

			PswgContent.registerLightsaberPreset(descriptor);
		}

		Galaxies.LOG.info("Baking PSWG addon content");
		PswgContent.bake();

		SwgItems.registerAddons();
		SwgItems.hookTabs();
	}
}
