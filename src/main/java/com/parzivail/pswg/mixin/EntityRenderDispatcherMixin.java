package com.parzivail.pswg.mixin;

import com.parzivail.pswg.component.SwgEntityComponents;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(EntityRenderDispatcher.class)
@Environment(EnvType.CLIENT)
public class EntityRenderDispatcherMixin
{
	@Shadow
	private Map<String, PlayerEntityRenderer> modelRenderers;

	@SuppressWarnings("unchecked")
	@Inject(method = "getRenderer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;getModel()Ljava/lang/String;"), cancellable = true)
	private <T extends Entity> void getRenderer(T entity, CallbackInfoReturnable<EntityRenderer<? super T>> cir)
	{
		if (!(entity instanceof PlayerEntity))
			return;

		var pc = SwgEntityComponents.getPersistent((PlayerEntity)entity);

		var species = pc.getSpecies();
		if (species == null)
			return;

		cir.setReturnValue((EntityRenderer<T>)modelRenderers.get(species.getModel().toString()));
		cir.cancel();
	}
}
