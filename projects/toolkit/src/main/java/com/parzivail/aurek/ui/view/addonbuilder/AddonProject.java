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
		var username = MinecraftClient.getInstance().getSession().getUsername();
		addonId = new ImString(username + "-pswg-addon", 32);

		addonAuthors.add(username);
	}

	public String getId(String path)
	{
		return addonId.get() + ':' + path;
	}
}
