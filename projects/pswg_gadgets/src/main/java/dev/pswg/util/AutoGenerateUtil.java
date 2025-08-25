package dev.pswg.util;

import dev.pswg.block.DyedBlocks;
import dev.pswg.block.NumberedBlocks;
import dev.pswg.block.StoneProducts;
import dev.pswg.block.WoodProducts;
import dev.pswg.container.GadgetsBlocks;
import dev.pswg.container.GadgetsItems;
import dev.pswg.item.ArmorItems;
import dev.pswg.item.DyedItems;
import dev.pswg.item.NumberedItems;
import net.minecraft.block.Block;
import net.minecraft.item.Item;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.function.BiConsumer;

public class AutoGenerateUtil
{
	public static <TA extends Annotation> void consumeAnnotatedGadgetsItems(Class<TA> annotationClazz, BiConsumer<Item, TA> consumer){
		AutoGenerateUtil.consumeAnnotatedFields(annotationClazz, GadgetsItems.class, Item.class, consumer);
		AutoGenerateUtil.consumeAnnotatedFields(annotationClazz, GadgetsItems.class, DyedItems.class, (dyedItems, ta) ->{
			for(Item item: dyedItems.values())
				consumer.accept(item, ta);
		});
		AutoGenerateUtil.consumeAnnotatedFields(annotationClazz, GadgetsItems.class, ArmorItems.class, (armorItems, ta) -> {
			consumer.accept(armorItems.helmet, ta);
			consumer.accept(armorItems.chestplate, ta);
			consumer.accept(armorItems.leggings, ta);
			consumer.accept(armorItems.boots, ta);
		});
		AutoGenerateUtil.consumeAnnotatedFields(annotationClazz, GadgetsItems.class, NumberedItems.class, (items, ta) -> {
			for(Item item: items)
				consumer.accept(item, ta);
		});
	}
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
		AutoGenerateUtil.consumeAnnotatedFields(annotationClazz, GadgetsBlocks.class, WoodProducts.class, (woodProducts, annotation) -> {
			consumer.accept(woodProducts.plank, annotation);
			consumer.accept(woodProducts.door, annotation);
			consumer.accept(woodProducts.trapdoor, annotation);
			consumer.accept(woodProducts.gate, annotation);
			consumer.accept(woodProducts.fence, annotation);
			consumer.accept(woodProducts.slab, annotation);
			consumer.accept(woodProducts.stairs, annotation);
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
