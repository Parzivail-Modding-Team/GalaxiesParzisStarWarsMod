package com.parzivail.pswg.container;

import com.parzivail.pswg.Resources;
import net.minecraft.util.Identifier;

import java.util.ArrayList;

public class SwgSpecies
{
	public static final ArrayList<Identifier> SPECIES = new ArrayList<>();

	public static final Identifier SPECIES_HUMAN = register(Resources.identifier("human"));
	public static final Identifier SPECIES_TOGRUTA_M = register(Resources.identifier("togruta_m"));
	public static final Identifier SPECIES_TOGRUTA_F = register(Resources.identifier("togruta_f"));
	public static final Identifier SPECIES_CHAGRIAN_M = register(Resources.identifier("chagrian_m"));
	public static final Identifier SPECIES_CHAGRIAN_F = register(Resources.identifier("chagrian_f"));
	public static final Identifier SPECIES_TWILEK_M = register(Resources.identifier("twilek_m"));
	public static final Identifier SPECIES_TWILEK_F = register(Resources.identifier("twilek_f"));

	private static Identifier register(Identifier species)
	{
		SPECIES.add(species);
		return species;
	}
}
