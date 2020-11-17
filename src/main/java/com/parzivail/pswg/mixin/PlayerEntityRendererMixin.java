package com.parzivail.pswg.mixin;

import com.parzivail.pswg.client.features.ForceFeatureRenderer;
import com.parzivail.pswg.item.BlasterItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntityRenderer.class)
@Environment(EnvType.CLIENT)
public class PlayerEntityRendererMixin
{
	@SuppressWarnings("unchecked")
	@Inject(method = "Lnet/minecraft/client/render/entity/PlayerEntityRenderer;<init>(Lnet/minecraft/client/render/entity/EntityRenderDispatcher;Z)V", at = @At("TAIL"))
	private void init(CallbackInfo ci)
	{
		((LivingEntityRendererAccessor<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>>)this).getFeatures().add(new ForceFeatureRenderer<>((PlayerEntityRenderer)(Object)this));
	}

	@Inject(method = "getArmPose", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;getStackInHand(Lnet/minecraft/util/Hand;)Lnet/minecraft/item/ItemStack;"), cancellable = true)
	private static void getArmPose(AbstractClientPlayerEntity abstractClientPlayerEntity, Hand hand, CallbackInfoReturnable<BipedEntityModel.ArmPose> cir)
	{
		ItemStack itemStack = abstractClientPlayerEntity.getStackInHand(hand);
		if (itemStack.isEmpty())
			return;

		if (itemStack.getItem() instanceof BlasterItem)
			cir.setReturnValue(BipedEntityModel.ArmPose.BOW_AND_ARROW);
	}
}
