package com.parzivail.pswg.client.item.render;

import com.parzivail.pswg.client.pm3d.PM3DFile;
import com.parzivail.pswg.client.pm3d.PM3DLod;
import com.parzivail.util.client.VertexConsumerBuffer;
import com.parzivail.util.item.CustomItemRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Lazy;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;

public class BlasterItemRenderer implements CustomItemRenderer
{
	private final Lazy<PM3DFile> pm3dModel;
	private final Identifier texture;
	private final Identifier inventoryTexture;

	public BlasterItemRenderer(Lazy<PM3DFile> model, Identifier texture, Identifier inventoryTexture)
	{
		this.pm3dModel = model;
		this.texture = texture;
		this.inventoryTexture = inventoryTexture;
	}

	@Override
	public void render(ItemStack stack, ModelTransformation.Mode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, BakedModel model)
	{
		matrices.push();

		model.getTransformation().getTransformation(renderMode).apply(leftHanded, matrices);

		matrices.scale(0.2f, 0.2f, 0.2f);

		if (renderMode == ModelTransformation.Mode.GUI)
		{
			matrices.multiply(new Quaternion(90, 0, 0, true));
			matrices.multiply(new Quaternion(0, 0, -90, true));
			matrices.multiply(new Quaternion(-135, 0, 0, true));

			PM3DLod m = pm3dModel.get().getLevelOfDetail(0);
			float f = 4 / (float)m.bounds.getZLength() * MathHelper.SQUARE_ROOT_OF_TWO;
			matrices.scale(f, f, f);

			matrices.translate(0, (float)-m.bounds.minY - m.bounds.getYLength() / 2, (float)-m.bounds.minZ - m.bounds.getZLength() / 2);

			VertexConsumer vc = vertexConsumers.getBuffer(RenderLayer.getEntitySolid(inventoryTexture));
			VertexConsumerBuffer.Instance.init(vc, matrices.peek(), 1, 1, 1, 1, overlay, light);
			m.render(VertexConsumerBuffer.Instance);
		}
		else if (renderMode == ModelTransformation.Mode.FIXED)
		{
			matrices.multiply(new Quaternion(90, 0, 0, true));
			matrices.multiply(new Quaternion(0, 0, 90, true));
			matrices.multiply(new Quaternion(-135, 0, 0, true));

			PM3DLod m = pm3dModel.get().getLevelOfDetail(1);
			float f = 4 / (float)m.bounds.getZLength() * MathHelper.SQUARE_ROOT_OF_TWO;
			matrices.scale(f, f, f);

			matrices.translate((float)-m.bounds.minX, (float)-m.bounds.minY - m.bounds.getYLength() / 2, (float)-m.bounds.minZ - m.bounds.getZLength() / 2);

			VertexConsumer vc = vertexConsumers.getBuffer(RenderLayer.getEntitySolid(inventoryTexture));
			VertexConsumerBuffer.Instance.init(vc, matrices.peek(), 1, 1, 1, 1, overlay, light);
			m.render(VertexConsumerBuffer.Instance);
		}
		else if (renderMode.isFirstPerson())
		{
			matrices.translate(0, 0.9f, 0);
			matrices.multiply(new Quaternion(0, 180, 0, true));
			matrices.translate(-0.4f, -0.5f, -0.25f);

			VertexConsumer vc = vertexConsumers.getBuffer(RenderLayer.getEntitySolid(texture));
			VertexConsumerBuffer.Instance.init(vc, matrices.peek(), 1, 1, 1, 1, overlay, light);
			pm3dModel.get().getLevelOfDetail(1).render(VertexConsumerBuffer.Instance);
		}
		else
		{
			matrices.translate(0, 0.9f, 0);
			matrices.multiply(new Quaternion(0, 180, 0, true));
			matrices.translate(-0.4f, -1, -0.5f);

			VertexConsumer vc = vertexConsumers.getBuffer(RenderLayer.getEntitySolid(texture));
			VertexConsumerBuffer.Instance.init(vc, matrices.peek(), 1, 1, 1, 1, overlay, light);
			pm3dModel.get().getLevelOfDetail(1).render(VertexConsumerBuffer.Instance);
		}

		matrices.pop();
	}
}
