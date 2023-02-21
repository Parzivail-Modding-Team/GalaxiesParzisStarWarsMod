package com.parzivail.aurek.editor;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.HashMap;

public interface IDirectItemEditor
{
	HashMap<Class<? extends Item>, IDirectItemEditor> EDITORS = new HashMap<>();

	void process(MinecraftClient client, ItemStack stack);
}
