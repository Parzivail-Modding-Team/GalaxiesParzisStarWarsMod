package dev.pswg.mixin.recoil;

import dev.pswg.interaction.IRecoilEntity;
import dev.pswg.interaction.RecoilEntityAttachment;
import dev.pswg.networking.RecoilImpulsePayload;
import dev.pswg.world.GameTime;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Adds angular impulses and recovers the shared view offset.
 */
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements IRecoilEntity
{
	/**
	 * Last local recoil update.
	 */
	@Unique
	private long _pswgLastRecoilTick = Long.MIN_VALUE;

	@Override
	public long pswg$getRecoilTime()
	{
		return RecoilEntityAttachment.get((LivingEntity)(Object)this).recoilStart();
	}

	@Override
	public float pswg$getRecoilFovMultiplier(LivingEntity entity, float partialTick)
	{
		var elapsed = GameTime.now(entity.level()) - pswg$getRecoilTime() + partialTick;
		if (elapsed <= 0 || elapsed >= 20)
		{
			return 1;
		}

		return (float)(1 - 0.1 * Math.exp(-5 * elapsed));
	}

	@Override
	public void pswg$addRecoilImpulse(Vector3f degrees, long sourceSerial, int recoveryTicks, float[] pitchMultipliers, float[] yawCycle)
	{
		var entity = (LivingEntity)(Object)this;
		var state = RecoilEntityAttachment.get(entity);
		var now = GameTime.now(entity.level());
		var elapsed = now - state.recoilStart();
		var sequence = 1;

		if (sourceSerial == state.recoilSourceSerial() && elapsed >= 0 && elapsed <= state.recoilTicks())
		{
			sequence = state.recoilShotSequence() == Integer.MAX_VALUE ? Integer.MAX_VALUE : state.recoilShotSequence() + 1;
		}

		var pitch = pitchMultipliers[Math.min(sequence - 1, pitchMultipliers.length - 1)];
		var yaw = yawCycle[(sequence - 1) % yawCycle.length];
		var impulse = new Vector3f(degrees.x * pitch, degrees.y * pitch * yaw, degrees.z * pitch);

		if (entity instanceof ServerPlayer player)
		{
			var event = state.recoilEventSequence() + 1;
			state.withRecoilEventSequence(event)
			     .withRecoilStart(now)
			     .withRecoilTicks(Math.max(0, recoveryTicks))
			     .withRecoilShotSequence(sequence)
			     .withRecoilSourceSerial(sourceSerial)
			     .set(entity);

			if (ServerPlayNetworking.canSend(player, RecoilImpulsePayload.TYPE))
			{
				ServerPlayNetworking.send(player, new RecoilImpulsePayload(
						player.level().dimension().identifier(),
						player.getId(),
						event,
						impulse,
						Math.max(0, recoveryTicks),
						sequence,
						sourceSerial
				));
			}

			return;
		}

		RecoilEntityAttachment.queueImpulse(entity, impulse, recoveryTicks, now, 0, sequence, sourceSerial);
	}

	/**
	 * Applies a kick and returns the actual rotation after pitch clamping.
	 */
	private static Vector3f applyKick(LivingEntity entity, Vector3f impulse)
	{
		var pitch = entity.getXRot();
		var yaw = entity.getYRot();
		setView(entity, pitch + impulse.x, yaw + impulse.y);

		return new Vector3f(entity.getXRot() - pitch, entity.getYRot() - yaw, 0);
	}

	/**
	 * Sets view and head yaw together. Pitch stays inside native bounds.
	 */
	private static void setView(LivingEntity entity, float pitch, float yaw)
	{
		entity.setXRot(Mth.clamp(pitch, -90, 90));
		entity.setYRot(yaw);
		entity.setYHeadRot(yaw);
	}

	/**
	 * Consumes only input that opposes recoverable recoil.
	 */
	private static float consumeCounterInput(float offset, float input)
	{
		if (offset == 0 || offset * input >= 0)
		{
			return offset;
		}

		return Math.copySign(Math.max(0, Math.abs(offset) - Math.abs(input)), offset);
	}

	/**
	 * Applies all queued sources to one offset and one return aim.
	 */
	@Inject(method = "aiStep()V", at = @At("TAIL"))
	private void tickRecoil(CallbackInfo ci)
	{
		var entity = (LivingEntity)(Object)this;
		if (entity instanceof ServerPlayer)
		{
			return;
		}

		var now = GameTime.now(entity.level());
		if (now == _pswgLastRecoilTick)
		{
			return;
		}

		_pswgLastRecoilTick = now;
		var state = RecoilEntityAttachment.get(entity);
		var offset = new Vector3f(state.recoilAimOffset());
		var pending = state.recoilImpulse();

		if (!state.hasViewSnapshot() && offset.lengthSquared() == 0 && pending.lengthSquared() == 0)
		{
			return;
		}

		var returnPitch = state.hasViewSnapshot() ? state.recoilReturnPitch() : entity.getXRot();
		var returnYaw = state.hasViewSnapshot() ? state.recoilReturnYaw() : entity.getYRot();

		if (state.hasViewSnapshot())
		{
			var inputPitch = entity.getXRot() - state.lastViewPitch();
			var yawChange = entity.getYRot() - state.lastViewYaw();
			var inputYaw = Mth.wrapDegrees(yawChange);
			offset.x = consumeCounterInput(offset.x, inputPitch);
			offset.y = consumeCounterInput(offset.y, inputYaw);

			if (inputPitch != 0)
			{
				returnPitch = entity.getXRot() - offset.x;
			}
			returnYaw += yawChange - inputYaw;

			if (inputYaw != 0)
			{
				returnYaw = entity.getYRot() - offset.y;
			}
		}

		if (pending.lengthSquared() > 0)
		{
			offset.add(applyKick(entity, pending));
		}

		if (offset.lengthSquared() > 0 && now - state.recoilStart() > state.recoilTicks())
		{
			offset.mul((float)Math.exp(-1.0 / Math.max(1, state.recoilTicks())));

			if (offset.lengthSquared() <= 1.0E-6f)
			{
				offset.zero();
			}

			// Set the target from the stored baseline
			setView(entity, returnPitch + offset.x, returnYaw + offset.y);
		}

		var active = offset.lengthSquared() > 0;
		state.withRecoilImpulse(new Vector3f())
		     .withRecoilAimOffset(offset)
		     .withRecoilReturnPitch(returnPitch)
		     .withRecoilReturnYaw(returnYaw)
		     .withLastViewPitch(active ? entity.getXRot() : 0)
		     .withLastViewYaw(active ? entity.getYRot() : 0)
		     .withHasViewSnapshot(active)
		     .set(entity);
	}
}
