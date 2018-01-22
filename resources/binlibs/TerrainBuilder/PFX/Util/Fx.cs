using System;
using System.Collections.Generic;
using System.Drawing;
using System.Linq;
using OpenTK;
using OpenTK.Graphics.OpenGL;

namespace PFX.Util
{
    public class Fx
    {
        public static class Util
        {
            public static int GetRgb(int r, int g, int b)
            {
                int rgb = r;
                rgb = (rgb << 8) + g;
                rgb = (rgb << 8) + b;
                return rgb;
            }

            public static int GetRgba(int r, int g, int b, int a)
            {
                int rgba = a;
                rgba = (rgba << 8) + r;
                rgba = (rgba << 8) + g;
                rgba = (rgba << 8) + b;
                return rgba;
            }

            public static double Lerp(double a, double b, double f)
            {
                return (1 - f) * a + f * b;
            }

            public static Vector2 Lerp(Vector2 a, Vector2 b, float f)
            {
                double x = Lerp(a.X, b.X, f);
                double y = Lerp(a.Y, b.Y, f);
                return new Vector2((float) x, (float) y);
            }

            public static void GlNormal1V(Vector3 v)
            {
                GL.Normal3(v.X, v.Y, v.Z);
            }

            public static void GlVertex1V(Vector3 v)
            {
                GL.Vertex3(v.X, v.Y, v.Z);
            }

            public static float HzPercent(float hz)
            {
                return (DateTime.Now.Ticks % (long)(1000 / hz)) / (1000 / hz);
            }

            /// <summary>
            /// Creates color with corrected brightness.
            /// </summary>
            /// <param name="color">Color to correct.</param>
            /// <param name="correctionFactor">The brightness correction factor. Must be between -1 and 1. 
            /// Negative values produce darker colors.</param>
            /// <returns>
            /// Corrected <see cref="Color"/> structure.
            /// </returns>
            public static Color ChangeColorBrightness(Color color, float correctionFactor)
            {
                float red = (float)color.R;
                float green = (float)color.G;
                float blue = (float)color.B;

                if (correctionFactor < 0)
                {
                    correctionFactor = 1 + correctionFactor;
                    red *= correctionFactor;
                    green *= correctionFactor;
                    blue *= correctionFactor;
                }
                else
                {
                    red = (255 - red) * correctionFactor + red;
                    green = (255 - green) * correctionFactor + green;
                    blue = (255 - blue) * correctionFactor + blue;
                }

                return Color.FromArgb(color.A, (int)red, (int)green, (int)blue);
            }

            public static byte[] MakeStipple(string imagePath)
            {
                var img = (Bitmap)Image.FromFile(imagePath);
                if (img.Width != 32 || img.Height != 32)
                    throw new ArgumentException("Image must be exactly 32x32!");

                var bytes = new List<byte>();
                for (var y = 0; y < 32; y++)
                    for (var x = 0; x < 32; x += 8)
                        bytes.Add((byte)(((img.GetPixel(x, y).R > 0 ? 1 : 0) << 7) +
                            ((img.GetPixel(x + 1, y).R > 0 ? 1 : 0) << 6) +
                            ((img.GetPixel(x + 2, y).R > 0 ? 1 : 0) << 5) +
                            ((img.GetPixel(x + 3, y).R > 0 ? 1 : 0) << 4) +
                            ((img.GetPixel(x + 4, y).R > 0 ? 1 : 0) << 3) +
                            ((img.GetPixel(x + 5, y).R > 0 ? 1 : 0) << 2) +
                            ((img.GetPixel(x + 6, y).R > 0 ? 1 : 0) << 1) +
                            (img.GetPixel(x + 7, y).R > 0 ? 1 : 0)));
                return bytes.ToArray();
            }

            public static Vector2d Lerp(Vector2d a, Vector2d b, double f)
            {
                double x = Lerp(a.X, b.X, f);
                double y = Lerp(a.Y, b.Y, f);
                return new Vector2d(x, y);
            }
        }

        public static class D2
        {
            /*
                Public Methods
             */

            public static void DrawLine(float x1, float y1, float x2, float y2)
            {
                GL.Begin(PrimitiveType.LineStrip);
                GL.Vertex2(x1, y1);
                GL.Vertex2(x2, y2);
                GL.End();
            }

            public static void DrawLine(Vector2 a, Vector2 b)
            {
                DrawLine(a.X, a.Y, b.X, b.Y);
            }

            public static void DrawWireRectangle(float x, float y, float w, float h)
            {
                Rectangle(x, y, w, h, PrimitiveType.LineLoop);
            }

            public static void DrawSolidRectangle(float x, float y, float w, float h)
            {
                Rectangle(x, y, w, h, PrimitiveType.Quads);
            }

            public static void DrawWireCircle(float x, float y, float radius)
            {
                Circle(x, y, radius, PrimitiveType.LineLoop);
            }

            public static void DrawSolidCircle(float x, float y, float radius)
            {
                Circle(x, y, radius, PrimitiveType.TriangleFan);
            }

            public static void DrawWirePieSlice(float x, float y, float radius, float percent)
            {
                Pie(x, y, radius, percent, PrimitiveType.LineLoop);
            }

            public static void DrawSolidPieSlice(float x, float y, float radius, float percent)
            {
                Pie(x, y, radius, percent, PrimitiveType.TriangleFan);
            }

            public static void DrawWireTriangle(float x, float y, float sideLen)
            {
                Triangle(x, y, sideLen, PrimitiveType.LineLoop);
            }

            public static void DrawSolidTriangle(float x, float y, float sideLen)
            {
                Triangle(x, y, sideLen, PrimitiveType.TriangleFan);
            }

            /*
                Private Methods
             */

            static void Rectangle(float x, float y, float w, float h, PrimitiveType mode)
            {
                GL.Begin(mode);
                GL.Vertex3(x, y, 0);
                GL.Vertex3(x, y + h, 0);
                GL.Vertex3(x + w, y + h, 0);
                GL.Vertex3(x + w, y, 0);
                GL.End();
            }

            static void Circle(float x, float y, float radius, PrimitiveType mode)
            {
                GL.Begin(mode);
                for (int i = 0; i <= 360; i++)
                {
                    double nx = Math.Sin(i * 3.141526f / 180) * radius;
                    double ny = Math.Cos(i * 3.141526f / 180) * radius;
                    GL.Vertex2(nx + x, ny + y);
                }
                GL.End();
            }

            static void Triangle(float x, float y, float sideLen, PrimitiveType mode)
            {
                GL.Begin(mode);
                GL.Vertex2(x, y - sideLen / 2);
                GL.Vertex2(x - sideLen / 2, y + sideLen / 2);
                GL.Vertex2(x + sideLen / 2, y + sideLen / 2);
                GL.End();
            }

            static void Pie(float x, float y, float radius, float percent, PrimitiveType mode)
            {
                GL.Begin(mode);
                GL.Vertex2(x, y);
                for (int i = 0; i <= 360 * percent; i++)
                {
                    double nx = Math.Sin(i * 3.141526f / 180) * radius;
                    double ny = Math.Cos(i * 3.141526f / 180) * radius;
                    GL.Vertex2(nx + x, ny + y);
                }
                GL.End();
            }

            public static void CentripetalCatmullRomTo(Vector2 p0, Vector2 p1, Vector2 p2, Vector2 p3,
                float numSamplePoints)
            {
                CentripetalCatmullRomTo(p0.X, p0.Y, p1.X, p1.Y, p2.X, p2.Y, p3.X, p3.Y, numSamplePoints);
            }

            public static void CentripetalCatmullRomTo(float p0X, float p0Y, float p1X, float p1Y, float p2X, float p2Y,
                float p3X, float p3Y, float numSamplePoints)
            {
                GL.Begin(PrimitiveType.LineStrip);
                for (var i = 0; i <= numSamplePoints; i++)
                    GL.Vertex2(EvalCentripetalCatmullRom(p0X, p0Y, p1X, p1Y, p2X, p2Y, p3X, p3Y,
                        i / numSamplePoints));
                GL.End();
            }

            public static void CentripetalCatmullRomToVertexOnly(Vector2 p0, Vector2 p1, Vector2 p2, Vector2 p3,
                float numSamplePoints)
            {
                CentripetalCatmullRomToVertexOnly(p0.X, p0.Y, p1.X, p1.Y, p2.X, p2.Y, p3.X, p3.Y, numSamplePoints);
            }

            public static void CentripetalCatmullRomToVertexOnly(float p0X, float p0Y, float p1X, float p1Y, float p2X, float p2Y,
                float p3X, float p3Y, float numSamplePoints)
            {
                for (var i = 0; i <= numSamplePoints; i++)
                    GL.Vertex2(EvalCentripetalCatmullRom(p0X, p0Y, p1X, p1Y, p2X, p2Y, p3X, p3Y,
                        i / numSamplePoints));
            }

            public static Vector2 EvalCentripetalCatmullRom(Vector2 p0, Vector2 p1, Vector2 p2, Vector2 p3, float percentageAcross)
            {
                return EvalCentripetalCatmullRom(p0.X, p0.Y, p1.X, p1.Y, p2.X, p2.Y, p3.X, p3.Y, percentageAcross);
            }

            public static Vector2 EvalCentripetalCatmullRom(float p0X, float p0Y, float p1X, float p1Y, float p2X,
                float p2Y, float p3X, float p3Y, float percentageAcross)
            {
                //We use 0.25 as our power because of the alpha value of 0.5 of centripetal catmull rom splines
                //============================1st Stage==================================//
                double dt0 = Math.Pow((p1X - p0X) * (p1X - p0X) + (p1Y - p0Y) * (p1Y - p0Y), 0.25),
                    dt1 = Math.Pow((p2X - p1X) * (p2X - p1X) + (p2Y - p1Y) * (p2Y - p1Y), 0.25),
                    dt2 = Math.Pow((p3X - p2X) * (p3X - p2X) + (p3Y - p2Y) * (p3Y - p2Y), 0.25);
                //=============================2nd Stage==================================//
                double t1X = ((p1X - p0X) / dt0 - (p2X - p0X) / (dt0 + dt1) + (p2X - p1X) / dt1) * dt1,
                    t1Y = ((p1Y - p0Y) / dt0 - (p2Y - p0Y) / (dt0 + dt1) + (p2Y - p1Y) / dt1) * dt1,
                    t2X = ((p2X - p1X) / dt1 - (p3X - p1X) / (dt1 + dt2) + (p3X - p2X) / dt2) * dt1,
                    t2Y = ((p2Y - p1Y) / dt1 - (p3Y - p1Y) / (dt1 + dt2) + (p3Y - p2Y) / dt2) * dt1;
                //=============================3rd Stage=================================//
                double c0X = p1X,
                    c0Y = p1Y,
                    c1X = t1X,
                    c1Y = t1Y,
                    c2X = -3 * p1X + 3 * p2X - 2 * t1X - t2X,
                    c2Y = -3 * p1Y + 3 * p2Y - 2 * t1Y - t2Y,
                    c3X = 2 * p1X - 2 * p2X + t1X + t2X,
                    c3Y = 2 * p1Y - 2 * p2Y + t1Y + t2Y;
                //==============================4th Stage================================//
                //Now to evalute it with our parameterization t, we start at 1 as we are already at 0,and we need
                //to make sure we have i=numSample points so we cn get the last point
                double t = percentageAcross,
                    t2 = t * t,
                    t3 = t2 * t;
                return new Vector2((float)(c0X + c1X * t + c2X * t2 + c3X * t3),
                    (float)(c0Y + c1Y * t + c2Y * t2 + c3Y * t3));
            }
        }

        public static class D3
        {
            private static bool _isInit;
            private static double[,] _vertsBox = new double[8, 3];

            private static double[,] _normalsBox =
            {
                {-1.0, 0.0, 0.0},
                {0.0, 1.0, 0.0},
                {1.0, 0.0, 0.0},
                {0.0, -1.0, 0.0},
                {0.0, 0.0, 1.0},
                {0.0, 0.0, -1.0}
            };

            private static int[,] _facesBox =
            {
                {0, 1, 2, 3}, {3, 2, 6, 7}, {7, 6, 5, 4}, {4, 5, 1, 0}, {5, 6, 2, 1}, {7, 4, 0, 3}
            };

            private static double[,] _dodec = new double[20, 3];

            public static void Init()
            {
                // cube
                _vertsBox[0, 0] = _vertsBox[1, 0] = _vertsBox[2, 0] = _vertsBox[3, 0] = -0.5f;
                _vertsBox[4, 0] = _vertsBox[5, 0] = _vertsBox[6, 0] = _vertsBox[7, 0] = 0.5f;
                _vertsBox[0, 1] = _vertsBox[1, 1] = _vertsBox[4, 1] = _vertsBox[5, 1] = -0.5f;
                _vertsBox[2, 1] = _vertsBox[3, 1] = _vertsBox[6, 1] = _vertsBox[7, 1] = 0.5f;
                _vertsBox[0, 2] = _vertsBox[3, 2] = _vertsBox[4, 2] = _vertsBox[7, 2] = -0.5f;
                _vertsBox[1, 2] = _vertsBox[2, 2] = _vertsBox[5, 2] = _vertsBox[6, 2] = 0.5f;

                // dodec
                double alpha;
                double beta;
                //alpha = sqrt(2.0 / (3.0 + sqrt(5.0)));
                //beta = 1.0 + sqrt(6.0 / (3.0 + sqrt(5.0)) - 2.0 + 2.0 * sqrt(2.0 / (3.0 + sqrt(5.0))));
                alpha = 0.618033989;
                beta = 1.618033989;
                _dodec[0, 0] = -alpha;
                _dodec[0, 1] = 0;
                _dodec[0, 2] = beta;
                _dodec[1, 0] = alpha;
                _dodec[1, 1] = 0;
                _dodec[1, 2] = beta;
                _dodec[2, 0] = -1;
                _dodec[2, 1] = -1;
                _dodec[2, 2] = -1;
                _dodec[3, 0] = -1;
                _dodec[3, 1] = -1;
                _dodec[3, 2] = 1;
                _dodec[4, 0] = -1;
                _dodec[4, 1] = 1;
                _dodec[4, 2] = -1;
                _dodec[5, 0] = -1;
                _dodec[5, 1] = 1;
                _dodec[5, 2] = 1;
                _dodec[6, 0] = 1;
                _dodec[6, 1] = -1;
                _dodec[6, 2] = -1;
                _dodec[7, 0] = 1;
                _dodec[7, 1] = -1;
                _dodec[7, 2] = 1;
                _dodec[8, 0] = 1;
                _dodec[8, 1] = 1;
                _dodec[8, 2] = -1;
                _dodec[9, 0] = 1;
                _dodec[9, 1] = 1;
                _dodec[9, 2] = 1;
                _dodec[10, 0] = beta;
                _dodec[10, 1] = alpha;
                _dodec[10, 2] = 0;
                _dodec[11, 0] = beta;
                _dodec[11, 1] = -alpha;
                _dodec[11, 2] = 0;
                _dodec[12, 0] = -beta;
                _dodec[12, 1] = alpha;
                _dodec[12, 2] = 0;
                _dodec[13, 0] = -beta;
                _dodec[13, 1] = -alpha;
                _dodec[13, 2] = 0;
                _dodec[14, 0] = -alpha;
                _dodec[14, 1] = 0;
                _dodec[14, 2] = -beta;
                _dodec[15, 0] = alpha;
                _dodec[15, 1] = 0;
                _dodec[15, 2] = -beta;
                _dodec[16, 0] = 0;
                _dodec[16, 1] = beta;
                _dodec[16, 2] = alpha;
                _dodec[17, 0] = 0;
                _dodec[17, 1] = beta;
                _dodec[17, 2] = -alpha;
                _dodec[18, 0] = 0;
                _dodec[18, 1] = -beta;
                _dodec[18, 2] = alpha;
                _dodec[19, 0] = 0;
                _dodec[19, 1] = -beta;
                _dodec[19, 2] = -alpha;
                _isInit = true;
            }

            /*
                Public Methods
             */

            public static void DrawLine(double x1, double y1, double z1, double x2, double y2, double z2)
            {
                GL.Begin(PrimitiveType.LineStrip);
                GL.Vertex3(x1, y1, z1);
                GL.Vertex3(x2, y2, z2);
                GL.End();
            }

            public static void DrawAxes()
            {
                GL.Begin(PrimitiveType.LineStrip);
                GL.Color3(Color.Red);
                GL.Vertex3(Vector3.UnitX);
                GL.Vertex3(Vector3.Zero);
                GL.Color3(Color.Green);
                GL.Vertex3(Vector3.UnitY);
                GL.Vertex3(Vector3.Zero);
                GL.Color3(Color.Blue);
                GL.Vertex3(Vector3.UnitZ);
                GL.Vertex3(Vector3.Zero);
                GL.End();
            }

            public static void DrawWireBox()
            {
                Box(PrimitiveType.LineLoop);
            }

            public static void DrawSolidBox()
            {
                Box(PrimitiveType.Quads);
            }

            public static void DrawWireDodec()
            {
                Dodecahedron(PrimitiveType.LineLoop);
            }

            public static void DrawSolidDodec()
            {
                Dodecahedron(PrimitiveType.TriangleFan);
            }

            public static void DrawWireTorus(double innerRadius, double outerRadius, int nsides, int rings)
            {
                GL.PushAttrib(AttribMask.PolygonBit);
                GL.PolygonMode(MaterialFace.FrontAndBack, PolygonMode.Line);
                Doughnut(innerRadius, outerRadius, nsides, rings, PrimitiveType.LineStrip);
                GL.PopAttrib();
            }

            public static void DrawSolidTorus(double innerRadius, double outerRadius, int nsides, int rings)
            {
                Doughnut(innerRadius, outerRadius, nsides, rings, PrimitiveType.QuadStrip);
            }

            /*
                Math Helpers
             */

            private static void Diff3(double[] a, double[] b, ref double[] _out)
            {
                _out[0] = a[0] - b[0];
                _out[1] = a[1] - b[1];
                _out[2] = a[2] - b[2];
            }


            private static void Crossprod(double[] v1, double[] v2, ref double[] prod)
            {
                double[] p = new double[3]; /* in case prod == v1 or v2 */

                p[0] = v1[1] * v2[2] - v2[1] * v1[2];
                p[1] = v1[2] * v2[0] - v2[2] * v1[0];
                p[2] = v1[0] * v2[1] - v2[0] * v1[1];
                prod[0] = p[0];
                prod[1] = p[1];
                prod[2] = p[2];
            }

            private static void Normalize(double[] v)
            {
                double d = Math.Sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]);
                if (d == 0.0)
                {
                    v[0] = d = 1.0;
                }
                d = 1 / d;
                v[0] *= d;
                v[1] *= d;
                v[2] *= d;
            }

            /*
                Private Methods
             */

            private static void Box(PrimitiveType type)
            {
                for (int i = 5; i >= 0; i--)
                {
                    GL.Begin(type);
                    GL.Normal3(_normalsBox[i, 0], _normalsBox[i, 1], _normalsBox[i, 2]);
                    GL.Vertex3(_vertsBox[_facesBox[i, 0], 0], _vertsBox[_facesBox[i, 0], 1],
                        _vertsBox[_facesBox[i, 0], 2]);
                    GL.Vertex3(_vertsBox[_facesBox[i, 1], 0], _vertsBox[_facesBox[i, 1], 1],
                        _vertsBox[_facesBox[i, 1], 2]);
                    GL.Vertex3(_vertsBox[_facesBox[i, 2], 0], _vertsBox[_facesBox[i, 2], 1],
                        _vertsBox[_facesBox[i, 2], 2]);
                    GL.Vertex3(_vertsBox[_facesBox[i, 3], 0], _vertsBox[_facesBox[i, 3], 1],
                        _vertsBox[_facesBox[i, 3], 2]);
                    GL.End();
                }
            }

            private static void Doughnut(double r, double rOuter, int nsides, int rings, PrimitiveType type)
            {
                int i, j;
                double theta, phi, theta1;
                double cosTheta, sinTheta;
                double cosTheta1, sinTheta1;
                double ringDelta, sideDelta;

                ringDelta = 2.0 * Math.PI / rings;
                sideDelta = 2.0 * Math.PI / nsides;

                theta = 0.0;
                cosTheta = 1.0;
                sinTheta = 0.0;
                for (i = rings - 1; i >= 0; i--)
                {
                    theta1 = theta + ringDelta;
                    cosTheta1 = Math.Cos((float)theta1);
                    sinTheta1 = Math.Sin((float)theta1);
                    GL.Begin(type);
                    phi = 0.0;
                    for (j = nsides; j >= 0; j--)
                    {
                        double cosPhi, sinPhi, dist;

                        phi += sideDelta;
                        cosPhi = Math.Cos((float)phi);
                        sinPhi = Math.Sin((float)phi);
                        dist = rOuter + r * cosPhi;

                        GL.Normal3(cosTheta1 * cosPhi, -sinTheta1 * cosPhi, sinPhi);
                        GL.Vertex3(cosTheta1 * dist, -sinTheta1 * dist, r * sinPhi);
                        GL.Normal3(cosTheta * cosPhi, -sinTheta * cosPhi, sinPhi);
                        GL.Vertex3(cosTheta * dist, -sinTheta * dist, r * sinPhi);
                    }
                    GL.End();
                    theta = theta1;
                    cosTheta = cosTheta1;
                    sinTheta = sinTheta1;
                }
            }

            private static void DodecFace(int a, int b, int c, int d, int e, PrimitiveType shadeType)
            {
                double[] n0 = new double[3], d1 = new double[3], d2 = new double[3];

                Diff3(_dodec.Cast<double>().Skip(19 * a).Take(3).ToArray(), _dodec.Cast<double>().Skip(19 * b).Take(3).ToArray(), ref d1);
                Diff3(_dodec.Cast<double>().Skip(19 * b).Take(3).ToArray(), _dodec.Cast<double>().Skip(19 * c).Take(3).ToArray(), ref d2);
                Crossprod(d1, d2, ref n0);
                Normalize(n0);

                GL.Begin(shadeType);
                GL.Normal3(n0[0], n0[1], n0[2]);
                GL.Vertex3(_dodec[a, 0], _dodec[a, 1], _dodec[a, 2]);
                GL.Vertex3(_dodec[b, 0], _dodec[b, 1], _dodec[b, 2]);
                GL.Vertex3(_dodec[c, 0], _dodec[c, 1], _dodec[c, 2]);
                GL.Vertex3(_dodec[d, 0], _dodec[d, 1], _dodec[d, 2]);
                GL.Vertex3(_dodec[e, 0], _dodec[e, 1], _dodec[e, 2]);
                GL.End();
            }

            private static void Dodecahedron(PrimitiveType type)
            {
                DodecFace(0, 1, 9, 16, 5, type);
                DodecFace(1, 0, 3, 18, 7, type);
                DodecFace(1, 7, 11, 10, 9, type);
                DodecFace(11, 7, 18, 19, 6, type);
                DodecFace(8, 17, 16, 9, 10, type);
                DodecFace(2, 14, 15, 6, 19, type);
                DodecFace(2, 13, 12, 4, 14, type);
                DodecFace(2, 19, 18, 3, 13, type);
                DodecFace(3, 0, 5, 12, 13, type);
                DodecFace(6, 15, 8, 10, 11, type);
                DodecFace(4, 17, 8, 15, 14, type);
                DodecFace(4, 12, 5, 16, 17, type);
            }
        }
    }
}
