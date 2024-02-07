package com.parzivail.pswg.features.thermaldetonator.client;

import com.parzivail.p3d.P3dManager;
import com.parzivail.pswg.Resources;
import com.parzivail.pswg.client.render.entity.ThermalDetonatorRenderer;
import com.parzivail.pswg.item.ThermalDetonatorItem;
import com.parzivail.pswg.item.ThermalDetonatorTag;
import com.parzivail.util.client.render.ICustomItemRenderer;
import com.parzivail.util.math.MathUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import org.joml.Quaternionf;

public class ThermalDetonatorItemRenderer implements ICustomItemRenderer
{
	public static final ThermalDetonatorItemRenderer INSTANCE = new ThermalDetonatorItemRenderer();

	private static final Identifier TEXTURE = Resources.id("textures/item/model/thermal_detonator/thermal_detonator.png");
	private static final Identifier PRIMED_TEXTURE = Resources.id("textures/item/model/thermal_detonator/thermal_detonator_primed.png");

	private ThermalDetonatorItemRenderer()
	{
	}

	@Override
	public void render(LivingEntity entity, ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, BakedModel model)
	{
		matrices.push();

		model.getTransformation().getTransformation(renderMode).apply(leftHanded, matrices);

		matrices.multiply(new Quaternionf().rotationY((float)(Math.PI / -2)));

		switch (renderMode)
		{
			case NONE:
			case GROUND:
			case HEAD:
			case FIRST_PERSON_LEFT_HAND:
			case FIRST_PERSON_RIGHT_HAND:
				matrices.translate(0.4, -0.1, 0);
				break;
			case THIRD_PERSON_LEFT_HAND:
			case THIRD_PERSON_RIGHT_HAND:
				matrices.translate(0, -0.4, 0);
				matrices.multiply(new Quaternionf().rotationY((float)(Math.PI / 2)));
				matrices.multiply(new Quaternionf().rotationX((float)(Math.PI / 4)));
				break;
			case FIXED:
				matrices.multiply(new Quaternionf().rotationZ((float)(Math.PI / 4)));
				matrices.multiply(new Quaternionf().rotationY((float)(135 * Math.PI / 180)));
				MathUtil.scalePos(matrices, 2f, 2f, 2f);
				break;
			case GUI:
				matrices.translate(0, -0.15, 0.1);
				matrices.multiply(new Quaternionf().rotationZ((float)(Math.PI / -4)));
				matrices.multiply(new Quaternionf().rotationY((float)(Math.PI / -4)));
				MathUtil.scalePos(matrices, 2.5f, 2.5f, 2.5f);
				break;
		}

		renderDirect(entity, stack, renderMode, matrices, vertexConsumers, light, overlay, false, true);

		matrices.pop();
	}

	public void renderDirect(LivingEntity entity, ItemStack stack, ModelTransformationMode renderMode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, boolean forceBlade, boolean useHandPos)
	{
		if (!(stack.getItem() instanceof ThermalDetonatorItem td))
			return;

		matrices.push();

		var tdt = new ThermalDetonatorTag(stack.getOrCreateNbt());

		var m = P3dManager.INSTANCE.get(ThermalDetonatorRenderer.MODEL);
		if (m == null)
		{
			var crashReport = CrashReport.create(new IllegalStateException("Thermal detonator model is null"), String.format("Unable to load thermal detonator model: %s", ThermalDetonatorRenderer.MODEL));
			throw new CrashException(crashReport);
		}

		var primed = tdt.primed;

		if (entity == null)
			entity = MinecraftClient.getInstance().player;

		if (entity != null)
			primed &= (entity.age / 7) % 2 == 0;

		final var renderedTexture = primed ? ThermalDetonatorRenderer.TEXTURE_PRIMED : ThermalDetonatorRenderer.TEXTURE;
		m.render(matrices, vertexConsumers, tdt, null, (v, tag, obj) -> v.getBuffer(RenderLayer.getEntityCutout(renderedTexture)), light, 0, 255, 255, 255, 255);

		matrices.pop();
	}
}
