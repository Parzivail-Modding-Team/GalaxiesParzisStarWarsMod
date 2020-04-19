package com.parzivail.pswg.client.pm3d;

import com.parzivail.pswg.client.model.SimpleModel;
import com.parzivail.pswg.util.VertUtil;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.ModelHelper;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

import java.util.function.Function;

public class PM3DBakedBlockModel extends SimpleModel
{
	private final Sprite baseSprite;
	private final PM3DFile container;

	private PM3DBakedBlockModel(Sprite baseSprite, Sprite particleSprite, PM3DFile container)
	{
		super(particleSprite, ModelHelper.MODEL_TRANSFORM_BLOCK);
		this.baseSprite = baseSprite;
		this.container = container;
	}

	private void emitFace(QuadEmitter quadEmitter, PM3DFace face)
	{
		quadEmitter.colorIndex(1).spriteColor(0, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF).material(getMaterial(face));

		PM3DVertPointer a = face.verts.get(0);
		PM3DVertPointer b = face.verts.get(1);
		PM3DVertPointer c = face.verts.get(2);
		PM3DVertPointer d = face.verts.size() == 4 ? face.verts.get(3) : c;

		Vec3d vA = container.verts.get(a.getVertex());
		Vec3d vB = container.verts.get(b.getVertex());
		Vec3d vC = container.verts.get(c.getVertex());
		Vec3d vD = container.verts.get(d.getVertex());

		Vec3d nA = container.normals.get(a.getNormal());
		Vec3d nB = container.normals.get(b.getNormal());
		Vec3d nC = container.normals.get(c.getNormal());
		Vec3d nD = container.normals.get(d.getNormal());

		Vec2f tA = container.uvs.get(a.getTexture());
		Vec2f tB = container.uvs.get(b.getTexture());
		Vec2f tC = container.uvs.get(c.getTexture());
		Vec2f tD = container.uvs.get(d.getTexture());

		quadEmitter.pos(0, VertUtil.toMinecraftVertex(vA)).normal(0, VertUtil.toMinecraftNormal(nA)).sprite(0, 0, tA.x, 1 - tA.y);
		quadEmitter.pos(1, VertUtil.toMinecraftVertex(vB)).normal(1, VertUtil.toMinecraftNormal(nB)).sprite(1, 0, tB.x, 1 - tB.y);
		quadEmitter.pos(2, VertUtil.toMinecraftVertex(vC)).normal(2, VertUtil.toMinecraftNormal(nC)).sprite(2, 0, tC.x, 1 - tC.y);
		quadEmitter.pos(3, VertUtil.toMinecraftVertex(vD)).normal(3, VertUtil.toMinecraftNormal(nD)).sprite(3, 0, tD.x, 1 - tD.y);

		quadEmitter.spriteBake(0, baseSprite, MutableQuadView.BAKE_NORMALIZED);

		quadEmitter.emit();
	}

	private RenderMaterial getMaterial(PM3DFace face)
	{
		switch (face.material)
		{
			case 0:
				return MAT_DIFFUSE_OPAQUE;
			case 1:
				return MAT_DIFFUSE_CUTOUT;
			case 2:
				return MAT_DIFFUSE_TRANSLUCENT;
			case 3:
				return MAT_EMISSIVE;
			default:
			{
				CrashReport crashReport = CrashReport.create(null, String.format("Unknown material ID: %s", face.material));
				throw new CrashException(crashReport);
			}
		}
	}

	@Override
	protected Mesh createMesh()
	{
		MeshBuilder meshBuilder = RENDERER.meshBuilder();
		QuadEmitter quadEmitter = meshBuilder.getEmitter();

		for (PM3DObject o : container.objects)
			for (PM3DFace face : o.faces)
				emitFace(quadEmitter, face);

		return meshBuilder.build();
	}

	public static PM3DBakedBlockModel create(PM3DFile container, Identifier baseTexture, Identifier particleTexture, Function<SpriteIdentifier, Sprite> spriteMap)
	{
		return new PM3DBakedBlockModel(spriteMap.apply(new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEX, baseTexture)), spriteMap.apply(new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEX, particleTexture)), container);
	}

	@Override
	public boolean hasDepth()
	{
		return true;
	}

	@Override
	public boolean isSideLit()
	{
		return true;
	}
}
