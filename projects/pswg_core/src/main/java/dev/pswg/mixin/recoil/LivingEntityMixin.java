package dev.pswg.mixin.recoil;

import dev.pswg.interaction.IRecoilEntity;
import dev.pswg.interaction.RecoilEntityAttachment;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
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
	public void pswg$addRecoilImpulse(Vector3f degrees, long sourceSerial, int recoveryTicks, float[] pitchMultipliers, float[] yawCycle)
	{
		var self = (LivingEntity)(Object)this;
		var recoil = RecoilEntityAttachment.get(self);
		var currentTick = self.level().getGameTime();
		var ticksSinceShot = currentTick - recoil.recoilStart();

		var shotSequence = sourceSerial != recoil.recoilSourceSerial() || ticksSinceShot < 0 || ticksSinceShot > recoil.recoilTicks()
		                   ? 1
		                   : recoil.recoilShotSequence() == Integer.MAX_VALUE ? Integer.MAX_VALUE : recoil.recoilShotSequence() + 1;

		var pitchMultiplier = pitchMultipliers[Math.min(shotSequence - 1, pitchMultipliers.length - 1)];
		var yawMultiplier = yawCycle[(shotSequence - 1) % yawCycle.length];
		var impulse = new Vector3f(degrees.x * pitchMultiplier, degrees.y * pitchMultiplier * yawMultiplier, degrees.z * pitchMultiplier);

		recoil.withRecoilImpulse(recoil.recoilImpulse().add(impulse, new Vector3f()))
		      .withRecoilTicks(Math.max(0, recoveryTicks))
		      .withRecoilStart(currentTick)
		      .withRecoilShotSequence(shotSequence)
		      .withRecoilSourceSerial(sourceSerial)
		      .set(self);
	}

	/**
	 * Applies one signed pitch/yaw rotation and returns the actual view change after pitch clamping.
	 */
	private static Vector3f applyAimRotation(LivingEntity entity, Vector3f degrees)
	{
		var previousPitch = entity.getXRot();
		var previousYaw = entity.getYHeadRot();
		entity.setXRot(Mth.clamp(entity.getXRot() + degrees.x, -90, 90));
		entity.setYRot(entity.getYRot() + degrees.y);
		entity.setYHeadRot(entity.getYHeadRot() + degrees.y);

		return new Vector3f(entity.getXRot() - previousPitch, entity.getYHeadRot() - previousYaw, 0);
	}

	/**
	 * Removes only mouse/controller motion that counters the stored recoil; excess input remains normal aim.
	 */
	private static float consumeCounterInput(float recoilOffset, float playerInput)
	{
		if (recoilOffset == 0 || recoilOffset * playerInput >= 0)
			return recoilOffset;

		var remaining = Math.max(0, Math.abs(recoilOffset) - Math.abs(playerInput));
		return Math.copySign(remaining, recoilOffset);
	}

	@Inject(method = "aiStep()V", at = @At(value = "TAIL"))
	private void tick(CallbackInfo ci)
	{
		var self = (LivingEntity)(Object)this;
		var recoil = RecoilEntityAttachment.get(self);
		var aimOffset = recoil.recoilAimOffset();
		var changed = recoil.hasViewSnapshot() || aimOffset.lengthSquared() > 1.0E-8f;

		if (recoil.hasViewSnapshot())
		{
			var inputPitch = self.getXRot() - recoil.lastViewPitch();
			var inputYaw = Mth.wrapDegrees(self.getYHeadRot() - recoil.lastViewYaw());
			aimOffset = new Vector3f(
					consumeCounterInput(aimOffset.x, inputPitch),
					consumeCounterInput(aimOffset.y, inputYaw),
					0
			);
		}

		if (recoil.recoilImpulse().lengthSquared() > 1.0E-8f)
		{
			var kick = recoil.recoilImpulse();
			aimOffset = aimOffset.add(applyAimRotation(self, kick), new Vector3f());
			recoil = recoil.withRecoilImpulse(new Vector3f());
			changed = true;
		}

		var ticksSinceShot = self.level().getGameTime() - recoil.recoilStart();
		if (aimOffset.lengthSquared() > 1.0E-8f && ticksSinceShot > recoil.recoilTicks())
		{
			var recoveryFraction = (float)Math.exp(-1.0 / Math.max(1, recoil.recoilTicks()));
			var recovery = aimOffset.mul(recoveryFraction - 1, new Vector3f());
			aimOffset = aimOffset.add(applyAimRotation(self, recovery), new Vector3f());
			if (aimOffset.lengthSquared() <= 1.0E-8f)
				aimOffset = new Vector3f();
			changed = true;
		}

		if (changed)
		{
			var hasViewSnapshot = aimOffset.lengthSquared() > 1.0E-8f;
			recoil.withRecoilAimOffset(aimOffset)
			      .withLastViewPitch(hasViewSnapshot ? self.getXRot() : 0)
			      .withLastViewYaw(hasViewSnapshot ? self.getYHeadRot() : 0)
			      .withHasViewSnapshot(hasViewSnapshot)
			      .set(self);
		}
	}
}
