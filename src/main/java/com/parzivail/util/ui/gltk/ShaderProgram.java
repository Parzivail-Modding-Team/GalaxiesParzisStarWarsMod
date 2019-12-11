package com.parzivail.util.ui.gltk;

import com.parzivail.util.common.Lumberjack;
import com.parzivail.util.debug.Assert;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.resources.IResource;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public abstract class ShaderProgram
{
	private final IResource _fProg;
	private final IResource _vProg;
	protected int fsId;
	protected int pgmId;
	protected int vsId;

	public ShaderProgram(IResource fProg, IResource vProg)
	{
		_fProg = fProg;
		_vProg = vProg;
		pgmId = OpenGlHelper.glCreateProgram();

		try
		{
			init();
		}
		catch (IOException e)
		{
			Lumberjack.err("Shader %s failed to initialize", pgmId);
			e.printStackTrace();
		}
	}

	private void init() throws IOException
	{
		OpenGlHelper.glUseProgram(pgmId);

		fsId = loadShader(_fProg, OpenGlHelper.GL_FRAGMENT_SHADER);
		vsId = loadShader(_vProg, OpenGlHelper.GL_VERTEX_SHADER);

		OpenGlHelper.glLinkProgram(pgmId);

		boolean success = OpenGlHelper.glGetProgrami(pgmId, OpenGlHelper.GL_LINK_STATUS) == GL11.GL_TRUE;
		if (!success)
		{
			String s = StringUtils.trim(OpenGlHelper.glGetProgramInfoLog(pgmId, 32768));
			Lumberjack.debug("GLSL pgm %s: %s", pgmId, s);
			Assert.isTrue("Linking shader", success);
		}
	}

	public abstract void use();

	public void release()
	{
		OpenGlHelper.glUseProgram(0);
	}

	private int loadShader(IResource prog, int type) throws IOException
	{
		int address = OpenGlHelper.glCreateShader(type);

		byte[] abyte = IOUtils.toByteArray(new BufferedInputStream(prog.getInputStream()));
		ByteBuffer bytebuffer = BufferUtils.createByteBuffer(abyte.length);
		bytebuffer.put(abyte);
		bytebuffer.position(0);

		OpenGlHelper.glShaderSource(address, bytebuffer);
		OpenGlHelper.glCompileShader(address);
		OpenGlHelper.glAttachShader(pgmId, address);

		boolean success = OpenGlHelper.glGetShaderi(address, OpenGlHelper.GL_COMPILE_STATUS) == GL11.GL_TRUE;
		if (!success)
		{
			String s = StringUtils.trim(OpenGlHelper.glGetShaderInfoLog(address, 32768));
			Lumberjack.debug("GLSL shader %s (pgm %s): %s", pgmId, address, s);
			Assert.isTrue("Compiling shader program", success);
		}

		return address;
	}
}
