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
            this.dgvLayers = new System.Windows.Forms.DataGridView();
            this.bAddLayer = new System.Windows.Forms.Button();
            this.nudSeed = new System.Windows.Forms.NumericUpDown();
            this.bRandomize = new System.Windows.Forms.Button();
            this.lSeed = new System.Windows.Forms.Label();
            this.nudSideLength = new System.Windows.Forms.NumericUpDown();
            this.label1 = new System.Windows.Forms.Label();
            this.bSave = new System.Windows.Forms.Button();
            this.nudWaterLevel = new System.Windows.Forms.NumericUpDown();
            this.label2 = new System.Windows.Forms.Label();
            this.bSaveData = new System.Windows.Forms.Button();
            this.bLoadData = new System.Windows.Forms.Button();
            this.groupBox1 = new System.Windows.Forms.GroupBox();
            this.groupBox2 = new System.Windows.Forms.GroupBox();
            this.label7 = new System.Windows.Forms.Label();
            this.label6 = new System.Windows.Forms.Label();
            this.nudLayer2 = new System.Windows.Forms.NumericUpDown();
            this.nudLayer1 = new System.Windows.Forms.NumericUpDown();
            this.label5 = new System.Windows.Forms.Label();
            this.nudCaveScale = new System.Windows.Forms.NumericUpDown();
            this.label4 = new System.Windows.Forms.Label();
            this.nudCaveThreshold = new System.Windows.Forms.NumericUpDown();
            this.pbNoise = new System.Windows.Forms.PictureBox();
            this.cbSubmarineTrees = new System.Windows.Forms.CheckBox();
            this.nudTreesPerChunk = new System.Windows.Forms.NumericUpDown();
            this.label3 = new System.Windows.Forms.Label();
            this.cbWireframe = new System.Windows.Forms.CheckBox();
            this.cbBlockRounding = new System.Windows.Forms.CheckBox();
            this.cbBlockApproximation = new System.Windows.Forms.CheckBox();
            this.groupBox3 = new System.Windows.Forms.GroupBox();
            this.rbInterior = new System.Windows.Forms.RadioButton();
            this.rbHeightmap = new System.Windows.Forms.RadioButton();
            this.cbRelaxedFeatures = new System.Windows.Forms.CheckBox();
            ((System.ComponentModel.ISupportInitialize)(this.dgvLayers)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.nudSeed)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.nudSideLength)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.nudWaterLevel)).BeginInit();
            this.groupBox1.SuspendLayout();
            this.groupBox2.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)(this.nudLayer2)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.nudLayer1)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.nudCaveScale)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.nudCaveThreshold)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.pbNoise)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.nudTreesPerChunk)).BeginInit();
            this.groupBox3.SuspendLayout();
            this.SuspendLayout();
            // 
            // dgvLayers
            // 
            this.dgvLayers.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom) 
            | System.Windows.Forms.AnchorStyles.Left)));
            this.dgvLayers.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
            this.dgvLayers.Location = new System.Drawing.Point(9, 129);
            this.dgvLayers.Name = "dgvLayers";
            this.dgvLayers.Size = new System.Drawing.Size(426, 391);
            this.dgvLayers.TabIndex = 0;
            this.dgvLayers.CellEndEdit += new System.Windows.Forms.DataGridViewCellEventHandler(this.dgvLayers_CellEndEdit);
            this.dgvLayers.CellEnter += new System.Windows.Forms.DataGridViewCellEventHandler(this.dgvLayers_CellEnter);
            this.dgvLayers.RowsAdded += new System.Windows.Forms.DataGridViewRowsAddedEventHandler(this.dgvLayers_RowsAdded);
            this.dgvLayers.RowsRemoved += new System.Windows.Forms.DataGridViewRowsRemovedEventHandler(this.dgvLayers_RowsRemoved);
            this.dgvLayers.SelectionChanged += new System.EventHandler(this.dgvLayers_SelectionChanged);
            // 
            // bAddLayer
            // 
            this.bAddLayer.Location = new System.Drawing.Point(9, 100);
            this.bAddLayer.Name = "bAddLayer";
            this.bAddLayer.Size = new System.Drawing.Size(426, 23);
            this.bAddLayer.TabIndex = 1;
            this.bAddLayer.Text = "Add Layer";
            this.bAddLayer.UseVisualStyleBackColor = true;
            this.bAddLayer.Click += new System.EventHandler(this.bAddLayer_Click);
            // 
            // nudSeed
            // 
            this.nudSeed.Location = new System.Drawing.Point(128, 22);
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
            this.nudSideLength.Increment = new decimal(new int[] {
            16,
            0,
            0,
            0});
            this.nudSideLength.Location = new System.Drawing.Point(128, 48);
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
            this.bSave.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Left)));
            this.bSave.Location = new System.Drawing.Point(12, 544);
            this.bSave.Name = "bSave";
            this.bSave.Size = new System.Drawing.Size(435, 23);
            this.bSave.TabIndex = 7;
            this.bSave.Text = "Export Java Terrain Generator";
            this.bSave.UseVisualStyleBackColor = true;
            this.bSave.Click += new System.EventHandler(this.bSave_Click);
            // 
            // nudWaterLevel
            // 
            this.nudWaterLevel.Location = new System.Drawing.Point(128, 74);
            this.nudWaterLevel.Maximum = new decimal(new int[] {
            195,
            0,
            0,
            0});
            this.nudWaterLevel.Minimum = new decimal(new int[] {
            60,
            0,
            0,
            -2147483648});
            this.nudWaterLevel.Name = "nudWaterLevel";
            this.nudWaterLevel.Size = new System.Drawing.Size(307, 20);
            this.nudWaterLevel.TabIndex = 8;
            this.nudWaterLevel.ValueChanged += new System.EventHandler(this.nudWaterLevel_ValueChanged);
            // 
            // label2
            // 
            this.label2.AutoSize = true;
            this.label2.Location = new System.Drawing.Point(6, 76);
            this.label2.Name = "label2";
            this.label2.Size = new System.Drawing.Size(65, 13);
            this.label2.TabIndex = 9;
            this.label2.Text = "Water Level";
            // 
            // bSaveData
            // 
            this.bSaveData.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Left)));
            this.bSaveData.Location = new System.Drawing.Point(12, 573);
            this.bSaveData.Name = "bSaveData";
            this.bSaveData.Size = new System.Drawing.Size(150, 23);
            this.bSaveData.TabIndex = 10;
            this.bSaveData.Text = "Save Terrain JSON";
            this.bSaveData.UseVisualStyleBackColor = true;
            this.bSaveData.Click += new System.EventHandler(this.bSaveData_Click);
            // 
            // bLoadData
            // 
            this.bLoadData.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Left)));
            this.bLoadData.Location = new System.Drawing.Point(297, 573);
            this.bLoadData.Name = "bLoadData";
            this.bLoadData.Size = new System.Drawing.Size(150, 23);
            this.bLoadData.TabIndex = 11;
            this.bLoadData.Text = "Load Terrain JSON";
            this.bLoadData.UseVisualStyleBackColor = true;
            this.bLoadData.Click += new System.EventHandler(this.bLoadData_Click);
            // 
            // groupBox1
            // 
            this.groupBox1.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom) 
            | System.Windows.Forms.AnchorStyles.Left)));
            this.groupBox1.Controls.Add(this.bRandomize);
            this.groupBox1.Controls.Add(this.bAddLayer);
            this.groupBox1.Controls.Add(this.nudSeed);
            this.groupBox1.Controls.Add(this.label2);
            this.groupBox1.Controls.Add(this.dgvLayers);
            this.groupBox1.Controls.Add(this.lSeed);
            this.groupBox1.Controls.Add(this.nudWaterLevel);
            this.groupBox1.Controls.Add(this.nudSideLength);
            this.groupBox1.Controls.Add(this.label1);
            this.groupBox1.Location = new System.Drawing.Point(12, 12);
            this.groupBox1.Name = "groupBox1";
            this.groupBox1.Size = new System.Drawing.Size(441, 526);
            this.groupBox1.TabIndex = 12;
            this.groupBox1.TabStop = false;
            this.groupBox1.Text = "Terrain";
            // 
            // groupBox2
            // 
            this.groupBox2.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom) 
            | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.groupBox2.Controls.Add(this.label7);
            this.groupBox2.Controls.Add(this.label6);
            this.groupBox2.Controls.Add(this.nudLayer2);
            this.groupBox2.Controls.Add(this.nudLayer1);
            this.groupBox2.Controls.Add(this.label5);
            this.groupBox2.Controls.Add(this.nudCaveScale);
            this.groupBox2.Controls.Add(this.label4);
            this.groupBox2.Controls.Add(this.nudCaveThreshold);
            this.groupBox2.Controls.Add(this.pbNoise);
            this.groupBox2.Controls.Add(this.cbSubmarineTrees);
            this.groupBox2.Controls.Add(this.nudTreesPerChunk);
            this.groupBox2.Controls.Add(this.label3);
            this.groupBox2.Location = new System.Drawing.Point(459, 12);
            this.groupBox2.Name = "groupBox2";
            this.groupBox2.Size = new System.Drawing.Size(306, 460);
            this.groupBox2.TabIndex = 13;
            this.groupBox2.TabStop = false;
            this.groupBox2.Text = "Decoration";
            // 
            // label7
            // 
            this.label7.AutoSize = true;
            this.label7.Enabled = false;
            this.label7.Location = new System.Drawing.Point(6, 96);
            this.label7.Name = "label7";
            this.label7.Size = new System.Drawing.Size(92, 13);
            this.label7.TabIndex = 11;
            this.label7.Text = "Layer 2 Threshold";
            // 
            // label6
            // 
            this.label6.AutoSize = true;
            this.label6.Enabled = false;
            this.label6.Location = new System.Drawing.Point(6, 70);
            this.label6.Name = "label6";
            this.label6.Size = new System.Drawing.Size(92, 13);
            this.label6.TabIndex = 10;
            this.label6.Text = "Layer 1 Threshold";
            // 
            // nudLayer2
            // 
            this.nudLayer2.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.nudLayer2.DecimalPlaces = 2;
            this.nudLayer2.Enabled = false;
            this.nudLayer2.Increment = new decimal(new int[] {
            1,
            0,
            0,
            131072});
            this.nudLayer2.Location = new System.Drawing.Point(180, 94);
            this.nudLayer2.Maximum = new decimal(new int[] {
            1,
            0,
            0,
            0});
            this.nudLayer2.Name = "nudLayer2";
            this.nudLayer2.Size = new System.Drawing.Size(120, 20);
            this.nudLayer2.TabIndex = 9;
            this.nudLayer2.Value = new decimal(new int[] {
            6,
            0,
            0,
            65536});
            this.nudLayer2.ValueChanged += new System.EventHandler(this.nudLayer2_ValueChanged);
            // 
            // nudLayer1
            // 
            this.nudLayer1.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.nudLayer1.DecimalPlaces = 2;
            this.nudLayer1.Enabled = false;
            this.nudLayer1.Increment = new decimal(new int[] {
            1,
            0,
            0,
            131072});
            this.nudLayer1.Location = new System.Drawing.Point(180, 68);
            this.nudLayer1.Maximum = new decimal(new int[] {
            1,
            0,
            0,
            0});
            this.nudLayer1.Name = "nudLayer1";
            this.nudLayer1.Size = new System.Drawing.Size(120, 20);
            this.nudLayer1.TabIndex = 8;
            this.nudLayer1.Value = new decimal(new int[] {
            9,
            0,
            0,
            65536});
            this.nudLayer1.ValueChanged += new System.EventHandler(this.nudLayer1_ValueChanged);
            // 
            // label5
            // 
            this.label5.AutoSize = true;
            this.label5.Enabled = false;
            this.label5.Location = new System.Drawing.Point(3, 338);
            this.label5.Name = "label5";
            this.label5.Size = new System.Drawing.Size(62, 13);
            this.label5.TabIndex = 7;
            this.label5.Text = "Cave Scale";
            // 
            // nudCaveScale
            // 
            this.nudCaveScale.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.nudCaveScale.DecimalPlaces = 2;
            this.nudCaveScale.Enabled = false;
            this.nudCaveScale.Location = new System.Drawing.Point(177, 336);
            this.nudCaveScale.Maximum = new decimal(new int[] {
            500,
            0,
            0,
            0});
            this.nudCaveScale.Minimum = new decimal(new int[] {
            1,
            0,
            0,
            0});
            this.nudCaveScale.Name = "nudCaveScale";
            this.nudCaveScale.Size = new System.Drawing.Size(120, 20);
            this.nudCaveScale.TabIndex = 6;
            this.nudCaveScale.Value = new decimal(new int[] {
            10,
            0,
            0,
            0});
            this.nudCaveScale.ValueChanged += new System.EventHandler(this.nudCaveScale_ValueChanged);
            // 
            // label4
            // 
            this.label4.AutoSize = true;
            this.label4.Enabled = false;
            this.label4.Location = new System.Drawing.Point(3, 312);
            this.label4.Name = "label4";
            this.label4.Size = new System.Drawing.Size(82, 13);
            this.label4.TabIndex = 5;
            this.label4.Text = "Cave Threshold";
            // 
            // nudCaveThreshold
            // 
            this.nudCaveThreshold.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.nudCaveThreshold.DecimalPlaces = 2;
            this.nudCaveThreshold.Enabled = false;
            this.nudCaveThreshold.Increment = new decimal(new int[] {
            1,
            0,
            0,
            131072});
            this.nudCaveThreshold.Location = new System.Drawing.Point(177, 310);
            this.nudCaveThreshold.Maximum = new decimal(new int[] {
            1,
            0,
            0,
            0});
            this.nudCaveThreshold.Name = "nudCaveThreshold";
            this.nudCaveThreshold.Size = new System.Drawing.Size(120, 20);
            this.nudCaveThreshold.TabIndex = 4;
            this.nudCaveThreshold.ValueChanged += new System.EventHandler(this.nudCaveThreshold_ValueChanged);
            // 
            // pbNoise
            // 
            this.pbNoise.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Left)));
            this.pbNoise.Location = new System.Drawing.Point(6, 354);
            this.pbNoise.Name = "pbNoise";
            this.pbNoise.Size = new System.Drawing.Size(100, 100);
            this.pbNoise.TabIndex = 3;
            this.pbNoise.TabStop = false;
            // 
            // cbSubmarineTrees
            // 
            this.cbSubmarineTrees.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.cbSubmarineTrees.AutoSize = true;
            this.cbSubmarineTrees.Location = new System.Drawing.Point(154, 45);
            this.cbSubmarineTrees.Name = "cbSubmarineTrees";
            this.cbSubmarineTrees.Size = new System.Drawing.Size(146, 17);
            this.cbSubmarineTrees.TabIndex = 2;
            this.cbSubmarineTrees.Text = "Trees Below Water Level";
            this.cbSubmarineTrees.UseVisualStyleBackColor = true;
            this.cbSubmarineTrees.CheckedChanged += new System.EventHandler(this.cbSubmarineTrees_CheckedChanged);
            // 
            // nudTreesPerChunk
            // 
            this.nudTreesPerChunk.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.nudTreesPerChunk.Location = new System.Drawing.Point(180, 19);
            this.nudTreesPerChunk.Name = "nudTreesPerChunk";
            this.nudTreesPerChunk.Size = new System.Drawing.Size(120, 20);
            this.nudTreesPerChunk.TabIndex = 1;
            this.nudTreesPerChunk.ValueChanged += new System.EventHandler(this.nudTreesPerChunk_ValueChanged);
            // 
            // label3
            // 
            this.label3.AutoSize = true;
            this.label3.Location = new System.Drawing.Point(6, 21);
            this.label3.Name = "label3";
            this.label3.Size = new System.Drawing.Size(86, 13);
            this.label3.TabIndex = 0;
            this.label3.Text = "Trees per Chunk";
            // 
            // cbWireframe
            // 
            this.cbWireframe.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right)));
            this.cbWireframe.AutoSize = true;
            this.cbWireframe.Location = new System.Drawing.Point(459, 478);
            this.cbWireframe.Name = "cbWireframe";
            this.cbWireframe.Size = new System.Drawing.Size(48, 17);
            this.cbWireframe.TabIndex = 0;
            this.cbWireframe.Text = "Wire";
            this.cbWireframe.UseVisualStyleBackColor = true;
            // 
            // cbBlockRounding
            // 
            this.cbBlockRounding.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right)));
            this.cbBlockRounding.AutoSize = true;
            this.cbBlockRounding.Checked = true;
            this.cbBlockRounding.CheckState = System.Windows.Forms.CheckState.Checked;
            this.cbBlockRounding.Location = new System.Drawing.Point(459, 501);
            this.cbBlockRounding.Name = "cbBlockRounding";
            this.cbBlockRounding.Size = new System.Drawing.Size(102, 17);
            this.cbBlockRounding.TabIndex = 14;
            this.cbBlockRounding.Text = "Block Rounding";
            this.cbBlockRounding.UseVisualStyleBackColor = true;
            this.cbBlockRounding.CheckedChanged += new System.EventHandler(this.cbBlockRounding_CheckedChanged);
            // 
            // cbBlockApproximation
            // 
            this.cbBlockApproximation.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right)));
            this.cbBlockApproximation.AutoSize = true;
            this.cbBlockApproximation.Location = new System.Drawing.Point(459, 524);
            this.cbBlockApproximation.Name = "cbBlockApproximation";
            this.cbBlockApproximation.Size = new System.Drawing.Size(122, 17);
            this.cbBlockApproximation.TabIndex = 15;
            this.cbBlockApproximation.Text = "Block Approximation";
            this.cbBlockApproximation.UseVisualStyleBackColor = true;
            this.cbBlockApproximation.CheckedChanged += new System.EventHandler(this.cbRealBlock_CheckedChanged);
            // 
            // groupBox3
            // 
            this.groupBox3.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right)));
            this.groupBox3.Controls.Add(this.rbInterior);
            this.groupBox3.Controls.Add(this.rbHeightmap);
            this.groupBox3.Location = new System.Drawing.Point(587, 478);
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
            this.cbRelaxedFeatures.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right)));
            this.cbRelaxedFeatures.AutoSize = true;
            this.cbRelaxedFeatures.Enabled = false;
            this.cbRelaxedFeatures.Location = new System.Drawing.Point(459, 547);
            this.cbRelaxedFeatures.Name = "cbRelaxedFeatures";
            this.cbRelaxedFeatures.Size = new System.Drawing.Size(109, 17);
            this.cbRelaxedFeatures.TabIndex = 17;
            this.cbRelaxedFeatures.Text = "Relaxed Features";
            this.cbRelaxedFeatures.UseVisualStyleBackColor = true;
            this.cbRelaxedFeatures.CheckedChanged += new System.EventHandler(this.cbRelaxedFeatures_CheckedChanged);
            // 
            // TerrainLayerList
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(777, 608);
            this.Controls.Add(this.cbRelaxedFeatures);
            this.Controls.Add(this.groupBox3);
            this.Controls.Add(this.cbBlockApproximation);
            this.Controls.Add(this.cbBlockRounding);
            this.Controls.Add(this.cbWireframe);
            this.Controls.Add(this.groupBox2);
            this.Controls.Add(this.groupBox1);
            this.Controls.Add(this.bLoadData);
            this.Controls.Add(this.bSaveData);
            this.Controls.Add(this.bSave);
            this.MinimumSize = new System.Drawing.Size(793, 647);
            this.Name = "TerrainLayerList";
            this.Text = "TerrainLayerList";
            this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.TerrainLayerList_FormClosing);
            ((System.ComponentModel.ISupportInitialize)(this.dgvLayers)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.nudSeed)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.nudSideLength)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.nudWaterLevel)).EndInit();
            this.groupBox1.ResumeLayout(false);
            this.groupBox1.PerformLayout();
            this.groupBox2.ResumeLayout(false);
            this.groupBox2.PerformLayout();
            ((System.ComponentModel.ISupportInitialize)(this.nudLayer2)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.nudLayer1)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.nudCaveScale)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.nudCaveThreshold)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.pbNoise)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.nudTreesPerChunk)).EndInit();
            this.groupBox3.ResumeLayout(false);
            this.groupBox3.PerformLayout();
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.DataGridView dgvLayers;
        private System.Windows.Forms.Button bAddLayer;
        private System.Windows.Forms.NumericUpDown nudSeed;
        private System.Windows.Forms.Button bRandomize;
        private System.Windows.Forms.Label lSeed;
        private System.Windows.Forms.NumericUpDown nudSideLength;
        private System.Windows.Forms.Label label1;
        private System.Windows.Forms.Button bSave;
        public System.Windows.Forms.NumericUpDown nudWaterLevel;
        private System.Windows.Forms.Label label2;
        private System.Windows.Forms.Button bSaveData;
        private System.Windows.Forms.Button bLoadData;
        private System.Windows.Forms.GroupBox groupBox1;
        private System.Windows.Forms.GroupBox groupBox2;
        public System.Windows.Forms.CheckBox cbWireframe;
        public System.Windows.Forms.NumericUpDown nudTreesPerChunk;
        private System.Windows.Forms.Label label3;
        public System.Windows.Forms.CheckBox cbSubmarineTrees;
        private System.Windows.Forms.PictureBox pbNoise;
        public System.Windows.Forms.CheckBox cbBlockRounding;
        public System.Windows.Forms.CheckBox cbBlockApproximation;
        private System.Windows.Forms.Label label4;
        public System.Windows.Forms.NumericUpDown nudCaveThreshold;
        private System.Windows.Forms.GroupBox groupBox3;
        public System.Windows.Forms.RadioButton rbHeightmap;
        public System.Windows.Forms.RadioButton rbInterior;
        private System.Windows.Forms.Label label5;
        public System.Windows.Forms.NumericUpDown nudCaveScale;
        public System.Windows.Forms.NumericUpDown nudLayer1;
        private System.Windows.Forms.Label label7;
        private System.Windows.Forms.Label label6;
        public System.Windows.Forms.NumericUpDown nudLayer2;
        public System.Windows.Forms.CheckBox cbRelaxedFeatures;
    }
}