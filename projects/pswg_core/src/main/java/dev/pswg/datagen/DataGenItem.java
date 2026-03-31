package dev.pswg.datagen;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DataGenItem
{
	boolean wiz() default false;
	DataGenItemTag[] itemTags() default {};

	ItemModel model() default ItemModel.GENERATED;

	DataGenItemGroup itemGroup() default DataGenItemGroup.ITEMS;

	String langOverride() default "";

	String textureOverride() default "";

	String overlayTextureOverride() default "";

	boolean invertLayer() default false;
}
