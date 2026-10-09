package dev.pswg.interaction;

import dev.pswg.Galaxies;
import dev.pswg.codecgenerator.CodecDefault;
import dev.pswg.codecgenerator.CodecRange;
import dev.pswg.codecgenerator.GenerateCodec;
import dev.pswg.generated.codecs.IRecoilEntityAttachmentCodec;
import dev.pswg.generated.recordbuilders.IRecoilEntityAttachmentBuilder;
import dev.pswg.mutablerecord.MutableRecord;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Vector3f;

/**
 * An attachment for entities that contains the data required to
 * represent recoil
 *
 * @param recoilImpulse      Pending one-shot angular displacement in degrees.
 * @param recoilAimOffset    Current aim displacement that is gradually recovered after firing stops.
 * @param recoilTicks        Quiet ticks before the current firing burst resets.
 * @param recoilStart        Timestamp of the last authored impulse for view/item presentation.
 * @param recoilShotSequence Number of shots in the current firing burst.
 * @param recoilSourceSerial Physical weapon whose current burst profile is stored.
 * @param lastViewPitch      Last pitch sampled while tracking player compensation input.
 * @param lastViewYaw        Last head yaw sampled while tracking player compensation input.
 * @param hasViewSnapshot    Whether the previous view angles are valid input samples.
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
		@CodecDefault("false") boolean hasViewSnapshot
) implements IRecoilEntityAttachmentBuilder, IRecoilEntityAttachmentCodec
{
	/**
	 * No pending recoil.
	 */
	public static final RecoilEntityAttachment DEFAULT = new RecoilEntityAttachment(new Vector3f(), new Vector3f(), 0, -20, 0, 0, 0, 0, false);

	@SuppressWarnings("UnstableApiUsage")
	public static final AttachmentType<RecoilEntityAttachment> ATTACHMENT = AttachmentRegistry.create(
			Galaxies.id("recoil_entity"),
			builder -> builder
					.initializer(() -> DEFAULT)
					.syncWith(RecoilEntityAttachment.PACKET_CODEC, AttachmentSyncPredicate.all())
	);

	@SuppressWarnings("EmptyMethod")
	public static void register()
	{
		// Dummy method to force static initialization
	}

	public static RecoilEntityAttachment get(LivingEntity entity)
	{
		return entity.getAttachedOrCreate(ATTACHMENT, () -> DEFAULT);
	}

	public void set(LivingEntity entity)
	{
		entity.setAttached(ATTACHMENT, this);
	}
}
