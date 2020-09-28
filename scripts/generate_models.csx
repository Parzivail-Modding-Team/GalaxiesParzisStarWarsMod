var modNamespace = "pswg";

var blockModelTemplate = @"{{
  ""parent"": ""block/cube_all"",
  ""textures"": {{
    ""all"": ""{0}:block/{1}""
  }}
}}";

var itemModelTemplate = @"{{
  ""parent"": ""{0}:block/{1}""
}}";

var blockstateTemplate = @"{{
  ""variants"": {{
    """": {{
      ""model"": ""{0}:block/{1}""
    }}
  }}
}}";

var d = @"..\src\main\resources\assets\";
var blockModelDir = Path.Combine(d, modNamespace, "models", "block");
var itemModelDir = Path.Combine(d, modNamespace, "models", "item");
var blockstateDir = Path.Combine(d, modNamespace, "blockstates");

Directory.CreateDirectory(blockModelDir);
Directory.CreateDirectory(itemModelDir);
Directory.CreateDirectory(blockstateDir);

var names = new[] { "ore_diatium", "ore_exonium", "ore_helicite", "ore_ionite", "ore_kelerium", "ore_thorolide" };

foreach (var regId in names)
{
    File.WriteAllText(Path.Combine(blockModelDir, regId + ".json"), string.Format(blockModelTemplate, modNamespace, regId));
    File.WriteAllText(Path.Combine(itemModelDir, regId + ".json"), string.Format(itemModelTemplate, modNamespace, regId));
    File.WriteAllText(Path.Combine(blockstateDir, regId + ".json"), string.Format(blockstateTemplate, modNamespace, regId));
}