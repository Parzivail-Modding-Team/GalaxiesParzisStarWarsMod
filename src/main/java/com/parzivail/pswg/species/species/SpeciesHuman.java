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
		return new SpeciesVariable[] { VAR_SKINTONE, VAR_HUMANOID_EYEBROWS, VAR_HUMANOID_HAIR };
	}

	@Override
	@Environment(EnvType.CLIENT)
	public Collection<Identifier> getTextureStack(PlayerEntity player, SwgSpecies species)
	{
		var stack = new ArrayList<Identifier>();
		stack.add(getGenderedTexture(this, VAR_SKINTONE));
		stack.add(getGenderedTexture(this, VAR_HUMANOID_EYEBROWS));
		stack.add(getClothes(player, gender));
		stack.add(tint(getGlobalTexture("eyes"), 0xFFFFFF));
		stack.add(getTexture(this, VAR_HUMANOID_HAIR));
		return stack;
	}
}
