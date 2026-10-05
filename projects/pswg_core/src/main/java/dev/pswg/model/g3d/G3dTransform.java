package dev.pswg.model.g3d;

import dev.pswg.codec.GalaxiesCodecs;
import dev.pswg.codecgenerator.*;
import dev.pswg.generated.codecs.IComponentCodec;
import dev.pswg.generated.codecs.IG3dTransformCodec;
import org.joml.*;

/**
 * A local translation, rotation, and scale. Source translations use sixteenths
 * of a block; compiled transforms and runtime poses use blocks.
 *
 * @param translation The offset from the parent.
 * @param rotation    Quaternion rotation; authoring accepts Euler XYZ degrees or x/y/z/w quaternion input.
 * @param scale       The scale along each local axis.
 */
@GenerateCodec(strict = true)
public record G3dTransform(
		@UseCodec(customCodec = @CodecSource(source = GalaxiesCodecs.class, member = "FINITE_VECTOR3F"))
		@CodecDefault("new org.joml.Vector3f()") Vector3fc translation,
		@UseCodec(customCodec = @CodecSource(source = GalaxiesCodecs.class, member = "ROTATION"))
		@CodecDefault("new org.joml.Quaternionf()") Quaternionfc rotation,
		@UseCodec(customCodec = @CodecSource(source = GalaxiesCodecs.class, member = "FINITE_VECTOR3F"))
		@CodecDefault("new org.joml.Vector3f(1)") Vector3fc scale
) implements IG3dTransformCodec
{
	/**
	 * Numeric components used by transform-animation channel paths.
	 */
	@GenerateEnumCodec
	public enum Component implements IComponentCodec
	{
		/**
		 * Translation in consumer-owned coordinate units.
		 */
		TRANSLATION,

		/**
		 * Rotation; vector animation values use Euler XYZ degrees.
		 */
		ROTATION,

		/**
		 * Dimensionless scale.
		 */
		SCALE
	}

	/**
	 * The unchanged local transform. Treat its JOML values as read-only.
	 */
	public static final G3dTransform IDENTITY = new G3dTransform(new Vector3f(), new Quaternionf(), new Vector3f(1));

	/**
	 * Writes this transform into a caller-owned matrix without allocating one.
	 */
	public Matrix4f matrix(Matrix4f output)
	{
		return output.translationRotateScale(translation, rotation, scale);
	}

	/**
	 * Converts a source transform to blocks and normalizes its rotation.
	 */
	public G3dTransform compile()
	{
		if (!translation.isFinite() || !rotation.isFinite() || !scale.isFinite())
			throw new IllegalArgumentException("Transform contains a non-finite value");

		var normalized = new Quaternionf(rotation);
		if (normalized.lengthSquared() == 0)
			normalized.identity();
		else
			normalized.normalize();

		return new G3dTransform(new Vector3f(translation).mul(1 / 16f), normalized, scale);
	}
}
