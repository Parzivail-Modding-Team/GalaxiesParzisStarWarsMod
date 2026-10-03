package dev.pswg.renderer.grenades;

import dev.pswg.entity.grenades.GrenadeEntity;
import dev.pswg.rendering.g3d.G3dEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import org.joml.Matrix4f;
import org.jspecify.annotations.Nullable;

/**
 * Draws thrown grenades with the same G3D assets used by their item models.
 * The asset's entity_origin socket places its base at the projectile position.
 *
 * @param <T> The grenade entity type.
 */
public final class G3dGrenadeEntityRenderer<T extends GrenadeEntity> extends G3dEntityRenderer<T>
{
	/**
	 * Optional primed item asset, selected from synchronized gameplay state.
	 */
	private final @Nullable Identifier _primedModelId;

	/**
	 * Uses one shared asset for a grenade without a separate primed appearance.
	 */
	public G3dGrenadeEntityRenderer(EntityRendererProvider.Context context, Identifier modelId)
	{
		this(context, modelId, null);
	}

	/**
	 * Uses the item's normal and primed assets for both handheld and thrown forms.
	 */
	public G3dGrenadeEntityRenderer(
			EntityRendererProvider.Context context,
			Identifier modelId,
			@Nullable Identifier primedModelId
	)
	{
		super(context, modelId, "entity_origin");
		_primedModelId = primedModelId;
	}

	/**
	 * Selects the matching primed appearance without retaining a reload-bound model.
	 */
	@Override
	protected Identifier extractModelId(T entity, float tickDelta)
	{
		return entity.isPrimed() && _primedModelId != null ? _primedModelId : super.extractModelId(entity, tickDelta);
	}

	/**
	 * Keeps the projectile's existing client yaw convention.
	 */
	@Override
	protected void extractTransform(T entity, Matrix4f output, float tickDelta)
	{
		output.rotateY((float)Math.toRadians(-entity.getClientYaw()));
	}
}
