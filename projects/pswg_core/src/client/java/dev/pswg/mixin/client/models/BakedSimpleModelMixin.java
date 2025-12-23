package dev.pswg.mixin.client.models;

import dev.pswg.rendering.models.GalaxiesModelBakery;
import net.minecraft.client.render.model.BakedSimpleModel;
import net.minecraft.client.render.model.Geometry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BakedSimpleModel.class)
public interface BakedSimpleModelMixin
{
	@Inject(method = "getGeometry(Lnet/minecraft/client/render/model/BakedSimpleModel;)Lnet/minecraft/client/render/model/Geometry;", at = @At("HEAD"), cancellable = true)
	private static void getGeometry(BakedSimpleModel model, CallbackInfoReturnable<Geometry> cir)
	{
		GalaxiesModelBakery.getGeometry(model).ifPresent(cir::setReturnValue);
	}
}
