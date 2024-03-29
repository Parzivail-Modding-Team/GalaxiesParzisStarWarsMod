package com.parzivail.pswg.features.grenades.client;

import com.parzivail.p3d.P3dManager;
import com.parzivail.pswg.client.container.SwgSoundTimelines;
import com.parzivail.pswg.client.render.entity.FragmentationGrenadeRenderer;
import com.parzivail.pswg.client.sound.FragmentationGrenadeItemSoundInstance;
import com.parzivail.pswg.client.sound.timeline.SoundTimelineManager;
import com.parzivail.pswg.item.FragmentationGrenadeItem;
import com.parzivail.pswg.item.ThrowableExplosiveTag;
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
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import org.joml.Quaternionf;

import java.util.HashSet;

public class FragmentationGrenadeItemRenderer implements ICustomItemRenderer
{
	public static final FragmentationGrenadeItemRenderer INSTANCE = new FragmentationGrenadeItemRenderer();

	private static final HashSet<LivingEntity> BEEPING_PLAYERS = new HashSet<>();

	private FragmentationGrenadeItemRenderer()
	{
		SoundTimelineManager.SOUND_EVENT_ENTERED.register((instance, timelineEvent, delta) -> {
			if (timelineEvent.equals(SwgSoundTimelines.FRAGMENTATION_GRENADE_BEEP_ID) && instance instanceof FragmentationGrenadeItemSoundInstance fgisi)
				BEEPING_PLAYERS.add(fgisi.getPlayer());
		});

		SoundTimelineManager.SOUND_EVENT_LEFT.register((instance, timelineEvent, delta) -> {
			if (timelineEvent.equals(SwgSoundTimelines.THERMAL_DETONATOR_BEEP_ID) && instance instanceof FragmentationGrenadeItemSoundInstance fgisi)
				BEEPING_PLAYERS.remove(fgisi.getPlayer());
		});
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
				matrices.translate(0.4, -0.3, 0);
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
				matrices.translate(0, -0.3, 0);
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
		if (!(stack.getItem() instanceof FragmentationGrenadeItem fg))
			return;

		matrices.push();

		var tet = new ThrowableExplosiveTag(stack.getOrCreateNbt());

		var m = P3dManager.INSTANCE.get(FragmentationGrenadeRenderer.MODEL);
		if (m == null)
		{
			var crashReport = CrashReport.create(new IllegalStateException("Fragmentation Grenade model is null"), String.format("Unable to load fragmentation grenade model: %s", FragmentationGrenadeRenderer.MODEL));
			throw new CrashException(crashReport);
		}

		var primed = tet.primed;
		var beeping = false;

		if (entity == null)
			entity = MinecraftClient.getInstance().player;

		if (entity != null)
			beeping = BEEPING_PLAYERS.contains(entity);

		final var renderedTexture = primed ? (beeping ? FragmentationGrenadeRenderer.TEXTURE_BEEPING : FragmentationGrenadeRenderer.TEXTURE_PRIMED) : FragmentationGrenadeRenderer.TEXTURE_OFF;
		m.render(matrices, vertexConsumers, tet, null, (v, tag, obj) -> v.getBuffer(RenderLayer.getEntityCutout(renderedTexture)), light, 0, 255, 255, 255, 255);

		matrices.pop();
	}
}
