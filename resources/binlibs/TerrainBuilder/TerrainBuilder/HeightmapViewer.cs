using PFX;
using PFX.Util;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Diagnostics;
using System.Drawing;
using System.IO;
using System.Windows.Forms;

namespace TerrainBuilder
{
    public partial class HeightmapViewer : Form
    {
        public ScriptedTerrainGenerator ScriptedTerrainGenerator = new ScriptedTerrainGenerator();
        private readonly ScriptWatcher _scriptWatcher = new ScriptWatcher();
        private readonly BackgroundWorker _backgroundRenderer = new BackgroundWorker();

        public Dictionary<int, Color> Colors = new Dictionary<int, Color>();

        private readonly Random _random = new Random();

        public HeightmapViewer()
        {
            InitializeComponent();

            nudSeed.Minimum = 0;
            nudSeed.Maximum = int.MaxValue;
            nudSeed.Value = _random.Next();

            Text = EmbeddedFiles.AppName;
            Icon = EmbeddedFiles.logo;

            for (var i = 0; i < 256; i++)
                Colors.Add(i, Color.FromArgb(i, i, i));
        }

        private void bCreate_Click(object sender, EventArgs e)
        {
            var sfd = new SaveFileDialog { Filter = "Lua Files|*.lua" };

            if (sfd.ShowDialog() == DialogResult.Cancel) return;

            File.WriteAllText(sfd.FileName, EmbeddedFiles.terrain);
            Process.Start(sfd.FileName);
            WatchTerrainScript(sfd.FileName);
        }

        private void bOpen_Click(object sender, EventArgs e)
        {
            var ofd = new OpenFileDialog { Filter = "Lua Files|*.lua" };

            if (ofd.ShowDialog() == DialogResult.Cancel) return;

            WatchTerrainScript(ofd.FileName);
        }

        private void bRandomize_Click(object sender, EventArgs e)
        {
            nudSeed.Value = _random.Next();
        }

        private void nudSeed_ValueChanged(object sender, EventArgs e)
        {
            ScriptedTerrainGenerator.SetSeed((long)nudSeed.Value);
            ReRenderNoiseImage();
        }

        private void nudSideLength_ValueChanged(object sender, EventArgs e)
        {
            ReRenderNoiseImage();
        }

        private void WatchTerrainScript(string filename)
        {
            _scriptWatcher.WatchTerrainScript(filename);
            ReRenderNoiseImage();
        }

        private void ReRenderNoiseImage()
        {
            if (Colors.Count == 0)
                return;

            try
            {
                var bmp = new Bitmap((int) nudSideLength.Value, (int) nudSideLength.Value);
                
                // If there's an ongoing render, cancel it
                if (IsRendering())
                    CancelRender();
                
                // Enable the render statusbar
                Invoke((MethodInvoker) delegate
                {
                    bCancelRender.Enabled = true;
                    bCancelRender.Visible = true;

                    pbRenderStatus.Visible = true;
                });

                // Fire up the render
                _backgroundRenderer.RunWorkerAsync(bmp);
            }
            catch (Exception ex)
            {
                Lumberjack.Error(ex.Message);
            }
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

        private void DoBackgroundRenderProgress(object sender, ProgressChangedEventArgs progressChangedEventArgs)
        {
            // Render thread says something
            Invoke((MethodInvoker)delegate
            {
                // Invoke changes on form thread
                pbRenderStatus.Value = progressChangedEventArgs.ProgressPercentage;
                if (progressChangedEventArgs.UserState is string s)
                    lRenderStatus.Text = s;
            });
        }

        private void DoBackgroundRenderComplete(object sender, RunWorkerCompletedEventArgs e)
        {
            // Render done, reset statusbar 
            Invoke((MethodInvoker)delegate
            {
                bCancelRender.Visible = false;
                bCancelRender.Enabled = false;

                pbRenderStatus.Visible = false;
                pbRenderStatus.Value = 0;

                lRenderStatus.Text = EmbeddedFiles.Status_Ready;
            });

            // If the render was manually cancelled, go no further
            if (_scriptWatcher.GetScriptId() == 0 || e.Cancelled)
                return;

            // Take the render result and upload it to the VBO
            pbHeightmap.Image = (Bitmap)e.Result;
            GC.Collect();

            // Wait for render thread to exit
            while (IsRendering())
                Application.DoEvents();
        }

        private void DoBackgroundRender(object sender, DoWorkEventArgs e)
        {
            try
            {
                // Make sure the render requirements are met
                if (_scriptWatcher.GetScriptId() == 0)
                {
                    Lumberjack.Warn("Can't render, no terrain loaded.");
                    return;
                }

                // Grab worker and report progress
                var worker = (BackgroundWorker)sender;
                var bitmap = (Bitmap)e.Argument;
                worker.ReportProgress(0, EmbeddedFiles.Status_GenHeightmap);

                for (var x = 0; x < bitmap.Width; x++)
                {
                    for (var y = 0; y < bitmap.Height; y++)
                    {
                        // Cancel if requested
                        if (worker.CancellationPending)
                        {
                            e.Cancel = true;
                            return;
                        }

                        var n = ScriptedTerrainGenerator.GetValue(x, y);
                        bitmap.SetPixel(x, y, Colors[(int) n]);
                    }
                    worker.ReportProgress((int) (x / (float)bitmap.Width * 100));
                }

                // Send the result back to the worker
                e.Result = bitmap;
            }
            catch (Exception ex)
            {
                Lumberjack.Error(ex.Message);
                e.Result = new VertexBufferInitializer();
            }
        }

        private void HeightmapViewer_Load(object sender, EventArgs e)
        {
            _scriptWatcher.FileChanged += ScriptWatcherOnFileChanged;

            // Wire up background worker
            _backgroundRenderer.WorkerReportsProgress = true;
            _backgroundRenderer.WorkerSupportsCancellation = true;
            _backgroundRenderer.DoWork += DoBackgroundRender;
            _backgroundRenderer.ProgressChanged += DoBackgroundRenderProgress;
            _backgroundRenderer.RunWorkerCompleted += DoBackgroundRenderComplete;
        }

        private void ScriptWatcherOnFileChanged(object sender, ScriptChangedEventArgs e)
        {
            Lumberjack.Info(string.Format(EmbeddedFiles.Info_FileReloaded, e.Filename));
            ScriptedTerrainGenerator.LoadScript(e.Script, e.ScriptCode);
            ReRenderNoiseImage();
        }

        private void bCancelRender_ButtonClick(object sender, EventArgs e)
        {
            CancelRender();
        }
    }
}
