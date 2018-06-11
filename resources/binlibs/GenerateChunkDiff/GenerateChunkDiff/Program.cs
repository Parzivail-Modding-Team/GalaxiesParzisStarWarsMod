using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using CommandLine;
using ShellProgressBar;
using Substrate;
using Substrate.Nbt;

namespace GenerateChunkDiff
{
    internal class CliOptions
    {
        [Value(0, MetaName = "original", HelpText = "Unmodified world for comparison")]
        public string OriginalPath { get; set; }

        [Value(1, MetaName = "world", HelpText = "World to diff")]
        public string WorldPath { get; set; }

        [Value(2, MetaName = "output", HelpText = "Output diff file")]
        public string DiffOutput { get; set; }

        [Option('b', "bounds", HelpText = "Chunk boundaries (format: \"xmin:xmax:zmin:zmax\")")]
        public string ChunkBounds { get; set; }
    }

    internal class Program
    {
        private static void Main(string[] args)
        {
            Parser.Default.ParseArguments<CliOptions>(args)
                .WithParsed(RunOptionsAndReturnExitCode)
                .WithNotParsed(HandleParseError);
        }

        private static void HandleParseError(IEnumerable<Error> errs)
        {
            foreach (var error in errs)
                Console.WriteLine($"Error: {error.Tag}");
        }

        private static void RunOptionsAndReturnExitCode(CliOptions opts)
        {
            var world = AnvilWorld.Open(opts.WorldPath);
            var donorWorld = AnvilWorld.Open(opts.OriginalPath);

            const int dim = 2;
            var manager = world.GetChunkManager(dim);
            var donorManager = donorWorld.GetChunkManager(dim);
            var numChunks = donorManager.Count();

            var inputMap = NbtMap.Load(Path.Combine(opts.OriginalPath, "cdfidmap.nbt"));
            var outputMap = NbtMap.Load(Path.Combine(opts.WorldPath, "cdfidmap.nbt"));

            var chunkBounds = new ChunkBounds(opts.ChunkBounds);

            if (!chunkBounds.Success)
            {
                Console.WriteLine("Input chunk bounds not in the correct format: \"xmin:xmax:zmin:zmax\"");
                return;
            }

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
                    if (!donorManager.ChunkExists(chunk.X, chunk.Z) || !chunkBounds.Contains(chunk))
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

                                if (inputMap[blockIdOriginal] == outputMap[blockId] && blockDataOriginal == blockData && nbtOriginal == nbt)
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

            diff.Save(opts.DiffOutput, outputMap);
        }
    }
}
