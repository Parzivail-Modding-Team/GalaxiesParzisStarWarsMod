using System;
using System.Collections.Generic;
using System.Configuration;
using System.Diagnostics;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace BuildMod
{
    class Program
    {
        static void Main(string[] args)
        {
            var cfg = Config.Load();
            Console.WriteLine($"INFO: Current version: {cfg.Major}.{cfg.Minor}.{cfg.Patch}");
            Console.WriteLine("USER: Increment? <ma(j)or, (m)inor, (p)atch, (n)o>");
            var validInput = new List<string>
            {
                "j", "m", "p", "n",
                "major", "minor", "patch", "no"
            };
            var userInputIncrement = "";
            do
            {
                Console.Write("USER: (p)> ");
                var readLine = Console.ReadLine();

                if (readLine == null) continue;

                userInputIncrement = readLine.ToLower();
                if (userInputIncrement == "")
                    userInputIncrement = "p";
            } while (!validInput.Contains(userInputIncrement));

            switch (userInputIncrement)
            {
                case "j":
                case "major":
                    cfg.Major++;
                    cfg.Minor = 0;
                    cfg.Patch = 0;
                    break;
                case "m":
                case "minor":
                    cfg.Minor++;
                    cfg.Patch = 0;
                    break;
                case "p":
                case "patch":
                    cfg.Patch++;
                    break;
                case "n":
                case "no":
                    break;
                default:
                    throw new ArgumentOutOfRangeException(nameof(userInputIncrement));
            }

            cfg.Save();
            var buildVersion = $"{cfg.Major}.{cfg.Minor}.{cfg.Patch}";
            Console.WriteLine($"INFO: New version: {buildVersion}");

            if (File.Exists("gradlew.bat"))
            {
                var process = new Process
                {
                    StartInfo = new ProcessStartInfo("gradlew.bat", "build")
                    {
                        UseShellExecute = false,
                        RedirectStandardError = true,
                        RedirectStandardInput = true,
                        RedirectStandardOutput = true
                    }
                };
                process.Start();
                process.WaitForExit();
                Console.WriteLine(process.StandardOutput.ReadToEnd());
                var destFileName = $"builds/pswg-{buildVersion}.jar";
                Console.WriteLine($"INFO: Moving jar: {destFileName}");
                if (File.Exists("build/libs/pswg-TEMP.jar"))
                {
                    if (!File.Exists(destFileName))
                        File.Move("build/libs/pswg-TEMP.jar", destFileName);
                    else
                        Console.WriteLine("WARN: Did not copy file, destination exists");
                    Console.WriteLine("INFO: Done.");
                }
                else
                    Console.WriteLine("CRIT: Unable to locate compiled jar!");
            }
            else
                Console.WriteLine("CRIT: Unable to locate gradlew.bat!");
            Console.ReadKey();
        }
    }
}
