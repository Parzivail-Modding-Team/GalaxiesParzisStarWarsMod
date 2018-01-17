using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Newtonsoft.Json;

namespace BuildMod
{
    class Config
    {
        private const string Filename = "buildmodconfig.json";

        public int Major { get; set; } = 0;
        public int Minor { get; set; } = 0;
        public int Patch { get; set; } = 0;

        public void Save()
        {
            File.WriteAllText(Filename, JsonConvert.SerializeObject(this));
        }

        public static Config Load()
        {
            if (!File.Exists(Filename))
                new Config().Save();
            return JsonConvert.DeserializeObject<Config>(File.ReadAllText(Filename));
        }
    }
}
