package com.parzivail.pswg.mixin;

import com.parzivail.pswg.client.render.armor.ArmorRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArmorFeatureRenderer.class)
public abstract class ArmorFeatureRendererMixin<T extends LivingEntity, M extends BipedEntityModel<T>, A extends BipedEntityModel<T>> extends FeatureRenderer<T, M>
{
	public ArmorFeatureRendererMixin(FeatureRendererContext<T, M> context)
	{
		super(context);
	}

	@Inject(method = "renderArmor(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/entity/EquipmentSlot;ILnet/minecraft/client/render/entity/model/BipedEntityModel;)V", at = @At("HEAD"), cancellable = true)
	public void renderArmor(MatrixStack matrices, VertexConsumerProvider vertexConsumers, T entity, EquipmentSlot armorSlot, int light, A model, CallbackInfo ci)
	{
		ArmorRenderer.renderArmor(getContextModel(), matrices, vertexConsumers, entity, armorSlot, light, model, ci);
	}

	@Inject(method = "Lnet/minecraft/client/render/entity/feature/ArmorFeatureRenderer;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V", at = @At("HEAD"), cancellable = true)
	public void renderExtraArmor(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float f, float g, float h, float j, float k, float l, CallbackInfo ci)
	{
		ArmorRenderer.renderExtraArmor(getContextModel(), matrices, vertexConsumers, entity, light, ci);
	}
}
