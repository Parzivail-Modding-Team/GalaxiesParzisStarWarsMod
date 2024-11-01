package dev.pswg.codecgenerator;

/**
 * A list of all available packet codecs
 */
public enum GenPacketCodec
{
	/**
	 * Automatically determine which packet codec best represents the given type
	 */
	AUTOMATIC,

	/**
	 * An available codec for the {@link Boolean} type.
	 * Delegates to {@link net.minecraft.network.codec.PacketCodecs#BOOL}
	 */
	BOOL,

	/**
	 * An available codec for the {@link Byte} type.
	 * Delegates to {@link net.minecraft.network.codec.PacketCodecs#BYTE}
	 */
	BYTE,

	/**
	 * An available codec for the {@link Short} type.
	 * Delegates to {@link net.minecraft.network.codec.PacketCodecs#SHORT}
	 */
	SHORT,

	/**
	 * An available codec for the {@link Integer} type.
	 * Delegates to {@link net.minecraft.network.codec.PacketCodecs#VAR_INT}
	 */
	VAR_INT,

	/**
	 * An available codec for the {@link Integer} type.
	 * Delegates to {@link net.minecraft.network.codec.PacketCodecs#UNSIGNED_SHORT}
	 */
	UNSIGNED_SHORT,

	/**
	 * An available codec for the {@link Integer} type.
	 * Delegates to {@link net.minecraft.network.codec.PacketCodecs#INTEGER}
	 */
	INTEGER,

	/**
	 * An available codec for the {@link Integer} type.
	 * Delegates to {@link net.minecraft.network.codec.PacketCodecs#SYNC_ID}
	 */
	SYNC_ID,

	/**
	 * An available codec for the {@link java.util.OptionalInt} type.
	 * Delegates to {@link net.minecraft.network.codec.PacketCodecs#OPTIONAL_INT}
	 */
	OPTIONAL_INT,

	/**
	 * An available codec for the {@link Long} type.
	 * Delegates to {@link net.minecraft.network.codec.PacketCodecs#VAR_LONG}
	 */
	VAR_LONG,

	/**
	 * An available codec for the {@link Long} type.
	 * Delegates to {@link net.minecraft.network.codec.PacketCodecs#LONG}
	 */
	LONG,

	/**
	 * An available codec for the {@link Float} type.
	 * Delegates to {@link net.minecraft.network.codec.PacketCodecs#FLOAT}
	 */
	FLOAT,

	/**
	 * An available codec for the {@link Float} type.
	 * Delegates to {@link net.minecraft.network.codec.PacketCodecs#DEGREES}
	 */
	DEGREES,

	/**
	 * An available codec for the {@link Double} type.
	 * Delegates to {@link net.minecraft.network.codec.PacketCodecs#DOUBLE}
	 */
	DOUBLE,

	/**
	 * An available codec for the {@link Byte[]} type.
	 * Delegates to {@link net.minecraft.network.codec.PacketCodecs#BYTE_ARRAY}
	 */
	BYTE_ARRAY,

	/**
	 * An available codec for the {@link String} type.
	 * Delegates to {@link net.minecraft.network.codec.PacketCodecs#STRING}
	 */
	STRING,

	/**
	 * An available codec for the {@link net.minecraft.nbt.NbtElement} type.
	 * Delegates to {@link net.minecraft.network.codec.PacketCodecs#NBT_ELEMENT}
	 */
	NBT_ELEMENT,

	/**
	 * An available codec for the {@link net.minecraft.nbt.NbtElement} type.
	 * Delegates to {@link net.minecraft.network.codec.PacketCodecs#UNLIMITED_NBT_ELEMENT}
	 */
	UNLIMITED_NBT_ELEMENT,

	/**
	 * An available codec for the {@link net.minecraft.nbt.NbtCompound} type.
	 * Delegates to {@link net.minecraft.network.codec.PacketCodecs#NBT_COMPOUND}
	 */
	NBT_COMPOUND,

	/**
	 * An available codec for the {@link net.minecraft.nbt.NbtCompound} type.
	 * Delegates to {@link net.minecraft.network.codec.PacketCodecs#UNLIMITED_NBT_COMPOUND}
	 */
	UNLIMITED_NBT_COMPOUND,

	/**
	 * An available codec for the {@link java.util.Optional<net.minecraft.nbt.NbtCompound>} type.
	 * Delegates to {@link net.minecraft.network.codec.PacketCodecs#OPTIONAL_NBT}
	 */
	OPTIONAL_NBT,

	/**
	 * An available codec for the {@link org.joml.Vector3f} type.
	 * Delegates to {@link net.minecraft.network.codec.PacketCodecs#VECTOR_3F}
	 */
	VECTOR_3F,

	/**
	 * An available codec for the {@link org.joml.Quaternionf} type.
	 * Delegates to {@link net.minecraft.network.codec.PacketCodecs#QUATERNION_F}
	 */
	QUATERNION_F,

	/**
	 * An available codec for the {@link com.mojang.authlib.properties.PropertyMap} type.
	 * Delegates to {@link net.minecraft.network.codec.PacketCodecs#PROPERTY_MAP}
	 */
	PROPERTY_MAP,

	/**
	 * An available codec for the {@link com.mojang.authlib.GameProfile} type.
	 * Delegates to {@link net.minecraft.network.codec.PacketCodecs#GAME_PROFILE}
	 */
	GAME_PROFILE
}
