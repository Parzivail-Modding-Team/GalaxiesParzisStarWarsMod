package com.parzivail.aurek.model.p3di;

public record P3diMesh(String name, float[][] transform, String material, P3diFace[] faces, P3diMesh[] children)
{
}
