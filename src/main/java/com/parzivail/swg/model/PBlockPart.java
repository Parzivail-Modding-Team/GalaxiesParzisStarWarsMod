package com.parzivail.swg.model;

import net.minecraft.client.renderer.block.model.BlockPart;
import net.minecraft.client.renderer.block.model.BlockPartFace;
import net.minecraft.client.renderer.block.model.BlockPartRotation;
import net.minecraft.util.EnumFacing;
import org.lwjgl.util.vector.Vector3f;

import javax.annotation.Nullable;
import java.util.Map;

public class PBlockPart extends BlockPart
{
	public final Vector3f rotated;

	public PBlockPart(Vector3f positionFromIn, Vector3f positionToIn, Map<EnumFacing, BlockPartFace> mapFacesIn, @Nullable BlockPartRotation partRotationIn, Vector3f partRotatedIn, boolean shadeIn)
	{
		super(positionFromIn, positionToIn, mapFacesIn, partRotationIn, shadeIn);
		rotated = partRotatedIn;
	}
}
