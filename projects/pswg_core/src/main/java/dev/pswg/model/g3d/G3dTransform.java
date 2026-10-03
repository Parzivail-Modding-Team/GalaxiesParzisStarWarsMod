package dev.pswg.model.g3d;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import org.joml.*;

/**
 * A local translation, rotation, and scale. Source translations use sixteenths
 * of a block; compiled transforms and runtime poses use blocks.
 *
 * @param translation The offset from the parent.
 * @param rotation    The rotation in x, y, z, w order.
 * @param scale       The scale along each local axis.
 */
public record G3dTransform(Vector3fc translation, Quaternionfc rotation, Vector3fc scale)
{
	/**
	 * The unchanged local transform. Treat its JOML values as read-only.
	 */
	public static final G3dTransform IDENTITY = new G3dTransform(new Vector3f(), new Quaternionf(), new Vector3f(1));

	/**
	 * Uses Minecraft's vector and quaternion codecs, including axis-angle input.
	 */
	public static final Codec<G3dTransform> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ExtraCodecs.VECTOR3F.optionalFieldOf("translation", IDENTITY.translation()).forGetter(G3dTransform::translation),
			ExtraCodecs.QUATERNIONF.optionalFieldOf("rotation", IDENTITY.rotation()).forGetter(G3dTransform::rotation),
			ExtraCodecs.VECTOR3F.optionalFieldOf("scale", IDENTITY.scale()).forGetter(G3dTransform::scale)
	).apply(instance, G3dTransform::new));

	/**
	 * Copies the caller's values so later edits do not change this transform.
	 */
	public G3dTransform
	{
		translation = new Vector3f(translation);
		rotation = new Quaternionf(rotation);
		scale = new Vector3f(scale);
	}

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

		// A zero rotation is harmless authoring input. Interpret it as no rotation.
		var normalized = new Quaternionf(rotation);
		if (normalized.lengthSquared() == 0)
			normalized.identity();
		else
			normalized.normalize();

		return new G3dTransform(new Vector3f(translation).mul(1 / 16f), normalized, scale);
	}
}
