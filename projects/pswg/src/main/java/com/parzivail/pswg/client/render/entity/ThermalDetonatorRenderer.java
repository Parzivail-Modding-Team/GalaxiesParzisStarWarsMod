package com.parzivail.pswg.client.render.entity;

import com.parzivail.p3d.P3dManager;
import com.parzivail.p3d.P3dModel;
import com.parzivail.pswg.Resources;
import com.parzivail.pswg.client.sound.ThermalDetonatorEntitySoundInstance;
import com.parzivail.pswg.client.sound.timeline.SoundTimelineManager;
import com.parzivail.pswg.container.SwgSounds;
import com.parzivail.pswg.entity.ThermalDetonatorEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

import java.util.HashSet;

public class ThermalDetonatorRenderer extends EntityRenderer<ThermalDetonatorEntity>
{
	public static final Identifier MODEL = Resources.id("item/thermal_detonator/thermal_detonator");
	public static final Identifier TEXTURE_OFF = Resources.id("textures/item/model/thermal_detonator/thermal_detonator_off.png");
	public static final Identifier TEXTURE_PRIMED = Resources.id("textures/item/model/thermal_detonator/thermal_detonator_primed.png");
	public static final Identifier TEXTURE_BEEPING = Resources.id("textures/item/model/thermal_detonator/thermal_detonator_beeping.png");

	public P3dModel model;

	private static final HashSet<ThermalDetonatorEntity> BEEPING_ENTITIES = new HashSet<>();

	public ThermalDetonatorRenderer(EntityRendererFactory.Context context)
	{
		super(context);

		SoundTimelineManager.SOUND_EVENT_ENTERED.register((instance, timelineEvent) -> {
			if (timelineEvent.equals(SwgSounds.Explosives.THERMAL_DETONATOR_BEEP_TIMELINE_EVENT_ID) && instance instanceof ThermalDetonatorEntitySoundInstance tdesi)
				BEEPING_ENTITIES.add(tdesi.getDetonator());
		});

		SoundTimelineManager.SOUND_EVENT_LEFT.register((instance, timelineEvent) -> {
			if (timelineEvent.equals(SwgSounds.Explosives.THERMAL_DETONATOR_BEEP_TIMELINE_EVENT_ID) && instance instanceof ThermalDetonatorEntitySoundInstance tdesi)
				BEEPING_ENTITIES.remove(tdesi.getDetonator());
		});
	}

	@Override
	public void render(ThermalDetonatorEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light)
	{
		matrices.push();
		var vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(getTexture(entity)));
		matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(entity.getYaw(tickDelta)));
		matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(entity.getPitch(tickDelta)));

		if (model == null)
			model = P3dManager.INSTANCE.get(MODEL);

		model.render(matrices, vertexConsumer, entity, null, light, tickDelta, 255, 255, 255, 255);
		matrices.pop();
		super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
	}

	@Override
	public Identifier getTexture(ThermalDetonatorEntity entity)
	{
		if (entity.isPrimed())
		{
			if (BEEPING_ENTITIES.contains(entity))
				return TEXTURE_BEEPING;

			return TEXTURE_PRIMED;
		}
		return TEXTURE_OFF;
	}
}
