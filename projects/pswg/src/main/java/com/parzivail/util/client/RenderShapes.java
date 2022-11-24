package com.parzivail.util.client;

public class RenderShapes
{
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

	private static final int[] _counterClockwiseVertIndices = new int[] {0, 1, 2, 3};
	private static final int[] _clockwiseVertIndices = new int[] {3, 2, 1, 0};

	private static boolean _renderClockwise = false;
	private static int _skipFace = -1;

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

	public static void box(VertexConsumerBuffer vcb)
	{
		box(vcb, _vertsBox);
	}

	public static void drawSolidBoxSkew(VertexConsumerBuffer vcb, float thickness, float topX, float topY, float topZ, float bottomX, float bottomY, float bottomZ)
	{
		box(vcb, thickness, thickness, topX, topY, topZ, bottomX, bottomY, bottomZ);
	}

	public static void drawSolidBoxSkewTaper(VertexConsumerBuffer vcb, float thicknessTop, float thicknessBottom, float topX, float topY, float topZ, float bottomX, float bottomY, float bottomZ)
	{
		box(vcb, thicknessTop, thicknessBottom, topX, topY, topZ, bottomX, bottomY, bottomZ);
	}

	public static void skipFace(int skipFace)
	{
		_skipFace = skipFace;
	}

	private static void box(VertexConsumerBuffer vcb, float thicknessTop, float thicknessBottom, float topX, float topY, float topZ, float bottomX, float bottomY, float bottomZ)
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

		box(vcb, _vertsBoxSkew);
	}

	private static void box(VertexConsumerBuffer vcb, float[][] verts)
	{
		var indices = _renderClockwise ? _clockwiseVertIndices : _counterClockwiseVertIndices;

		for (var i = 0; i < 6; i++)
		{
			if (i == _skipFace)
				continue;
			vcb.vertex(verts[_facesBox[i][indices[0]]][0], verts[_facesBox[i][indices[0]]][1], verts[_facesBox[i][indices[0]]][2], _normalsBox[i][0], _normalsBox[i][1], _normalsBox[i][2], 0, 0);
			vcb.vertex(verts[_facesBox[i][indices[1]]][0], verts[_facesBox[i][indices[1]]][1], verts[_facesBox[i][indices[1]]][2], _normalsBox[i][0], _normalsBox[i][1], _normalsBox[i][2], 1, 0);
			vcb.vertex(verts[_facesBox[i][indices[2]]][0], verts[_facesBox[i][indices[2]]][1], verts[_facesBox[i][indices[2]]][2], _normalsBox[i][0], _normalsBox[i][1], _normalsBox[i][2], 1, 1);
			vcb.vertex(verts[_facesBox[i][indices[3]]][0], verts[_facesBox[i][indices[3]]][1], verts[_facesBox[i][indices[3]]][2], _normalsBox[i][0], _normalsBox[i][1], _normalsBox[i][2], 0, 1);
		}
	}

	public static void invertCull(boolean invert)
	{
		_renderClockwise = invert;
	}
}
