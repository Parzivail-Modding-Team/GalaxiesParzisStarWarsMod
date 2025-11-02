package dev.pswg.feature.brewing;

import dev.pswg.container.GadgetsItems;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Colors;

import java.util.HashMap;

public class MixerFoodColors
{
	public static HashMap<Item, Integer> colorMap = new HashMap<>();

	public static int getColor(ItemStack stack)
	{
		return colorMap.getOrDefault(stack.getItem(), Colors.GRAY);
	}

	public static void init()
	{

		colorMap.put(Items.APPLE, 0xFFff1c2b);
		colorMap.put(Items.BEETROOT, 0xFF71160d);
		colorMap.put(Items.CARROT, 0xFFff1c2b);
		colorMap.put(Items.GLOW_BERRIES, 0xFFff1c2b);
		colorMap.put(Items.MELON, 0xFF848920);
		colorMap.put(Items.SWEET_BERRIES, 0xFFa50700);

		colorMap.put(GadgetsItems.BLACK_MELON, 0xFF42443b);
		colorMap.put(GadgetsItems.BLUE_MILK, 0xFF4f69a6);
		colorMap.put(GadgetsItems.DEB_DEB, 0xFFd37e1a);
		colorMap.put(GadgetsItems.DESERT_PLUMS, 0xFF404766);
		colorMap.put(GadgetsItems.JOGAN_FRUIT, 0xFF57254d);
		colorMap.put(GadgetsItems.MEILOORUN, 0xFFff442b);
		colorMap.put(GadgetsItems.PALLIE_FRUIT, 0xFF5c6f36);
		colorMap.put(GadgetsItems.PIKA_FRUIT, 0xFF567d8a);
	}
}
