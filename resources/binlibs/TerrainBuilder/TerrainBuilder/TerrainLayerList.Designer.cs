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
            this.lSideLen = new System.Windows.Forms.Label();
            this.groupBox1 = new System.Windows.Forms.GroupBox();
            this.pbNoise = new System.Windows.Forms.PictureBox();
            this.cbWireframe = new System.Windows.Forms.CheckBox();
            this.bCreateTerrain = new System.Windows.Forms.Button();
            this.bOpenTerrain = new System.Windows.Forms.Button();
            this.pbRender = new System.Windows.Forms.ProgressBar();
            this.LRenderStatus = new System.Windows.Forms.Label();
            this.bCancelGen = new System.Windows.Forms.Button();
            ((System.ComponentModel.ISupportInitialize)(this.nudSeed)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.nudSideLength)).BeginInit();
            this.groupBox1.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)(this.pbNoise)).BeginInit();
            this.SuspendLayout();
            // 
            // nudSeed
            // 
            this.nudSeed.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.nudSeed.Location = new System.Drawing.Point(125, 22);
            this.nudSeed.Name = "nudSeed";
            this.nudSeed.Size = new System.Drawing.Size(301, 20);
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
            this.nudSideLength.Location = new System.Drawing.Point(125, 48);
            this.nudSideLength.Maximum = new decimal(new int[] {
            1024,
            0,
            0,
            0});
            this.nudSideLength.Name = "nudSideLength";
            this.nudSideLength.Size = new System.Drawing.Size(301, 20);
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
            this.groupBox1.Location = new System.Drawing.Point(12, 12);
            this.groupBox1.Name = "groupBox1";
            this.groupBox1.Size = new System.Drawing.Size(432, 80);
            this.groupBox1.TabIndex = 12;
            this.groupBox1.TabStop = false;
            this.groupBox1.Text = "Terrain";
            // 
            // pbNoise
            // 
            this.pbNoise.Location = new System.Drawing.Point(12, 156);
            this.pbNoise.Name = "pbNoise";
            this.pbNoise.Size = new System.Drawing.Size(245, 245);
            this.pbNoise.TabIndex = 3;
            this.pbNoise.TabStop = false;
            // 
            // cbWireframe
            // 
            this.cbWireframe.AutoSize = true;
            this.cbWireframe.Location = new System.Drawing.Point(263, 156);
            this.cbWireframe.Name = "cbWireframe";
            this.cbWireframe.Size = new System.Drawing.Size(74, 17);
            this.cbWireframe.TabIndex = 0;
            this.cbWireframe.Text = "Wireframe";
            this.cbWireframe.UseVisualStyleBackColor = true;
            // 
            // bCreateTerrain
            // 
            this.bCreateTerrain.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.bCreateTerrain.Location = new System.Drawing.Point(12, 98);
            this.bCreateTerrain.Name = "bCreateTerrain";
            this.bCreateTerrain.Size = new System.Drawing.Size(432, 23);
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
            this.bOpenTerrain.Size = new System.Drawing.Size(432, 23);
            this.bOpenTerrain.TabIndex = 19;
            this.bOpenTerrain.Text = "Open Terrain";
            this.bOpenTerrain.UseVisualStyleBackColor = true;
            this.bOpenTerrain.Click += new System.EventHandler(this.bOpenTerrain_Click);
            // 
            // pbRender
            // 
            this.pbRender.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.pbRender.Location = new System.Drawing.Point(263, 378);
            this.pbRender.Name = "pbRender";
            this.pbRender.Size = new System.Drawing.Size(197, 23);
            this.pbRender.TabIndex = 20;
            // 
            // LRenderStatus
            // 
            this.LRenderStatus.AutoSize = true;
            this.LRenderStatus.Location = new System.Drawing.Point(263, 333);
            this.LRenderStatus.Name = "LRenderStatus";
            this.LRenderStatus.Size = new System.Drawing.Size(103, 13);
            this.LRenderStatus.TabIndex = 21;
            this.LRenderStatus.Text = "Generation Progress";
            // 
            // bCancelGen
            // 
            this.bCancelGen.Location = new System.Drawing.Point(263, 349);
            this.bCancelGen.Name = "bCancelGen";
            this.bCancelGen.Size = new System.Drawing.Size(75, 23);
            this.bCancelGen.TabIndex = 22;
            this.bCancelGen.Text = "Cancel Gen";
            this.bCancelGen.UseVisualStyleBackColor = true;
            this.bCancelGen.Click += new System.EventHandler(this.bCancelGen_Click);
            // 
            // TerrainLayerList
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(472, 413);
            this.Controls.Add(this.bCancelGen);
            this.Controls.Add(this.LRenderStatus);
            this.Controls.Add(this.pbRender);
            this.Controls.Add(this.pbNoise);
            this.Controls.Add(this.bOpenTerrain);
            this.Controls.Add(this.bCreateTerrain);
            this.Controls.Add(this.cbWireframe);
            this.Controls.Add(this.groupBox1);
            this.MinimumSize = new System.Drawing.Size(488, 452);
            this.Name = "TerrainLayerList";
            this.Text = "TerrainLayerList";
            this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.TerrainLayerList_FormClosing);
            ((System.ComponentModel.ISupportInitialize)(this.nudSeed)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.nudSideLength)).EndInit();
            this.groupBox1.ResumeLayout(false);
            this.groupBox1.PerformLayout();
            ((System.ComponentModel.ISupportInitialize)(this.pbNoise)).EndInit();
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
        private System.Windows.Forms.Button bCreateTerrain;
        private System.Windows.Forms.Button bOpenTerrain;
        internal System.Windows.Forms.ProgressBar pbRender;
        private System.Windows.Forms.Label LRenderStatus;
        private System.Windows.Forms.Button bCancelGen;
    }
}