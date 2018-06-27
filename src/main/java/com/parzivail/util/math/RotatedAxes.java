package com.parzivail.util.math;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class RotatedAxes
{
	private float rotationYaw;
	private float rotationPitch;
	private float rotationRoll;
	private Matrix4f rotationMatrix;

	public RotatedAxes()
	{
		//Load identity
		rotationMatrix = new Matrix4f();
	}

	public RotatedAxes(Matrix4f mat)
	{
		rotationMatrix = mat;
		convertMatrixToAngles();
	}

	public RotatedAxes(float yaw, float pitch, float roll)
	{
		setAngles(yaw, pitch, roll);
	}

	@Override
	public RotatedAxes clone()
	{
		RotatedAxes newAxes = new RotatedAxes();
		newAxes.rotationMatrix.load(getMatrix());
		newAxes.convertMatrixToAngles();
		return newAxes;
	}

	public void setAngles(float yaw, float pitch, float roll)
	{
		rotationYaw = yaw;
		rotationPitch = pitch;
		rotationRoll = roll;
		convertAnglesToMatrix();
	}

	public float getYaw()
	{
		return rotationYaw;
	}

	public float getPitch()
	{
		return rotationPitch;
	}

	public float getRoll()
	{
		return rotationRoll;
	}

	public Vector3f getXAxis()
	{
		return new Vector3f(rotationMatrix.m00, rotationMatrix.m10, rotationMatrix.m20);
	}

	public Vector3f getYAxis()
	{
		return new Vector3f(rotationMatrix.m01, rotationMatrix.m11, rotationMatrix.m21);
	}

	public Vector3f getZAxis()
	{
		return new Vector3f(-rotationMatrix.m02, -rotationMatrix.m12, -rotationMatrix.m22);
	}

	public Matrix4f getMatrix()
	{
		return rotationMatrix;
	}

	//Rotate locally by some angle about the yaw axis
	public void rotateLocalYaw(float rotateBy)
	{
		rotationMatrix.rotate(rotateBy * 3.14159265F / 180F, getYAxis().normalise(null));
		convertMatrixToAngles();
	}

	//Rotate locally by some angle about the pitch axis
	public void rotateLocalPitch(float rotateBy)
	{
		rotationMatrix.rotate(rotateBy * 3.14159265F / 180F, getZAxis().normalise(null));
		convertMatrixToAngles();
	}

	//Rotate locally by some angle about the roll axis
	public void rotateLocalRoll(float rotateBy)
	{
		rotationMatrix.rotate(rotateBy * 3.14159265F / 180F, getXAxis().normalise(null));
		convertMatrixToAngles();
	}

	//Rotate globally by some angle about the yaw axis
	public RotatedAxes rotateGlobalYaw(float rotateBy)
	{
		rotationMatrix.rotate(rotateBy * 3.14159265F / 180F, new Vector3f(0F, 1F, 0F));
		convertMatrixToAngles();
		return this;
	}

	//Rotate globally by some angle about the pitch axis
	public RotatedAxes rotateGlobalPitch(float rotateBy)
	{
		rotationMatrix.rotate(rotateBy * 3.14159265F / 180F, new Vector3f(0F, 0F, 1F));
		convertMatrixToAngles();
		return this;
	}

	//Rotate globally by some angle about the roll axis
	public RotatedAxes rotateGlobalRoll(float rotateBy)
	{
		rotationMatrix.rotate(rotateBy * 3.14159265F / 180F, new Vector3f(1F, 0F, 0F));
		convertMatrixToAngles();
		return this;
	}

	//Rotate globally by some angle about the yaw axis
	public RotatedAxes rotateGlobalYawInRads(float rotateBy)
	{
		rotationMatrix.rotate(rotateBy, new Vector3f(0F, 1F, 0F));
		convertMatrixToAngles();
		return this;
	}

	//Rotate globally by some angle about the pitch axis
	public RotatedAxes rotateGlobalPitchInRads(float rotateBy)
	{
		rotationMatrix.rotate(rotateBy, new Vector3f(0F, 0F, 1F));
		convertMatrixToAngles();
		return this;
	}

	//Rotate globally by some angle about the roll axis
	public RotatedAxes rotateGlobalRollInRads(float rotateBy)
	{
		rotationMatrix.rotate(rotateBy, new Vector3f(1F, 0F, 0F));
		convertMatrixToAngles();
		return this;
	}

	//Rotate by some angle around some axis
	public void rotateLocal(float rotateBy, Vector3f rotateAround)
	{
		rotationMatrix.rotate(rotateBy * 3.14159265F / 180F, findLocalVectorGlobally(rotateAround));
		convertMatrixToAngles();
	}

	//Rotate by some angle around some axis
	public void rotateGlobal(float rotateBy, Vector3f rotateAround)
	{
		rotationMatrix.rotate(rotateBy * 3.14159265F / 180F, rotateAround);
		convertMatrixToAngles();
	}

	//Find a global vector in terms of this basis.
	public Vector3f findGlobalVectorLocally(Vector3f in)
	{
		//Create a new matrix and use the first column to store the vector we are rotating
		Matrix4f mat = new Matrix4f();
		mat.m00 = in.x;
		mat.m10 = in.y;
		mat.m20 = in.z;
		//Do the rotations used to obtain this basis in reverse
		mat.rotate(-rotationYaw * 3.14159265F / 180F, new Vector3f(0F, 1F, 0F));
		mat.rotate(-rotationPitch * 3.14159265F / 180F, new Vector3f(0F, 0F, 1F));
		mat.rotate(-rotationRoll * 3.14159265F / 180F, new Vector3f(1F, 0F, 0F));
		return new Vector3f(mat.m00, mat.m10, mat.m20);
	}

	//Find a local vector in terms of the global axes.
	public Vector3f findLocalVectorGlobally(Vector3f in)
	{
		//Create a new matrix and use the first column to store the vector we are rotating
		Matrix4f mat = new Matrix4f();
		mat.m00 = in.x;
		mat.m10 = in.y;
		mat.m20 = in.z;
		//Do the rotations used to obtain this basis
		mat.rotate(rotationRoll * 3.14159265F / 180F, new Vector3f(0F, 0F, 1F));
		mat.rotate(rotationPitch * 3.14159265F / 180F, new Vector3f(1F, 0F, 0F));
		mat.rotate(-rotationYaw * 3.14159265F / 180F, new Vector3f(0F, 1F, 0F));
		return new Vector3f(mat.m00, mat.m10, mat.m20);
	}

	private void convertAnglesToMatrix()
	{
		//Re-load the identity
		rotationMatrix = new Matrix4f();
		rotationMatrix.rotate(rotationRoll * 3.14159265F / 180F, new Vector3f(1F, 0F, 0F));
		rotationMatrix.rotate(rotationPitch * 3.14159265F / 180F, new Vector3f(0F, 0F, 1F));
		rotationMatrix.rotate(rotationYaw * 3.14159265F / 180F, new Vector3f(0F, 1F, 0F));
		convertMatrixToAngles();
	}

	private void convertMatrixToAngles()
	{
		rotationYaw = (float)Math.atan2(rotationMatrix.m20, rotationMatrix.m00) * 180F / 3.14159265F;
		rotationPitch = (float)Math.atan2(-rotationMatrix.m10, Math.sqrt(rotationMatrix.m12 * rotationMatrix.m12 + rotationMatrix.m11 * rotationMatrix.m11)) * 180F / 3.14159265F;
		rotationRoll = (float)Math.atan2(rotationMatrix.m12, rotationMatrix.m11) * 180F / 3.14159265F;
	}

	public RotatedAxes findLocalAxesGlobally(RotatedAxes in)
	{
		//Take the input matrix
		Matrix4f mat = new Matrix4f();
		mat.load(in.getMatrix());
		//Perform the rotations to convert from this local set of axes to the global axes
		mat.rotate(rotationRoll * 3.14159265F / 180F, new Vector3f(1F, 0F, 0F));
		mat.rotate(rotationPitch * 3.14159265F / 180F, new Vector3f(0F, 0F, 1F));
		mat.rotate(rotationYaw * 3.14159265F / 180F, new Vector3f(0F, 1F, 0F));
		//Return the globalised matrix
		return new RotatedAxes(mat);
	}

	@Override
	public String toString()
	{
		return "RotatedAxes[Yaw = " + getYaw() + ", Pitch = " + getPitch() + ", Roll = " + getRoll() + "]";
	}
}
