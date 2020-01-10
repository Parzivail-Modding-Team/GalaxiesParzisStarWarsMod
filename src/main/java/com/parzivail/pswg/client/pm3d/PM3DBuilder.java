package com.parzivail.pswg.client.pm3d;

import com.parzivail.util.primative.Vector2f;
import com.parzivail.util.primative.Vector3f;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;

public class PM3DBuilder
{
	//	public static final Sprite DEFAULT_SPRITE = MinecraftClient.getInstance().getSpriteAtlas().getSprite(SpriteAtlasTexture.BLOCK_ATLAS_TEX);

	public static Mesh build(PM3DContainer container)
	{
		MeshBuilder meshBuilder = RendererAccess.INSTANCE.getRenderer().meshBuilder();
		QuadEmitter quadEmitter = meshBuilder.getEmitter();

		for (PM3DObject o : container.objects)
			for (PM3DFace face : o.faces)
				emitFace(quadEmitter, container, o, face);

		return meshBuilder.build();
	}

	private static void emitFace(QuadEmitter quadEmitter, PM3DContainer container, PM3DObject object, PM3DFace face)
	{
		quadEmitter.spriteColor(0, -1, -1, -1, -1).material(RendererAccess.INSTANCE.getRenderer().materialFinder().find()).colorIndex(1);

		Pm3dVertPointer a = face.verts.get(0);
		Pm3dVertPointer b = face.verts.get(1);
		Pm3dVertPointer c = face.verts.get(2);

		Vector3f vA = container.verts.get(a.getVertex() - 1);
		Vector3f vB = container.verts.get(b.getVertex() - 1);
		Vector3f vC = container.verts.get(c.getVertex() - 1);

		Vector3f nA = container.normals.get(a.getNormal() - 1);
		Vector3f nB = container.normals.get(b.getNormal() - 1);
		Vector3f nC = container.normals.get(c.getNormal() - 1);

		Vector2f tA = container.uvs.get(a.getTexture() - 1);
		Vector2f tB = container.uvs.get(b.getTexture() - 1);
		Vector2f tC = container.uvs.get(c.getTexture() - 1);

		quadEmitter.pos(0, vA.toMinecraft()).normal(0, nA.toMinecraft()).sprite(0, 0, tA.x, tA.y);

		quadEmitter.pos(1, vB.toMinecraft()).normal(1, nB.toMinecraft()).sprite(1, 0, tB.x, tB.y);

		quadEmitter.pos(2, vC.toMinecraft()).normal(2, nC.toMinecraft()).sprite(2, 0, tC.x, tC.y);

		quadEmitter.pos(3, vC.toMinecraft()).normal(3, nC.toMinecraft()).sprite(3, 0, tC.x, tC.y);

		String spriteName = container.textures.get(object.matName);

		//		SpriteIdentifier spriteIdentifier;
		//		if (container.textures.containsKey(object.matName))
		//			spriteIdentifier = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEX, new Identifier(spriteName.toLowerCase()));
		//		else
		//			spriteIdentifier = new SpriteIdentifier(SpriteAtlasTexture.PARTICLE_ATLAS_TEX, MissingSprite.getMissingSpriteId());
		//
		//		quadEmitter.spriteBake(0, spriteIdentifier.getSprite(), MutableQuadView.BAKE_NORMALIZED);

		quadEmitter.emit();
	}
}
