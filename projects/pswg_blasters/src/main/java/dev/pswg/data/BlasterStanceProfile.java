package dev.pswg.data;

import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import dev.pswg.codec.GalaxiesCodecs;
import dev.pswg.codecgenerator.*;
import dev.pswg.generated.codecs.*;
import dev.pswg.model.animation.EulerTransform;
import dev.pswg.model.animation.EulerTransform.Component;
import dev.pswg.model.animation.TransformAnimation;
import net.minecraft.resources.Identifier;
import net.minecraft.util.InclusiveRange;
import net.minecraft.util.StringRepresentable;

import java.util.*;

/**
 * Stance pose data
 *
 * @param archetype       Blaster archetype described by this profile.
 * @param poseDefinitions Numeric pose definitions indexed by resource ID.
 * @param rows            Ordered pose-selection rows, independently layered.
 * @param transitions     Directed pose transitions referencing animation tracks.
 * @param tracks          Generic numeric transform animations.
 * @param locomotionSway  Optional bounded weapon/grip oscillator.
 */
@GenerateCodec(packetCodec = false, strict = true)
public record BlasterStanceProfile(
		Identifier archetype,
		@SelfCodec
		@CodecSize(min = 1)
		Map<Identifier, PoseDefinition> poseDefinitions,
		@SelfCodec
		@CodecSize(min = 1)
		List<Row> rows,
		@SelfCodec
		@CodecDefault("java.util.List.of()")
		List<Transition> transitions,
		@SelfCodec
		@CodecDefault("java.util.List.of()")
		List<TransformAnimation> tracks,
		@SelfCodec
		Optional<LocomotionSway> locomotionSway
) implements IBlasterStanceProfileCodec
{
	/**
	 * Selector layers composed independently of one another.
	 */
	public enum Layer implements StringRepresentable
	{
		/**
		 * Replaces the declared locomotion base channels.
		 */
		LOCOMOTION("locomotion"),

		/**
		 * Adds a posture offset.
		 */
		POSTURE("posture"),

		/**
		 * Adds an archetype/grip offset.
		 */
		GRIP("grip"),

		/**
		 * Adds a weapon-state offset.
		 */
		WEAPON_STATE("weapon_state"),

		/**
		 * Adds an attachment-state offset.
		 */
		ATTACHMENT("attachment"),

		/**
		 * Positions a back-held weapon and relinquishes both arms.
		 */
		HOLSTER("holster");

		public static final Codec<Layer> CODEC = StringRepresentable.fromEnum(Layer::values);

		/**
		 * Serialized layer name.
		 */
		private final String _serializedName;

		Layer(String serializedName)
		{
			_serializedName = serializedName;
		}

		/**
		 * Gets the serialized layer name.
		 */
		@Override
		public String getSerializedName()
		{
			return _serializedName;
		}
	}

	/**
	 * Locomotion selector values.
	 */
	public enum Locomotion implements StringRepresentable
	{
		/**
		 * Idle locomotion.
		 */
		IDLE("idle"),

		/**
		 * Walking locomotion.
		 */
		WALKING("walking"),

		/**
		 * Running locomotion.
		 */
		RUNNING("running"),

		/**
		 * Sprinting locomotion.
		 */
		SPRINTING("sprinting"),

		/**
		 * Falling locomotion.
		 */
		FALLING("falling"),

		/**
		 * Flying locomotion.
		 */
		FLYING("flying"),

		/**
		 * Hovering locomotion.
		 */
		HOVERING("hovering"),

		/**
		 * Climbing locomotion.
		 */
		CLIMBING("climbing");

		public static final Codec<Locomotion> CODEC = StringRepresentable.fromEnum(Locomotion::values);

		/**
		 * Serialized locomotion name.
		 */
		private final String _serializedName;

		Locomotion(String serializedName)
		{
			_serializedName = serializedName;
		}

		/**
		 * Gets the serialized locomotion name.
		 */
		@Override
		public String getSerializedName()
		{
			return _serializedName;
		}
	}

	/**
	 * Posture selector values.
	 */
	public enum Posture implements StringRepresentable
	{
		/**
		 * Upright posture.
		 */
		STANDING("standing"),

		/**
		 * Crouched posture.
		 */
		CROUCHING("crouching"),

		/**
		 * Seated posture.
		 */
		SITTING("sitting"),

		/**
		 * Prone posture.
		 */
		PRONE("prone");

		public static final Codec<Posture> CODEC = StringRepresentable.fromEnum(Posture::values);

		/**
		 * Serialized posture name.
		 */
		private final String _serializedName;

		Posture(String serializedName)
		{
			_serializedName = serializedName;
		}

		/**
		 * Gets the serialized posture name.
		 */
		@Override
		public String getSerializedName()
		{
			return _serializedName;
		}
	}

	/**
	 * Weapon-state selector values.
	 */
	public enum WeaponState implements StringRepresentable
	{
		/**
		 * Patrol-ready weapon state.
		 */
		PATROL("patrol"),

		/**
		 * Hip-fire weapon state.
		 */
		HIP("hip"),

		/**
		 * Aim-down-sights weapon state.
		 */
		ADS("ads"),

		/**
		 * Firing weapon state.
		 */
		FIRING("firing"),

		/**
		 * Venting weapon state.
		 */
		VENTING("venting"),

		/**
		 * Reloading weapon state.
		 */
		RELOADING("reloading"),

		/**
		 * Deployed attachment state.
		 */
		DEPLOYED("deployed"),

		/**
		 * Folded attachment state.
		 */
		FOLDED("folded"),

		/**
		 * Holstered weapon state.
		 */
		HOLSTERED("holstered");

		public static final Codec<WeaponState> CODEC = StringRepresentable.fromEnum(WeaponState::values);

		/**
		 * Serialized weapon-state name.
		 */
		private final String _serializedName;

		WeaponState(String serializedName)
		{
			_serializedName = serializedName;
		}

		/**
		 * Gets the serialized weapon-state name.
		 */
		@Override
		public String getSerializedName()
		{
			return _serializedName;
		}
	}

	/**
	 * Held-hand selector values.
	 */
	public enum Hand implements StringRepresentable
	{
		/**
		 * Main hand only.
		 */
		MAIN("main"),

		/**
		 * Off hand only.
		 */
		OFF("off"),

		/**
		 * Both hands.
		 */
		DUAL("dual");

		public static final Codec<Hand> CODEC = StringRepresentable.fromEnum(Hand::values);

		/**
		 * Serialized hand name.
		 */
		private final String _serializedName;

		Hand(String serializedName)
		{
			_serializedName = serializedName;
		}

		/**
		 * Gets the serialized hand name.
		 */
		@Override
		public String getSerializedName()
		{
			return _serializedName;
		}
	}

	/**
	 * Arms that a weapon pose is permitted to animate.
	 */
	public enum ArmMask implements StringRepresentable
	{
		/**
		 * Main-hand arm.
		 */
		MAIN_ARM("mainArm"),

		/**
		 * Support-hand arm.
		 */
		SUPPORT_ARM("supportArm");

		public static final Codec<ArmMask> CODEC = StringRepresentable.fromEnum(ArmMask::values);

		/**
		 * Serialized arm-mask name.
		 */
		private final String _serializedName;

		ArmMask(String serializedName)
		{
			_serializedName = serializedName;
		}

		/**
		 * Gets the serialized arm-mask name.
		 */
		@Override
		public String getSerializedName()
		{
			return _serializedName;
		}
	}

	/**
	 * Humanoid, weapon, and grip transform targets.
	 */
	public enum TransformTarget implements StringRepresentable
	{
		/**
		 * Weapon root.
		 */
		WEAPON_ROOT("weaponRoot"),

		/**
		 * Body.
		 */
		BODY("body"),

		/**
		 * Head.
		 */
		HEAD("head"),

		/**
		 * Right arm.
		 */
		RIGHT_ARM("rightArm"),

		/**
		 * Left arm.
		 */
		LEFT_ARM("leftArm"),

		/**
		 * Right leg.
		 */
		RIGHT_LEG("rightLeg"),

		/**
		 * Left leg.
		 */
		LEFT_LEG("leftLeg"),

		/**
		 * Main-hand grip.
		 */
		MAIN_HAND_GRIP("mainHandGrip"),

		/**
		 * Support-hand grip.
		 */
		SUPPORT_HAND_GRIP("supportHandGrip");

		public static final Codec<TransformTarget> CODEC = StringRepresentable.fromEnum(TransformTarget::values);

		/**
		 * Serialized target name.
		 */
		private final String _serializedName;

		TransformTarget(String serializedName)
		{
			_serializedName = serializedName;
		}

		/**
		 * Gets the serialized target name.
		 */
		@Override
		public String getSerializedName()
		{
			return _serializedName;
		}
	}

	/**
	 * Whether a row mirrors the authored side for opposite-hand use.
	 */
	public enum Handedness implements StringRepresentable
	{
		/**
		 * Mirrors the authored pose for the opposite hand.
		 */
		MIRROR("mirror"),

		/**
		 * Preserves the authored side without mirroring.
		 */
		PRESERVE("preserve");

		public static final Codec<Handedness> CODEC = StringRepresentable.fromEnum(Handedness::values);

		/**
		 * Serialized handedness name.
		 */
		private final String _serializedName;

		Handedness(String serializedName)
		{
			_serializedName = serializedName;
		}

		/**
		 * Gets the serialized handedness name.
		 */
		@Override
		public String getSerializedName()
		{
			return _serializedName;
		}
	}

	/**
	 * Map codecs and semantic validation shared by this profile's pose records.
	 */
	public static final class Codecs
	{
		/**
		 * Codec for humanoid target transforms; the record component applies the entry cap.
		 */
		public static final Codec<Map<TransformTarget, EulerTransform>> BONE_TRANSFORMS_CODEC =
				Codec.unboundedMap(TransformTarget.CODEC, EulerTransform.CODEC);

		/**
		 * Codec for hand-grip transforms; the record component applies the entry cap.
		 */
		public static final Codec<Map<TransformTarget, EulerTransform>> GRIP_TRANSFORMS_CODEC =
				Codec.unboundedMap(TransformTarget.CODEC, EulerTransform.CODEC);

		/**
		 * Humanoid targets accepted by the boneTransforms map.
		 */
		private static final Set<TransformTarget> BONE_TARGETS = Set.of(
				TransformTarget.BODY,
				TransformTarget.HEAD,
				TransformTarget.RIGHT_ARM,
				TransformTarget.LEFT_ARM,
				TransformTarget.RIGHT_LEG,
				TransformTarget.LEFT_LEG
		);

		/**
		 * Hand-grip targets accepted by the gripTransforms map.
		 */
		private static final Set<TransformTarget> GRIP_TARGETS = Set.of(
				TransformTarget.MAIN_HAND_GRIP,
				TransformTarget.SUPPORT_HAND_GRIP
		);

		/**
		 * Prevents instances of this codec-holder utility.
		 */
		private Codecs()
		{
		}
	}

	/**
	 * Inline numeric pose and the arm channels it is permitted to animate.
	 *
	 * @param weaponTransform Weapon-root transform, defaulting to identity.
	 * @param boneTransforms  Sparse humanoid bone transforms.
	 * @param gripTransforms  Sparse hand-grip transforms.
	 * @param armMask         Arms the weapon layer may animate.
	 * @param asset           Optional resource-pack asset ID.
	 */
	@GenerateCodec(packetCodec = false, strict = true)
	public record PoseDefinition(
			@SelfCodec
			@CodecDefault("dev.pswg.model.animation.EulerTransform.IDENTITY")
			EulerTransform weaponTransform,
			@UseCodec(customCodec = @CodecSource(source = BlasterStanceProfile.Codecs.class, member = "BONE_TRANSFORMS_CODEC"))
			@CodecDefault("java.util.Map.of()")
			Map<TransformTarget, EulerTransform> boneTransforms,
			@UseCodec(customCodec = @CodecSource(source = BlasterStanceProfile.Codecs.class, member = "GRIP_TRANSFORMS_CODEC"))
			@CodecDefault("java.util.Map.of()")
			Map<TransformTarget, EulerTransform> gripTransforms,
			@SelfCodec
			List<ArmMask> armMask,
			Optional<Identifier> asset
	) implements IPoseDefinitionCodec
	{
		/**
		 * Strict codec with semantic target-category validation.
		 */
		public static final Codec<PoseDefinition> CODEC = GalaxiesCodecs.validate(
				IPoseDefinitionCodec.CODEC,
				PoseDefinition::validate
		);

		/**
		 * Copies mutable collections to preserve pose immutability.
		 */
		public PoseDefinition
		{
			Objects.requireNonNull(weaponTransform);
			boneTransforms = Map.copyOf(boneTransforms);
			gripTransforms = Map.copyOf(gripTransforms);
			armMask = List.copyOf(armMask);
			Objects.requireNonNull(asset);
		}

		/**
		 * Ensures sparse transform maps use their designated target categories.
		 */
		private void validate()
		{
			Preconditions.checkArgument(armMask.size() <= 2, "armMask may contain at most two arms");
			Preconditions.checkArgument(new HashSet<>(armMask).size() == armMask.size(), "armMask entries must be unique");

			for (var entry : boneTransforms.entrySet())
			{
				Preconditions.checkArgument(Codecs.BONE_TARGETS.contains(entry.getKey()),
				                            "boneTransforms target must be a humanoid bone: " + entry.getKey().getSerializedName());
			}

			for (var entry : gripTransforms.entrySet())
			{
				Preconditions.checkArgument(Codecs.GRIP_TARGETS.contains(entry.getKey()),
				                            "gripTransforms target must be a hand grip: " + entry.getKey().getSerializedName());
			}
		}
	}

	/**
	 * Optional independent selector axes; absent axes match any state.
	 *
	 * @param locomotion  Optional locomotion selector.
	 * @param posture     Optional posture selector.
	 * @param weaponState Optional weapon-state selector.
	 * @param hand        Optional held-hand selector.
	 * @param busy        Optional busy-state selector.
	 */
	@GenerateCodec(packetCodec = false, strict = true)
	public record Selector(
			@SelfCodec Optional<Locomotion> locomotion,
			@SelfCodec Optional<Posture> posture,
			@SelfCodec Optional<WeaponState> weaponState,
			@SelfCodec Optional<Hand> hand,
			Optional<Boolean> busy
	) implements ISelectorCodec
	{
	}

	/**
	 * Ordered candidate row selected independently within its layer.
	 *
	 * @param layer         Independent composition layer.
	 * @param selector      Selector axes; omitted axes are wildcards and serialize as {@code when}.
	 * @param pose          Referenced pose-definition ID.
	 * @param handedness    How opposite-hand poses are treated.
	 * @param blendInTicks  Blend-in duration, defaulting to zero.
	 * @param blendOutTicks Blend-out duration, defaulting to zero.
	 * @param priority      Priority within this layer, defaulting to zero.
	 */
	@GenerateCodec(packetCodec = false, strict = true)
	public record Row(
			@SelfCodec Layer layer,
			@CodecName("when") @SelfCodec Selector selector,
			Identifier pose,
			@SelfCodec
			@CodecDefault("dev.pswg.data.BlasterStanceProfile.Handedness.MIRROR")
			Handedness handedness,
			@CodecDefault("0")
			int blendInTicks,
			@CodecDefault("0")
			int blendOutTicks,
			@CodecDefault("0")
			int priority
	) implements IRowCodec
	{
	}

	/**
	 * Directed pose transition driven by a track declared in this profile.
	 *
	 * @param from  Source pose ID.
	 * @param to    Destination pose ID.
	 * @param track Transition track ID.
	 */
	@GenerateCodec(packetCodec = false, strict = true)
	public record Transition(Identifier from, Identifier to, Identifier track) implements ITransitionCodec
	{
	}

	/**
	 * Typed numeric oscillator applied to weapon-root or hand-grip sway targets.
	 *
	 * @param target    Weapon-root or hand-grip transform target.
	 * @param amplitude Oscillation amplitude in degrees.
	 * @param frequency Oscillation frequency in cycles per second.
	 */
	@GenerateCodec(packetCodec = false, strict = true)
	public record LocomotionSway(
			@SelfCodec TransformTarget target,
			@CodecRange(min = 0) float amplitude,
			@CodecRange(min = 0) float frequency
	) implements ILocomotionSwayCodec
	{
	}

	/**
	 * Strict generated codec wrapped with profile reference validation.
	 */
	public static final Codec<BlasterStanceProfile> CODEC = GalaxiesCodecs.validate(
			IBlasterStanceProfileCodec.CODEC,
			BlasterStanceProfile::validateProfile
	);

	/**
	 * Weapon-root and hand-grip targets accepted by locomotion sway.
	 */
	private static final Set<TransformTarget> SWAY_TARGETS = Set.of(
			TransformTarget.WEAPON_ROOT,
			TransformTarget.MAIN_HAND_GRIP,
			TransformTarget.SUPPORT_HAND_GRIP
	);

	/**
	 * Resolves profile references and validates contract-specific numeric limits.
	 */
	private void validateProfile()
	{
		for (var pose : poseDefinitions.values())
		{
			pose.validate();
		}

		var trackIds = new HashSet<Identifier>();
		for (var track : tracks)
		{
			Preconditions.checkArgument(trackIds.add(track.id()), "Duplicate track ID " + track.id());
			validateTrack(track);
		}

		for (var row : rows)
		{
			var pose = poseDefinitions.get(row.pose());
			Preconditions.checkArgument(pose != null, "Row references missing pose " + row.pose());
			if (row.layer() == Layer.HOLSTER)
			{
				Preconditions.checkArgument(pose.armMask().isEmpty(),
				                            "Holster row pose must have an empty armMask: " + row.pose());
			}
		}

		for (var transition : transitions)
		{
			Preconditions.checkArgument(poseDefinitions.containsKey(transition.from()),
			                            "Transition references missing source pose " + transition.from());
			Preconditions.checkArgument(poseDefinitions.containsKey(transition.to()),
			                            "Transition references missing destination pose " + transition.to());
			Preconditions.checkArgument(trackIds.contains(transition.track()),
			                            "Transition references missing track " + transition.track());
		}

		locomotionSway.ifPresent(
				sway ->
						Preconditions.checkArgument(SWAY_TARGETS.contains(sway.target()),
						                            "locomotionSway target must be weaponRoot, mainHandGrip, or supportHandGrip")
		);
	}

	/**
	 * Validates the blaster-specific duration, transform target, and axis rules on a generic track.
	 */
	private static void validateTrack(TransformAnimation track)
	{
		Preconditions.checkArgument(track.durationTicks() >= 1 && track.durationTicks() <= 1200,
		                            "Blaster stance track durationTicks must be in [1, 1200]");
		for (var channel : track.channels())
		{
			var separator = channel.target().lastIndexOf('.');
			Preconditions.checkArgument(separator > 0 && separator < channel.target().length() - 1,
			                            "Blaster stance target must be <transform>.<component>: " + channel.target());
			transformTarget(channel.target().substring(0, separator));
		}
	}

	/**
	 * Resolves one serialized transform target without accepting arbitrary target names.
	 */
	private static TransformTarget transformTarget(String serializedName)
	{
		for (var target : TransformTarget.values())
		{
			if (target.getSerializedName().equals(serializedName))
			{
				return target;
			}
		}
		throw new IllegalArgumentException("Unknown blaster transform target " + serializedName);
	}

	/**
	 * Resolves one serialized transform component.
	 */
	private static Component transformComponent(String serializedName)
	{
		for (var component : Component.values())
		{
			if (component.getSerializedName().equals(serializedName))
			{
				return component;
			}
		}
		throw new IllegalArgumentException("Unknown transform component " + serializedName);
	}
}
