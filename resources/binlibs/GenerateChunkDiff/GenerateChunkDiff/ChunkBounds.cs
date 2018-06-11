using System;
using Substrate;

namespace GenerateChunkDiff
{
    internal class ChunkBounds
    {
        public int MinX { get; set; }
        public int MaxX { get; set; }

        public int MinZ { get; set; }
        public int MaxZ { get; set; }

        private bool BoundsExist { get; }
        public bool Success { get; }

        public ChunkBounds(string boundsStr)
        {
            if (boundsStr == null)
            {
                BoundsExist = false;
                Success = true;
            }
            else
            {
                BoundsExist = true;
                try
                {
                    var split = boundsStr.Split(':');
                    MinX = int.Parse(split[0]);
                    MaxX = int.Parse(split[1]);
                    MinZ = int.Parse(split[2]);
                    MaxZ = int.Parse(split[3]);
                    Success = true;
                }
                catch (Exception)
                {
                    Success = false;
                }
            }
        }

        public bool Contains(ChunkRef chunk)
        {
            if (!BoundsExist)
                return true;
            return chunk.X >= MinX && chunk.X <= MaxX && chunk.Z >= MinZ && chunk.Z <= MaxZ;
        }
    }
}