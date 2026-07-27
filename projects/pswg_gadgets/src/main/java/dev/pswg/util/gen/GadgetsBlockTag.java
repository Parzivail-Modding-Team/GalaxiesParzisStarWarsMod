package dev.pswg.util.gen;

import dev.pswg.container.GadgetsBlocks;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
/**
 * Interface used in {@link GadgetsBlocks} to automatically add blocks to Gadgets tags
 */
public @interface GadgetsBlockTag
{
	DataGenGadgetsBlockTag[] blockTags() default {};
}
