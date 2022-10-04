package com.parzivail.util.meta;

import org.spongepowered.asm.mixin.Mixin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that a method declaration is intended to override a
 * method declaration in a supertype in a situation where the
 * target method might not exist in the supertype (e.g. in the
 * case of a {@linkplain Mixin} injecting a method into a supertype
 * at runtime, as is done with lighting handlers in Iris)
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface ImplicitOverride
{
	/**
	 * Indicates the method that this method implicitly
	 * overrides
	 *
	 * @return the implicitly overridden method
	 */
	String value() default "";
}
