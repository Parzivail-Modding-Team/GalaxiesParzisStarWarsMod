package dev.pswg.mixin.recoil;

import dev.pswg.interaction.IRecoilEntity;
import dev.pswg.interaction.RecoilEntityAttachment;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.util.Mth;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Main featureset for recoil support in entities. Handles most
 * item interactions and data storage.
 */
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements IRecoilEntity
{
	@Override
	public long pswg$getRecoilTime()
	{
		return RecoilEntityAttachment.get((LivingEntity)(Object)this).recoilStart();
	}

	@Override
	public float pswg$getRecoilFovMultiplier(LivingEntity entity, float tickDelta)
	{
		var recoilTime = entity.level().getGameTime() - this.pswg$getRecoilTime() + tickDelta;
		if (recoilTime <= 0 || recoilTime >= 20)
			return 1;

		var effectDepth = 0.1;
		var effectSpeed = 5;
		return (float)(1 - effectDepth * Math.exp(-effectSpeed * recoilTime));
	}

	@Override
	public void pswg$addRecoilImpulse(Vector3f degrees, int recoveryTicks)
	{
		var self = (LivingEntity)(Object)this;
		var recoil = RecoilEntityAttachment.get(self);

		recoil.withRecoilImpulse(recoil.recoilImpulse().add(degrees, new Vector3f()))
		      .withRecoilTicks(Math.max(recoil.recoilTicks(), Math.max(1, recoveryTicks)))
		      .withRecoilStart(self.level().getGameTime())
		      .set(self);
	}

	@Inject(method = "aiStep()V", at = @At(value = "TAIL"))
	private void tick(CallbackInfo ci)
	{
		var self = (LivingEntity)(Object)this;

		var impulse = RecoilEntityAttachment.get(self);
		if (impulse.recoilTicks() > 0)
		{
			var step = impulse.recoilImpulse().div(impulse.recoilTicks(), new Vector3f());
			self.setXRot(Mth.clamp(self.getXRot() + step.x, -90, 90));
			self.setYRot(self.getYRot() + step.y);
			impulse.withRecoilImpulse(impulse.recoilImpulse().sub(step, new Vector3f()))
			       .withRecoilTicks(impulse.recoilTicks() - 1)
			       .set(self);
		}

	}
}
