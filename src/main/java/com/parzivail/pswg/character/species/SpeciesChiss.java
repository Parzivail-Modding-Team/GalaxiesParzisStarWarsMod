package com.parzivail.pswg.character.species;

import com.parzivail.pswg.character.SpeciesStringVariable;
import com.parzivail.pswg.character.SpeciesVariable;
import com.parzivail.pswg.character.SwgSpecies;
import com.parzivail.pswg.container.SwgSpeciesRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Collection;

public class SpeciesChiss extends SwgSpecies
{
	protected static final SpeciesVariable VAR_EYEBROWS = new SpeciesStringVariable(
			SwgSpeciesRegistry.SPECIES_HUMANOID,
			"eyebrows",
			"black",
			"black",
			"white"
	);
	protected static final SpeciesVariable VAR_HAIR = new SpeciesStringVariable(
			SwgSpeciesRegistry.SPECIES_HUMANOID,
			"hair",
			"brown_1",
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
	public Collection<Identifier> getTextureStack(PlayerEntity player, SwgSpecies species)
	{
		var stack = new ArrayList<Identifier>();
		stack.add(getGenderedTexture(this, "skin"));
		stack.add(getGenderedTexture(this, VAR_EYEBROWS));
		stack.add(getClothes(player, gender));
		stack.add(getTexture(this, VAR_HAIR));
		return stack;
	}
}
