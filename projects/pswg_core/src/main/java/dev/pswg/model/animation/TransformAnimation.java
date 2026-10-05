package dev.pswg.model.animation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import dev.pswg.codec.GalaxiesCodecs;
import dev.pswg.codecgenerator.*;
import dev.pswg.generated.codecs.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.util.*;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.List;
import java.util.Optional;

/**
 * Reusable numeric transform animation.
 *
 * @param id            Animation identifier.
 * @param durationTicks Duration in ticks.
 * @param loop          Playback loop mode.
 * @param easing        Closed easing choice.
 * @param channels      Independently targeted ordered vector channels.
 */
@GenerateCodec(strict = true)
public record TransformAnimation(
		Identifier id,
		@CodecRange(min = 1, max = 12000) int durationTicks,
		@CodecDefault("dev.pswg.model.animation.TransformAnimation.LoopMode.ONCE") LoopMode loop,
		@CodecDefault("dev.pswg.model.animation.TransformAnimation.Easing.LINEAR") Easing easing,
		@SelfCodec @CodecSize(min = 1, max = 128) @CodecUnique(key = "target") List<Channel> channels
) implements ITransformAnimationCodec
{
	/**
	 * Supported playback policies.
	 */
	@GenerateEnumCodec
	public enum LoopMode implements ILoopModeCodec
	{
		/**
		 * Play once, then hold the final frame.
		 */
		ONCE,

		/**
		 * Repeat from the start.
		 */
		LOOP,

		/**
		 * Alternate forwards and backwards.
		 */
		PING_PONG
	}

	/**
	 * Fixed author-facing easing choices with native sampler adapters.
	 */
	@GenerateEnumCodec
	public enum Easing implements IEasingCodec
	{
		/**
		 * Constant interpolation speed.
		 */
		LINEAR,

		/**
		 * Smooth acceleration and deceleration.
		 */
		SMOOTHSTEP,

		/**
		 * Quadratic acceleration.
		 */
		EASE_IN,

		/**
		 * Quadratic deceleration.
		 */
		EASE_OUT;

		/**
		 * Returns the function expected by Minecraft's keyframe track.
		 */
		public EasingType nativeEasing()
		{
			return switch (this)
			{
				case LINEAR -> EasingType.LINEAR;
				case SMOOTHSTEP -> value -> (float)Mth.smoothstep(value);
				case EASE_IN -> EasingType.IN_QUAD;
				case EASE_OUT -> EasingType.OUT_QUAD;
			};
		}
	}

	/**
	 * A consumer-defined transform/component path and its strictly ordered frames.
	 */
	@GenerateCodec(strict = true)
	public record Channel(
			@UseCodec(codec = GenStandardCodec.NON_EMPTY_STRING) String target,
			@UseCodec(
					customCodec = @CodecSource(source = TransformAnimation.class, member = "KEYFRAMES_CODEC"),
					customPacket = @CodecSource(source = TransformAnimation.class, member = "KEYFRAMES_PACKET_CODEC")
			)
			@CodecSize(min = 2) List<Keyframe<Vector3fc>> keyframes
	) implements IChannelCodec
	{
	}

	/**
	 * Contract-shaped adapter for a native vector keyframe.
	 */
	@GenerateCodec(strict = true)
	public record VectorKeyframe(
			@CodecName("timeTicks") @CodecRange(min = 0) int ticks,
			@UseCodec(customCodec = @CodecSource(source = GalaxiesCodecs.class, member = "FINITE_VECTOR3F")) Vector3fc value
	) implements IVectorKeyframeCodec
	{
		/**
		 * Adapts a native frame without copying or mutating its read-only vector.
		 */
		public static VectorKeyframe fromNative(Keyframe<Vector3fc> frame)
		{
			return new VectorKeyframe(frame.ticks(), frame.value());
		}

		/**
		 * Creates the native sampler frame.
		 */
		public Keyframe<Vector3fc> toNative()
		{
			return new Keyframe<>(ticks, value);
		}
	}

	/**
	 * Validates only the ordering relationship between already validated frames.
	 */
	private static DataResult<List<Keyframe<Vector3fc>>> orderedFrames(List<Keyframe<Vector3fc>> frames)
	{
		for (var index = 1; index < frames.size(); index++)
		{
			if (frames.get(index).ticks() <= frames.get(index - 1).ticks())
				return DataResult.error(() -> "Keyframe times must be strictly increasing");
		}

		return DataResult.success(frames);
	}

	/**
	 * Validates clip endpoints after its field codecs have decoded channel contents.
	 */
	private static DataResult<TransformAnimation> validate(TransformAnimation animation)
	{
		for (var channel : animation.channels())
		{
			if (channel.keyframes().getFirst().ticks() != 0 || channel.keyframes().getLast().ticks() != animation.durationTicks())
				return DataResult.error(() -> "Channel " + channel.target() + " must span tick 0 through durationTicks");
		}
		return DataResult.success(animation);
	}

	public static final Codec<Keyframe<Vector3fc>> KEYFRAME_CODEC = VectorKeyframe.CODEC.xmap(VectorKeyframe::toNative, VectorKeyframe::fromNative);

	public static final Codec<List<Keyframe<Vector3fc>>> KEYFRAMES_CODEC = KEYFRAME_CODEC.listOf(1, Integer.MAX_VALUE).validate(TransformAnimation::orderedFrames);

	public static final StreamCodec<RegistryFriendlyByteBuf, Keyframe<Vector3fc>> KEYFRAME_PACKET_CODEC = VectorKeyframe.PACKET_CODEC
			.map(VectorKeyframe::toNative, VectorKeyframe::fromNative);

	public static final StreamCodec<RegistryFriendlyByteBuf, List<Keyframe<Vector3fc>>> KEYFRAMES_PACKET_CODEC = KEYFRAME_PACKET_CODEC
			.apply(net.minecraft.network.codec.ByteBufCodecs.list(32))
			.map(List::copyOf, java.util.function.Function.identity());

	public static final Codec<TransformAnimation> CODEC = ITransformAnimationCodec.CODEC.validate(TransformAnimation::validate);

	/**
	 * Maps elapsed ticks into this clip's playback interval.
	 */
	public long sampleTicks(long elapsedTicks)
	{
		return switch (loop)
		{
			case ONCE -> Math.clamp(elapsedTicks, 0L, durationTicks);
			case LOOP -> Math.floorMod(elapsedTicks, durationTicks);
			case PING_PONG ->
			{
				var phase = Math.floorMod(elapsedTicks, durationTicks * 2L);
				yield phase <= durationTicks ? phase : durationTicks * 2L - phase;
			}
		};
	}

	/**
	 * Bakes a channel using Minecraft's native vector interpolation.
	 */
	public KeyframeTrackSampler<Vector3fc> bakeSampler(Channel channel)
	{
		return new KeyframeTrack<>(channel.keyframes(), easing.nativeEasing()).bakeSampler(
				Optional.empty(),
				(alpha, from, to) -> new Vector3f(from).lerp(to, alpha)
		);
	}
}
