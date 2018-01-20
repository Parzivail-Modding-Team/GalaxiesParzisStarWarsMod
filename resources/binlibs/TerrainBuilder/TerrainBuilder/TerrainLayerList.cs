using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
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
        private DataTable _t = new DataTable("Layers");

        public BindingList<TerrainLayer> Layers = new BindingList<TerrainLayer>();

        public Dictionary<int, Color> Colors = new Dictionary<int, Color>();

        public TerrainLayerList(WindowVisualize parent)
        {
            _parent = parent;
            InitializeComponent();

            nudSeed.Minimum = 0;
            nudSeed.Maximum = int.MaxValue;
            nudSeed.Value = _random.Next();

            Layers.Add(new TerrainLayer(Guid.NewGuid(), (long)nudSeed.Value));

            dgvLayers.AutoGenerateColumns = false;
            dgvLayers.AutoSizeColumnsMode = DataGridViewAutoSizeColumnsMode.Fill;
            dgvLayers.Columns.Add(new DataGridViewTextBoxColumn
            {
                Name = "Scale",
                HeaderText = "Scale",
                DataPropertyName = "Scale"
            });
            dgvLayers.Columns.Add(new DataGridViewTextBoxColumn
            {
                Name = "Range",
                HeaderText = "Range",
                DataPropertyName = "Range"
            });
            dgvLayers.Columns.Add(new DataGridViewComboBoxColumn
            {
                Name = "Function",
                HeaderText = "Function",
                DataPropertyName = "Function",
                ValueType = typeof(NoiseFunction),
                DataSource = Enum.GetValues(typeof(NoiseFunction))
            });
            dgvLayers.Columns.Add(new DataGridViewComboBoxColumn
            {
                Name = "Method",
                HeaderText = "Method",
                DataPropertyName = "Method",
                ValueType = typeof(Method),
                DataSource = Enum.GetValues(typeof(Method))
            });
            dgvLayers.DataSource = Layers;
            dgvLayers.DataError += DgvLayers_DataError;

            for (var i = 0; i < 256; i++)
                Colors.Add(i, Color.FromArgb(i, i, i));
        }

        private void DgvLayers_DataError(object sender, DataGridViewDataErrorEventArgs e)
        {
            MessageBox.Show(this, "Invalid value for column!", "TerrainVisualizer", MessageBoxButtons.OK,
                MessageBoxIcon.Error);
        }

        private void bAddLayer_Click(object sender, EventArgs e)
        {
            Layers.Add(new TerrainLayer(Guid.NewGuid(), (long) nudSeed.Value));
            _parent.ReRender();
        }

        private void bRandomize_Click(object sender, EventArgs e)
        {
            nudSeed.Value = _random.Next();
            foreach (var terrainLayer in Layers)
            {
                terrainLayer.SetSeed((long) nudSeed.Value);
            }
            _parent.ReRender();
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

        private void dgvLayers_CellEndEdit(object sender, DataGridViewCellEventArgs e)
        {
            _parent.ReRender();
        }

        private void dgvLayers_RowsAdded(object sender, DataGridViewRowsAddedEventArgs e)
        {
            _parent.ReRender();
        }

        private void dgvLayers_RowsRemoved(object sender, DataGridViewRowsRemovedEventArgs e)
        {
            _parent.ReRender();
        }

        private void nudWaterLevel_ValueChanged(object sender, EventArgs e)
        {
            _parent.WaterLevel = (int)nudWaterLevel.Value;
        }

        private void bSaveData_Click(object sender, EventArgs e)
        {
            var sfd = new SaveFileDialog { Filter = "JSON Files|*.json" };

            if (sfd.ShowDialog() == DialogResult.Cancel) return;

            var data = new TerrainStore
            {
                TerrainData = Layers.ToList(),
                WaterLevel = _parent.WaterLevel,
                TreesPerChunk = (int)nudTreesPerChunk.Value,
                TreesBelowWaterLevel = cbSubmarineTrees.Checked,
                ThLayer1 = (float)nudLayer1.Value,
                ThLayer2 = (float)nudLayer2.Value
            };
            File.WriteAllText(sfd.FileName, JsonConvert.SerializeObject(data));
            _parent.Title = $"TerrainViewer | {sfd.FileName}";
        }

        private void bLoadData_Click(object sender, EventArgs e)
        {
            var ofd = new OpenFileDialog {Filter = "JSON Files|*.json"};

            if (ofd.ShowDialog() != DialogResult.Cancel)
            {
                var data = JsonConvert.DeserializeObject<TerrainStore>(File.ReadAllText(ofd.FileName));
                nudWaterLevel.Value = data.WaterLevel;
                nudTreesPerChunk.Value = data.TreesPerChunk;
                cbSubmarineTrees.Checked = data.TreesBelowWaterLevel;
                nudLayer1.Value = (decimal) data.ThLayer1;
                nudLayer1.Value = (decimal) data.ThLayer2;
                Layers = new BindingList<TerrainLayer>(data.TerrainData);
                _parent.Title = $"TerrainViewer | {ofd.FileName}";
            }
            _parent.ReRender();
            dgvLayers.DataSource = Layers;
        }

        private void nudSeed_ValueChanged(object sender, EventArgs e)
        {
            foreach (var terrainLayer in Layers)
            {
                terrainLayer.SetSeed((long)nudSeed.Value);
            }
            _parent.ReRender();
        }

        private void nudTreesPerChunk_ValueChanged(object sender, EventArgs e)
        {
            _parent.ReDecorate();
        }

        private void cbSubmarineTrees_CheckedChanged(object sender, EventArgs e)
        {
            _parent.ReDecorate();
        }

        private void dgvLayers_SelectionChanged(object sender, EventArgs e)
        {
            ReRenderNoiseImage();
        }

        private void ReRenderNoiseImage()
        {
            if (dgvLayers.SelectedRows.Count == 0)
                return;

            var ridx = dgvLayers.SelectedRows[0].Index;
            var terrainlayer = Layers[ridx];
            var bmp = new Bitmap(pbNoise.Width, pbNoise.Height);
            for (var x = 0; x < pbNoise.Width; x++)
                for (var y = 0; y < pbNoise.Height; y++)
                {
                    var n = terrainlayer.GetValue(x, y) / terrainlayer.Range * 255;
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

        private void dgvLayers_CellEnter(object sender, DataGridViewCellEventArgs e)
        {
            var validClick = (e.RowIndex != -1 && e.ColumnIndex != -1); //Make sure the clicked row/column is valid.
            var datagridview = sender as DataGridView;

            // Check to make sure the cell clicked is the cell containing the combobox 
            if (!(datagridview?.Columns[e.ColumnIndex] is DataGridViewComboBoxColumn) || !validClick) return;
            datagridview.BeginEdit(true);
            ((ComboBox)datagridview.EditingControl).DroppedDown = true;
        }

        private void rbHeightmap_CheckedChanged(object sender, EventArgs e)
        {
            _parent.ReRender();
            label3.Enabled = nudTreesPerChunk.Enabled = cbSubmarineTrees.Enabled = rbHeightmap.Checked;
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

        private void nudLayer1_ValueChanged(object sender, EventArgs e)
        {
            if (rbInterior.Checked)
                _parent.ReRender();
        }

        private void nudLayer2_ValueChanged(object sender, EventArgs e)
        {
            if (rbInterior.Checked)
                _parent.ReRender();
        }

        private void rbInterior_CheckedChanged_1(object sender, EventArgs e)
        {
            _parent.ReRender();
            label6.Enabled = label7.Enabled = nudLayer1.Enabled = nudLayer2.Enabled = rbInterior.Checked;
        }

        private void cbRelaxedFeatures_CheckedChanged(object sender, EventArgs e)
        {
            _parent.ReRender();
        }
    }
}
