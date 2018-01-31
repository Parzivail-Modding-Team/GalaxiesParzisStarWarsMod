using System;

namespace PFX
{
    public class Lumberjack
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
                Console.WriteLine(Resources.Log_Format, DateTime.Now, header.Length > 0 ? " " + header : header, message);
            else
            {
                Console.ForegroundColor = color;
                Console.WriteLine(Resources.Log_Format, DateTime.Now, header.Length > 0 ? " " + header : header, message);
            }
        }
    }
}