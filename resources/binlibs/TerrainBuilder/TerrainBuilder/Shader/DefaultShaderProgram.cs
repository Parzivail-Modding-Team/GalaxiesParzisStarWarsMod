using System;
using System.IO;
using OpenTK.Graphics.OpenGL;

namespace TerrainBuilder.Shader
{
    class DefaultShaderProgram : ShaderProgram
    {
        private readonly string _program;

        public DefaultShaderProgram(string program)
        {
            _program = program;
        }

        protected override void Init()
        {
            LoadShader(File.ReadAllText(_program + ".fs"), ShaderType.FragmentShader, PgmId, out FsId);
            //LoadShader(File.ReadAllText(_program + ".vs"), ShaderType.VertexShader, PgmId, out VsId);

            GL.LinkProgram(PgmId);
            Log(GL.GetProgramInfoLog(PgmId));
        }
    }
}
