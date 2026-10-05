package dev.pswg.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import dev.pswg.codecgenerator.*;
import dev.pswg.generated.codecs.*;
import dev.pswg.model.animation.HumanoidPoseSet;
import dev.pswg.model.animation.HumanoidPoseSet.*;
import dev.pswg.model.g3d.G3dTransform;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Blaster-specific selection rules over a reusable core humanoid pose set.
 *
 * @param archetype Blaster archetype described by this profile.
 * @param poses Generic numeric poses, transitions, animations and sway.
 * @param rows Ordered blaster selection rows, independently layered.
 */
@GenerateCodec(strict = true)
public record BlasterStanceProfile(
		Identifier archetype,
		@SelfCodec HumanoidPoseSet poses,
		@SelfCodec @CodecSize(min = 1) List<Row> rows
) implements IBlasterStanceProfileCodec
{
	/**
	 * Blaster composition layers.
	 */
	@GenerateEnumCodec
	public enum Layer implements ILayerCodec
	{
		/**
		 * Locomotion base.
		 */
		LOCOMOTION,

		/**
		 * Posture offset.
		 */
		POSTURE,

		/**
		 * Archetype/grip offset.
		 */
		GRIP,

		/**
		 * Weapon-state offset.
		 */
		WEAPON_STATE,

		/**
		 * Attachment-state offset.
		 */
		ATTACHMENT,

		/**
		 * Back holster, relinquishing both arms.
		 */
		HOLSTER
	}

	/**
	 * Closed blaster states, also used by attachment modifier conditions.
	 */
	@GenerateEnumCodec
	public enum WeaponState implements IWeaponStateCodec
	{
		/**
		 * Patrol carry.
		 */
		PATROL,

		/**
		 * Hip fire.
		 */
		HIP,

		/**
		 * Aimed.
		 */
		ADS,

		/**
		 * Firing.
		 */
		FIRING,

		/**
		 * Venting.
		 */
		VENTING,

		/**
		 * Reloading.
		 */
		RELOADING,

		/**
		 * Deployed.
		 */
		DEPLOYED,

		/**
		 * Folded.
		 */
		FOLDED,

		/**
		 * Holstered.
		 */
		HOLSTERED
	}

	/**
	 * Blaster selector axes; absent values match every state of that axis.
	 */
	@GenerateCodec(strict = true)
	public record Selector(
			Optional<Locomotion> locomotion,
			Optional<Posture> posture,
			Optional<WeaponState> weaponState,
			Optional<Hand> hand,
			Optional<Boolean> busy
	) implements ISelectorCodec
	{
	}

	/**
	 * One independently layered pose selection row.
	 */
	@GenerateCodec(strict = true)
	public record Row(
			Layer layer,
			@CodecName("when") @SelfCodec Selector selector,
			Identifier pose,
			@CodecDefault("dev.pswg.model.animation.HumanoidPoseSet.Handedness.MIRROR") Handedness handedness,
			@CodecDefault("0") @CodecRange(min = 0) int blendInTicks,
			@CodecDefault("0") @CodecRange(min = 0) int blendOutTicks,
			@CodecDefault("0") int priority
	) implements IRowCodec
	{
	}

	/**
	 * Validates selection references and the blaster's channel/holster relationships.
	 */
	private static DataResult<BlasterStanceProfile> validate(BlasterStanceProfile profile)
	{
		for (var row : profile.rows())
		{
			var pose = profile.poses().poseDefinitions().get(row.pose());

			if (pose == null)
				return DataResult.error(() -> "Row references missing pose " + row.pose());

			if (row.layer() == Layer.HOLSTER && !pose.armMask().isEmpty())
				return DataResult.error(() -> "Holster pose must relinquish both arms: " + row.pose());
		}

		var targets = Stream.concat(
				Stream.of("itemRoot"),
				Stream.concat(Stream.of(Bone.values()).map(Bone::getSerializedName), Stream.of(Grip.values()).map(Grip::getSerializedName))
		).toList();

		var components = Stream.of(G3dTransform.Component.values()).map(G3dTransform.Component::getSerializedName).toList();
		for (var track : profile.poses().tracks())
		{
			for (var channel : track.channels())
			{
				var parts = channel.target().split("\\.");
				if (parts.length != 2 || !targets.contains(parts[0]) || !components.contains(parts[1]))
					return DataResult.error(() -> "Unknown humanoid transform channel " + channel.target());
			}
		}

		return DataResult.success(profile);
	}

	/**
	 * Local generated codec plus blaster-specific pose-selection relationships.
	 */
	public static final Codec<BlasterStanceProfile> CODEC = IBlasterStanceProfileCodec.CODEC.validate(BlasterStanceProfile::validate);
}
