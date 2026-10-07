package dev.pswg.interaction;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import dev.pswg.Galaxies;
import dev.pswg.codecgenerator.CodecRange;
import dev.pswg.codecgenerator.GenerateCodec;
import dev.pswg.codecgenerator.GenerateEnumCodec;
import dev.pswg.generated.codecs.IItemInteractionKindCodec;
import dev.pswg.generated.codecs.IItemInteractionTimerCodec;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.world.entity.LivingEntity;

/**
 * Unsaved item interaction timing.
 */
@GenerateCodec(strict = true)
public record ItemInteractionTimer(
		ItemInteractionKind kind,
		long serial,
		@CodecRange(min = 0) long startedAt,
		@CodecRange(min = 0) long completesAt
) implements IItemInteractionTimerCodec
{
	/**
	 * Supported player-visible interactions.
	 */
	@GenerateEnumCodec
	public enum ItemInteractionKind implements IItemInteractionKindCodec
	{
		/**
		 * Equipping/drawing an item.
		 */
		DRAW,

		/**
		 * Reloading a stored resource.
		 */
		RELOAD
	}

	/**
	 * Registers the native attachment.
	 */
	public static void register()
	{
	}

	/**
	 * Publishes endpoints.
	 */
	public static void begin(LivingEntity owner, ItemInteractionKind kind, long serial, long now, int duration)
	{
		if (duration <= 0)
			clear(owner);
		else
			owner.setAttached(ATTACHMENT, new ItemInteractionTimer(kind, serial, now, now + duration));
	}

	/**
	 * Removes cancelled/completed interaction feedback.
	 */
	public static void clear(LivingEntity owner)
	{
		owner.removeAttached(ATTACHMENT);
	}

	public static final Codec<ItemInteractionTimer> CODEC = IItemInteractionTimerCodec.CODEC.validate(
			value ->
					value.completesAt() > value.startedAt() ? DataResult.success(value)
					                                        : DataResult.error(() -> "Interaction completion must follow its start")
	);

	/**
	 * Timer attachment
	 */
	public static final AttachmentType<ItemInteractionTimer> ATTACHMENT = AttachmentRegistry.create(
			Galaxies.id("item_interaction_timer"),
			builder -> builder.syncWith(ItemInteractionTimer.PACKET_CODEC, AttachmentSyncPredicate.targetOnly())
	);

	/**
	 * True only during the visible interaction.
	 */
	public boolean isActive(long now)
	{
		return now >= startedAt && now < completesAt;
	}

	/**
	 * Completed fraction for the standard item bar.
	 */
	public float progress(long now, float partialTick)
	{
		return (float)Math.clamp((now - startedAt + (double)partialTick) / (completesAt - startedAt), 0, 1);
	}
}
