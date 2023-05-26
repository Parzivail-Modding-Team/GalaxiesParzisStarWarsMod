package com.parzivail.pswg.container;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.character.SpeciesFactory;
import com.parzivail.pswg.character.SpeciesGender;
import com.parzivail.pswg.character.SwgSpecies;
import com.parzivail.pswg.component.PlayerData;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
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

	public static void handleSetOwnSpecies(MinecraftServer minecraftServer, ServerPlayerEntity serverPlayerEntity, ServerPlayNetworkHandler serverPlayNetworkHandler, PacketByteBuf packetByteBuf, PacketSender packetSender)
	{
		var speciesString = packetByteBuf.readString();

		var c = PlayerData.getPersistentPublic(serverPlayerEntity);
		c.setCharacter(deserialize(speciesString));
	}
}
