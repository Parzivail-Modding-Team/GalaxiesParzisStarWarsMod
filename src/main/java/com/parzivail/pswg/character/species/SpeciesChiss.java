package com.parzivail.pswg.character.species;

import com.parzivail.pswg.character.DatapackedSpeciesVariable;
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
	protected static final SpeciesVariable VAR_EYEBROWS = new DatapackedSpeciesVariable(SwgSpeciesRegistry.SPECIES_CHISS, "eyebrows");
	protected static final SpeciesVariable VAR_HAIR = new DatapackedSpeciesVariable(SwgSpeciesRegistry.SPECIES_CHISS, "hair");

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
	public Collection<Identifier> getTextureStack(PlayerEntity player)
	{
		var stack = new ArrayList<Identifier>();
		stack.add(getGenderedTexture(this, "skin"));
		stack.add(getGenderedTexture(this, VAR_EYEBROWS));
		stack.add(getClothes(player, gender));
		stack.add(getTexture(this, VAR_HAIR));
		return stack;
	}
}
