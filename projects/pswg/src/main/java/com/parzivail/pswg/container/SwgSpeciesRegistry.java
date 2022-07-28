package com.parzivail.pswg.container;

import com.google.common.base.Suppliers;
import com.parzivail.pswg.Resources;
import com.parzivail.pswg.character.SwgSpecies;
import com.parzivail.pswg.character.species.*;
import com.parzivail.pswg.component.SwgEntityComponents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class SwgSpeciesRegistry
{
	protected static final HashMap<Identifier, Function<String, SwgSpecies>> SPECIES = new LinkedHashMap<>();

	// Species with special meaning internally
	public static final Identifier SPECIES_GLOBAL = Resources.id("global"); // "global" species contains shared textures
	public static final Identifier SPECIES_HUMANOID = Resources.id("humanoid"); // "humanoid" species contains shared textures for the "human" model
	public static final Identifier SPECIES_NONE = new Identifier("none"); // "none" species delegates player models back to Minecraft

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

	public static final Supplier<List<SwgSpecies>> ALL_SPECIES = Suppliers.memoize(
			() -> SPECIES
					.values()
					.stream()
					.map(stringSwgSpeciesFunction -> stringSwgSpeciesFunction.apply(null))
					.collect(Collectors.toList())
	);

	static
	{
		SPECIES.put(SwgSpeciesRegistry.SPECIES_AQUALISH, SpeciesAqualish::new);
		SPECIES.put(SwgSpeciesRegistry.SPECIES_BITH, SpeciesBith::new);
		//		SPECIES.put(SwgSpeciesRegistry.SPECIES_BOTHAN, SpeciesBothan::new);
		SPECIES.put(SwgSpeciesRegistry.SPECIES_CHAGRIAN, SpeciesChagrian::new);
		SPECIES.put(SwgSpeciesRegistry.SPECIES_JAWA, SpeciesJawa::new);
		//		SPECIES.put(SwgSpeciesRegistry.SPECIES_KAMINOAN, SpeciesKaminoan::new);
		SPECIES.put(SwgSpeciesRegistry.SPECIES_TOGRUTA, SpeciesTogruta::new);
		SPECIES.put(SwgSpeciesRegistry.SPECIES_TWILEK, SpeciesTwilek::new);
		SPECIES.put(SwgSpeciesRegistry.SPECIES_HUMAN, SpeciesHuman::new);
		SPECIES.put(SwgSpeciesRegistry.SPECIES_CHISS, SpeciesChiss::new);
		SPECIES.put(SwgSpeciesRegistry.SPECIES_PANTORAN, SpeciesPantoran::new);
		SPECIES.put(SwgSpeciesRegistry.SPECIES_WOOKIEE, SpeciesWookiee::new);
	}

	public static SwgSpecies deserialize(String serialized)
	{
		var id = SwgSpecies.getSpeciesSlug(serialized);
		if (!SPECIES.containsKey(id))
			return null;

		return SPECIES.get(id).apply(serialized);
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

		var c = SwgEntityComponents.getPersistent(serverPlayerEntity);
		c.setSpecies(deserialize(speciesString));
	}
}
