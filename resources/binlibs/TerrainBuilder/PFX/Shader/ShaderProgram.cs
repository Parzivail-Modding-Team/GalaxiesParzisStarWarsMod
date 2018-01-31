using System;
using System.Collections.Generic;
using OpenTK;
using OpenTK.Graphics.OpenGL;

namespace PFX.Shader
{
    public abstract class ShaderProgram
    {
        protected Dictionary<string, int> CacheLoc;
        protected int FsId;
        protected int PgmId;
        protected int VsId;

        protected ShaderProgram()
        {
            CacheLoc = new Dictionary<string, int>();
        }

        public void InitProgram()
        {
            PgmId = GL.CreateProgram();
            Init();
        }

        public void Use(IEnumerable<Uniform> uniforms)
        {
            GL.UseProgram(PgmId);
            SetupUniforms(uniforms);
        }

        protected abstract void Init();

        protected virtual void SetupUniforms(IEnumerable<Uniform> uniforms)
        {
            foreach (var uniform in uniforms)
            {
                var loc = GetCachedUniformLoc(uniform.Name);
                var val = uniform.GetValue();
                var type = val.GetType();
                if (type == typeof(float))
                {
                    GL.Uniform1(loc, (float) val);
                }
                else if (type == typeof(double))
                {
                    GL.Uniform1(loc, (double) val);
                }
                else if (type == typeof(int))
                {
                    GL.Uniform1(loc, (int) val);
                }
                else if (type == typeof(uint))
                {
                    GL.Uniform1(loc, (uint) val);
                }
                else if (type == typeof(Vector2))
                {
                    var vec2 = (Vector2) val;
                    GL.Uniform2(loc, vec2.X, vec2.Y);
                }
                else if (type == typeof(Vector3))
                {
                    var vec3 = (Vector3) val;
                    GL.Uniform3(loc, vec3.X, vec3.Y, vec3.Z);
                }
                else
                {
                    throw new ArgumentException($"Unsupported uniform type: {type}");
                }
            }
        }

        private int GetCachedUniformLoc(string uniformName)
        {
            if (!CacheLoc.ContainsKey(uniformName))
                CacheLoc.Add(uniformName, GL.GetUniformLocation(PgmId, uniformName));
            return CacheLoc[uniformName];
        }

        protected void LoadShader(string source, ShaderType type, int program, out int address)
        {
            address = GL.CreateShader(type);
            GL.ShaderSource(address, source);
            GL.CompileShader(address);
            GL.AttachShader(program, address);
            Log(GL.GetShaderInfoLog(address));
        }

        protected void Log(string msg)
        {
            msg = msg.Trim();
            if (msg.Length > 0)
                Lumberjack.Log(msg, ConsoleColor.DarkYellow, "GLSL");
        }
    }
}