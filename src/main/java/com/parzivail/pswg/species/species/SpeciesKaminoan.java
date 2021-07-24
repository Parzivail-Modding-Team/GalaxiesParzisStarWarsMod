package com.parzivail.pswg.species.species;

import com.parzivail.pswg.container.SwgSpeciesRegistry;
import com.parzivail.pswg.species.SpeciesVariable;
import com.parzivail.pswg.species.SwgSpecies;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Collection;

public class SpeciesKaminoan extends SwgSpecies
{
	private static final SpeciesVariable VAR_BODY = new SpeciesVariable(SwgSpeciesRegistry.SPECIES_KAMINOAN,
	                                                             "body",
	                                                             "white",
	                                                             "blue",
	                                                             "purple",
	                                                             "white"
	);

	public SpeciesKaminoan(String serialized)
	{
		super(serialized);
	}

	@Override
	public Identifier getSlug()
	{
		return SwgSpeciesRegistry.SPECIES_KAMINOAN;
	}

	@Override
	public SpeciesVariable[] getVariables()
	{
		return new SpeciesVariable[] { VAR_BODY };
	}

	@Override
	@Environment(EnvType.CLIENT)
	public Collection<Identifier> getTextureStack(PlayerEntity player, SwgSpecies species)
	{
		var stack = new ArrayList<Identifier>();
//		stack.add(getGenderedTexture(this, getVariable(VAR_BODY)));
//		stack.add(getGenderedTexture(this, "clothes"));
//		stack.add(getTexture(this, "eyes"));
		return stack;
	}
}
