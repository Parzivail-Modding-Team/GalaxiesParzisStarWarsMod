package com.parzivail.aurek.ui.view.addonbuilder;

import com.parzivail.aurek.ToolkitClient;
import com.parzivail.pswg.Resources;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.SemanticVersion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record FabricModJson(int schemaVersion, String id, String version, String description, List<String> authors, String environment, Map<String, String> depends,
                            Map<String, String> recommends, Map<String, Object> custom)
{
	public static FabricModJson universal(String id, String version, String description, List<String> authors)
	{
		authors = new ArrayList<>(authors);
		authors.add("PSWG Team");

		var depends = new HashMap<String, String>();

		var pswgContainer = FabricLoader.getInstance().getModContainer(Resources.MODID).orElseThrow(() -> new RuntimeException("Could not get PSWG mod container"));
		if (!FabricLoader.getInstance().isDevelopmentEnvironment() && pswgContainer.getMetadata().getVersion() instanceof SemanticVersion pswgVersion)
			depends.put(Resources.MODID, String.format(">=%s", pswgVersion));
		else
			depends.put(Resources.MODID, "*");

		var recommends = new HashMap<String, String>();
		recommends.put("aurek", "*");

		var custom = new HashMap<String, Object>();

		var ownContainer = FabricLoader.getInstance().getModContainer(ToolkitClient.MODID).orElseThrow(() -> new RuntimeException("Could not get own mod container"));
		var ownVersion = ownContainer.getMetadata().getVersion();
		custom.put("aurek", new AurekGeneratorMetadata(ownVersion instanceof SemanticVersion v ? v.toString() : "unknown"));

		return new FabricModJson(1, id, version, description, authors, "*", depends, recommends, custom);
	}
}
