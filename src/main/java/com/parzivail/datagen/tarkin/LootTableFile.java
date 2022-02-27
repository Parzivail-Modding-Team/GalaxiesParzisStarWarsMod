package com.parzivail.datagen.tarkin;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.parzivail.util.block.IPicklingBlock;
import net.minecraft.block.Block;
import net.minecraft.item.ItemConvertible;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LootTableFile
{
	public static LootTableFile empty(Block block)
	{
		var reg = AssetGenerator.getRegistryName(block);
		return new LootTableFile(IdentifierUtil.concat("blocks/", reg), new Identifier("block"));
	}

	public static LootTableFile ofPool(Block block, Pool pool)
	{
		return empty(block).pool(pool);
	}

	public static LootTableFile single(Block block, ItemConvertible drop)
	{
		var reg = AssetGenerator.getRegistryName(drop);
		return ofPool(block,
		              new Pool(1)
				              .condition(new Identifier("survives_explosion"))
				              .entry(new Pool.Entry(new Identifier("item"), reg))
		);
	}

	public static LootTableFile singleSelf(Block block)
	{
		return single(block, block);
	}

	public static <T extends Block & IPicklingBlock> LootTableFile pickling(T block)
	{
		var reg = AssetGenerator.getRegistryName(block);

		var entry = new Pool.Entry(new Identifier("item"), reg);

		var pickleProp = block.getPickleProperty();
		pickleProp.getValues().stream().sorted().forEachOrdered(i -> {
			if (i <= 1)
				return;

			entry.function(new Pool.CountFunction(i)
					               .add(false)
					               .condition(new Pool.Condition(new Identifier("block_state_property"))
							                          .block(reg)
							                          .property(pickleProp.getName(), String.valueOf(i)))
			);
		});

		entry.function(new Pool.Function(new Identifier("explosion_decay")));

		return ofPool(block, new Pool(1).entry(entry));
	}

	public static LootTableFile count(Block block, ItemConvertible drop, Pool.CountFunction count)
	{
		var reg = AssetGenerator.getRegistryName(drop);
		return ofPool(block,
		              new Pool(1)
				              .entry(new Pool.Entry(new Identifier("alternatives"))
						                     .child(new Pool.Entry(new Identifier("item"), reg)
								                            .function(count)
								                            .function(new Pool.Function(new Identifier("explosion_decay")))
						                     )
				              )
		);
	}

	public static LootTableFile many(Block block, ItemConvertible drop, int count)
	{
		return count(block, drop, new Pool.CountFunction(count));
	}

	public static LootTableFile many(Block block, ItemConvertible drop, Pool.CountFunction.Range range)
	{
		return count(block, drop, new Pool.CountFunction(range));
	}

	public static LootTableFile seedCrop(Block block, ItemConvertible seeds, ItemConvertible crop, int cropAge, int extra, double probability)
	{
		var blockReg = AssetGenerator.getRegistryName(block);
		var seedReg = AssetGenerator.getRegistryName(seeds);
		var cropReg = AssetGenerator.getRegistryName(crop);
		return empty(block)
				.function(new Pool.Function(new Identifier("explosion_decay")))
				.pool(new Pool(1)
						      .entry(new Pool.Entry(new Identifier("alternatives"))
								             .child(new Pool.Entry(new Identifier("item"), cropReg)
										                    .condition(new Pool.Condition(new Identifier("block_state_property"))
												                               .block(blockReg)
												                               .property("age", cropAge)))
								             .child(new Pool.Entry(new Identifier("item"), seedReg))))
				.pool(new Pool(1)
						      .condition(new Pool.Condition(new Identifier("block_state_property"))
								                 .block(blockReg)
								                 .property("age", cropAge))
						      .entry(new Pool.Entry(new Identifier("item"), seedReg)
								             .function(new Pool.Function(new Identifier("apply_bonus"))
										                       .enchantment(new Identifier("fortune"))
										                       .formula(new Identifier("binomial_with_bonus_count"))
										                       .parameter("extra", extra)
										                       .parameter("probability", probability))));
	}

	public static LootTableFile singleFortuneBonus(Block block, ItemConvertible drop)
	{
		var reg = AssetGenerator.getRegistryName(drop);
		return ofPool(block,
		              new Pool(1)
				              .entry(new Pool.Entry(new Identifier("alternatives"))
						                     .child(new Pool.Entry(new Identifier("item"), reg)
								                            .function(new Pool.Function(new Identifier("apply_bonus"))
										                                      .enchantment(new Identifier("fortune"))
										                                      .formula(new Identifier("ore_drops"))
								                            )
								                            .function(new Pool.Function(new Identifier("explosion_decay")))
						                     )
				              )
		);
	}

	public static class Pool
	{
		public static class Entry
		{
			public void serialize(JsonObject element)
			{
				element.addProperty("type", type.toString());

				if (name != null)
					element.addProperty("name", name.toString());

				if (!functions.isEmpty())
				{
					var functionArray = new JsonArray();
					for (var function : functions)
					{
						var entryElement = new JsonObject();

						function.serialize(entryElement);

						functionArray.add(entryElement);
					}
					element.add("functions", functionArray);
				}

				if (!conditions.isEmpty())
				{
					var conditionArray = new JsonArray();
					for (var condition : conditions)
					{
						var entryElement = new JsonObject();

						condition.serialize(entryElement);

						conditionArray.add(entryElement);
					}
					element.add("conditions", conditionArray);
				}

				if (!children.isEmpty())
				{
					var entryArray = new JsonArray();
					for (var entry : children)
					{
						var entryElement = new JsonObject();

						entry.serialize(entryElement);

						entryArray.add(entryElement);
					}
					element.add("children", entryArray);
				}
			}

			public final Identifier type;
			public final Identifier name;
			public final ArrayList<Entry> children;

			public final ArrayList<Condition> conditions;
			public final ArrayList<Function> functions;

			private Entry(Identifier type, Identifier name)
			{
				this.type = type;
				this.name = name;
				children = new ArrayList<>();

				functions = new ArrayList<>();
				conditions = new ArrayList<>();
			}

			private Entry(Identifier type)
			{
				this(type, null);
			}

			public Entry child(Entry entry)
			{
				this.children.add(entry);
				return this;
			}

			public Entry function(Function function)
			{
				this.functions.add(function);
				return this;
			}

			public Entry condition(Condition condition)
			{
				this.conditions.add(condition);
				return this;
			}
		}

		public static class Function
		{
			Identifier function;
			Identifier enchantment;
			Identifier formula;
			HashMap<String, Object> parameters;
			List<Condition> conditions;
			Boolean add;

			public Function(Identifier function)
			{
				this.function = function;
				this.parameters = new HashMap<>();
				this.conditions = new ArrayList<>();
			}

			public Function enchantment(Identifier enchantment)
			{
				this.enchantment = enchantment;
				return this;
			}

			public Function formula(Identifier formula)
			{
				this.formula = formula;
				return this;
			}

			public Function add(boolean add)
			{
				this.add = add;
				return this;
			}

			public Function condition(Condition condition)
			{
				this.conditions.add(condition);
				return this;
			}

			public Function parameter(String key, Object value)
			{
				this.parameters.put(key, value);
				return this;
			}

			public void serialize(JsonObject entryElement)
			{
				entryElement.addProperty("function", function.toString());

				if (enchantment != null)
					entryElement.addProperty("enchantment", enchantment.toString());

				if (formula != null)
					entryElement.addProperty("formula", formula.toString());

				if (add != null)
					entryElement.addProperty("add", add);

				if (!conditions.isEmpty())
				{
					var conditionArray = new JsonArray();
					for (var condition : conditions)
					{
						var conditionElement = new JsonObject();

						condition.serialize(conditionElement);

						conditionArray.add(conditionElement);
					}
					entryElement.add("conditions", conditionArray);
				}

				if (!parameters.isEmpty())
				{
					var paramElement = new JsonObject();

					for (var pair : parameters.entrySet())
					{
						if (pair.getValue() instanceof Number)
							paramElement.addProperty(pair.getKey(), (Number)pair.getValue());
						else if (pair.getValue() instanceof Boolean)
							paramElement.addProperty(pair.getKey(), (Boolean)pair.getValue());
						else if (pair.getValue() instanceof Character)
							paramElement.addProperty(pair.getKey(), (Character)pair.getValue());
						else if (pair.getValue() != null)
							paramElement.addProperty(pair.getKey(), pair.getValue().toString());
					}

					entryElement.add("parameters", paramElement);
				}
			}
		}

		public static class CountFunction extends Function
		{
			public static class Range
			{
				float min;
				float max;
				Identifier type;

				public Range(float min, float max, Identifier type)
				{
					this.min = min;
					this.max = max;
					this.type = type;
				}
			}

			public Integer count = null;
			public Range range = null;

			public CountFunction(int count)
			{
				super(new Identifier("set_count"));
				this.count = count;
			}

			public CountFunction(Range count)
			{
				super(new Identifier("set_count"));
				this.range = count;
			}

			@Override
			public void serialize(JsonObject entryElement)
			{
				super.serialize(entryElement);

				if (count != null)
					entryElement.addProperty("count", count);

				if (range != null)
				{
					var countElement = new JsonObject();

					countElement.addProperty("min", range.min);
					countElement.addProperty("max", range.max);
					countElement.addProperty("type", range.type.toString());

					entryElement.add("count", countElement);
				}
			}
		}

		public static class Condition
		{
			Identifier condition;
			Identifier block;
			HashMap<String, Object> properties;

			public Condition(Identifier condition)
			{
				this.condition = condition;
				this.properties = new HashMap<>();
			}

			public Condition block(Identifier block)
			{
				this.block = block;
				return this;
			}

			public Condition property(String key, Object value)
			{
				this.properties.put(key, value);
				return this;
			}

			public void serialize(JsonObject entryElement)
			{
				entryElement.addProperty("condition", condition.toString());

				if (block != null)
					entryElement.addProperty("block", block.toString());

				if (!properties.isEmpty())
				{
					var paramElement = new JsonObject();

					for (var pair : properties.entrySet())
					{
						if (pair.getValue() instanceof Number)
							paramElement.addProperty(pair.getKey(), (Number)pair.getValue());
						else if (pair.getValue() instanceof Boolean)
							paramElement.addProperty(pair.getKey(), (Boolean)pair.getValue());
						else if (pair.getValue() instanceof Character)
							paramElement.addProperty(pair.getKey(), (Character)pair.getValue());
						else if (pair.getValue() != null)
							paramElement.addProperty(pair.getKey(), pair.getValue().toString());
					}

					entryElement.add("properties", paramElement);
				}
			}
		}

		public final int rolls;
		public final ArrayList<Entry> entries;
		public final ArrayList<Condition> conditions;

		public Pool(int rolls)
		{
			this.rolls = rolls;
			this.entries = new ArrayList<>();
			this.conditions = new ArrayList<>();
		}

		public Pool entry(Entry entry)
		{
			entries.add(entry);
			return this;
		}

		public Pool condition(Identifier condition)
		{
			conditions.add(new Condition(condition));
			return this;
		}

		public Pool condition(Condition condition)
		{
			conditions.add(condition);
			return this;
		}
	}

	public final Identifier filename;
	public final Identifier type;
	public final List<Pool> pools;
	public final ArrayList<Pool.Function> functions;

	public LootTableFile(Identifier filename, Identifier type)
	{
		this.type = type;
		this.filename = filename;
		this.pools = new ArrayList<>();
		this.functions = new ArrayList<>();
	}

	public LootTableFile pool(Pool pool)
	{
		pools.add(pool);
		return this;
	}

	public LootTableFile function(Pool.Function function)
	{
		functions.add(function);
		return this;
	}

	public JsonElement build()
	{
		var root = new JsonObject();
		root.addProperty("type", type.toString());

		var poolArray = new JsonArray();

		for (var pool : pools)
		{
			var poolElement = new JsonObject();
			poolElement.addProperty("rolls", pool.rolls);

			var entryArray = new JsonArray();
			for (var entry : pool.entries)
			{
				var entryElement = new JsonObject();
				entry.serialize(entryElement);
				entryArray.add(entryElement);
			}
			poolElement.add("entries", entryArray);

			if (!pool.conditions.isEmpty())
			{
				var conditionArray = new JsonArray();
				for (var condition : pool.conditions)
				{
					var conditionElement = new JsonObject();
					condition.serialize(conditionElement);
					conditionArray.add(conditionElement);
				}
				poolElement.add("conditions", conditionArray);
			}

			poolArray.add(poolElement);
		}

		root.add("pools", poolArray);

		if (!functions.isEmpty())
		{
			var conditionArray = new JsonArray();
			for (var function : functions)
			{
				var functionElement = new JsonObject();
				function.serialize(functionElement);
				conditionArray.add(functionElement);
			}
			root.add("functions", conditionArray);
		}

		return root;
	}
}
