package com.parzivail.datagen.tarkin.config;

import com.parzivail.datagen.tarkin.BuiltAsset;
import com.parzivail.datagen.tarkin.ItemGenerator;
import com.parzivail.pswgtcw.TcwItems;

import java.util.List;

public class TcwTarkin
{
	public static void build(List<BuiltAsset> assets)
	{
		ItemGenerator.armor(TcwItems.Armor.Phase1Clone, assets);
		ItemGenerator.armor(TcwItems.Armor.Phase2Clone, assets);
	}
}
