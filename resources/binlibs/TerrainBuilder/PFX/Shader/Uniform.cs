namespace PFX.Shader
{
    public class Uniform
    {
        public Uniform(string name)
        {
            Name = name;
        }

        public string Name { get; set; }
        public object Value { get; set; }

        public virtual object GetValue()
        {
            return Value;
        }
    }
}