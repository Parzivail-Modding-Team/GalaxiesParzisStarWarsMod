package com.parzivail.pswg.species.species;

import com.parzivail.pswg.container.SwgSpeciesRegistry;
import com.parzivail.pswg.species.SpeciesVariable;
import com.parzivail.pswg.species.SwgSpecies;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Collection;

public class SpeciesChiss extends SwgSpecies
{
	private static final SpeciesVariable VAR_EYEBROWS = new SpeciesVariable(
			SwgSpeciesRegistry.SPECIES_HUMANOID,
			"eyebrows",
			"black",
			"black",
			"white"
	);

	private static final SpeciesVariable VAR_HAIR = new SpeciesVariable(
			SwgSpeciesRegistry.SPECIES_HUMANOID,
			"hair",
			"black_1",
			"black_1",
			"black_2",
			"black_3",
			"black_4",
			"gray_1",
			"gray_2",
			"gray_3",
			"gray_4"
	);

	public SpeciesChiss(String serialized)
	{
		super(serialized);
	}

	@Override
	public Identifier getSlug()
	{
		return SwgSpeciesRegistry.SPECIES_CHISS;
	}

	@Override
	public SpeciesVariable[] getVariables()
	{
		return new SpeciesVariable[] { VAR_EYEBROWS, VAR_HAIR };
	}

	@Override
	@Environment(EnvType.CLIENT)
	public Collection<Identifier> getTextureStack(SwgSpecies species)
	{
		var stack = new ArrayList<Identifier>();
		stack.add(getGenderedTexture(this, "skin"));
		stack.add(getGenderedTexture(this, VAR_EYEBROWS));
		stack.add(getGenderedGlobalTexture(gender, "clothes"));
		stack.add(getTexture(this, VAR_HAIR));
		return stack;
	}
}
