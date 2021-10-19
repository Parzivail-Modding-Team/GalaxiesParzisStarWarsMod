package com.parzivail.datagen.tarkin;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.List;

public class LanguageBuilder
{
	private final Identifier locale;
	private final StringBuilder data;

	public LanguageBuilder(Identifier locale)
	{
		this(locale, new StringBuilder());
	}

	public LanguageBuilder(Identifier locale, StringBuilder builder)
	{
		this.locale = locale;
		this.data = builder;
	}

	public LanguageBuilder dot(String value)
	{
		return new LanguageBuilder(locale, new StringBuilder(data).append('.').append(value));
	}

	public LanguageBuilder modid()
	{
		return dot("%1$s");
	}

	public LanguageBuilder cloneWithRoot(String root)
	{
		return new LanguageBuilder(locale, new StringBuilder(data).append(root));
	}

	public LanguageBuilder container(String value)
	{
		return cloneWithRoot("container").modid().dot(value);
	}

	public LanguageBuilder category(String value)
	{
		return cloneWithRoot("category").modid().dot(value);
	}

	public LanguageBuilder cause_of_death(String value)
	{
		return cloneWithRoot("death").dot("attack").modid().dot(value);
	}

	public LanguageBuilder command(String value)
	{
		return cloneWithRoot("command").modid().dot(value);
	}

	public LanguageBuilder entity(EntityType<? extends Entity> entity)
	{
		var entityId = Registry.ENTITY_TYPE.getId(entity);
		return cloneWithRoot("entity").modid().dot(entityId.getPath());
	}

	public LanguageBuilder screen(String value)
	{
		return cloneWithRoot("screen").modid().dot(value);
	}

	public LanguageBuilder item(String value)
	{
		return cloneWithRoot("item").modid().dot(value);
	}

	public LanguageBuilder tooltip(String value)
	{
		return cloneWithRoot("tooltip").modid().dot(value);
	}

	public LanguageBuilder itemGroup(String value)
	{
		return cloneWithRoot("itemGroup").modid().dot(value);
	}

	public LanguageBuilder key(KeyBinding key)
	{
		return new LanguageBuilder(locale, new StringBuilder(key.getTranslationKey()));
	}

	public LanguageBuilder message(String value)
	{
		return cloneWithRoot("msg").modid().dot(value);
	}

	public LanguageBuilder keyCategory(String value)
	{
		return cloneWithRoot("key").dot("category").dot(value);
	}

	public LanguageProvider getProvider()
	{
		var key = String.format(data.toString(), locale.getNamespace());
		return new LanguageProvider(locale, key, key);
	}

	public void build(List<BuiltAsset> assets)
	{
		assets.add(getProvider().build());
	}
}
