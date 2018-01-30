using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Linq;
using System.Windows.Forms;
using OpenTK;
using OpenTK.Graphics.OpenGL;
using OpenTK.Input;
using PFX;
using PFX.BmFont;
using PFX.Util;

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
        private static BitmapFont font;
        private readonly HaltonSequence _halton = new HaltonSequence();
        private readonly SimpleVertexBuffer _tvbo = new SimpleVertexBuffer();
        private readonly BackgroundWorker _backgroundRenderer = new BackgroundWorker();

        /*
         * Terrain-related
         */
        private int _sideLength = 64;
        public int SideLength
        {
            get { return _sideLength; }
            set { _sideLength = (int)(value / 2f); }
        }
        public static double[,] Heightmap;
        private readonly TerrainLayerList _terrainLayerList;

        /*
         * Window-related
         */
        private bool _shouldDie;
        private readonly Profiler _profiler = new Profiler();
        private static KeyboardState _keyboard;
        private Dictionary<string, TimeSpan> _profile = new Dictionary<string, TimeSpan>();

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
            Title = $"{EmbeddedFiles.AppName} | {EmbeddedFiles.Title_Unsaved}";
        }

        private void LoadHandler(object sender, EventArgs e)
        {
            const float diffuse = 0.65f;
            float[] matDiffuse = { diffuse, diffuse, diffuse };
            GL.Material(MaterialFace.FrontAndBack, MaterialParameter.AmbientAndDiffuse, matDiffuse);
            GL.Light(LightName.Light0, LightParameter.Position, new[] { 0.0f, 0.0f, 0.0f, 10.0f });
            GL.Light(LightName.Light0, LightParameter.Diffuse, new[] { diffuse, diffuse, diffuse, diffuse });

            GL.Enable(EnableCap.Lighting);
            GL.Enable(EnableCap.Light0);
            GL.ShadeModel(ShadingModel.Smooth);
            GL.Enable(EnableCap.ColorMaterial);
            GL.Enable(EnableCap.DepthTest);
            GL.Enable(EnableCap.RescaleNormal);
            GL.Enable(EnableCap.DepthTest);
            GL.Enable(EnableCap.Blend);
            GL.BlendFunc(BlendingFactorSrc.SrcAlpha, BlendingFactorDest.OneMinusSrcAlpha);

            GL.ClearColor(Color.FromArgb(255, 13, 13, 13));

            font = BitmapFont.LoadBinaryFont("dina", FontBank.FontDina, FontBank.BmDina);

            _keyboard = Keyboard.GetState();

            Lumberjack.Info(EmbeddedFiles.Info_WindowLoaded);
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
            Lumberjack.Info(string.Format(EmbeddedFiles.Info_FileReloaded, e.Filename));
            _terrainLayerList.ScriptedTerrainGenerator.LoadScript(e.Script, e.ScriptCode);
            _dirty = true;
        }

        public bool IsRendering()
        {
            return _backgroundRenderer.IsBusy;
        }

        public void CancelRender()
        {
            Lumberjack.Warn(EmbeddedFiles.Info_CancellingPreviousRenderOp);
            _backgroundRenderer.CancelAsync();

            while (IsRendering())
                Application.DoEvents();
        }

        public void ReRender(bool manualOverride = false)
        {
            if (_terrainLayerList == null || _terrainLayerList.cbPauseGen.Checked && !manualOverride)
                return;

            if (IsRendering())
                CancelRender();

            _terrainLayerList.bCancelRender.Enabled = true;

            _terrainLayerList.bCancelRender.Visible = true;
            _terrainLayerList.pbRenderStatus.Visible = true;

            _backgroundRenderer.RunWorkerAsync();
        }

        private void DoBackgroundRenderProgress(object sender, ProgressChangedEventArgs progressChangedEventArgs)
        {
            _terrainLayerList.Invoke((MethodInvoker) delegate
            {
                _terrainLayerList.pbRenderStatus.Value = progressChangedEventArgs.ProgressPercentage;
                if (progressChangedEventArgs.UserState is string s)
                    _terrainLayerList.lRenderStatus.Text = s;
            });
        }

        private void DoBackgroundRenderComplete(object sender, RunWorkerCompletedEventArgs e)
        {
            _terrainLayerList.bCancelRender.Visible = false;
            _terrainLayerList.pbRenderStatus.Visible = false;

            _terrainLayerList.bCancelRender.Enabled = false;
            _terrainLayerList.lRenderStatus.Text = EmbeddedFiles.Status_Ready;
            _terrainLayerList.pbRenderStatus.Value = 0;

            if (_scriptWatcher.GetScriptId() == 0 || e.Cancelled)
                return;

            var result = (BackgroundRenderResult)e.Result;
            _tvbo.InitializeVbo(result.Vertices, result.Normals, result.Colors, result.Indices);
            GC.Collect();

            ReDecorate();
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

            worker.ReportProgress(0, EmbeddedFiles.Status_GenHeightmap);

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

            worker.ReportProgress(50, EmbeddedFiles.Status_UploadVBO);

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

                    // This heightmap position
                    var valueHere = Heightmap[nx, nz];

                    // Neighboring positions
                    var valuePosX = Heightmap[nx + 1, nz];
                    var valueNegX = Heightmap[nx - 1, nz];
                    var valuePosZ = Heightmap[nx, nz + 1];
                    var valueNegZ = Heightmap[nx, nz - 1];

                    var valuePosXPosZ = Heightmap[nx + 1, nz + 1];
                    var valueNegXPosZ = Heightmap[nx - 1, nz + 1];
                    var valuePosXNegZ = Heightmap[nx + 1, nz - 1];
                    var valueNegXNegZ = Heightmap[nx - 1, nz - 1];
                    
                    var isPosXHigher = valuePosX > valueHere;
                    var isNegXHigher = valueNegX > valueHere;
                    var isPosZHigher = valuePosZ > valueHere;
                    var isNegZHigher = valueNegZ > valueHere;

                    var isPosXPosZHigher = valuePosXPosZ > valueHere; 
                    var isNegXPosZHigher = valueNegXPosZ > valueHere;
                    var isPosXNegZHigher = valuePosXNegZ > valueHere;
                    var isNegXNegZHigher = valueNegXNegZ > valueHere;

                    var colorMult = (float)((valueHere - globalMinY) / globalRangeY);
                    var color = Util.RgbToInt(colorMult * 0.25f, 0.25f * colorMult + 0.5f, colorMult * 0.25f);
                    const float occludedScalar = 0.8f;
                    var occludedColor = Util.RgbToInt(colorMult * 0.25f * occludedScalar, (0.25f * colorMult + 0.5f) * occludedScalar, colorMult * 0.25f * occludedScalar);

                    vertices.Add(new Vector3(x, (float)valueHere, z));
                    normals.Add(UpVector);
                    colors.Add(isPosXHigher || isPosZHigher || isPosXPosZHigher ? occludedColor : color);
                    vertices.Add(new Vector3(x - 1, (float)valueHere, z));
                    normals.Add(UpVector);
                    colors.Add(isNegXHigher || isPosZHigher || isNegXPosZHigher ? occludedColor : color);
                    vertices.Add(new Vector3(x - 1, (float)valueHere, z - 1));
                    normals.Add(UpVector);
                    colors.Add(isNegXHigher || isNegZHigher || isNegXNegZHigher ? occludedColor : color);
                    vertices.Add(new Vector3(x, (float)valueHere, z - 1));
                    normals.Add(UpVector);
                    colors.Add(isPosXHigher || isNegZHigher || isPosXNegZHigher ? occludedColor : color);
                    indices.AddRange(new List<int> { i++, i++, i++, i++ });

                    if (valuePosZ < valueHere)
                    {
                        vertices.Add(new Vector3(x, (float)valueHere, z));
                        normals.Add(PosZVector);
                        colors.Add(color);
                        vertices.Add(new Vector3(x, (float)valuePosZ, z));
                        normals.Add(PosZVector);
                        colors.Add(color);
                        vertices.Add(new Vector3(x - 1, (float)valuePosZ, z));
                        normals.Add(PosZVector);
                        colors.Add(color);
                        vertices.Add(new Vector3(x - 1, (float)valueHere, z));
                        normals.Add(PosZVector);
                        colors.Add(color);
                        indices.AddRange(new List<int> { i++, i++, i++, i++ });
                    }

                    if (valueNegZ < valueHere)
                    {
                        vertices.Add(new Vector3(x, (float)valueHere, z - 1));
                        normals.Add(NegZVector);
                        colors.Add(color);
                        vertices.Add(new Vector3(x, (float)valueNegZ, z - 1));
                        normals.Add(NegZVector);
                        colors.Add(color);
                        vertices.Add(new Vector3(x - 1, (float)valueNegZ, z - 1));
                        normals.Add(NegZVector);
                        colors.Add(color);
                        vertices.Add(new Vector3(x - 1, (float)valueHere, z - 1));
                        normals.Add(NegZVector);
                        colors.Add(color);
                        indices.AddRange(new List<int> { i++, i++, i++, i++ });
                    }

                    if (valuePosX < valueHere)
                    {
                        vertices.Add(new Vector3(x, (float)valueHere, z));
                        normals.Add(PosXVector);
                        colors.Add(color);
                        vertices.Add(new Vector3(x, (float)valuePosX, z));
                        normals.Add(PosXVector);
                        colors.Add(color);
                        vertices.Add(new Vector3(x, (float)valuePosX, z - 1));
                        normals.Add(PosXVector);
                        colors.Add(color);
                        vertices.Add(new Vector3(x, (float)valueHere, z - 1));
                        normals.Add(PosXVector);
                        colors.Add(color);
                        indices.AddRange(new List<int> { i++, i++, i++, i++ });
                    }

                    if (valueNegX < valueHere)
                    {
                        vertices.Add(new Vector3(x - 1, (float)valueHere, z));
                        normals.Add(NegXVector);
                        colors.Add(color);
                        vertices.Add(new Vector3(x - 1, (float)valueNegX, z));
                        normals.Add(NegXVector);
                        colors.Add(color);
                        vertices.Add(new Vector3(x - 1, (float)valueNegX, z - 1));
                        normals.Add(NegXVector);
                        colors.Add(color);
                        vertices.Add(new Vector3(x - 1, (float)valueHere, z - 1));
                        normals.Add(NegXVector);
                        colors.Add(color);
                        indices.AddRange(new List<int> { i++, i++, i++, i++ });
                    }
                }

                worker.ReportProgress((int)((x + SideLength) / (SideLength * 2f) * 50) + 50);
            }

            e.Result = new BackgroundRenderResult(vertices.ToArray(), normals.ToArray(), colors.ToArray(),
                indices.ToArray());
        }

        private void UpdateHandler(object sender, FrameEventArgs e)
        {
            _keyboard = Keyboard.GetState();

            if (_shouldDie)
                Exit();

            if (_dirty)
            {
                _terrainLayerList.ReRenderNoiseImage();
                ReRender();
                _dirty = false;
            }

            var delta = e.Time;
            var amount = _keyboard[Key.LShift] || _keyboard[Key.RShift] ? 45 : 90;

            if (Keyboard[Key.Left])
                _angle += amount * delta;
            if (Keyboard[Key.Right])
                _angle -= amount * delta;
            if (Keyboard[Key.Up])
                _angleY += amount * delta;
            if (Keyboard[Key.Down])
                _angleY -= amount * delta;
        }

        public void ReDecorate()
        {
            if (ListDecor == 0)
                ListDecor = GL.GenLists(1);

            GL.NewList(ListDecor, ListMode.Compile);

            _halton.Reset();
            GL.Color3(137 / 255f, 18 / 255f, 0);
            for (var x = -(float)SideLength / 16; x < (float)SideLength / 16; x++)
                for (var z = -(float)SideLength / 16; z < (float)SideLength / 16; z++)
                    PopulateChunk(x, z);

            GL.EndList();
        }

        private void PopulateChunk(float x, float z)
        {
            var nx = x * 16 + SideLength + 1;
            var nz = z * 16 + SideLength + 1;
            for (var i = 0; i < _terrainLayerList.ScriptedTerrainGenerator.TreesPerChunk; i++)
            {
                var pos = _halton.Increment();
                pos *= 16;
                pos += new Vector3(nx, 0, nz);
                var val = Heightmap[(int)pos.X, (int)pos.Z];
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
            _profiler.Start("render");

            GL.Clear(ClearBufferMask.ColorBufferBit |
                     ClearBufferMask.DepthBufferBit |
                     ClearBufferMask.StencilBufferBit);

            var aspectRatio = Width / (float)Height;
            var projection = Matrix4.CreatePerspectiveFieldOfView(MathHelper.PiOver4, aspectRatio, 1, 1024);
            GL.MatrixMode(MatrixMode.Projection);
            GL.LoadMatrix(ref projection);

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

            GL.Color3(0, 0.27f, 0.78f);

            GL.Begin(PrimitiveType.Quads);
            GL.Normal3(UpVector);
            GL.Vertex3(-SideLength, _terrainLayerList.ScriptedTerrainGenerator.WaterLevel - 0.1, -SideLength);
            GL.Vertex3(SideLength, _terrainLayerList.ScriptedTerrainGenerator.WaterLevel - 0.1, -SideLength);
            GL.Vertex3(SideLength, _terrainLayerList.ScriptedTerrainGenerator.WaterLevel - 0.1, SideLength);
            GL.Vertex3(-SideLength, _terrainLayerList.ScriptedTerrainGenerator.WaterLevel - 0.1, SideLength);
            GL.End();
            
            GL.MatrixMode(MatrixMode.Projection);
            GL.LoadIdentity();
            GL.Ortho(0, Width, Height, 0, 1, -1);
            GL.MatrixMode(MatrixMode.Modelview);
            GL.LoadIdentity();

            GL.PushMatrix();

            GL.Enable(EnableCap.Blend);
            GL.Enable(EnableCap.Texture2D);
            GL.Disable(EnableCap.Lighting);
            GL.Color3(Color.White);

            if (_profile.ContainsKey("render"))
            {
                var ms = _profile["render"].TotalMilliseconds;
                font.RenderString($"FPS: {((int)Math.Ceiling(ms) != 0 ? ((int)Math.Ceiling(1000 / ms)).ToString() : "INF").PadRight(4)} ({(int)Math.Ceiling(ms)}ms)");
            }

            GL.Enable(EnableCap.Lighting);
            GL.Disable(EnableCap.Texture2D);
            GL.Disable(EnableCap.Blend);

            GL.PopMatrix();

            SwapBuffers();
            _profiler.End();
            _profile = _profiler.Reset();
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