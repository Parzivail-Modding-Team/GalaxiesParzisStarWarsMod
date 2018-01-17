using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using ShellProgressBar;
using Substrate;
using Substrate.Nbt;

namespace GenerateChunkDiff
{
    class Program
    {
        static void Main(string[] args)
        {
            var world = AnvilWorld.Open(@"E:\MultiMC\instances\Forge 1.7.10\minecraft\saves\ChunkDiff1");
            var worldOriginal = AnvilWorld.Open(@"E:\MultiMC\instances\Forge 1.7.10\minecraft\saves\ChunkDiff1_Original");

            const int dim = 0;
            var manager = world.GetChunkManager(dim);
            var managerOriginal = worldOriginal.GetChunkManager(dim);
            var numChunks = managerOriginal.Count();

            const int chunkMinX = -3; // 14,9
            const int chunkMinZ = 13; // 15, 10
            const int chunkMaxX = -3;
            const int chunkMaxZ = 13;

            var diff = new ChunkDiff();

            var options = new ProgressBarOptions
            {
                ForegroundColor = ConsoleColor.Green,
                BackgroundColor = ConsoleColor.DarkGray
            };

            using (var pbar = new ProgressBar(numChunks, "Chunks Processed", options))
            {
                foreach (var chunk in manager)
                {
                    pbar.Tick();

                    if (chunk.X > chunkMaxX || 
                        chunk.X < chunkMinX || 
                        chunk.Z > chunkMaxZ || 
                        chunk.Z < chunkMinZ || 
                        !managerOriginal.ChunkExists(chunk.X, chunk.Z))
                        continue;

                    var pos = new ChunkPosition(chunk.X, chunk.Z);
                    var otherChunk = managerOriginal.GetChunk(chunk.X, chunk.Z);

                    for (var y = 0; y < 256; y++)
                    {
                        for (var x = 0; x < 16; x++)
                        {
                            for (var z = 0; z < 16; z++)
                            {
                                var blockId = chunk.Blocks.GetID(x, y, z);
                                var blockIdOriginal = otherChunk.Blocks.GetID(x, y, z);

                                if (blockIdOriginal == blockId)
                                    continue;

                                diff.Add(pos, new BlockPosition(x, y, z), new BlockDiff(blockId));
                            }
                        }
                    }

                    // Save after each chunk so we can release unneeded chunks back to the system
                    world.GetChunkManager(dim).Save();
                }

                pbar.Tick();
            }
            
            diff.Save("diff.cdf");
        }
    }

    internal class ChunkDiff : Dictionary<ChunkPosition, List<KeyValuePair<BlockPosition, BlockDiff>>>
    {
        public readonly int Version = 1;

        public void Save(string filename)
        {
            var sw = new StreamWriter(filename);
            using (var f = new BinaryWriter(sw.BaseStream))
            {
                var ident = "CDF".ToCharArray();

                f.Write(ident);
                f.Write(Version);
                f.Write(Keys.Count); // Keys = Chunks
                
                // For each chunk
                foreach (var pair in this)
                {
                    // Write out the chunk pos and how many blocks it has
                    f.Write(pair.Key.X);
                    f.Write(pair.Key.Z);
                    f.Write(pair.Value.Count);

                    // Write out each block's position and data
                    foreach (var block in pair.Value)
                    {
                        f.Write(block.Key.X);
                        f.Write(block.Key.Y);
                        f.Write(block.Key.Z);
                        f.Write(block.Value.Id);
                        f.Write((byte)block.Value.Flags);
                        if (block.Value.Flags.HasFlag(BlockDiff.BlockFlags.HasMetadata))
                            f.Write(block.Value.Metadata);
                        if (block.Value.Flags.HasFlag(BlockDiff.BlockFlags.HasTileNbt))
                            block.Value.TileData.WriteTo(f.BaseStream);
                    }
                }
            }
        }

        public static ChunkDiff Load(string filename)
        {
            return new ChunkDiff();
        }

        public void Add(ChunkPosition chunk, BlockPosition pos, BlockDiff block)
        {
            var entry = new KeyValuePair<BlockPosition, BlockDiff>(pos, block);
            if (!ContainsKey(chunk))
                Add(chunk, new List<KeyValuePair<BlockPosition, BlockDiff>>());
            this[chunk].Add(entry);
        }
    }

    internal class BlockDiff
    {
        public int Id { get; }
        public BlockFlags Flags { get; }
        public int Metadata { get; }
        public NbtTree TileData { get; }

        [Flags]
        internal enum BlockFlags
        {
            None = 0,
            HasMetadata = 0b1,
            HasTileNbt = 0b10
        }

        public BlockDiff(int id, int metdata = 0, NbtTree tileData = null)
        {
            Id = id;
            Metadata = metdata;
            TileData = tileData;
            Flags = (Metadata == 0 ? BlockFlags.None : BlockFlags.HasMetadata) | 
                    (TileData == null ? BlockFlags.None : BlockFlags.HasTileNbt);
        }
    }

    internal class BlockPosition
    {
        public int X { get; }
        public int Y { get; }
        public int Z { get; }

        public BlockPosition(int x, int y, int z)
        {
            X = x;
            Y = y;
            Z = z;
        }

        public override string ToString()
        {
            return $"({X},{Y},{Z})";
        }
    }

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
