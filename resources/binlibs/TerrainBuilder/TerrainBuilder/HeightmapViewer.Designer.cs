namespace TerrainBuilder
{
    partial class HeightmapViewer
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
            this.toolStrip1 = new System.Windows.Forms.ToolStrip();
            this.bCreate = new System.Windows.Forms.ToolStripButton();
            this.bOpen = new System.Windows.Forms.ToolStripButton();
            this.groupBox1 = new System.Windows.Forms.GroupBox();
            this.bRandomize = new System.Windows.Forms.Button();
            this.nudSeed = new System.Windows.Forms.NumericUpDown();
            this.lSeed = new System.Windows.Forms.Label();
            this.nudSideLength = new System.Windows.Forms.NumericUpDown();
            this.lSideLen = new System.Windows.Forms.Label();
            this.pbHeightmap = new TerrainBuilder.PictureBoxWithInterpolationMode();
            this.toolStrip1.SuspendLayout();
            this.groupBox1.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)(this.nudSeed)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.nudSideLength)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.pbHeightmap)).BeginInit();
            this.SuspendLayout();
            // 
            // toolStrip1
            // 
            this.toolStrip1.GripStyle = System.Windows.Forms.ToolStripGripStyle.Hidden;
            this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.bCreate,
            this.bOpen});
            this.toolStrip1.Location = new System.Drawing.Point(0, 0);
            this.toolStrip1.Name = "toolStrip1";
            this.toolStrip1.Size = new System.Drawing.Size(468, 25);
            this.toolStrip1.TabIndex = 1;
            this.toolStrip1.Text = "toolStrip1";
            // 
            // bCreate
            // 
            this.bCreate.Image = global::TerrainBuilder.EmbeddedFiles.brick_edit;
            this.bCreate.ImageTransparentColor = System.Drawing.Color.Magenta;
            this.bCreate.Name = "bCreate";
            this.bCreate.Size = new System.Drawing.Size(100, 22);
            this.bCreate.Text = "&Create Terrain";
            this.bCreate.Click += new System.EventHandler(this.bCreate_Click);
            // 
            // bOpen
            // 
            this.bOpen.Image = global::TerrainBuilder.EmbeddedFiles.folder_brick;
            this.bOpen.ImageTransparentColor = System.Drawing.Color.Magenta;
            this.bOpen.Name = "bOpen";
            this.bOpen.Size = new System.Drawing.Size(95, 22);
            this.bOpen.Text = "&Open Terrain";
            this.bOpen.Click += new System.EventHandler(this.bOpen_Click);
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
            this.groupBox1.Size = new System.Drawing.Size(444, 80);
            this.groupBox1.TabIndex = 2;
            this.groupBox1.TabStop = false;
            this.groupBox1.Text = "Terrain";
            // 
            // bRandomize
            // 
            this.bRandomize.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.bRandomize.BackgroundImageLayout = System.Windows.Forms.ImageLayout.Center;
            this.bRandomize.Image = global::TerrainBuilder.EmbeddedFiles.arrow_switch;
            this.bRandomize.Location = new System.Drawing.Point(92, 20);
            this.bRandomize.Name = "bRandomize";
            this.bRandomize.Size = new System.Drawing.Size(23, 23);
            this.bRandomize.TabIndex = 3;
            this.bRandomize.TextImageRelation = System.Windows.Forms.TextImageRelation.ImageBeforeText;
            this.bRandomize.UseVisualStyleBackColor = true;
            this.bRandomize.Click += new System.EventHandler(this.bRandomize_Click);
            // 
            // nudSeed
            // 
            this.nudSeed.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.nudSeed.Location = new System.Drawing.Point(121, 22);
            this.nudSeed.Name = "nudSeed";
            this.nudSeed.Size = new System.Drawing.Size(317, 20);
            this.nudSeed.TabIndex = 2;
            this.nudSeed.ValueChanged += new System.EventHandler(this.nudSeed_ValueChanged);
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
            this.nudSideLength.Location = new System.Drawing.Point(121, 48);
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
            this.lSideLen.Size = new System.Drawing.Size(60, 13);
            this.lSideLen.TabIndex = 6;
            this.lSideLen.Text = "Image SIze";
            // 
            // pbHeightmap
            // 
            this.pbHeightmap.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom) 
            | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.pbHeightmap.BorderStyle = System.Windows.Forms.BorderStyle.Fixed3D;
            this.pbHeightmap.InterpolationMode = System.Drawing.Drawing2D.InterpolationMode.NearestNeighbor;
            this.pbHeightmap.Location = new System.Drawing.Point(12, 114);
            this.pbHeightmap.Name = "pbHeightmap";
            this.pbHeightmap.Size = new System.Drawing.Size(438, 438);
            this.pbHeightmap.SizeMode = System.Windows.Forms.PictureBoxSizeMode.Zoom;
            this.pbHeightmap.TabIndex = 3;
            this.pbHeightmap.TabStop = false;
            // 
            // HeightmapViewer
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(468, 564);
            this.Controls.Add(this.pbHeightmap);
            this.Controls.Add(this.groupBox1);
            this.Controls.Add(this.toolStrip1);
            this.Name = "HeightmapViewer";
            this.Text = "HeightmapViewer";
            this.Load += new System.EventHandler(this.HeightmapViewer_Load);
            this.toolStrip1.ResumeLayout(false);
            this.toolStrip1.PerformLayout();
            this.groupBox1.ResumeLayout(false);
            this.groupBox1.PerformLayout();
            ((System.ComponentModel.ISupportInitialize)(this.nudSeed)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.nudSideLength)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.pbHeightmap)).EndInit();
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.ToolStrip toolStrip1;
        private System.Windows.Forms.ToolStripButton bCreate;
        private System.Windows.Forms.ToolStripButton bOpen;
        private System.Windows.Forms.GroupBox groupBox1;
        private System.Windows.Forms.Button bRandomize;
        private System.Windows.Forms.NumericUpDown nudSeed;
        private System.Windows.Forms.Label lSeed;
        private System.Windows.Forms.NumericUpDown nudSideLength;
        private System.Windows.Forms.Label lSideLen;
        private PictureBoxWithInterpolationMode pbHeightmap;
    }
}