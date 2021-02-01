package com.parzivail.pswg.species;

import com.google.common.collect.ImmutableMap;
import com.parzivail.pswg.Resources;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SpeciesTogruta extends SwgSpecies
{
	private static final SpeciesVariable VAR_BODY = new SpeciesVariable(
			Resources.identifier("body"),
			"green",
			"orange",
			"purple",
			"red"
	);

	private static final SpeciesVariable VAR_MONTRALS = new SpeciesVariable(
			Resources.identifier("montral"),
			"1",
			"2",
			"3",
			"4",
			"5"
	);

	private static final Map<Identifier, String> DEFAULT_VARIABLES = ImmutableMap
			.<Identifier, String>builder()
			.put(VAR_BODY.getName(), VAR_BODY.getPossibleValues()[0])
			.put(VAR_MONTRALS.getName(), VAR_MONTRALS.getPossibleValues()[0])
			.build();

	private final Identifier model;
	private SpeciesGender gender;
	private Map<Identifier, String> variables;

	public SpeciesTogruta(Identifier model)
	{
		this.model = model;
		this.gender = SpeciesGender.MALE;
		this.variables = getDefaultVariables();
	}

	public Identifier getModel()
	{
		return model;
	}

	@Override
	public Identifier getSlug()
	{
		return Resources.identifier("togruta");
	}

	@Override
	public SpeciesGender getGender()
	{
		return gender;
	}

	public String getVariable(SpeciesVariable variable)
	{
		return variables.get(variable.getName());
	}

	@Override
	public Identifier serialize()
	{
		StringBuilder b = new StringBuilder();

		Identifier slug = SpeciesGender.toSlug(this);

		b.append(slug.getPath());

		for (Map.Entry<Identifier, String> variable : variables.entrySet())
		{
			b.append(',');
			b.append(variable.getKey().toString());
			b.append('=');
			b.append(variable.getValue());
		}

		return new Identifier(slug.getNamespace(), b.toString());
	}

	@Override
	public void deserialize(Identifier identifier)
	{
		this.variables = getDefaultVariables();

		String[] variables = identifier.getPath().split(",");

		String genderSlug = variables[0];
		this.gender = SpeciesGender.fromSlug(genderSlug);

		for (int i = 1; i < variables.length; i++)
		{
			String[] pairParts = variables[i].split("=", 2);
			this.variables.put(new Identifier(pairParts[0]), pairParts[1]);
		}
	}

	@Override
	public Map<Identifier, String> getDefaultVariables()
	{
		return new HashMap<>(DEFAULT_VARIABLES);
	}

	@Override
	public SpeciesVariable[] getVariables()
	{
		return new SpeciesVariable[] { VAR_BODY, VAR_MONTRALS };
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		SpeciesTogruta that = (SpeciesTogruta)o;

		if (gender != that.gender)
			return false;
		return variables.equals(that.variables);
	}

	@Override
	public int hashCode()
	{
		int result = gender.hashCode();
		result = 31 * result + variables.hashCode();
		return result;
	}

	@Override
	@Environment(EnvType.CLIENT)
	public Collection<Identifier> getTextureStack()
	{
		ArrayList<Identifier> stack = new ArrayList<>();
		stack.add(getGenderedTexture(this, getVariable(VAR_BODY)));
		stack.add(getGenderedGlobalTexture(gender, "clothes"));
		stack.add(getTexture(this, "montrals/" + getVariable(VAR_MONTRALS)));
		stack.add(getGenderedGlobalTexture(gender, "eyes"));
		return stack;
	}
}
