package com.parzivail.tarkin.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TarkinBlock
{
	TarkinLangPreset lang() default TarkinLangPreset.Block;

	TarkinBlockStatePreset state() default TarkinBlockStatePreset.Singleton;

	TarkinModelFilePreset model() default TarkinModelFilePreset.Cube;

	TarkinModelFilePreset itemModel() default TarkinModelFilePreset.Block;

	TarkinLootTablePreset loot() default TarkinLootTablePreset.SingleSelf;

	TarkinBlockTagPreset[] tags() default { TarkinBlockTagPreset.PickaxeMineable };

	TarkinItemTagPreset[] itemTags() default {};
}
