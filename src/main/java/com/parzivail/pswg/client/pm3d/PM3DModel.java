package com.parzivail.pswg.client.pm3d;

import com.parzivail.pswg.client.model.SimpleModel;
import com.parzivail.util.primative.Vector2f;
import com.parzivail.util.primative.Vector3f;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.ModelHelper;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.function.Function;

public class PM3DModel extends SimpleModel
{
	private final PM3DFile container;
	private final HashMap<String, Sprite> sprites = new HashMap<>();

	private PM3DModel(Sprite sprite, Function<SpriteIdentifier, Sprite> spriteMap, PM3DFile container)
	{
		super(sprite, ModelHelper.MODEL_TRANSFORM_BLOCK);
		this.container = container;

		for (HashMap.Entry<String, String> entry : container.textures.entrySet())
			sprites.put(entry.getKey(), spriteMap.apply(new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEX, new Identifier(entry.getValue()))));
	}

	private void emitFace(QuadEmitter quadEmitter, PM3DObject object, PM3DFace face)
	{
		quadEmitter.spriteColor(0, 0xffffffff, 0xffffffff, 0xffffffff, 0xffffffff).material(RENDERER.materialFinder().find()).colorIndex(1);

		PM3DVertPointer a = face.verts.get(0);
		PM3DVertPointer b = face.verts.get(1);
		PM3DVertPointer c = face.verts.get(2);
		PM3DVertPointer d = face.verts.size() == 4 ? face.verts.get(3) : c;

		Vector3f vA = container.verts.get(a.getVertex() - 1);
		Vector3f vB = container.verts.get(b.getVertex() - 1);
		Vector3f vC = container.verts.get(c.getVertex() - 1);
		Vector3f vD = container.verts.get(d.getVertex() - 1);

		Vector3f nA = container.normals.get(a.getNormal() - 1);
		Vector3f nB = container.normals.get(b.getNormal() - 1);
		Vector3f nC = container.normals.get(c.getNormal() - 1);
		Vector3f nD = container.normals.get(d.getNormal() - 1);

		Vector2f tA = container.uvs.get(a.getTexture() - 1);
		Vector2f tB = container.uvs.get(b.getTexture() - 1);
		Vector2f tC = container.uvs.get(c.getTexture() - 1);
		Vector2f tD = container.uvs.get(d.getTexture() - 1);

		quadEmitter.pos(0, vA.toMinecraft(16)).normal(0, nA.toMinecraft(16)).sprite(0, 0, tA.x, tA.y);
		quadEmitter.pos(1, vB.toMinecraft(16)).normal(1, nB.toMinecraft(16)).sprite(1, 0, tB.x, tB.y);
		quadEmitter.pos(2, vC.toMinecraft(16)).normal(2, nC.toMinecraft(16)).sprite(2, 0, tC.x, tC.y);
		quadEmitter.pos(3, vD.toMinecraft(16)).normal(3, nD.toMinecraft(16)).sprite(3, 0, tD.x, tD.y);

		quadEmitter.spriteBake(0, sprites.get(object.matName), MutableQuadView.BAKE_NORMALIZED);

		quadEmitter.emit();
	}

	@Override
	protected Mesh createMesh()
	{
		MeshBuilder meshBuilder = RENDERER.meshBuilder();
		QuadEmitter quadEmitter = meshBuilder.getEmitter();

		for (PM3DObject o : container.objects)
			for (PM3DFace face : o.faces)
				emitFace(quadEmitter, o, face);

		return meshBuilder.build();
	}

	public static PM3DModel create(Function<SpriteIdentifier, Sprite> spriteMap, PM3DFile container)
	{
		return new PM3DModel(spriteMap.apply(new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEX, new Identifier(container.textures.get("particle")))), spriteMap, container);
	}
}
