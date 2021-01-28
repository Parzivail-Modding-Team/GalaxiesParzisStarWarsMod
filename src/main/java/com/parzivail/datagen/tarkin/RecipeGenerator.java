package com.parzivail.datagen.tarkin;

import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public abstract class RecipeGenerator
{
	public final Identifier output;

	public RecipeGenerator(Identifier output)
	{
		this.output = output;
	}

	public abstract void build(List<BuiltAsset> assets);

	public static class Smelting extends RecipeGenerator
	{
		public static Smelting of(ItemConvertible output, ItemConvertible input)
		{
			return new Smelting(AssetGenerator.getRegistryName(output), AssetGenerator.getRegistryName(input));
		}

		public final Identifier input;
		public float experience = 0.35f;
		public int cookTime = 200;

		private Smelting(Identifier output, Identifier input)
		{
			super(output);
			this.input = input;
		}

		public Smelting experience(float experience)
		{
			this.experience = experience;
			return this;
		}

		public Smelting cookTime(int cookTime)
		{
			this.cookTime = cookTime;
			return this;
		}

		@Override
		public void build(List<BuiltAsset> assets)
		{
			// TODO
		}
	}

	public static class Shapeless extends RecipeGenerator
	{
		public static Shapeless of(ItemStack output)
		{
			return new Shapeless(output);
		}

		public final int outputCount;
		public final List<Identifier> inputs;

		private Shapeless(ItemStack output)
		{
			super(AssetGenerator.getRegistryName(output.getItem()));
			this.outputCount = output.getCount();
			this.inputs = new ArrayList<>();
		}

		public Shapeless ingredient(ItemConvertible ingredient)
		{
			inputs.add(AssetGenerator.getRegistryName(ingredient.asItem()));
			return this;
		}

		@Override
		public void build(List<BuiltAsset> assets)
		{
			// TODO
		}
	}

	public static class Shaped extends RecipeGenerator
	{
		public static Shaped of(ItemStack output)
		{
			return new Shaped(output);
		}

		public final int outputCount;
		public List<ItemConvertible[]> shapes;

		private Shaped(ItemStack output)
		{
			super(AssetGenerator.getRegistryName(output.getItem()));
			this.outputCount = output.getCount();
			this.shapes = new ArrayList<>();
		}

		public Shaped small(ItemConvertible a, ItemConvertible b, ItemConvertible c, ItemConvertible d)
		{
			shapes.add(new ItemConvertible[] { a, b, c, d });
			return this;
		}

		public Shaped full(ItemConvertible a, ItemConvertible b, ItemConvertible c, ItemConvertible d, ItemConvertible e, ItemConvertible f, ItemConvertible g, ItemConvertible h, ItemConvertible i)
		{
			shapes.add(new ItemConvertible[] { a, b, c, d, e, f, g, h, i });
			return this;
		}

		@Override
		public void build(List<BuiltAsset> assets)
		{
			// TODO
		}
	}
}
