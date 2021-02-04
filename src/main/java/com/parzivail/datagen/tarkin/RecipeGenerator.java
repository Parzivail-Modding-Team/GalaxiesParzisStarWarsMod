package com.parzivail.datagen.tarkin;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
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

	public static void buildMetal(List<BuiltAsset> assets, ItemConvertible ore, ItemConvertible ingot, ItemConvertible block)
	{
		buildOreToIngot(assets, ore, ingot);
		buildBidirectional(assets, ingot, "ingot", block, "block");
	}

	public static void buildMetal(List<BuiltAsset> assets, ItemConvertible ore, ItemConvertible ingot, ItemConvertible nugget, ItemConvertible block)
	{
		buildOreToIngot(assets, ore, ingot);
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

	public static void buildOreToIngot(List<BuiltAsset> assets, ItemConvertible ore, ItemConvertible ingot)
	{
		Cooking.smelting("ore", ore, ingot)
		       .build(assets);
		Cooking.blasting("blasting_ore", ore, ingot)
		       .build(assets);
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

	private static JsonObject toJson(ItemConvertible item)
	{
		JsonObject o = new JsonObject();
		o.addProperty("item", AssetGenerator.getRegistryName(item).toString());

		return o;
	}

	protected abstract void buildInto(JsonObject jsonElement);

	public void build(List<BuiltAsset> assets)
	{
		JsonObject root = new JsonObject();

		root.addProperty("type", type.toString());
		if (group != null)
			root.addProperty("group", group.toString());

		buildInto(root);

		assets.add(BuiltAsset.recipe(IdentifierUtil.concat(output, "_from_" + sourceName), root));
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

		public final ItemConvertible input;
		public float experience = 0.35f;
		public int cookTime = 200;

		private Cooking(Identifier type, Identifier output, ItemConvertible input, String sourceName)
		{
			super(type, output, sourceName);
			this.input = input;
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

			jsonElement.add("ingredient", RecipeGenerator.toJson(input));
		}
	}

	public static class Shapeless extends RecipeGenerator
	{
		public static Shapeless of(ItemStack output, String sourceName)
		{
			return new Shapeless(output, sourceName);
		}

		public final int outputCount;
		public final List<ItemConvertible> inputs;

		private Shapeless(ItemStack output, String sourceName)
		{
			super(new Identifier("crafting_shapeless"), AssetGenerator.getRegistryName(output.getItem()), sourceName);
			this.outputCount = output.getCount();
			this.inputs = new ArrayList<>();
		}

		public Shapeless ingredient(ItemConvertible ingredient)
		{
			inputs.add(ingredient.asItem());
			return this;
		}

		@Override
		protected void buildInto(JsonObject jsonElement)
		{
			JsonArray ingredients = new JsonArray();
			for (ItemConvertible input : inputs)
				ingredients.add(toJson(input));
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
			public final ItemConvertible[][] grid;

			public Shape(String sourceName, ItemConvertible[][] grid)
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
			super(new Identifier("crafting_shaped"), AssetGenerator.getRegistryName(output.getItem()), "");
			this.outputCount = output.getCount();
			this.shapes = new ArrayList<>();
		}

		public Shaped grid1x2(String sourceName, ItemConvertible a, ItemConvertible b)
		{
			return grid3x3(sourceName, a, null, null, b, null, null, null, null, null);
		}

		public Shaped grid1x3(String sourceName, ItemConvertible a, ItemConvertible b, ItemConvertible c)
		{
			return grid3x3(sourceName, a, null, null, b, null, null, c, null, null);
		}

		public Shaped grid2x2(String sourceName, ItemConvertible a, ItemConvertible b, ItemConvertible d, ItemConvertible e)
		{
			return grid3x3(sourceName, a, b, null, d, e, null, null, null, null);
		}

		public Shaped fill2x2(String sourceName, ItemConvertible item)
		{
			return grid2x2(sourceName, item, item, item, item);
		}

		public Shaped grid2x3(String sourceName, ItemConvertible a, ItemConvertible b, ItemConvertible d, ItemConvertible e, ItemConvertible g, ItemConvertible h)
		{
			return grid3x3(sourceName, a, b, null, d, e, null, g, h, null);
		}

		public Shaped grid3x2(String sourceName, ItemConvertible a, ItemConvertible b, ItemConvertible c, ItemConvertible d, ItemConvertible e, ItemConvertible f)
		{
			return grid3x3(sourceName, a, b, c, d, e, f, null, null, null);
		}

		public Shaped grid3x3(String sourceName, ItemConvertible a, ItemConvertible b, ItemConvertible c, ItemConvertible d, ItemConvertible e, ItemConvertible f, ItemConvertible g, ItemConvertible h, ItemConvertible i)
		{
			shapes.add(new Shape(sourceName, new ItemConvertible[][] { { a, b, c }, { d, e, f }, { g, h, i } }));
			return this;
		}

		public Shaped fill3x3(String sourceName, ItemConvertible item)
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
				ItemConvertible[][] shapeGrid = shape.grid;

				int minX = 3;
				int maxX = -1;

				int minY = 3;
				int maxY = -1;

				// minimize recipe size to allow shifting smaller recipes around the crafting grid
				for (int i = 0; i < 3; i++)
					for (int j = 0; j < 3; j++)
					{
						if (shapeGrid[i][j] == null)
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

				HashMap<ItemConvertible, Character> key = new HashMap<>();
				ArrayList<String> recipe = new ArrayList<>();

				// build pattern
				for (int j = minY; j <= maxY; j++)
				{
					StringBuilder b = new StringBuilder();
					for (int i = minX; i <= maxX; i++)
					{
						if (shapeGrid[i][j] == null)
							b.append(" ");
						else
						{
							ItemConvertible item = shapeGrid[i][j];
							if (!key.containsKey(item))
							{
								Identifier reg = AssetGenerator.getRegistryName(item);
								String itemName = reg.getPath();
								char c = itemName.charAt(0);

								while (anyValue(key, c))
								{
									itemName = itemName.substring(1);
									c = itemName.charAt(0);
								}

								key.put(item, c);
							}

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
				for (Map.Entry<ItemConvertible, Character> entry : key.entrySet())
					keyObject.add(entry.getValue().toString(), RecipeGenerator.toJson(entry.getKey()));
				root.add("key", keyObject);

				JsonObject result = new JsonObject();
				result.addProperty("item", output.toString());
				result.addProperty("count", outputCount);
				root.add("result", result);

				// built asset
				String source = type.getPath();
				if (shape.sourceName != null)
					source = shape.sourceName;

				assets.add(BuiltAsset.recipe(IdentifierUtil.concat(output, "_from_" + source), root));
			}
		}

		private boolean anyValue(HashMap<ItemConvertible, Character> key, char c)
		{
			for (Map.Entry<ItemConvertible, Character> entry : key.entrySet())
				if (entry.getValue() == c)
					return true;

			return false;
		}
	}
}
