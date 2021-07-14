package com.parzivail.pswg.species.species;

import com.parzivail.pswg.container.SwgSpeciesRegistry;
import com.parzivail.pswg.species.SpeciesVariable;
import com.parzivail.pswg.species.SwgSpecies;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Collection;

public class SpeciesBothan extends SwgSpecies
{
//	private static final SpeciesVariable VAR_BODY = new SpeciesVariable(
//			Resources.identifier("body"),
//			"white",
//			"green",
//			"pink",
//			"white"
//	);

	public SpeciesBothan(String serialized)
	{
		super(serialized);
	}

	@Override
	public Identifier getSlug()
	{
		return SwgSpeciesRegistry.SPECIES_BOTHAN;
	}

	@Override
	public SpeciesVariable[] getVariables()
	{
		return new SpeciesVariable[] { /*VAR_BODY*/ };
	}

	@Override
	@Environment(EnvType.CLIENT)
	public Collection<Identifier> getTextureStack()
	{
		var stack = new ArrayList<Identifier>();
//		stack.add(getGenderedTexture(this, "base"));
//		stack.add(getGenderedTexture(this, getVariable(VAR_BODY)));
//		stack.add(getGenderedGlobalTexture(gender, "clothes"));
		return stack;
	}
}
