package com.parzivail.pswg.container.registry;

import com.parzivail.pswg.Resources;
import com.parzivail.util.block.PStairsBlock;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.minecraft.block.*;
import net.minecraft.util.DyeColor;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.function.Function;

public class RegistryHelper
{
	public static class DyedBlockVariants extends HashMap<DyeColor, Block>
	{
		public DyedBlockVariants(Function<DyeColor, Block> blockFunction)
		{
			for (var color : DyeColor.values())
				put(color, blockFunction.apply(color));
		}
	}

	public static class DyedBlockStairsSlabWallVariants extends HashMap<DyeColor, BlockStairsSlabWallVariants>
	{
		public DyedBlockStairsSlabWallVariants(Function<DyeColor, BlockStairsSlabWallVariants> blockFunction)
		{
			for (var color : DyeColor.values())
				put(color, blockFunction.apply(color));
		}
	}

	public static class BlockStairsSlabWallVariants
	{
		public final Block block;
		public final StairsBlock stairs;
		public final SlabBlock slab;
		public final WallBlock wall;

		public BlockStairsSlabWallVariants(Block block)
		{
			this.block = block;
			this.stairs = new PStairsBlock(block.getDefaultState(), AbstractBlock.Settings.copy(block));
			this.slab = new SlabBlock(AbstractBlock.Settings.copy(block));
			this.wall = new WallBlock(AbstractBlock.Settings.copy(block));
		}
	}

	public static class PlankStairsSlabFenceGateVariants
	{
		public final Block plank;
		public final StairsBlock stairs;
		public final SlabBlock slab;
		public final FenceBlock fence;
		public final FenceGateBlock gate;
		public final TrapdoorBlock trapdoor;
		public final DoorBlock door;

		public PlankStairsSlabFenceGateVariants(Block plank)
		{
			this.plank = plank;
			this.stairs = new PStairsBlock(plank.getDefaultState(), AbstractBlock.Settings.copy(plank));
			this.slab = new SlabBlock(AbstractBlock.Settings.copy(plank));
			this.fence = new FenceBlock(AbstractBlock.Settings.copy(plank));
			this.gate = new FenceGateBlock(AbstractBlock.Settings.copy(plank));
			this.trapdoor = new TrapdoorBlock(AbstractBlock.Settings.copy(plank));
			this.door = new DoorBlock(AbstractBlock.Settings.copy(plank));
		}
	}

	public static <T> void registerAnnotatedFields(Class<?> rootClazz, Class<T> registryType, RegistryMethod<T> registryFunction)
	{
		for (var clazz : rootClazz.getClasses())
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

	public static void registerFlammable(Class<?> rootClazz)
	{
		for (var clazz : rootClazz.getClasses())
		{
			// Register inner classes
			registerFlammable(clazz);

			for (var field : clazz.getFields())
			{
				var annotation = field.getAnnotation(Flammable.class);
				if (!Modifier.isStatic(field.getModifiers()) || annotation == null || !Block.class.isAssignableFrom(field.getType()))
					continue;

				try
				{
					FlammableBlockRegistry.getDefaultInstance().add((Block)field.get(null), annotation.burn(), annotation.spread());
				}
				catch (IllegalAccessException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
}
