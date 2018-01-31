using OpenTK.Graphics.OpenGL;

namespace PFX.Shader
{
    public class DefaultShaderProgram : ShaderProgram
    {
        private readonly string _program;

        public DefaultShaderProgram(string program)
        {
            _program = program;
        }

        protected override void Init()
        {
            LoadShader(_program, ShaderType.FragmentShader, PgmId, out FsId);

            GL.LinkProgram(PgmId);
            Log(GL.GetProgramInfoLog(PgmId));
        }
    }
}