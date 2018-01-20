using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace TerrainBuilder
{
    class TerrainStore
    {
        public List<TerrainLayer> TerrainData { get; set; }
        public int WaterLevel { get; set; }
        public int TreesPerChunk { get; set; }
        public bool TreesBelowWaterLevel { get; set; }
        public float ThLayer1 { get; set; }
        public float ThLayer2 { get; set; }
    }
}
