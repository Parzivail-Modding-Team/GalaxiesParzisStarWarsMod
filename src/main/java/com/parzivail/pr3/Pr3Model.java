package com.parzivail.pr3;

import com.google.common.io.LittleEndianDataInputStream;
import com.parzivail.brotli.BrotliInputStream;
import com.parzivail.swg.Resources;
import com.parzivail.swg.proxy.Client;
import com.parzivail.util.binary.BinaryUtil;
import com.parzivail.util.math.lwjgl.Matrix4f;
import com.parzivail.util.math.lwjgl.Vector3f;
import com.parzivail.util.ui.gltk.GL;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;

public class Pr3Model
{
	private static final String MAGIC = "PR3";
	private static int ACCEPTED_VERSION = 0x01;

	private static Pr3Shader shader;
	private static FloatBuffer buff = BufferUtils.createFloatBuffer(16);

	private final ArrayList<Pr3Object> objects;
	private final ResourceLocation texture;

	public Pr3Model(ArrayList<Pr3Object> objects, ResourceLocation texture)
	{
		this.objects = objects;
		this.texture = texture;
	}

	public static Pr3Model load(ResourceLocation location, ResourceLocation texture) throws IOException
	{
		IResource from = Client.mc.getResourceManager().getResource(location);
		BrotliInputStream bis = new BrotliInputStream(from.getInputStream());
		LittleEndianDataInputStream objStream = new LittleEndianDataInputStream(bis);

		byte[] identBytes = new byte[MAGIC.length()];
		int read = objStream.read(identBytes);
		String ident = new String(identBytes);
		if (!ident.equals(MAGIC) || read != identBytes.length)
			throw new IOException("Input file not PR3 model");

		int version = objStream.readInt();

		if (version != ACCEPTED_VERSION)
			throw new IOException(String.format("Input file version is 0x%s, expected 0x%s", Integer.toHexString(version), Integer.toHexString(ACCEPTED_VERSION)));

		int numObjects = objStream.readInt();

		ArrayList<Pr3Object> objects = new ArrayList<>();

		for (int i = 0; i < numObjects; i++)
		{
			String name = BinaryUtil.readNullTerminatedString(objStream);
			String materialName = BinaryUtil.readNullTerminatedString(objStream);

			Matrix4f transformationMatrix = BinaryUtil.readMatrix4(objStream);

			ArrayList<Vector3f> vertices = readLengthCodedVectors(objStream);
			ArrayList<Vector3f> normals = readLengthCodedVectors(objStream);
			ArrayList<Vector3f> uvs = readLengthCodedVectors(objStream);

			ArrayList<Pr3FacePointer> faces = readLengthCodedFaces(objStream);

			objects.add(new Pr3Object(name, vertices, faces, normals, uvs, transformationMatrix, materialName));
		}

		return new Pr3Model(objects, texture);
	}

	private static ArrayList<Vector3f> readLengthCodedVectors(LittleEndianDataInputStream objStream) throws IOException
	{
		int num = objStream.readInt();

		ArrayList<Vector3f> vectors = new ArrayList<>();

		for (int i = 0; i < num; i++)
		{
			float x = objStream.readFloat();
			float y = objStream.readFloat();
			float z = objStream.readFloat();

			vectors.add(new Vector3f(x, y, z));
		}

		return vectors;
	}

	private static ArrayList<Pr3FacePointer> readLengthCodedFaces(LittleEndianDataInputStream objStream) throws IOException
	{
		int num = objStream.readInt();

		ArrayList<Pr3FacePointer> faces = new ArrayList<>();

		for (int i = 0; i < num; i++)
		{
			int a = objStream.readInt();
			int b = objStream.readInt();
			int c = objStream.readInt();

			faces.add(new Pr3FacePointer(a, b, c));
		}

		return faces;
	}

	public void draw()
	{
		if (shader == null)
		{
			ResourceLocation rFrag = Resources.location("shaders/model.frag");
			ResourceLocation rVert = Resources.location("shaders/model.vert");

			try
			{
				IResource frag = Minecraft.getMinecraft().getResourceManager().getResource(rFrag);
				IResource vert = Minecraft.getMinecraft().getResourceManager().getResource(rVert);

				shader = new Pr3Shader(frag, vert);
			}
			catch (IOException e)
			{
				CrashReport crashreport = CrashReport.makeCrashReport(e, "Loading PR3 shaders");
				CrashReportCategory crashreportcategory = crashreport.makeCategory("Shaders being loaded");
				crashreportcategory.addCrashSection("Vertex", rFrag.toString());
				crashreportcategory.addCrashSection("Fragment", rVert.toString());
				throw new ReportedException(crashreport);
			}
		}

		GL.PushMatrix();

		GL.Rotate(180, 0, 1, 0);
		GL.Rotate(-90, 1, 0, 0);

		Client.mc.getTextureManager().bindTexture(texture);

		for (Pr3Object object : objects)
		{
			GL.PushMatrix();

			GL11.glMatrixMode(GL11.GL_MODELVIEW);
			buff.clear();
			object.transformationMatrix.store(buff);
			buff.flip();
			GL11.glMultMatrix(buff);

			switch (object.name)
			{
				case "WingTopLeft":
				case "WingBottomRight":
					GL.Rotate(Client.xwingDebug ? 13 : 0, 0, 1, 0);
					break;
				case "WingTopRight":
				case "WingBottomLeft":
					GL.Rotate(Client.xwingDebug ? -13 : 0, 0, 1, 0);
					break;
			}

			//			shader.use();
			object.vertexBuffer.render(GL11.GL_TRIANGLES);
			//			shader.release();
			GL.PopMatrix();
		}

		GL.PopMatrix();
	}
}
