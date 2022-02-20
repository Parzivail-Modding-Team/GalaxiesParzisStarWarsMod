package com.parzivail.pswg.character;

import com.parzivail.pswg.Client;
import com.parzivail.pswg.character.species.SpeciesTogruta;
import com.parzivail.pswg.container.SwgSpeciesRegistry;
import com.parzivail.util.client.ColorUtil;
import com.parzivail.util.data.TintedIdentifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
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

	protected static final SpeciesVariable VAR_HUMANOID_EYEBROWS = new SpeciesStringVariable(
			SwgSpeciesRegistry.SPECIES_HUMANOID,
			"eyebrows",
			"black",
			"black",
			"blonde",
			"brown",
			"white"
	);

	protected static final SpeciesVariable VAR_HUMANOID_HAIR = new SpeciesStringVariable(
			SwgSpeciesRegistry.SPECIES_HUMANOID,
			"hair",
			"brown_1",
			"black_1",
			"black_2",
			"black_3",
			"black_4",
			"blonde_1",
			"blonde_2",
			"blonde_3",
			"blonde_4",
			"brown_1",
			"brown_2",
			"brown_3",
			"brown_4",
			"gray_1",
			"gray_2",
			"gray_3",
			"gray_4"
	);

	protected static final SpeciesColorVariable VAR_HUMANOID_EYE_COLOR = new SpeciesColorVariable(
			SwgSpeciesRegistry.SPECIES_HUMANOID,
			"eye_color",
			0xFFFFFF
	);

	public static Identifier getSpeciesSlug(String serializedSpecies)
	{
		var parts = serializedSpecies.split(MODEL_SEPARATOR);
		return SpeciesGender.stripGender(parts[0]);
	}

	protected static Identifier getTexture(SwgSpecies species, SpeciesVariable texture)
	{
		return getTexture(texture.getDefiningSpeciesSlug(), texture.getName() + "/" + species.getVariable(texture));
	}

	protected static Identifier getGenderedTexture(SwgSpecies species, SpeciesVariable texture)
	{
		return getTexture(SpeciesGender.toModel(texture.getDefiningSpeciesSlug(), species.gender), texture.getName() + "/" + species.getVariable(texture));
	}

	protected static Identifier getGenderedTexture(SwgSpecies species, String texture)
	{
		return getTexture(SpeciesGender.toModel(species), texture);
	}

	protected static Identifier getGlobalTexture(String texture)
	{
		return getTexture(SwgSpeciesRegistry.SPECIES_GLOBAL, texture);
	}

	protected static Identifier getClothes(PlayerEntity player, SpeciesGender gender)
	{
		return Client.remoteTextureProvider.getId(
				String.format("character/%s", player.getUuidAsString()),
				() -> getGenderedGlobalTexture(gender, "clothes")
		);
	}

	protected static Identifier getGenderedGlobalTexture(SpeciesGender gender, String texture)
	{
		return getTexture(SpeciesGender.toModel(SwgSpeciesRegistry.SPECIES_GLOBAL, gender), texture);
	}

	private static Identifier getTexture(Identifier slug, String texture)
	{
		return new Identifier(slug.getNamespace(), "textures/species/" + slug.getPath() + "/" + texture + ".png");
	}

	protected static Identifier tint(Identifier texture, SwgSpecies species, SpeciesColorVariable variable)
	{
		return Client.tintedTextureProvider.getId(
				texture.getNamespace() + "/" + texture.getPath() + "/" + species.getVariable(variable),
				() -> texture,
				() -> new TintedIdentifier(texture.getNamespace(), texture.getPath(), ColorUtil.rgbaToAbgr(Integer.parseUnsignedInt(species.getVariable(variable), 16)))
		);
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
	public abstract Collection<Identifier> getTextureStack(PlayerEntity player, SwgSpecies species);

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
		this.variables.putAll(species.variables);

		this.gender = species.gender;
	}
}
