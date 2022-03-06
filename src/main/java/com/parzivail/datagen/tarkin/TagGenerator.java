package com.parzivail.datagen.tarkin;

import net.minecraft.tag.Tag;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.List;

public class TagGenerator
{
	public static TagGenerator forTag(String tagDataType, TagKey<?> tag, Identifier id)
	{
		return new TagGenerator(tagDataType, tag, new Tag.TagEntry(id));
	}

	public static TagGenerator forObject(String tagDataType, TagKey<?> tag, Identifier id)
	{
		return new TagGenerator(tagDataType, tag, new Tag.ObjectEntry(id));
	}

	public static TagGenerator forItemTag(TagKey<?> tag, TagKey<?> other)
	{
		return new TagGenerator("tags/items", tag, new Tag.TagEntry(other.id()));
	}

	public static TagGenerator forBlockTag(TagKey<?> tag, TagKey<?> other)
	{
		return new TagGenerator("tags/blocks", tag, new Tag.TagEntry(other.id()));
	}

	private final String tagDataType;
	private final TagKey<?> tag;
	private final Tag.Entry entry;

	TagGenerator(String tagDataType, TagKey<?> tag, Tag.Entry entry)
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
