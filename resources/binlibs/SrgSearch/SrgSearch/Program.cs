using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using CsvHelper;

namespace SrgSearch
{
    class Program
    {
        static void Main(string[] args)
        {
            var srgFields = new List<SrgField>();
            var srgMethods = new List<SrgMethod>();
            var srgParams = new List<SrgParam>();

            using (var csvFields = new CsvReader(new StreamReader("Mappings/stable_12/fields.csv")))
                srgFields = csvFields.GetRecords<SrgField>().ToList();

            using (var csvMethods = new CsvReader(new StreamReader("Mappings/stable_12/methods.csv")))
                srgMethods = csvMethods.GetRecords<SrgMethod>().ToList();

            using (var csvParams = new CsvReader(new StreamReader("Mappings/stable_12/params.csv")))
                srgParams = csvParams.GetRecords<SrgParam>().ToList();

            Console.WriteLine("Loaded.");

            var table = new ConsoleTable("MCP", "Mapped Name", "side", "Comment");

            while (true)
            {
                var query = ReadLine.Read("> ");
                query = query.Trim();

                if (query.ToLowerInvariant() == "!exit")
                    break;

                table.Rows.Clear();

                var fieldResults = srgFields.Where(field => field.searge == query || field.name == query);
                var methodResults = srgMethods.Where(field => field.searge == query || field.name == query);
                var paramResults = srgParams.Where(field => field.param == query || field.name == query);

                foreach (var result in fieldResults)
                    table.AddRow(result.searge, result.name, TranslateSide(result.side), result.desc);
                foreach (var result in methodResults)
                    table.AddRow(result.searge, result.name, TranslateSide(result.side), result.desc);
                foreach (var result in paramResults)
                    table.AddRow(result.param, result.name, TranslateSide(result.side), "");

                Console.WriteLine(table.ToMinimalString());
            }
        }

        private static string TranslateSide(string side)
        {
            switch (side)
            {
                case "0":
                    return "Client";
                case "1":
                    return "Server";
                case "2":
                    return "Both";
                default:
                    return "Unknown";
            }
        }
    }

    internal class SrgParam
    {
        public string param { get; set; }
        public string name { get; set; }
        public string side { get; set; }
    }

    internal class SrgMethod
    {
        public string searge { get; set; }
        public string name { get; set; }
        public string side { get; set; }
        public string desc { get; set; }
    }

    internal class SrgField
    {
        public string searge { get; set; }
        public string name { get; set; }
        public string side { get; set; }
        public string desc { get; set; }
    }
}
