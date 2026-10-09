package dev.pswg.interaction;

import dev.pswg.Galaxies;
import dev.pswg.codecgenerator.CodecDefault;
import dev.pswg.codecgenerator.CodecRange;
import dev.pswg.codecgenerator.GenerateCodec;
import dev.pswg.generated.codecs.IRecoilEntityAttachmentCodec;
import dev.pswg.generated.recordbuilders.IRecoilEntityAttachmentBuilder;
import dev.pswg.mutablerecord.MutableRecord;
import dev.pswg.networking.RecoilImpulsePayload;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Vector3f;

/**
 * Stores local recoil and recovery history.
 *
 * @param recoilImpulse       Pending one-shot angular displacement in degrees.
 * @param recoilAimOffset     Current aim displacement that is gradually recovered after firing stops.
 * @param recoilTicks         Quiet ticks before the current firing burst resets.
 * @param recoilStart         Timestamp of the last authored impulse for view/item presentation.
 * @param recoilShotSequence  Number of shots in the current firing burst.
 * @param recoilSourceSerial  Physical weapon whose current burst profile is stored.
 * @param lastViewPitch       Last pitch sampled while tracking player compensation input.
 * @param lastViewYaw         Last head yaw sampled while tracking player compensation input.
 * @param hasViewSnapshot     Whether the previous view angles are valid input samples.
 * @param recoilReturnPitch   Pitch to recover to after uncompensated recoil.
 * @param recoilReturnYaw     Yaw to recover to after uncompensated recoil.
 * @param recoilEventSequence Last accepted impulse event, independent of weapon burst sequence.
 */
@MutableRecord
@GenerateCodec
public record RecoilEntityAttachment(
		@CodecDefault("new org.joml.Vector3f()") Vector3f recoilImpulse,
		@CodecDefault("new org.joml.Vector3f()") Vector3f recoilAimOffset,
		@CodecDefault("0") @CodecRange(min = 0) int recoilTicks,
		@CodecDefault("0L") long recoilStart,
		@CodecDefault("0") @CodecRange(min = 0) int recoilShotSequence,
		@CodecDefault("0L") long recoilSourceSerial,
		@CodecDefault("0.0f") float lastViewPitch,
		@CodecDefault("0.0f") float lastViewYaw,
		@CodecDefault("false") boolean hasViewSnapshot,
		@CodecDefault("0.0f") float recoilReturnPitch,
		@CodecDefault("0.0f") float recoilReturnYaw,
		@CodecDefault("0L") @CodecRange(min = 0) long recoilEventSequence
) implements IRecoilEntityAttachmentBuilder, IRecoilEntityAttachmentCodec
{
	/**
	 * No pending recoil.
	 */
	public static final RecoilEntityAttachment DEFAULT = new RecoilEntityAttachment(new Vector3f(), new Vector3f(), 0, -20, 0, 0, 0, 0, false, 0, 0, 0);

	public static final AttachmentType<RecoilEntityAttachment> ATTACHMENT = AttachmentRegistry.create(
			Galaxies.id("recoil_entity"),
			builder -> builder.initializer(() -> DEFAULT)
	);

	public static void register()
	{
		PayloadTypeRegistry.clientboundPlay().register(RecoilImpulsePayload.TYPE, RecoilImpulsePayload.PACKET_CODEC);
	}

	/**
	 * Reads this entity's local history.
	 */
	public static RecoilEntityAttachment get(LivingEntity entity)
	{
		return entity.getAttachedOrCreate(ATTACHMENT, () -> DEFAULT);
	}

	/**
	 * Adds an accepted impulse without replacing offsets or input samples.
	 */
	public static void queueImpulse(
			LivingEntity entity,
			Vector3f impulse,
			int recoveryTicks,
			long startedAt,
			long eventSequence,
			int shotSequence,
			long sourceSerial
	)
	{
		var state = get(entity);

		if (eventSequence > 0 && eventSequence <= state.recoilEventSequence())
		{
			return;
		}

		var active = state.recoilImpulse().lengthSquared() > 0 || state.recoilAimOffset().lengthSquared() > 0;
		var ticks = active ? Math.max(state.recoilTicks(), recoveryTicks) : recoveryTicks;

		state.withRecoilImpulse(state.recoilImpulse().add(impulse, new Vector3f()))
		     .withRecoilTicks(Math.max(0, ticks))
		     .withRecoilStart(startedAt)
		     .withRecoilEventSequence(eventSequence > 0 ? eventSequence : state.recoilEventSequence())
		     .withRecoilShotSequence(shotSequence)
		     .withRecoilSourceSerial(sourceSerial)
		     .set(entity);
	}

	public void set(LivingEntity entity)
	{
		entity.setAttached(ATTACHMENT, this);
	}
}
