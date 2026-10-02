package dev.pswg.renderer.mines;

import dev.pswg.Gadgets;
import dev.pswg.entity.mines.PressureMineEntity;
import dev.pswg.rendering.g3d.G3dEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.joml.Matrix4f;

/**
 * Shares the pressure mine's authored model with its inventory appearance.
 */
public final class PressureMineEntityRenderer extends G3dEntityRenderer<PressureMineEntity>
{
	/**
	 * Binds the mine to its shared model and artist-authored placement socket.
	 */
	public PressureMineEntityRenderer(EntityRendererProvider.Context context)
	{
		super(context, Gadgets.id("item/pressure_mine"), "entity_origin");
	}

	/**
	 * Captures the mine's yaw while keeping its flat base at the entity position.
	 */
	@Override
	protected void extractTransform(PressureMineEntity entity, Matrix4f output, float tickDelta)
	{
		output.rotateY((float)Math.toRadians(-entity.getYRot(tickDelta)));
	}
}
