package com.parzivail.pswg.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(EntityRenderDispatcher.class)
@Environment(EnvType.CLIENT)
public interface EntityRenderDispatcherAccessor
{
	@Accessor("modelRenderers")
	Map<String, PlayerEntityRenderer> getModelRenderers();
}
