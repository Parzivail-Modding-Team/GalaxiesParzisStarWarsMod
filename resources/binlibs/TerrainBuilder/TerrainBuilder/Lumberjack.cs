using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace TerrainBuilder
{
    class Lumberjack
    {
        public static void Log(string message)
        {
            Log(message, ConsoleColor.Gray);
        }

        public static void Info(string message)
        {
            Log(message, ConsoleColor.Green);
        }

        public static void Warn(string message)
        {
            Log(message, ConsoleColor.Yellow);
        }

        public static void Error(string message)
        {
            Log(message, ConsoleColor.Red);
        }

        private static void Log(string message, ConsoleColor color)
        {
            if (Console.ForegroundColor == color)
                Console.WriteLine(message);
            else
            {
                Console.ForegroundColor = color;
                Console.WriteLine(message);
            }
        }
    }
}
