package dev.pswg;

import dev.pswg.api.GalaxiesAddon;
import dev.pswg.configuration.GalaxiesConfig;
import dev.pswg.configuration.IConfigContainer;
import dev.pswg.configuration.MemoryConfigContainer;
import dev.pswg.container.*;
import dev.pswg.container.worldgen.GalaxiesDimensions;
import dev.pswg.container.worldgen.GalaxiesStructureKeys;
import dev.pswg.container.worldgen.GalaxiesStructurePieces;
import dev.pswg.container.worldgen.GalaxiesStructureTypes;
import dev.pswg.interaction.GalaxiesEntityLeftClickManager;
import dev.pswg.interaction.GalaxiesPlayerActionManager;
import dev.pswg.interaction.LeftClickingEntityAttachment;
import dev.pswg.interaction.RecoilEntityAttachment;
import dev.pswg.networking.*;
import dev.pswg.updater.GithubReleaseEntry;
import dev.pswg.updater.UpdateChecker;
import dev.pswg.util.world.DimensionTeleporter;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Optional;

/**
 * The main entrypoint for PSWG common-side registration
 */
public final class Galaxies implements ModInitializer
{
	/**
	 * The mod ID assigned to PSWG
	 */
	public static final String MODID = "pswg";

	/**
	 * Creates a scoped {@link ResourceLocation} whose domain is this
	 * mod's MODID
	 *
	 * @param path The path for the {@link ResourceLocation}
	 *
	 * @return A scoped {@link ResourceLocation}
	 */
	public static ResourceLocation id(String path)
	{
		return ResourceLocation.fromNamespaceAndPath(MODID, path);
	}

	/**
	 * The newer version of PSWG core that exists on the remote, if any
	 */
	private static GithubReleaseEntry REMOTE_VERSION = null;

	/**
	 * A logger available only to PSWG core
	 */
	public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

	/**
	 * The configuration file that controls the behavior of PSWG core
	 */
	public static final IConfigContainer<GalaxiesConfig> CONFIG = new MemoryConfigContainer<>(new GalaxiesConfig());

	/**
	 * Create a derived logger for a subsystem within PSWG
	 *
	 * @param key The sub-header to log underneath the modid
	 *
	 * @return The nested logger
	 */
	public static Logger createSubLogger(String key)
	{
		return LoggerFactory.getLogger(MODID + "/" + key);
	}

	/**
	 * Determines if the user has requested that PSWG core and addons
	 * should not use the internet to determine if newer versions are
	 * available
	 *
	 * @return True if update checking is disabled
	 */
	public static boolean isUpdateCheckDisabled()
	{
		var config = CONFIG.get();
		return config.isUpdateCheckingDisabled;
	}

	/**
	 * Gets the newer version of PSWG core present on the remote, if any
	 *
	 * @return The version if a newer one exists, or empty otherwise
	 */
	public static Optional<GithubReleaseEntry> getRemoteVersion()
	{
		return Optional.ofNullable(REMOTE_VERSION);
	}

	public static final TagKey<DamageType> IGNORES_INVULNERABLE_FRAMES = TagKey.create(Registries.DAMAGE_TYPE, id("ignores_invulnerable_frames"));
	public static final TagKey<DamageType> IGNORES_DAMAGE_TILT = TagKey.create(Registries.DAMAGE_TYPE, id("ignores_damage_tilt"));


	@Override
	public void onInitialize()
	{
		if (!isUpdateCheckDisabled())
		{
			REMOTE_VERSION = UpdateChecker.getRemoteVersion(MODID, "Parzivail-Modding-Team/GalaxiesParzisStarWarsMod").orElse(null);
		}

		PayloadTypeRegistry.playC2S().register(PlayerInteractItemLeftC2SPacket.ID, PlayerInteractItemLeftC2SPacket.CODEC);

		PayloadTypeRegistry.playC2S().register(GalaxiesPlayerActionC2SPacket.ID, GalaxiesPlayerActionC2SPacket.CODEC);
		PayloadTypeRegistry.playS2C().register(GalaxiesPlayerActionS2CPacket.ID, GalaxiesPlayerActionS2CPacket.CODEC);

		PayloadTypeRegistry.playS2C().register(GalaxiesEntitySpawnS2CPacket.ID, GalaxiesEntitySpawnS2CPacket.CODEC);

		PayloadTypeRegistry.playS2C().register(PreciseVelocityParticleS2CPayload.ID, PreciseVelocityParticleS2CPayload.CODEC);

		GalaxiesEntityLeftClickManager.initialize();
		GalaxiesPlayerActionManager.initialize();

		LeftClickingEntityAttachment.register();

		RecoilEntityAttachment.register();

		GalaxiesBlocks.register();
		GalaxiesBlockEntities.register();

		GalaxiesItems.register();
		GalaxiesItemGroups.register();
		GalaxiesToolMaterials.register();

		GalaxiesLootTables.register();
		GalaxiesStructureKeys.register();
		GalaxiesStructurePieces.register();
		GalaxiesStructureTypes.register();

		GalaxiesScreenHandlerTypes.register();

		GalaxiesDimensions.register();

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher.register(Commands.literal("cdim")
			                                  .requires(source -> source.hasPermission(2) && source.getEntity() != null) // same permission level as tp
			                                  .then(Commands.argument("dimension", DimensionArgument.dimension())
			                                                      .executes(context -> {
				                                                      var world = DimensionArgument.getDimension(context, "dimension");
				                                                      DimensionTeleporter.teleport(Objects.requireNonNull(context.getSource().getEntity()), world);
				                                                      return 1;
			                                                      })));
		});

		LOGGER.info("Loading PSWG modules and addons via pswg-addon");
		FabricLoader.getInstance().invokeEntrypoints("pswg-addon", GalaxiesAddon.class, GalaxiesAddon::onGalaxiesStarting);
		FabricLoader.getInstance().invokeEntrypoints("pswg-addon", GalaxiesAddon.class, GalaxiesAddon::onGalaxiesReady);
		FabricLoader.getInstance().invokeEntrypoints("pswg-addon", GalaxiesAddon.class, GalaxiesAddon::onGalaxiesFinalizing);

		LOGGER.info("Galaxies initialized");
	}
}
