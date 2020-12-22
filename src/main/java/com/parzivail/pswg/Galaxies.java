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
		SwgItems.register();

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
