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
            LoadShader(_program, ShaderType.FragmentShader, PgmId, out FsId);

            GL.LinkProgram(PgmId);
            Log(GL.GetProgramInfoLog(PgmId));
        }
    }
}
