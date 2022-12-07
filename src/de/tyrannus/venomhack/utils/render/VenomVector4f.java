package de.tyrannus.venomhack.utils.render;

import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.math.MathHelper;

public class VenomVector4f {
   private double x;
   private double y;
   private double z;
   private double w;

   public VenomVector4f(double x, double y, double z, double w) {
      this.x = x;
      this.y = y;
      this.z = z;
      this.w = w;
   }

   public VenomVector4f(Vec3f vector) {
      this((double)vector.getX(), (double)vector.getY(), (double)vector.getZ(), 1.0);
   }

   public double getX() {
      return this.x;
   }

   public double getY() {
      return this.y;
   }

   public double getZ() {
      return this.z;
   }

   public double getW() {
      return this.w;
   }

   public void multiply(double value) {
      this.x *= value;
      this.y *= value;
      this.z *= value;
      this.w *= value;
   }

   public void multiplyComponentwise(Vec3f vector) {
      this.x *= (double)vector.getX();
      this.y *= (double)vector.getY();
      this.z *= (double)vector.getZ();
   }

   public void set(double x, double y, double z, double w) {
   }

   public void add(double x, double y, double z, double w) {
      x += x;
      y += y;
      z += z;
      w += w;
   }

   public double dotProduct(VenomVector4f other) {
      return this.x * other.x + this.y * other.y + this.z * other.z + this.w * other.w;
   }

   public boolean normalize() {
      double f = this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w;
      if (f < 1.0E-5) {
         return false;
      } else {
         double g = MathHelper.fastInverseSqrt(f);
         this.x *= g;
         this.y *= g;
         this.z *= g;
         this.w *= g;
         return true;
      }
   }

   public void transform(VenomMatrix4f matrix) {
      double f = this.x;
      double g = this.y;
      double h = this.z;
      double i = this.w;
      this.x = matrix.a00 * f + matrix.a01 * g + matrix.a02 * h + matrix.a03 * i;
      this.y = matrix.a10 * f + matrix.a11 * g + matrix.a12 * h + matrix.a13 * i;
      this.z = matrix.a20 * f + matrix.a21 * g + matrix.a22 * h + matrix.a23 * i;
      this.w = matrix.a30 * f + matrix.a31 * g + matrix.a32 * h + matrix.a33 * i;
   }

   public void rotate(Quaternion rotation) {
      Quaternion quaternion = new Quaternion(rotation);
      quaternion.hamiltonProduct(new Quaternion((float)this.getX(), (float)this.getY(), (float)this.getZ(), 0.0F));
      Quaternion quaternion2 = new Quaternion(rotation);
      quaternion2.conjugate();
      quaternion.hamiltonProduct(quaternion2);
      this.set((double)quaternion.getX(), (double)quaternion.getY(), (double)quaternion.getZ(), this.getW());
   }

   public void normalizeProjectiveCoordinates() {
      this.x /= this.w;
      this.y /= this.w;
      this.z /= this.w;
      this.w = 1.0;
   }

   public void lerp(VenomVector4f to, double delta) {
      double f = 1.0 - delta;
      this.x = this.x * f + to.x * delta;
      this.y = this.y * f + to.y * delta;
      this.z = this.z * f + to.z * delta;
      this.w = this.w * f + to.w * delta;
   }

   @Override
   public String toString() {
      return "[" + this.x + ", " + this.y + ", " + this.z + ", " + this.w + "]";
   }
}
