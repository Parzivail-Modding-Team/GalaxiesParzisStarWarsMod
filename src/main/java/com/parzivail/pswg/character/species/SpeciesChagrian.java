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

public class SpeciesChagrian extends SwgSpecies
{
	private static final SpeciesVariable VAR_BODY = new DatapackedSpeciesVariable(SwgSpeciesRegistry.SPECIES_CHAGRIAN, "body");
	protected static final SpeciesVariable VAR_EYEBROWS = new DatapackedSpeciesVariable(SwgSpeciesRegistry.SPECIES_CHAGRIAN, "eyebrows");

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
		return new SpeciesVariable[] {
				VAR_BODY,
				VAR_HUMANOID_SCARS,
				VAR_HUMANOID_TATTOOS,
				VAR_HUMANOID_TATTOO_COLOR,
				VAR_EYEBROWS,
				VAR_HUMANOID_EYE_COLOR,
				VAR_HUMANOID_CLOTHES_UNDERLAYER,
				VAR_HUMANOID_CLOTHES_TOPS,
				VAR_HUMANOID_CLOTHES_BOTTOMS,
				VAR_HUMANOID_CLOTHES_BELTS,
				VAR_HUMANOID_CLOTHES_BOOTS,
				VAR_HUMANOID_CLOTHES_GLOVES,
				VAR_HUMANOID_CLOTHES_ACCESSORIES,
				VAR_HUMANOID_CLOTHES_OUTERWEAR
		};
	}

	@Override
	@Environment(EnvType.CLIENT)
	public Collection<Identifier> getTextureStack(PlayerEntity player)
	{
		var stack = new ArrayList<Identifier>();
		stack.add(getGenderedTexture(this, VAR_BODY));
		if (SpeciesVariable.isNotEmpty(this, VAR_HUMANOID_SCARS))
			stack.add(getTexture(this, VAR_HUMANOID_SCARS));
		if (SpeciesVariable.isNotEmpty(this, VAR_HUMANOID_TATTOOS))
			stack.add(tint(getTexture(this, VAR_HUMANOID_TATTOOS), this, VAR_HUMANOID_TATTOO_COLOR));

		stack.add(getGlobalTexture("eyes_whites"));
		stack.add(tint(getGlobalTexture("eyes"), this, VAR_HUMANOID_EYE_COLOR));
		stack.add(getGenderedTexture(this, VAR_HUMANOID_EYEBROWS));

		stack.add(getClothes(this, player));
		return stack;
	}
}
