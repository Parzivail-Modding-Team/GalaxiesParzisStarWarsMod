using System;
using System.CodeDom;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.IO;
using System.Linq;
using System.Threading;
using MoonSharp.Interpreter;
using OpenTK;
using OpenTK.Graphics.OpenGL;
using OpenTK.Input;
using PFX;
using PFX.BmFont;

namespace TerrainBuilder
{
    public class WindowVisualize : GameWindow
    {
        private float _zoom = 1;
        private double _angle = 45;
        private double _angleY = 160;
        private bool _shouldDie;
        private readonly TerrainLayerList _terrainLayerList;
        private readonly HaltonSequence _halton = new HaltonSequence();

        private static int _stringHash;
        private string _scriptToLoad;
        private FileSystemWatcher _watcher;

        public static Vector3 UpVector = Vector3.UnitY;
        public static Vector3 PosXVector = Vector3.UnitX;
        public static Vector3 NegXVector = -PosXVector;
        public static Vector3 PosZVector = Vector3.UnitZ;
        public static Vector3 NegZVector = -PosZVector;

        public static int ListTerrain = 1;
        public static int ListDecor = 3;
        public static int ListBlock = 4;

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

            _terrainLayerList = new TerrainLayerList(this);
            _terrainLayerList.Show();
            Title = "TerrainViewer | Unsaved File";
        }

        private int _sideLength = 64;

        public int SideLength
        {
            get => _sideLength;
            set => _sideLength = (int) (value / 2f);
        }

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
                _terrainLayerList?.Close();
        }

        private void LoadHandler(object sender, EventArgs e)
        {
            float[] matDiffuse = { 1.0f, 1.0f, 1.0f };
            GL.Material(MaterialFace.FrontAndBack, MaterialParameter.AmbientAndDiffuse, matDiffuse);
            GL.Light(LightName.Light0, LightParameter.Position, new[] { 0.0f, 0.0f, 0.0f, 1.0f });
            GL.Light(LightName.Light0, LightParameter.Diffuse, new[] { 0.75f, 0.75f, 0.75f, 0.75f });

            GL.Enable(EnableCap.Lighting);
            GL.Enable(EnableCap.Light0);
            GL.Enable(EnableCap.ColorMaterial);
            GL.Enable(EnableCap.DepthTest);

            GL.NewList(ListBlock, ListMode.Compile);
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

        public void WatchTerrainScript(string filename)
        {
            filename = Path.GetFullPath(filename);
            // Create a new FileSystemWatcher and set its properties.
            _watcher?.Dispose();
            _watcher = new FileSystemWatcher
            {
                Path = Path.GetDirectoryName(filename),
                NotifyFilter = NotifyFilters.LastWrite | NotifyFilters.FileName | NotifyFilters.DirectoryName,
                Filter = Path.GetFileName(filename)
            };

            // Add event handlers.
            _watcher.Changed += OnChanged;
            _watcher.Deleted += OnDeleted;
            _watcher.Renamed += OnRenamed;

            // Begin watching.
            _watcher.EnableRaisingEvents = true;
            LoadScript(filename);
        }

        private void OnRenamed(object sender, RenamedEventArgs e)
        {
        }

        private void OnDeleted(object sender, FileSystemEventArgs e)
        {
        }

        private void OnChanged(object sender, FileSystemEventArgs e)
        {
            _scriptToLoad = e.FullPath;
        }

        private void LoadScript(string filename)
        {
            var fs = WaitForFile(filename, FileMode.Open, FileAccess.Read, FileShare.Read);
            if (fs == null)
            {
                Console.ForegroundColor = ConsoleColor.Red;
                Console.WriteLine("Failed to gain exclusive lock to read script!");
                Console.ResetColor();
                return;
            }

            string scriptCode;
            using (var reader = new StreamReader(fs))
                scriptCode = reader.ReadToEnd();

            if (scriptCode.GetHashCode() == _stringHash)
                return;

            Console.ForegroundColor = ConsoleColor.Green;
            Console.WriteLine($"Reloaded {filename}");
            Console.ResetColor();

            _stringHash = scriptCode.GetHashCode();
            var script = new Script { Options = { DebugPrint = Console.WriteLine } };

            _terrainLayerList.ScriptedTerrainGenerator.LoadScript(script, scriptCode);
            _terrainLayerList.ReRenderNoiseImage();
            ReRender();
        }

        static FileStream WaitForFile(string fullPath, FileMode mode, FileAccess access, FileShare share)
        {
            for (var numTries = 0; numTries < 10; numTries++)
            {
                FileStream fs = null;
                try
                {
                    fs = new FileStream(fullPath, mode, access, share);
                    return fs;
                }
                catch (IOException)
                {
                    fs?.Dispose();
                    Thread.Sleep(50);
                }
            }

            return null;
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

            if (_scriptToLoad != null)
            {
                LoadScript(_scriptToLoad);
                _scriptToLoad = null;
            }
        }

        public void ReRender()
        {
            if (_terrainLayerList == null)
                return;

            if (_terrainLayerList.rbHeightmap.Checked)
            {
                RenderHeightmap();
            }
            else if (_terrainLayerList.rbInterior.Checked)
            {
                RenderInterior();
            }
        }

        private void RenderInterior()
        {
            GL.NewList(ListTerrain, ListMode.Compile);
            GL.Translate(-8, -30, -8);
            for (var x = 0; x < 16; x++)
                for (var z = 0; z < 16; z++)
                {
                    var height = GetValueAt(x, z) + 60;
                    RenderBlock(x, 0, z, BlockLayer.Bedrock);
                    var finalHeight = (int)height;
                    for (var y = 1; y <= finalHeight; y++)
                    {
                        var thLayer2 = height * _terrainLayerList.ScriptedTerrainGenerator.ThLayer1;
                        var thLayer3 = height * _terrainLayerList.ScriptedTerrainGenerator.ThLayer2;

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

        private void RenderHeightmap()
        {
            GL.NewList(ListTerrain, ListMode.Compile);

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

                    if (!_terrainLayerList.cbBlockApproximation.Checked)
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

            ReDecorate(Heightmap);
        }

        private double Min(params double[] values)
        {
            return values.Min();
        }

        public void ReDecorate(double[,] heightmap = null)
        {
            if (heightmap == null)
                heightmap = Heightmap;
            GL.NewList(ListDecor, ListMode.Compile);

            _halton.Reset();
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
            return _terrainLayerList.cbBlockRounding.Checked ? (int)getValueAt : getValueAt;
        }

        private void PopulateChunk(float x, float z, double[,] heightmap)
        {
            var nx = x * 16 + SideLength + 1;
            var nz = z * 16 + SideLength + 1;
            for (var i = 0; i < _terrainLayerList.ScriptedTerrainGenerator.TreesPerChunk; i++)
            {
                var pos = _halton.Increment();
                pos *= 16;
                pos += new Vector3(nx, 0, nz);
                var val = heightmap[(int)pos.X, (int)pos.Z];
                if (val <= _terrainLayerList.ScriptedTerrainGenerator.WaterLevel && !_terrainLayerList.ScriptedTerrainGenerator.TreesBelowWaterLevel) continue;
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

            GL.PolygonMode(MaterialFace.FrontAndBack, _terrainLayerList.cbWireframe.Checked ? PolygonMode.Line : PolygonMode.Fill);

            GL.CallList(ListTerrain);
            GL.CallList(ListTerrain + 1);

            if (_terrainLayerList.rbHeightmap.Checked)
            {
                GL.CallList(ListDecor);

                GL.Color3(0, 70 / 255f, 200 / 255f);

                GL.Begin(PrimitiveType.TriangleStrip);
                GL.Normal3(UpVector);
                GL.Vertex3(-SideLength, _terrainLayerList.ScriptedTerrainGenerator.WaterLevel - 0.1, -SideLength);
                GL.Vertex3(SideLength, _terrainLayerList.ScriptedTerrainGenerator.WaterLevel - 0.1, -SideLength);
                GL.Vertex3(-SideLength, _terrainLayerList.ScriptedTerrainGenerator.WaterLevel - 0.1, SideLength);

                GL.Vertex3(SideLength, _terrainLayerList.ScriptedTerrainGenerator.WaterLevel - 0.1, -SideLength);
                GL.Vertex3(-SideLength, _terrainLayerList.ScriptedTerrainGenerator.WaterLevel - 0.1, SideLength);
                GL.Vertex3(SideLength, _terrainLayerList.ScriptedTerrainGenerator.WaterLevel - 0.1, SideLength);
                GL.End();
            }

            SwapBuffers();
        }

        public double GetValueAt(int x, int z)
        {
            var value = _terrainLayerList.ScriptedTerrainGenerator.GetValue(x, z);

            if (value < 0)
                value = 0;
            if (value > 255)
                value = 255;

            return value;
        }

        private void DrawBox(float size)
        {
            GL.PushMatrix();
            GL.Scale(size, size, size);
            GL.CallList(ListBlock);
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