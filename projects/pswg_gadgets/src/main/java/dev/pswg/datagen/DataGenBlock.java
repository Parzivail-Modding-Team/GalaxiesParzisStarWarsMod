package dev.pswg.datagen;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DataGenBlock
{
	DataGenBlockModel model() default DataGenBlockModel.CubeAll;

	DataGenItemGroup itemGroup() default DataGenItemGroup.ConstructionBlock;

	String langOverride() default "";

	String dataGenModelKey() default "";

	DGBlockTag[] tags() default {};
	DGItemTag[] itemTags() default {};
	DGBlockRotation rotation() default DGBlockRotation.Default;
}
