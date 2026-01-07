package dev.pswg.util;

import dev.pswg.autoreg.AutoGenerateUtil;
import dev.pswg.block.*;
import dev.pswg.container.GadgetsBlocks;
import net.minecraft.block.Block;

import java.lang.annotation.Annotation;
import java.util.function.BiConsumer;

public class GadgetsGenUtil
{
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
