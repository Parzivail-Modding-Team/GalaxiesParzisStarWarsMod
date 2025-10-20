package dev.pswg.rendering.models;

import org.joml.Vector2f;
import org.joml.Vector3f;

public record GVertex(Vector3f position, Vector3f normal, Vector2f texCoords, int color, int overlay, int light)
{
}
