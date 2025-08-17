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

	ItemModel model() default ItemModel.generated;

	DataGenItemGroup itemGroup() default DataGenItemGroup.Items;

	String langOverride() default "";
}
