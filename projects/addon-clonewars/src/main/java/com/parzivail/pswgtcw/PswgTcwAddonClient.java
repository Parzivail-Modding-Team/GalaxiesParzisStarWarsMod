package com.parzivail.pswgtcw;

import com.parzivail.pswg.api.PswgClientAddon;
import com.parzivail.pswg.client.render.armor.ArmorRenderer;
import com.parzivail.pswgtcw.client.CloneArmorTransformer;

public class PswgTcwAddonClient implements PswgClientAddon
{
	@Override
	public void onPswgClientReady()
	{
		var cloneArmorAssets = new ArmorRenderer.Assets(
				PswgTcwAddon.id("armor/clonetrooper"),
				PswgTcwAddon.id("textures/armor/clonetrooper.png")
		);

		var clonePhase1ArmorModelKey = PswgTcwAddon.id("phase1_clone");
		ArmorRenderer.register(
				TcwItems.Armor.Phase1Clone,
				clonePhase1ArmorModelKey,
				cloneArmorAssets,
				ArmorRenderer.Metadata.HIDE_CHEST_HIDE_HAIR
		);
		ArmorRenderer.registerTransformer(clonePhase1ArmorModelKey, new CloneArmorTransformer(false));

		var clonePhase2ArmorModelKey = PswgTcwAddon.id("phase2_clone");
		ArmorRenderer.register(
				TcwItems.Armor.Phase2Clone,
				clonePhase2ArmorModelKey,
				cloneArmorAssets,
				ArmorRenderer.Metadata.HIDE_CHEST_HIDE_HAIR
		);
		ArmorRenderer.registerTransformer(clonePhase2ArmorModelKey, new CloneArmorTransformer(true));
	}
}
