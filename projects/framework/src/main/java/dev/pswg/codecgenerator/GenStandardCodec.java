package dev.pswg.codecgenerator;

/**
 * A list of all available standard codecs
 */
public enum GenStandardCodec
{
	/**
	 * Automatically determine which codec best represents the given type
	 */
	AUTOMATIC,

	/**
	 * An available codec for the {@link Boolean} type.
	 * Delegates to {@link com.mojang.serialization.Codec#BOOL}
	 */
	BOOL,

	/**
	 * An available codec for the {@link Byte} type.
	 * Delegates to {@link com.mojang.serialization.Codec#BYTE}
	 */
	BYTE,

	/**
	 * An available codec for the {@link Byte} type.
	 * Delegates to {@link net.minecraft.util.dynamic.Codecs#UNSIGNED_BYTE}
	 */
	UNSIGNED_BYTE,

	/**
	 * An available codec for the {@link Short} type.
	 * Delegates to {@link com.mojang.serialization.Codec#SHORT}
	 */
	SHORT,

	/**
	 * An available codec for the {@link Integer} type.
	 * Delegates to {@link com.mojang.serialization.Codec#INT}
	 */
	INT,

	/**
	 * An available codec for the {@link Integer} type.
	 * Delegates to {@link net.minecraft.util.dynamic.Codecs#RGB}
	 */
	RGB,

	/**
	 * An available codec for the {@link Integer} type.
	 * Delegates to {@link net.minecraft.util.dynamic.Codecs#ARGB}
	 */
	ARGB,

	/**
	 * An available codec for the {@link Integer} type.
	 * Delegates to {@link net.minecraft.util.dynamic.Codecs#NON_NEGATIVE_INT}
	 */
	NON_NEGATIVE_INT,

	/**
	 * An available codec for the {@link Integer} type.
	 * Delegates to {@link net.minecraft.util.dynamic.Codecs#POSITIVE_INT}
	 */
	POSITIVE_INT,

	/**
	 * An available codec for the {@link Integer} type.
	 * Delegates to {@link net.minecraft.util.dynamic.Codecs#CODEPOINT}
	 */
	UNICODE_CODEPOINT,

	/**
	 * An available codec for the {@link Long} type.
	 * Delegates to {@link com.mojang.serialization.Codec#LONG}
	 */
	LONG,

	/**
	 * An available codec for the {@link Float} type.
	 * Delegates to {@link com.mojang.serialization.Codec#FLOAT}
	 */
	FLOAT,

	/**
	 * An available codec for the {@link Float} type.
	 * Delegates to {@link net.minecraft.util.dynamic.Codecs#NON_NEGATIVE_FLOAT}
	 */
	NON_NEGATIVE_FLOAT,

	/**
	 * An available codec for the {@link Float} type.
	 * Delegates to {@link net.minecraft.util.dynamic.Codecs#POSITIVE_FLOAT}
	 */
	POSITIVE_FLOAT,

	/**
	 * An available codec for the {@link Double} type.
	 * Delegates to {@link com.mojang.serialization.Codec#DOUBLE}
	 */
	DOUBLE,

	/**
	 * An available codec for the {@link java.lang.String} type.
	 * Delegates to {@link com.mojang.serialization.Codec#STRING}
	 */
	STRING,

	/**
	 * An available codec for the {@link java.lang.String} type.
	 * Delegates to {@link net.minecraft.util.dynamic.Codecs#ESCAPED_STRING}
	 */
	ESCAPED_STRING,

	/**
	 * An available codec for the {@link java.lang.String} type.
	 * Delegates to {@link net.minecraft.util.dynamic.Codecs#PLAYER_NAME}
	 */
	PLAYER_NAME,

	/**
	 * An available codec for the {@link java.lang.String} type.
	 * Delegates to {@link net.minecraft.util.dynamic.Codecs#NON_EMPTY_STRING}
	 */
	NON_EMPTY_STRING,

	/**
	 * An available codec for the {@link java.lang.String} type.
	 * Delegates to {@link net.minecraft.util.dynamic.Codecs#IDENTIFIER_PATH}
	 */
	IDENTIFIER_PATH,

	/**
	 * An available codec for the {@link java.nio.ByteBuffer} type.
	 * Delegates to {@link com.mojang.serialization.Codec#BYTE_BUFFER}
	 */
	BYTE_BUFFER,

	/**
	 * An available codec for the {@link java.util.stream.IntStream} type.
	 * Delegates to {@link com.mojang.serialization.Codec#INT_STREAM}
	 */
	INT_STREAM,

	/**
	 * An available codec for the {@link java.util.stream.LongStream} type.
	 * Delegates to {@link com.mojang.serialization.Codec#LONG_STREAM}
	 */
	LONG_STREAM,

	/**
	 * An available codec for the {@link com.google.gson.JsonElement} type.
	 * Delegates to {@link net.minecraft.util.dynamic.Codecs#JSON_ELEMENT}
	 */
	JSON_ELEMENT,

	/**
	 * An available codec for the {@link org.joml.Vector3f} type.
	 * Delegates to {@link net.minecraft.util.dynamic.Codecs#VECTOR_3F}
	 */
	VECTOR_3F,

	/**
	 * An available codec for the {@link org.joml.Vector4f} type.
	 * Delegates to {@link net.minecraft.util.dynamic.Codecs#VECTOR_4F}
	 */
	VECTOR_4F,

	/**
	 * An available codec for the {@link org.joml.Quaternionf} type.
	 * Delegates to {@link net.minecraft.util.dynamic.Codecs#QUATERNION_F}
	 */
	QUATERNION_F,

	/**
	 * An available codec for the {@link org.joml.Quaternionf} type.
	 * Delegates to {@link net.minecraft.util.dynamic.Codecs#ROTATION}
	 */
	ROTATION,

	/**
	 * An available codec for the {@link org.joml.AxisAngle4f} type.
	 * Delegates to {@link net.minecraft.util.dynamic.Codecs#AXIS_ANGLE_4F}
	 */
	AXIS_ANGLE_4F,

	/**
	 * An available codec for the {@link org.joml.Matrix4f} type.
	 * Delegates to {@link net.minecraft.util.dynamic.Codecs#MATRIX_4F}
	 */
	MATRIX_4F,

	/**
	 * An available codec for the {@link java.time.Instant} type.
	 * Delegates to {@link net.minecraft.util.dynamic.Codecs#INSTANT}
	 */
	INSTANT,

	/**
	 * An available codec for the {@link net.minecraft.util.dynamic.Codecs.TagEntryId} type.
	 * Delegates to {@link net.minecraft.util.dynamic.Codecs#TAG_ENTRY_ID}
	 */
	TAG_ENTRY_ID,

	/**
	 * An available codec for the {@link java.util.BitSet} type.
	 * Delegates to {@link net.minecraft.util.dynamic.Codecs#BIT_SET}
	 */
	BIT_SET,

	/**
	 * An available codec for the {@link com.mojang.authlib.properties.Property} type.
	 * Delegates to {@link net.minecraft.util.dynamic.Codecs#GAME_PROFILE_PROPERTY}
	 */
	GAME_PROFILE_PROPERTY,

	/**
	 * An available codec for the {@link com.mojang.authlib.properties.PropertyMap} type.
	 * Delegates to {@link net.minecraft.util.dynamic.Codecs#GAME_PROFILE_PROPERTY_MAP}
	 */
	GAME_PROFILE_PROPERTY_MAP,

	/**
	 * An available codec for the {@link com.mojang.authlib.GameProfile} type.
	 * Delegates to {@link net.minecraft.util.dynamic.Codecs#GAME_PROFILE_WITH_PROPERTIES}
	 */
	GAME_PROFILE_WITH_PROPERTIES,

	/**
	 * An available codec for the byte[] type.
	 * Delegates to {@link net.minecraft.util.dynamic.Codecs#BASE_64}
	 */
	BASE_64,

	/**
	 * An available codec for the {@link net.minecraft.util.Identifier} type.
	 * Delegates to {@link net.minecraft.util.Identifier#CODEC}
	 */
	IDENTIFIER;
}
