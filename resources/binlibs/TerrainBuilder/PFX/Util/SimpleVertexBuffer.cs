using System;
using System.Collections.Generic;
using OpenTK;
using OpenTK.Graphics.OpenGL;

namespace PFX.Util
{
    public class SimpleVertexBuffer
    {
        private List<int> _colors = new List<int>();
        private List<int> _indices = new List<int>();
        private List<Vector3> _normals = new List<Vector3>();
        private List<Vector3> _vertices = new List<Vector3>();

        public int ColorBufferId;
        public int ElementBufferId;
        public int NormalBufferId;
        public int NumElements;
        public int VertexBufferId;

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
            _vertices.Add(pos);
            _normals.Add(normal);
            _colors.Add(color);
            _indices.Add(_indices.Count);
        }

        public void InitializeVbo()
        {
            InitializeVbo(_vertices.ToArray(), _normals.ToArray(), _colors.ToArray(), _indices.ToArray());
            _vertices = new List<Vector3>();
            _normals = new List<Vector3>();
            _indices = new List<int>();
            _colors = new List<int>();
        }

        public void InitializeVbo(VertexBufferInitializer vbi)
        {
            _vertices = vbi.Vertices;
            _normals = vbi.Normals;
            _indices = vbi.Indices;
            _colors = vbi.Colors;
            InitializeVbo();
        }

        public void InitializeVbo(Vector3[] vertices, Vector3[] vertexNormals, int[] vertexColors, int[] indices)
        {
            if (vertices == null) return;
            if (indices == null) return;

            int bufferSize;
            try
            {
                // Color Array Buffer
                if (vertexColors != null)
                {
                    // Generate Array Buffer Id
                    GL.GenBuffers(1, out ColorBufferId);

                    // Bind current context to Array Buffer ID
                    GL.BindBuffer(BufferTarget.ArrayBuffer, ColorBufferId);

                    // Send data to buffer
                    GL.BufferData(BufferTarget.ArrayBuffer, (IntPtr) (vertexColors.Length * sizeof(int)), vertexColors,
                        BufferUsageHint.StaticDraw);

                    // Validate that the buffer is the correct size
                    GL.GetBufferParameter(BufferTarget.ArrayBuffer, BufferParameterName.BufferSize, out bufferSize);
                    if (vertexColors.Length * sizeof(int) != bufferSize)
                        throw new ApplicationException("Vertex color array not uploaded correctly");

                    // Clear the buffer Binding
                    GL.BindBuffer(BufferTarget.ArrayBuffer, 0);
                }

                // Normal Array Buffer
                if (vertexNormals != null)
                {
                    // Generate Array Buffer Id
                    GL.GenBuffers(1, out NormalBufferId);

                    // Bind current context to Array Buffer ID
                    GL.BindBuffer(BufferTarget.ArrayBuffer, NormalBufferId);

                    // Send data to buffer
                    GL.BufferData(BufferTarget.ArrayBuffer, (IntPtr) (vertexNormals.Length * Vector3.SizeInBytes),
                        vertexNormals, BufferUsageHint.StaticDraw);

                    // Validate that the buffer is the correct size
                    GL.GetBufferParameter(BufferTarget.ArrayBuffer, BufferParameterName.BufferSize, out bufferSize);
                    if (vertexNormals.Length * Vector3.SizeInBytes != bufferSize)
                        throw new ApplicationException("Normal array not uploaded correctly");

                    // Clear the buffer Binding
                    GL.BindBuffer(BufferTarget.ArrayBuffer, 0);
                }

                // Vertex Array Buffer
                {
                    // Generate Array Buffer Id
                    GL.GenBuffers(1, out VertexBufferId);

                    // Bind current context to Array Buffer ID
                    GL.BindBuffer(BufferTarget.ArrayBuffer, VertexBufferId);

                    // Send data to buffer
                    GL.BufferData(BufferTarget.ArrayBuffer, (IntPtr) (vertices.Length * Vector3.SizeInBytes), vertices,
                        BufferUsageHint.DynamicDraw);

                    // Validate that the buffer is the correct size
                    GL.GetBufferParameter(BufferTarget.ArrayBuffer, BufferParameterName.BufferSize, out bufferSize);
                    if (vertices.Length * Vector3.SizeInBytes != bufferSize)
                        throw new ApplicationException("Vertex array not uploaded correctly");

                    // Clear the buffer Binding
                    GL.BindBuffer(BufferTarget.ArrayBuffer, 0);
                }

                // Element Array Buffer
                {
                    // Generate Array Buffer Id
                    GL.GenBuffers(1, out ElementBufferId);

                    // Bind current context to Array Buffer ID
                    GL.BindBuffer(BufferTarget.ElementArrayBuffer, ElementBufferId);

                    // Send data to buffer
                    GL.BufferData(BufferTarget.ElementArrayBuffer, (IntPtr) (indices.Length * sizeof(int)), indices,
                        BufferUsageHint.StaticDraw);

                    // Validate that the buffer is the correct size
                    GL.GetBufferParameter(BufferTarget.ElementArrayBuffer, BufferParameterName.BufferSize,
                        out bufferSize);
                    if (indices.Length * sizeof(int) != bufferSize)
                        throw new ApplicationException("Element array not uploaded correctly");

                    // Clear the buffer Binding
                    GL.BindBuffer(BufferTarget.ElementArrayBuffer, 0);
                }
            }
            catch (ApplicationException ex)
            {
                Lumberjack.Error($"{ex.Message}. Try re-rendering.");
            }

            // Store the number of elements for the DrawElements call
            NumElements = indices.Length;
        }

        public void Render(PrimitiveType type = PrimitiveType.Quads)
        {
            // Push current Array Buffer state so we can restore it later
            GL.PushClientAttrib(ClientAttribMask.ClientVertexArrayBit);

            if (VertexBufferId == 0) return;
            if (ElementBufferId == 0) return;

            // Normal Array Buffer
            if (NormalBufferId != 0)
            {
                // Bind to the Array Buffer ID
                GL.BindBuffer(BufferTarget.ArrayBuffer, NormalBufferId);

                // Set the Pointer to the current bound array describing how the data ia stored
                GL.NormalPointer(NormalPointerType.Float, Vector3.SizeInBytes, IntPtr.Zero);

                // Enable the client state so it will use this array buffer pointer
                GL.EnableClientState(ArrayCap.NormalArray);
            }

            // Color Array Buffer (Colors not used when lighting is enabled)
            if (ColorBufferId != 0)
            {
                // Bind to the Array Buffer ID
                GL.BindBuffer(BufferTarget.ArrayBuffer, ColorBufferId);

                // Set the Pointer to the current bound array describing how the data ia stored
                GL.ColorPointer(4, ColorPointerType.UnsignedByte, sizeof(int), IntPtr.Zero);

                // Enable the client state so it will use this array buffer pointer
                GL.EnableClientState(ArrayCap.ColorArray);
            }

            // Vertex Array Buffer
            {
                // Bind to the Array Buffer ID
                GL.BindBuffer(BufferTarget.ArrayBuffer, VertexBufferId);

                // Set the Pointer to the current bound array describing how the data ia stored
                GL.VertexPointer(3, VertexPointerType.Float, Vector3.SizeInBytes, IntPtr.Zero);

                // Enable the client state so it will use this array buffer pointer
                GL.EnableClientState(ArrayCap.VertexArray);
            }

            // Element Array Buffer
            {
                // Bind to the Array Buffer ID
                GL.BindBuffer(BufferTarget.ElementArrayBuffer, ElementBufferId);

                // Draw the elements in the element array buffer
                // Draws up items in the Color, Vertex, TexCoordinate, and Normal Buffers using indices in the ElementArrayBuffer
                GL.DrawElements(type, NumElements, DrawElementsType.UnsignedInt, IntPtr.Zero);

                // Could also call GL.DrawArrays which would ignore the ElementArrayBuffer and just use primitives
                // Of course we would have to reorder our data to be in the correct primitive order
            }

            // Restore the state
            GL.PopClientAttrib();
        }
    }
}