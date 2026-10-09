package dev.pswg.networking;

import dev.pswg.Galaxies;
import dev.pswg.codecgenerator.CodecRange;
import dev.pswg.codecgenerator.GenerateCodec;
import dev.pswg.generated.codecs.IRecoilImpulsePayloadCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.joml.Vector3f;

/**
 * Delivers one server-accepted angular impulse to the owning player's view.
 */
@GenerateCodec
public record RecoilImpulsePayload(
		Identifier dimension,
		@CodecRange(min = 0) int entityId,
		@CodecRange(min = 1) long eventSequence,
		Vector3f impulse,
		@CodecRange(min = 0) int recoveryTicks,
		@CodecRange(min = 1) int shotSequence,
		long sourceSerial
) implements CustomPacketPayload, IRecoilImpulsePayloadCodec
{
	/** Play-channel identifier. */
	public static final Type<RecoilImpulsePayload> TYPE = new Type<>(Galaxies.id("recoil_impulse"));

	@Override
	public Type<? extends CustomPacketPayload> type()
	{
		return TYPE;
	}
}
