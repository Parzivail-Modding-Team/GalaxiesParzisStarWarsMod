package com.parzivail.datagen.tarkin;

import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.List;

public class TagGenerator
{
	public static TagGenerator forTag(String tagDataType, TagKey<?> tag, Identifier id)
	{
		return new TagGenerator(tagDataType, tag, TagEntry.createTag(id));
	}

	public static TagGenerator forObject(String tagDataType, TagKey<?> tag, Identifier id)
	{
		return new TagGenerator(tagDataType, tag, TagEntry.create(id));
	}

	public static TagGenerator forItemTag(TagKey<?> parent, TagKey<?> child)
	{
		return forTag("tags/items", parent, child.id());
	}

	public static TagGenerator forBlock(TagKey<?> parent, TagKey<?> child)
	{
		return forTag("tags/blocks", parent, child.id());
	}

	public static void addBlocksToTag(TagKey<?> parent, List<BuiltAsset> assets, Block... children)
	{
		for (var block : children)
			forObject("tags/blocks", parent, Registries.BLOCK.getId(block)).build(assets);
	}

	private final String tagDataType;
	private final TagKey<?> tag;
	private final TagEntry entry;

	TagGenerator(String tagDataType, TagKey<?> tag, TagEntry entry)
	{
		this.tagDataType = tagDataType;
		this.tag = tag;
		this.entry = entry;
	}

	public void build(List<BuiltAsset> assets)
	{
		assets.add(BuiltAsset.tag(tag.id().withPrefixedPath(tagDataType + "/"), entry));
	}
}
