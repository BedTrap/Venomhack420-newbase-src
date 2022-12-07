package de.tyrannus.venomhack.utils.render;

import java.nio.FloatBuffer;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;

public final class VenomMatrix4f {
   private static final int ORDER = 4;
   double a00;
   double a01;
   double a02;
   double a03;
   double a10;
   double a11;
   double a12;
   double a13;
   double a20;
   double a21;
   double a22;
   double a23;
   double a30;
   double a31;
   double a32;
   double a33;

   public VenomMatrix4f() {
   }

   public VenomMatrix4f(VenomMatrix4f matrix) {
      this.a00 = matrix.a00;
      this.a01 = matrix.a01;
      this.a02 = matrix.a02;
      this.a03 = matrix.a03;
      this.a10 = matrix.a10;
      this.a11 = matrix.a11;
      this.a12 = matrix.a12;
      this.a13 = matrix.a13;
      this.a20 = matrix.a20;
      this.a21 = matrix.a21;
      this.a22 = matrix.a22;
      this.a23 = matrix.a23;
      this.a30 = matrix.a30;
      this.a31 = matrix.a31;
      this.a32 = matrix.a32;
      this.a33 = matrix.a33;
   }

   public VenomMatrix4f(Quaternion quaternion) {
      double f = (double)quaternion.getX();
      double g = (double)quaternion.getY();
      double h = (double)quaternion.getZ();
      double i = (double)quaternion.getW();
      double j = 2.0 * f * f;
      double k = 2.0 * g * g;
      double l = 2.0 * h * h;
      this.a00 = 1.0 - k - l;
      this.a11 = 1.0 - l - j;
      this.a22 = 1.0 - j - k;
      this.a33 = 1.0;
      double m = f * g;
      double n = g * h;
      double o = h * f;
      double p = f * i;
      double q = g * i;
      double r = h * i;
      this.a10 = 2.0 * (m + r);
      this.a01 = 2.0 * (m - r);
      this.a20 = 2.0 * (o - q);
      this.a02 = 2.0 * (o + q);
      this.a21 = 2.0 * (n + p);
      this.a12 = 2.0 * (n - p);
   }

   public boolean method_35433() {
      VenomMatrix4f matrix4f = new VenomMatrix4f();
      matrix4f.a30 = 1.0;
      matrix4f.a31 = 1.0;
      matrix4f.a32 = 1.0;
      matrix4f.a33 = 0.0;
      VenomMatrix4f matrix4f2 = this.copy();
      matrix4f2.multiply(matrix4f);
      return isInteger(matrix4f2.a00 / matrix4f2.a03)
         && isInteger(matrix4f2.a10 / matrix4f2.a13)
         && isInteger(matrix4f2.a20 / matrix4f2.a23)
         && isInteger(matrix4f2.a01 / matrix4f2.a03)
         && isInteger(matrix4f2.a11 / matrix4f2.a13)
         && isInteger(matrix4f2.a21 / matrix4f2.a23)
         && isInteger(matrix4f2.a02 / matrix4f2.a03)
         && isInteger(matrix4f2.a12 / matrix4f2.a13)
         && isInteger(matrix4f2.a22 / matrix4f2.a23);
   }

   private static boolean isInteger(double value) {
      return Math.abs(value - (double)Math.round(value)) <= 1.0E-5;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         VenomMatrix4f matrix4f = (VenomMatrix4f)o;
         return Float.compare((float)matrix4f.a00, (float)this.a00) == 0
            && Float.compare((float)matrix4f.a01, (float)this.a01) == 0
            && Float.compare((float)matrix4f.a02, (float)this.a02) == 0
            && Float.compare((float)matrix4f.a03, (float)this.a03) == 0
            && Float.compare((float)matrix4f.a10, (float)this.a10) == 0
            && Float.compare((float)matrix4f.a11, (float)this.a11) == 0
            && Float.compare((float)matrix4f.a12, (float)this.a12) == 0
            && Float.compare((float)matrix4f.a13, (float)this.a13) == 0
            && Float.compare((float)matrix4f.a20, (float)this.a20) == 0
            && Float.compare((float)matrix4f.a21, (float)this.a21) == 0
            && Float.compare((float)matrix4f.a22, (float)this.a22) == 0
            && Float.compare((float)matrix4f.a23, (float)this.a23) == 0
            && Float.compare((float)matrix4f.a30, (float)this.a30) == 0
            && Float.compare((float)matrix4f.a31, (float)this.a31) == 0
            && Float.compare((float)matrix4f.a32, (float)this.a32) == 0
            && Float.compare((float)matrix4f.a33, (float)this.a33) == 0;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int i = this.a00 != 0.0 ? Float.floatToIntBits((float)this.a00) : 0;
      i = 31 * i + (this.a01 != 0.0 ? Float.floatToIntBits((float)this.a01) : 0);
      i = 31 * i + (this.a02 != 0.0 ? Float.floatToIntBits((float)this.a02) : 0);
      i = 31 * i + (this.a03 != 0.0 ? Float.floatToIntBits((float)this.a03) : 0);
      i = 31 * i + (this.a10 != 0.0 ? Float.floatToIntBits((float)this.a10) : 0);
      i = 31 * i + (this.a11 != 0.0 ? Float.floatToIntBits((float)this.a11) : 0);
      i = 31 * i + (this.a12 != 0.0 ? Float.floatToIntBits((float)this.a12) : 0);
      i = 31 * i + (this.a13 != 0.0 ? Float.floatToIntBits((float)this.a13) : 0);
      i = 31 * i + (this.a20 != 0.0 ? Float.floatToIntBits((float)this.a20) : 0);
      i = 31 * i + (this.a21 != 0.0 ? Float.floatToIntBits((float)this.a21) : 0);
      i = 31 * i + (this.a22 != 0.0 ? Float.floatToIntBits((float)this.a22) : 0);
      i = 31 * i + (this.a23 != 0.0 ? Float.floatToIntBits((float)this.a23) : 0);
      i = 31 * i + (this.a30 != 0.0 ? Float.floatToIntBits((float)this.a30) : 0);
      i = 31 * i + (this.a31 != 0.0 ? Float.floatToIntBits((float)this.a31) : 0);
      i = 31 * i + (this.a32 != 0.0 ? Float.floatToIntBits((float)this.a32) : 0);
      return 31 * i + (this.a33 != 0.0 ? Float.floatToIntBits((float)this.a33) : 0);
   }

   private static int pack(int x, int y) {
      return y * 4 + x;
   }

   public void readColumnMajor(FloatBuffer buf) {
      this.a00 = (double)buf.get(pack(0, 0));
      this.a01 = (double)buf.get(pack(0, 1));
      this.a02 = (double)buf.get(pack(0, 2));
      this.a03 = (double)buf.get(pack(0, 3));
      this.a10 = (double)buf.get(pack(1, 0));
      this.a11 = (double)buf.get(pack(1, 1));
      this.a12 = (double)buf.get(pack(1, 2));
      this.a13 = (double)buf.get(pack(1, 3));
      this.a20 = (double)buf.get(pack(2, 0));
      this.a21 = (double)buf.get(pack(2, 1));
      this.a22 = (double)buf.get(pack(2, 2));
      this.a23 = (double)buf.get(pack(2, 3));
      this.a30 = (double)buf.get(pack(3, 0));
      this.a31 = (double)buf.get(pack(3, 1));
      this.a32 = (double)buf.get(pack(3, 2));
      this.a33 = (double)buf.get(pack(3, 3));
   }

   public void readRowMajor(FloatBuffer buf) {
      this.a00 = (double)buf.get(pack(0, 0));
      this.a01 = (double)buf.get(pack(1, 0));
      this.a02 = (double)buf.get(pack(2, 0));
      this.a03 = (double)buf.get(pack(3, 0));
      this.a10 = (double)buf.get(pack(0, 1));
      this.a11 = (double)buf.get(pack(1, 1));
      this.a12 = (double)buf.get(pack(2, 1));
      this.a13 = (double)buf.get(pack(3, 1));
      this.a20 = (double)buf.get(pack(0, 2));
      this.a21 = (double)buf.get(pack(1, 2));
      this.a22 = (double)buf.get(pack(2, 2));
      this.a23 = (double)buf.get(pack(3, 2));
      this.a30 = (double)buf.get(pack(0, 3));
      this.a31 = (double)buf.get(pack(1, 3));
      this.a32 = (double)buf.get(pack(2, 3));
      this.a33 = (double)buf.get(pack(3, 3));
   }

   public void read(FloatBuffer buf, boolean rowMajor) {
      if (rowMajor) {
         this.readRowMajor(buf);
      } else {
         this.readColumnMajor(buf);
      }
   }

   public void load(VenomMatrix4f source) {
      this.a00 = source.a00;
      this.a01 = source.a01;
      this.a02 = source.a02;
      this.a03 = source.a03;
      this.a10 = source.a10;
      this.a11 = source.a11;
      this.a12 = source.a12;
      this.a13 = source.a13;
      this.a20 = source.a20;
      this.a21 = source.a21;
      this.a22 = source.a22;
      this.a23 = source.a23;
      this.a30 = source.a30;
      this.a31 = source.a31;
      this.a32 = source.a32;
      this.a33 = source.a33;
   }

   @Override
   public String toString() {
      return "VenomMatrix4f:\n"
         + this.a00
         + " "
         + this.a01
         + " "
         + this.a02
         + " "
         + this.a03
         + "\n"
         + this.a10
         + " "
         + this.a11
         + " "
         + this.a12
         + " "
         + this.a13
         + "\n"
         + this.a20
         + " "
         + this.a21
         + " "
         + this.a22
         + " "
         + this.a23
         + "\n"
         + this.a30
         + " "
         + this.a31
         + " "
         + this.a32
         + " "
         + this.a33
         + "\n";
   }

   public void loadIdentity() {
      this.a00 = 1.0;
      this.a01 = 0.0;
      this.a02 = 0.0;
      this.a03 = 0.0;
      this.a10 = 0.0;
      this.a11 = 1.0;
      this.a12 = 0.0;
      this.a13 = 0.0;
      this.a20 = 0.0;
      this.a21 = 0.0;
      this.a22 = 1.0;
      this.a23 = 0.0;
      this.a30 = 0.0;
      this.a31 = 0.0;
      this.a32 = 0.0;
      this.a33 = 1.0;
   }

   public double determinantAndAdjugate() {
      double f = this.a00 * this.a11 - this.a01 * this.a10;
      double g = this.a00 * this.a12 - this.a02 * this.a10;
      double h = this.a00 * this.a13 - this.a03 * this.a10;
      double i = this.a01 * this.a12 - this.a02 * this.a11;
      double j = this.a01 * this.a13 - this.a03 * this.a11;
      double k = this.a02 * this.a13 - this.a03 * this.a12;
      double l = this.a20 * this.a31 - this.a21 * this.a30;
      double m = this.a20 * this.a32 - this.a22 * this.a30;
      double n = this.a20 * this.a33 - this.a23 * this.a30;
      double o = this.a21 * this.a32 - this.a22 * this.a31;
      double p = this.a21 * this.a33 - this.a23 * this.a31;
      double q = this.a22 * this.a33 - this.a23 * this.a32;
      double r = this.a11 * q - this.a12 * p + this.a13 * o;
      double s = -this.a10 * q + this.a12 * n - this.a13 * m;
      double t = this.a10 * p - this.a11 * n + this.a13 * l;
      double u = -this.a10 * o + this.a11 * m - this.a12 * l;
      double v = -this.a01 * q + this.a02 * p - this.a03 * o;
      double w = this.a00 * q - this.a02 * n + this.a03 * m;
      double x = -this.a00 * p + this.a01 * n - this.a03 * l;
      double y = this.a00 * o - this.a01 * m + this.a02 * l;
      double z = this.a31 * k - this.a32 * j + this.a33 * i;
      double aa = -this.a30 * k + this.a32 * h - this.a33 * g;
      double ab = this.a30 * j - this.a31 * h + this.a33 * f;
      double ac = -this.a30 * i + this.a31 * g - this.a32 * f;
      double ad = -this.a21 * k + this.a22 * j - this.a23 * i;
      double ae = this.a20 * k - this.a22 * h + this.a23 * g;
      double af = -this.a20 * j + this.a21 * h - this.a23 * f;
      double ag = this.a20 * i - this.a21 * g + this.a22 * f;
      this.a00 = r;
      this.a10 = s;
      this.a20 = t;
      this.a30 = u;
      this.a01 = v;
      this.a11 = w;
      this.a21 = x;
      this.a31 = y;
      this.a02 = z;
      this.a12 = aa;
      this.a22 = ab;
      this.a32 = ac;
      this.a03 = ad;
      this.a13 = ae;
      this.a23 = af;
      this.a33 = ag;
      return f * q - g * p + h * o + i * n - j * m + k * l;
   }

   public double determinant() {
      double f = this.a00 * this.a11 - this.a01 * this.a10;
      double g = this.a00 * this.a12 - this.a02 * this.a10;
      double h = this.a00 * this.a13 - this.a03 * this.a10;
      double i = this.a01 * this.a12 - this.a02 * this.a11;
      double j = this.a01 * this.a13 - this.a03 * this.a11;
      double k = this.a02 * this.a13 - this.a03 * this.a12;
      double l = this.a20 * this.a31 - this.a21 * this.a30;
      double m = this.a20 * this.a32 - this.a22 * this.a30;
      double n = this.a20 * this.a33 - this.a23 * this.a30;
      double o = this.a21 * this.a32 - this.a22 * this.a31;
      double p = this.a21 * this.a33 - this.a23 * this.a31;
      double q = this.a22 * this.a33 - this.a23 * this.a32;
      return f * q - g * p + h * o + i * n - j * m + k * l;
   }

   public void transpose() {
      double f = this.a10;
      this.a10 = this.a01;
      this.a01 = f;
      f = this.a20;
      this.a20 = this.a02;
      this.a02 = f;
      f = this.a21;
      this.a21 = this.a12;
      this.a12 = f;
      f = this.a30;
      this.a30 = this.a03;
      this.a03 = f;
      f = this.a31;
      this.a31 = this.a13;
      this.a13 = f;
      f = this.a32;
      this.a32 = this.a23;
      this.a23 = f;
   }

   public boolean invert() {
      double f = this.determinantAndAdjugate();
      if (Math.abs(f) > 1.0E-6F) {
         this.multiply(f);
         return true;
      } else {
         return false;
      }
   }

   public void multiply(VenomMatrix4f matrix) {
      double f = this.a00 * matrix.a00 + this.a01 * matrix.a10 + this.a02 * matrix.a20 + this.a03 * matrix.a30;
      double g = this.a00 * matrix.a01 + this.a01 * matrix.a11 + this.a02 * matrix.a21 + this.a03 * matrix.a31;
      double h = this.a00 * matrix.a02 + this.a01 * matrix.a12 + this.a02 * matrix.a22 + this.a03 * matrix.a32;
      double i = this.a00 * matrix.a03 + this.a01 * matrix.a13 + this.a02 * matrix.a23 + this.a03 * matrix.a33;
      double j = this.a10 * matrix.a00 + this.a11 * matrix.a10 + this.a12 * matrix.a20 + this.a13 * matrix.a30;
      double k = this.a10 * matrix.a01 + this.a11 * matrix.a11 + this.a12 * matrix.a21 + this.a13 * matrix.a31;
      double l = this.a10 * matrix.a02 + this.a11 * matrix.a12 + this.a12 * matrix.a22 + this.a13 * matrix.a32;
      double m = this.a10 * matrix.a03 + this.a11 * matrix.a13 + this.a12 * matrix.a23 + this.a13 * matrix.a33;
      double n = this.a20 * matrix.a00 + this.a21 * matrix.a10 + this.a22 * matrix.a20 + this.a23 * matrix.a30;
      double o = this.a20 * matrix.a01 + this.a21 * matrix.a11 + this.a22 * matrix.a21 + this.a23 * matrix.a31;
      double p = this.a20 * matrix.a02 + this.a21 * matrix.a12 + this.a22 * matrix.a22 + this.a23 * matrix.a32;
      double q = this.a20 * matrix.a03 + this.a21 * matrix.a13 + this.a22 * matrix.a23 + this.a23 * matrix.a33;
      double r = this.a30 * matrix.a00 + this.a31 * matrix.a10 + this.a32 * matrix.a20 + this.a33 * matrix.a30;
      double s = this.a30 * matrix.a01 + this.a31 * matrix.a11 + this.a32 * matrix.a21 + this.a33 * matrix.a31;
      double t = this.a30 * matrix.a02 + this.a31 * matrix.a12 + this.a32 * matrix.a22 + this.a33 * matrix.a32;
      double u = this.a30 * matrix.a03 + this.a31 * matrix.a13 + this.a32 * matrix.a23 + this.a33 * matrix.a33;
      this.a00 = f;
      this.a01 = g;
      this.a02 = h;
      this.a03 = i;
      this.a10 = j;
      this.a11 = k;
      this.a12 = l;
      this.a13 = m;
      this.a20 = n;
      this.a21 = o;
      this.a22 = p;
      this.a23 = q;
      this.a30 = r;
      this.a31 = s;
      this.a32 = t;
      this.a33 = u;
   }

   public void multiply(Quaternion quaternion) {
      this.multiply(new VenomMatrix4f(quaternion));
   }

   public void multiply(double scalar) {
      this.a00 *= scalar;
      this.a01 *= scalar;
      this.a02 *= scalar;
      this.a03 *= scalar;
      this.a10 *= scalar;
      this.a11 *= scalar;
      this.a12 *= scalar;
      this.a13 *= scalar;
      this.a20 *= scalar;
      this.a21 *= scalar;
      this.a22 *= scalar;
      this.a23 *= scalar;
      this.a30 *= scalar;
      this.a31 *= scalar;
      this.a32 *= scalar;
      this.a33 *= scalar;
   }

   public void add(VenomMatrix4f matrix) {
      this.a00 += matrix.a00;
      this.a01 += matrix.a01;
      this.a02 += matrix.a02;
      this.a03 += matrix.a03;
      this.a10 += matrix.a10;
      this.a11 += matrix.a11;
      this.a12 += matrix.a12;
      this.a13 += matrix.a13;
      this.a20 += matrix.a20;
      this.a21 += matrix.a21;
      this.a22 += matrix.a22;
      this.a23 += matrix.a23;
      this.a30 += matrix.a30;
      this.a31 += matrix.a31;
      this.a32 += matrix.a32;
      this.a33 += matrix.a33;
   }

   public void subtract(VenomMatrix4f matrix) {
      this.a00 -= matrix.a00;
      this.a01 -= matrix.a01;
      this.a02 -= matrix.a02;
      this.a03 -= matrix.a03;
      this.a10 -= matrix.a10;
      this.a11 -= matrix.a11;
      this.a12 -= matrix.a12;
      this.a13 -= matrix.a13;
      this.a20 -= matrix.a20;
      this.a21 -= matrix.a21;
      this.a22 -= matrix.a22;
      this.a23 -= matrix.a23;
      this.a30 -= matrix.a30;
      this.a31 -= matrix.a31;
      this.a32 -= matrix.a32;
      this.a33 -= matrix.a33;
   }

   public double trace() {
      return this.a00 + this.a11 + this.a22 + this.a33;
   }

   public static VenomMatrix4f viewboxMatrix(double fov, double aspectRatio, double cameraDepth, double viewDistance) {
      double f = 1.0 / Math.tan(fov * (float) (Math.PI / 180.0) / 2.0);
      VenomMatrix4f matrix4f = new VenomMatrix4f();
      matrix4f.a00 = f / aspectRatio;
      matrix4f.a11 = f;
      matrix4f.a22 = (viewDistance + cameraDepth) / (cameraDepth - viewDistance);
      matrix4f.a32 = -1.0;
      matrix4f.a23 = 2.0 * viewDistance * cameraDepth / (cameraDepth - viewDistance);
      return matrix4f;
   }

   public static VenomMatrix4f projectionMatrix(double width, double height, double nearPlane, double farPlane) {
      VenomMatrix4f matrix4f = new VenomMatrix4f();
      matrix4f.a00 = 2.0 / width;
      matrix4f.a11 = 2.0 / height;
      double f = farPlane - nearPlane;
      matrix4f.a22 = -2.0 / f;
      matrix4f.a33 = 1.0;
      matrix4f.a03 = -1.0;
      matrix4f.a13 = 1.0;
      matrix4f.a23 = -(farPlane + nearPlane) / f;
      return matrix4f;
   }

   public static VenomMatrix4f projectionMatrix(double left, double right, double bottom, double top, double nearPlane, double farPlane) {
      VenomMatrix4f matrix4f = new VenomMatrix4f();
      double f = right - left;
      double g = bottom - top;
      double h = farPlane - nearPlane;
      matrix4f.a00 = 2.0 / f;
      matrix4f.a11 = 2.0 / g;
      matrix4f.a22 = -2.0 / h;
      matrix4f.a03 = -(right + left) / f;
      matrix4f.a13 = -(bottom + top) / g;
      matrix4f.a23 = -(farPlane + nearPlane) / h;
      matrix4f.a33 = 1.0;
      return matrix4f;
   }

   public void addToLastColumn(Vec3f vector) {
      this.a03 += (double)vector.getX();
      this.a13 += (double)vector.getY();
      this.a23 += (double)vector.getZ();
   }

   public VenomMatrix4f copy() {
      return new VenomMatrix4f(this);
   }

   public void multiplyByTranslation(double x, double y, double z) {
      this.a03 += this.a00 * x + this.a01 * y + this.a02 * z;
      this.a13 += this.a10 * x + this.a11 * y + this.a12 * z;
      this.a23 += this.a20 * x + this.a21 * y + this.a22 * z;
      this.a33 += this.a30 * x + this.a31 * y + this.a32 * z;
   }

   public static VenomMatrix4f scale(double x, double y, double z) {
      VenomMatrix4f matrix4f = new VenomMatrix4f();
      matrix4f.a00 = x;
      matrix4f.a11 = y;
      matrix4f.a22 = z;
      matrix4f.a33 = 1.0;
      return matrix4f;
   }

   public static VenomMatrix4f translate(double x, double y, double z) {
      VenomMatrix4f matrix4f = new VenomMatrix4f();
      matrix4f.a00 = 1.0;
      matrix4f.a11 = 1.0;
      matrix4f.a22 = 1.0;
      matrix4f.a33 = 1.0;
      matrix4f.a03 = x;
      matrix4f.a13 = y;
      matrix4f.a23 = z;
      return matrix4f;
   }
}
