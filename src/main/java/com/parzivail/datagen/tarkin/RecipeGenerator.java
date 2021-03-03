package com.parzivail.datagen.tarkin;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.parzivail.datagen.tarkin.crafting.IJsonCraftingComponent;
import com.parzivail.datagen.tarkin.crafting.IngredientTag;
import com.parzivail.datagen.tarkin.crafting.ItemConvertibleJsonCraftingComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class RecipeGenerator
{
	public static void buildFood(List<BuiltAsset> assets, Item input, Item output)
	{
		Cooking.smelting("raw", input, output)
		       .build(assets);
		Cooking.smoking("smoking_raw", input, output)
		       .build(assets);
		Cooking.campfire("campfire_raw", input, output)
		       .build(assets);
	}

	public static void buildMetal(List<BuiltAsset> assets, float experience, ItemConvertible ore, ItemConvertible ingot, ItemConvertible block)
	{
		buildOreToIngot(assets, experience, ore, ingot);
		buildBidirectional(assets, ingot, "ingot", block, "block");
	}

	public static void buildMetal(List<BuiltAsset> assets, float experience, ItemConvertible ore, ItemConvertible ingot, ItemConvertible nugget, ItemConvertible block)
	{
		buildOreToIngot(assets, experience, ore, ingot);
		buildBidirectional(assets, ingot, "ingot", block, "block");
		buildBidirectional(assets, nugget, "nugget", ingot, "ingot");
	}

	public static void buildBidirectional(List<BuiltAsset> assets, ItemConvertible smallUnit, String smallUnitName, ItemConvertible largeUnit, String largeUnitName)
	{
		Shapeless.of(new ItemStack(smallUnit, 9), largeUnitName)
		         .ingredient(largeUnit)
		         .build(assets);
		Shaped.of(new ItemStack(largeUnit, 1))
		      .fill3x3(smallUnitName, smallUnit)
		      .build(assets);
	}

	public static void buildOreToIngot(List<BuiltAsset> assets, float experience, ItemConvertible ore, ItemConvertible ingot)
	{
		Cooking.smelting("ore", ore, ingot).experience(experience)
		       .build(assets);
		Cooking.blasting("blasting_ore", ore, ingot).experience(experience)
		       .build(assets);
	}

	private static IJsonCraftingComponent getJsonComponent(Object o)
	{
		if (o == null)
			return null;

		if (o instanceof IJsonCraftingComponent)
			return (IJsonCraftingComponent)o;

		if (o instanceof ItemConvertible)
			return new ItemConvertibleJsonCraftingComponent((ItemConvertible)o);

		if (o instanceof Tag.Identified<?>)
			return new IngredientTag(((Tag.Identified<?>)o).getId());

		throw new IllegalArgumentException();
	}

	public final Identifier type;
	public final Identifier output;
	public final String sourceName;

	public Identifier group;

	private RecipeGenerator(Identifier type, Identifier output, String sourceName)
	{
		this.type = type;
		this.output = output;

		this.group = output;
		this.sourceName = sourceName;
	}

	protected abstract void buildInto(JsonObject jsonElement);

	public void build(List<BuiltAsset> assets)
	{
		JsonObject root = new JsonObject();

		root.addProperty("type", type.toString());
		if (group != null)
			root.addProperty("group", group.toString());

		buildInto(root);

		String source = "";
		if (sourceName != null)
			source = "_from_" + sourceName;

		assets.add(BuiltAsset.recipe(IdentifierUtil.concat(output, source), root));
	}

	public static class Cooking extends RecipeGenerator
	{
		public static Cooking smelting(String sourceName, ItemConvertible input, ItemConvertible output)
		{
			return new Cooking(new Identifier("smelting"), AssetGenerator.getRegistryName(output), input, sourceName);
		}

		public static Cooking blasting(String sourceName, ItemConvertible input, ItemConvertible output)
		{
			return new Cooking(new Identifier("blasting"), AssetGenerator.getRegistryName(output), input, sourceName)
					.cookTime(100);
		}

		public static Cooking campfire(String sourceName, ItemConvertible input, ItemConvertible output)
		{
			return new Cooking(new Identifier("campfire_cooking"), AssetGenerator.getRegistryName(output), input, sourceName)
					.cookTime(600);
		}

		public static Cooking smoking(String sourceName, ItemConvertible input, ItemConvertible output)
		{
			return new Cooking(new Identifier("smoking"), AssetGenerator.getRegistryName(output), input, sourceName)
					.cookTime(100);
		}

		public final IJsonCraftingComponent input;
		public float experience = 0.35f;
		public int cookTime = 200;

		private Cooking(Identifier type, Identifier output, Object input, String sourceName)
		{
			super(type, output, sourceName);
			this.input = RecipeGenerator.getJsonComponent(input);
		}

		public Cooking experience(float experience)
		{
			this.experience = experience;
			return this;
		}

		public Cooking cookTime(int cookTime)
		{
			this.cookTime = cookTime;
			return this;
		}

		@Override
		public void buildInto(JsonObject jsonElement)
		{
			jsonElement.addProperty("result", output.toString());
			jsonElement.addProperty("experience", experience);
			jsonElement.addProperty("cookingtime", cookTime);

			jsonElement.add("ingredient", input.getIngredientObject());
		}
	}

	public static class Shapeless extends RecipeGenerator
	{
		public static Shapeless of(ItemStack output, String sourceName)
		{
			return new Shapeless(output, sourceName);
		}

		public final int outputCount;
		public final List<IJsonCraftingComponent> inputs;

		private Shapeless(ItemStack output, String sourceName)
		{
			super(new Identifier("crafting_shapeless"), AssetGenerator.getRegistryName(output.getItem()), sourceName);
			this.outputCount = output.getCount();
			this.inputs = new ArrayList<>();
		}

		public Shapeless ingredient(Object ingredient)
		{
			inputs.add(RecipeGenerator.getJsonComponent(ingredient));
			return this;
		}

		@Override
		protected void buildInto(JsonObject jsonElement)
		{
			JsonArray ingredients = new JsonArray();
			for (IJsonCraftingComponent input : inputs)
				ingredients.add(input.getIngredientObject());
			jsonElement.add("ingredients", ingredients);

			JsonObject result = new JsonObject();
			result.addProperty("item", output.toString());
			result.addProperty("count", outputCount);

			jsonElement.add("result", result);
		}
	}

	public static class Shaped extends RecipeGenerator
	{
		private static class Shape
		{
			public final String sourceName;
			public final IJsonCraftingComponent[][] grid;

			public Shape(String sourceName, IJsonCraftingComponent[][] grid)
			{
				this.sourceName = sourceName;
				this.grid = grid;
			}
		}

		public static Shaped of(ItemStack output)
		{
			return new Shaped(output);
		}

		public final int outputCount;
		public List<Shape> shapes;

		private Shaped(ItemStack output)
		{
			super(new Identifier("crafting_shaped"), AssetGenerator.getRegistryName(output.getItem()), null);
			this.outputCount = output.getCount();
			this.shapes = new ArrayList<>();
		}

		public Shaped grid1x2(String sourceName, Object a, Object b)
		{
			return grid3x3(sourceName, a, null, null, b, null, null, null, null, null);
		}

		public Shaped fill1x2(String sourceName, Object a)
		{
			return grid1x2(sourceName, a, a);
		}

		public Shaped grid1x3(String sourceName, Object a, Object b, Object c)
		{
			return grid3x3(sourceName, a, null, null, b, null, null, c, null, null);
		}

		public Shaped fill1x3(String sourceName, Object a)
		{
			return grid1x3(sourceName, a, a, a);
		}

		public Shaped grid2x1(String sourceName, Object a, Object b)
		{
			return grid3x3(sourceName, a, b, null, null, null, null, null, null, null);
		}

		public Shaped fill2x1(String sourceName, Object a)
		{
			return grid2x1(sourceName, a, a);
		}

		public Shaped grid2x2(String sourceName, Object a, Object b, Object d, Object e)
		{
			return grid3x3(sourceName, a, b, null, d, e, null, null, null, null);
		}

		public Shaped fill2x2(String sourceName, Object item)
		{
			return grid2x2(sourceName, item, item, item, item);
		}

		public Shaped grid2x3(String sourceName, Object a, Object b, Object d, Object e, Object g, Object h)
		{
			return grid3x3(sourceName, a, b, null, d, e, null, g, h, null);
		}

		public Shaped fill2x3(String sourceName, Object a)
		{
			return grid2x3(sourceName, a, a, a, a, a, a);
		}

		public Shaped grid3x1(String sourceName, Object a, Object b, Object c)
		{
			return grid3x3(sourceName, a, b, c, null, null, null, null, null, null);
		}

		public Shaped fill3x1(String sourceName, Object a)
		{
			return grid3x1(sourceName, a, a, a);
		}

		public Shaped grid3x2(String sourceName, Object a, Object b, Object c, Object d, Object e, Object f)
		{
			return grid3x3(sourceName, a, b, c, d, e, f, null, null, null);
		}

		public Shaped fill3x2(String sourceName, Object a)
		{
			return grid3x2(sourceName, a, a, a, a, a, a);
		}

		public Shaped grid3x3(String sourceName, Object a, Object b, Object c, Object d, Object e, Object f, Object g, Object h, Object i)
		{
			shapes.add(new Shape(sourceName, new IJsonCraftingComponent[][] {
					{ getJsonComponent(a), getJsonComponent(b), getJsonComponent(c) },
					{ getJsonComponent(d), getJsonComponent(e), getJsonComponent(f) },
					{ getJsonComponent(g), getJsonComponent(h), getJsonComponent(i) }
			}));
			return this;
		}

		public Shaped fill3x3(String sourceName, Object item)
		{
			return grid3x3(sourceName, item, item, item, item, item, item, item, item, item);
		}

		@Override
		protected void buildInto(JsonObject jsonElement)
		{
		}

		@Override
		public void build(List<BuiltAsset> assets)
		{
			for (Shape shape : shapes)
			{
				IJsonCraftingComponent[][] shapeGrid = shape.grid;

				int minX = 3;
				int maxX = -1;

				int minY = 3;
				int maxY = -1;

				// minimize recipe size to allow shifting smaller recipes around the crafting grid
				for (int i = 0; i < 3; i++)
					for (int j = 0; j < 3; j++)
					{
						if (shapeGrid[j][i] == null)
							continue;

						if (i < minX)
							minX = i;
						if (i > maxX)
							maxX = i;

						if (j < minY)
							minY = j;
						if (j > maxY)
							maxY = j;
					}

				HashMap<IJsonCraftingComponent, Character> key = new HashMap<>();
				ArrayList<String> recipe = new ArrayList<>();

				// there can never be more than 9 ingredients in a "crafting_shaped" recipe
				char[] ingredientKeys = new char[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i' };
				int ingredientKeyIdx = 0;

				// build pattern
				for (int j = minY; j <= maxY; j++)
				{
					StringBuilder b = new StringBuilder();
					for (int i = minX; i <= maxX; i++)
					{
						if (shapeGrid[j][i] == null)
							b.append(" ");
						else
						{
							IJsonCraftingComponent item = shapeGrid[j][i];
							if (!key.containsKey(item))
								key.put(item, ingredientKeys[ingredientKeyIdx++]);

							b.append(key.get(item));
						}
					}
					recipe.add(b.toString());
				}

				// serialize
				JsonObject root = new JsonObject();

				root.addProperty("type", type.toString());
				if (group != null)
					root.addProperty("group", group.toString());

				JsonArray pattern = new JsonArray();
				recipe.forEach(pattern::add);
				root.add("pattern", pattern);

				JsonObject keyObject = new JsonObject();
				for (Map.Entry<IJsonCraftingComponent, Character> entry : key.entrySet())
					keyObject.add(entry.getValue().toString(), entry.getKey().getIngredientObject());
				root.add("key", keyObject);

				JsonObject result = new JsonObject();
				result.addProperty("item", output.toString());
				result.addProperty("count", outputCount);
				root.add("result", result);

				// built asset
				String source = "";
				if (shape.sourceName != null)
					source = "_from_" + shape.sourceName;

				assets.add(BuiltAsset.recipe(IdentifierUtil.concat(output, source), root));
			}
		}

		private boolean anyValue(HashMap<IJsonCraftingComponent, Character> key, char c)
		{
			for (Map.Entry<IJsonCraftingComponent, Character> entry : key.entrySet())
				if (entry.getValue() == c)
					return true;

			return false;
		}
	}
}
