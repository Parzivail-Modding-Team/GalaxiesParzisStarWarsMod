package dev.pswg.mixin.client.models;

import com.mojang.math.Transformation;
import dev.pswg.rendering.g3d.G3dClientModels;
import dev.pswg.rendering.g3d.G3dGeometry;
import dev.pswg.rendering.g3d.G3dItemModel;
import dev.pswg.rendering.g3d.G3dRenderer;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.renderer.item.CuboidItemModelWrapper;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ModelRenderProperties;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.client.resources.model.sprite.TextureSlots;
import net.minecraft.resources.Identifier;
import org.joml.Matrix4fc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;

/**
 * Adapts only sampled or posed G3D leaves. Static atlas geometry is baked by
 * vanilla through Fabric's public unbaked-model hook, including nested selectors.
 */
@Mixin(CuboidItemModelWrapper.Unbaked.class)
public abstract class CuboidItemModelWrapperMixin
{
	/**
	 * Gets the wrapped model identifier.
	 *
	 * @return The wrapped model identifier.
	 */
	@Shadow
	public abstract Identifier model();

	/**
	 * Gets the optional additional transformation.
	 *
	 * @return The optional local transformation.
	 */
	@Shadow
	public abstract Optional<Transformation> transformation();

	/**
	 * Gets the tint sources for this item model.
	 *
	 * @return The tint source list.
	 */
	@Shadow
	public abstract List<ItemTintSource> tints();

	/**
	 * Keeps this private vanilla bake dependency limited to the special leaf path.
	 */
	@Inject(method = "bake", at = @At("HEAD"), cancellable = true)
	private void injectG3dBake(ItemModel.BakingContext context, Matrix4fc transformation, CallbackInfoReturnable<ItemModel> cir)
	{
		var baker = context.blockModelBaker();

		ResolvedModel resolvedModel = baker.getModel(model());
		if (!(resolvedModel.getTopGeometry() instanceof G3dGeometry geometry))
			return;

		var poseProvider = G3dClientModels.itemPose(model());
		if (geometry.atlasCapable() && poseProvider == null)
			return;

		TextureSlots textureSlots = resolvedModel.getTopTextureSlots();
		var properties = ModelRenderProperties.fromResolvedModel(baker, resolvedModel, textureSlots);
		Matrix4fc modelTransform = Transformation.compose(transformation, transformation());

		cir.setReturnValue(new G3dItemModel(
				new G3dRenderer(geometry, baker),
				properties,
				modelTransform,
				tints(),
				poseProvider
		));
	}
}
