package com.parzivail.pswg.container;

import com.parzivail.pswg.Resources;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;

public class SwgSpecies
{
	public static final ArrayList<Identifier> SPECIES = new ArrayList<>();
	public static final HashMap<Identifier, String[]> VARIANTS = new HashMap<>();

	public static final Identifier SPECIES_NONE = register(new Identifier("none"), "none");
	//	public static final Identifier SPECIES_HUMAN_M = register(Resources.identifier("human"), "steve", "blonde");
	//	public static final Identifier SPECIES_HUMAN_F = register(Resources.identifier("human"), "alex", "blonde");
	public static final Identifier SPECIES_AQUALISH_M = register(Resources.identifier("aqualish_m"), "beige", "green");
	public static final Identifier SPECIES_AQUALISH_F = register(Resources.identifier("aqualish_f"), "beige", "green");
	public static final Identifier SPECIES_BITH_M = register(Resources.identifier("bith_m"), "green", "pink", "white");
	public static final Identifier SPECIES_BITH_F = register(Resources.identifier("bith_f"), "green", "pink", "white");
	//	public static final Identifier SPECIES_BOTHAN_M = register(Resources.identifier("bothan_m"), "brown");
	//	public static final Identifier SPECIES_BOTHAN_F = register(Resources.identifier("bothan_f"), "brown");
	public static final Identifier SPECIES_CHAGRIAN_M = register(Resources.identifier("chagrian_m"), "blue");
	public static final Identifier SPECIES_CHAGRIAN_F = register(Resources.identifier("chagrian_f"), "blue");
	public static final Identifier SPECIES_JAWA = register(Resources.identifier("jawa"), "brown");
	//	public static final Identifier SPECIES_KAMINOAN_M = register(Resources.identifier("kaminoan_m"), "blue", "purple", "white");
	//	public static final Identifier SPECIES_KAMINOAN_F = register(Resources.identifier("kaminoan_f"), "blue", "purple", "white");
	public static final Identifier SPECIES_TOGRUTA_M = register(Resources.identifier("togruta_m"), "orange", "purple", "red", "yellow");
	public static final Identifier SPECIES_TOGRUTA_F = register(Resources.identifier("togruta_f"), "orange", "purple", "red", "yellow");
	public static final Identifier SPECIES_TWILEK_M = register(Resources.identifier("twilek_m"), "blue", "green", "pink", "purple", "beige", "yellow");
	public static final Identifier SPECIES_TWILEK_F = register(Resources.identifier("twilek_f"), "blue", "green", "pink", "purple", "beige", "yellow");
	//	public static final Identifier SPECIES_WOOKIEE_M = register(Resources.identifier("wookiee_m"), "brown");
	//	public static final Identifier SPECIES_WOOKIEE_F = register(Resources.identifier("wookiee_f"), "brown");

	private static Identifier register(Identifier species, String... variants)
	{
		SPECIES.add(species);
		VARIANTS.put(species, variants);
		return species;
	}
}
