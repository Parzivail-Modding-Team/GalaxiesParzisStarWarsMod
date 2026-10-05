package dev.pswg.model.animation;

import com.mojang.serialization.Codec;
import dev.pswg.codec.GalaxiesCodecs;
import dev.pswg.codecgenerator.CodecDefault;
import dev.pswg.codecgenerator.CodecSource;
import dev.pswg.codecgenerator.GenerateCodec;
import dev.pswg.codecgenerator.UseCodec;
import dev.pswg.generated.codecs.IEulerTransformCodec;
import dev.pswg.model.g3d.G3dTransform;
import net.minecraft.util.StringRepresentable;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.Objects;

/**
 * Immutable translation, Euler XYZ rotation, and scale used by numeric poses.
 * Translation is in blocks, rotation is in degrees, and scale is dimensionless.
 *
 * <p>This authored Euler representation is distinct from {@link G3dTransform},
 * which stores rotation as a quaternion. Conversion creates that quaternion
 * without rescaling translation; callers supply translation in blocks.</p>
 *
 * @param translation Translation in blocks.
 * @param rotation    Euler XYZ angles in degrees.
 * @param scale       Dimensionless scale per axis.
 */
@GenerateCodec(packetCodec = false, strict = true)
public record EulerTransform(
		@UseCodec(customCodec = @CodecSource(source = GalaxiesCodecs.class, member = "FINITE_VECTOR3F"))
		@CodecDefault("new org.joml.Vector3f()")
		Vector3fc translation,
		@UseCodec(customCodec = @CodecSource(source = GalaxiesCodecs.class, member = "FINITE_VECTOR3F"))
		@CodecDefault("new org.joml.Vector3f()")
		Vector3fc rotation,
		@UseCodec(customCodec = @CodecSource(source = GalaxiesCodecs.class, member = "FINITE_VECTOR3F"))
		@CodecDefault("new org.joml.Vector3f(1.0F)")
		Vector3fc scale
) implements IEulerTransformCodec
{
	/**
	 * Numeric channels shared by generic transforms and animation target paths.
	 */
	public enum Component implements StringRepresentable
	{
		/**
		 * Translation in blocks.
		 */
		TRANSLATION("translation"),

		/**
		 * Euler XYZ rotation in degrees.
		 */
		ROTATION("rotation"),

		/**
		 * Dimensionless scale.
		 */
		SCALE("scale");

		/**
		 * Codec for the closed transform-component vocabulary.
		 */
		public static final Codec<Component> CODEC = StringRepresentable.fromEnum(Component::values);

		/**
		 * Serialized component name.
		 */
		private final String _serializedName;

		/**
		 * Creates a transform-component value.
		 */
		Component(String serializedName)
		{
			_serializedName = serializedName;
		}

		/**
		 * Gets the serialized component name.
		 */
		@Override
		public String getSerializedName()
		{
			return _serializedName;
		}
	}

	/**
	 * The unchanged transform shared by omitted pose transform fields.
	 */
	public static final EulerTransform IDENTITY = new EulerTransform(
			new Vector3f(),
			new Vector3f(),
			new Vector3f(1.0F)
	);

	/**
	 * Copies JOML vectors so later mutation of constructor arguments cannot alter this value.
	 */
	public EulerTransform
	{
		translation = new Vector3f(Objects.requireNonNull(translation));
		rotation = new Vector3f(Objects.requireNonNull(rotation));
		scale = new Vector3f(Objects.requireNonNull(scale));
	}

	/**
	 * Converts the authored Euler transform to the runtime quaternion TRS without rescaling translation.
	 */
	public G3dTransform toG3dTransform()
	{
		var quaternion = new Quaternionf().rotationXYZ(
				(float)Math.toRadians(rotation.x()),
				(float)Math.toRadians(rotation.y()),
				(float)Math.toRadians(rotation.z())
		);
		return new G3dTransform(translation, quaternion, scale);
	}
}
