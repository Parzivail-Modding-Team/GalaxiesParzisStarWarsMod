namespace TerrainBuilder.Shader
{
    class Uniform
    {
        public string Name { get; set; }
        public object Value { get; set; }

        public Uniform(string name)
        {
            Name = name;
        }

        public virtual object GetValue() => Value;
    }
}
