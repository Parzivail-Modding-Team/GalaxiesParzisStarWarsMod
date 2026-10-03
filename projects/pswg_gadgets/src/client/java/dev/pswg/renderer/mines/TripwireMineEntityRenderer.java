package dev.pswg.renderer.mines;

import dev.pswg.Gadgets;
import dev.pswg.entity.mines.TripwireMineEntity;
import dev.pswg.model.g3d.G3dTransform;
import dev.pswg.rendering.g3d.G3dEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.phys.AABB;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Map;

/**
 * Shares the tripwire body with its item and poses its authored beam per entity.
 * The beam is collapsed at rest, so inventory rendering contains only the mine.
 */
public final class TripwireMineEntityRenderer extends G3dEntityRenderer<TripwireMineEntity>
{
	/**
	 * Beam start at the top of the mine, measured in blocks from entity_origin.
	 */
	private static final float BEAM_OFFSET = 1 / 16f;

	/**
	 * Binds the body, beam, and placement socket from the same shared asset.
	 */
	public TripwireMineEntityRenderer(EntityRendererProvider.Context context)
	{
		super(context, Gadgets.id("item/tripwire_mine"), "entity_origin");
	}

	/**
	 * Aligns local +Y with the mine's look direction, including walls and ceilings.
	 */
	@Override
	protected void extractTransform(TripwireMineEntity entity, Matrix4f output, float tickDelta)
	{
		output.rotateY((float)Math.toRadians(-entity.getYRot(tickDelta) + 90));
		output.rotateZ((float)Math.toRadians(entity.getXRot(tickDelta) + 90));
	}

	/**
	 * Captures beam length in the pose. No shared model part is edited at submit.
	 */
	@Override
	protected Map<String, G3dTransform> extractPose(TripwireMineEntity entity, float tickDelta)
	{
		float length = entity.tripwireDistance - BEAM_OFFSET;
		if (!entity.isPrimed() || !Float.isFinite(length) || length <= 0)
			return Map.of();
		return Map.of("beam", new G3dTransform(
				new Vector3f(0.5f, BEAM_OFFSET, 0.5f),
				new Quaternionf(),
				new Vector3f(1, length, 1)
		));
	}

	/**
	 * Includes the visible ray when the body itself is outside the camera view.
	 */
	@Override
	protected AABB getBoundingBoxForCulling(TripwireMineEntity entity, float tickDelta)
	{
		var bounds = super.getBoundingBoxForCulling(entity, tickDelta);
		return entity.isPrimed() && Float.isFinite(entity.tripwireDistance)
				? bounds.expandTowards(entity.getLookAngle().scale(Math.max(0, entity.tripwireDistance)))
				: bounds;
	}
}
