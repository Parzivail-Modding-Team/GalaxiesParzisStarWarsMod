package dev.pswg.feature.brewing;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectInstance;

public class BrewingMap
{
	public static final int SIZE_X = 32;
	public static final int SIZE_Y = 32;
	public static final ArrayList<ArrayList<BrewingCell>> map = new ArrayList<>(SIZE_Y);

	public static void init(InputStream stream) throws IOException
	{
		String input = IOUtils.toString(stream, StandardCharsets.UTF_8);
		input = input.replace(" ", "");
		List<String> lines = Arrays.stream(input.split("\\r?\\n|\\r")).toList();

		for (int i = 0; i < SIZE_Y; i++)
		{
			String line = lines.get(i);
			List<String> cells = Arrays.stream(line.split(",")).toList();
			map.add(i, new ArrayList<>(SIZE_X));
			for (int j = 0; j < SIZE_X; j++)
			{
				String cell = cells.get(j);
				map.get(i).add(j, parseCell(cell));
			}
		}
	}

	public static BrewingCell getCell(float x, float y)
	{

		return map.get((int)y / 16).get((int)x / 16);
	}

	private static BrewingCell parseCell(String string)
	{
		char c = string.charAt(0);
		BrewingCell cell = new BrewingCell(BrewingCellType.Empty);
		switch (c)
		{
			case 'c':
				cell = new BrewingCell(BrewingCellType.Corner);
				break;
			case 'b':
				cell = new BrewingCell(BrewingCellType.Empty);
				break;
			case 'e':
			{
				List<String> args = Arrays.stream(string.split("\\.")).toList();
				var effect = BuiltInRegistries.MOB_EFFECT.get(Identifier.parse(args.get(1))).get();
				cell = new EffectCell(new MobEffectInstance(effect, Integer.parseInt(args.get(3)), args.get(2).charAt(0) - '1', false, false, true), BrewingCellType.Potion);
				break;
			}
			case 'd':
			{
				List<String> args = Arrays.stream(string.split("\\.")).toList();
				var effect = BuiltInRegistries.MOB_EFFECT.get(Identifier.parse(args.get(1))).get();
				cell = new EffectCell(new MobEffectInstance(effect, Integer.parseInt(args.get(3)), args.get(2).charAt(0) - '1', false, false, true), BrewingCellType.Danger);
				break;
			}
		}
		return cell;
	}
}
