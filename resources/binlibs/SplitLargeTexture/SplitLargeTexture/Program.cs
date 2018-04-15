using System;
using System.Collections.Generic;
using System.Drawing;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using CommandLine;

namespace SplitLargeTexture
{
    public class Options
    {
        [Value(0, MetaName = "input file",
            HelpText = "Input file to be processed.",
            Required = true)]
        public string FileName { get; set; }
    }

    class Program
    {
        [STAThread]
        static int Main(string[] args)
        {
            return Parser.Default.ParseArguments<Options>(args)
                .MapResult(
                    Run,
                    _ => 1);
        }

        private static int Run(Options opts)
        {
            if (!File.Exists(opts.FileName))
                return 1;

            using (var b = new Bitmap(opts.FileName))
            {
                var width = b.Width / 32;
                var height = b.Height / 32;

                for (var x = 0; x < width; x++)
                {
                    for (var y = 0; y < height; y++)
                    {
                        var bb = b.Clone(new Rectangle(x * 32, y * 32, 32, 32), b.PixelFormat);
                        bb.Save($"{Path.GetFileNameWithoutExtension(opts.FileName)}_{x}_{y}.png");
                    }
                }
            }

            return 0;
        }
    }
}
