package com.parzivail.pswg.container.registry;

import com.parzivail.pswg.Resources;
import com.parzivail.util.block.PStairsBlock;
import com.parzivail.util.block.VerticalSlabBlock;
import net.minecraft.block.*;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.util.DyeColor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class RegistryHelper
{
	public static class Numbered<T> extends ArrayList<T>
	{
		/**
		 *
		 * @param count 1 ... count
		 * @param generator
		 */
		public Numbered(int count, Function<Integer, T> generator)
		{
			for (var i = 1; i <= count; i++)
				add(generator.apply(i));
		}
	}

	public static class NumberedItems extends Numbered<Item>
	{
		public NumberedItems(int count, Function<Integer, Item> itemFunction)
		{
			super(count, itemFunction);
		}
	}

	public static class NumberedBlocks extends Numbered<Block>
	{
		public NumberedBlocks(int count, Function<Integer, Block> blockFunction)
		{
			super(count, blockFunction);
		}
	}

	public static class Dyed<T> extends HashMap<DyeColor, T>
	{
		public Dyed(Function<DyeColor, T> generator)
		{
			for (var color : DyeColor.values())
				put(color, generator.apply(color));
		}
	}

	public static class DyedItems extends Dyed<Item>
	{
		public DyedItems(Function<DyeColor, Item> blockFunction)
		{
			super(blockFunction);
		}
	}

	public static class DyedBlocks extends Dyed<Block>
	{
		public DyedBlocks(Function<DyeColor, Block> blockFunction)
		{
			super(blockFunction);
		}
	}

	public static class DyedStoneProducts extends HashMap<DyeColor, StoneProducts>
	{
		public DyedStoneProducts(Function<DyeColor, StoneProducts> blockFunction)
		{
			for (var color : DyeColor.values())
				put(color, blockFunction.apply(color));
		}
	}

	public static class ArmorItems
	{
		public final ArmorItem helmet;
		public final ArmorItem chestplate;
		public final ArmorItem leggings;
		public final ArmorItem boots;

		public ArmorItems(ArmorMaterial material, Item.Settings settings)
		{
			helmet = new ArmorItem(material, EquipmentSlot.HEAD, settings);
			chestplate = new ArmorItem(material, EquipmentSlot.CHEST, settings);
			leggings = new ArmorItem(material, EquipmentSlot.LEGS, settings);
			boots = new ArmorItem(material, EquipmentSlot.FEET, settings);
		}
	}

	public static class StoneProducts
	{
		public final Block block;
		public final StairsBlock stairs;
		public final VerticalSlabBlock slab;
		public final WallBlock wall;

		public StoneProducts(Block block)
		{
			this.block = block;
			this.stairs = new PStairsBlock(block.getDefaultState(), AbstractBlock.Settings.copy(block));
			this.slab = new VerticalSlabBlock(AbstractBlock.Settings.copy(block));
			this.wall = new WallBlock(AbstractBlock.Settings.copy(block));
		}
	}

	public static class WoodProducts
	{
		public final Block plank;
		public final StairsBlock stairs;
		public final VerticalSlabBlock slab;
		public final FenceBlock fence;
		public final FenceGateBlock gate;
		public final TrapdoorBlock trapdoor;
		public final DoorBlock door;

		public WoodProducts(Block plank)
		{
			this.plank = plank;
			this.stairs = new PStairsBlock(plank.getDefaultState(), AbstractBlock.Settings.copy(plank));
			this.slab = new VerticalSlabBlock(AbstractBlock.Settings.copy(plank));
			this.fence = new FenceBlock(AbstractBlock.Settings.copy(plank));
			this.gate = new FenceGateBlock(AbstractBlock.Settings.copy(plank));
			this.trapdoor = new TrapdoorBlock(AbstractBlock.Settings.copy(plank));
			this.door = new DoorBlock(AbstractBlock.Settings.copy(plank));
		}
	}

	public static <T> void registerAnnotatedFields(Class<?> rootClazz, Class<T> registryType, RegistryMethod<T> registryFunction)
	{
		for (var clazz : getSortedClasses(rootClazz))
		{
			// Register inner classes
			registerAnnotatedFields(clazz, registryType, registryFunction);

			for (var field : clazz.getFields())
			{
				var annotation = field.getAnnotation(RegistryName.class);
				if (!Modifier.isStatic(field.getModifiers()) || annotation == null || !registryType.isAssignableFrom(field.getType()))
					continue;

				try
				{
					registryFunction.accept((T)field.get(null), Resources.id(annotation.value()), field.getAnnotation(TabIgnore.class) != null);
				}
				catch (IllegalAccessException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	public static <TA extends Annotation, TB> void register(Class<?> rootClazz, Class<TA> annotationClazz, Class<TB> acceptClazz, BiConsumer<TA, TB> registryFunction)
	{
		for (var clazz : getSortedClasses(rootClazz))
		{
			// Register inner classes
			register(clazz, annotationClazz, acceptClazz, registryFunction);

			for (var field : clazz.getFields())
			{
				var annotation = field.getAnnotation(annotationClazz);
				if (!Modifier.isStatic(field.getModifiers()) || annotation == null || !acceptClazz.isAssignableFrom(field.getType()))
					continue;

				try
				{
					registryFunction.accept(annotation, (TB)field.get(null));
				}
				catch (IllegalAccessException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	private static List<Class<?>> getSortedClasses(Class<?> rootClazz)
	{
		var classes = rootClazz.getClasses();
		return Arrays.stream(classes).sorted(RegistryHelper::compareClassRegistryOrder).toList();
	}

	private static int compareClassRegistryOrder(Class<?> a, Class<?> b)
	{
		return Integer.compare(getRegistryOrder(a), getRegistryOrder(b));
	}

	private static int getRegistryOrder(Class<?> a)
	{
		var annotation = a.getAnnotation(RegistryOrder.class);
		if (annotation == null)
			return Integer.MAX_VALUE;

		return annotation.value();
	}
}