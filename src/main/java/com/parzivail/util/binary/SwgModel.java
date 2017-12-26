package com.parzivail.util.binary;

import com.google.common.io.LittleEndianDataInputStream;
import com.parzivail.util.common.Lumberjack;
import com.parzivail.util.ui.gltk.GL;
import com.parzivail.util.ui.gltk.PrimitiveType;
import com.sun.media.sound.InvalidDataException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import java.io.InputStream;
import java.util.HashMap;

/**
 * Created by colby on 12/25/2017.
 */
public class SwgModel
{
	public int version;
	public String modelName;
	public int flags;
	public int animationLength;
	public SwgTag[] tags;
	public SwgPart[] parts;
	/**
	 * PartLists.get("name")[frame] = list
	 */
	public HashMap<String, Integer[]> partRenderLists;

	public SwgModel(int version, String modelName, int flags, int animationLength, SwgTag[] tags, SwgPart[] parts)
	{
		this.version = version;
		this.modelName = modelName;
		this.flags = flags;
		this.animationLength = animationLength;
		this.tags = tags;
		this.parts = parts;
		BuildRenderLists();
	}

	private void BuildRenderLists()
	{
		partRenderLists = new HashMap<>();
		for (int i = 0; i < parts.length; i++)
		{
			Integer[] frameLists = new Integer[animationLength];
			for (int frame = 0; frame < animationLength; frame++)
			{
				int list = GL11.glGenLists(1);
				frameLists[frame] = list;
				GL11.glNewList(list, GL11.GL_COMPILE);
				renderPart(parts[i], frame);
				GL.EndList();
			}
			partRenderLists.put(parts[i].name, frameLists);
		}
		Lumberjack.log("Built %s part lists each with %s frames (%s total)", parts.length, animationLength, parts.length * animationLength);
	}

	public static void renderPart(SwgPart part, int frame)
	{
		GL.Begin(PrimitiveType.Triangles);
		for (int j = 0; j < part.triangles.length; j++)
		{
			FacePointer face = part.triangles[j];

			GL.Normal3(part.verts[frame][face.a].normal);
			GL.TexCoord2(part.verts[frame][face.a].textureCoord);
			GL.Vertex3(part.verts[frame][face.a].position);

			GL.Normal3(part.verts[frame][face.b].normal);
			GL.TexCoord2(part.verts[frame][face.b].textureCoord);
			GL.Vertex3(part.verts[frame][face.b].position);

			GL.Normal3(part.verts[frame][face.c].normal);
			GL.TexCoord2(part.verts[frame][face.c].textureCoord);
			GL.Vertex3(part.verts[frame][face.c].position);
		}
		GL.End();
	}

	public static SwgModel Load(ResourceLocation filename)
	{
		try
		{
			IResource res = Minecraft.getMinecraft().getResourceManager().getResource(filename);
			InputStream fs = res.getInputStream();
			LittleEndianDataInputStream s = new LittleEndianDataInputStream(fs);

			byte[] identr = new byte[4];
			int read = s.read(identr);
			String ident = new String(identr);
			if (!ident.equals("SWG3") || read != identr.length)
				throw new InvalidDataException("Input file not SWG3 model");

			int version = s.readInt();
			String modelName = PIO.readNullTerminatedString(s);
			int flags = s.readInt();
			int animationLength = s.readInt();
			int numTags = s.readInt();
			int numParts = s.readInt();

			SwgTag[] tags = new SwgTag[numTags];
			SwgPart[] parts = new SwgPart[numParts];

			for (int i = 0; i < numTags; i++)
			{
				String tagName = PIO.readNullTerminatedString(s);
				double tagX = s.readFloat();
				double tagY = s.readFloat();
				double tagZ = s.readFloat();
				tags[i] = new SwgTag(tagName, new Vector3f((float)tagX, (float)tagY, (float)tagZ));
			}

			for (int i = 0; i < numParts; i++)
			{
				String partName = PIO.readNullTerminatedString(s);
				int partFlags = s.readInt();
				int numTextures = s.readInt();
				int numTriangles = s.readInt();
				int numVerts = s.readInt();

				SwgTexture[] textures = new SwgTexture[numTextures];
				FacePointer[] triangles = new FacePointer[numTriangles];
				SwgSt[] stPairs = new SwgSt[numVerts];
				Vertex[][] verts = new Vertex[animationLength][numVerts];

				for (int j = 0; j < numTextures; j++)
				{
					String textureName = PIO.readNullTerminatedString(s);
					textures[j] = new SwgTexture(textureName);
				}

				for (int j = 0; j < numTriangles; j++)
				{
					int a = s.readInt();
					int b = s.readInt();
					int c = s.readInt();
					triangles[j] = new FacePointer(a, b, c);
				}

				for (int j = 0; j < numVerts; j++)
				{
					float a = s.readFloat();
					float b = s.readFloat();
					stPairs[j] = new SwgSt(a, b);
				}

				for (int frame = 0; frame < animationLength; frame++)
				{
					for (int j = 0; j < numVerts; j++)
					{
						double vertX = s.readFloat();
						double vertY = s.readFloat();
						double vertZ = s.readFloat();
						byte azimuth = s.readByte();
						byte zenith = s.readByte();

						double lat = zenith * (2 * Math.PI) / 255;
						double lng = azimuth * (2 * Math.PI) / 255;

						verts[frame][j] = new Vertex(new Vector3f((float)vertX, (float)vertY, (float)vertZ), new Vector3f((float)(Math.cos(lng) * Math.sin(lat)), (float)(Math.sin(lng) * Math.sin(lat)), (float)Math.cos(lat)), new Vector2f(stPairs[j].s, stPairs[j].t));
					}
				}

				parts[i] = new SwgPart(partName, partFlags, textures, triangles, stPairs, verts);
			}

			s.close();
			fs.close();
			return new SwgModel(version, modelName, flags, animationLength, tags, parts);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
}
