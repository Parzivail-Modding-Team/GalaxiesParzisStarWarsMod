package com.parzivail.pswg.character;

import com.google.common.hash.Hashing;
import com.parzivail.pswg.Client;
import com.parzivail.pswg.api.HumanoidCustomizationOptions;
import com.parzivail.pswg.container.SwgSpeciesRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public abstract class SwgSpecies
{
	private static final String VARIABLE_EQUALS = "=";
	private static final String VARIABLE_SEPARATOR = ",";
	private static final String MODEL_SEPARATOR = ";";

	protected static final SpeciesVariable VAR_HUMANOID_EYEBROWS = new BakedSpeciesVariable(SwgSpeciesRegistry.METASPECIES_HUMANOID, "humanoid_eyebrows");
	protected static final SpeciesVariable VAR_HUMANOID_HAIR = new BakedSpeciesVariable(SwgSpeciesRegistry.METASPECIES_HUMANOID, "humanoid_hair");
	protected static final SpeciesVariable VAR_HUMANOID_SCARS = new BakedSpeciesVariable(SwgSpeciesRegistry.METASPECIES_HUMANOID, "humanoid_scars");
	protected static final SpeciesVariable VAR_HUMANOID_TATTOOS = new BakedSpeciesVariable(SwgSpeciesRegistry.METASPECIES_HUMANOID, "humanoid_tattoos");

	protected static final SpeciesVariable VAR_HUMANOID_CLOTHES_UNDERLAYER = new BakedSpeciesVariable(SwgSpeciesRegistry.METASPECIES_HUMANOID, "humanoid_clothes_underlayer");
	protected static final SpeciesVariable VAR_HUMANOID_CLOTHES_TOPS = new BakedSpeciesVariable(SwgSpeciesRegistry.METASPECIES_HUMANOID, "humanoid_clothes_top");
	protected static final SpeciesVariable VAR_HUMANOID_CLOTHES_BOTTOMS = new BakedSpeciesVariable(SwgSpeciesRegistry.METASPECIES_HUMANOID, "humanoid_clothes_bottom");
	protected static final SpeciesVariable VAR_HUMANOID_CLOTHES_BELTS = new BakedSpeciesVariable(SwgSpeciesRegistry.METASPECIES_HUMANOID, "humanoid_clothes_belt");
	protected static final SpeciesVariable VAR_HUMANOID_CLOTHES_BOOTS = new BakedSpeciesVariable(SwgSpeciesRegistry.METASPECIES_HUMANOID, "humanoid_clothes_boots");
	protected static final SpeciesVariable VAR_HUMANOID_CLOTHES_GLOVES = new BakedSpeciesVariable(SwgSpeciesRegistry.METASPECIES_HUMANOID, "humanoid_clothes_gloves");
	protected static final SpeciesVariable VAR_HUMANOID_CLOTHES_ACCESSORIES = new BakedSpeciesVariable(SwgSpeciesRegistry.METASPECIES_HUMANOID, "humanoid_clothes_accessories");
	protected static final SpeciesVariable VAR_HUMANOID_CLOTHES_OUTERWEAR = new BakedSpeciesVariable(SwgSpeciesRegistry.METASPECIES_HUMANOID, "humanoid_clothes_outerwear");

	protected static final SpeciesColorVariable VAR_HUMANOID_EYE_COLOR = new SpeciesColorVariable(SwgSpeciesRegistry.METASPECIES_HUMANOID, "humanoid_eye_color", 0x226622);
	protected static final SpeciesColorVariable VAR_HUMANOID_TATTOO_COLOR = new SpeciesColorVariable(SwgSpeciesRegistry.METASPECIES_HUMANOID, "humanoid_tattoo_color", 0x665544);
	protected static final SpeciesColorVariable VAR_HUMANOID_HAIR_COLOR = new BakedSpeciesColorVariable(SwgSpeciesRegistry.METASPECIES_HUMANOID, "humanoid_hair_color");

	private static final HashMap<String, SpeciesVariable> VARIABLE_TABLE = Util.make(new HashMap<>(), (h) -> {
		h.put("humanoid_eyebrows", VAR_HUMANOID_EYEBROWS);
		h.put("humanoid_scars", VAR_HUMANOID_SCARS);
		h.put("humanoid_tattoos", VAR_HUMANOID_TATTOOS);
		h.put("humanoid_hair", VAR_HUMANOID_HAIR);
		h.put("humanoid_hair_color", VAR_HUMANOID_HAIR_COLOR);
		h.put("humanoid_clothes_underlayer", VAR_HUMANOID_CLOTHES_UNDERLAYER);
		h.put("humanoid_clothes_top", VAR_HUMANOID_CLOTHES_TOPS);
		h.put("humanoid_clothes_bottom", VAR_HUMANOID_CLOTHES_BOTTOMS);
		h.put("humanoid_clothes_belt", VAR_HUMANOID_CLOTHES_BELTS);
		h.put("humanoid_clothes_boots", VAR_HUMANOID_CLOTHES_BOOTS);
		h.put("humanoid_clothes_gloves", VAR_HUMANOID_CLOTHES_GLOVES);
		h.put("humanoid_clothes_accessories", VAR_HUMANOID_CLOTHES_ACCESSORIES);
		h.put("humanoid_clothes_outerwear", VAR_HUMANOID_CLOTHES_OUTERWEAR);
	});

	public static void registerHumanoidOptions(String key, HumanoidCustomizationOptions value)
	{
		if (VARIABLE_TABLE.containsKey(key))
		{
			if (VARIABLE_TABLE.get(key) instanceof BakedSpeciesVariable bakedVar)
				bakedVar.bakeWith(value);
			else if (VARIABLE_TABLE.get(key) instanceof BakedSpeciesColorVariable bakedColorVar)
				bakedColorVar.bakeWith(value);
		}
	}

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
		return getTexture(toModel(texture.getDefiningSpeciesSlug(), species.gender), texture.getName() + "/" + species.getVariable(texture));
	}

	protected static Identifier getGenderedTexture(SwgSpecies species, String texture)
	{
		return getTexture(toModel(species), texture);
	}

	protected static Identifier getGlobalTexture(String texture)
	{
		return getTexture(SwgSpeciesRegistry.METASPECIES_GLOBAL, texture);
	}

	protected static Identifier getClothes(SwgSpecies species, LivingEntity player)
	{
		return Client.remoteSkinTextureProvider.getId(
				String.format("character/%s", player.getUuidAsString()),
				() -> SwgSpecies.getClothingStack(species, player)
		);
	}

	private static String serializeVariablePairs(Map<String, String> variables)
	{
		return variables
				.entrySet()
				.stream()
				.map(variable -> variable.getKey() + VARIABLE_EQUALS + variable.getValue())
				.collect(Collectors.joining(VARIABLE_SEPARATOR));
	}

	private static String digestVariables(SwgSpecies species, SpeciesVariable... variables)
	{
		var variableString = Arrays.stream(variables)
		                           .map(variable -> variable.getName() + VARIABLE_EQUALS + species.getVariable(variable))
		                           .collect(Collectors.joining(VARIABLE_SEPARATOR));

		return Hashing.sha256()
		              .hashString(variableString, StandardCharsets.UTF_8)
		              .toString();
	}

	private static Identifier getClothingStack(SwgSpecies species, LivingEntity player)
	{
		var digest = SwgSpecies.digestVariables(species,
		                                        VAR_HUMANOID_CLOTHES_UNDERLAYER,
		                                        VAR_HUMANOID_CLOTHES_TOPS,
		                                        VAR_HUMANOID_CLOTHES_BOTTOMS,
		                                        VAR_HUMANOID_CLOTHES_BELTS,
		                                        VAR_HUMANOID_CLOTHES_BOOTS,
		                                        VAR_HUMANOID_CLOTHES_GLOVES,
		                                        VAR_HUMANOID_CLOTHES_ACCESSORIES,
		                                        VAR_HUMANOID_CLOTHES_OUTERWEAR
		);
		return Client.stackedTextureProvider.getId(
				String.format("clothing/%s", digest),
				() -> getGenderedGlobalTexture(species.getGender(), "clothes"),
				() -> SwgSpecies.createClothingStack(species, player)
		);
	}

	private static Collection<Identifier> createClothingStack(SwgSpecies species, LivingEntity player)
	{
		var stack = new ArrayList<Identifier>();
		if (species.getVariable(VAR_HUMANOID_CLOTHES_UNDERLAYER).equals(SpeciesVariable.NONE))
		{
			stack.add(getTexture(species, VAR_HUMANOID_CLOTHES_TOPS));
			stack.add(getTexture(species, VAR_HUMANOID_CLOTHES_BOTTOMS));
		}
		else
			stack.add(getTexture(species, VAR_HUMANOID_CLOTHES_UNDERLAYER));
		appendOptionalLayer(stack, species, VAR_HUMANOID_CLOTHES_BELTS);
		appendOptionalLayer(stack, species, VAR_HUMANOID_CLOTHES_BOOTS);
		appendOptionalLayer(stack, species, VAR_HUMANOID_CLOTHES_GLOVES);
		appendOptionalLayer(stack, species, VAR_HUMANOID_CLOTHES_ACCESSORIES);
		appendOptionalLayer(stack, species, VAR_HUMANOID_CLOTHES_OUTERWEAR);
		return stack;
	}

	protected static Identifier getDefaultClothes(PlayerEntity player, SpeciesGender gender)
	{
		return Client.remoteSkinTextureProvider.getId(
				String.format("character/%s", player.getUuidAsString()),
				() -> getGenderedGlobalTexture(gender, "clothes")
		);
	}

	protected static Identifier getGenderedGlobalTexture(SpeciesGender gender, String texture)
	{
		return getTexture(toModel(SwgSpeciesRegistry.METASPECIES_GLOBAL, gender), texture);
	}

	protected static Identifier getTexture(Identifier slug, String texture)
	{
		return slug.withPath(path -> "textures/species/" + path + "/" + texture + ".png");
	}

	protected static Identifier tint(Identifier texture, SwgSpecies species, SpeciesColorVariable variable)
	{
		return Client.tintTexture(texture, Integer.parseUnsignedInt(species.getVariable(variable), 16));
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

		this.model = Identifier.of(parts[0]);
		this.gender = fromModel(parts[0]);

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

	public static SpeciesGender fromModel(String genderedSlug)
	{
		var parts = genderedSlug.split(SpeciesGender.GENDER_SEPARATOR, 2);

		if (parts.length == 2 && parts[1].equals(SpeciesGender.FEMALE.getSlug()))
			return SpeciesGender.FEMALE;

		return SpeciesGender.MALE;
	}

	public static Identifier toModel(SwgSpecies species)
	{
		var slug = species.getSlug();
		var gender = species.getGender();
		return toModel(slug, gender);
	}

	public static Identifier toModel(Identifier species, SpeciesGender gender)
	{
		return species.withSuffixedPath(SpeciesGender.GENDER_SEPARATOR + gender.getSlug());
	}

	public abstract Identifier getSlug();

	public abstract SpeciesVariable[] getVariables();

	@Environment(EnvType.CLIENT)
	public abstract Collection<Identifier> getTextureStack(LivingEntity player);

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

	public abstract SpeciesVariable getVariableReference(String variableName);

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
		var variablePairs = serializeVariablePairs(variables);
		return toModel(this) + MODEL_SEPARATOR + variablePairs;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		var that = (SwgSpecies)o;

		if (gender != that.gender)
			return false;
		return variables.equals(that.variables);
	}

	@Override
	public int hashCode()
	{
		return digest().hashCode();
	}

	public String digest()
	{
		return Hashing.sha256()
		              .hashString(serialize(), StandardCharsets.UTF_8)
		              .toString();
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
