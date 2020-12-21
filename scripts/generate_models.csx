var modNamespace = "pswg";

var blockModelTemplate = "{{\n\t\"parent\": \"block/cube_all\",\n\t\"textures\": {{\n\t\t\"all\": \"{0}:block/{1}\"\n\t}}\n}}";
var itemModelTemplate = "{{\n\t\"parent\": \"{0}:block/{1}\"\n}}";
var blockstateTemplate = "{{\n\t\"variants\": {{\n\t\t\"\": {{\n\t\t\t\"model\": \"{0}:block/{1}\"\n\t\t}}\n\t}}\n}}";

var d = @"..\src\main\resources\assets\";
var blockModelDir = Path.Combine(d, modNamespace, "models", "block");
var itemModelDir = Path.Combine(d, modNamespace, "models", "item");
var blockstateDir = Path.Combine(d, modNamespace, "blockstates");

Directory.CreateDirectory(blockModelDir);
Directory.CreateDirectory(itemModelDir);
Directory.CreateDirectory(blockstateDir);

var names = new[] { "stone_temple", "stone_temple_bricks", "stone_temple_bricks_chiseled", "stone_temple_slab_side_smooth", "stone_temple_smooth" };

foreach (var regId in names)
{
    File.WriteAllText(Path.Combine(blockModelDir, regId + ".json"), string.Format(blockModelTemplate, modNamespace, regId));
    File.WriteAllText(Path.Combine(itemModelDir, regId + ".json"), string.Format(itemModelTemplate, modNamespace, regId));
    File.WriteAllText(Path.Combine(blockstateDir, regId + ".json"), string.Format(blockstateTemplate, modNamespace, regId));
}
