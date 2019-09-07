package com.parzivail.swg.register;

import com.parzivail.pr3.Pr3Model;
import com.parzivail.swg.Resources;

import java.io.IOException;

public class Pr3ModelRegister
{
	public static Pr3Model XwingT65b;

	public static void load() throws IOException
	{
		XwingT65b = Pr3Model.load(Resources.location("models/rigged/xwing_t65b.pr3"), Resources.location("textures/model/xwing_t65b.png"));
	}
}
