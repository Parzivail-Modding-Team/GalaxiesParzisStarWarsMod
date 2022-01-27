package com.parzivail.datagen.tarkin;

import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

import java.util.List;

public class TagGenerator
{
	public static TagGenerator forTag(String tagDataType, Tag.Identified<?> tag, Identifier id)
	{
		return new TagGenerator(tagDataType, tag, new Tag.TagEntry(id));
	}

	public static TagGenerator forObject(String tagDataType, Tag.Identified<?> tag, Identifier id)
	{
		return new TagGenerator(tagDataType, tag, new Tag.ObjectEntry(id));
	}

	public static TagGenerator forItemTag(Tag.Identified<?> tag, Tag.Identified<?> other)
	{
		return new TagGenerator("tags/items", tag, new Tag.TagEntry(other.getId()));
	}

	public static TagGenerator forBlockTag(Tag.Identified<?> tag, Tag.Identified<?> other)
	{
		return new TagGenerator("tags/blocks", tag, new Tag.TagEntry(other.getId()));
	}

	private final String tagDataType;
	private final Tag.Identified<?> tag;
	private final Tag.Entry entry;

	TagGenerator(String tagDataType, Tag.Identified<?> tag, Tag.Entry entry)
	{
		this.tagDataType = tagDataType;
		this.tag = tag;
		this.entry = entry;
	}

	public void build(List<BuiltAsset> assets)
	{
		assets.add(BuiltAsset.tag(IdentifierUtil.concat(tagDataType + "/", tag.getId()), entry));
	}
}
