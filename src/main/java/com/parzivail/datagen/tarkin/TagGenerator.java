package com.parzivail.datagen.tarkin;

import com.google.gson.JsonArray;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class TagGenerator
{
	public interface Entry extends Tag.Entry {
		@Override
		default <T> boolean resolve(Function<Identifier, Tag<T>> tagGetter, Function<Identifier, T> objectGetter, Consumer<T> collector) {
			return true;
		}

		@Override
		default boolean canAdd(Predicate<Identifier> objectExistsTest, Predicate<Identifier> tagExistsTest) {
			return true;
		}
	}
	public record TagEntry(Identifier id) implements Entry {
		@Override
		public void addToJson(JsonArray json)
		{
			json.add("#" + this.id);
		}
	}

	public record ObjectEntry(Identifier id) implements Entry {
		@Override
		public void addToJson(JsonArray json)
		{
			json.add(this.id.toString());
		}
	}

	public static TagGenerator forTag(String tagDataType, TagKey<?> tag, Identifier id)
	{
		return new TagGenerator(tagDataType, tag, new TagEntry(id));
	}

	public static TagGenerator forObject(String tagDataType, TagKey<?> tag, Identifier id)
	{
		return new TagGenerator(tagDataType, tag, new ObjectEntry(id));
	}

	public static TagGenerator forItemTag(TagKey<?> tag, TagKey<?> other)
	{
		return new TagGenerator("tags/items", tag, new TagEntry(other.id()));
	}

	public static TagGenerator forBlockTag(TagKey<?> tag, TagKey<?> other)
	{
		return new TagGenerator("tags/blocks", tag, new TagEntry(other.id()));
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
