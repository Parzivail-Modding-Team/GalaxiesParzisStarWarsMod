using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Newtonsoft.Json;

namespace BuildMod
{
    class McModInfo
    {
        [JsonProperty("modid")]
        public string Modid { get; set; }

        [JsonProperty("name")]
        public string Name { get; set; }

        [JsonProperty("description")]
        public string Description { get; set; }

        [JsonProperty("version")]
        public string Version { get; set; }

        [JsonProperty("mcversion")]
        public string Mcversion { get; set; }

        [JsonProperty("url")]
        public string Url { get; set; }

        [JsonProperty("updateUrl")]
        public string UpdateUrl { get; set; }

        [JsonProperty("authorList")]
        public List<string> AuthorList { get; set; }

        [JsonProperty("credits")]
        public string Credits { get; set; }

        [JsonProperty("logoFile")]
        public string LogoFile { get; set; }

        [JsonProperty("screenshots")]
        public List<object> Screenshots { get; set; }

        [JsonProperty("dependencies")]
        public List<object> Dependencies { get; set; }

        public static List<McModInfo> FromJson(string json)
        {
            return JsonConvert.DeserializeObject<List<McModInfo>>(json);
        }
    }
}
