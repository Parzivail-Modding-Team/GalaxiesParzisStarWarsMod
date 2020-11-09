package com.parzivail.pswg.mixin;

import com.parzivail.pswg.client.model.npc.PlayerEntityRendererWithModel;
import com.parzivail.pswg.client.species.SwgSpeciesInstance;
import com.parzivail.pswg.client.species.SwgSpeciesModel;
import com.parzivail.pswg.client.species.SwgSpeciesModels;
import com.parzivail.pswg.component.SwgEntityComponents;
import com.parzivail.pswg.component.SwgPersistentComponents;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(EntityRenderDispatcher.class)
@Environment(EnvType.CLIENT)
public class EntityRenderDispatcherMixin
{
	@Shadow
	@Final
	private Map<String, PlayerEntityRenderer> modelRenderers;

	@Inject(method = "registerRenderers", at = @At("TAIL"))
	private void registerRenderers(ItemRenderer itemRenderer, ReloadableResourceManager reloadableResourceManager, CallbackInfo ci)
	{
		for (Map.Entry<Identifier, SwgSpeciesModel> pair : SwgSpeciesModels.MODELS.entrySet())
			modelRenderers.put(pair.getKey().toString(), new PlayerEntityRendererWithModel((EntityRenderDispatcher)(Object)this, pair.getValue().model));
	}

	@SuppressWarnings("unchecked")
	@Inject(method = "getRenderer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;getModel()Ljava/lang/String;"), cancellable = true)
	private <T extends Entity> void getRenderer(T entity, CallbackInfoReturnable<EntityRenderer<? super T>> cir)
	{
		if (!(entity instanceof PlayerEntity))
			return;

		SwgPersistentComponents pc = SwgEntityComponents.getPersistent((PlayerEntity)entity);

		SwgSpeciesInstance species = pc.getSpecies();
		if (species == null)
			return;

		cir.setReturnValue((EntityRenderer<T>)modelRenderers.get(species.getModel().identifier.toString()));
		cir.cancel();
	}
}
