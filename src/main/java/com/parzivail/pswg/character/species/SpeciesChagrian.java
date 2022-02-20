package com.parzivail.pswg.character.species;

import com.parzivail.pswg.character.SpeciesVariable;
import com.parzivail.pswg.character.SwgSpecies;
import com.parzivail.pswg.container.SwgSpeciesRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Collection;

public class SpeciesChagrian extends SwgSpecies
{
	private static final SpeciesVariable VAR_BODY = new SpeciesVariable(SwgSpeciesRegistry.SPECIES_CHAGRIAN,
	                                                             "body",
	                                                             "blue",
	                                                             "black",
	                                                             "blue"
	);

	public SpeciesChagrian(String serialized)
	{
		super(serialized);
	}

	@Override
	public Identifier getSlug()
	{
		return SwgSpeciesRegistry.SPECIES_CHAGRIAN;
	}

	@Override
	public SpeciesVariable[] getVariables()
	{
		return new SpeciesVariable[] { VAR_BODY, VAR_HUMANOID_EYEBROWS, VAR_HUMANOID_EYE_COLOR };
	}

	@Override
	@Environment(EnvType.CLIENT)
	public Collection<Identifier> getTextureStack(PlayerEntity player, SwgSpecies species)
	{
		var stack = new ArrayList<Identifier>();
		stack.add(getGenderedTexture(this, VAR_BODY));
		stack.add(getGenderedTexture(this, VAR_HUMANOID_EYEBROWS));
		stack.add(getClothes(player, gender));
		stack.add(tint(getGlobalTexture("eyes"), this, VAR_HUMANOID_EYE_COLOR));
		return stack;
	}
}
