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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class SwgSpecies
{
	private static final String VARIABLE_EQUALS = "=";
	private static final String VARIABLE_SEPARATOR = ",";
	private static final String MODEL_SEPARATOR = ";";

	protected static final SpeciesVariable VAR_HUMANOID_EYEBROWS = new DatapackedSpeciesVariable(SwgSpeciesRegistry.SPECIES_HUMANOID, "eyebrows");
	protected static final SpeciesVariable VAR_HUMANOID_HAIR = new DatapackedSpeciesVariable(SwgSpeciesRegistry.SPECIES_HUMANOID, "hair");
	protected static final SpeciesVariable VAR_HUMANOID_SCARS = new DatapackedSpeciesVariable(SwgSpeciesRegistry.SPECIES_HUMANOID, "scars");
	protected static final SpeciesVariable VAR_HUMANOID_TATTOOS = new DatapackedSpeciesVariable(SwgSpeciesRegistry.SPECIES_HUMANOID, "tattoos");

	protected static final SpeciesVariable VAR_HUMANOID_CLOTHES_UNDERLAYER = new DatapackedSpeciesVariable(SwgSpeciesRegistry.SPECIES_HUMANOID, "clothes_underlayer");
	protected static final SpeciesVariable VAR_HUMANOID_CLOTHES_TOPS = new DatapackedSpeciesVariable(SwgSpeciesRegistry.SPECIES_HUMANOID, "clothes_top");
	protected static final SpeciesVariable VAR_HUMANOID_CLOTHES_BOTTOMS = new DatapackedSpeciesVariable(SwgSpeciesRegistry.SPECIES_HUMANOID, "clothes_bottom");
	protected static final SpeciesVariable VAR_HUMANOID_CLOTHES_BELTS = new DatapackedSpeciesVariable(SwgSpeciesRegistry.SPECIES_HUMANOID, "clothes_belt");
	protected static final SpeciesVariable VAR_HUMANOID_CLOTHES_BOOTS = new DatapackedSpeciesVariable(SwgSpeciesRegistry.SPECIES_HUMANOID, "clothes_boots");
	protected static final SpeciesVariable VAR_HUMANOID_CLOTHES_GLOVES = new DatapackedSpeciesVariable(SwgSpeciesRegistry.SPECIES_HUMANOID, "clothes_gloves");
	protected static final SpeciesVariable VAR_HUMANOID_CLOTHES_ACCESSORIES = new DatapackedSpeciesVariable(SwgSpeciesRegistry.SPECIES_HUMANOID, "clothes_accessories");
	protected static final SpeciesVariable VAR_HUMANOID_CLOTHES_OUTERWEAR = new DatapackedSpeciesVariable(SwgSpeciesRegistry.SPECIES_HUMANOID, "clothes_outerwear");

	protected static final SpeciesColorVariable VAR_HUMANOID_EYE_COLOR = new SpeciesColorVariable(SwgSpeciesRegistry.SPECIES_HUMANOID, "eye_color", 0x226622);
	protected static final SpeciesColorVariable VAR_HUMANOID_TATTOO_COLOR = new SpeciesColorVariable(SwgSpeciesRegistry.SPECIES_HUMANOID, "tattoo_color", 0x665544);
	protected static final SpeciesColorVariable VAR_HUMANOID_HAIR_COLOR = new SpeciesColorVariable(SwgSpeciesRegistry.SPECIES_HUMANOID, "hair_color", 0x905424);

	public static Identifier getSpeciesSlug(String serializedSpecies)
	{
		var parts = serializedSpecies.split(MODEL_SEPARATOR);
		return SpeciesGender.stripGender(parts[0]);
	}

	protected static void appendOptionalLayer(Collection<Identifier> stack, SwgSpecies species, SpeciesVariable variable)
	{
		if (species.getVariable(variable).equals(SpeciesVariable.NONE))
			return;
		stack.add(getTexture(species, variable));
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

	protected static Identifier getClothes(SwgSpecies species, PlayerEntity player)
	{
		return Client.remoteTextureProvider.getId(
				String.format("character/%s", player.getUuidAsString()),
				() -> SwgSpecies.getClothingStack(species, player)
		);
	}

	private static int hashVariableValues(SwgSpecies species, SpeciesVariable... variables)
	{
		var hash = 0;
		for (var variable : variables)
			hash = 31 * hash + species.getVariable(variable).hashCode();
		return hash;
	}

	private static Identifier getClothingStack(SwgSpecies species, PlayerEntity player)
	{
		var hashCode = SwgSpecies.hashVariableValues(species,
		                                             VAR_HUMANOID_CLOTHES_TOPS,
		                                             VAR_HUMANOID_CLOTHES_BOTTOMS,
		                                             VAR_HUMANOID_CLOTHES_BELTS,
		                                             VAR_HUMANOID_CLOTHES_BOOTS,
		                                             VAR_HUMANOID_CLOTHES_GLOVES,
		                                             VAR_HUMANOID_CLOTHES_ACCESSORIES,
		                                             VAR_HUMANOID_CLOTHES_OUTERWEAR
		);
		return Client.stackedTextureProvider.getId(String.format("clothing/%08x", hashCode), () -> getGenderedGlobalTexture(species.getGender(), "clothes"), () -> SwgSpecies.createClothingStack(species, player));
	}

	private static Collection<Identifier> createClothingStack(SwgSpecies species, PlayerEntity player)
	{
		var stack = new ArrayList<Identifier>();
		stack.add(getTexture(species, VAR_HUMANOID_CLOTHES_TOPS));
		stack.add(getTexture(species, VAR_HUMANOID_CLOTHES_BOTTOMS));
		appendOptionalLayer(stack, species, VAR_HUMANOID_CLOTHES_BELTS);
		appendOptionalLayer(stack, species, VAR_HUMANOID_CLOTHES_BOOTS);
		appendOptionalLayer(stack, species, VAR_HUMANOID_CLOTHES_GLOVES);
		appendOptionalLayer(stack, species, VAR_HUMANOID_CLOTHES_ACCESSORIES);
		appendOptionalLayer(stack, species, VAR_HUMANOID_CLOTHES_OUTERWEAR);
		return stack;
	}

	protected static Identifier getDefaultClothes(PlayerEntity player, SpeciesGender gender)
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

	protected static Identifier getTexture(Identifier slug, String texture)
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
	public abstract Collection<Identifier> getTextureStack(PlayerEntity player);

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

	public float getScaleFactor()
	{
		return 1;
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
