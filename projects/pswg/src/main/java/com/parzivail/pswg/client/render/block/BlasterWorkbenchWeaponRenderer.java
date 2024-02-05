package com.parzivail.pswg.client.render.block;

import com.parzivail.pswg.container.SwgBlocks;
import com.parzivail.pswg.features.blasters.BlasterItem;
import com.parzivail.pswg.features.blasters.client.BlasterItemRenderer;
import com.parzivail.pswg.features.blasters.data.BlasterArchetype;
import com.parzivail.pswg.features.blasters.workbench.BlasterWorkbenchBlockEntity;
import com.parzivail.util.block.rotating.WaterloggableRotatingBlock;
import com.parzivail.util.math.MathUtil;
import com.parzivail.util.math.QuatUtil;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
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

		var rotation = state.get(WaterloggableRotatingBlock.FACING);

		matrices.translate(0.5, 0, 0.5);
		matrices.multiply(MathUtil.getEastRotation(rotation));

		var desc = BlasterItem.getBlasterDescriptor(blaster);

		// TODO: move further based on blaster length?
		if (desc.type == BlasterArchetype.PISTOL)
			matrices.translate(3 / 16.0, 14 / 16.0, -5 / 16.0);
		else
			matrices.translate(5 / 16.0, 14 / 16.0, -3 / 16.0);

		matrices.multiply(QuatUtil.ROT_Y_POS10);
		matrices.multiply(QuatUtil.ROT_Z_POS80);

		BlasterItemRenderer.INSTANCE.render(null, blaster, ModelTransformationMode.NONE, false, matrices, vertexConsumers, light, overlay, null);

		matrices.pop();
	}
}
