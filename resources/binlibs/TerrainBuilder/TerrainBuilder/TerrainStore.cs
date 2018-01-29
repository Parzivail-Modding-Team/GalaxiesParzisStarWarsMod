namespace TerrainBuilder
{
    class TerrainStore
    {
        public int WaterLevel { get; set; }
        public int TreesPerChunk { get; set; }
        public bool TreesBelowWaterLevel { get; set; }
        public float ThLayer1 { get; set; }
        public float ThLayer2 { get; set; }
    }
}
