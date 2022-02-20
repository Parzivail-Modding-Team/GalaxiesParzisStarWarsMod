package com.parzivail.pswg.client.render.block;

import com.parzivail.pswg.blockentity.BlasterWorkbenchBlockEntity;
import com.parzivail.pswg.client.render.item.BlasterItemRenderer;
import com.parzivail.pswg.container.SwgBlocks;
import com.parzivail.util.block.rotating.RotatingBlock;
import com.parzivail.util.math.ClientMathUtil;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;

public class BlasterWorkbenchWeaponRenderer implements BlockEntityRenderer<BlasterWorkbenchBlockEntity>
{
	public BlasterWorkbenchWeaponRenderer(BlockEntityRendererFactory.Context ctx)
	{
	}

	@Override
	public void render(BlasterWorkbenchBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay)
	{
		var blaster = blockEntity.getStack(BlasterWorkbenchBlockEntity.SLOT_BLASTER);

		if (blaster == null || blaster.isEmpty())
			return;

		var world = blockEntity.getWorld();
		if (world == null)
			return;

		var state = world.getBlockState(blockEntity.getPos());
		if (!state.isOf(SwgBlocks.Workbench.Blaster))
			return;

		matrices.push();

		var rotation = state.get(RotatingBlock.FACING);

		matrices.translate(0.5, 0, 0.5);
		matrices.multiply(ClientMathUtil.getRotation(rotation));
		matrices.translate(2 / 16.0, 15 / 16.0, -7 / 16.0);

		BlasterItemRenderer.INSTANCE.render(blaster, ModelTransformation.Mode.NONE, false, matrices, vertexConsumers, light, overlay, null);

		matrices.pop();
	}
}
