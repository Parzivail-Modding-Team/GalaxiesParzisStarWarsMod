package dev.pswg.util.gen;

import dev.pswg.container.GadgetsItems;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
/**
 * Interface used in {@link GadgetsItems} to automatically add items to Gadgets tags
 */
public @interface GadgetsItemTag
{
	DataGenGadgetsItemTag[] itemTags() default {};
}
