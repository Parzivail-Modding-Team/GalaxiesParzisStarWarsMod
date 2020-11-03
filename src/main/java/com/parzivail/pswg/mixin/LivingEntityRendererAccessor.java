package com.parzivail.pswg.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(LivingEntityRenderer.class)
@Environment(EnvType.CLIENT)
public interface LivingEntityRendererAccessor<T extends LivingEntity, M extends EntityModel<T>>
{
	@Accessor
	@Final
	List<FeatureRenderer<T, M>> getFeatures();
}
