package com.parzivail.pswg.mixin;

import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.entity.AnimationState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(SinglePartEntityModel.class)
public interface SinglePartEntityModelAccessor
{
	@Invoker
	void callAnimateMovement(Animation animation, float limbAngle, float limbDistance, float limbAngleScale, float limbDistanceScale);

	@Invoker
	void callUpdateAnimation(AnimationState animationState, Animation animation, float animationProgress, float speedMultiplier);

}
