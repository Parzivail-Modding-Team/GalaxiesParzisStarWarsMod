using System;
using System.Collections.Generic;
using OpenTK;

namespace PFX.Util
{
    public class VertexBufferInitializer
    {
        public List<Vector3> Vertices { get; }
        public List<Vector3> Normals { get; }
        public List<int> Colors { get; }
        public List<int> Indices { get; }

        public VertexBufferInitializer(List<Vector3> vertices, List<Vector3> normals, List<int> colors,
            List<int> indices)
        {
            Vertices = vertices;
            Normals = normals;
            Colors = colors;
            Indices = indices;
        }

        public VertexBufferInitializer()
        {
            Vertices = new List<Vector3>();
            Normals = new List<Vector3>();
            Colors = new List<int>();
            Indices = new List<int>();
        }

        public void AddVertex(Vector3 pos)
        {
            AddVertex(pos, Vector3.Zero);
        }

        public void AddVertex(Vector3 pos, Vector3 normal)
        {
            AddVertex(pos, normal, 0xFFFFFF);
        }

        public void AddVertex(Vector3 pos, Vector3 normal, int color)
        {
            Vertices.Add(pos);
            Normals.Add(normal);
            Colors.Add(color);
            Indices.Add(Indices.Count);
        }
    }
}