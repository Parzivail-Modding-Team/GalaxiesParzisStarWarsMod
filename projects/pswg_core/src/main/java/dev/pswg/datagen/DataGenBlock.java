package dev.pswg.datagen;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DataGenBlock
{
	boolean addItemTranslation() default true;

	DataGenBlockModel model() default DataGenBlockModel.CUBE_ALL;

	DataGenItemGroup itemGroup() default DataGenItemGroup.CONSTRUCTION_BLOCK;

	String langOverride() default "";

	String dataGenModelKey() default "";

	DGBlockTag[] blockTags() default {};
	DGItemTag[] itemTags() default {};

	DGBlockRotation rotation() default DGBlockRotation.DEFAULT;
}
