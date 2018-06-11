using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using ShellProgressBar;
using Substrate;
using Substrate.Core;
using Substrate.Nbt;

namespace GenerateChunkDiff
{
    class Program
    {
        static void Main(string[] args)
        {
            var world = AnvilWorld.Open(@"E:\Forge\Mods\StarWarsGalaxy\eclipse\saves\New World-20180501-183728");
            var donorWorld = AnvilWorld.Open(@"E:\Forge\Mods\StarWarsGalaxy\eclipse\saves\ChunkDiffDonor");

            const int dim = 2;
            var manager = world.GetChunkManager(dim);
            var donorManager = donorWorld.GetChunkManager(dim);
            var numChunks = donorManager.Count();

            var checkBounds = false;
            const int chunkMinX = -100;
            const int chunkMinZ = 100;
            const int chunkMaxX = -100;
            const int chunkMaxZ = 100;

            var diff = new ChunkDiff();

            var options = new ProgressBarOptions
            {
                ForegroundColor = ConsoleColor.Green,
                BackgroundColor = ConsoleColor.DarkGray
            };

            var diffed = 0;
            var skipped = 0;

            using (var pbar = new ProgressBar(numChunks, "Chunks Processed", options))
            {
                foreach (var chunk in manager)
                {
                    if (!donorManager.ChunkExists(chunk.X, chunk.Z) ||
                        checkBounds && (chunk.X > chunkMaxX ||
                                        chunk.X < chunkMinX ||
                                        chunk.Z > chunkMaxZ ||
                                        chunk.Z < chunkMinZ))
                    {
                        skipped++;
                        continue;
                    }

                    diffed++;

                    pbar.Tick();

                    var pos = new ChunkPosition(chunk.X, chunk.Z);
                    var otherChunk = donorManager.GetChunk(chunk.X, chunk.Z);

                    for (var y = 0; y < 256; y++)
                    {
                        for (var x = 0; x < 16; x++)
                        {
                            for (var z = 0; z < 16; z++)
                            {
                                var blockId = chunk.Blocks.GetID(x, y, z);
                                var blockData = chunk.Blocks.GetData(x, y, z);
                                NbtTree nbt = null;
                                var te = chunk.Blocks.GetTileEntity(x, y, z);
                                if (te != null)
                                    nbt = new NbtTree(te.Source, "tile");

                                var blockIdOriginal = otherChunk.Blocks.GetID(x, y, z);
                                var blockDataOriginal = otherChunk.Blocks.GetData(x, y, z);
                                NbtTree nbtOriginal = null;
                                var teOriginal = chunk.Blocks.GetTileEntity(x, y, z);
                                if (teOriginal != null)
                                    nbtOriginal = new NbtTree(teOriginal.Source, "tile");

                                if (blockIdOriginal == blockId && blockDataOriginal == blockData && nbt == nbtOriginal)
                                    continue;

                                diff.Add(pos, new BlockPosition(x, y, z), new BlockDiff(blockId, blockData, nbt));
                            }
                        }
                    }

                    // Save after each chunk so we can release unneeded chunks back to the system
                    world.GetChunkManager(dim).Save();
                }

                pbar.Tick();
            }

            Console.WriteLine($"Diffed {diffed} chunks, skipped {skipped}");

            diff.Save("diff.cdf", NbtMap.Load(@"E:\Forge\Mods\StarWarsGalaxy\eclipse\config\Dev World-map.nbt"));
        }
    }

    internal class ChunkDiff : Dictionary<ChunkPosition, List<KeyValuePair<BlockPosition, BlockDiff>>>
    {
        public readonly int Version = 2;

        public void Save(string filename, NbtMap map)
        {
            var sw = new StreamWriter(filename);
            using (var f = new BinaryWriter(sw.BaseStream))
            {
                var ident = "CDF".ToCharArray();

                f.Write(ident);
                f.Write(Version);
                f.Write(Keys.Count); // Keys = Chunks
                f.Write(map.Keys.Count);

                foreach (var pair in map)
                {
                    f.Write(pair.Key);

                    var buffer = Encoding.UTF8.GetBytes(pair.Value);
                    f.Write(buffer);
                    f.Write((byte)0);
                }

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
                        var x = (byte)(block.Key.X - pair.Key.X * 16) & 0x0F;
                        var z = (byte)(block.Key.Z - pair.Key.Z * 16) & 0x0F;
                        f.Write((byte)((x << 4) | z));
                        f.Write((byte)block.Key.Y);
                        f.Write(block.Value.Id);
                        f.Write((byte)block.Value.Flags);
                        if (block.Value.Flags.HasFlag(BlockDiff.BlockFlags.HasMetadata))
                            f.Write(block.Value.Metadata);

                        if (!block.Value.Flags.HasFlag(BlockDiff.BlockFlags.HasTileNbt)) continue;

                        using (var memstream = new MemoryStream())
                        {
                            // Terrible hack to make the NBT in the format that MC likes
                            block.Value.TileData.WriteTo(memstream);
                            memstream.Seek(0, SeekOrigin.Begin);
                            var len = memstream.Length;
                            f.Write((int)len);
                            var b = new byte[(int)len];
                            memstream.Read(b, 0, (int)len);
                            f.Write(b);
                        }
                    }
                }
            }
        }

        public static ChunkDiff Load(string filename)
        {
            // TODO
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

    internal class NbtMap : Dictionary<int, string>
    {
        public static NbtMap Load(string filename)
        {
            var map = new NbtMap();

            var nf = new NBTFile(filename);

            using (var nbtstr = nf.GetDataInputStream())
            {
                var tree = new NbtTree(nbtstr);

                var root = tree.Root["map"];
                var list = root.ToTagList();

                foreach (var tag in list)
                {
                    var k = tag.ToTagCompound()["k"].ToTagString();
                    var v = tag.ToTagCompound()["v"].ToTagInt();
                    if (!map.ContainsKey(v))
                        map.Add(v, k);
                }

                return map;
            }
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
