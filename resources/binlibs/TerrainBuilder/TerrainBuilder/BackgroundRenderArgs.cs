namespace TerrainBuilder
{
    public class BackgroundRenderArgs
    {
        public bool Voxels { get; }

        public BackgroundRenderArgs(bool voxels)
        {
            Voxels = voxels;
        }
    }
}