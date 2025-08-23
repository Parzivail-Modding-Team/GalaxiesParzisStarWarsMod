package dev.pswg.util;

import dev.pswg.block.DyedBlocks;
import dev.pswg.block.NumberedBlocks;
import dev.pswg.block.StoneProducts;
import dev.pswg.container.GadgetsBlocks;
import net.minecraft.block.Block;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class AutoGenerateUtil
{
	public static <TA extends Annotation> void consumeAnnotatedGadgetsBlocks(Class<TA> annotationClazz, BiConsumer<Block, TA> consumer){

		ArrayList<Block> list = new ArrayList<>(1024);

		AutoGenerateUtil.consumeAnnotatedFields(annotationClazz, GadgetsBlocks.class, Block.class, (block, dataGenBlock) -> {
			list.add(block);
		});
		AutoGenerateUtil.consumeAnnotatedFields(annotationClazz, GadgetsBlocks.class, NumberedBlocks.class, (numberedBlocks, dataGenBlock) -> {
			list.addAll(numberedBlocks);
		});
		AutoGenerateUtil.consumeAnnotatedFields(annotationClazz, GadgetsBlocks.class, DyedBlocks.class, (dyedBlocks, dataGenBlock) -> {
			list.addAll(dyedBlocks.values());
		});
		AutoGenerateUtil.consumeAnnotatedFields(annotationClazz, GadgetsBlocks.class, StoneProducts.class, (stoneProducts, dataGenBlock) -> {
			list.add(stoneProducts.slab);
			list.add(stoneProducts.block);
			list.add(stoneProducts.stairs);
			list.add(stoneProducts.wall);
		});
	}

	public static <T, TA extends Annotation> void consumeAnnotatedFields(Class<TA> annotationClazz, Class<?> rootClazz, Class<T> registryType, BiConsumer<T, TA> consumer)
	{
		for (var field : rootClazz.getFields())
		{
			var annotation = field.getAnnotation(annotationClazz);
			if (!Modifier.isStatic(field.getModifiers()) || annotation == null || !registryType.isAssignableFrom(field.getType()))
				continue;

			try
			{
				consumer.accept((T)field.get(null), annotation);
			}
			catch (IllegalAccessException e)
			{
				e.printStackTrace();
			}
		}

		for (var clazz : rootClazz.getClasses())
			consumeAnnotatedFields(annotationClazz, clazz, registryType, consumer);
	}
}
