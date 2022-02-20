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

public class SpeciesTogruta extends SwgSpecies
{
	private static final SpeciesVariable VAR_BODY = new DatapackedSpeciesVariable(SwgSpeciesRegistry.SPECIES_TOGRUTA, "body");
	private static final SpeciesVariable VAR_FACE = new DatapackedSpeciesVariable(SwgSpeciesRegistry.SPECIES_TOGRUTA, "face");
	private static final SpeciesVariable VAR_EYEBROWS = new DatapackedSpeciesVariable(SwgSpeciesRegistry.SPECIES_TOGRUTA, "eyebrows");
	private static final SpeciesVariable VAR_LOWER_MONTRAL = new DatapackedSpeciesVariable(SwgSpeciesRegistry.SPECIES_TOGRUTA, "lower_montral");
	private static final SpeciesVariable VAR_UPPER_MONTRAL = new DatapackedSpeciesVariable(SwgSpeciesRegistry.SPECIES_TOGRUTA, "upper_montral");

	public SpeciesTogruta(String serialized)
	{
		super(serialized);
	}

	@Override
	public Identifier getSlug()
	{
		return SwgSpeciesRegistry.SPECIES_TOGRUTA;
	}

	@Override
	public SpeciesVariable[] getVariables()
	{
		return new SpeciesVariable[] { VAR_BODY, VAR_FACE, VAR_EYEBROWS, VAR_LOWER_MONTRAL, VAR_UPPER_MONTRAL, VAR_HUMANOID_EYE_COLOR };
	}

	@Override
	@Environment(EnvType.CLIENT)
	public Collection<Identifier> getTextureStack(PlayerEntity player)
	{
		var stack = new ArrayList<Identifier>();
		stack.add(getGenderedTexture(this, VAR_BODY));
		stack.add(getGenderedTexture(this, VAR_LOWER_MONTRAL));

		if (SpeciesVariable.isNotEmpty(this, VAR_UPPER_MONTRAL))
			stack.add(getGenderedTexture(this, VAR_UPPER_MONTRAL));

		if (SpeciesVariable.isNotEmpty(this, VAR_FACE))
			stack.add(getTexture(this, VAR_FACE));

		stack.add(tint(getGlobalTexture("eyes"), this, VAR_HUMANOID_EYE_COLOR));
		stack.add(getGenderedTexture(this, VAR_EYEBROWS));
		stack.add(getClothes(player, gender));
		return stack;
	}
}
