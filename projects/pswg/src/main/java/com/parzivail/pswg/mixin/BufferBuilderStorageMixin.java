package com.parzivail.pswg.mixin;

import com.parzivail.pswg.client.render.entity.EnergyRenderer;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.RenderLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BufferBuilderStorage.class)
public abstract class BufferBuilderStorageMixin
{
	@Shadow
	private static native void assignBufferBuilder(Object2ObjectLinkedOpenHashMap<RenderLayer, BufferBuilder> builderStorage, RenderLayer layer);

	@Inject(method = "method_54639", at = @At("RETURN"))
	private void addRenderLayers(Object2ObjectLinkedOpenHashMap<RenderLayer, BufferBuilder> map, CallbackInfo ci) {
		assignBufferBuilder(map, EnergyRenderer.LAYER_ENERGY);
	}
}
