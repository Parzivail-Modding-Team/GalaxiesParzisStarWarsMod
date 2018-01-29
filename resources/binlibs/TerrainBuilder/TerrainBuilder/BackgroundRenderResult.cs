using OpenTK;

namespace TerrainBuilder
{
    internal class BackgroundRenderResult
    {
        public Vector3[] Vertices { get; }
        public Vector3[] Normals { get; }
        public int[] Colors { get; }
        public int[] Indices { get; }

        public BackgroundRenderResult(Vector3[] vertices, Vector3[] normals, int[] colors, int[] indices)
        {
            Vertices = vertices;
            Normals = normals;
            Colors = colors;
            Indices = indices;
        }
    }
}