var d = @"..\src\main\resources\assets\pswg\textures\items";

var modNamespace = "pswg";
var t = @"{{
  ""parent"": ""item/generated"",
  ""textures"": {{
    ""layer0"": ""{0}:items/{1}""
  }}
}}";

foreach (var f in Directory.GetFiles(d, "*.png"))
{
    var regId = Path.GetFileNameWithoutExtension(f);
    File.WriteAllText(Path.GetDirectoryName(f) + Path.DirectorySeparatorChar + regId + ".json", string.Format(t, modNamespace, regId));
}