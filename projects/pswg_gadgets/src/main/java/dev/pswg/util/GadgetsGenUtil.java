package dev.pswg.util;

import dev.pswg.autoreg.AutoGenerateUtil;
import dev.pswg.block.collection.*;
import dev.pswg.container.GadgetsBlocks;
import java.lang.annotation.Annotation;
import java.util.function.BiConsumer;

import dev.pswg.container.GadgetsItems;
import dev.pswg.item.ArmorItems;
import dev.pswg.item.DyedItems;
import dev.pswg.item.NumberedItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class GadgetsGenUtil
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
	public static <TA extends Annotation> void consumeAnnotatedGadgetsBlocks(Class<TA> annotationClazz, BiConsumer<Block, TA> consumer)
	{
		AutoGenerateUtil.consumeAnnotatedFields(annotationClazz, GadgetsBlocks.class, Block.class, consumer);
		AutoGenerateUtil.consumeAnnotatedFields(annotationClazz, GadgetsBlocks.class, NumberedBlocks.class, (numberedBlocks, annotation) -> {
			for (Block block : numberedBlocks)
				consumer.accept(block, annotation);
		});
		AutoGenerateUtil.consumeAnnotatedFields(annotationClazz, GadgetsBlocks.class, DyedBlocks.class, (dyedBlocks, annotation) -> {
			for (Block block : dyedBlocks.values())
				consumer.accept(block, annotation);
		});
		AutoGenerateUtil.consumeAnnotatedFields(annotationClazz, GadgetsBlocks.class, ReducedDryingRuiningStoneProducts.class, (reducedDryingRuiningStoneProducts, annotation) -> {
			consumer.accept(reducedDryingRuiningStoneProducts.slab, annotation);
			consumer.accept(reducedDryingRuiningStoneProducts.block, annotation);
			consumer.accept(reducedDryingRuiningStoneProducts.stairs, annotation);
		});
		AutoGenerateUtil.consumeAnnotatedFields(annotationClazz, GadgetsBlocks.class, ReducedDryingStoneProducts.class, (reducedDryingStoneProducts, annotation) -> {
			consumer.accept(reducedDryingStoneProducts.slab, annotation);
			consumer.accept(reducedDryingStoneProducts.block, annotation);
			consumer.accept(reducedDryingStoneProducts.stairs, annotation);
		});
		AutoGenerateUtil.consumeAnnotatedFields(annotationClazz, GadgetsBlocks.class, ReducedStoneProducts.class, (reducedStoneProducts, annotation) -> {
			consumer.accept(reducedStoneProducts.slab, annotation);
			consumer.accept(reducedStoneProducts.block, annotation);
			consumer.accept(reducedStoneProducts.stairs, annotation);
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
}
