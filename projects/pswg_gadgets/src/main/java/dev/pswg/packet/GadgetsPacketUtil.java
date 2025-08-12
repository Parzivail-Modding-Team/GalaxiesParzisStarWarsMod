package dev.pswg.packet;

import com.mojang.datafixers.util.Function3;
import com.mojang.datafixers.util.Function5;
import net.minecraft.network.codec.PacketCodec;

import java.util.function.Function;

public class GadgetsPacketUtil
{
	public static <B, C, T1, T2, T3, T4, T5> PacketCodec<B, C> quintuple(
			PacketCodec<? super B, T1> codec1,
			Function<C, T1> from1,
			PacketCodec<? super B, T2> codec2,
			Function<C, T2> from2,
			PacketCodec<? super B, T3> codec3,
			Function<C, T3> from3,
			PacketCodec<? super B, T4> codec4,
			Function<C, T4> from4,
			PacketCodec<? super B, T5> codec5,
			Function<C, T5> from5,
			Function5<T1, T2, T3, T4, T5, C> to
	)
	{
		return new PacketCodec<B, C>()
		{
			@Override
			public C decode(B object)
			{
				T1 object2 = codec1.decode(object);
				T2 object3 = codec2.decode(object);
				T3 object4 = codec3.decode(object);
				T4 object5 = codec4.decode(object);
				T5 object6 = codec5.decode(object);
				return to.apply(object2, object3, object4, object5, object6);
			}

			@Override
			public void encode(B object, C object2)
			{
				codec1.encode(object, (T1)from1.apply(object2));
				codec2.encode(object, (T2)from2.apply(object2));
				codec3.encode(object, (T3)from3.apply(object2));
				codec4.encode(object, (T4)from4.apply(object2));
				codec5.encode(object, (T5)from5.apply(object2));
			}
		};
	}
}
