package com.parzivail.pswg.species;

import com.parzivail.pswg.container.SwgSpeciesRegistry;
import com.parzivail.pswg.species.species.SpeciesTogruta;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class SwgSpecies
{
	private static final String VARIABLE_EQUALS = "=";
	private static final String VARIABLE_SEPARATOR = ",";
	private static final String MODEL_SEPARATOR = ";";

	public static Identifier getSpeciesSlug(String serializedSpecies)
	{
		var parts = serializedSpecies.split(MODEL_SEPARATOR);
		return SpeciesGender.stripGender(parts[0]);
	}

	protected static Identifier getTexture(SwgSpecies species, SpeciesVariable texture)
	{
		return getTexture(texture.getSpeciesSlug(), texture.getName() + "/" + species.getVariable(texture));
	}

	protected static Identifier getGenderedTexture(SwgSpecies species, SpeciesVariable texture)
	{
		return getTexture(SpeciesGender.toModel(texture.getSpeciesSlug(), species.gender), texture.getName() + "/" + species.getVariable(texture));
	}

	protected static Identifier getGenderedTexture(SwgSpecies species, String texture)
	{
		return getTexture(SpeciesGender.toModel(species), texture);
	}

	protected static Identifier getGlobalTexture(String texture)
	{
		return getTexture(SwgSpeciesRegistry.SPECIES_GLOBAL, texture);
	}

	protected static Identifier getGenderedGlobalTexture(SpeciesGender gender, String texture)
	{
		return getTexture(SpeciesGender.toModel(SwgSpeciesRegistry.SPECIES_GLOBAL, gender), texture);
	}

	private static Identifier getTexture(Identifier slug, String texture)
	{
		return new Identifier(slug.getNamespace(), "textures/species/" + slug.getPath() + "/" + texture + ".png");
	}

	protected final Map<String, String> variables = new HashMap<>();
	protected Identifier model;
	protected SpeciesGender gender;

	public SwgSpecies()
	{
		setDefaultVariables();
	}

	public SwgSpecies(String serialized)
	{
		this();
		if (serialized == null)
			return;

		var parts = serialized.split(MODEL_SEPARATOR);

		this.model = new Identifier(parts[0]);
		this.gender = SpeciesGender.fromModel(parts[0]);

		if (parts.length > 1)
		{
			var variables = parts[1].split(VARIABLE_SEPARATOR);

			for (var variable : variables)
			{
				var pairParts = variable.split(VARIABLE_EQUALS, 2);
				this.variables.put(pairParts[0], pairParts[1]);
			}
		}
	}

	public abstract Identifier getSlug();

	public abstract SpeciesVariable[] getVariables();

	@Environment(EnvType.CLIENT)
	public abstract Collection<Identifier> getTextureStack();

	public void setDefaultVariables()
	{
		gender = SpeciesGender.MALE;
		for (var variable : getVariables())
			setVariable(variable, variable.getDefaultValue());
	}

	public Identifier getModel()
	{
		return model;
	}

	public SpeciesGender getGender()
	{
		return gender;
	}

	public void setGender(SpeciesGender gender)
	{
		this.gender = gender;
	}

	public String getVariable(SpeciesVariable variable)
	{
		return variables.get(variable.getName());
	}

	public void setVariable(SpeciesVariable variable, String value)
	{
		variables.put(variable.getName(), value);
	}

	public String serialize()
	{
		var variablePairs = variables
				.entrySet()
				.stream()
				.map(variable -> variable.getKey() + VARIABLE_EQUALS + variable.getValue())
				.collect(Collectors.joining(VARIABLE_SEPARATOR));

		return SpeciesGender.toModel(this) + MODEL_SEPARATOR + variablePairs;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		var that = (SpeciesTogruta)o;

		if (gender != that.gender)
			return false;
		return variables.equals(that.variables);
	}

	@Override
	public int hashCode()
	{
		var result = getSlug().hashCode();
		result = 31 * result + gender.hashCode();
		result = 31 * result + variables.hashCode();
		return result;
	}

	public boolean isSameSpecies(SwgSpecies other)
	{
		if (other == null)
			return false;

		return getSlug().equals(other.getSlug());
	}

	public void copy(SwgSpecies species)
	{
		this.setDefaultVariables();
		for (var pair : species.variables.entrySet())
			this.variables.put(pair.getKey(), pair.getValue());

		this.gender = species.gender;
	}
}
