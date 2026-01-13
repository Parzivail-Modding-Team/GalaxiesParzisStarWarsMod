package dev.pswg.autoreg;

import dev.pswg.Galaxies;
import dev.pswg.block.*;
import dev.pswg.container.GalaxiesBlocks;
import dev.pswg.container.GalaxiesItems;
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
		AutoGenerateUtil.consumeAnnotatedFields(annotationClazz, GalaxiesItems.class, Item.class, consumer);
		AutoGenerateUtil.consumeAnnotatedFields(annotationClazz, GalaxiesItems.class, DyedItems.class, (dyedItems, ta) ->{
			for(Item item: dyedItems.values())
				consumer.accept(item, ta);
		});
		AutoGenerateUtil.consumeAnnotatedFields(annotationClazz, GalaxiesItems.class, ArmorItems.class, (armorItems, ta) -> {
			consumer.accept(armorItems.helmet, ta);
			consumer.accept(armorItems.chestplate, ta);
			consumer.accept(armorItems.leggings, ta);
			consumer.accept(armorItems.boots, ta);
		});
		AutoGenerateUtil.consumeAnnotatedFields(annotationClazz, GalaxiesItems.class, NumberedItems.class, (items, ta) -> {
			for(Item item: items)
				consumer.accept(item, ta);
		});
	}
	public static <TA extends Annotation> void consumeAnnotatedGalaxiesBlocks(Class<TA> annotationClazz, BiConsumer<Block, TA> consumer){
		AutoGenerateUtil.consumeAnnotatedFields(annotationClazz, GalaxiesBlocks.class, Block.class, consumer);
		AutoGenerateUtil.consumeAnnotatedFields(annotationClazz, GalaxiesBlocks.class, NumberedBlocks.class, (numberedBlocks, annotation) -> {
			for(Block block : numberedBlocks)
				consumer.accept(block, annotation);
		});
		AutoGenerateUtil.consumeAnnotatedFields(annotationClazz, GalaxiesBlocks.class, DyedBlocks.class, (dyedBlocks, annotation) -> {
			for(Block block : dyedBlocks.values())
				consumer.accept(block, annotation);
		});
		AutoGenerateUtil.consumeAnnotatedFields(annotationClazz, GalaxiesBlocks.class, ReducedDryingRuiningStoneProducts.class, (reducedDryingRuiningStoneProducts, annotation) -> {
			consumer.accept(reducedDryingRuiningStoneProducts.slab, annotation);
			consumer.accept(reducedDryingRuiningStoneProducts.block, annotation);
			consumer.accept(reducedDryingRuiningStoneProducts.stairs, annotation);
		});
		AutoGenerateUtil.consumeAnnotatedFields(annotationClazz, GalaxiesBlocks.class, ReducedDryingStoneProducts.class, (reducedDryingStoneProducts, annotation) -> {
			consumer.accept(reducedDryingStoneProducts.slab, annotation);
			consumer.accept(reducedDryingStoneProducts.block, annotation);
			consumer.accept(reducedDryingStoneProducts.stairs, annotation);
		});
		AutoGenerateUtil.consumeAnnotatedFields(annotationClazz, GalaxiesBlocks.class, ReducedStoneProducts.class, (reducedStoneProducts, annotation) -> {
			consumer.accept(reducedStoneProducts.slab, annotation);
			consumer.accept(reducedStoneProducts.block, annotation);
			consumer.accept(reducedStoneProducts.stairs, annotation);
		});
		AutoGenerateUtil.consumeAnnotatedFields(annotationClazz, GalaxiesBlocks.class, StoneProducts.class, (stoneProducts, annotation) -> {
			consumer.accept(stoneProducts.slab, annotation);
			consumer.accept(stoneProducts.block, annotation);
			consumer.accept(stoneProducts.stairs, annotation);
			consumer.accept(stoneProducts.wall, annotation);
		});
		AutoGenerateUtil.consumeAnnotatedFields(annotationClazz, GalaxiesBlocks.class, WoodProducts.class, (woodProducts, annotation) -> {
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
