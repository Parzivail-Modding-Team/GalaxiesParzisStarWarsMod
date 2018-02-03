namespace TerrainBuilder
{
    public class BackgroundRenderArgs
    {
        public bool Voxels { get; }
        public bool RegenHeightmap { get; }

        public BackgroundRenderArgs(bool voxels, bool regenHeightmap)
        {
            Voxels = voxels;
            RegenHeightmap = regenHeightmap;
        }
    }
}