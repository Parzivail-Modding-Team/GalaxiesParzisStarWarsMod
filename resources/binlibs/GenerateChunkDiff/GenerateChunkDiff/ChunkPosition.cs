namespace GenerateChunkDiff
{
    internal class ChunkPosition
    {
        public int X { get; }
        public int Z { get; }

        public ChunkPosition(int x, int z)
        {
            X = x;
            Z = z;
        }

        public override string ToString()
        {
            return $"({X},{Z})";
        }
    }
}