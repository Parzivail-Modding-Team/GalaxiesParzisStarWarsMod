package com.parzivail.pswg.species;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.container.SwgSpeciesRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Collection;

public class SpeciesTogruta extends SwgSpecies
{
	private static final SpeciesVariable VAR_BODY = new SpeciesVariable(
			Resources.identifier("body"),
			"green",
			"green",
			"orange",
			"purple",
			"red"
	);

	private static final SpeciesVariable VAR_MONTRALS = new SpeciesVariable(
			Resources.identifier("montral"),
			"1",
			"1",
			"2",
			"3",
			"4",
			"5"
	);

	public SpeciesTogruta()
	{
		this.gender = SpeciesGender.FEMALE;
		this.model = SpeciesGender.toModel(this);

		setVariable(VAR_BODY, "red");
		setVariable(VAR_MONTRALS, "3");
	}

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
		return new SpeciesVariable[] { VAR_BODY, VAR_MONTRALS };
	}

	@Override
	@Environment(EnvType.CLIENT)
	public Collection<Identifier> getTextureStack()
	{
		ArrayList<Identifier> stack = new ArrayList<>();
		stack.add(getGenderedTexture(this, getVariable(VAR_BODY)));
		stack.add(getGenderedGlobalTexture(gender, "clothes"));
		stack.add(getTexture(this, "montrals/" + getVariable(VAR_MONTRALS)));
		stack.add(getTexture(this, "eyes"));
		return stack;
	}
}
