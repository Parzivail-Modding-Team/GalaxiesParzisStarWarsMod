package com.parzivail.util.data;

import com.parzivail.util.item.ItemAction;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;

public class MorePacketCodecs
{
	public static <E extends Enum<E>> PacketCodec<PacketByteBuf, E> enumCodec(Class<E> cls) {
		return PacketCodec.ofStatic(
				PacketByteBuf::writeEnumConstant,
				buf -> buf.readEnumConstant(cls)
		);
	}

	public static final PacketCodec<PacketByteBuf, Hand> HAND = enumCodec(Hand.class);

	public static final PacketCodec<PacketByteBuf, ItemAction> ITEM_ACTION = enumCodec(ItemAction.class);

	public static final PacketCodec<PacketByteBuf, Vec3d> VEC3D = PacketCodec.tuple(
			PacketCodecs.DOUBLE, Vec3d::getX,
			PacketCodecs.DOUBLE, Vec3d::getY,
			PacketCodecs.DOUBLE, Vec3d::getZ,
			Vec3d::new
	);
}
