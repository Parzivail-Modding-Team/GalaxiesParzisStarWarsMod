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
using Newtonsoft.Json;

namespace TerrainBuilder
{
    public partial class TerrainLayerList : Form
    {
        private readonly WindowVisualize _parent;
        private readonly Random _random = new Random();

        public ScriptedTerrainGenerator ScriptedTerrainGenerator = new ScriptedTerrainGenerator();
        public Dictionary<int, Color> Colors = new Dictionary<int, Color>();

        public TerrainLayerList(WindowVisualize parent)
        {
            _parent = parent;
            InitializeComponent();

            nudSeed.Minimum = 0;
            nudSeed.Maximum = int.MaxValue;
            nudSeed.Value = _random.Next();

            for (var i = 0; i < 256; i++)
                Colors.Add(i, Color.FromArgb(i, i, i));
        }

        private void bRandomize_Click(object sender, EventArgs e)
        {
            nudSeed.Value = _random.Next();
            ScriptedTerrainGenerator.SetSeed((long)nudSeed.Value);
            _parent.ReRender();
            ReRenderNoiseImage();
        }

        private void TerrainLayerList_FormClosing(object sender, FormClosingEventArgs e)
        {
            _parent.Kill();
        }

        private void nudSideLength_ValueChanged(object sender, EventArgs e)
        {
            _parent.SideLength = (int) nudSideLength.Value;
            _parent.ReRender();
        }

        private void bSave_Click(object sender, EventArgs e)
        {
            var sfd = new SaveFileDialog {Filter = "Java Files|*.java"};

            if (sfd.ShowDialog() != DialogResult.Cancel)
            {
                Exporter.Export(this, sfd.FileName);
            }
        }

        private void nudSeed_ValueChanged(object sender, EventArgs e)
        {
            ScriptedTerrainGenerator.SetSeed((long)nudSeed.Value);
            _parent.ReRender();
            ReRenderNoiseImage();
        }

        public void ReRenderNoiseImage()
        {
            if (Colors.Count == 0)
                return;
            
            var bmp = new Bitmap(pbNoise.Width, pbNoise.Height);
            for (var x = 0; x < pbNoise.Width; x++)
                for (var y = 0; y < pbNoise.Height; y++)
                {
                    var n = _parent.GetValueAt(x, y);
                    if (n > 255)
                        n = 255;
                    if (n < 0)
                        n = 0;
                    bmp.SetPixel(x, y, Colors[(int)n]);
                }
            pbNoise.Image = bmp;
        }

        private void cbBlockRounding_CheckedChanged(object sender, EventArgs e)
        {
            cbBlockApproximation.Enabled = cbBlockRounding.Checked;
            _parent.ReRender();
        }

        private void cbRealBlock_CheckedChanged(object sender, EventArgs e)
        {
            _parent.ReRender();
        }

        private void rbHeightmap_CheckedChanged(object sender, EventArgs e)
        {
            _parent.ReRender();
        }

        private void nudCaveThreshold_ValueChanged(object sender, EventArgs e)
        {
            if (rbInterior.Checked)
                _parent.ReRender();
        }

        private void nudCaveScale_ValueChanged(object sender, EventArgs e)
        {
            if (rbInterior.Checked)
                _parent.ReRender();
        }

        private void rbInterior_CheckedChanged_1(object sender, EventArgs e)
        {
            _parent.ReRender();
        }

        private void cbRelaxedFeatures_CheckedChanged(object sender, EventArgs e)
        {
            _parent.ReRender();
        }

        private void bCreateTerrain_Click(object sender, EventArgs e)
        {
            var sfd = new SaveFileDialog { Filter = "Lua Files|*.lua" };

            if (sfd.ShowDialog() == DialogResult.Cancel) return;

            File.WriteAllText(sfd.FileName, EmbeddedFiles.terrain);
            Process.Start(sfd.FileName);
            _parent.WatchTerrainScript(sfd.FileName);
            _parent.Title = $"TerrainViewer | {sfd.FileName}";
        }

        private void bOpenTerrain_Click(object sender, EventArgs e)
        {
            var ofd = new OpenFileDialog { Filter = "Lua Files|*.lua" };

            if (ofd.ShowDialog() == DialogResult.Cancel) return;

            _parent.WatchTerrainScript(ofd.FileName);
            _parent.Title = $"TerrainViewer | {ofd.FileName}";
        }
    }
}
