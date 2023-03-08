package com.parzivail.util.client;

import com.parzivail.util.math.MathUtil;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;
import org.joml.Vector4f;

public enum ImmediateBuffer
{
	// Multiple instances are possible (to hold
	// concurrent states) but are unused here
	A;

	private static final float[][] _vertsBox = new float[8][3];
	private static final float[][] _vertsBoxSkew;

	private static final float[][] _normalsBox = {
			{ -1.0f, 0.0f, 0.0f },
			{ 0.0f, 1.0f, 0.0f },
			{ 1.0f, 0.0f, 0.0f },
			{ 0.0f, -1.0f, 0.0f },
			{ 0.0f, 0.0f, 1.0f },
			{ 0.0f, 0.0f, -1.0f }
	};

	private static final int[][] _facesBox = {
			{ 0, 1, 2, 3 }, { 3, 2, 6, 7 }, { 7, 6, 5, 4 }, { 4, 5, 1, 0 }, { 5, 6, 2, 1 }, { 7, 4, 0, 3 }
	};

	private static final int[] _counterClockwiseVertIndices = new int[] { 0, 1, 2, 3 };
	private static final int[] _clockwiseVertIndices = new int[] { 3, 2, 1, 0 };

	static
	{
		// cube
		_vertsBox[0][0] = _vertsBox[1][0] = _vertsBox[2][0] = _vertsBox[3][0] = -0.5f;
		_vertsBox[4][0] = _vertsBox[5][0] = _vertsBox[6][0] = _vertsBox[7][0] = 0.5f;
		_vertsBox[0][1] = _vertsBox[1][1] = _vertsBox[4][1] = _vertsBox[5][1] = -0.5f;
		_vertsBox[2][1] = _vertsBox[3][1] = _vertsBox[6][1] = _vertsBox[7][1] = 0.5f;
		_vertsBox[0][2] = _vertsBox[3][2] = _vertsBox[4][2] = _vertsBox[7][2] = -0.5f;
		_vertsBox[1][2] = _vertsBox[2][2] = _vertsBox[5][2] = _vertsBox[6][2] = 0.5f;
		_vertsBoxSkew = deepCopyIntMatrix(_vertsBox);
	}

	private static float[][] deepCopyIntMatrix(float[][] input)
	{
		if (input == null)
			return null;
		var result = new float[input.length][];
		for (var r = 0; r < input.length; r++)
		{
			result[r] = input[r].clone();
		}
		return result;
	}

	private VertexConsumer vertexConsumer;
	private MatrixStack.Entry matrices;
	private float r;
	private float g;
	private float b;
	private float a;
	private int overlay;
	private int light;

	private boolean _renderClockwise = false;
	private int _skipFace = -1;
	private float scaleX = 1;
	private float scaleY = 1;
	private float scaleZ = 1;

	public void init(VertexConsumer vertexConsumer, MatrixStack.Entry matrices, float r, float g, float b, float a, int overlay, int light)
	{
		this.vertexConsumer = vertexConsumer;
		this.matrices = matrices;
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
		this.overlay = overlay;
		this.light = light;
	}

	public void setMatrices(MatrixStack.Entry matrices)
	{
		this.matrices = matrices;
	}

	public void setColor(float r, float g, float b, float a)
	{
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}

	public void setColor(int argb)
	{
		this.a = ((argb >> 24) & 0xFF) / 255f;
		this.r = ((argb >> 16) & 0xFF) / 255f;
		this.g = ((argb >> 8) & 0xFF) / 255f;
		this.b = (argb & 0xFF) / 255f;
	}

	public void setColor(int baseRgb, int a)
	{
		this.a = (a & 0xFF) / 255f;
		this.r = ((baseRgb >> 16) & 0xFF) / 255f;
		this.g = ((baseRgb >> 8) & 0xFF) / 255f;
		this.b = (baseRgb & 0xFF) / 255f;
	}

	public void setOverlay(int overlay)
	{
		this.overlay = overlay;
	}

	public void setLight(int light)
	{
		this.light = light;
	}

	public void vertex(Vector3f pos, Vector3f normal, float u, float v)
	{
		var pos4 = new Vector4f(pos.x, pos.y, pos.z, 1);
		normal = new Vector3f(normal);

		pos4.mul(matrices.getPositionMatrix());
		normal.mul(matrices.getNormalMatrix());

		vertexConsumer.vertex(pos4.x, pos4.y, pos4.z, r, g, b, a, u, v, overlay, light, normal.x, normal.y, normal.z);
	}

	public void vertex(float x, float y, float z, float nx, float ny, float nz, float u, float v)
	{
		vertex(new Vector3f(x, y, z), new Vector3f(nx, ny, nz), u, v);
	}

	public void line(float x1, float y1, float z1, float x2, float y2, float z2)
	{
		var start = new Vector3f(x1, y1, z1);
		var end = new Vector3f(x2, y2, z2);
		var normal = new Vector3f(x2, y2, z2);
		normal.sub(start);
		normal.normalize();

		vertex(start, normal, 0, 0);
		vertex(end, normal, 0, 0);
	}

	public void line(Vec3d start, Vec3d end)
	{
		var normal = new Vec3d(end.x, end.y, end.z).subtract(start).normalize();

		vertex((float)start.x, (float)start.y, (float)start.z, (float)normal.x, (float)normal.y, (float)normal.z, 0, 0);
		vertex((float)end.x, (float)end.y, (float)end.z, (float)normal.x, (float)normal.y, (float)normal.z, 0, 0);
	}

	public void axes(float scale)
	{
		setColor(1, 0, 0, 1);
		line(Vec3d.ZERO, MathUtil.V3D_POS_X.multiply(scale));
		setColor(0, 1, 0, 1);
		line(Vec3d.ZERO, MathUtil.V3D_POS_Y.multiply(scale));
		setColor(0, 0, 1, 1);
		line(Vec3d.ZERO, MathUtil.V3D_POS_Z.multiply(scale));
	}

	public void box()
	{
		box(_vertsBox);
	}

	public void drawSolidBoxSkew(float thickness, float topX, float topY, float topZ, float bottomX, float bottomY, float bottomZ)
	{
		box(thickness, thickness, topX, topY, topZ, bottomX, bottomY, bottomZ);
	}

	public void drawSolidBoxSkewTaper(float thicknessTop, float thicknessBottom, float topX, float topY, float topZ, float bottomX, float bottomY, float bottomZ)
	{
		box(thicknessTop, thicknessBottom, topX, topY, topZ, bottomX, bottomY, bottomZ);
	}

	public void skipFace(int skipFace)
	{
		_skipFace = skipFace;
	}

	private void box(float thicknessTop, float thicknessBottom, float topX, float topY, float topZ, float bottomX, float bottomY, float bottomZ)
	{
		_vertsBoxSkew[0][0] = _vertsBoxSkew[1][0] = -thicknessBottom + bottomX;
		_vertsBoxSkew[2][0] = _vertsBoxSkew[3][0] = -thicknessTop + topX;
		_vertsBoxSkew[4][0] = _vertsBoxSkew[5][0] = thicknessBottom + bottomX;
		_vertsBoxSkew[6][0] = _vertsBoxSkew[7][0] = thicknessTop + topX;

		_vertsBoxSkew[0][1] = _vertsBoxSkew[1][1] = bottomY;
		_vertsBoxSkew[4][1] = _vertsBoxSkew[5][1] = bottomY;
		_vertsBoxSkew[2][1] = _vertsBoxSkew[3][1] = topY;
		_vertsBoxSkew[6][1] = _vertsBoxSkew[7][1] = topY;

		_vertsBoxSkew[0][2] = -thicknessBottom + bottomZ;
		_vertsBoxSkew[7][2] = -thicknessTop + topZ;
		_vertsBoxSkew[4][2] = -thicknessBottom + bottomZ;
		_vertsBoxSkew[3][2] = -thicknessTop + topZ;
		_vertsBoxSkew[1][2] = thicknessBottom + bottomZ;
		_vertsBoxSkew[6][2] = thicknessTop + topZ;
		_vertsBoxSkew[5][2] = thicknessBottom + bottomZ;
		_vertsBoxSkew[2][2] = thicknessTop + topZ;

		box(_vertsBoxSkew);
	}

	private void box(float[][] verts)
	{
		var indices = _renderClockwise ? _clockwiseVertIndices : _counterClockwiseVertIndices;

		for (var i = 0; i < 6; i++)
		{
			if (i == _skipFace)
				continue;
			vertex(verts[_facesBox[i][indices[0]]][0] * scaleX, verts[_facesBox[i][indices[0]]][1] * scaleY, verts[_facesBox[i][indices[0]]][2] * scaleZ, _normalsBox[i][0], _normalsBox[i][1], _normalsBox[i][2], 0, 0);
			vertex(verts[_facesBox[i][indices[1]]][0] * scaleX, verts[_facesBox[i][indices[1]]][1] * scaleY, verts[_facesBox[i][indices[1]]][2] * scaleZ, _normalsBox[i][0], _normalsBox[i][1], _normalsBox[i][2], 1, 0);
			vertex(verts[_facesBox[i][indices[2]]][0] * scaleX, verts[_facesBox[i][indices[2]]][1] * scaleY, verts[_facesBox[i][indices[2]]][2] * scaleZ, _normalsBox[i][0], _normalsBox[i][1], _normalsBox[i][2], 1, 1);
			vertex(verts[_facesBox[i][indices[3]]][0] * scaleX, verts[_facesBox[i][indices[3]]][1] * scaleY, verts[_facesBox[i][indices[3]]][2] * scaleZ, _normalsBox[i][0], _normalsBox[i][1], _normalsBox[i][2], 0, 1);
		}
	}

	public void setScale(float x, float y, float z)
	{
		scaleX = x;
		scaleY = y;
		scaleZ = z;
	}

	public void resetScale()
	{
		scaleX = 1;
		scaleY = 1;
		scaleZ = 1;
	}

	public void invertCull(boolean invert)
	{
		_renderClockwise = invert;
	}
}
