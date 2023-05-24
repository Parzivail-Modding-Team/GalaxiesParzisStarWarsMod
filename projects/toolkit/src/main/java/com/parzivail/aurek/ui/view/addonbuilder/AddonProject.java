package com.parzivail.aurek.ui.view.addonbuilder;

import imgui.type.ImString;
import net.minecraft.client.MinecraftClient;

import java.util.ArrayList;

public class AddonProject
{
	public final ImString editingAuthor = new ImString();

	public final ImString addonId;
	public final ImString addonVersion = new ImString("1.0.0", 32);
	public final ImString addonDescription = new ImString("Adds new features to Galaxies: Parzi's Star Wars Mod", 256);
	public final ArrayList<String> addonAuthors = new ArrayList<>();

	public final ArrayList<IAddonFeature> addonFeatures = new ArrayList<>();

	public AddonProject()
	{
		var profile = MinecraftClient.getInstance().getSession().getProfile();
		addonId = new ImString("%s-pswg-addon".formatted(profile.getName()), 32);

		addonAuthors.add(profile.getName());
	}

	public String getId(String path)
	{
		return "%s:%s".formatted(addonId.get(), path);
	}
}
