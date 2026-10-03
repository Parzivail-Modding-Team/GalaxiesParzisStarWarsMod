package dev.pswg.model.g3d;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import org.joml.Vector3f;
import org.joml.Vector3fc;

/**
 * A rest-pose bounding box, in blocks. Empty geometry has a zero-sized box.
 *
 * @param min The lowest coordinates.
 * @param max The highest coordinates.
 */
public record G3dBounds(Vector3fc min, Vector3fc max)
{
	/**
	 * A box used when there are no vertices.
	 */
	public static final G3dBounds EMPTY = new G3dBounds(new Vector3f(), new Vector3f());

	/**
	 * The shared metadata codec.
	 */
	public static final Codec<G3dBounds> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ExtraCodecs.VECTOR3F.fieldOf("min").forGetter(G3dBounds::min),
			ExtraCodecs.VECTOR3F.fieldOf("max").forGetter(G3dBounds::max)
	).apply(instance, G3dBounds::new));

	/**
	 * Keeps the bounds independent of scratch vectors used during compilation.
	 */
	public G3dBounds
	{
		min = new Vector3f(min);
		max = new Vector3f(max);
	}
}
