package com.parzivail.pr3;

import com.parzivail.util.ui.gltk.ShaderProgram;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.IResource;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.nio.FloatBuffer;

public class Pr3Shader extends ShaderProgram
{
	FloatBuffer matMv = GLAllocation.createDirectFloatBuffer(16);
	FloatBuffer matP = GLAllocation.createDirectFloatBuffer(16);

	private int locLightPos;
	private int locTexRandom;
	private int locTexModel;
	private int locMatMv;
	private int locMatP;

	public Pr3Shader(IResource fProg, IResource vProg)
	{
		super(fProg, vProg);
		getUniformLocations();
	}

	private void getUniformLocations()
	{
		GL20.glUseProgram(this.pgmId);

		locTexRandom = GL20.glGetUniformLocation(this.pgmId, "texRandom");
		locTexModel = GL20.glGetUniformLocation(this.pgmId, "texModel");

		locLightPos = GL20.glGetUniformLocation(this.pgmId, "lightPos");
		locMatMv = GL20.glGetUniformLocation(this.pgmId, "mv");
		locMatP = GL20.glGetUniformLocation(this.pgmId, "p");

		GL20.glUniform1i(locTexModel, 0);
		GL20.glUniform1i(locTexRandom, 1);

		GL20.glUseProgram(0);
	}

	@Override
	public void use()
	{
		GL20.glUseProgram(this.pgmId);

		GlStateManager.getFloat(GL11.GL_MODELVIEW_MATRIX, matMv);
		GlStateManager.getFloat(GL11.GL_PROJECTION_MATRIX, matP);

		GL20.glUniformMatrix4(locMatMv, true, matMv);
		GL20.glUniformMatrix4(locMatP, true, matP);

		GL20.glUniform3f(locLightPos, 1, 0, 0);
	}
}
