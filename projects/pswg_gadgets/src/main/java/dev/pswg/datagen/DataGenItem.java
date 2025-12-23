package dev.pswg.datagen;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DataGenItem
{
	boolean genModel() default true;

	boolean wiz() default false;
	DGItemTag[] itemTags() default {};

	ItemModel model() default ItemModel.generated;

	DataGenItemGroup itemGroup() default DataGenItemGroup.Items;

	String langOverride() default "";

	String textureOverride() default "";

	String overlayTextureOverride() default "";

	boolean invertLayer() default false;
}
