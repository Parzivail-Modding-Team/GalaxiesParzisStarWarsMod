package com.parzivail.util.math;

import com.google.common.collect.Queues;
import net.minecraft.util.Util;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;

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
		Matrix4f matrix4f = new Matrix4f();
		matrix4f.loadIdentity();
		stack.add(new State(matrix4f));
	});

	public void translate(double x, double y, double z)
	{
		this.stack.getLast().modelMatrix.multiplyByTranslation((float)x, (float)y, (float)z);
	}

	public void scale(float x, float y, float z)
	{
		this.stack.getLast().modelMatrix.multiply(Matrix4f.scale(x, y, z));
	}

	public void multiply(Matrix4f matrix4f)
	{
		this.stack.getLast().modelMatrix.multiply(matrix4f);
	}

	public void multiply(Quaternion quaternion)
	{
		this.stack.getLast().modelMatrix.multiply(quaternion);
	}

	public void save()
	{
		State state = this.stack.getLast();
		this.stack.addLast(new State(state.modelMatrix.copy()));
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
		this.stack.getLast().modelMatrix.loadIdentity();
	}
}
