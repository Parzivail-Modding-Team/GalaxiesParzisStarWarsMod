package dev.pswg.interaction;

import dev.pswg.Galaxies;
import dev.pswg.codecgenerator.CodecSource;
import dev.pswg.codecgenerator.GenerateCodec;
import dev.pswg.codecgenerator.UseCodec;
import dev.pswg.generated.codecs.ILeftClickingEntityAttachmentCodec;
import dev.pswg.generated.recordbuilders.ILeftClickingEntityAttachmentBuilder;
import dev.pswg.mutablerecord.MutableRecord;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

/**
 * An attachment for entities that contains the data required to
 * represent their left-using state
 */
@MutableRecord
@GenerateCodec
public record LeftClickingEntityAttachment(
		boolean isUsingItemLeft,
		int itemLeftUseTimeLeft,
		@UseCodec(
				customCodec = @CodecSource(source = ItemStack.class, member = "OPTIONAL_CODEC"),
				customPacket = @CodecSource(source = ItemStack.class, member = "OPTIONAL_PACKET_CODEC")
		)
		ItemStack leftActiveItemStack
) implements ILeftClickingEntityAttachmentBuilder, ILeftClickingEntityAttachmentCodec
{
	@SuppressWarnings("UnstableApiUsage")
	public static final AttachmentType<LeftClickingEntityAttachment> ATTACHMENT = AttachmentRegistry.create(
			Galaxies.id("left_clicking_entity"),
			builder -> builder
					.initializer(() -> new LeftClickingEntityAttachment(false, 0, ItemStack.EMPTY))
					.persistent(LeftClickingEntityAttachment.CODEC)
					.syncWith(LeftClickingEntityAttachment.PACKET_CODEC, AttachmentSyncPredicate.all())
	);

	@SuppressWarnings("EmptyMethod")
	public static void register()
	{
		// Dummy method to force static initialization
	}

	public static LeftClickingEntityAttachment get(LivingEntity entity)
	{
		return entity.getAttachedOrCreate(ATTACHMENT, () -> new LeftClickingEntityAttachment(
				false,
				0,
				ItemStack.EMPTY
		));
	}

	public void set(LivingEntity entity)
	{
		entity.setAttached(ATTACHMENT, this);
	}
}
