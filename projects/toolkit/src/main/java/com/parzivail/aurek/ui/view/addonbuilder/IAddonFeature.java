package com.parzivail.aurek.ui.view.addonbuilder;

import java.util.Map;
import java.util.zip.ZipOutputStream;

public interface IAddonFeature
{
	FeatureType getType();

	String getName();

	String getId();

	void serialize(String domain, ZipOutputStream zip);

	void appendLanguageKeys(String domain, Map<String, String> languageKeys);
}
