package dev.pswg.codecgenerator;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({})
@Retention(RetentionPolicy.SOURCE)
public @interface CodecSource
{
	Class<?> source();

	String member();
}
