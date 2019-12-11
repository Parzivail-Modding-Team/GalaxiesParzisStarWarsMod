package com.parzivail.util.ui.gltk;

import com.parzivail.util.common.Lumberjack;
import com.parzivail.util.math.lwjgl.Vector2f;
import com.parzivail.util.math.lwjgl.Vector3f;
import net.minecraft.client.renderer.OpenGlHelper;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.io.IOException;

public class VertexBuffer
{
	private final int _hint;

	private boolean initialized;
	private int numElements;

	private int vaoId = -1;

	private int elementBufferId = -1;
	private int texCoordBufferId = -1;
	private int vertexBufferId = -1;
	private int normalBufferId = -1;

	public VertexBuffer()
	{
		this(GL15.GL_STATIC_DRAW);
	}

	private VertexBuffer(int hint)
	{
		_hint = hint;
	}

	public void initialize(Vector3f[] vertices, Vector2f[] texCoords, Vector3f[] normals, int[] elements)
	{
		if (initialized)
		{
			GL30.glDeleteVertexArrays(vaoId);
			GL15.glDeleteBuffers(elementBufferId);
			GL15.glDeleteBuffers(texCoordBufferId);
			GL15.glDeleteBuffers(vertexBufferId);
			GL15.glDeleteBuffers(normalBufferId);
		}

		try
		{
			if (vaoId == -1)
				vaoId = GL30.glGenVertexArrays();

			GL30.glBindVertexArray(vaoId);

			int bufferSize;

			// Uv Array Buffer
			{
				// Generate Array Buffer Id
				if (texCoordBufferId == -1)
					texCoordBufferId = GL15.glGenBuffers();

				// Bind current context to Array Buffer ID
				GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, texCoordBufferId);

				// Send data to buffer
				GL15.glBufferData(GL15.GL_ARRAY_BUFFER, FloatBufferUtils.makeBuffer(texCoords), _hint);

				// Validate that the buffer is the correct size
				bufferSize = GL15.glGetBufferParameteri(GL15.GL_ARRAY_BUFFER, GL15.GL_BUFFER_SIZE);
				if (texCoords.length * Float.BYTES * 2 != bufferSize)
					throw new IOException("Uv array not uploaded correctly");

				// Clear the buffer Binding
				GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
			}

			// Normal Array Buffer
			{
				// Generate Array Buffer Id
				if (normalBufferId == -1)
					normalBufferId = GL15.glGenBuffers();

				// Bind current context to Array Buffer ID
				GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, normalBufferId);

				// Send data to buffer
				GL15.glBufferData(GL15.GL_ARRAY_BUFFER, FloatBufferUtils.makeBuffer(normals), _hint);

				// Validate that the buffer is the correct size
				bufferSize = GL15.glGetBufferParameteri(GL15.GL_ARRAY_BUFFER, GL15.GL_BUFFER_SIZE);
				if (normals.length * Float.BYTES * 3 != bufferSize)
					throw new IOException("Normal array not uploaded correctly");

				// Clear the buffer Binding
				GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
			}

			// Vertex Array Buffer
			{
				// Generate Array Buffer Id
				if (vertexBufferId == -1)
					vertexBufferId = GL15.glGenBuffers();

				// Bind current context to Array Buffer ID
				GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertexBufferId);

				// Send data to buffer
				GL15.glBufferData(GL15.GL_ARRAY_BUFFER, FloatBufferUtils.makeBuffer(vertices), _hint);

				// Validate that the buffer is the correct size
				bufferSize = GL15.glGetBufferParameteri(GL15.GL_ARRAY_BUFFER, GL15.GL_BUFFER_SIZE);
				if (vertices.length * Float.BYTES * 3 != bufferSize)
					throw new IOException("Vertex array not uploaded correctly");

				// Clear the buffer Binding
				GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
			}

			// Element Array Buffer
			{
				// Generate Array Buffer Id
				if (elementBufferId == -1)
					elementBufferId = GL15.glGenBuffers();

				// Bind current context to Array Buffer ID
				GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, elementBufferId);

				// Send data to buffer
				GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, IntBufferUtils.makeBuffer(elements), _hint);

				// Validate that the buffer is the correct size
				bufferSize = GL15.glGetBufferParameteri(GL15.GL_ELEMENT_ARRAY_BUFFER, GL15.GL_BUFFER_SIZE);
				if (elements.length * Integer.BYTES != bufferSize)
					throw new IOException("Element array not uploaded correctly");

				// Clear the buffer Binding
				GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
			}

			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertexBufferId);
			GL20.glEnableVertexAttribArray(0);
			GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0);

			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, normalBufferId);
			GL20.glEnableVertexAttribArray(1);
			GL20.glVertexAttribPointer(1, 3, GL11.GL_FLOAT, false, 0, 0);

			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, texCoordBufferId);
			GL20.glEnableVertexAttribArray(2);
			GL20.glVertexAttribPointer(2, 2, GL11.GL_FLOAT, false, 0, 0);

			GL30.glBindVertexArray(0);

			initialized = true;
		}
		catch (IOException ex)
		{
			Lumberjack.err("VertexBuffer %s failed to initialize", vaoId);
			ex.printStackTrace();
		}
		finally
		{
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		}

		// Store the number of elements for the DrawElements call
		numElements = elements.length;
	}

	public void render()
	{
		render(GL11.GL_QUADS);
	}

	public void render(int type)
	{
		GL30.glBindVertexArray(vaoId);
		OpenGlHelper.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, elementBufferId);
		GL11.glDrawElements(type, numElements, GL11.GL_INT, 0);
		OpenGlHelper.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		GL30.glBindVertexArray(0);
	}
}
