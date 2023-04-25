package com.parzivail.pswgtcw;

import com.google.gson.GsonBuilder;
import com.parzivail.pswg.Galaxies;
import com.parzivail.pswg.Resources;
import com.parzivail.pswg.api.PswgClientAddon;
import com.parzivail.pswg.client.render.armor.ArmorRenderer;
import com.parzivail.pswgtcw.client.CloneArmorTransformer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.SemanticVersion;
import net.fabricmc.loader.api.Version;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;

import java.io.InputStreamReader;
import java.net.URL;

public class PswgTcwAddonClient implements PswgClientAddon
{
	private static Resources.GithubReleaseEntry REMOTE_VERSION = null;

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
			if (client.player != null)
			{
				if (REMOTE_VERSION != null)
				{
					Text versionText = Text.literal(REMOTE_VERSION.name)
					                       .styled((style) -> style
							                       .withItalic(true)
					                       );

					var url = "https://www.curseforge.com/minecraft/mc-mods/pswg-addon-clonewars";

					Text urlText = Text.literal(url)
					                   .styled((style) -> style
							                   .withColor(TextColor.fromRgb(0x5bc0de))
							                   .withUnderline(true)
							                   .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url))
							                   .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("PSWG Addon: Clone Wars on CurseForge")))
					                   );
					client.player.sendMessage(Text.translatable("msg.pswg_addon_clonewars.update", versionText, urlText), false);
				}
			}
		});
	}

	public static void checkVersion()
	{
		try
		{
			var container = FabricLoader.getInstance().getModContainer(PswgTcwAddon.MODID).orElseThrow(() -> new Exception("Could not get own mod container"));

			//FabricLoader.getInstance().isDevelopmentEnvironment() ||
			if (!(container.getMetadata().getVersion() instanceof SemanticVersion ownVersion))
			{
				Galaxies.LOG.log("Will not perform version check in a development environment");
				return;
			}

			var con = new URL("https://api.github.com/repos/Parzivail-Modding-Team/pswg-addon-clonewars/releases").openConnection();
			con.setConnectTimeout(3000);
			con.setReadTimeout(3000);
			var isr = new InputStreamReader(con.getInputStream());

			var g = new GsonBuilder().create();

			var entries = g.fromJson(isr, Resources.GithubReleaseEntry[].class);

			if (entries.length == 0)
				throw new Exception("No versions present on remote");

			var mostRecentRelease = entries[0];

			if (isRemoteVersionNewer(ownVersion, SemanticVersion.parse(mostRecentRelease.tag_name)))
			{
				REMOTE_VERSION = mostRecentRelease;

				Galaxies.LOG.warn("A new version is available at https://www.curseforge.com/minecraft/mc-mods/pswg-addon-clonewars: %s (vs: %s)", REMOTE_VERSION.name, container.getMetadata().getVersion());
			}
		}
		catch (Exception e)
		{
			Galaxies.LOG.error("Failed to check for updates: %s", e.getMessage());
		}
	}

	private static boolean isRemoteVersionNewer(SemanticVersion local, SemanticVersion remote)
	{
		return local.compareTo((Version)remote) < 0;
	}
}
