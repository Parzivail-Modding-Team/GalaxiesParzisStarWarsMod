package com.parzivail.datagen.tarkin;

import com.parzivail.util.client.TooltipUtil;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Identifier;

import java.util.List;

public class LanguageBuilder
{
	private final Identifier locale;
	private final String data;

	public LanguageBuilder(Identifier locale)
	{
		this(locale, "");
	}

	public LanguageBuilder(Identifier locale, String builder)
	{
		this.locale = locale;
		this.data = builder;
	}

	public LanguageBuilder dot(String value)
	{
		return new LanguageBuilder(locale, data + '.' + value);
	}

	public LanguageBuilder modid()
	{
		return dot("%1$s");
	}

	public LanguageBuilder entry(String root)
	{
		return new LanguageBuilder(locale, data + root);
	}

	public LanguageBuilder container(String value)
	{
		return entry("container").modid().dot(value);
	}

	public LanguageBuilder category(String value)
	{
		return entry("category").modid().dot(value);
	}

	public LanguageBuilder cause_of_death(RegistryKey<DamageType> value)
	{
		return entry("death").dot("attack").modid().dot(value.getValue().getPath());
	}

	public LanguageBuilder command(String value)
	{
		return entry("command").modid().dot(value);
	}

	public LanguageBuilder entity(EntityType<? extends Entity> entity)
	{
		var entityId = Registries.ENTITY_TYPE.getId(entity);
		return entry("entity").modid().dot(entityId.getPath());
	}

	public LanguageBuilder screen(String value)
	{
		return entry("screen").modid().dot(value);
	}

	public LanguageBuilder item(String value)
	{
		return entry("item").modid().dot(value);
	}

	public LanguageBuilder tooltip(String value)
	{
		return entry("tooltip").modid().dot(value);
	}

	public LanguageBuilder lore(Item item)
	{
		return entry(TooltipUtil.getLoreKey(item));
	}

	public LanguageBuilder status(Item item)
	{
		return entry(TooltipUtil.getStatusKey(item));
	}

	public LanguageBuilder itemGroup(ItemGroup value)
	{
		return new LanguageBuilder(locale, ((TranslatableTextContent)value.getDisplayName().getContent()).getKey());
	}

	public LanguageBuilder key(KeyBinding key)
	{
		return new LanguageBuilder(locale, key.getTranslationKey());
	}

	public LanguageBuilder message(String value)
	{
		return entry("msg").modid().dot(value);
	}

	public LanguageBuilder keyCategory(String value)
	{
		return entry("key").dot("category").dot(value);
	}

	public LanguageProvider getProvider()
	{
		return getProvider(null);
	}

	public LanguageProvider getProvider(String defaultValue)
	{
		var key = String.format(data, locale.getNamespace());
		return new LanguageProvider(locale, key, defaultValue == null ? key : defaultValue);
	}

	public void build(List<BuiltAsset> assets)
	{
		build(assets, null);
	}

	public void build(List<BuiltAsset> assets, String defaultValue)
	{
		assets.add(getProvider(defaultValue).build());
	}
}
