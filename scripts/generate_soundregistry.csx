var sounds = File.ReadAllText(@"..\src\main\resources\assets\pswg\sounds.json");

var rgx = new Regex("\"(.+)\": \\{");
var matches = rgx.Matches(sounds);

foreach (Match match in matches)
{
    Console.WriteLine($"r.register(getSound(\"{match.Groups[1].Value}\"));");
}