var modNamespace = "pswg";

var blockModelTemplate = "{{\n\t\"parent\": \"minecraft:block/cube_column{3}\",\n\t\"textures\": {{\n\t\t\"end\": \"{0}:block/{2}\",\n\t\t\"side\": \"{0}:block/{1}\"\n\t}}\n}}\n";
var itemModelTemplate = "{{\n\t\"parent\": \"{0}:block/{1}\"\n}}";
var blockstateTemplate = "{{\n\t\"variants\": {{\n\t\t\"axis=x\": {{\n\t\t\t\"model\": \"{0}:block/{2}\",\n\t\t\t\"x\": 90,\n\t\t\t\"y\": 90\n\t\t}},\n\t\t\"axis=y\": {{\n\t\t\t\"model\": \"{0}:block/{1}\"\n\t\t}},\n\t\t\"axis=z\": {{\n\t\t\t\"model\": \"{0}:block/{2}\",\n\t\t\t\"x\": 90\n\t\t}}\n\t}}\n}}";

var d = @"..\src\main\resources\assets\";
var blockModelDir = Path.Combine(d, modNamespace, "models", "block");
var itemModelDir = Path.Combine(d, modNamespace, "models", "item");
var blockstateDir = Path.Combine(d, modNamespace, "blockstates");

var modelIdH = "{0}_horizontal";
var modelIdV = "{0}";

var textureIdTop = "panel_imperial_base";
var textureIdSide = "{0}";

Directory.CreateDirectory(blockModelDir);
Directory.CreateDirectory(itemModelDir);
Directory.CreateDirectory(blockstateDir);

var names = new[] { "panel_imperial_1", "panel_imperial_light_1", "panel_imperial_light_6", "panel_imperial_light_tall_3", "panel_imperial_2", "panel_imperial_light_2", "panel_imperial_light_decoy", "panel_imperial_light_tall_4", "panel_imperial_3", "panel_imperial_light_3", "panel_imperial_light_off", "panel_imperial_base", "panel_imperial_light_4", "panel_imperial_light_tall_1", "panel_imperial_black_base", "panel_imperial_light_5", "panel_imperial_light_tall_2" };

foreach (var regId in names)
{
    File.WriteAllText(Path.Combine(blockModelDir, string.Format(modelIdV, regId) + ".json"), string.Format(blockModelTemplate, modNamespace, string.Format(textureIdSide, regId), string.Format(textureIdTop, regId), ""));
    File.WriteAllText(Path.Combine(blockModelDir, string.Format(modelIdH, regId) + ".json"), string.Format(blockModelTemplate, modNamespace, string.Format(textureIdSide, regId), string.Format(textureIdTop, regId), "_horizontal"));
    File.WriteAllText(Path.Combine(itemModelDir, regId + ".json"), string.Format(itemModelTemplate, modNamespace, regId));
    File.WriteAllText(Path.Combine(blockstateDir, regId + ".json"), string.Format(blockstateTemplate, modNamespace, string.Format(modelIdH, regId), string.Format(modelIdV, regId)));
}