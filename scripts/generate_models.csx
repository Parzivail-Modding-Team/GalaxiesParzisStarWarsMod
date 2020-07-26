var d = @"..\src\main\resources\assets\pswg\textures\item";

var modNamespace = "pswg";
var t = @"{{
  ""parent"": ""item/generated"",
  ""textures"": {{
    ""layer0"": ""{0}:item/{1}""
  }}
}}";

foreach (var f in Directory.GetFiles(d, "*.png"))
{
    var regId = Path.GetFileNameWithoutExtension(f);
    File.WriteAllText(Path.GetDirectoryName(Path.GetDirectoryName(Path.GetDirectoryName(f))) + Path.DirectorySeparatorChar + "models" + Path.DirectorySeparatorChar + "item" + Path.DirectorySeparatorChar + regId + ".json", string.Format(t, modNamespace, regId));
}