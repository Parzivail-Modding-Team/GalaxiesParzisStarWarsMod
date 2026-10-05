package dev.pswg.model.animation;

import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import dev.pswg.codec.GalaxiesCodecs;
import dev.pswg.codecgenerator.*;
import dev.pswg.generated.codecs.IChannelCodec;
import dev.pswg.generated.codecs.ITransformAnimationCodec;
import dev.pswg.generated.codecs.IVectorKeyframeCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.util.*;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Generic bounded vector animation data backed by Minecraft keyframes and easing.
 * It describes data and exposes a native sampler adapter; it does not implement
 * any renderer or pose-composition system.
 *
 * @param id            Resource identifier for this animation.
 * @param durationTicks Animation duration in ticks.
 * @param loop          Playback loop mode.
 * @param easing        Native keyframe easing selected from the contract aliases.
 * @param channels      Independently targeted vector channels.
 */
@GenerateCodec(packetCodec = false, strict = true)
public record TransformAnimation(
		Identifier id,
		@CodecRange(min = 1) int durationTicks,
		@SelfCodec
		@CodecDefault("dev.pswg.model.animation.TransformAnimation.LoopMode.ONCE")
		LoopMode loop,
		@UseCodec(customCodec = @CodecSource(source = TransformAnimation.Codecs.class, member = "EASING_CODEC"))
		@CodecDefault("net.minecraft.util.EasingType.LINEAR")
		EasingType easing,
		@SelfCodec
		@CodecSize(min = 1)
		List<Channel> channels
) implements ITransformAnimationCodec
{
	/**
	 * Generic clip loop modes serialized with the profile contract's names.
	 */
	public enum LoopMode implements StringRepresentable
	{
		/**
		 * Plays through once and then holds the last keyframe.
		 */
		ONCE("once"),

		/**
		 * Repeats from the first keyframe after the duration.
		 */
		LOOP("loop"),

		/**
		 * Reverses direction on alternating duration intervals.
		 */
		PING_PONG("ping_pong");

		public static final Codec<LoopMode> CODEC = StringRepresentable.fromEnum(LoopMode::values);

		/**
		 * Serialized loop-mode name.
		 */
		private final String _serializedName;

		LoopMode(String serializedName)
		{
			_serializedName = serializedName;
		}

		/**
		 * Gets the serialized loop-mode name.
		 */
		@Override
		public String getSerializedName()
		{
			return _serializedName;
		}
	}

	/**
	 * One generic target and its ordered native vector keyframes.
	 *
	 * @param target    Consumer-defined channel target string.
	 * @param keyframes Ordered vector keyframes using Minecraft's native record.
	 */
	@GenerateCodec(packetCodec = false, strict = true)
	public record Channel(
			@UseCodec(customCodec = @CodecSource(source = TransformAnimation.Codecs.class, member = "TARGET_CODEC"))
			String target,
			@UseCodec(customCodec = @CodecSource(source = TransformAnimation.Codecs.class, member = "KEYFRAMES_CODEC"))
			@CodecSize(min = 2)
			List<Keyframe<Vector3fc>> keyframes
	) implements IChannelCodec
	{
		public static final Codec<Channel> CODEC = GalaxiesCodecs.validate(IChannelCodec.CODEC, Channel::validate);

		public Channel
		{
			Objects.requireNonNull(target);
			keyframes = keyframes.stream()
			                     .map(keyframe -> new Keyframe<Vector3fc>(keyframe.ticks(), new Vector3f(keyframe.value())))
			                     .toList();
		}

		/**
		 * Validates generic target-string and keyframe ordering constraints.
		 */
		private void validate()
		{
			Codecs.validateTarget(target);
			Codecs.validateKeyframeOrder(keyframes);
		}
	}

	/**
	 * Wire record preserving the contract's {@code timeTicks} key for native keyframes.
	 *
	 * @param ticks Keyframe tick, serialized as {@code timeTicks}.
	 * @param value Finite vector value.
	 */
	@GenerateCodec(packetCodec = false, strict = true)
	public record VectorKeyframe(
			@CodecName("timeTicks")
			@CodecRange(min = 0)
			int ticks,
			@UseCodec(customCodec = @CodecSource(source = GalaxiesCodecs.class, member = "FINITE_VECTOR3F"))
			Vector3fc value
	) implements IVectorKeyframeCodec
	{
		public VectorKeyframe
		{
			value = new Vector3f(Objects.requireNonNull(value));
		}

		/**
		 * Converts this contract-shaped wire value to Minecraft's keyframe record.
		 */
		public Keyframe<Vector3fc> toNative()
		{
			return new Keyframe<>(ticks, new Vector3f(value));
		}

		/**
		 * Adapts a Minecraft keyframe to the contract-shaped wire record.
		 */
		public static VectorKeyframe fromNative(Keyframe<Vector3fc> keyframe)
		{
			return new VectorKeyframe(keyframe.ticks(), keyframe.value());
		}
	}

	/**
	 * Codec sources shared by the generated animation records.
	 */
	public static final class Codecs
	{

		/**
		 * Bounded nonempty channel-target string codec; target interpretation is consumer-specific.
		 */
		public static final Codec<String> TARGET_CODEC = Codec.sizeLimitedString(256).validate(
				target ->
						!target.isBlank()
						? DataResult.success(target)
						: DataResult.error(() -> "Animation targets must be nonempty")
		);

		/**
		 * Core smoothstep easing function, independent of blaster code.
		 */
		public static final EasingType SMOOTHSTEP = value -> (float)Mth.smoothstep(value);

		/**
		 * Closed contract easing aliases mapped to Minecraft's easing functions.
		 */
		public static final Codec<EasingType> EASING_CODEC = Codec.STRING.comapFlatMap(
				Codecs::easingForName,
				Codecs::nameForEasing
		);

		/**
		 * Codec adapting contract-shaped wire frames to Minecraft keyframe records.
		 */
		public static final Codec<Keyframe<Vector3fc>> KEYFRAME_CODEC = VectorKeyframe.CODEC.xmap(
				VectorKeyframe::toNative,
				VectorKeyframe::fromNative
		);
		/**
		 * Codec for lists of native keyframes in the contract's object shape.
		 */
		public static final Codec<List<Keyframe<Vector3fc>>> KEYFRAMES_CODEC = KEYFRAME_CODEC.listOf();

		/**
		 * Maps one supported contract alias to the native easing function.
		 */
		private static DataResult<EasingType> easingForName(String name)
		{
			return switch (name)
			{
				case "linear" -> DataResult.success(EasingType.LINEAR);
				case "smoothstep" -> DataResult.success(SMOOTHSTEP);
				case "ease_in" -> DataResult.success(EasingType.IN_QUAD);
				case "ease_out" -> DataResult.success(EasingType.OUT_QUAD);
				default -> DataResult.error(() -> "Unknown transform easing '" + name + "'");
			};
		}

		/**
		 * Gets the contract alias for one of the supported native easing instances.
		 */
		private static String nameForEasing(EasingType easing)
		{
			if (easing == EasingType.LINEAR)
			{
				return "linear";
			}

			if (easing == SMOOTHSTEP)
			{
				return "smoothstep";
			}

			if (easing == EasingType.IN_QUAD)
			{
				return "ease_in";
			}

			if (easing == EasingType.OUT_QUAD)
			{
				return "ease_out";
			}

			throw new IllegalArgumentException("Unsupported transform easing " + easing);
		}

		/**
		 * Validates a generic target string before channel data is used.
		 */
		private static void validateTarget(String target)
		{
			Preconditions.checkArgument(!target.isBlank(), "Animation targets must be nonempty");
		}

		/**
		 * Validates generic channel keyframe limits, finite values, and strict time order.
		 */
		private static void validateKeyframeOrder(List<Keyframe<Vector3fc>> keyframes)
		{
			Preconditions.checkArgument(keyframes.size() >= 2 && keyframes.size() <= 32,
			                            "Animation channel keyframes must contain 2..32 entries");
			var previous = -1;
			for (var keyframe : keyframes)
			{
				Preconditions.checkArgument(keyframe.ticks() > previous,
				                            "Keyframe times must be unique and strictly increasing");
				previous = keyframe.ticks();
				Preconditions.checkArgument(Float.isFinite(keyframe.value().x())
				                            && Float.isFinite(keyframe.value().y())
				                            && Float.isFinite(keyframe.value().z()),
				                            "Keyframe values must have finite axes");
			}
		}

		/**
		 * Prevents instances of this codec-holder utility.
		 */
		private Codecs()
		{
		}
	}

	/**
	 * Strict codec wrapped with semantic checks for duration, endpoints, and channel uniqueness.
	 */
	public static final Codec<TransformAnimation> CODEC = GalaxiesCodecs.validate(
			ITransformAnimationCodec.CODEC,
			TransformAnimation::validate
	);

	/**
	 * Copies channel inputs to preserve animation immutability.
	 */
	public TransformAnimation
	{
		Objects.requireNonNull(id);
		Objects.requireNonNull(loop);
		Objects.requireNonNull(easing);
		channels = List.copyOf(channels);
	}

	/**
	 * Validates generic duration, uniqueness, and each channel's endpoint contract.
	 */
	private void validate()
	{
		Preconditions.checkArgument(durationTicks >= 1 && durationTicks <= 12000,
		                            "Animation durationTicks must be in [1, 12000]");
		Preconditions.checkArgument(!channels.isEmpty() && channels.size() <= 128,
		                            "Animation channels must contain 1..128 entries");
		var targets = new HashSet<String>();
		for (var channel : channels)
		{
			channel.validate();
			Preconditions.checkArgument(targets.add(channel.target()), "Duplicate animation target " + channel.target());
			Preconditions.checkArgument(channel.keyframes().getFirst().ticks() == 0,
			                            "Animation channel " + channel.target() + " must start at tick 0");
			Preconditions.checkArgument(channel.keyframes().getLast().ticks() == durationTicks,
			                            "Animation channel " + channel.target() + " must end at durationTicks");
			for (var keyframe : channel.keyframes())
			{
				Preconditions.checkArgument(keyframe.ticks() <= durationTicks,
				                            "Keyframe exceeds durationTicks in channel " + channel.target());
				Preconditions.checkArgument(Float.isFinite(keyframe.value().x())
				                            && Float.isFinite(keyframe.value().y())
				                            && Float.isFinite(keyframe.value().z()),
				                            "Keyframe values must have finite axes");
			}
		}
	}

	/**
	 * Maps elapsed ticks to the bounded clip interval for this loop mode. Pass the
	 * returned tick to a sampler produced by {@link #bakeSampler(Channel)}.
	 *
	 * @param elapsedTicks Elapsed clip ticks, including negative values.
	 *
	 * @return A sample tick in {@code 0..durationTicks}.
	 */
	public long sampleTicks(long elapsedTicks)
	{
		validate();
		return switch (loop)
		{
			case ONCE -> Math.max(0L, Math.min((long)durationTicks, elapsedTicks));
			case LOOP -> Math.floorMod(elapsedTicks, (long)durationTicks);
			case PING_PONG ->
			{
				var phase = Math.floorMod(elapsedTicks, durationTicks * 2L);
				yield phase <= durationTicks ? phase : durationTicks * 2L - phase;
			}
		};
	}

	/**
	 * Bakes one channel through Minecraft's native keyframe sampler and linear vector lerp.
	 * Loop behavior is applied by {@link #sampleTicks(long)} before sampling.
	 *
	 * @param channel Channel belonging to this animation.
	 *
	 * @return Minecraft's native sampler for the channel.
	 */
	public KeyframeTrackSampler<Vector3fc> bakeSampler(Channel channel)
	{
		validate();
		Preconditions.checkArgument(channels.contains(channel), "Channel does not belong to animation " + id);
		return new KeyframeTrack<>(channel.keyframes(), easing).bakeSampler(
				Optional.empty(),
				(alpha, from, to) -> new Vector3f(from).lerp(to, alpha)
		);
	}
}
