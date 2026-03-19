package dev.pswg.interaction;

import dev.pswg.Galaxies;
import dev.pswg.codecgenerator.GenPacketCodec;
import dev.pswg.codecgenerator.GenStandardCodec;
import dev.pswg.codecgenerator.GenerateCodec;
import dev.pswg.codecgenerator.UseCodec;
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
 * @param recoilVelocity The recoil rate in degrees per tick
 */
@MutableRecord
@GenerateCodec
public record RecoilEntityAttachment(Vector3f recoilVelocity) implements IRecoilEntityAttachmentBuilder, IRecoilEntityAttachmentCodec
{
	@SuppressWarnings("UnstableApiUsage")
	public static final AttachmentType<RecoilEntityAttachment> ATTACHMENT = AttachmentRegistry.create(
			Galaxies.id("recoil_entity"),
			builder -> builder
					.initializer(() -> new RecoilEntityAttachment(new Vector3f()))
					.persistent(RecoilEntityAttachment.CODEC)
					.syncWith(RecoilEntityAttachment.PACKET_CODEC, AttachmentSyncPredicate.all())
	);

	@SuppressWarnings("EmptyMethod")
	public static void register()
	{
		// Dummy method to force static initialization
	}

	public static RecoilEntityAttachment get(LivingEntity entity)
	{
		return entity.getAttachedOrCreate(ATTACHMENT, () -> new RecoilEntityAttachment(
				new Vector3f()
		));
	}

	public void set(LivingEntity entity)
	{
		entity.setAttached(ATTACHMENT, this);
	}
}
