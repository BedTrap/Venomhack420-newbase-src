package de.tyrannus.venomhack.utils;

import de.tyrannus.venomhack.Venomhack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class RangeUtils {
   public static Vec3d playerEyePos() {
      return new Vec3d(
         Venomhack.mc.player.getX(),
         Venomhack.mc.player.getY() + (double)Venomhack.mc.player.getEyeHeight(Venomhack.mc.player.getPose()),
         Venomhack.mc.player.getZ()
      );
   }

   public static boolean isWithinRange(RangeUtils.Origin origin, Entity entity, double range) {
      return squaredDistanceTo(origin, entity) <= range * range;
   }

   public static boolean isWithinRange(RangeUtils.Origin origin, BlockPos blockPos, double range) {
      return squaredDistanceTo(origin, blockPos) <= range * range;
   }

   public static boolean isWithinRange(RangeUtils.Origin origin, Vec3d vec3d, double range) {
      return squaredDistanceTo(origin, vec3d) <= range * range;
   }

   public static double squaredDistanceTo(RangeUtils.Origin origin, Entity entity) {
      return squaredDistanceTo(origin, entity.getPos().x, entity.getPos().y, entity.getPos().z);
   }

   public static double squaredDistanceTo(RangeUtils.Origin origin, BlockPos blockPos) {
      return squaredDistanceTo(origin, (double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ());
   }

   public static double squaredDistanceTo(RangeUtils.Origin origin, Vec3d vec3d) {
      return squaredDistanceTo(origin, vec3d.x, vec3d.y, vec3d.z);
   }

   public static double squaredDistanceTo(RangeUtils.Origin origin, double x, double y, double z) {
      double f = Venomhack.mc.player.getX() - x;
      double g = Venomhack.mc.player.getY()
         + (double)(origin == RangeUtils.Origin.Eye ? Venomhack.mc.player.getEyeHeight(Venomhack.mc.player.getPose()) : 0.0F)
         - y;
      double h = Venomhack.mc.player.getZ() - z;
      return f * f + g * g + h * h;
   }

   public static enum Origin {
      Eye,
      Feet;
   }
}
