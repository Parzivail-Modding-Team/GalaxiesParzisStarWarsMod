package dev.pswg.mixin.client.models;

import dev.pswg.rendering.models.GalaxiesModelBakery;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.client.resources.model.UnbakedGeometry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ResolvedModel.class)
public interface BakedSimpleModelMixin
{
	@Inject(method = "findTopGeometry(Lnet/minecraft/client/resources/model/ResolvedModel;)Lnet/minecraft/client/resources/model/UnbakedGeometry;", at = @At("HEAD"), cancellable = true)
	private static void getGeometry(ResolvedModel model, CallbackInfoReturnable<UnbakedGeometry> cir)
	{
		GalaxiesModelBakery.getGeometry(model).ifPresent(cir::setReturnValue);
	}
}
