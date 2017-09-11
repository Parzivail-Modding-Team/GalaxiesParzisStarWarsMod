package com.parzivail.swg.command;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by colby on 9/10/2017.
 */
@Retention(RetentionPolicy.RUNTIME)
@interface Parameter
{
	int index();
}
