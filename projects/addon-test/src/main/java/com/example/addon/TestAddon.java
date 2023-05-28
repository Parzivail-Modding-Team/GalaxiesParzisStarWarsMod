package com.example.addon;

import com.parzivail.pswg.api.PswgAddon;
import com.parzivail.pswg.api.PswgContent;
import com.parzivail.pswg.api.RegisterResult;
import com.parzivail.util.Lumberjack;
import com.parzivail.util.math.ColorUtil;

public class TestAddon implements PswgAddon
{
	public static final String MODID = "pswg-addon-test";
	public static final Lumberjack LOG = new Lumberjack(MODID);

	@Override
	public void onPswgReady()
	{
		LOG.log("Hello, World!");
	}

	@Override
	public void onPswgStarting()
	{
		// Example event handler, modifying Luke's ROTH saber preset
		// from green to magenta
		PswgContent.LIGHTSABER_REGISTERED.register((id, descriptor) -> {
			if (id.toString().equals("pswg:luke_rotj"))
				descriptor.bladeColor = ColorUtil.packHsv(0.76f, 1, 1);
			return RegisterResult.Pass;
		});
	}
}
