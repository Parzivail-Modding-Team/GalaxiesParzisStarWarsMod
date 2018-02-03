using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Diagnostics;
using System.Drawing;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;
using PFX;
using PFX.Util;

namespace TerrainBuilder
{
    public partial class HeightmapViewer : Form
    {
        public ScriptedTerrainGenerator ScriptedTerrainGenerator = new ScriptedTerrainGenerator();
        private readonly ScriptWatcher _scriptWatcher = new ScriptWatcher();

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
                for (var x = 0; x < bmp.Width; x++)
                for (var y = 0; y < bmp.Height; y++)
                {
                    var n = ScriptedTerrainGenerator.GetValue(x, y);
                    bmp.SetPixel(x, y, Colors[(int) n]);
                }

                pbHeightmap.Image = bmp;
            }
            catch (Exception ex)
            {
                Lumberjack.Error(ex.Message);
            }
        }

        private void HeightmapViewer_Load(object sender, EventArgs e)
        {
            _scriptWatcher.FileChanged += ScriptWatcherOnFileChanged;
        }

        private void ScriptWatcherOnFileChanged(object sender, ScriptChangedEventArgs e)
        {
            Lumberjack.Info(string.Format(EmbeddedFiles.Info_FileReloaded, e.Filename));
            ScriptedTerrainGenerator.LoadScript(e.Script, e.ScriptCode);
            ReRenderNoiseImage();
        }
    }
}
