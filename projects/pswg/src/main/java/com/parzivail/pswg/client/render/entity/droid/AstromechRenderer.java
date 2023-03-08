package com.parzivail.pswg.client.render.entity.droid;

import com.parzivail.p3d.P3dManager;
import com.parzivail.pswg.Client;
import com.parzivail.pswg.Resources;
import com.parzivail.pswg.entity.droid.AstromechEntity;
import com.parzivail.pswg.entity.rigs.RigR2;
import com.parzivail.util.math.MathUtil;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.joml.Math;
import org.joml.Quaternionf;

import java.util.ArrayList;
import java.util.Collection;

public class AstromechRenderer<T extends AstromechEntity> extends EntityRenderer<T>
{
	private static final Identifier FALLBACK_R_TEXTURE = Resources.id("textures/droid/r/preset/r2d2.png");

	private static final Identifier PARAMETRIC_R_BASE_TEXTURE = Resources.id("textures/droid/r/base.png");
	private static final Identifier PARAMETRIC_R_DETAIL_TEXTURE = Resources.id("textures/droid/r/details.png");
	private static final Identifier PARAMETRIC_R_WIRE_TEXTURE = Resources.id("textures/droid/r/wires.png");
	private static final Identifier PARAMETRIC_R_2_DOME_TEXTURE = Resources.id("textures/droid/r/2/dome.png");
	private static final Identifier PARAMETRIC_R_PAINT_TEXTURE = Resources.id("textures/droid/r/design/1.png");

	public AstromechRenderer(EntityRendererFactory.Context ctx)
	{
		super(ctx);
		this.shadowRadius = 0.4f;
	}

	@Override
	public void render(T entity, float yaw, float tickDelta, MatrixStack matrix, VertexConsumerProvider vertexConsumers, int light)
	{
		if (entity.hasCustomName())
			this.renderLabelIfPresent(entity, entity.getDisplayName(), matrix, vertexConsumers, light);

		matrix.push();

		MathUtil.scalePos(matrix, 10 / 16f, 10 / 16f, 10 / 16f);
		matrix.multiply(new Quaternionf().rotationY(Math.toRadians(180 - yaw)));

		//		var r = entity.getViewRotation(tickDelta);
		//		matrix.multiply(r);

		var modelRef = P3dManager.INSTANCE.get(Resources.id("droid/r2"));
		var vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(getTexture(entity)));

		modelRef.render(matrix, vertexConsumer, entity, RigR2.INSTANCE::getPartTransformation, light, tickDelta, 255, 255, 255, 255);

		matrix.pop();
	}

	private Collection<Identifier> createTextureStack(AstromechEntity.AstromechParameters parameters)
	{
		var stack = new ArrayList<Identifier>();

		if (parameters.usingPaintPreset)
			stack.add(Resources.id(String.format("textures/droid/r/preset/%s.png", parameters.paintPreset)));
		else
		{
			stack.add(Client.tintTexture(PARAMETRIC_R_BASE_TEXTURE, parameters.baseTint));
			stack.add(Client.tintTexture(PARAMETRIC_R_2_DOME_TEXTURE, parameters.domeTint));
			stack.add(Client.tintTexture(PARAMETRIC_R_PAINT_TEXTURE, parameters.paintTint));
			stack.add(PARAMETRIC_R_DETAIL_TEXTURE);
			stack.add(PARAMETRIC_R_WIRE_TEXTURE);
		}

		return stack;
	}

	@Override
	public Identifier getTexture(T entity)
	{
		var parameters = entity.getParameters();
		return Client.stackedTextureProvider.getId(
				String.format("astromech/%08x", parameters.hashCode()),
				() -> FALLBACK_R_TEXTURE,
				() -> createTextureStack(parameters)
		);
	}
}
