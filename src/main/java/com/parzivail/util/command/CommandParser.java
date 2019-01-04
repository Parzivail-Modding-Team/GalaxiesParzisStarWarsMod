package com.parzivail.util.command;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by colby on 9/10/2017.
 */
public class CommandParser
{
	public static <T> T parse(T input, String[] args)
	{
		Field[] fields = input.getClass().getFields();
		if (args.length != fields.length)
			return null;
		Arrays.sort(fields, Comparator.comparingInt(o -> o.getAnnotation(Parameter.class).index()));

		try
		{
			for (int i = 0; i < fields.length; i++)
			{
				String name = fields[i].getType().getSimpleName();
				if (name.equals(String.class.getSimpleName()))
					fields[i].set(input, args[i]);
				else if (name.equals(int.class.getSimpleName()))
					fields[i].set(input, Integer.parseInt(args[i]));
				else if (name.equals(float.class.getSimpleName()))
					fields[i].set(input, Float.parseFloat(args[i]));
				else if (name.equals(boolean.class.getSimpleName()))
					fields[i].set(input, Boolean.parseBoolean(args[i]));
			}
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}

		return input;
	}
}
