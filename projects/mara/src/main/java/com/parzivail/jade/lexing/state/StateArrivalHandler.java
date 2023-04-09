package com.parzivail.jade.lexing.state;

import com.parzivail.jade.lexing.TokenizeState;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface StateArrivalHandler
{
	TokenizeState value();
}
