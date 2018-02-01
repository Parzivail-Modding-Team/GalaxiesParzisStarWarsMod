using System;
using System.Windows.Forms;

namespace TerrainBuilder
{
    class Program
    {
        [STAThread]
        static void Main(string[] args)
        {
            Console.Title = EmbeddedFiles.AppName;
            Application.EnableVisualStyles();
            Application.SetCompatibleTextRenderingDefault(false);
            new WindowVisualize().Run(40, 60);
        }
    }
}
