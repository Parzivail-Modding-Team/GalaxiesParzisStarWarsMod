package com.parzivail.tarkin.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TarkinBlock
{
	TrLang lang() default TrLang.Block;

	TrState state() default TrState.Singleton;

	TrModel model() default TrModel.Cube;

	TrModel itemModel() default TrModel.Block;

	TrLoot loot() default TrLoot.SingleSelf;

	TrBlockTag[] tags() default { TrBlockTag.PickaxeMineable };

	TrItemTag[] itemTags() default {};
}
