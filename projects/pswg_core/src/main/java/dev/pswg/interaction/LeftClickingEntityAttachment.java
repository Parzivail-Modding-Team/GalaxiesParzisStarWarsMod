package dev.pswg.interaction;

import dev.pswg.Galaxies;
import dev.pswg.codecgenerator.CodecSource;
import dev.pswg.codecgenerator.GenerateCodec;
import dev.pswg.codecgenerator.UseCodec;
import dev.pswg.generated.codecs.ILeftClickingEntityAttachmentCodec;
import dev.pswg.generated.recordbuilders.ILeftClickingEntityAttachmentBuilder;
import dev.pswg.mutablerecord.MutableRecord;
import dev.pswg.networking.PlayerLeftUsingStateS2CPacket;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

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
	public static final AttachmentType<LeftClickingEntityAttachment> ATTACHMENT = AttachmentRegistry.createPersistent(
			Galaxies.id("left_clicking_entity"),
			LeftClickingEntityAttachment.CODEC
	);

	// TODO: this can be sync'd with the syncing extension if PACKET_CODEC can be PacketCodec<PacketByteBuf, T> instead
	//       of RegistryByteBuf
	//
	//	public static final AttachmentType<LeftClickingEntityAttachment> ATTACHMENT = AttachmentRegistry.create(
	//			Galaxies.id("left_clicking_entity"),
	//			builder -> builder
	//					.initializer(() -> new LeftClickingEntityAttachment(false, 0, ItemStack.EMPTY))
	//					.persistent(LeftClickingEntityAttachment.CODEC)
	//					.syncWith(LeftClickingEntityAttachment.PACKET_CODEC, AttachmentSyncPredicate.all())
	//	);

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

		if (entity instanceof ServerPlayerEntity player)
			ServerPlayNetworking.send(player, new PlayerLeftUsingStateS2CPacket(this));
	}
}
