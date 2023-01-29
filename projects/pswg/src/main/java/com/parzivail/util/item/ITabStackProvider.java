package com.parzivail.util.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;

public interface ITabStackProvider
{
	void appendStacks(FabricItemGroupEntries entries);
}
