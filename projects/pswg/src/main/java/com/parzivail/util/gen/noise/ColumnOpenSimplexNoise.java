package com.parzivail.util.gen.noise;

/**
 * OpenSimplex2S noise, speed-optimized for column generation.
 * Lock in an X/Z position, and call getForY(y) for each successive Y position.
 * It remembers enough of the current state of the noise to achieve a ~1.8x speedup.
 *
 * Works best for small steps in Y. Larger steps may see diminished performance gains.
 * Therefore it is more ideal a step towards avoiding interpolation, than to use with it.
 * Works great with conditional noise layer skipping!
 *
 * Uses "Improve XZ Planes" domain rotation to reduce subtle diagonal patterns.
 *
 * Not recommended for 2D noise where X and Y are both horizontal, as there is some bias.
 * I may later release noise better suited to this purpose.
 *
 * @author K.jpg
 */
 
/*
 * Optimized version by Jasmine K. (SuperCoder79)
 *
 * Utilizes fused-multiply-add, more optimized math, and method inlining to achieve improved performance on 3d noise.
 * Other dimensions are untouched.
 *
 * Released under CC0 License, June 2022
 */

public final class ColumnOpenSimplexNoise {
    private static final double ROOT3 = 1.7320508075688772;
    private static final double ROOT3OVER3 = (float) 0.577350269189626;
    private static final double ROOT3OVER3_1_5 = (float) (1.5 * ROOT3OVER3);
    private static final double _2ROOT3 = (float) (2 * ROOT3);

    private static final int PERMUTATION_TABLE_SIZE_EXPONENT = 8;

    private static final int PSIZE = 1 << PERMUTATION_TABLE_SIZE_EXPONENT;
    private static final int PMASK = PSIZE - 1;
    private static final double Y_ADDITION = (PSIZE + 1) * (ROOT3/2);

    private final short[] perm;
    private final ThreadLocal<ColumnGenerator> columnGenerator = ThreadLocal.withInitial(ColumnGenerator::new);

    public ColumnOpenSimplexNoise(long seed) {
        perm = new short[PSIZE];
        short[] source = new short[PSIZE];
        for (short i = 0; i < PSIZE; i++)
            source[i] = i;
        for (int i = PSIZE - 1; i >= 0; i--) {
            seed = seed * 6364136223846793005L + 1442695040888963407L;
            int r = (int)((seed + 31) % (i + 1));
            if (r < 0)
                r += (i + 1);
            perm[i] = source[r];
            source[r] = source[i];
        }
    }

    public void setXZ(double x, double z) {
        this.columnGenerator.get().setXZ(x, z);
    }

    public double getForY(double y) {
        return this.columnGenerator.get().getForY(y);
    }

    public final class ColumnGenerator {

        HalfColumnGenerator cA, cB;

        public ColumnGenerator() {
            cA = new HalfColumnGenerator();
            cB = new HalfColumnGenerator();
        }

        public void setXZ(double x, double z) {
            cA.setXZ(x, z);
            cB.setXZ(x, z);
        }

        public double getForY(double y) {
            double yB = y + Y_ADDITION;
            return cA.getForY(y) + cB.getForY(yB);
        }

    }

    private final class HalfColumnGenerator {

        private double xs, zs, xzr;
        private double localMinY, localMaxY, y0;
        private int xrb, yrb, zrb;
        private double xi0, zi0;
        private double gbRet, gvRet;
        private double g000b, g100b, g010b, g001b;
        private double g000v, g100v, g010v, g001v;
        private double xzFalloff000, xzFalloff100, xzFalloff010, xzFalloff001;
        private double yb000, yb100, yb010, yb001;

        private double yBound000, yBound100, yBound010, yBound001;
        private double yMin000, yMin100, yMin010, yMin001;
        private double yMax000, yMax100, yMax010, yMax001;

        protected HalfColumnGenerator() {
            localMinY = Double.POSITIVE_INFINITY;
        }

        public void setXZ(double x, double z) {
            // Domain rotation, start
            double xz = x + z;

            this.xs = fma(xz, -0.211324865405187, x);
            this.zs = fma(xz, -0.211324865405187, z);
            this.xzr = xz * -ROOT3OVER3;

            localMinY = Double.POSITIVE_INFINITY;
        }

        public double getForY(double y) {

            setupAndDomainRotate(y);

            double t = y - y0;

            setup000Corner(t);

            corner100(t);

            corner010(t);

            corner001(t);

            double value = valueContribution(t);

            return value;
        }

        private void corner100(double t) {
            if (t < yMin100 || t > yMax100) {
                if (t > yBound100) { // 011
                    yb100 = 2 * ROOT3OVER3;
                    yMin100 = yBound100;
                    yMax100 = Double.POSITIVE_INFINITY;
                    double xi = xi0 + 0.788675134594813;
                    double zi = zi0 - 0.211324865405187;

                    int gi = gradIndex(xrb, yrb + 1, zrb + 1);
                    sample100(xi, zi, gi);
                } else { // 100
                    yb100 = ROOT3OVER3;
                    yMin100 = Double.NEGATIVE_INFINITY;
                    yMax100 = yBound100;
                    double xi = xi0 - 0.788675134594813;
                    double zi = zi0 + 0.211324865405187;

                    int gi = gradIndex(xrb + 1, yrb, zrb);
                    sample100(xi, zi, gi);
                }
            }
        }

        private void corner010(double t) {
            if (t < yMin010 || t > yMax010) {
                if (t > yBound010) { // 101
                    yb010 = 2 * ROOT3OVER3;
                    yMin010 = yBound010;
                    yMax010 = Double.POSITIVE_INFINITY;
                    double xi = xi0 - 0.57735026918962;
                    double zi = zi0 - 0.57735026918962;

                    int gi = gradIndex(xrb + 1, yrb, zrb + 1);
                    sample010(xi, zi, gi);
                } else { // 010
                    yb010 = ROOT3OVER3;
                    yMin010 = Double.NEGATIVE_INFINITY;
                    yMax010 = yBound010;
                    double xi = xi0 + 0.577350269189626;
                    double zi = zi0 + 0.577350269189626;

                    int gi = gradIndex(xrb, yrb + 1, zrb);
                    sample010(xi, zi, gi);
                }
            }
        }

        private void corner001(double t) {
            if (t < yMin001 || t > yMax001) {
                if (t > yBound001) { // 110
                    yb001 = 2 * ROOT3OVER3;
                    yMin001 = yBound001;
                    yMax001 = Double.POSITIVE_INFINITY;
                    double xi = xi0 - 0.211324865405187;
                    double zi = zi0 + 0.788675134594813;

                    int gi = gradIndex(xrb + 1, yrb + 1, zrb);
                    sample001(xi, zi, gi);
                } else { // 001
                    yb001 = ROOT3OVER3;
                    yMin001 = Double.NEGATIVE_INFINITY;
                    yMax001 = yBound001;
                    double xi = xi0 + 0.211324865405187;
                    double zi = zi0 - 0.788675134594813;

                    int gi = gradIndex(xrb, yrb, zrb + 1);
                    sample001(xi, zi, gi);
                }
            }
        }

        private double valueContribution(double t) {
            double value = 0;

            double dy000 = t - yb000;
            double dy100 = t - yb100;
            double dy010 = t - yb010;
            double dy001 = t - yb001;

            double dy000Sq = dy000 * dy000;
            double dy100Sq = dy100 * dy100;
            double dy010Sq = dy010 * dy010;
            double dy001Sq = dy001 * dy001;

            if (xzFalloff000 > dy000Sq) {
                value = falloffContribution(dy000, dy000Sq, xzFalloff000, g000v, g000b);
            }

            if (xzFalloff100 > dy100Sq) {
                value += falloffContribution(dy100, dy100Sq, xzFalloff100, g100v, g100b);
            }

            if (xzFalloff010 > dy010Sq) {
                value += falloffContribution(dy010, dy010Sq, xzFalloff010, g010v, g010b);
            }

            if (xzFalloff001 > dy001Sq) {
                value += falloffContribution(dy001, dy001Sq, xzFalloff001, g001v, g001b);
            }

            return value;
        }

        private void setup000Corner(double t) {
            if (t < yMin000 || t > yMax000) {
                if (t > yBound000) { // 111
                    yb000 = ROOT3;
                    yMin000 = yBound000;
                    yMax000 = Double.POSITIVE_INFINITY;

                    int gi = gradIndex(xrb + 1, yrb + 1, zrb + 1);

                    sampleCorner(gi, xi0, zi0);
                } else { // 000
                    yb000 = 0;
                    yMin000 = Double.NEGATIVE_INFINITY;
                    yMax000 = yBound000;
                    int gi = gradIndex(xrb, yrb, zrb);

                    sampleCorner(gi, xi0, zi0);
                }

                g000b = gbRet;
                g000v = gvRet;
            }
        }

        private void setupAndDomainRotate(double y) {
            if (y < localMinY || y > localMaxY) {

                // Domain rotation, finish
                double xr = fma(y, ROOT3OVER3, xs);
                double zr = fma(y, ROOT3OVER3, zs);
                double yr = fma(y, ROOT3OVER3, xzr);

                // Cube base and bounds
                xrb = fastFloor(xr);
                yrb = fastFloor(yr);
                zrb = fastFloor(zr);

                double xri = xr - xrb;
                double yri = yr - yrb;
                double zri = zr - zrb;

                // Set bounds of Y that will stay inside the cube.
                localMinY = fma((xri < zri ? (Math.min(xri, yri)) : (Math.min(zri, yri))), -ROOT3, y);
                localMaxY = fma((1 - (xri >= zri ? (Math.max(xri, yri)) : (Math.max(zri, yri)))), ROOT3, y);
                y0 = (yrb + xrb + zrb) * ROOT3OVER3;

                // Remove the (world-space) Y coordinate so we can consider everything relative to the base cube vertex.
                double tri = xri + yri;
                tri = tri + zri;

                double xri0 = fma(tri, -0.3333333333333333, xri);
                double yri0 = fma(tri, -0.3333333333333333, yri);
                double zri0 = fma(tri, -0.3333333333333333, zri);

                // Planar dividers between contributing vertices, intersections with the current column.

                double c2 = setupMulAdd(xri0, yri0, zri0);

                xzFalloff000 = c2;

                // Trigger the compares to happen regardless
                yMin000 = yMin100 = yMin010 = yMin001 = Double.POSITIVE_INFINITY;
            }
        }

        private double setupMulAdd(double xri0, double yri0, double zri0) {
            double x0r1 = fma(xri0, _2ROOT3, ROOT3OVER3_1_5);
            double y0r1 = fma(yri0, _2ROOT3, ROOT3OVER3_1_5);
            double z0r1 = fma(zri0, _2ROOT3, ROOT3OVER3_1_5);

            double a1 = xri0 + zri0;
            double b2 = yri0 * ROOT3OVER3;

            double s2i = fma(a1, -0.211324865405187, -b2);
            xi0 = xri0 + s2i;
            zi0 = zri0 + s2i;

            double c1 = fma(-xi0, xi0, 0.75);
            double c2 = fma(-zi0, zi0, c1);

            yBound000 = ROOT3OVER3_1_5;
            yBound100 = x0r1;
            yBound010 = y0r1;
            yBound001 = z0r1;

            return c2;
        }

        private void sample100(double xi, double zi, int gi) {
            sampleCorner(gi, xi, zi);

            g100b = gbRet;
            g100v = gvRet;

            double c1 = fma(-xi, xi, 0.75);
            double c2 = fma(-zi, zi, c1);

            xzFalloff100 = c2;
        }

        private void sample010(double xi, double zi, int gi) {
            sampleCorner(gi, xi, zi);

            g010b = gbRet;
            g010v = gvRet;

            double c1 = fma(-xi, xi, 0.75);
            double c2 = fma(-zi, zi, c1);

            xzFalloff010 = c2;
        }

        private void sample001(double xi, double zi, int gi) {
            sampleCorner(gi, xi, zi);

            g001b = gbRet;
            g001v = gvRet;

            double c1 = fma(-xi, xi, 0.75);
            double c2 = fma(-zi, zi, c1);

            xzFalloff001 = c2;
        }

        private void sampleCorner(int gi, double xi, double zi) {
            int i0 = gi + 0;
            int i1 = gi + 1;
            int i2 = gi + 2;

            double gr0 = RGRADIENTS_3D[i0];
            double gr1 = RGRADIENTS_3D[i1];
            double gr2 = RGRADIENTS_3D[i2];

            double b1 = gr0 * xi;

            gvRet = gr2;
            gbRet = fma(gr1, zi, b1);
        }

        private double falloffContribution(double dy000, double dy000Sq, double xzFalloff, double gv, double gb) {
            double falloff = xzFalloff - dy000Sq;

            falloff = falloff * falloff;
            double f2 = falloff * falloff;

            return f2 * fma(gv, dy000, gb);
        }

        private int gradIndex(int xrv, int yrv, int zrv) {
            int xm = xrv & PMASK;
            int ym = yrv & PMASK;
            int zm = zrv & PMASK;

            return perm[perm[perm[xm] ^ ym] ^ zm] & 0xFC;
        }
    }

    private static int fastFloor(double x) {
        int xi = (int)x;
        return x < xi ? xi - 1 : xi;
    }

    // Use FMA if you target CPUs that support it, otherwise use the direct math version! If your CPU doesn't have FMA, Math.fma() is *wildly* slower!
    private static double fma(double a, double b, double c) {
        return Math.fma(a, b, c);
		// return (a * b) + c;
    }

    private static final double[] RGRADIENTS_3D = {
            0, 1, 1, 0,  0,-1, 1, 0,  0, 1,-1, 0,  0,-1,-1, 0,
            1, 0, 1, 0, -1, 0, 1, 0,  1, 0,-1, 0, -1, 0,-1, 0,
            1, 1, 0, 0, -1, 1, 0, 0,  1,-1, 0, 0, -1,-1, 0, 0,
            0, 1, 1, 0,  0,-1, 1, 0,  0, 1,-1, 0,  0,-1,-1, 0,
            1, 0, 1, 0, -1, 0, 1, 0,  1, 0,-1, 0, -1, 0,-1, 0,
            1, 1, 0, 0, -1, 1, 0, 0,  1,-1, 0, 0, -1,-1, 0, 0,
            0, 1, 1, 0,  0,-1, 1, 0,  0, 1,-1, 0,  0,-1,-1, 0,
            1, 0, 1, 0, -1, 0, 1, 0,  1, 0,-1, 0, -1, 0,-1, 0,
            1, 1, 0, 0, -1, 1, 0, 0,  1,-1, 0, 0, -1,-1, 0, 0,
            0, 1, 1, 0,  0,-1, 1, 0,  0, 1,-1, 0,  0,-1,-1, 0,
            1, 0, 1, 0, -1, 0, 1, 0,  1, 0,-1, 0, -1, 0,-1, 0,
            1, 1, 0, 0, -1, 1, 0, 0,  1,-1, 0, 0, -1,-1, 0, 0,
            0, 1, 1, 0,  0,-1, 1, 0,  0, 1,-1, 0,  0,-1,-1, 0,
            1, 0, 1, 0, -1, 0, 1, 0,  1, 0,-1, 0, -1, 0,-1, 0,
            1, 1, 0, 0, -1, 1, 0, 0,  1,-1, 0, 0, -1,-1, 0, 0,
            1, 1, 0, 0,  0,-1, 1, 0, -1, 1, 0, 0,  0,-1,-1, 0
    };

    private static final double NORMALIZING_MULTIPLIER_3D = 9.046026385208288;
    static {
        // 256 length
        for (int i = 0; i < RGRADIENTS_3D.length; i += 4) {
            double gx = RGRADIENTS_3D[i | 0];
            double gy = RGRADIENTS_3D[i | 1];
            double gz = RGRADIENTS_3D[i | 2];

            // Rotate and apply noise normalizing constant
            // Store as XZY so X and Z are next to each other when we read them at the same time.
            double s2 = (gx + gz) * -0.211324865405187 + gy * 0.577350269189626;
            RGRADIENTS_3D[i | 0] = (gx + s2) * NORMALIZING_MULTIPLIER_3D;
            RGRADIENTS_3D[i | 1] = (gz + s2) * NORMALIZING_MULTIPLIER_3D;
            RGRADIENTS_3D[i | 2] = (gy - gx - gz) * (ROOT3OVER3 * NORMALIZING_MULTIPLIER_3D);
        }
    }

}