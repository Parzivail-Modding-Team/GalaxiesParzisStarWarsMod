using System;
using System.CodeDom;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Linq;
using System.Threading;
using OpenTK;
using OpenTK.Graphics.OpenGL;
using OpenTK.Input;

namespace TerrainBuilder
{
    public class WindowVisualize : GameWindow
    {
        private float _zoom = 1;
        private double _angle = 45;
        private double _angleY = 160;
        private bool _shouldDie;
        private TerrainLayerList terrainLayerList;
        private OpenSimplexNoise caveNoise = new OpenSimplexNoise(0);
        private HaltonSequence halton = new HaltonSequence();

        public static Vector3 UpVector = new Vector3(0, 1, 0);
        public static Vector3 PosXVector = new Vector3(1, 0, 0);
        public static Vector3 NegXVector = new Vector3(-1, 0, 0);
        public static Vector3 PosZVector = new Vector3(0, 0, 1);
        public static Vector3 NegZVector = new Vector3(0, 0, -1);

        public static int LIST_TERRAIN = 1;
        public static int LIST_DECOR = 3;
        public static int LIST_BLOCK = 4;

        public static double[,] Heightmap;

        public static float[,] BoxNormals = {
                {-1.0f, 0.0f, 0.0f},
                {0.0f, 1.0f, 0.0f},
                {1.0f, 0.0f, 0.0f},
                {0.0f, -1.0f, 0.0f},
                {0.0f, 0.0f, 1.0f},
                {0.0f, 0.0f, -1.0f}
            };
        public static int[,] BoxFaces = {
                {0, 1, 2, 3},
                {3, 2, 6, 7},
                {7, 6, 5, 4},
                {4, 5, 1, 0},
                {5, 6, 2, 1},
                {7, 4, 0, 3}
            };

        public WindowVisualize() : base(800, 600)
        {
            Load += LoadHandler;
            Closing += CloseHandler;
            Resize += ResizeHandler;
            UpdateFrame += UpdateHandler;
            RenderFrame += RenderHandler;

            MouseWheel += WindowVisualize_MouseWheel;

            terrainLayerList = new TerrainLayerList(this);
            terrainLayerList.Show();
            Title = "TerrainViewer | Unsaved File";
        }

        private int _sideLength = 64;

        public int SideLength
        {
            get { return _sideLength; }
            set { _sideLength = (int) (value/2f); }
        }
        public int WaterLevel { get; set; } = 0;

        private void WindowVisualize_MouseWheel(object sender, MouseWheelEventArgs e)
        {
            _zoom -= e.DeltaPrecise / 4f;

            if (_zoom < 0.1f)
                _zoom = 0.1f;
            if (_zoom > 10)
                _zoom = 10;
        }

        private void CloseHandler(object sender, CancelEventArgs e)
        {
            if (!_shouldDie)
                terrainLayerList?.Close();
        }

        private void LoadHandler(object sender, EventArgs e)
        {
            float[] mat_diffuse = { 1.0f, 1.0f, 1.0f };
            GL.Material(MaterialFace.FrontAndBack, MaterialParameter.AmbientAndDiffuse, mat_diffuse);
            GL.Light(LightName.Light0, LightParameter.Position, new[] { 0.0f, 0.0f, 0.0f, 1.0f });
            GL.Light(LightName.Light0, LightParameter.Diffuse, new[] { 0.75f, 0.75f, 0.75f, 0.75f });

            GL.Enable(EnableCap.Lighting);
            GL.Enable(EnableCap.Light0);
            GL.Enable(EnableCap.ColorMaterial);
            GL.Enable(EnableCap.DepthTest);

            GL.NewList(LIST_BLOCK, ListMode.Compile);
            var v = new float[8, 3];
            int i;

            v[0, 0] = v[1, 0] = v[2, 0] = v[3, 0] = -0.5f;
            v[4, 0] = v[5, 0] = v[6, 0] = v[7, 0] = 0.5f;
            v[0, 1] = v[1, 1] = v[4, 1] = v[5, 1] = -0.5f;
            v[2, 1] = v[3, 1] = v[6, 1] = v[7, 1] = 0.5f;
            v[0, 2] = v[3, 2] = v[4, 2] = v[7, 2] = -0.5f;
            v[1, 2] = v[2, 2] = v[5, 2] = v[6, 2] = 0.5f;

            GL.Begin(PrimitiveType.Quads);
            for (i = 5; i >= 0; i--)
            {
                GL.Normal3(ref BoxNormals[i, 0]);
                GL.Vertex3(ref v[BoxFaces[i, 0], 0]);
                GL.Vertex3(ref v[BoxFaces[i, 1], 0]);
                GL.Vertex3(ref v[BoxFaces[i, 2], 0]);
                GL.Vertex3(ref v[BoxFaces[i, 3], 0]);
            }
            GL.End();
            GL.EndList();

            ReRender();
        }

        private void ResizeHandler(object sender, EventArgs e)
        {
            GL.Viewport(ClientRectangle);

            var aspect_ratio = Width / (float)Height;
            var _projection = Matrix4.CreatePerspectiveFieldOfView(MathHelper.PiOver4, aspect_ratio, 1, 1024);
            GL.MatrixMode(MatrixMode.Projection);
            GL.LoadMatrix(ref _projection);
        }

        private void UpdateHandler(object sender, FrameEventArgs e)
        {
            if (_shouldDie)
                Exit();
        }

        public void ReRender()
        {
            if (terrainLayerList == null)
                return;

            if (terrainLayerList.rbHeightmap.Checked)
            {
                RenderHeightmap();
            }
            else if (terrainLayerList.rbInterior.Checked)
            {
                RenderInterior();
            }
        }

        private void RenderInterior()
        {
            GL.NewList(LIST_TERRAIN, ListMode.Compile);
            GL.Translate(-8, -30, -8);
            for (var x = 0; x < 16; x++)
                for (var z = 0; z < 16; z++)
                {
                    var height = GetValueAt(x, z) + 60;
                    RenderBlock(x, 0, z, BlockLayer.Bedrock);
                    var finalHeight = (int)height;
                    for (var y = 1; y <= finalHeight; y++)
                    {
                        //if (caveNoise.eval(x / (double)terrainLayerList.nudCaveScale.Value, y / (double)terrainLayerList.nudCaveScale.Value, z / (double)terrainLayerList.nudCaveScale.Value) > (double)terrainLayerList.nudCaveThreshold.Value)
                        //    continue;
                        var thLayer2 = height * (float)terrainLayerList.nudLayer1.Value;
                        var thLayer3 = height * (float)terrainLayerList.nudLayer2.Value;

                        if (y >= thLayer2)
                            RenderBlock(x, y, z, BlockLayer.Layer2);
                        else if (y >= thLayer3 && y < thLayer2)
                           RenderBlock(x, y, z, BlockLayer.Layer1);
                        else
                            RenderBlock(x, y, z, BlockLayer.Layer0);
                    }
                }
            GL.EndList();
        }

        private void RenderBlock(int x, int y, int z, BlockLayer blockLayer)
        {
            GL.PushMatrix();
            GL.Translate(x, y, z);
            switch (blockLayer)
            {
                case BlockLayer.Bedrock:
                    GL.Color3(Color.SlateGray);
                    break;
                case BlockLayer.Layer0:
                    GL.Color3(Color.Red);
                    break;
                case BlockLayer.Layer1:
                    GL.Color3(Color.Blue);
                    break;
                case BlockLayer.Layer2:
                    GL.Color3(Color.Green);
                    break;
                default:
                    throw new ArgumentOutOfRangeException(nameof(blockLayer), blockLayer, null);
            }
            DrawBox(1);
            GL.PopMatrix();
        }

        private void Blur(ref double[,] target, int w, int h, int r)
        {
            for (var x = 0; x < w; x++)
            for (var y = 0; y < h; y++)
            {
                double total = 0;
                for (var rx = -r / 2; rx < r / 2; rx++)
                for (var ry = -r / 2; ry < r / 2; ry++)
                    total += GetValueAt(x + rx, y + ry);
                target[x, y] = Floor(total / (r * r));
            }
        }

        private void RenderHeightmap()
        {
            GL.NewList(LIST_TERRAIN, ListMode.Compile);

            Heightmap = new double[2 * SideLength + 2, 2 * SideLength + 2];
            var colorMinY = double.MaxValue;
            var colorMaxY = double.MinValue;
            for (var x = 0; x < 2 * SideLength + 2; x++)
                for (var z = 0; z < 2 * SideLength + 2; z++)
                {
                    Heightmap[x, z] = Floor(GetValueAt(x, z));
                    colorMaxY = Math.Max(Heightmap[x, z], colorMaxY);
                    colorMinY = Math.Min(Heightmap[x, z], colorMinY);
                }
            var colorRangeY = colorMaxY - colorMinY;

            const int colorR = 0;
            const int colorG = 137;
            const int colorB = 18;
            GL.Begin(PrimitiveType.Quads);

            for (var x = -SideLength; x < SideLength; x++)
                for (var z = -SideLength; z < SideLength; z++)
                {
                    var nx = x + SideLength + 1;
                    var nz = z + SideLength + 1;
                    var valueA = new Vector3d(x, Heightmap[nx, nz], z);
                    var valueB = new Vector3d(x - 1, Heightmap[nx - 1, nz], z);
                    var valueC = new Vector3d(x, Heightmap[nx, nz - 1], z - 1);
                    var valueD = new Vector3d(x - 1, Heightmap[nx - 1, nz - 1], z - 1);

                    var colorMult = (valueA.Y - colorMinY) / colorRangeY;
                    GL.Color3(colorR, colorG / 255f + (255 - colorG) * colorMult / 255, colorB / 255f + (255 - colorB) * colorMult / 255);

                    if (!terrainLayerList.cbBlockApproximation.Checked)
                    {
                        var minY = Min(Heightmap[nx + 1, nz], Heightmap[nx, nz], Heightmap[nx - 1, nz], Heightmap[nx + 1, nz + 1], Heightmap[nx - 1, nz + 1], Heightmap[nx + 1, nz - 1], Heightmap[nx, nz - 1], Heightmap[nx - 1, nz - 1]);

                        GL.Normal3(UpVector);
                        GL.Vertex3(x, valueA.Y, z);
                        GL.Vertex3(x - 1, valueA.Y, z);
                        GL.Vertex3(x - 1, valueA.Y, z - 1);
                        GL.Vertex3(x, valueA.Y, z - 1);

                        GL.Normal3(PosZVector);
                        GL.Vertex3(x, valueA.Y, z);
                        GL.Vertex3(x, minY, z);
                        GL.Vertex3(x - 1, minY, z);
                        GL.Vertex3(x - 1, valueA.Y, z);

                        GL.Normal3(NegZVector);
                        GL.Vertex3(x, valueA.Y, z - 1);
                        GL.Vertex3(x, minY, z - 1);
                        GL.Vertex3(x - 1, minY, z - 1);
                        GL.Vertex3(x - 1, valueA.Y, z - 1);

                        GL.Normal3(PosXVector);
                        GL.Vertex3(x, valueA.Y, z);
                        GL.Vertex3(x, minY, z);
                        GL.Vertex3(x, minY, z - 1);
                        GL.Vertex3(x, valueA.Y, z - 1);

                        GL.Normal3(NegXVector);
                        GL.Vertex3(x - 1, valueA.Y, z);
                        GL.Vertex3(x - 1, minY, z);
                        GL.Vertex3(x - 1, minY, z - 1);
                        GL.Vertex3(x - 1, valueA.Y, z - 1);
                    }
                    else
                    {
                        var dirA = Vector3d.Cross(valueB - valueA, valueC - valueA);
                        var normA = Vector3d.Normalize(dirA);
                        GL.Normal3(-normA);
                        GL.Vertex3(valueA);
                        GL.Vertex3(valueB);
                        GL.Vertex3(valueD);
                        GL.Vertex3(valueC);
                    }
                }
            GL.End();

            GL.EndList();

            //if (terrainLayerList.cbRelaxedFeatures.Checked)
            //    RenderRelaxedHeightmap();

            ReDecorate(Heightmap);
        }

        private void RenderRelaxedHeightmap()
        {
            GL.NewList(LIST_TERRAIN + 1, ListMode.Compile);

            var target = new double[2 * SideLength + 2, 2 * SideLength + 2];
            Blur(ref target, 2 * SideLength + 2, 2 * SideLength + 2, 10);

            var colorMinY = double.MaxValue;
            var colorMaxY = double.MinValue;
            for (var x = 0; x < 2 * SideLength + 2; x++)
                for (var z = 0; z < 2 * SideLength + 2; z++)
                {
                    colorMaxY = Math.Max(target[x, z], colorMaxY);
                    colorMinY = Math.Min(target[x, z], colorMinY);
                }
            var colorRangeY = colorMaxY - colorMinY;

            const int colorR = 0;
            const int colorG = 18;
            const int colorB = 137;
            GL.Begin(PrimitiveType.Quads);

            for (var x = -SideLength; x < SideLength; x++)
                for (var z = -SideLength; z < SideLength; z++)
                {
                    var nx = x + SideLength + 1;
                    var nz = z + SideLength + 1;
                    var valueA = new Vector3d(x, target[nx, nz], z);
                    var valueB = new Vector3d(x - 1, target[nx - 1, nz], z);
                    var valueC = new Vector3d(x, target[nx, nz - 1], z - 1);
                    var valueD = new Vector3d(x - 1, target[nx - 1, nz - 1], z - 1);

                    var colorMult = (valueA.Y - colorMinY) / colorRangeY;
                    GL.Color3(colorR, colorG / 255f + (255 - colorG) * colorMult / 255, colorB / 255f + (255 - colorB) * colorMult / 255);

                    if (!terrainLayerList.cbBlockApproximation.Checked)
                    {
                        var minY = Min(target[nx + 1, nz], target[nx, nz], target[nx - 1, nz], target[nx + 1, nz + 1], target[nx - 1, nz + 1], target[nx + 1, nz - 1], target[nx, nz - 1], target[nx - 1, nz - 1]);

                        GL.Normal3(UpVector);
                        GL.Vertex3(x, valueA.Y, z);
                        GL.Vertex3(x - 1, valueA.Y, z);
                        GL.Vertex3(x - 1, valueA.Y, z - 1);
                        GL.Vertex3(x, valueA.Y, z - 1);

                        GL.Normal3(PosZVector);
                        GL.Vertex3(x, valueA.Y, z);
                        GL.Vertex3(x, minY, z);
                        GL.Vertex3(x - 1, minY, z);
                        GL.Vertex3(x - 1, valueA.Y, z);

                        GL.Normal3(NegZVector);
                        GL.Vertex3(x, valueA.Y, z - 1);
                        GL.Vertex3(x, minY, z - 1);
                        GL.Vertex3(x - 1, minY, z - 1);
                        GL.Vertex3(x - 1, valueA.Y, z - 1);

                        GL.Normal3(PosXVector);
                        GL.Vertex3(x, valueA.Y, z);
                        GL.Vertex3(x, minY, z);
                        GL.Vertex3(x, minY, z - 1);
                        GL.Vertex3(x, valueA.Y, z - 1);

                        GL.Normal3(NegXVector);
                        GL.Vertex3(x - 1, valueA.Y, z);
                        GL.Vertex3(x - 1, minY, z);
                        GL.Vertex3(x - 1, minY, z - 1);
                        GL.Vertex3(x - 1, valueA.Y, z - 1);
                    }
                    else
                    {
                        var dirA = Vector3d.Cross(valueB - valueA, valueC - valueA);
                        var normA = Vector3d.Normalize(dirA);
                        GL.Normal3(-normA);
                        GL.Vertex3(valueA);
                        GL.Vertex3(valueB);
                        GL.Vertex3(valueD);
                        GL.Vertex3(valueC);
                    }
                }
            GL.End();

            GL.EndList();

            ReDecorate(target);
        }

        private double Min(params double[] values)
        {
            return values.Min();
        }

        public void ReDecorate(double[,] heightmap = null)
        {
            if (heightmap == null)
                heightmap = Heightmap;
            GL.NewList(LIST_DECOR, ListMode.Compile);

            halton.Reset();
            GL.Color3(137 / 255f, 18 / 255f, 0);
            for (var x = -(float)(SideLength) / 16; x < (float)(SideLength) / 16; x++)
                for (var z = -(float)(SideLength) / 16; z < (float)(SideLength) / 16; z++)
                {
                    PopulateChunk(x, z, heightmap);
                }

            GL.EndList();
        }

        private double Floor(double getValueAt)
        {
            return terrainLayerList.cbBlockRounding.Checked ? (int)getValueAt : getValueAt;
        }

        private void PopulateChunk(float x, float z, double[,] heightmap)
        {
            var nx = x * 16 + SideLength + 1;
            var nz = z * 16 + SideLength + 1;
            for (var i = 0; i < terrainLayerList.nudTreesPerChunk.Value; i++)
            {
                halton.Increment();
                var pos = halton.MCurrentPos;
                pos *= 16;
                pos += new Vector3(nx, 0, nz);
                var val = heightmap[(int) pos.X, (int) pos.Z];
                if (val < WaterLevel && !terrainLayerList.cbSubmarineTrees.Checked) continue;
                GL.PushMatrix();
                GL.Translate((int)pos.X - SideLength - 1.5, val + 0.5, (int)pos.Z - SideLength - 1.5);
                DrawBox(1);
                GL.PopMatrix();
            }
        }

        private void RenderHandler(object sender, FrameEventArgs e)
        {
            GL.Clear(ClearBufferMask.ColorBufferBit |
                     ClearBufferMask.DepthBufferBit |
                     ClearBufferMask.StencilBufferBit);

            var lookat = Matrix4.LookAt(0, 128, 256, 0, 0, 0, 0, 1, 0);
            GL.MatrixMode(MatrixMode.Modelview);
            GL.LoadMatrix(ref lookat);

            GL.Translate(0, -25, 0);

            var delta = e.Time;

            if (Keyboard[Key.Left])
                _angle += 90 * delta;
            if (Keyboard[Key.Right])
                _angle -= 90 * delta;
            if (Keyboard[Key.Up])
                _angleY += 90 * delta;
            if (Keyboard[Key.Down])
                _angleY -= 90 * delta;

            var scale = new Vector3(4 * (1 / _zoom), -4 * (1 / _zoom), 4 * (1 / _zoom));
            GL.Scale(scale);
            GL.Rotate(_angleY, 1.0f, 0.0f, 0.0f);
            GL.Rotate(_angle, 0.0f, 1.0f, 0.0f);

            GL.PolygonMode(MaterialFace.FrontAndBack, terrainLayerList.cbWireframe.Checked ? PolygonMode.Line : PolygonMode.Fill);

            GL.CallList(LIST_TERRAIN);
            GL.CallList(LIST_TERRAIN + 1);

            if (terrainLayerList.rbHeightmap.Checked)
            {
                GL.CallList(LIST_DECOR);

                GL.Color3(0, 70/255f, 200/255f);

                GL.Begin(PrimitiveType.TriangleStrip);
                GL.Normal3(UpVector);
                GL.Vertex3(-SideLength, WaterLevel - 0.1, -SideLength);
                GL.Vertex3(SideLength, WaterLevel - 0.1, -SideLength);
                GL.Vertex3(-SideLength, WaterLevel - 0.1, SideLength);

                GL.Vertex3(SideLength, WaterLevel - 0.1, -SideLength);
                GL.Vertex3(-SideLength, WaterLevel - 0.1, SideLength);
                GL.Vertex3(SideLength, WaterLevel - 0.1, SideLength);
                GL.End();
            }

            SwapBuffers();
        }

        private double GetValueAt(int x, int z)
        {
            var value = 0d;
            foreach (var terrainLayer in terrainLayerList.Layers)
            {
                var tval = terrainLayer.GetValue(x, z);
                switch (terrainLayer.Method)
                {
                    case Method.Add:
                        value += tval;
                        break;
                    case Method.Subtract:
                        value -= tval;
                        break;
                    case Method.Multiply:
                        value *= tval;
                        break;
                    default:
                        throw new ArgumentOutOfRangeException();
                }
            }

            if (value < -60)
                value = -60;
            if (value > 195)
                value = 195;

            return value;
        }

        private void DrawBox(float size)
        {
            GL.PushMatrix();
            GL.Scale(size, size, size);
            GL.CallList(LIST_BLOCK);
            GL.PopMatrix();
        }

        public void Kill()
        {
            _shouldDie = true;
        }
    }

    internal enum BlockLayer
    {
        Bedrock,
        Layer0,
        Layer1,
        Layer2
    }
}