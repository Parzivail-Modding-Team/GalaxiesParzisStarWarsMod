package com.parzivail.pm3d;

import java.util.Objects;

public class Pm3dModelObjectInfo
{
	public final String objectName;
	public final String materialName;

	public Pm3dModelObjectInfo(String objectName, String materialName)
	{
		this.objectName = objectName;
		this.materialName = materialName;
	}

	@Override
	public int hashCode()
	{
		return ((objectName != null ? objectName.hashCode() : 0) * 397) ^ (materialName != null ? materialName.hashCode() : 0);
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof Pm3dModelObjectInfo))
			return false;
		Pm3dModelObjectInfo other = (Pm3dModelObjectInfo)obj;
		return Objects.equals(this.objectName, other.objectName) && Objects.equals(this.materialName, other.materialName);
	}
}
