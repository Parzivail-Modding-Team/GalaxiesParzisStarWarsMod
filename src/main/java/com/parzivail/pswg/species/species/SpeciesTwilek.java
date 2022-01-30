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

public class SpeciesTwilek extends SwgSpecies
{
	private static final SpeciesVariable VAR_BODY = new SpeciesVariable(SwgSpeciesRegistry.SPECIES_TWILEK,
			"body",
			"green",
			"blue",
			"cyan",
			"green",
			"lime",
			"magenta",
			"orange",
			"pink",
			"purple",
			"red",
			"white",
			"yellow"
	);

	public SpeciesTwilek(String serialized)
	{
		super(serialized);
	}

	@Override
	public Identifier getSlug()
	{
		return SwgSpeciesRegistry.SPECIES_TWILEK;
	}

	@Override
	public SpeciesVariable[] getVariables()
	{
		return new SpeciesVariable[] { VAR_BODY, SwgSpecies.VAR_HUMANOID_EYEBROWS };
	}

	@Override
	@Environment(EnvType.CLIENT)
	public Collection<Identifier> getTextureStack(PlayerEntity player, SwgSpecies species)
	{
		var stack = new ArrayList<Identifier>();
		stack.add(getGenderedTexture(this, VAR_BODY));
		stack.add(getGenderedTexture(this, VAR_HUMANOID_EYEBROWS));
		stack.add(getClothes(player, gender));
		stack.add(getGlobalTexture("eyes"));
		return stack;
	}
}
