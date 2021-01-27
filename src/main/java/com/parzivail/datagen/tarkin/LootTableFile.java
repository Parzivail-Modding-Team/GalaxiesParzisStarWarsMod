package com.parzivail.datagen.tarkin;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.block.Block;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class LootTableFile
{
	public static LootTableFile basic(Block block)
	{
		Identifier reg = AssetGenerator.getRegistryName(block);
		return new LootTableFile(IdentifierUtil.concat("blocks/", reg), new Identifier("block"))
				.pool(
						new Pool(1)
								.condition(new Identifier("survives_explosion"))
								.entry(new Identifier("item"), reg)
				);
	}

	public static class Pool
	{
		public static class Entry
		{
			public final Identifier type;
			public final Identifier name;

			private Entry(Identifier type, Identifier name)
			{
				this.type = type;
				this.name = name;
			}
		}

		public static class Condition
		{
			public final Identifier condition;

			private Condition(Identifier condition)
			{
				this.condition = condition;
			}
		}

		public final int rolls;
		public final List<Entry> entries;
		public final List<Condition> conditions;

		public Pool(int rolls)
		{
			this.rolls = rolls;
			this.entries = new ArrayList<>();
			this.conditions = new ArrayList<>();
		}

		public Pool entry(Identifier type, Identifier name)
		{
			entries.add(new Entry(type, name));
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
		JsonObject root = new JsonObject();
		root.addProperty("type", type.toString());

		JsonArray poolArray = new JsonArray();

		for (Pool pool : pools)
		{
			JsonObject poolElement = new JsonObject();
			poolElement.addProperty("rolls", pool.rolls);

			JsonArray entryArray = new JsonArray();
			for (Pool.Entry entry : pool.entries)
			{
				JsonObject entryElement = new JsonObject();

				entryElement.addProperty("type", entry.type.toString());
				entryElement.addProperty("name", entry.name.toString());

				entryArray.add(entryElement);
			}
			poolElement.add("entries", entryArray);

			JsonArray conditionArray = new JsonArray();
			for (Pool.Condition condition : pool.conditions)
			{
				JsonObject conditionElement = new JsonObject();

				conditionElement.addProperty("condition", condition.condition.toString());

				conditionArray.add(conditionElement);
			}
			poolElement.add("conditions", conditionArray);

			poolArray.add(poolElement);
		}

		root.add("pools", poolArray);

		return root;
	}
}
