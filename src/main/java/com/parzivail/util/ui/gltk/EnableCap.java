package com.parzivail.util.ui.gltk;

import org.lwjgl.opengl.GL11;

/**
 * Created by colby on 9/13/2017.
 */
public enum EnableCap
{
	PointSmooth(GL11.GL_POINT_SMOOTH),
	LineSmooth(GL11.GL_LINE_SMOOTH),
	LineStipple(GL11.GL_LINE_STIPPLE),
	PolygonSmooth(GL11.GL_POLYGON_SMOOTH),
	PolygonStipple(GL11.GL_POLYGON_STIPPLE),
	CullFace(GL11.GL_CULL_FACE),
	Lighting(GL11.GL_LIGHTING),
	ColorMaterial(GL11.GL_COLOR_MATERIAL),
	Fog(GL11.GL_FOG),
	DepthTest(GL11.GL_DEPTH_TEST),
	StencilTest(GL11.GL_STENCIL_TEST),
	Normalize(GL11.GL_NORMALIZE),
	AlphaTest(GL11.GL_ALPHA_TEST),
	Dither(GL11.GL_DITHER),
	Blend(GL11.GL_BLEND),
	IndexLogicOp(GL11.GL_INDEX_LOGIC_OP),
	ColorLogicOp(GL11.GL_COLOR_LOGIC_OP),
	ScissorTest(GL11.GL_SCISSOR_TEST),
	TextureGenS(GL11.GL_TEXTURE_GEN_S),
	TextureGenT(GL11.GL_TEXTURE_GEN_T),
	TextureGenR(GL11.GL_TEXTURE_GEN_R),
	TextureGenQ(GL11.GL_TEXTURE_GEN_Q),
	AutoNormal(GL11.GL_AUTO_NORMAL),
	Map1Color4(GL11.GL_MAP1_COLOR_4),
	Map1Index(GL11.GL_MAP1_INDEX),
	Map1Normal(GL11.GL_MAP1_NORMAL),
	Map1TextureCoord1(GL11.GL_MAP1_TEXTURE_COORD_1),
	Map1TextureCoord2(GL11.GL_MAP1_TEXTURE_COORD_2),
	Map1TextureCoord3(GL11.GL_MAP1_TEXTURE_COORD_3),
	Map1TextureCoord4(GL11.GL_MAP1_TEXTURE_COORD_4),
	Map1Vertex3(GL11.GL_MAP1_VERTEX_3),
	Map1Vertex4(GL11.GL_MAP1_VERTEX_4),
	Map2Color4(GL11.GL_MAP2_COLOR_4),
	Map2Index(GL11.GL_MAP2_INDEX),
	Map2Normal(GL11.GL_MAP2_NORMAL),
	Map2TextureCoord1(GL11.GL_MAP2_TEXTURE_COORD_1),
	Map2TextureCoord2(GL11.GL_MAP2_TEXTURE_COORD_2),
	Map2TextureCoord3(GL11.GL_MAP2_TEXTURE_COORD_3),
	Map2TextureCoord4(GL11.GL_MAP2_TEXTURE_COORD_4),
	Map2Vertex3(GL11.GL_MAP2_VERTEX_3),
	Map2Vertex4(GL11.GL_MAP2_VERTEX_4),
	Texture1D(GL11.GL_TEXTURE_1D),
	Texture2D(GL11.GL_TEXTURE_2D),
	PolygonOffsetPoint(GL11.GL_POLYGON_OFFSET_POINT),
	PolygonOffsetLine(GL11.GL_POLYGON_OFFSET_LINE),
	ClipPlane0(GL11.GL_CLIP_PLANE0),
	ClipPlane1(GL11.GL_CLIP_PLANE1),
	ClipPlane2(GL11.GL_CLIP_PLANE2),
	ClipPlane3(GL11.GL_CLIP_PLANE3),
	ClipPlane4(GL11.GL_CLIP_PLANE4),
	ClipPlane5(GL11.GL_CLIP_PLANE5),
	Light0(GL11.GL_LIGHT0),
	Light1(GL11.GL_LIGHT1),
	Light2(GL11.GL_LIGHT2),
	Light3(GL11.GL_LIGHT3),
	Light4(GL11.GL_LIGHT4),
	Light5(GL11.GL_LIGHT5),
	Light6(GL11.GL_LIGHT6),
	Light7(GL11.GL_LIGHT7),
	PolygonOffsetFill(GL11.GL_POLYGON_OFFSET_FILL),
	VertexArray(GL11.GL_VERTEX_ARRAY),
	NormalArray(GL11.GL_NORMAL_ARRAY),
	ColorArray(GL11.GL_COLOR_ARRAY),
	IndexArray(GL11.GL_INDEX_ARRAY),
	TextureCoordArray(GL11.GL_TEXTURE_COORD_ARRAY),
	EdgeFlagArray(GL11.GL_EDGE_FLAG_ARRAY);

	private final int glValue;

	EnableCap(int glValue)
	{
		this.glValue = glValue;
	}

	public int getGlValue()
	{
		return glValue;
	}
}
