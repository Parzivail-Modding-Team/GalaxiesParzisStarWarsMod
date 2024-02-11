package com.parzivail.tarkin.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TarkinItem
{
	TrModel model() default TrModel.Sprite;

	TrLang lang() default TrLang.Item;

	TrItemTag[] tags() default {};
}
