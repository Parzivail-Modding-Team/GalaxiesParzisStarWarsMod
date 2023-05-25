package com.parzivail.pswg.character;

import com.google.common.collect.ImmutableList;
import com.parzivail.pswg.container.SwgSpeciesRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;

public class SpeciesBuilder
{
	@FunctionalInterface
	public interface TextureBuilderComponent
	{
		void addLayer(ArrayList<Identifier> stack, HashMap<String, SpeciesVariable> variables, SwgSpecies species, PlayerEntity player);
	}

	public static class TextureBuilderBuilder
	{
		private final ArrayList<TextureBuilderComponent> components = new ArrayList<>();

		public List<TextureBuilderComponent> build()
		{
			if (components.isEmpty())
				throw new RuntimeException("Layer renderer cannot be empty");
			return ImmutableList.copyOf(components);
		}

		private static SpeciesVariable getCheckedVariable(HashMap<String, SpeciesVariable> variables, String variableId)
		{
			if (!variables.containsKey(variableId))
				throw new RuntimeException(String.format("Could not find variable \"%s\" within set \"%s\"", variableId, String.join(", ", variables.keySet())));
			return variables.get(variableId);
		}

		public TextureBuilderBuilder then(String variableId, boolean skipNone)
		{
			components.add((stack, variables, species, player) -> {
				var variable = getCheckedVariable(variables, variableId);
				if ((!skipNone) || SpeciesVariable.isNotNone(species, variable))
					stack.add(SwgSpecies.getTexture(species, variable));
			});
			return this;
		}

		public TextureBuilderBuilder thenGender(String variableId, boolean skipNone)
		{
			components.add((stack, variables, species, player) -> {
				var variable = getCheckedVariable(variables, variableId);
				if ((!skipNone) || SpeciesVariable.isNotNone(species, variable))
					stack.add(SwgSpecies.getGenderedTexture(species, variable));
			});
			return this;
		}

		public TextureBuilderBuilder thenTint(String variableId, String tintVariableId, boolean skipNone)
		{
			components.add((stack, variables, species, player) -> {
				var variable = getCheckedVariable(variables, variableId);
				var tintVariable = getCheckedVariable(variables, tintVariableId);
				if (!(tintVariable instanceof SpeciesColorVariable colorVariable))
					throw new RuntimeException("Cannot tint based on a non-color variable");
				if ((!skipNone) || SpeciesVariable.isNotNone(species, variable))
					stack.add(SwgSpecies.tint(SwgSpecies.getTexture(species, variable), species, colorVariable));
			});
			return this;
		}

		public TextureBuilderBuilder thenTintGender(String variableId, String tintVariableId, boolean skipNone)
		{
			components.add((stack, variables, species, player) -> {
				var variable = getCheckedVariable(variables, variableId);
				var tintVariable = getCheckedVariable(variables, tintVariableId);
				if (!(tintVariable instanceof SpeciesColorVariable colorVariable))
					throw new RuntimeException("Cannot tint based on a non-color variable");
				if ((!skipNone) || SpeciesVariable.isNotNone(species, variable))
					stack.add(SwgSpecies.tint(SwgSpecies.getGenderedTexture(species, variable), species, colorVariable));
			});
			return this;
		}

		public TextureBuilderBuilder thenStatic(String name)
		{
			components.add((stack, variables, species, player) -> {
				stack.add(SwgSpecies.getTexture(species.getSlug(), name));
			});
			return this;
		}

		public TextureBuilderBuilder thenTintStatic(String name, String tintVariableId)
		{
			components.add((stack, variables, species, player) -> {
				var tintVariable = getCheckedVariable(variables, tintVariableId);
				if (!(tintVariable instanceof SpeciesColorVariable colorVariable))
					throw new RuntimeException("Cannot tint based on a non-color variable");
				stack.add(SwgSpecies.tint(SwgSpecies.getTexture(species.getSlug(), name), species, colorVariable));
			});
			return this;
		}

		public TextureBuilderBuilder thenGenderStatic(String name)
		{
			components.add((stack, variables, species, player) -> {
				stack.add(SwgSpecies.getGenderedTexture(species, name));
			});
			return this;
		}

		public TextureBuilderBuilder thenGlobal(String name)
		{
			components.add((stack, variables, species, player) -> {
				stack.add(SwgSpecies.getGlobalTexture(name));
			});
			return this;
		}

		public TextureBuilderBuilder thenTintGlobal(String name, String tintVariableId)
		{
			components.add((stack, variables, species, player) -> {
				var tintVariable = getCheckedVariable(variables, tintVariableId);
				if (!(tintVariable instanceof SpeciesColorVariable colorVariable))
					throw new RuntimeException("Cannot tint based on a non-color variable");
				stack.add(SwgSpecies.tint(SwgSpecies.getGlobalTexture(name), species, colorVariable));
			});
			return this;
		}

		public TextureBuilderBuilder thenHumanoidBodyModifications()
		{
			return this.then("humanoid_scars", true)
			           .thenTint("humanoid_tattoo", "humanoid_tattoo_color", true);
		}

		public TextureBuilderBuilder thenHumanoidEyes()
		{
			return this.thenGlobal("eyes_whites")
			           .thenTintGlobal("eyes", "humanoid_eye_color");
		}

		public TextureBuilderBuilder thenHumanoidEyebrows()
		{
			return this.thenGender("humanoid_eyebrows", true);
		}

		public TextureBuilderBuilder thenHumanoidHair()
		{
			return this.thenHumanoidHair("humanoid_hair_color");
		}

		public TextureBuilderBuilder thenHumanoidHair(String hairColorId)
		{
			return this.thenTint("humanoid_hair", hairColorId, true);
		}

		public TextureBuilderBuilder thenHumanoidClothing()
		{
			components.add((stack, variables, species, player) -> {
				stack.add(SwgSpecies.getClothes(species, player));
			});
			return this;
		}
	}

	private final Identifier id;
	private final HashMap<String, SpeciesVariable> variableTable = new HashMap<>();
	private List<TextureBuilderComponent> textureBuilder;

	public SpeciesBuilder(Identifier id)
	{
		this.id = id;
	}

	public SpeciesBuilder withHumanoidBodyModifications()
	{
		variableTable.put("humanoid_scars", SwgSpecies.VAR_HUMANOID_SCARS);
		variableTable.put("humanoid_tattoo", SwgSpecies.VAR_HUMANOID_TATTOOS);
		variableTable.put("humanoid_tattoo_color", SwgSpecies.VAR_HUMANOID_TATTOO_COLOR);
		return this;
	}

	public SpeciesBuilder withHumanoidEyes()
	{
		variableTable.put("humanoid_eye_color", SwgSpecies.VAR_HUMANOID_EYE_COLOR);
		return this;
	}

	public SpeciesBuilder withHumanoidEyebrows()
	{
		variableTable.put("humanoid_eyebrows", SwgSpecies.VAR_HUMANOID_EYEBROWS);
		return this;
	}

	public SpeciesBuilder withHumanoidHairAndColor()
	{
		variableTable.put("humanoid_hair", SwgSpecies.VAR_HUMANOID_HAIR);
		variableTable.put("humanoid_hair_color", SwgSpecies.VAR_HUMANOID_HAIR_COLOR);
		return this;
	}

	public SpeciesBuilder withHumanoidHair()
	{
		variableTable.put("humanoid_hair", SwgSpecies.VAR_HUMANOID_HAIR);
		return this;
	}

	public SpeciesBuilder withHumanoidClothing()
	{
		variableTable.put("humanoid_clothes_underlayer", SwgSpecies.VAR_HUMANOID_CLOTHES_UNDERLAYER);
		variableTable.put("humanoid_clothes_top", SwgSpecies.VAR_HUMANOID_CLOTHES_TOPS);
		variableTable.put("humanoid_clothes_bottom", SwgSpecies.VAR_HUMANOID_CLOTHES_BOTTOMS);
		variableTable.put("humanoid_clothes_belt", SwgSpecies.VAR_HUMANOID_CLOTHES_BELTS);
		variableTable.put("humanoid_clothes_boots", SwgSpecies.VAR_HUMANOID_CLOTHES_BOOTS);
		variableTable.put("humanoid_clothes_gloves", SwgSpecies.VAR_HUMANOID_CLOTHES_GLOVES);
		variableTable.put("humanoid_clothes_accessories", SwgSpecies.VAR_HUMANOID_CLOTHES_ACCESSORIES);
		variableTable.put("humanoid_clothes_outerwear", SwgSpecies.VAR_HUMANOID_CLOTHES_OUTERWEAR);
		return this;
	}

	public SpeciesBuilder humanoidVariable(String variableId, String defaultValue, String... additionalValues)
	{
		var valueSet = new HashSet<String>();
		valueSet.add(defaultValue);
		valueSet.addAll(List.of(additionalValues));
		variableTable.put(variableId, new SpeciesStringVariable(SwgSpeciesRegistry.METASPECIES_HUMANOID, variableId, defaultValue, valueSet.toArray(new String[0])));
		return this;
	}

	public SpeciesBuilder variable(String variableId, String defaultValue, String... additionalValues)
	{
		var valueSet = new HashSet<String>();
		valueSet.add(defaultValue);
		valueSet.addAll(List.of(additionalValues));
		variableTable.put(variableId, new SpeciesStringVariable(id, variableId, defaultValue, valueSet.toArray(new String[0])));
		return this;
	}

	public SpeciesBuilder variable(String variableId, int defaultValue, int... additionalValues)
	{
		var allowedValues = new int[0];

		if (additionalValues.length > 0)
		{
			var valueSet = new HashSet<Integer>();
			valueSet.add(defaultValue);
			for (var value : additionalValues)
				valueSet.add(value);
			allowedValues = valueSet.stream().mapToInt(i -> i).toArray();
		}

		variableTable.put(variableId, new SpeciesColorVariable(id, variableId, defaultValue, allowedValues));
		return this;
	}

	public SpeciesBuilder layerRenderer(Consumer<TextureBuilderBuilder> consumer)
	{
		if (textureBuilder != null)
			throw new RuntimeException("Cannot set layer renderer more than once");

		var builder = new TextureBuilderBuilder();
		consumer.accept(builder);
		textureBuilder = builder.build();

		return this;
	}

	public SpeciesFactory build()
	{
		if (textureBuilder == null)
			throw new RuntimeException("Layer renderer must be present");

		return new SpeciesFactory(id, variableTable, textureBuilder);
	}
}
