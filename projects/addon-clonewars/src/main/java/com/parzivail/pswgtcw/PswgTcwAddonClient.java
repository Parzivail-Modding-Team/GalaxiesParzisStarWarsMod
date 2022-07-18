package com.parzivail.pswgtcw;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.api.PswgClientAddon;
import com.parzivail.pswg.client.render.armor.ArmorRenderer;
import com.parzivail.pswgtcw.client.ClonetrooperArmorTransformer;

public class PswgTcwAddonClient implements PswgClientAddon
{
	@Override
	public void onPswgClientReady()
	{
		var clonetrooperArmorModelKey = PswgTcwAddon.id("clonetrooper");
		ArmorRenderer.register(
				TcwItems.Armor.Clonetrooper,
				clonetrooperArmorModelKey,
				Resources.id("armor/clonetrooper"),
				Resources.id("textures/armor/clonetrooper.png")
		);
		ArmorRenderer.registerTransformer(clonetrooperArmorModelKey, new ClonetrooperArmorTransformer(false));
	}
}
