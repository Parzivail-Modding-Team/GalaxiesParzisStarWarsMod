package com.parzivail.pswg.client.item;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.client.pm3d.PM3DFile;
import com.parzivail.pswg.container.SwgItems;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Lazy;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class SimpleItemRender
{
	private static final Lazy<PM3DFile> lightsaber_luke_rotj = new Lazy<>(() -> PM3DFile.tryLoad(Resources.identifier("models/item/lightsaber_luke_rotj.pm3d")));

	public static void renderItem(ItemStack stack, ModelTransformation.Mode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, BakedModel model, CallbackInfo ci)
	{
		if (!stack.isEmpty() && stack.getItem() == SwgItems.Lightsaber)
		{
			matrices.push();

			model.getTransformation().getTransformation(renderMode).apply(leftHanded, matrices);
			matrices.scale(0.04f, 0.04f, 0.04f);
			//			matrices.translate(-0.5, -0.5, -0.5);
			matrices.translate(-0.5, 4, 1);

			//			final RenderLayer layer = renderMode == ModelTransformation.Mode.GUI ? TexturedRenderLayers.getEntityTranslucentCull() : RenderLayers.getItemLayer(stack, renderMode != ModelTransformation.Mode.GROUND);
			//			VertexConsumer vc = vertexConsumers.getBuffer(RenderLayers.getItemLayer(stack, renderMode != ModelTransformation.Mode.GROUND));

			VertexConsumer vc = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(new Identifier("minecraft", "textures/block/stone.png")));

			lightsaber_luke_rotj.get().render(matrices, vc, light);

			matrices.pop();

			ci.cancel();
		}
	}
}
