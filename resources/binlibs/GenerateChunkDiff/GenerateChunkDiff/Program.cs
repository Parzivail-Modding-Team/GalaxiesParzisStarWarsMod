using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.IO;
using System.Linq;
using CommandLine;
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

            const int dim = 0;
            var manager = world.GetChunkManager(dim).ToList();
            var donorManager = donorWorld.GetChunkManager(dim);

            var inputMap = NbtMap.Load(Path.Combine(opts.OriginalPath, "cdfidmap.nbt"));
            var outputMap = NbtMap.Load(Path.Combine(opts.WorldPath, "cdfidmap.nbt"));

            var chunkBounds = new ChunkBounds(opts.ChunkBounds);

            if (!chunkBounds.Success)
            {
                Console.WriteLine("Input chunk bounds not in the correct format: \"xmin:xmax:zmin:zmax\"");
                return;
            }

            var diff = new ChunkDiff();

            var cgui = new ConsoleGui();
            var pbChunks = new ConsoleGuiProgressBar(0, 0, Console.WindowWidth, 0, 1)
            {
                ForegroundColor = ConsoleColor.Green,
                BackgroundColor = ConsoleColor.DarkGray
            };

            var lChunksTotal = new ConsoleGuiLabel(0, 1, "Total Chunks    : {0}");
            var lChunksRemaining = new ConsoleGuiLabel(0, 2, "Remaining Chunks: {0}");
            var lStatus = new ConsoleGuiLabel(0, 3, "Status          : {0}");

            var lChunksProcessed = new ConsoleGuiLabel(Console.WindowWidth / 2, 1, "Processed Chunks : {0}");
            var lChunksSkipped = new ConsoleGuiLabel(Console.WindowWidth / 2, 2, "Skipped Chunks   : {0}");
            var lChunksDiffed = new ConsoleGuiLabel(Console.WindowWidth / 2, 3, "Diffed Chunks    : {0}");
            var lBlocksDiffed = new ConsoleGuiLabel(Console.WindowWidth / 2, 4, "Diffed Blocks    : {0}");

            cgui.Add(pbChunks);

            cgui.Add(lChunksTotal);
            cgui.Add(lChunksRemaining);
            cgui.Add(lStatus);

            cgui.Add(lChunksProcessed);
            cgui.Add(lChunksSkipped);
            cgui.Add(lChunksDiffed);
            cgui.Add(lBlocksDiffed);

            var processedChunks = 0;
            var diffedChunks = 0;
            var diffedBlocks = 0;
            var skipped = 0;

            var lastTime = DateTime.Now;

            lStatus.Value = "Processing...";

            for (var i = 1; i <= manager.Count; i++)
            {
                var chunk = manager[i - 1];

                if (i > 1)
                    manager[i - 2] = null;

                pbChunks.Value = (float)i / manager.Count;

                if (!donorManager.ChunkExists(chunk.X, chunk.Z) || !chunkBounds.Contains(chunk))
                {
                    skipped++;
                    continue;
                }

                processedChunks++;

                var pos = new ChunkPosition(chunk.X, chunk.Z);
                var otherChunk = donorManager.GetChunk(chunk.X, chunk.Z);

                var numBlocksBefore = diffedBlocks;
                for (var y = 0; y < 256; y++)
                {
                    for (var x = 0; x < 16; x++)
                    {
                        for (var z = 0; z < 16; z++)
                        {
                            var blockId = (short)chunk.Blocks.GetID(x, y, z);
                            var blockData = chunk.Blocks.GetData(x, y, z);
                            NbtTree nbt = null;
                            var te = chunk.Blocks.GetTileEntity(x, y, z);
                            if (te != null)
                                nbt = new NbtTree(te.Source, "tile");

                            var blockIdOriginal = (short)otherChunk.Blocks.GetID(x, y, z);
                            var blockDataOriginal = otherChunk.Blocks.GetData(x, y, z);
                            NbtTree nbtOriginal = null;
                            var teOriginal = chunk.Blocks.GetTileEntity(x, y, z);
                            if (teOriginal != null)
                                nbtOriginal = new NbtTree(teOriginal.Source, "tile");

                            if (inputMap[blockIdOriginal] == outputMap[blockId] && blockDataOriginal == blockData && nbtOriginal == nbt)
                                continue;

                            diffedBlocks++;
                            diff.Add(pos, new BlockPosition(x, y, z), new BlockDiff(blockId, blockData, nbt));
                        }
                    }
                }

                if (diffedBlocks != numBlocksBefore)
                    diffedChunks++;

                lChunksTotal.Value = i.ToString("N0");
                lChunksRemaining.Value = (manager.Count - i).ToString("N0");

                lChunksProcessed.Value = processedChunks.ToString("N0");
                lBlocksDiffed.Value = diffedBlocks.ToString("N0");
                lChunksDiffed.Value = diffedChunks.ToString("N0");
                lChunksSkipped.Value = skipped.ToString("N0");

                cgui.Render();
            }

            lStatus.Value = "Saving...";
            cgui.Render();

            diff.Save(opts.DiffOutput, outputMap);

            lStatus.Value = "Done. Press Enter.";
            cgui.Render();

            Console.ReadKey();
        }
    }
}
