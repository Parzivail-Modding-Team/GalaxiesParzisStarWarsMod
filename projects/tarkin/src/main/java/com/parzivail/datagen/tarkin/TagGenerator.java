package com.parzivail.datagen.tarkin;

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

	public static TagGenerator forItemTag(TagKey<?> tag, TagKey<?> other)
	{
		return forTag("tags/items", tag, other.id());
	}

	public static TagGenerator forBlockTag(TagKey<?> tag, TagKey<?> other)
	{
		return forTag("tags/blocks", tag, other.id());
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
		assets.add(BuiltAsset.tag(IdentifierUtil.concat(tagDataType + "/", tag.id()), entry));
	}
}
