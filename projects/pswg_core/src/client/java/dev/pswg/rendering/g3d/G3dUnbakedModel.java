package dev.pswg.rendering.g3d;

import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.client.resources.model.cuboid.ItemTransforms;
import net.minecraft.client.resources.model.sprite.TextureSlots;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

/**
 * Replaces only geometry. Vanilla still resolves the sidecar's parent,
 * transforms, particle, lighting, and texture slots.
 *
 * @param sidecar  The normal vanilla model JSON.
 * @param geometry The G3D geometry with the same identifier.
 */
public record G3dUnbakedModel(UnbakedModel sidecar, G3dGeometry geometry) implements UnbakedModel
{
	/**
	 * Preserves the sidecar's ambient occlusion setting.
	 */
	@Override
	public @Nullable Boolean ambientOcclusion()
	{
		return sidecar.ambientOcclusion();
	}

	/**
	 * Preserves the sidecar's inventory lighting choice.
	 */
	@Override
	public @Nullable GuiLight guiLight()
	{
		return sidecar.guiLight();
	}

	/**
	 * Preserves vanilla item display transforms.
	 */
	@Override
	public @Nullable ItemTransforms transforms()
	{
		return sidecar.transforms();
	}

	/**
	 * Preserves ordinary vanilla texture and particle references.
	 */
	@Override
	public TextureSlots.Data textureSlots()
	{
		return sidecar.textureSlots();
	}

	/**
	 * Keeps the normal parent-model resolution path.
	 */
	@Override
	public @Nullable Identifier parent()
	{
		return sidecar.parent();
	}
}
