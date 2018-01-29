using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Linq;
using System.Windows.Forms;
using OpenTK;
using OpenTK.Graphics.OpenGL;
using OpenTK.Input;

namespace TerrainBuilder
{
    public class WindowVisualize : GameWindow
    {
        /*
         * Constants
         */
        public static Vector3 UpVector = Vector3.UnitY;
        public static Vector3 PosXVector = Vector3.UnitX;
        public static Vector3 NegXVector = -PosXVector;
        public static Vector3 PosZVector = Vector3.UnitZ;
        public static Vector3 NegZVector = -PosZVector;

        /*
         * Script-related
         */
        private bool _dirty;
        private readonly ScriptWatcher _scriptWatcher = new ScriptWatcher();

        /*
         * Render-related
         */
        private float _zoom = 1;
        private double _angle = 45;
        private double _angleY = 160;
        public static int ListDecor;
        public static int ListBlock;
        private readonly HaltonSequence _halton = new HaltonSequence();
        private readonly SimpleVertexBuffer _tvbo = new SimpleVertexBuffer();
        private readonly BackgroundWorker _backgroundRenderer = new BackgroundWorker();

        /*
         * Terrain-related
         */
        private int _sideLength = 64;
        public int SideLength
        {
            get => _sideLength;
            set => _sideLength = (int)(value / 2f);
        }
        public static double[,] Heightmap;
        private readonly TerrainLayerList _terrainLayerList;

        /*
         * Window-related
         */
        private bool _shouldDie;

        public WindowVisualize() : base(800, 600)
        {
            // Wire up window
            Load += LoadHandler;
            Closing += CloseHandler;
            Resize += ResizeHandler;
            UpdateFrame += UpdateHandler;
            RenderFrame += RenderHandler;
            MouseWheel += WindowVisualize_MouseWheel;

            // Wire up background worker
            _backgroundRenderer.WorkerReportsProgress = true;
            _backgroundRenderer.WorkerSupportsCancellation = true;
            _backgroundRenderer.DoWork += DoBackgroundRender;
            _backgroundRenderer.ProgressChanged += DoBackgroundRenderProgress;
            _backgroundRenderer.RunWorkerCompleted += DoBackgroundRenderComplete;

            // Wire up file watcher
            _scriptWatcher.FileChanged += ScriptWatcherOnFileChanged;

            // Load UI window
            _terrainLayerList = new TerrainLayerList(this);
            _terrainLayerList.Show();
            Title = "TerrainViewer | Unsaved File";
        }

        private static void LoadHandler(object sender, EventArgs e)
        {
            const float diffuse = 0.65f;
            float[] matDiffuse = { diffuse, diffuse, diffuse };
            GL.Material(MaterialFace.FrontAndBack, MaterialParameter.AmbientAndDiffuse, matDiffuse);
            GL.Light(LightName.Light0, LightParameter.Position, new[] { 0.0f, 0.0f, 0.0f, 1.0f });
            GL.Light(LightName.Light0, LightParameter.Diffuse, new[] { diffuse, diffuse, diffuse, diffuse });

            GL.Enable(EnableCap.Lighting);
            GL.Enable(EnableCap.Light0);
            GL.ShadeModel(ShadingModel.Smooth);
            GL.Enable(EnableCap.ColorMaterial);
            GL.Enable(EnableCap.DepthTest);
            GL.Enable(EnableCap.RescaleNormal);

            GL.ClearColor(Color.FromArgb(255, 13, 13, 13));

            Lumberjack.Info("Window loaded");
        }

        private void WindowVisualize_MouseWheel(object sender, MouseWheelEventArgs e)
        {
            _zoom -= e.DeltaPrecise / 4f;

            if (_zoom < 0.5f)
                _zoom = 0.5f;
            if (_zoom > 20)
                _zoom = 20;
        }

        private void CloseHandler(object sender, CancelEventArgs e)
        {
            if (!_shouldDie)
                _terrainLayerList?.Close();
        }

        public void Kill()
        {
            _shouldDie = true;
        }

        private void ResizeHandler(object sender, EventArgs e)
        {
            GL.Viewport(ClientRectangle);

            var aspectRatio = Width / (float)Height;
            var projection = Matrix4.CreatePerspectiveFieldOfView(MathHelper.PiOver4, aspectRatio, 1, 1024);
            GL.MatrixMode(MatrixMode.Projection);
            GL.LoadMatrix(ref projection);
        }

        public void WatchTerrainScript(string filename)
        {
            _scriptWatcher.WatchTerrainScript(filename);
        }

        private void ScriptWatcherOnFileChanged(object sender, ScriptChangedEventArgs e)
        {
            Lumberjack.Info($"Reloaded {e.Filename}");
            _terrainLayerList.ScriptedTerrainGenerator.LoadScript(e.Script, e.ScriptCode);
            _dirty = true;
        }

        public bool IsRendering()
        {
            return _backgroundRenderer.IsBusy;
        }

        public void CancelRender()
        {
            Lumberjack.Warn("Cancelling previous render operation");
            _backgroundRenderer.CancelAsync();

            while (IsRendering())
                Application.DoEvents();
        }

        public void ReRender()
        {
            if (IsRendering())
                CancelRender();

            _backgroundRenderer.RunWorkerAsync();
        }

        private void DoBackgroundRenderProgress(object sender, ProgressChangedEventArgs progressChangedEventArgs)
        {
            _terrainLayerList.pbRender.Value = progressChangedEventArgs.ProgressPercentage;
        }

        private void DoBackgroundRenderComplete(object sender, RunWorkerCompletedEventArgs e)
        {
            _terrainLayerList.pbRender.Value = 0;

            if (_scriptWatcher.GetScriptId() == 0 || e.Cancelled)
                return;

            var result = (BackgroundRenderResult)e.Result;
            _tvbo.InitializeVbo(result.Vertices, result.Normals, result.Colors, result.Indices);

            ReDecorate(Heightmap);
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

        private void DoBackgroundRender(object sender, DoWorkEventArgs e)
        {
            if (_scriptWatcher.GetScriptId() == 0)
                return;

            var worker = (BackgroundWorker)sender;

            Heightmap = new double[2 * SideLength + 2, 2 * SideLength + 2];
            var globalMinY = double.MaxValue;
            var globalMaxY = double.MinValue;
            for (var x = 0; x < 2 * SideLength + 2; x++)
            {
                for (var z = 0; z < 2 * SideLength + 2; z++)
                {
                    if (worker.CancellationPending)
                    {
                        e.Cancel = true;
                        return;
                    }

                    Heightmap[x, z] = (int)GetValueAt(x, z);
                    globalMaxY = Math.Max(Heightmap[x, z], globalMaxY);
                    globalMinY = Math.Min(Heightmap[x, z], globalMinY);
                }

                worker.ReportProgress((int)(x / (2f * SideLength + 2) * 50));
            }

            var globalRangeY = globalMaxY - globalMinY;

            var vertices = new List<Vector3>();
            var normals = new List<Vector3>();
            var indices = new List<int>();
            var colors = new List<int>();
            var i = 0;

            for (var x = -SideLength; x < SideLength; x++)
            {
                for (var z = -SideLength; z < SideLength; z++)
                {
                    if (worker.CancellationPending)
                    {
                        e.Cancel = true;
                        return;
                    }

                    var nx = x + SideLength + 1;
                    var nz = z + SideLength + 1;
                    var valueA = new Vector3d(x, Heightmap[nx, nz], z);

                    var colorMult = (float)((valueA.Y - globalMinY) / globalRangeY);
                    var color = Util.RgbToInt(colorMult * 0.25f, 0.25f * colorMult + 0.5f, colorMult * 0.25f);

                    var minY = Util.Min(
                        Heightmap[nx + 1, nz],
                        Heightmap[nx, nz],
                        Heightmap[nx - 1, nz],
                        Heightmap[nx + 1, nz + 1],
                        Heightmap[nx - 1, nz + 1],
                        Heightmap[nx + 1, nz - 1],
                        Heightmap[nx, nz - 1],
                        Heightmap[nx - 1, nz - 1]
                    );

                    vertices.Add(new Vector3(x, (float)valueA.Y, z));
                    normals.Add(UpVector);
                    colors.Add(color);
                    vertices.Add(new Vector3(x - 1, (float)valueA.Y, z));
                    normals.Add(UpVector);
                    colors.Add(color);
                    vertices.Add(new Vector3(x - 1, (float)valueA.Y, z - 1));
                    normals.Add(UpVector);
                    colors.Add(color);
                    vertices.Add(new Vector3(x, (float)valueA.Y, z - 1));
                    normals.Add(UpVector);
                    colors.Add(color);
                    indices.AddRange(new List<int> { i++, i++, i++, i++ });

                    vertices.Add(new Vector3(x, (float)valueA.Y, z));
                    normals.Add(PosZVector);
                    colors.Add(color);
                    vertices.Add(new Vector3(x, (float)minY, z));
                    normals.Add(PosZVector);
                    colors.Add(color);
                    vertices.Add(new Vector3(x - 1, (float)minY, z));
                    normals.Add(PosZVector);
                    colors.Add(color);
                    vertices.Add(new Vector3(x - 1, (float)valueA.Y, z));
                    normals.Add(PosZVector);
                    colors.Add(color);
                    indices.AddRange(new List<int> { i++, i++, i++, i++ });

                    vertices.Add(new Vector3(x, (float)valueA.Y, z - 1));
                    normals.Add(NegZVector);
                    colors.Add(color);
                    vertices.Add(new Vector3(x, (float)minY, z - 1));
                    normals.Add(NegZVector);
                    colors.Add(color);
                    vertices.Add(new Vector3(x - 1, (float)minY, z - 1));
                    normals.Add(NegZVector);
                    colors.Add(color);
                    vertices.Add(new Vector3(x - 1, (float)valueA.Y, z - 1));
                    normals.Add(NegZVector);
                    colors.Add(color);
                    indices.AddRange(new List<int> { i++, i++, i++, i++ });

                    vertices.Add(new Vector3(x, (float)valueA.Y, z));
                    normals.Add(PosXVector);
                    colors.Add(color);
                    vertices.Add(new Vector3(x, (float)minY, z));
                    normals.Add(PosXVector);
                    colors.Add(color);
                    vertices.Add(new Vector3(x, (float)minY, z - 1));
                    normals.Add(PosXVector);
                    colors.Add(color);
                    vertices.Add(new Vector3(x, (float)valueA.Y, z - 1));
                    normals.Add(PosXVector);
                    colors.Add(color);
                    indices.AddRange(new List<int> { i++, i++, i++, i++ });

                    vertices.Add(new Vector3(x - 1, (float)valueA.Y, z));
                    normals.Add(NegXVector);
                    colors.Add(color);
                    vertices.Add(new Vector3(x - 1, (float)minY, z));
                    normals.Add(NegXVector);
                    colors.Add(color);
                    vertices.Add(new Vector3(x - 1, (float)minY, z - 1));
                    normals.Add(NegXVector);
                    colors.Add(color);
                    vertices.Add(new Vector3(x - 1, (float)valueA.Y, z - 1));
                    normals.Add(NegXVector);
                    colors.Add(color);
                    indices.AddRange(new List<int> { i++, i++, i++, i++ });
                }

                worker.ReportProgress((int)((x + SideLength) / (SideLength * 2f) * 50) + 50);
            }

            e.Result = new BackgroundRenderResult(vertices.ToArray(), normals.ToArray(), colors.ToArray(),
                indices.ToArray());
        }

        private void UpdateHandler(object sender, FrameEventArgs e)
        {
            if (_shouldDie)
                Exit();

            if (_dirty)
            {
                _terrainLayerList.ReRenderNoiseImage();
                ReRender();
                _dirty = false;
            }

            var delta = e.Time;

            if (Keyboard[Key.Left])
                _angle += 90 * delta;
            if (Keyboard[Key.Right])
                _angle -= 90 * delta;
            if (Keyboard[Key.Up])
                _angleY += 90 * delta;
            if (Keyboard[Key.Down])
                _angleY -= 90 * delta;
        }

        public void ReDecorate(double[,] heightmap = null)
        {
            if (heightmap == null)
                heightmap = Heightmap;
            if (ListDecor == 0)
                ListDecor = GL.GenLists(1);

            GL.NewList(ListDecor, ListMode.Compile);

            _halton.Reset();
            GL.Color3(137 / 255f, 18 / 255f, 0);
            for (var x = -(float)SideLength / 16; x < (float)SideLength / 16; x++)
                for (var z = -(float)SideLength / 16; z < (float)SideLength / 16; z++)
                    PopulateChunk(x, z, heightmap);

            GL.EndList();
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
                if (val <= _terrainLayerList.ScriptedTerrainGenerator.WaterLevel &&
                    !_terrainLayerList.ScriptedTerrainGenerator.TreesBelowWaterLevel) continue;
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

            var scale = new Vector3(4 * (1 / _zoom), -4 * (1 / _zoom), 4 * (1 / _zoom));
            GL.Scale(scale);
            GL.Rotate(_angleY, 1.0f, 0.0f, 0.0f);
            GL.Rotate(_angle, 0.0f, 1.0f, 0.0f);

            GL.PolygonMode(MaterialFace.FrontAndBack,
                _terrainLayerList.cbWireframe.Checked ? PolygonMode.Line : PolygonMode.Fill);

            _tvbo.Render();
            GL.CallList(ListDecor);

            GL.Color3(0, 70 / 255f, 200 / 255f);

            GL.Begin(PrimitiveType.Quads);
            GL.Normal3(UpVector);
            GL.Vertex3(-SideLength, _terrainLayerList.ScriptedTerrainGenerator.WaterLevel - 0.1, -SideLength);
            GL.Vertex3(SideLength, _terrainLayerList.ScriptedTerrainGenerator.WaterLevel - 0.1, -SideLength);
            GL.Vertex3(SideLength, _terrainLayerList.ScriptedTerrainGenerator.WaterLevel - 0.1, SideLength);
            GL.Vertex3(-SideLength, _terrainLayerList.ScriptedTerrainGenerator.WaterLevel - 0.1, SideLength);
            GL.End();

            SwapBuffers();
        }

        private static void DrawBox(float size)
        {
            if (ListBlock == 0)
            {
                ListBlock = GL.GenLists(1);
                GL.NewList(ListBlock, ListMode.Compile);

                float[,] boxNormals =
                {
                    {-1.0f, 0.0f, 0.0f},
                    {0.0f, 1.0f, 0.0f},
                    {1.0f, 0.0f, 0.0f},
                    {0.0f, -1.0f, 0.0f},
                    {0.0f, 0.0f, 1.0f},
                    {0.0f, 0.0f, -1.0f}
                };

                int[,] boxFaces =
                {
                    {0, 1, 2, 3},
                    {3, 2, 6, 7},
                    {7, 6, 5, 4},
                    {4, 5, 1, 0},
                    {5, 6, 2, 1},
                    {7, 4, 0, 3}
                };

                var boxVertices = new float[8, 3];
                boxVertices[0, 0] = boxVertices[1, 0] = boxVertices[2, 0] = boxVertices[3, 0] = -0.5f;
                boxVertices[4, 0] = boxVertices[5, 0] = boxVertices[6, 0] = boxVertices[7, 0] = 0.5f;
                boxVertices[0, 1] = boxVertices[1, 1] = boxVertices[4, 1] = boxVertices[5, 1] = -0.5f;
                boxVertices[2, 1] = boxVertices[3, 1] = boxVertices[6, 1] = boxVertices[7, 1] = 0.5f;
                boxVertices[0, 2] = boxVertices[3, 2] = boxVertices[4, 2] = boxVertices[7, 2] = -0.5f;
                boxVertices[1, 2] = boxVertices[2, 2] = boxVertices[5, 2] = boxVertices[6, 2] = 0.5f;

                GL.Begin(PrimitiveType.Quads);
                for (var i = 5; i >= 0; i--)
                {
                    GL.Normal3(ref boxNormals[i, 0]);
                    GL.Vertex3(ref boxVertices[boxFaces[i, 0], 0]);
                    GL.Vertex3(ref boxVertices[boxFaces[i, 1], 0]);
                    GL.Vertex3(ref boxVertices[boxFaces[i, 2], 0]);
                    GL.Vertex3(ref boxVertices[boxFaces[i, 3], 0]);
                }

                GL.End();
                GL.EndList();
            }

            GL.PushMatrix();
            GL.Scale(size, size, size);
            GL.CallList(ListBlock);
            GL.PopMatrix();
        }
    }
}