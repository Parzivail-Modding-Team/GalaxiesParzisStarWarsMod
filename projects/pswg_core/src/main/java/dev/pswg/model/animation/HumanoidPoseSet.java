package dev.pswg.model.animation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import dev.pswg.codecgenerator.*;
import dev.pswg.generated.codecs.*;
import dev.pswg.model.g3d.G3dTransform;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Reusable humanoid/item poses and transition clips, independent of an item's gameplay selectors.
 * Pose translations are in blocks; G3D source compilation owns its separate sixteenth-block conversion.
 *
 * @param poseDefinitions Numeric poses keyed by resource ID.
 * @param transitions     Directed pose changes referencing declared clips.
 * @param tracks          Reusable transform clips.
 * @param locomotionSway  Optional item/grip oscillator.
 */
@GenerateCodec(strict = true)
public record HumanoidPoseSet(
		@SelfCodec @CodecSize(min = 1) Map<Identifier, HumanoidPose> poseDefinitions,
		@SelfCodec @CodecDefault("java.util.List.of()") List<Transition> transitions,
		@SelfCodec @CodecDefault("java.util.List.of()") @CodecUnique(key = "id") List<TransformAnimation> tracks,
		@SelfCodec Optional<LocomotionSway> locomotionSway
) implements IHumanoidPoseSetCodec
{
	/**
	 * Supported humanoid bone names.
	 */
	@GenerateEnumCodec
	public enum Bone implements IBoneCodec
	{
		/**
		 * Torso.
		 */
		BODY,

		/**
		 * Head.
		 */
		HEAD,

		/**
		 * Right arm.
		 */
		@CodecName("rightArm") RIGHT_ARM,

		/**
		 * Left arm.
		 */
		@CodecName("leftArm") LEFT_ARM,

		/**
		 * Right leg.
		 */
		@CodecName("rightLeg") RIGHT_LEG,

		/**
		 * Left leg.
		 */
		@CodecName("leftLeg") LEFT_LEG
	}

	/**
	 * Supported hand-grip targets.
	 */
	@GenerateEnumCodec
	public enum Grip implements IGripCodec
	{
		/**
		 * Main-hand grip.
		 */
		@CodecName("mainHandGrip") MAIN_HAND_GRIP,

		/**
		 * Support-hand grip.
		 */
		@CodecName("supportHandGrip") SUPPORT_HAND_GRIP
	}

	/**
	 * Arm channels a held item may animate.
	 */
	@GenerateEnumCodec
	public enum ArmMask implements IArmMaskCodec
	{
		/**
		 * Main-hand arm.
		 */
		@CodecName("mainArm") MAIN_ARM,

		/**
		 * Support-hand arm.
		 */
		@CodecName("supportArm") SUPPORT_ARM
	}

	/**
	 * Item/grip oscillator targets.
	 */
	@GenerateEnumCodec
	public enum SwayTarget implements ISwayTargetCodec
	{
		/**
		 * Held item's root.
		 */
		@CodecName("itemRoot") ITEM_ROOT,

		/**
		 * Main-hand grip.
		 */
		@CodecName("mainHandGrip") MAIN_HAND_GRIP,

		/**
		 * Support-hand grip.
		 */
		@CodecName("supportHandGrip") SUPPORT_HAND_GRIP
	}

	/**
	 * Locomotion modes.
	 */
	@GenerateEnumCodec
	public enum Locomotion implements ILocomotionCodec
	{
		/**
		 * Idle.
		 */
		IDLE,

		/**
		 * Walking.
		 */
		WALKING,

		/**
		 * Running.
		 */
		RUNNING,

		/**
		 * Sprinting.
		 */
		SPRINTING,

		/**
		 * Falling.
		 */
		FALLING,

		/**
		 * Flying.
		 */
		FLYING,

		/**
		 * Hovering.
		 */
		HOVERING,

		/**
		 * Climbing.
		 */
		CLIMBING
	}

	/**
	 * Posture categories.
	 */
	@GenerateEnumCodec
	public enum Posture implements IPostureCodec
	{
		/**
		 * Upright.
		 */
		STANDING,

		/**
		 * Crouched.
		 */
		CROUCHING,

		/**
		 * Seated.
		 */
		SITTING,

		/**
		 * Prone.
		 */
		PRONE
	}

	/**
	 * Supported hand contexts.
	 */
	@GenerateEnumCodec
	public enum Hand implements IHandCodec
	{
		/**
		 * Main hand.
		 */
		MAIN,

		/**
		 * Off hand.
		 */
		OFF,

		/**
		 * Both hands.
		 */
		DUAL
	}

	/**
	 * Opposite-hand interpretation of a pose.
	 */
	@GenerateEnumCodec
	public enum Handedness implements IHandednessCodec
	{
		/**
		 * Mirror for the opposite hand.
		 */
		MIRROR,

		/**
		 * Preserve the authored side.
		 */
		PRESERVE
	}

	/**
	 * Humanoid and held-item pose.
	 */
	@GenerateCodec(strict = true)
	public record HumanoidPose(
			@SelfCodec @CodecDefault("dev.pswg.model.g3d.G3dTransform.IDENTITY") G3dTransform itemTransform,
			@SelfCodec @CodecDefault("java.util.Map.of()") Map<Bone, G3dTransform> boneTransforms,
			@SelfCodec @CodecDefault("java.util.Map.of()") Map<Grip, G3dTransform> gripTransforms,
			@CodecUnique List<ArmMask> armMask,
			Optional<Identifier> asset
	) implements IHumanoidPoseCodec
	{
	}

	/**
	 * Directed pose transition using one declared animation.
	 */
	@GenerateCodec(strict = true)
	public record Transition(Identifier from, Identifier to, Identifier track) implements ITransitionCodec
	{
	}

	/**
	 * Item/grip oscillator in degrees and cycles per second.
	 */
	@GenerateCodec(strict = true)
	public record LocomotionSway(
			SwayTarget target,
			@CodecRange(min = 0) float amplitude,
			@CodecRange(min = 0) float frequency
	) implements ILocomotionSwayCodec
	{
	}

	/**
	 * Validates the cross-references.
	 */
	private static DataResult<HumanoidPoseSet> validate(HumanoidPoseSet poses)
	{
		var trackIds = poses.tracks().stream().map(TransformAnimation::id).toList();
		for (var transition : poses.transitions())
		{
			if (!poses.poseDefinitions().containsKey(transition.from()) || !poses.poseDefinitions().containsKey(transition.to()))
				return DataResult.error(() -> "Transition references missing source/destination pose " + transition);
			if (!trackIds.contains(transition.track()))
				return DataResult.error(() -> "Transition references missing track " + transition.track());
		}
		return DataResult.success(poses);
	}

	public static final Codec<HumanoidPoseSet> CODEC = IHumanoidPoseSetCodec.CODEC.validate(HumanoidPoseSet::validate);
}
