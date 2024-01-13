package com.parzivail.datagen.tarkin;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.parzivail.datagen.AssetUtils;
import com.parzivail.datagen.tarkin.crafting.IJsonCraftingComponent;
import com.parzivail.datagen.tarkin.crafting.IngredientTag;
import com.parzivail.datagen.tarkin.crafting.ItemConvertibleJsonCraftingComponent;
import com.parzivail.pswg.Resources;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

	public static void buildCrystal(List<BuiltAsset> assets, ItemConvertible dust, ItemConvertible crystal, ItemConvertible block)
	{
		Shapeless.of(new ItemStack(dust, 2), "crystal")
		         .ingredient(crystal)
		         .build(assets);

		buildReversible9to1(assets, crystal, "crystal", block, "block");
	}

	public static void buildMetal(List<BuiltAsset> assets, float experience, ItemConvertible ore, ItemConvertible rawOre, ItemConvertible ingot, ItemConvertible block)
	{
		buildOreToIngot(assets, experience, ore, rawOre, ingot);
		buildReversible9to1(assets, ingot, "ingot", block, "block");
	}

	public static void buildMetal(List<BuiltAsset> assets, float experience, ItemConvertible ore, ItemConvertible rawOre, ItemConvertible ingot, ItemConvertible nugget, ItemConvertible block)
	{
		buildOreToIngot(assets, experience, ore, rawOre, ingot);
		buildBlockIngotNugget(assets, block, ingot, nugget);
	}

	public static void buildBlockIngotNugget(List<BuiltAsset> assets, ItemConvertible block, ItemConvertible ingot, ItemConvertible nugget)
	{
		buildReversible9to1(assets, ingot, "ingot", block, "block");
		buildReversible9to1(assets, nugget, "nugget", ingot, "ingot");
	}

	public static void buildReversible9to1(List<BuiltAsset> assets, ItemConvertible smallUnit, String smallUnitName, ItemConvertible largeUnit, String largeUnitName)
	{
		Shapeless.of(new ItemStack(smallUnit, 9), largeUnitName)
		         .ingredient(largeUnit)
		         .build(assets);
		Shaped.of(new ItemStack(largeUnit, 1))
		      .fill3x3(smallUnitName, smallUnit)
		      .build(assets);
	}

	public static void buildOreToIngot(List<BuiltAsset> assets, float experience, ItemConvertible ore, ItemConvertible rawOre, ItemConvertible ingot)
	{
		Cooking.smelting("ore", ore, ingot).experience(experience)
		       .build(assets);
		Cooking.blasting("blasting_ore", ore, ingot).experience(experience)
		       .build(assets);
		Cooking.smelting("raw_ore", rawOre, ingot).experience(experience)
		       .build(assets);
		Cooking.blasting("blasting_raw_ore", rawOre, ingot).experience(experience)
		       .build(assets);
	}

	private static IJsonCraftingComponent getJsonComponent(Object o)
	{
		if (o == null)
			return null;

		if (o instanceof IJsonCraftingComponent)
			return (IJsonCraftingComponent)o;

		if (o instanceof ItemConvertible itemConvertible)
			return new ItemConvertibleJsonCraftingComponent(itemConvertible);

		if  (o instanceof TagKey<?> tagKey)
		{
			return new IngredientTag(tagKey.id());
		}

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
		var root = new JsonObject();

		root.addProperty("type", type.toString());
		if (group != null)
			root.addProperty("group", group.toString());

		buildInto(root);

		var source = "";
		if (sourceName != null)
			source = "_from_" + sourceName;

		assets.add(BuiltAsset.recipe(Resources.id(IdentifierUtil.concat(output, source).getPath()), root));
	}

	public static class Cooking extends RecipeGenerator
	{
		public static Cooking smelting(String sourceName, ItemConvertible input, ItemConvertible output)
		{
			return new Cooking(new Identifier("smelting"), AssetUtils.getRegistryName(output), input, sourceName);
		}

		public static Cooking blasting(String sourceName, ItemConvertible input, ItemConvertible output)
		{
			return new Cooking(new Identifier("blasting"), AssetUtils.getRegistryName(output), input, sourceName)
					.cookTime(100);
		}

		public static Cooking campfire(String sourceName, ItemConvertible input, ItemConvertible output)
		{
			return new Cooking(new Identifier("campfire_cooking"), AssetUtils.getRegistryName(output), input, sourceName)
					.cookTime(600);
		}

		public static Cooking smoking(String sourceName, ItemConvertible input, ItemConvertible output)
		{
			return new Cooking(new Identifier("smoking"), AssetUtils.getRegistryName(output), input, sourceName)
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
			super(new Identifier("crafting_shapeless"), AssetUtils.getRegistryName(output.getItem()), sourceName);
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
			var ingredients = new JsonArray();
			for (var input : inputs)
				ingredients.add(input.getIngredientObject());
			jsonElement.add("ingredients", ingredients);

			var result = new JsonObject();
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
			super(new Identifier("crafting_shaped"), AssetUtils.getRegistryName(output.getItem()), null);
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
			for (var shape : shapes)
			{
				var shapeGrid = shape.grid;

				var minX = 3;
				var maxX = -1;

				var minY = 3;
				var maxY = -1;

				// minimize recipe size to allow shifting smaller recipes around the crafting grid
				for (var i = 0; i < 3; i++)
					for (var j = 0; j < 3; j++)
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

				var key = new HashMap<IJsonCraftingComponent, Character>();
				var recipe = new ArrayList<String>();

				// there can never be more than 9 ingredients in a "crafting_shaped" recipe
				var ingredientKeys = new char[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i' };
				var ingredientKeyIdx = 0;

				// build pattern
				for (var j = minY; j <= maxY; j++)
				{
					var b = new StringBuilder();
					for (var i = minX; i <= maxX; i++)
					{
						if (shapeGrid[j][i] == null)
							b.append(" ");
						else
						{
							var item = shapeGrid[j][i];
							if (!key.containsKey(item))
								key.put(item, ingredientKeys[ingredientKeyIdx++]);

							b.append(key.get(item));
						}
					}
					recipe.add(b.toString());
				}

				// serialize
				var root = new JsonObject();

				root.addProperty("type", type.toString());
				if (group != null)
					root.addProperty("group", group.toString());

				var pattern = new JsonArray();
				recipe.forEach(pattern::add);
				root.add("pattern", pattern);

				var keyObject = new JsonObject();
				for (var entry : key.entrySet())
					keyObject.add(entry.getValue().toString(), entry.getKey().getIngredientObject());
				root.add("key", keyObject);

				var result = new JsonObject();
				result.addProperty("item", output.toString());
				result.addProperty("count", outputCount);
				root.add("result", result);

				// built asset
				var source = "";
				if (shape.sourceName != null)
					source = "_from_" + shape.sourceName;

				assets.add(BuiltAsset.recipe(Resources.id(IdentifierUtil.concat(output, source).getPath()), root));
			}
		}

		private boolean anyValue(HashMap<IJsonCraftingComponent, Character> key, char c)
		{
			for (var entry : key.entrySet())
				if (entry.getValue() == c)
					return true;

			return false;
		}
	}
}
