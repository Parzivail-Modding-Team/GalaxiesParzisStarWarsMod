package com.parzivail.datagen.tarkin;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
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

	public static LootTableFile count(Block block, ItemConvertible drop, Pool.Entry.CountFunction count)
	{
		var reg = AssetGenerator.getRegistryName(drop);
		return ofPool(block,
		              new Pool(1)
				              .entry(new Pool.Entry(new Identifier("alternatives"))
						                     .child(new Pool.Entry(new Identifier("item"), reg)
								                            .function(count)
								                            .function(new Pool.Entry.Function(new Identifier("explosion_decay")))
						                     )
				              )
		);
	}

	public static LootTableFile many(Block block, ItemConvertible drop, int count)
	{
		return count(block, drop, new Pool.Entry.CountFunction(count));
	}

	public static LootTableFile many(Block block, ItemConvertible drop, Pool.Entry.CountFunction.Range range)
	{
		return count(block, drop, new Pool.Entry.CountFunction(range));
	}

	public static LootTableFile singleFortuneBonus(Block block, ItemConvertible drop)
	{
		var reg = AssetGenerator.getRegistryName(drop);
		return ofPool(block,
		              new Pool(1)
				              .entry(new Pool.Entry(new Identifier("alternatives"))
						                     .child(new Pool.Entry(new Identifier("item"), reg)
								                            .function(new Pool.Entry.Function(new Identifier("apply_bonus"))
										                                      .enchantment(new Identifier("fortune"))
										                                      .formula(new Identifier("ore_drops"))
								                            )
								                            .function(new Pool.Entry.Function(new Identifier("explosion_decay")))
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

			public static class Function
			{
				Identifier function;
				Identifier enchantment;
				Identifier formula;
				HashMap<String, Object> parameters;

				public Function(Identifier function)
				{
					this.function = function;
					this.parameters = new HashMap<>();
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

			public final Identifier type;
			public final Identifier name;
			public final ArrayList<Entry> children;

			public final ArrayList<Function> functions;

			private Entry(Identifier type, Identifier name)
			{
				this.type = type;
				this.name = name;
				children = new ArrayList<>();

				functions = new ArrayList<>();
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
		}

		public static class Condition
		{
			public final Identifier condition;

			private Condition(Identifier condition)
			{
				this.condition = condition;
			}

			public void serialize(JsonObject element)
			{
				element.addProperty("condition", condition.toString());
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
	}

	public final Identifier filename;
	public final Identifier type;
	public final List<Pool> pools;

	public LootTableFile(Identifier filename, Identifier type)
	{
		this.type = type;
		this.filename = filename;
		this.pools = new ArrayList<>();
	}

	public LootTableFile pool(Pool pool)
	{
		pools.add(pool);
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

		return root;
	}
}
