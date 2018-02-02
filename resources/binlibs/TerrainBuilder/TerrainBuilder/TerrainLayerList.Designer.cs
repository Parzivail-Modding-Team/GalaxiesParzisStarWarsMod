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
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(TerrainLayerList));
            this.nudSeed = new System.Windows.Forms.NumericUpDown();
            this.bRandomize = new System.Windows.Forms.Button();
            this.lSeed = new System.Windows.Forms.Label();
            this.nudSideLength = new System.Windows.Forms.NumericUpDown();
            this.lSideLen = new System.Windows.Forms.Label();
            this.groupBox1 = new System.Windows.Forms.GroupBox();
            this.pbNoise = new System.Windows.Forms.PictureBox();
            this.cbWireframe = new System.Windows.Forms.CheckBox();
            this.statusStrip1 = new System.Windows.Forms.StatusStrip();
            this.lRenderStatus = new System.Windows.Forms.ToolStripStatusLabel();
            this.pbRenderStatus = new System.Windows.Forms.ToolStripProgressBar();
            this.bCancelRender = new System.Windows.Forms.ToolStripSplitButton();
            this.cbPauseGen = new System.Windows.Forms.CheckBox();
            this.groupBox2 = new System.Windows.Forms.GroupBox();
            this.pbTerrainColor = new System.Windows.Forms.PictureBox();
            this.lTerrainColor = new System.Windows.Forms.Label();
            this.bManuallyGenerate = new System.Windows.Forms.Button();
            this.toolStrip1 = new System.Windows.Forms.ToolStrip();
            this.bCreate = new System.Windows.Forms.ToolStripButton();
            this.bOpen = new System.Windows.Forms.ToolStripButton();
            this.ddTools = new System.Windows.Forms.ToolStripDropDownButton();
            this.heightmapViewerToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.colorPicker = new System.Windows.Forms.ColorDialog();
            ((System.ComponentModel.ISupportInitialize)(this.nudSeed)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.nudSideLength)).BeginInit();
            this.groupBox1.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)(this.pbNoise)).BeginInit();
            this.statusStrip1.SuspendLayout();
            this.groupBox2.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)(this.pbTerrainColor)).BeginInit();
            this.toolStrip1.SuspendLayout();
            this.SuspendLayout();
            // 
            // nudSeed
            // 
            this.nudSeed.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.nudSeed.Location = new System.Drawing.Point(125, 22);
            this.nudSeed.Name = "nudSeed";
            this.nudSeed.Size = new System.Drawing.Size(317, 20);
            this.nudSeed.TabIndex = 2;
            this.nudSeed.ValueChanged += new System.EventHandler(this.nudSeed_ValueChanged);
            // 
            // bRandomize
            // 
            this.bRandomize.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.bRandomize.BackgroundImageLayout = System.Windows.Forms.ImageLayout.Center;
            this.bRandomize.Image = global::TerrainBuilder.EmbeddedFiles.arrow_switch;
            this.bRandomize.Location = new System.Drawing.Point(96, 20);
            this.bRandomize.Name = "bRandomize";
            this.bRandomize.Size = new System.Drawing.Size(23, 23);
            this.bRandomize.TabIndex = 3;
            this.bRandomize.TextImageRelation = System.Windows.Forms.TextImageRelation.ImageBeforeText;
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
            this.nudSideLength.Location = new System.Drawing.Point(125, 48);
            this.nudSideLength.Maximum = new decimal(new int[] {
            1024,
            0,
            0,
            0});
            this.nudSideLength.Name = "nudSideLength";
            this.nudSideLength.Size = new System.Drawing.Size(317, 20);
            this.nudSideLength.TabIndex = 5;
            this.nudSideLength.Value = new decimal(new int[] {
            128,
            0,
            0,
            0});
            this.nudSideLength.ValueChanged += new System.EventHandler(this.nudSideLength_ValueChanged);
            // 
            // lSideLen
            // 
            this.lSideLen.AutoSize = true;
            this.lSideLen.Location = new System.Drawing.Point(6, 50);
            this.lSideLen.Name = "lSideLen";
            this.lSideLen.Size = new System.Drawing.Size(64, 13);
            this.lSideLen.TabIndex = 6;
            this.lSideLen.Text = "Side Length";
            // 
            // groupBox1
            // 
            this.groupBox1.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.groupBox1.Controls.Add(this.bRandomize);
            this.groupBox1.Controls.Add(this.nudSeed);
            this.groupBox1.Controls.Add(this.lSeed);
            this.groupBox1.Controls.Add(this.nudSideLength);
            this.groupBox1.Controls.Add(this.lSideLen);
            this.groupBox1.Location = new System.Drawing.Point(12, 28);
            this.groupBox1.Name = "groupBox1";
            this.groupBox1.Size = new System.Drawing.Size(448, 80);
            this.groupBox1.TabIndex = 1;
            this.groupBox1.TabStop = false;
            this.groupBox1.Text = "Terrain";
            // 
            // pbNoise
            // 
            this.pbNoise.BorderStyle = System.Windows.Forms.BorderStyle.Fixed3D;
            this.pbNoise.Location = new System.Drawing.Point(12, 114);
            this.pbNoise.Name = "pbNoise";
            this.pbNoise.Size = new System.Drawing.Size(261, 261);
            this.pbNoise.TabIndex = 3;
            this.pbNoise.TabStop = false;
            // 
            // cbWireframe
            // 
            this.cbWireframe.AutoSize = true;
            this.cbWireframe.Location = new System.Drawing.Point(6, 19);
            this.cbWireframe.Name = "cbWireframe";
            this.cbWireframe.Size = new System.Drawing.Size(74, 17);
            this.cbWireframe.TabIndex = 0;
            this.cbWireframe.Text = "Wireframe";
            this.cbWireframe.UseVisualStyleBackColor = true;
            // 
            // statusStrip1
            // 
            this.statusStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.lRenderStatus,
            this.pbRenderStatus,
            this.bCancelRender});
            this.statusStrip1.Location = new System.Drawing.Point(0, 391);
            this.statusStrip1.Name = "statusStrip1";
            this.statusStrip1.Size = new System.Drawing.Size(472, 22);
            this.statusStrip1.TabIndex = 3;
            this.statusStrip1.Text = "statusStrip1";
            // 
            // lRenderStatus
            // 
            this.lRenderStatus.Name = "lRenderStatus";
            this.lRenderStatus.Size = new System.Drawing.Size(39, 17);
            this.lRenderStatus.Text = "Ready";
            // 
            // pbRenderStatus
            // 
            this.pbRenderStatus.Name = "pbRenderStatus";
            this.pbRenderStatus.Size = new System.Drawing.Size(100, 16);
            this.pbRenderStatus.Visible = false;
            // 
            // bCancelRender
            // 
            this.bCancelRender.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
            this.bCancelRender.DropDownButtonWidth = 0;
            this.bCancelRender.Enabled = false;
            this.bCancelRender.Image = global::TerrainBuilder.EmbeddedFiles.cancel;
            this.bCancelRender.ImageTransparentColor = System.Drawing.Color.Magenta;
            this.bCancelRender.Name = "bCancelRender";
            this.bCancelRender.Size = new System.Drawing.Size(21, 20);
            this.bCancelRender.Text = "Cancel Generation";
            this.bCancelRender.Visible = false;
            this.bCancelRender.ButtonClick += new System.EventHandler(this.bCancelGen_Click);
            // 
            // cbPauseGen
            // 
            this.cbPauseGen.AutoSize = true;
            this.cbPauseGen.Location = new System.Drawing.Point(6, 42);
            this.cbPauseGen.Name = "cbPauseGen";
            this.cbPauseGen.Size = new System.Drawing.Size(163, 17);
            this.cbPauseGen.TabIndex = 24;
            this.cbPauseGen.Text = "Pause Generation on Reload";
            this.cbPauseGen.UseVisualStyleBackColor = true;
            this.cbPauseGen.CheckedChanged += new System.EventHandler(this.cbPauseGen_CheckedChanged);
            // 
            // groupBox2
            // 
            this.groupBox2.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.groupBox2.Controls.Add(this.pbTerrainColor);
            this.groupBox2.Controls.Add(this.lTerrainColor);
            this.groupBox2.Controls.Add(this.bManuallyGenerate);
            this.groupBox2.Controls.Add(this.cbWireframe);
            this.groupBox2.Controls.Add(this.cbPauseGen);
            this.groupBox2.Location = new System.Drawing.Point(279, 114);
            this.groupBox2.Name = "groupBox2";
            this.groupBox2.Size = new System.Drawing.Size(181, 261);
            this.groupBox2.TabIndex = 2;
            this.groupBox2.TabStop = false;
            this.groupBox2.Text = "Render";
            // 
            // pbTerrainColor
            // 
            this.pbTerrainColor.BackColor = System.Drawing.SystemColors.Control;
            this.pbTerrainColor.BorderStyle = System.Windows.Forms.BorderStyle.Fixed3D;
            this.pbTerrainColor.Cursor = System.Windows.Forms.Cursors.Cross;
            this.pbTerrainColor.Location = new System.Drawing.Point(9, 107);
            this.pbTerrainColor.Name = "pbTerrainColor";
            this.pbTerrainColor.Size = new System.Drawing.Size(100, 50);
            this.pbTerrainColor.TabIndex = 28;
            this.pbTerrainColor.TabStop = false;
            this.pbTerrainColor.Click += new System.EventHandler(this.pbMinColor_Click);
            // 
            // lTerrainColor
            // 
            this.lTerrainColor.AutoSize = true;
            this.lTerrainColor.Location = new System.Drawing.Point(6, 91);
            this.lTerrainColor.Name = "lTerrainColor";
            this.lTerrainColor.Size = new System.Drawing.Size(67, 13);
            this.lTerrainColor.TabIndex = 26;
            this.lTerrainColor.Text = "Terrain Color";
            // 
            // bManuallyGenerate
            // 
            this.bManuallyGenerate.Enabled = false;
            this.bManuallyGenerate.Location = new System.Drawing.Point(6, 65);
            this.bManuallyGenerate.Name = "bManuallyGenerate";
            this.bManuallyGenerate.Size = new System.Drawing.Size(169, 23);
            this.bManuallyGenerate.TabIndex = 25;
            this.bManuallyGenerate.Text = "Manually Generate";
            this.bManuallyGenerate.UseVisualStyleBackColor = true;
            this.bManuallyGenerate.Click += new System.EventHandler(this.bManuallyGenerate_Click);
            // 
            // toolStrip1
            // 
            this.toolStrip1.GripStyle = System.Windows.Forms.ToolStripGripStyle.Hidden;
            this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.bCreate,
            this.bOpen,
            this.ddTools});
            this.toolStrip1.Location = new System.Drawing.Point(0, 0);
            this.toolStrip1.Name = "toolStrip1";
            this.toolStrip1.Size = new System.Drawing.Size(472, 25);
            this.toolStrip1.TabIndex = 0;
            this.toolStrip1.Text = "toolStrip1";
            // 
            // bCreate
            // 
            this.bCreate.Image = global::TerrainBuilder.EmbeddedFiles.brick_edit;
            this.bCreate.ImageTransparentColor = System.Drawing.Color.Magenta;
            this.bCreate.Name = "bCreate";
            this.bCreate.Size = new System.Drawing.Size(100, 22);
            this.bCreate.Text = "&Create Terrain";
            this.bCreate.Click += new System.EventHandler(this.bCreateTerrain_Click);
            // 
            // bOpen
            // 
            this.bOpen.Image = global::TerrainBuilder.EmbeddedFiles.folder_brick;
            this.bOpen.ImageTransparentColor = System.Drawing.Color.Magenta;
            this.bOpen.Name = "bOpen";
            this.bOpen.Size = new System.Drawing.Size(95, 22);
            this.bOpen.Text = "&Open Terrain";
            this.bOpen.Click += new System.EventHandler(this.bOpenTerrain_Click);
            // 
            // ddTools
            // 
            this.ddTools.DropDownItems.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.heightmapViewerToolStripMenuItem});
            this.ddTools.Image = global::TerrainBuilder.EmbeddedFiles.wrench;
            this.ddTools.ImageTransparentColor = System.Drawing.Color.Magenta;
            this.ddTools.Name = "ddTools";
            this.ddTools.Size = new System.Drawing.Size(64, 22);
            this.ddTools.Text = "Tools";
            // 
            // heightmapViewerToolStripMenuItem
            // 
            this.heightmapViewerToolStripMenuItem.Name = "heightmapViewerToolStripMenuItem";
            this.heightmapViewerToolStripMenuItem.Size = new System.Drawing.Size(172, 22);
            this.heightmapViewerToolStripMenuItem.Text = "Heightmap Viewer";
            this.heightmapViewerToolStripMenuItem.Click += new System.EventHandler(this.heightmapViewerToolStripMenuItem_Click);
            // 
            // colorPicker
            // 
            this.colorPicker.AnyColor = true;
            this.colorPicker.Color = System.Drawing.Color.LimeGreen;
            this.colorPicker.FullOpen = true;
            // 
            // TerrainLayerList
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(472, 413);
            this.Controls.Add(this.toolStrip1);
            this.Controls.Add(this.groupBox2);
            this.Controls.Add(this.statusStrip1);
            this.Controls.Add(this.pbNoise);
            this.Controls.Add(this.groupBox1);
            this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
            this.MinimumSize = new System.Drawing.Size(488, 452);
            this.Name = "TerrainLayerList";
            this.Text = "TerrainLayerList";
            this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.TerrainLayerList_FormClosing);
            ((System.ComponentModel.ISupportInitialize)(this.nudSeed)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.nudSideLength)).EndInit();
            this.groupBox1.ResumeLayout(false);
            this.groupBox1.PerformLayout();
            ((System.ComponentModel.ISupportInitialize)(this.pbNoise)).EndInit();
            this.statusStrip1.ResumeLayout(false);
            this.statusStrip1.PerformLayout();
            this.groupBox2.ResumeLayout(false);
            this.groupBox2.PerformLayout();
            ((System.ComponentModel.ISupportInitialize)(this.pbTerrainColor)).EndInit();
            this.toolStrip1.ResumeLayout(false);
            this.toolStrip1.PerformLayout();
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion
        private System.Windows.Forms.NumericUpDown nudSeed;
        private System.Windows.Forms.Button bRandomize;
        private System.Windows.Forms.Label lSeed;
        private System.Windows.Forms.NumericUpDown nudSideLength;
        private System.Windows.Forms.Label lSideLen;
        private System.Windows.Forms.GroupBox groupBox1;
        public System.Windows.Forms.CheckBox cbWireframe;
        private System.Windows.Forms.PictureBox pbNoise;
        private System.Windows.Forms.StatusStrip statusStrip1;
        public System.Windows.Forms.ToolStripProgressBar pbRenderStatus;
        public System.Windows.Forms.ToolStripStatusLabel lRenderStatus;
        public System.Windows.Forms.ToolStripSplitButton bCancelRender;
        public System.Windows.Forms.CheckBox cbPauseGen;
        private System.Windows.Forms.GroupBox groupBox2;
        private System.Windows.Forms.Button bManuallyGenerate;
        private System.Windows.Forms.ToolStrip toolStrip1;
        private System.Windows.Forms.ToolStripButton bCreate;
        private System.Windows.Forms.ToolStripButton bOpen;
        private System.Windows.Forms.PictureBox pbTerrainColor;
        private System.Windows.Forms.Label lTerrainColor;
        private System.Windows.Forms.ColorDialog colorPicker;
        private System.Windows.Forms.ToolStripDropDownButton ddTools;
        private System.Windows.Forms.ToolStripMenuItem heightmapViewerToolStripMenuItem;
    }
}