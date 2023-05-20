package com.parzivail.pswg.character;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class SpeciesFactory
{
	private class Instance extends SwgSpecies
	{
		public Instance(String serialized)
		{
			super(serialized);
		}

		@Override
		public Identifier getSlug()
		{
			return SpeciesFactory.this.slug;
		}

		@Override
		public SpeciesVariable[] getVariables()
		{
			return SpeciesFactory.this.variables;
		}

		@Override
		public Collection<Identifier> getTextureStack(PlayerEntity player)
		{
			var stack = new ArrayList<Identifier>();
			for (var component : SpeciesFactory.this.textureBuilder)
				component.addLayer(stack, SpeciesFactory.this.variableTable, this, player);
			return stack;
		}

		@Override
		public SpeciesVariable getVariableReference(String variableName)
		{
			return SpeciesFactory.this.variableTable.get(variableName);
		}
	}

	private final Identifier slug;
	private final HashMap<String, SpeciesVariable> variableTable;
	private final SpeciesVariable[] variables;
	private final List<SpeciesBuilder.TextureBuilderComponent> textureBuilder;

	public SpeciesFactory(Identifier slug, HashMap<String, SpeciesVariable> variables, List<SpeciesBuilder.TextureBuilderComponent> textureBuilder)
	{
		this.slug = slug;
		this.variableTable = variables;
		this.textureBuilder = textureBuilder;
		this.variables = variableTable.values().toArray(new SpeciesVariable[0]);
	}

	public SwgSpecies create(String serialized)
	{
		return new Instance(serialized);
	}

	public Identifier getSlug()
	{
		return slug;
	}
}
