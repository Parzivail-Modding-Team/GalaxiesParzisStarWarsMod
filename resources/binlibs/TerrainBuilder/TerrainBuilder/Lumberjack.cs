using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using PFX;

namespace TerrainBuilder
{
    class Lumberjack
    {
        public static void Log(string message)
        {
            Log(message, ConsoleColor.Gray, "LOG");
        }

        public static void Info(string message)
        {
            Log(message, ConsoleColor.Green, "INFO");
        }

        public static void Warn(string message)
        {
            Log(message, ConsoleColor.Yellow, "WARN");
        }

        public static void Error(string message)
        {
            Log(message, ConsoleColor.Red, "ERROR");
        }

        public static void Log(string message, ConsoleColor color, string header = "")
        {
            if (Console.ForegroundColor == color)
                Console.WriteLine(FontBank.Log_Format, DateTime.Now, header.Length > 0 ? " " + header : header, message);
            else
            {
                Console.ForegroundColor = color;
                Console.WriteLine(FontBank.Log_Format, DateTime.Now, header.Length > 0 ? " " + header : header, message);
            }
        }
    }
}
