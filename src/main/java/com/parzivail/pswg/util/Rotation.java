package com.parzivail.pswg.util;

import com.parzivail.pswg.util.struct.Matrix4f;
import net.minecraft.util.math.EulerAngle;
import net.minecraft.util.math.Vec3d;

/**
 * Provides utilities to
 */
public class Rotation
{
	private final Matrix4f matrix;

	public Rotation()
	{
		this(new Matrix4f());
	}

	public Rotation(Matrix4f mat)
	{
		matrix = mat;
	}

	public Rotation(EulerAngle angles)
	{
		this(new Matrix4f());
		rotate(matrix, angles);
	}

	/**
	 * Rotate the matrix by the given angles in roll, pitch, yaw order
	 *
	 * @param matrix
	 * @param angles
	 */
	private void rotate(Matrix4f matrix, EulerAngle angles)
	{
		matrix.rotate(angles.getRoll() / 180 * MathUtil.PI, MathUtil.POSZ);
		matrix.rotate(angles.getPitch() / 180 * MathUtil.PI, MathUtil.POSX);
		matrix.rotate(angles.getYaw() / 180 * MathUtil.PI, MathUtil.POSY);
	}

	/**
	 * Yaw the matrix by the given amount
	 *
	 * @param rotateBy the angle in degrees
	 */
	public void rotateYaw(float rotateBy)
	{
		rotateLocal(MathUtil.POSY, rotateBy);
	}

	/**
	 * Pitch the matrix by the given amount
	 *
	 * @param rotateBy the angle in degrees
	 */
	public void rotatePitch(float rotateBy)
	{
		rotateLocal(MathUtil.POSX, rotateBy);
	}

	/**
	 * Roll the matrix by the given amount
	 *
	 * @param rotateBy the angle in degrees
	 */
	public void rotateRoll(float rotateBy)
	{
		rotateLocal(MathUtil.POSZ, rotateBy);
	}

	/**
	 * Yaw the matrix by the given amount with respect to the global axes
	 *
	 * @param rotateBy the angle in degrees
	 */
	public void rotateGlobalYaw(float rotateBy)
	{
		rotateGlobal(MathUtil.POSY, rotateBy);
	}

	/**
	 * Pitch the matrix by the given amount with respect to the global axes
	 *
	 * @param rotateBy the angle in degrees
	 */
	public void rotateGlobalPitch(float rotateBy)
	{
		rotateGlobal(MathUtil.POSX, rotateBy);
	}

	/**
	 * Roll the matrix by the given amount with respect to the global axes
	 *
	 * @param rotateBy the angle in degrees
	 */
	public void rotateGlobalRoll(float rotateBy)
	{
		rotateGlobal(MathUtil.POSZ, rotateBy);
	}

	/**
	 * Rotates the matrix around the given local axis
	 *
	 * @param rotateAround
	 * @param rotateBy
	 */
	public void rotateLocal(Vec3d rotateAround, float rotateBy)
	{
		matrix.rotate(rotateBy / 180 * MathUtil.PI, unproject(rotateAround));
	}

	/**
	 * Rotates the matrix around the given global axis
	 *
	 * @param rotateAround
	 * @param rotateBy
	 */
	public void rotateGlobal(Vec3d rotateAround, float rotateBy)
	{
		matrix.rotate(rotateBy / 180 * MathUtil.PI, rotateAround);
	}

	/**
	 * Converts a global vector to an equivalent local one
	 *
	 * @param in
	 */
	public Vec3d project(Vec3d in)
	{
		//Create a new matrix and use the first column to store the vector we are rotating
		Matrix4f mat = new Matrix4f();
		mat.m00 = (float)in.x;
		mat.m10 = (float)in.y;
		mat.m20 = (float)in.z;
		EulerAngle angles = toEulerAngles();
		//Do the rotations used to obtain this basis in reverse
		mat.rotate(-angles.getYaw() / 180 * MathUtil.PI, MathUtil.POSY);
		mat.rotate(-angles.getPitch() / 180 * MathUtil.PI, MathUtil.POSX);
		mat.rotate(-angles.getRoll() / 180 * MathUtil.PI, MathUtil.POSZ);
		return new Vec3d(mat.m00, mat.m10, mat.m20);
	}

	/**
	 * Converts a local vector to an equivalent global one
	 *
	 * @param in
	 */
	public Vec3d unproject(Vec3d in)
	{
		//Create a new matrix and use the first column to store the vector we are rotating
		Matrix4f mat = new Matrix4f();
		mat.m00 = (float)in.x;
		mat.m10 = (float)in.y;
		mat.m20 = (float)in.z;
		EulerAngle angles = toEulerAngles();
		//Do the rotations used to obtain this basis
		mat.rotate(angles.getRoll() / 180 * MathUtil.PI, MathUtil.POSZ);
		mat.rotate(angles.getPitch() / 180 * MathUtil.PI, MathUtil.POSX);
		mat.rotate(angles.getYaw() / 180 * MathUtil.PI, MathUtil.POSY);
		return new Vec3d(mat.m00, mat.m10, mat.m20);
	}

	/**
	 * Converts a local rotation to an equivalent global one
	 *
	 * @param in
	 */
	public Rotation unproject(Rotation in)
	{
		Matrix4f mat = new Matrix4f();
		mat.load(in.getMatrix());

		EulerAngle angles = toEulerAngles();
		rotate(mat, angles);

		return new Rotation(mat);
	}

	/**
	 * Gets the Euler angles of this object in roll, pitch, yaw order
	 *
	 * @return
	 */
	public EulerAngle toEulerAngles()
	{
		float yaw = (float)Math.atan2(matrix.m20, matrix.m00) / MathUtil.PI * 180;
		float pitch = (float)Math.atan2(-matrix.m10, Math.sqrt(matrix.m12 * matrix.m12 + matrix.m11 * matrix.m11)) / MathUtil.PI * 180;
		float roll = (float)Math.atan2(matrix.m12, matrix.m11) / MathUtil.PI * 180;
		return new EulerAngle(pitch, yaw, roll);
	}

	/**
	 * Gets the local vector along which the x-axis lies
	 *
	 * @return
	 */
	public Vec3d getXAxis()
	{
		return new Vec3d(matrix.m00, matrix.m10, matrix.m20).normalize();
	}

	/**
	 * Gets the local vector along which the y-axis lies
	 *
	 * @return
	 */
	public Vec3d getYAxis()
	{
		return new Vec3d(matrix.m01, matrix.m11, matrix.m21).normalize();
	}

	/**
	 * Gets the local vector along which the z-axis lies
	 *
	 * @return
	 */
	public Vec3d getZAxis()
	{
		return new Vec3d(-matrix.m02, -matrix.m12, -matrix.m22).normalize();
	}

	/**
	 * Gets the rotation matrix
	 *
	 * @return
	 */
	public Matrix4f getMatrix()
	{
		return matrix;
	}

	@Override
	public Rotation clone()
	{
		Rotation newAxes = new Rotation();
		newAxes.matrix.load(getMatrix());
		return newAxes;
	}

	@Override
	public String toString()
	{
		EulerAngle angle = toEulerAngles();
		return "RotatedAxes[Yaw = " + angle.getYaw() + ", Pitch = " + angle.getPitch() + ", Roll = " + angle.getRoll() + "]";
	}
}
