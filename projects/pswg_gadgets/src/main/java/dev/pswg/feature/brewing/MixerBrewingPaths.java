package dev.pswg.feature.brewing;

import com.mojang.datafixers.util.Pair;
import dev.pswg.container.GalaxiesItems;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Colors;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MixerBrewingPaths
{
	public static HashMap<Item, List<Pair<Float, Float>>> pathMap = new HashMap<>();

	public static List<Pair<Float, Float>> getPath(ItemStack stack)
	{
		return pathMap.get(stack.getItem());
	}

	public static void init()
	{
		pathMap.put(GalaxiesItems.STRIPPED_JAPOR_BRANCH, List.of(Pair.of((float)Math.PI * 7 / 4f, 20f), Pair.of((float)Math.PI * 1 / 3f, 65f)));
		pathMap.put(GalaxiesItems.CHASUKA_LEAF, List.of(Pair.of((float)Math.PI * 7 / 12f, 25f), Pair.of((float)Math.PI * 3 / 4f, 20f), Pair.of((float)Math.PI * 11 / 12f, 15f)));
		pathMap.put(GalaxiesItems.HKAK_BEAN, List.of(Pair.of((float)Math.PI * 11 / 6f, 36f), Pair.of((float)Math.PI * 19 / 12f, 50f), Pair.of((float)Math.PI * 5 / 4f, 50f)));
		pathMap.put(GalaxiesItems.CORPSE_OF_GORG, List.of(Pair.of((float)Math.PI * 7 / 4f, 100f), Pair.of((float)Math.PI * 5 / 4f, 25f)));
		pathMap.put(GalaxiesItems.EYE_OF_SKETTO, List.of(Pair.of((float)Math.PI * 7 / 6f, 25f), Pair.of((float)Math.PI * 5 / 6f, 30f), Pair.of((float)Math.PI * 7 / 6f, 20f)));
		pathMap.put(GalaxiesItems.KREETLE_HUSK, List.of(Pair.of((float)Math.PI / -4f, 45f), Pair.of((float)Math.PI / 6f, 45f)));
		pathMap.put(GalaxiesItems.LIZARD_GIZZARD, List.of(Pair.of((float)Math.PI * 10 / 7f, 45f), Pair.of((float)Math.PI * 9f / 8f, 22f),  Pair.of((float)Math.PI / -9f, 12f)));
		pathMap.put(GalaxiesItems.SQUILL_LIVER, Arrays.asList(Pair.of((float)Math.PI * 11 / 12f, 30f), Pair.of((float)Math.PI * 3 / 4f, 40f)));
		pathMap.put(GalaxiesItems.TONGUE_OF_WORRT, Arrays.asList(Pair.of((float)Math.PI * 2 / 3f, 26f), Pair.of((float)Math.PI / 6f, 34f)));
	}
}
