package com.parzivail.swg.player.species;

public enum SpeciesType
{
	Human, Bith_M, Bothan_F, Bothan_M, Chagrian_F, Chagrian_M, Togruta_F, Togruta_M, Twilek_F, Twilek_M;

	public static SpeciesType getNamedSpecies(String search)
	{
		for (SpeciesType each : SpeciesType.class.getEnumConstants())
			if (each.name().compareToIgnoreCase(search) == 0)
				return each;
		return null;
	}
}
