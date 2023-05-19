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

		public TextureBuilderBuilder then(String variableId, boolean skipNone)
		{
			components.add((stack, variables, species, player) -> {
				var variable = variables.get(variableId);
				if ((!skipNone) || SpeciesVariable.isNotNone(species, variable))
					stack.add(SwgSpecies.getTexture(species, variable));
			});
			return this;
		}

		public TextureBuilderBuilder thenGender(String variableId, boolean skipNone)
		{
			components.add((stack, variables, species, player) -> {
				var variable = variables.get(variableId);
				if ((!skipNone) || SpeciesVariable.isNotNone(species, variable))
					stack.add(SwgSpecies.getGenderedTexture(species, variable));
			});
			return this;
		}

		public TextureBuilderBuilder thenTint(String variableId, String tintVariableId, boolean skipNone)
		{
			components.add((stack, variables, species, player) -> {
				var variable = variables.get(variableId);
				var tintVariable = variables.get(tintVariableId);
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
				var variable = variables.get(variableId);
				var tintVariable = variables.get(tintVariableId);
				if (!(tintVariable instanceof SpeciesColorVariable colorVariable))
					throw new RuntimeException("Cannot tint based on a non-color variable");
				if ((!skipNone) || SpeciesVariable.isNotNone(species, variable))
					stack.add(SwgSpecies.tint(SwgSpecies.getGenderedTexture(species, variable), species, colorVariable));
			});
			return this;
		}

		public TextureBuilderBuilder then(String name)
		{
			components.add((stack, variables, species, player) -> {
				stack.add(SwgSpecies.getTexture(species.getSlug(), name));
			});
			return this;
		}

		public TextureBuilderBuilder thenHumanoidBodyModifications()
		{
			components.add((stack, variables, species, player) -> {
				if (SpeciesVariable.isNotNone(species, SwgSpecies.VAR_HUMANOID_SCARS))
					stack.add(SwgSpecies.getTexture(species, SwgSpecies.VAR_HUMANOID_SCARS));
				if (SpeciesVariable.isNotNone(species, SwgSpecies.VAR_HUMANOID_TATTOOS))
					stack.add(SwgSpecies.tint(SwgSpecies.getTexture(species, SwgSpecies.VAR_HUMANOID_TATTOOS), species, SwgSpecies.VAR_HUMANOID_TATTOO_COLOR));
			});
			return this;
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
		variableTable.put("scars", SwgSpecies.VAR_HUMANOID_SCARS);
		variableTable.put("tattoo", SwgSpecies.VAR_HUMANOID_TATTOOS);
		variableTable.put("tattoo_color", SwgSpecies.VAR_HUMANOID_TATTOO_COLOR);
		return this;
	}

	public SpeciesBuilder withHumanoidClothing()
	{
		variableTable.put("clothes_underlayer", SwgSpecies.VAR_HUMANOID_CLOTHES_UNDERLAYER);
		variableTable.put("clothes_top", SwgSpecies.VAR_HUMANOID_CLOTHES_TOPS);
		variableTable.put("clothes_bottom", SwgSpecies.VAR_HUMANOID_CLOTHES_BOTTOMS);
		variableTable.put("clothes_belt", SwgSpecies.VAR_HUMANOID_CLOTHES_BELTS);
		variableTable.put("clothes_boots", SwgSpecies.VAR_HUMANOID_CLOTHES_BOOTS);
		variableTable.put("clothes_gloves", SwgSpecies.VAR_HUMANOID_CLOTHES_GLOVES);
		variableTable.put("clothes_accessories", SwgSpecies.VAR_HUMANOID_CLOTHES_ACCESSORIES);
		variableTable.put("clothes_outerwear", SwgSpecies.VAR_HUMANOID_CLOTHES_OUTERWEAR);
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
