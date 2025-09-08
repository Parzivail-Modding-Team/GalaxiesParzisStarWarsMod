package dev.pswg.feature.brewing;

import dev.pswg.Gadgets;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

	public static BrewingCell getCell(int x, int y)
	{
		return map.get(y / 8).get(x / 8);
	}

	private static BrewingCell parseCell(String string)
	{
		char c = string.charAt(0);
		BrewingCell cell = new BrewingCell();
		switch (c)
		{
			case 'c':
				cell = new CornerCell();
				break;
			case 'b':
				cell = new BrewingCell();
				break;
			case 'e':
			{
				List<String> args = Arrays.stream(string.split("\\.")).toList();
				var effect = Registries.STATUS_EFFECT.getEntry(Identifier.of(args.get(1))).get();
				cell = new EffectCell(new StatusEffectInstance(effect, 100, args.get(2).charAt(0) - '0'));
				break;
			}
			case 'd':
			{
				List<String> args = Arrays.stream(string.split("\\.")).toList();
				var effect = Registries.STATUS_EFFECT.getEntry(Identifier.of(args.get(1))).get();
				cell = new DangerCell(new StatusEffectInstance(effect, 100, args.get(2).charAt(0) - '0'));
				break;
			}
			default:
				new BrewingCell();
		}
		;
		return cell;
	}
}
