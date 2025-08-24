package dev.pswg.util;

import dev.pswg.block.DyedBlocks;
import dev.pswg.block.NumberedBlocks;
import dev.pswg.block.StoneProducts;
import dev.pswg.container.GadgetsBlocks;
import net.minecraft.block.Block;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.function.BiConsumer;

public class AutoGenerateUtil
{
	public static <TA extends Annotation> void consumeAnnotatedGadgetsBlocks(Class<TA> annotationClazz, BiConsumer<Block, TA> consumer){
		AutoGenerateUtil.consumeAnnotatedFields(annotationClazz, GadgetsBlocks.class, Block.class, consumer);
		AutoGenerateUtil.consumeAnnotatedFields(annotationClazz, GadgetsBlocks.class, NumberedBlocks.class, (numberedBlocks, annotation) -> {
			for(Block block : numberedBlocks)
				consumer.accept(block, annotation);
		});
		AutoGenerateUtil.consumeAnnotatedFields(annotationClazz, GadgetsBlocks.class, DyedBlocks.class, (dyedBlocks, annotation) -> {
			for(Block block : dyedBlocks.values())
				consumer.accept(block, annotation);
		});
		AutoGenerateUtil.consumeAnnotatedFields(annotationClazz, GadgetsBlocks.class, StoneProducts.class, (stoneProducts, annotation) -> {
			consumer.accept(stoneProducts.slab, annotation);
			consumer.accept(stoneProducts.block, annotation);
			consumer.accept(stoneProducts.stairs, annotation);
			consumer.accept(stoneProducts.wall, annotation);
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
