package dev.pswg.mixin.recoil;

import dev.pswg.interaction.IRecoilEntity;
import dev.pswg.interaction.RecoilEntityAttachment;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
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
	@Unique
	private long pswg$recoilTime = 0;

	@Override
	public long pswg$getRecoilTime()
	{
		return pswg$recoilTime;
	}

	@Override
	public void pswg$setRecoilTime(long time)
	{
		pswg$recoilTime = time;
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
	public Vector3f pswg$getRecoilVelocity()
	{
		var self = (LivingEntity)(Object)this;

		return RecoilEntityAttachment
				.get(self)
				.recoilVelocity();
	}

	@Override
	public void pswg$setRecoilVelocity(Vector3f velocity)
	{
		var self = (LivingEntity)(Object)this;

		RecoilEntityAttachment
				.get(self)
				.withRecoilVelocity(velocity)
				.set(self);
	}

	@Override
	public void pswg$addRecoilVelocity(Vector3f velocity)
	{
		var self = (LivingEntity)(Object)this;

		var recoil = RecoilEntityAttachment.get(self);

		var newRecoil = recoil.recoilVelocity().add(velocity, new Vector3f());

		recoil.withRecoilVelocity(newRecoil)
		      .set(self);
	}

	@Inject(method = "aiStep()V", at = @At(value = "TAIL"))
	private void tick(CallbackInfo ci)
	{
		var self = (LivingEntity)(Object)this;

		var recoilVelocity = this.pswg$getRecoilVelocity();

		// Decelerate the recoil velocity
		if (recoilVelocity.lengthSquared() > 1e-3)
		{
			// Apply the recoil to the entity's angle
			self.setXRot(self.getXRot() + recoilVelocity.x);
			self.setYRot(self.getYRot() + recoilVelocity.y);

			// TODO: tune recoil decay
			this.pswg$setRecoilVelocity(recoilVelocity.mul(IRecoilEntity.RECOIL_DAMPENING, new Vector3f()));
		}
		else
			this.pswg$setRecoilVelocity(new Vector3f());
	}
}
