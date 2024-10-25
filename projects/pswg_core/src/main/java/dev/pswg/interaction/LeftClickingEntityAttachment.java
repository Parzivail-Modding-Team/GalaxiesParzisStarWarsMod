package dev.pswg.interaction;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.pswg.Galaxies;
import dev.pswg.networking.PlayerLeftUsingStateS2CPacket;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * An attachment for entities that contains the data required to
 * represent their left-using state
 */
public record LeftClickingEntityAttachment(boolean isUsingItemLeft, int itemLeftUseTimeLeft, ItemStack leftActiveItemStack)
{
	public static final Codec<LeftClickingEntityAttachment> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.BOOL.fieldOf("isUsingItemLeft").forGetter(LeftClickingEntityAttachment::isUsingItemLeft),
			Codec.INT.fieldOf("itemLeftUseTimeLeft").forGetter(LeftClickingEntityAttachment::itemLeftUseTimeLeft),
			ItemStack.OPTIONAL_CODEC.fieldOf("leftActiveItemStack").forGetter(LeftClickingEntityAttachment::leftActiveItemStack)
	).apply(instance, LeftClickingEntityAttachment::new));

	public static final PacketCodec<RegistryByteBuf, LeftClickingEntityAttachment> PACKET_CODEC = PacketCodec.tuple(
			PacketCodecs.BOOL,
			LeftClickingEntityAttachment::isUsingItemLeft,
			PacketCodecs.VAR_INT,
			LeftClickingEntityAttachment::itemLeftUseTimeLeft,
			ItemStack.OPTIONAL_PACKET_CODEC,
			LeftClickingEntityAttachment::leftActiveItemStack,
			LeftClickingEntityAttachment::new
	);

	public static final AttachmentType<LeftClickingEntityAttachment> ATTACHMENT = AttachmentRegistry.createPersistent(
			Galaxies.id("left_clicking_entity"),
			LeftClickingEntityAttachment.CODEC
	);

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

	public LeftClickingEntityAttachment withIsUsingItemLeft(boolean isLeftUsing)
	{
		return new LeftClickingEntityAttachment(isLeftUsing, itemLeftUseTimeLeft, leftActiveItemStack);
	}

	public LeftClickingEntityAttachment withItemLeftUseTimeLeft(int timeLeft)
	{
		return new LeftClickingEntityAttachment(isUsingItemLeft, timeLeft, leftActiveItemStack);
	}

	public LeftClickingEntityAttachment withLeftActiveItemStack(ItemStack stack)
	{
		return new LeftClickingEntityAttachment(isUsingItemLeft, itemLeftUseTimeLeft, stack);
	}
}
