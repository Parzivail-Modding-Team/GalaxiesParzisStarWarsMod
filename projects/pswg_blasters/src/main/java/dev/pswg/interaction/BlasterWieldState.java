package dev.pswg.interaction;

import dev.pswg.Blasters;
import dev.pswg.codecgenerator.GenerateCodec;
import dev.pswg.codecgenerator.SelfCodec;
import dev.pswg.data.BlasterStanceProfile;
import dev.pswg.generated.codecs.IBlasterWieldStateCodec;
import dev.pswg.generated.codecs.IBlasterWieldWeaponCodec;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.world.InteractionHand;

import java.util.Optional;

/**
 * Unsaved server stance/readiness
 */
@GenerateCodec
public record BlasterWieldState(
		boolean busy,
		@SelfCodec Optional<BlasterWieldWeapon> main,
		@SelfCodec Optional<BlasterWieldWeapon> off
) implements IBlasterWieldStateCodec
{
	@GenerateCodec
	public record BlasterWieldWeapon(
			long serial,
			int sourceSlot,
			BlasterStanceProfile.WeaponState stance,
			boolean holstered,
			boolean patrol,
			long readyAt
	) implements IBlasterWieldWeaponCodec
	{
	}

	/**
	 * Empty state for missing or not-yet-synchronized player data.
	 */
	public static final BlasterWieldState EMPTY = new BlasterWieldState(false, Optional.empty(), Optional.empty());

	public static final AttachmentType<BlasterWieldState> ATTACHMENT = AttachmentRegistry.create(
			Blasters.id("wield_state"),
			builder -> builder.syncWith(PACKET_CODEC, AttachmentSyncPredicate.all())
	);

	public static void register()
	{
	}

	public Optional<BlasterWieldWeapon> weapon(InteractionHand hand)
	{
		return hand == InteractionHand.MAIN_HAND ? main : off;
	}
}
