package dev.pswg.autoreg;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ServerBlockRegistryData
{
	int fireBurn() default 0;

	int fireSpread() default 0;
}
