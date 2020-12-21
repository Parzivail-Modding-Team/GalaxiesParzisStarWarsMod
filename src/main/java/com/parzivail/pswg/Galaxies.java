package com.parzivail.pswg;

import com.parzivail.pswg.client.species.SwgSpeciesInstance;
import com.parzivail.pswg.command.SpeciesArgumentType;
import com.parzivail.pswg.command.SpeciesVariantArgumentType;
import com.parzivail.pswg.component.SwgEntityComponents;
import com.parzivail.pswg.component.SwgPersistentComponents;
import com.parzivail.pswg.container.*;
import com.parzivail.pswg.dimension.DimensionTeleporter;
import com.parzivail.pswg.entity.ShipEntity;
import com.parzivail.pswg.entity.data.TrackedDataHandlers;
import com.parzivail.pswg.handler.PlayerPacketHandler;
import com.parzivail.pswg.util.Lumberjack;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.command.argument.ArgumentTypes;
import net.minecraft.command.argument.DimensionArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Objects;

public class Galaxies implements ModInitializer
{
	public static final ItemGroup Tab = FabricItemGroupBuilder
			.create(Resources.identifier("main"))
			.icon(() -> new ItemStack(SwgItems.Lightsaber.Lightsaber))
			.build();

	@Override
	public void onInitialize()
	{
		Lumberjack.debug("onInitialize");

		// TODO: dual weilding one on and one off lightsaber might play the sound twice
		// TODO: new blaster models
		// TODO: Pm3D block particle textures
		// TODO: Modeled block aabb bounds
		// TODO: Modeled block waterlogging
		// TODO: Fusion tank texture to power of two to fix mipmapping
		// TODO: Imperial lights to use borderless imperial panel texture

		AutoConfig.register(Config.class, JanksonConfigSerializer::new);
		Resources.CONFIG = AutoConfig.getConfigHolder(Config.class);
		TrackedDataHandlers.register();

		SwgRecipeType.register();
		SwgRecipeSerializers.register();

		SwgEntities.register();

		{
			Path modDir = FabricLoader.getInstance().getGameDir().resolve("mods");
			try {
				for (Path file: (Iterable<Path>) Files.list(modDir)::iterator) {
					String name = file.getFileName().toString();
					if (name.startsWith("zipfstmp") && name.endsWith(".tmp")) {
						try {
							Files.delete(file);
						} catch (IOException ignored) {
							// Ignore this. If we get an exception here, we're on Windows,
							// and the file is being used.
						}
					}
				}
			}
			catch (IOException ignored)
			{
			}
		}

		SwgStructures.General.register();

		SwgSounds.register();

		SwgBlocks.register();

		Registry.register(Registry.ITEM, Resources.identifier("lightsaber"), SwgItems.Lightsaber.Lightsaber);

		Registry.register(Registry.ITEM, Resources.identifier("blaster_a280"), SwgItems.Blaster.A280);
		Registry.register(Registry.ITEM, Resources.identifier("blaster_dh17"), SwgItems.Blaster.DH17);
		Registry.register(Registry.ITEM, Resources.identifier("blaster_e11"), SwgItems.Blaster.E11);
		Registry.register(Registry.ITEM, Resources.identifier("blaster_ee3"), SwgItems.Blaster.EE3);

		Registry.register(Registry.ITEM, Resources.identifier("debug"), SwgItems.Debug.Debug);

		Registry.register(Registry.ITEM, Resources.identifier("beskar_ingot"), SwgItems.Ingot.Beskar);
		Registry.register(Registry.ITEM, Resources.identifier("chromium_ingot"), SwgItems.Ingot.Chromium);
		Registry.register(Registry.ITEM, Resources.identifier("cortosis_ingot"), SwgItems.Ingot.Cortosis);
		Registry.register(Registry.ITEM, Resources.identifier("desh_ingot"), SwgItems.Ingot.Desh);
		Registry.register(Registry.ITEM, Resources.identifier("durasteel_ingot"), SwgItems.Ingot.Durasteel);
		Registry.register(Registry.ITEM, Resources.identifier("plasteel_ingot"), SwgItems.Ingot.Plasteel);
		Registry.register(Registry.ITEM, Resources.identifier("titanium_ingot"), SwgItems.Ingot.Titanium);

		Registry.register(Registry.ITEM, Resources.identifier("salt_pile"), SwgItems.Food.SaltPile);

		Registry.register(Registry.ITEM, Resources.identifier("jogan_fruit"), SwgItems.Food.JoganFruit);
		Registry.register(Registry.ITEM, Resources.identifier("chasuka"), SwgItems.Food.Chasuka);
		Registry.register(Registry.ITEM, Resources.identifier("meiloorun"), SwgItems.Food.Meiloorun);
		Registry.register(Registry.ITEM, Resources.identifier("mynock_wing"), SwgItems.Food.MynockWing);
		Registry.register(Registry.ITEM, Resources.identifier("cooked_mynock_wing"), SwgItems.Food.FriedMynockWing);
		Registry.register(Registry.ITEM, Resources.identifier("bantha_chop"), SwgItems.Food.BanthaChop);
		Registry.register(Registry.ITEM, Resources.identifier("cooked_bantha_chop"), SwgItems.Food.BanthaSteak);
		Registry.register(Registry.ITEM, Resources.identifier("nerf_chop"), SwgItems.Food.NerfChop);
		Registry.register(Registry.ITEM, Resources.identifier("cooked_nerf_chop"), SwgItems.Food.NerfSteak);
		Registry.register(Registry.ITEM, Resources.identifier("gizka_chop"), SwgItems.Food.GizkaChop);
		Registry.register(Registry.ITEM, Resources.identifier("cooked_gizka_chop"), SwgItems.Food.GizkaSteak);
		Registry.register(Registry.ITEM, Resources.identifier("flangth_takeout"), SwgItems.Food.FlangthTakeout);
		Registry.register(Registry.ITEM, Resources.identifier("flangth_plate"), SwgItems.Food.FlangthPlate);

		Registry.register(Registry.ITEM, Resources.identifier("death_stick_red"), SwgItems.Food.DeathStickRed);
		Registry.register(Registry.ITEM, Resources.identifier("death_stick_yellow"), SwgItems.Food.DeathStickYellow);

		Registry.register(Registry.ITEM, Resources.identifier("blue_milk"), SwgItems.Food.BlueMilk);
		Registry.register(Registry.ITEM, Resources.identifier("blue_puff_cube"), SwgItems.Food.BluePuffCube);
		Registry.register(Registry.ITEM, Resources.identifier("blue_yogurt"), SwgItems.Food.BlueYogurt);

		Registry.register(Registry.ITEM, Resources.identifier("qrikki_bread"), SwgItems.Food.QrikkiBread);
		Registry.register(Registry.ITEM, Resources.identifier("qrikki_waffle"), SwgItems.Food.QrikkiWaffle);

		SwgDimensions.Tatooine.registerDimension();

		SwgScreenTypes.register();

		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) ->
				                                           dispatcher.register(CommandManager.literal("cdim")
				                                                                             .requires(source -> source.hasPermissionLevel(2) && source.getEntity() != null) // same permission level as tp
				                                                                             .then(CommandManager.argument("dimension", DimensionArgumentType.dimension())
				                                                                                                 .executes(context -> {
					                                                                                                 ServerWorld world = DimensionArgumentType.getDimensionArgument(context, "dimension");
					                                                                                                 DimensionTeleporter.teleport(Objects.requireNonNull(context.getSource().getEntity()), world);
					                                                                                                 return 1;
				                                                                                                 }))));

		ArgumentTypes.register("pswg:species", SpeciesArgumentType.class, new ConstantArgumentSerializer<>(SpeciesArgumentType::new));
		ArgumentTypes.register("pswg:variant", SpeciesVariantArgumentType.class, new ConstantArgumentSerializer<>(SpeciesVariantArgumentType::new));
		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) ->
			dispatcher.register(CommandManager.literal("pswg_species")
				.requires(source -> source.hasPermissionLevel(2)) // same permission level as tp
				.then(CommandManager.argument("players", EntityArgumentType.players())
				.then(CommandManager.argument("species", new SpeciesArgumentType()) // uses default variant
				.executes(context -> {
					Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers(context, "players");
					Identifier species = context.getArgument("species", Identifier.class);

					if (!SwgSpecies.SPECIES.contains(species)) {
						context.getSource().sendFeedback(new TranslatableText(Resources.command("species.invalid"), species.toString()), false);
						return 0;
					}

					for (ServerPlayerEntity player : players) {
						SwgPersistentComponents pc = SwgEntityComponents.getPersistent(player);
						if (species.equals(SwgSpecies.SPECIES_NONE))
							pc.setSpecies(null);
						else
							pc.setSpecies(new SwgSpeciesInstance(species));
					}

					return 1;
				})
				.then(CommandManager.argument("variant", new SpeciesVariantArgumentType()) // uses specified variant
					.executes(context -> {
						Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers(context, "players");
						Identifier species = context.getArgument("species", Identifier.class);
						String variant = context.getArgument("variant", String.class);

						if (!SwgSpecies.SPECIES.contains(species)) {
							context.getSource().sendFeedback(new TranslatableText(Resources.command("species.invalid"), species.toString()), false);
							return 0;
						}

						if (!ArrayUtils.contains(SwgSpecies.VARIANTS.get(species), variant)) {
							context.getSource().sendFeedback(new TranslatableText(Resources.command("species.variant.invalid"), variant, species.toString()), false);
							return 0;
						}

						for (ServerPlayerEntity player : players) {
							SwgPersistentComponents pc = SwgEntityComponents.getPersistent(player);
							if (species.equals(SwgSpecies.SPECIES_NONE))
								pc.setSpecies(null);
							else
								pc.setSpecies(new SwgSpeciesInstance(species, variant));
						}

						return 1;
					})
				)))));

		ServerSidePacketRegistry.INSTANCE.register(SwgPackets.C2S.PacketPlayerLeftClickItem, PlayerPacketHandler::handleLeftClickPacket);
		ServerSidePacketRegistry.INSTANCE.register(SwgPackets.C2S.PacketPlayerLightsaberToggle, PlayerPacketHandler::handleLightsaberTogglePacket);
		ServerSidePacketRegistry.INSTANCE.register(SwgPackets.C2S.PacketShipRotation, ShipEntity::handleRotationPacket);
		ServerSidePacketRegistry.INSTANCE.register(SwgPackets.C2S.PacketShipControls, ShipEntity::handleControlPacket);
	}
}
