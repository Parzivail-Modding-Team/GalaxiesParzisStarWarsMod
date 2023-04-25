package com.parzivail.pswgtcw;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.api.PswgClientAddon;
import com.parzivail.pswg.client.render.armor.ArmorRenderer;
import com.parzivail.pswgtcw.client.CloneArmorTransformer;
import com.parzivail.updater.GithubReleaseEntry;
import com.parzivail.updater.UpdateChecker;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;

public class PswgTcwAddonClient implements PswgClientAddon
{
	private static GithubReleaseEntry REMOTE_VERSION = null;

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

		checkVersion();

		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
			if (client.player == null)
				return;

			if (REMOTE_VERSION == null)
				return;

			Text versionText = Text.literal(REMOTE_VERSION.name)
			                       .styled((style) -> style
					                       .withItalic(true)
			                       );

			var url = "https://modrinth.com/mod/pswg-addon-clonewars/" + REMOTE_VERSION.tag_name;

			Text urlText = Text.literal(url)
			                   .styled((style) -> style
					                   .withColor(TextColor.fromRgb(0x5bc0de))
					                   .withUnderline(true)
					                   .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url))
					                   .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("PSWG Addon: Clone Wars on Modrinth")))
			                   );
			client.player.sendMessage(Text.translatable("msg.pswg_addon_clonewars.update", versionText, urlText), false);
		});
	}

	public static void checkVersion()
	{
		if (Resources.isUpdateCheckDisabled())
			return;

		REMOTE_VERSION = UpdateChecker.getRemoteVersion(PswgTcwAddon.MODID, "Parzivail-Modding-Team/pswg-addon-clonewars");
	}
}
