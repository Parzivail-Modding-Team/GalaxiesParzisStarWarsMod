package dev.pswg.model.g3d;

import org.joml.Matrix4f;
import org.joml.Matrix4fc;

import java.util.Map;

/**
 * Reusable pose evaluation on either side of the game. Each instance belongs
 * to one entity or caller; the loaded rig is shared. All coordinates use blocks.
 */
public final class G3dPose
{
	/**
	 * Tests whether a node has been reduced to a point. A fully zero scale is
	 * the pose-side way to hide a part; skip it before building normal matrices.
	 */
	public static boolean isCollapsed(Matrix4fc matrix)
	{
		return matrix.m00() == 0 && matrix.m01() == 0 && matrix.m02() == 0
		       && matrix.m10() == 0 && matrix.m11() == 0 && matrix.m12() == 0
		       && matrix.m20() == 0 && matrix.m21() == 0 && matrix.m22() == 0;
	}

	/**
	 * Shared, read-only rest data.
	 */
	private final G3dRig _rig;

	/**
	 * Model-space transforms, reused across evaluations.
	 */
	private final Matrix4f[] _matrices;

	/**
	 * Scratch local transform.
	 */
	private final Matrix4f _local = new Matrix4f();

	/**
	 * Creates a pose initialized to the rig's rest transforms.
	 */
	public G3dPose(G3dRig rig)
	{
		_rig = rig;
		_matrices = new Matrix4f[rig.nodes().size()];

		for (int index = 0; index < _matrices.length; index++)
			_matrices[index] = new Matrix4f();

		evaluate(Map.of());
	}

	/**
	 * Replaces named local transforms and uses rest transforms for missing names.
	 * No recursion or matrices are allocated during evaluation. Unknown names
	 * are ignored so gameplay code can work with models from different packs.
	 */
	public void evaluate(Map<String, G3dTransform> overrides)
	{
		for (int index = 0; index < _matrices.length; index++)
		{
			var node = _rig.nodes().get(index);
			overrides.getOrDefault(node.id(), node.restTransform()).matrix(_local);

			if (node.parent() < 0)
				_matrices[index].set(_local);
			else
				_matrices[node.parent()].mul(_local, _matrices[index]);
		}
	}

	/**
	 * Gets a read-only node matrix that remains valid until the next evaluation.
	 */
	public Matrix4fc nodeMatrix(int index)
	{
		return _matrices[index];
	}

	/**
	 * Writes a socket's model-space transform into a caller-owned matrix.
	 */
	public Matrix4f socketMatrix(String name, Matrix4f output)
	{
		var socket = _rig.socket(name);
		socket.localTransform().matrix(_local);
		return _matrices[socket.node()].mul(_local, output);
	}

	/**
	 * Writes a world-space socket transform using the caller's model-to-world matrix.
	 */
	public Matrix4f socketMatrix(String name, Matrix4fc modelToWorld, Matrix4f output)
	{
		socketMatrix(name, output);
		return modelToWorld.mul(output, output);
	}

	/**
	 * Copies evaluated matrices for a queued render submission or another thread.
	 */
	public Matrix4fc[] snapshot()
	{
		var result = new Matrix4fc[_matrices.length];
		for (int index = 0; index < result.length; index++)
			result[index] = new Matrix4f(_matrices[index]);

		return result;
	}
}
