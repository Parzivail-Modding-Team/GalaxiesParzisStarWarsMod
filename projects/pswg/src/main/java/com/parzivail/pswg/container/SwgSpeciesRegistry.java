package com.parzivail.pswg.container;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.character.SpeciesFactory;
import com.parzivail.pswg.character.SpeciesGender;
import com.parzivail.pswg.character.SwgSpecies;
import com.parzivail.pswg.client.screen.CharacterScreen;
import com.parzivail.pswg.component.PlayerData;
import com.parzivail.pswg.entity.MannequinEntity;
import com.parzivail.pswg.mixin.ServerPlayerEntityAccessor;
import com.parzivail.pswg.screen.CharacterScreenHandler;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.*;
import java.util.function.Function;

public class SwgSpeciesRegistry
{
	protected static final HashMap<Identifier, Function<String, SwgSpecies>> SPECIES = new LinkedHashMap<>();

	// Species with special meaning internally
	public static final Identifier METASPECIES_GLOBAL = Resources.id("global"); // "global" species contains shared textures
	public static final Identifier METASPECIES_HUMANOID = Resources.id("humanoid"); // "humanoid" species contains shared textures for the "human" model
	public static final Identifier METASPECIES_NONE = new Identifier("none"); // "none" species delegates player models back to Minecraft

	// Normal species and variants
	public static final Identifier SPECIES_AQUALISH = Resources.id("aqualish");
	public static final Identifier SPECIES_BITH = Resources.id("bith");
	public static final Identifier SPECIES_BOTHAN = Resources.id("bothan");
	public static final Identifier SPECIES_CHAGRIAN = Resources.id("chagrian");
	public static final Identifier SPECIES_CHISS = Resources.id("chiss");
	public static final Identifier SPECIES_DEVARONIAN = Resources.id("devaronian");
	public static final Identifier SPECIES_DUROS = Resources.id("duros");
	public static final Identifier SPECIES_HUMAN = Resources.id("human");
	public static final Identifier SPECIES_IKTOTCHI = Resources.id("iktotchi");
	public static final Identifier SPECIES_JAWA = Resources.id("jawa");
	public static final Identifier SPECIES_KAMINOAN = Resources.id("kaminoan");
	public static final Identifier SPECIES_MON_CALAMARI = Resources.id("mon_calamari");
	public static final Identifier SPECIES_PANTORAN = Resources.id("pantoran");
	public static final Identifier SPECIES_RODIAN = Resources.id("rodian");
	public static final Identifier SPECIES_TOGRUTA = Resources.id("togruta");
	public static final Identifier SPECIES_TRANDOSHAN = Resources.id("trandoshan");
	public static final Identifier SPECIES_TWILEK = Resources.id("twilek");
	public static final Identifier SPECIES_WOOKIEE = Resources.id("wookiee");
	public static final Identifier SPECIES_GOTAL = Resources.id("gotal");
	public static final Identifier SPECIES_ONGREE = Resources.id("ongree");
	public static final Identifier SPECIES_GRAN = Resources.id("gran");

	public static void registerAll(Map<Identifier, SpeciesFactory> speciesPresets)
	{
		for (var entry : speciesPresets.entrySet())
			SPECIES.put(entry.getKey(), (serialized) -> entry.getValue().create(serialized));
	}

	public static Set<Identifier> getAllSlugs()
	{
		return SPECIES.keySet();
	}

	public static Collection<Function<String, SwgSpecies>> getFactories()
	{
		return SPECIES.values();
	}

	public static SwgSpecies deserialize(String serialized)
	{
		var id = SwgSpecies.getSpeciesSlug(serialized);
		if (!SPECIES.containsKey(id))
			return null;

		return SPECIES.get(id).apply(serialized);
	}

	public static SwgSpecies create(Identifier species, SpeciesGender gender)
	{
		return SPECIES.get(species).apply(SwgSpecies.toModel(species, gender).toString());
	}

	public static String getTranslationKey(SwgSpecies species)
	{
		if (species == null)
			return "species.pswg.none";
		return getTranslationKey(species.getSlug());
	}

	public static String getTranslationKey(Identifier species)
	{
		return "species." + species.getNamespace() + "." + species.getPath();
	}

	public static void handleSetOwnSpecies(MinecraftServer server, ServerPlayerEntity serverPlayerEntity, ServerPlayNetworkHandler serverPlayNetworkHandler, PacketByteBuf packetByteBuf, PacketSender packetSender)
	{
		var speciesString = packetByteBuf.readString();

		server.execute(() -> {
			// TODO: replace this with some other system
			if (serverPlayerEntity.currentScreenHandler instanceof CharacterScreenHandler characterScreenHandler)
				characterScreenHandler.setSpecies(speciesString);
			else
			{
				var c = PlayerData.getPersistentPublic(serverPlayerEntity);
				c.setCharacter(deserialize(speciesString));
			}
		});
	}

	public static void handleRequestCustomizeSelf(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler serverPlayNetworkHandler, PacketByteBuf packetByteBuf, PacketSender packetSender)
	{
		openCharacterCustomizer(player, player);
	}

	public static void openCharacterCustomizer(ServerPlayerEntity player, LivingEntity targetEntity)
	{
		var playera = (ServerPlayerEntityAccessor)player;

		playera.invokeIncrementScreenHandlerSyncId();

		var buf = new PacketByteBuf(Unpooled.buffer());

		buf.writeInt(playera.getScreenHandlerSyncId());
		buf.writeInt(targetEntity.getId());

		ServerPlayNetworking.send(player, SwgPackets.S2C.OpenCharacterCustomizer, buf);
		player.currentScreenHandler = new CharacterScreenHandler(playera.getScreenHandlerSyncId(), player.getInventory(), targetEntity);

		playera.invokeOnScreenHandlerOpened(player.currentScreenHandler);
	}

	public static void handleOpenCharacterCustomizer(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender)
	{
		var syncId = buf.readInt();
		var entityId = buf.readInt();

		client.execute(() -> {
			var entity = client.world.getEntityById(entityId);
			if (!(entity instanceof LivingEntity livingEntity))
				return;

			var clientPlayerEntity = client.player;
			var screenHandler = new CharacterScreenHandler(syncId, clientPlayerEntity.getInventory(), livingEntity);
			clientPlayerEntity.currentScreenHandler = screenHandler;

			String originalSpecies = SwgSpeciesRegistry.METASPECIES_NONE.toString();

			if (entity instanceof PlayerEntity playerEntity)
			{
				var components = PlayerData.getPersistentPublic(playerEntity);
				if (components.getCharacter() != null)
					originalSpecies = components.getCharacter().serialize();
			}
			else if (entity instanceof MannequinEntity mannequinEntity)
				originalSpecies = mannequinEntity.getSpecies();

			client.setScreen(new CharacterScreen(new CharacterScreen.Context(true, originalSpecies), screenHandler, clientPlayerEntity.getInventory(), livingEntity));
		});
	}
}
