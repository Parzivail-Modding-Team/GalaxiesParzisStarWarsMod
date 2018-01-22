namespace TerrainBuilder
{
    partial class TerrainLayerList
    {
        /// <summary>
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            this.nudSeed = new System.Windows.Forms.NumericUpDown();
            this.bRandomize = new System.Windows.Forms.Button();
            this.lSeed = new System.Windows.Forms.Label();
            this.nudSideLength = new System.Windows.Forms.NumericUpDown();
            this.label1 = new System.Windows.Forms.Label();
            this.bSave = new System.Windows.Forms.Button();
            this.groupBox1 = new System.Windows.Forms.GroupBox();
            this.pbNoise = new System.Windows.Forms.PictureBox();
            this.cbWireframe = new System.Windows.Forms.CheckBox();
            this.cbBlockRounding = new System.Windows.Forms.CheckBox();
            this.cbBlockApproximation = new System.Windows.Forms.CheckBox();
            this.groupBox3 = new System.Windows.Forms.GroupBox();
            this.rbInterior = new System.Windows.Forms.RadioButton();
            this.rbHeightmap = new System.Windows.Forms.RadioButton();
            this.cbRelaxedFeatures = new System.Windows.Forms.CheckBox();
            this.bCreateTerrain = new System.Windows.Forms.Button();
            this.bOpenTerrain = new System.Windows.Forms.Button();
            ((System.ComponentModel.ISupportInitialize)(this.nudSeed)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.nudSideLength)).BeginInit();
            this.groupBox1.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)(this.pbNoise)).BeginInit();
            this.groupBox3.SuspendLayout();
            this.SuspendLayout();
            // 
            // nudSeed
            // 
            this.nudSeed.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.nudSeed.Location = new System.Drawing.Point(135, 22);
            this.nudSeed.Name = "nudSeed";
            this.nudSeed.Size = new System.Drawing.Size(307, 20);
            this.nudSeed.TabIndex = 2;
            this.nudSeed.ValueChanged += new System.EventHandler(this.nudSeed_ValueChanged);
            // 
            // bRandomize
            // 
            this.bRandomize.Location = new System.Drawing.Point(44, 19);
            this.bRandomize.Name = "bRandomize";
            this.bRandomize.Size = new System.Drawing.Size(75, 23);
            this.bRandomize.TabIndex = 3;
            this.bRandomize.Text = "Randomize";
            this.bRandomize.UseVisualStyleBackColor = true;
            this.bRandomize.Click += new System.EventHandler(this.bRandomize_Click);
            // 
            // lSeed
            // 
            this.lSeed.AutoSize = true;
            this.lSeed.Location = new System.Drawing.Point(6, 24);
            this.lSeed.Name = "lSeed";
            this.lSeed.Size = new System.Drawing.Size(32, 13);
            this.lSeed.TabIndex = 4;
            this.lSeed.Text = "Seed";
            // 
            // nudSideLength
            // 
            this.nudSideLength.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.nudSideLength.Increment = new decimal(new int[] {
            16,
            0,
            0,
            0});
            this.nudSideLength.Location = new System.Drawing.Point(135, 48);
            this.nudSideLength.Maximum = new decimal(new int[] {
            512,
            0,
            0,
            0});
            this.nudSideLength.Name = "nudSideLength";
            this.nudSideLength.Size = new System.Drawing.Size(307, 20);
            this.nudSideLength.TabIndex = 5;
            this.nudSideLength.Value = new decimal(new int[] {
            128,
            0,
            0,
            0});
            this.nudSideLength.ValueChanged += new System.EventHandler(this.nudSideLength_ValueChanged);
            // 
            // label1
            // 
            this.label1.AutoSize = true;
            this.label1.Location = new System.Drawing.Point(6, 50);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(64, 13);
            this.label1.TabIndex = 6;
            this.label1.Text = "Side Length";
            // 
            // bSave
            // 
            this.bSave.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.bSave.Location = new System.Drawing.Point(12, 376);
            this.bSave.Name = "bSave";
            this.bSave.Size = new System.Drawing.Size(448, 23);
            this.bSave.TabIndex = 7;
            this.bSave.Text = "Export Java Terrain Generator";
            this.bSave.UseVisualStyleBackColor = true;
            this.bSave.Click += new System.EventHandler(this.bSave_Click);
            // 
            // groupBox1
            // 
            this.groupBox1.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.groupBox1.Controls.Add(this.bRandomize);
            this.groupBox1.Controls.Add(this.nudSeed);
            this.groupBox1.Controls.Add(this.lSeed);
            this.groupBox1.Controls.Add(this.nudSideLength);
            this.groupBox1.Controls.Add(this.label1);
            this.groupBox1.Location = new System.Drawing.Point(12, 12);
            this.groupBox1.Name = "groupBox1";
            this.groupBox1.Size = new System.Drawing.Size(448, 80);
            this.groupBox1.TabIndex = 12;
            this.groupBox1.TabStop = false;
            this.groupBox1.Text = "Terrain";
            // 
            // pbNoise
            // 
            this.pbNoise.Location = new System.Drawing.Point(12, 156);
            this.pbNoise.Name = "pbNoise";
            this.pbNoise.Size = new System.Drawing.Size(200, 200);
            this.pbNoise.TabIndex = 3;
            this.pbNoise.TabStop = false;
            // 
            // cbWireframe
            // 
            this.cbWireframe.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.cbWireframe.AutoSize = true;
            this.cbWireframe.Location = new System.Drawing.Point(225, 280);
            this.cbWireframe.Name = "cbWireframe";
            this.cbWireframe.Size = new System.Drawing.Size(48, 17);
            this.cbWireframe.TabIndex = 0;
            this.cbWireframe.Text = "Wire";
            this.cbWireframe.UseVisualStyleBackColor = true;
            // 
            // cbBlockRounding
            // 
            this.cbBlockRounding.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.cbBlockRounding.AutoSize = true;
            this.cbBlockRounding.Checked = true;
            this.cbBlockRounding.CheckState = System.Windows.Forms.CheckState.Checked;
            this.cbBlockRounding.Location = new System.Drawing.Point(225, 303);
            this.cbBlockRounding.Name = "cbBlockRounding";
            this.cbBlockRounding.Size = new System.Drawing.Size(102, 17);
            this.cbBlockRounding.TabIndex = 14;
            this.cbBlockRounding.Text = "Block Rounding";
            this.cbBlockRounding.UseVisualStyleBackColor = true;
            this.cbBlockRounding.CheckedChanged += new System.EventHandler(this.cbBlockRounding_CheckedChanged);
            // 
            // cbBlockApproximation
            // 
            this.cbBlockApproximation.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.cbBlockApproximation.AutoSize = true;
            this.cbBlockApproximation.Location = new System.Drawing.Point(225, 326);
            this.cbBlockApproximation.Name = "cbBlockApproximation";
            this.cbBlockApproximation.Size = new System.Drawing.Size(122, 17);
            this.cbBlockApproximation.TabIndex = 15;
            this.cbBlockApproximation.Text = "Block Approximation";
            this.cbBlockApproximation.UseVisualStyleBackColor = true;
            this.cbBlockApproximation.CheckedChanged += new System.EventHandler(this.cbRealBlock_CheckedChanged);
            // 
            // groupBox3
            // 
            this.groupBox3.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.groupBox3.Controls.Add(this.rbInterior);
            this.groupBox3.Controls.Add(this.rbHeightmap);
            this.groupBox3.Location = new System.Drawing.Point(225, 156);
            this.groupBox3.Name = "groupBox3";
            this.groupBox3.Size = new System.Drawing.Size(178, 118);
            this.groupBox3.TabIndex = 16;
            this.groupBox3.TabStop = false;
            this.groupBox3.Text = "Render Method";
            // 
            // rbInterior
            // 
            this.rbInterior.AutoSize = true;
            this.rbInterior.Location = new System.Drawing.Point(7, 46);
            this.rbInterior.Name = "rbInterior";
            this.rbInterior.Size = new System.Drawing.Size(123, 17);
            this.rbInterior.TabIndex = 1;
            this.rbInterior.TabStop = true;
            this.rbInterior.Text = "Single Chunk Interior";
            this.rbInterior.UseVisualStyleBackColor = true;
            this.rbInterior.CheckedChanged += new System.EventHandler(this.rbInterior_CheckedChanged_1);
            // 
            // rbHeightmap
            // 
            this.rbHeightmap.AutoSize = true;
            this.rbHeightmap.Checked = true;
            this.rbHeightmap.Location = new System.Drawing.Point(7, 20);
            this.rbHeightmap.Name = "rbHeightmap";
            this.rbHeightmap.Size = new System.Drawing.Size(110, 17);
            this.rbHeightmap.TabIndex = 0;
            this.rbHeightmap.TabStop = true;
            this.rbHeightmap.Text = "Chunk Heightmap";
            this.rbHeightmap.UseVisualStyleBackColor = true;
            this.rbHeightmap.CheckedChanged += new System.EventHandler(this.rbHeightmap_CheckedChanged);
            // 
            // cbRelaxedFeatures
            // 
            this.cbRelaxedFeatures.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.cbRelaxedFeatures.AutoSize = true;
            this.cbRelaxedFeatures.Enabled = false;
            this.cbRelaxedFeatures.Location = new System.Drawing.Point(225, 349);
            this.cbRelaxedFeatures.Name = "cbRelaxedFeatures";
            this.cbRelaxedFeatures.Size = new System.Drawing.Size(109, 17);
            this.cbRelaxedFeatures.TabIndex = 17;
            this.cbRelaxedFeatures.Text = "Relaxed Features";
            this.cbRelaxedFeatures.UseVisualStyleBackColor = true;
            this.cbRelaxedFeatures.CheckedChanged += new System.EventHandler(this.cbRelaxedFeatures_CheckedChanged);
            // 
            // bCreateTerrain
            // 
            this.bCreateTerrain.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.bCreateTerrain.Location = new System.Drawing.Point(12, 98);
            this.bCreateTerrain.Name = "bCreateTerrain";
            this.bCreateTerrain.Size = new System.Drawing.Size(448, 23);
            this.bCreateTerrain.TabIndex = 18;
            this.bCreateTerrain.Text = "Create New Terrain";
            this.bCreateTerrain.UseVisualStyleBackColor = true;
            this.bCreateTerrain.Click += new System.EventHandler(this.bCreateTerrain_Click);
            // 
            // bOpenTerrain
            // 
            this.bOpenTerrain.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.bOpenTerrain.Location = new System.Drawing.Point(12, 127);
            this.bOpenTerrain.Name = "bOpenTerrain";
            this.bOpenTerrain.Size = new System.Drawing.Size(448, 23);
            this.bOpenTerrain.TabIndex = 19;
            this.bOpenTerrain.Text = "Open Terrain";
            this.bOpenTerrain.UseVisualStyleBackColor = true;
            this.bOpenTerrain.Click += new System.EventHandler(this.bOpenTerrain_Click);
            // 
            // TerrainLayerList
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(472, 413);
            this.Controls.Add(this.pbNoise);
            this.Controls.Add(this.bOpenTerrain);
            this.Controls.Add(this.bCreateTerrain);
            this.Controls.Add(this.cbRelaxedFeatures);
            this.Controls.Add(this.groupBox3);
            this.Controls.Add(this.cbBlockApproximation);
            this.Controls.Add(this.cbBlockRounding);
            this.Controls.Add(this.cbWireframe);
            this.Controls.Add(this.groupBox1);
            this.Controls.Add(this.bSave);
            this.MinimumSize = new System.Drawing.Size(488, 452);
            this.Name = "TerrainLayerList";
            this.Text = "TerrainLayerList";
            this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.TerrainLayerList_FormClosing);
            ((System.ComponentModel.ISupportInitialize)(this.nudSeed)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.nudSideLength)).EndInit();
            this.groupBox1.ResumeLayout(false);
            this.groupBox1.PerformLayout();
            ((System.ComponentModel.ISupportInitialize)(this.pbNoise)).EndInit();
            this.groupBox3.ResumeLayout(false);
            this.groupBox3.PerformLayout();
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion
        private System.Windows.Forms.NumericUpDown nudSeed;
        private System.Windows.Forms.Button bRandomize;
        private System.Windows.Forms.Label lSeed;
        private System.Windows.Forms.NumericUpDown nudSideLength;
        private System.Windows.Forms.Label label1;
        private System.Windows.Forms.Button bSave;
        private System.Windows.Forms.GroupBox groupBox1;
        public System.Windows.Forms.CheckBox cbWireframe;
        private System.Windows.Forms.PictureBox pbNoise;
        public System.Windows.Forms.CheckBox cbBlockRounding;
        public System.Windows.Forms.CheckBox cbBlockApproximation;
        private System.Windows.Forms.GroupBox groupBox3;
        public System.Windows.Forms.RadioButton rbHeightmap;
        public System.Windows.Forms.RadioButton rbInterior;
        public System.Windows.Forms.CheckBox cbRelaxedFeatures;
        private System.Windows.Forms.Button bCreateTerrain;
        private System.Windows.Forms.Button bOpenTerrain;
    }
}