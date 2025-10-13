package dev.pswg.mixin.recoil;

import dev.pswg.interaction.IRecoilEntity;
import dev.pswg.interaction.RecoilEntityAttachment;
import net.minecraft.entity.LivingEntity;
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

	@Inject(method = "tickMovement()V", at = @At(value = "TAIL"))
	private void tick(CallbackInfo ci)
	{
		var self = (LivingEntity)(Object)this;

		var recoilVelocity = this.pswg$getRecoilVelocity();

		// Decelerate the recoil velocity
		if (recoilVelocity.lengthSquared() > 1e-2)
		{
			// Apply the recoil to the entity's angle
			self.setPitch(self.getPitch() + recoilVelocity.x);
			self.setYaw(self.getYaw() + recoilVelocity.y);

			// TODO: tune recoil decay
			this.pswg$setRecoilVelocity(recoilVelocity.mul(0.6f));
		}
		else
			this.pswg$setRecoilVelocity(new Vector3f());
	}
}
