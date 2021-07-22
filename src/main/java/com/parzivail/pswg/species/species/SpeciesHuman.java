package com.parzivail.pswg.species.species;

import com.parzivail.pswg.container.SwgSpeciesRegistry;
import com.parzivail.pswg.species.SpeciesVariable;
import com.parzivail.pswg.species.SwgSpecies;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Collection;

public class SpeciesHuman extends SwgSpecies
{
	private static final SpeciesVariable VAR_SKINTONE = new SpeciesVariable(
			SwgSpeciesRegistry.SPECIES_HUMAN,
			"skin_tone",
			"almond",
			"almond",
			"chocolate",
			"ivory",
			"limestone",
			"sand"
	);

	private static final SpeciesVariable VAR_EYEBROWS = new SpeciesVariable(
			SwgSpeciesRegistry.SPECIES_HUMANOID,
			"eyebrows",
			"black",
			"black",
			"blonde",
			"brown",
			"white"
	);

	private static final SpeciesVariable VAR_HAIR = new SpeciesVariable(
			SwgSpeciesRegistry.SPECIES_HUMANOID,
			"hair",
			"brown_1",
			"black_1",
			"black_2",
			"black_3",
			"black_4",
			"blonde_1",
			"blonde_2",
			"blonde_3",
			"blonde_4",
			"brown_1",
			"brown_2",
			"brown_3",
			"brown_4",
			"gray_1",
			"gray_2",
			"gray_3",
			"gray_4"
	);

	public SpeciesHuman(String serialized)
	{
		super(serialized);
	}

	@Override
	public Identifier getSlug()
	{
		return SwgSpeciesRegistry.SPECIES_HUMAN;
	}

	@Override
	public SpeciesVariable[] getVariables()
	{
		return new SpeciesVariable[] { VAR_SKINTONE, VAR_EYEBROWS, VAR_HAIR };
	}

	@Override
	@Environment(EnvType.CLIENT)
	public Collection<Identifier> getTextureStack()
	{
		var stack = new ArrayList<Identifier>();
		stack.add(getGenderedTexture(this, VAR_SKINTONE));
		stack.add(getGenderedTexture(this, VAR_EYEBROWS));
		stack.add(getGenderedGlobalTexture(gender, "clothes"));
		stack.add(getGlobalTexture("eyes"));
		stack.add(getTexture(this, VAR_HAIR));
		return stack;
	}
}
