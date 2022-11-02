package com.parzivail.util.math;

import com.google.common.collect.Queues;
import net.minecraft.util.Util;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import java.util.Deque;

public class Transform
{
	public static final class State
	{
		final Matrix4f modelMatrix;

		State(Matrix4f matrix4f)
		{
			this.modelMatrix = matrix4f;
		}

		public Matrix4f getModel()
		{
			return this.modelMatrix;
		}
	}

	private final Deque<State> stack = Util.make(Queues.newArrayDeque(), (stack) -> {
		stack.add(new State(new Matrix4f()));
	});

	public void translate(double x, double y, double z)
	{
		this.stack.getLast().modelMatrix.translate((float)x, (float)y, (float)z);
	}

	public void scale(float x, float y, float z)
	{
		this.stack.getLast().modelMatrix.scale(x, y, z);
	}

	public void multiply(Matrix4f matrix4f)
	{
		this.stack.getLast().modelMatrix.mul(matrix4f);
	}

	public void multiply(Quaternionf quaternion)
	{
		this.stack.getLast().modelMatrix.rotate(quaternion);
	}

	public void save()
	{
		var state = this.stack.getLast();
		this.stack.addLast(new State(new Matrix4f(state.modelMatrix)));
	}

	public void restore()
	{
		this.stack.removeLast();
	}

	public State value()
	{
		return this.stack.getLast();
	}

	public boolean isEmpty()
	{
		return this.stack.size() == 1;
	}

	public void loadIdentity()
	{
		this.stack.getLast().modelMatrix.identity();
	}
}
