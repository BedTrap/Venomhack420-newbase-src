package de.tyrannus.venomhack.utils;

import de.tyrannus.venomhack.Venomhack;
import de.tyrannus.venomhack.modules.combat.autocrystal.Origin;
import de.tyrannus.venomhack.utils.players.PlayerUtils;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.mob.GiantEntity;
import net.minecraft.world.GameMode;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.MathHelper;

public class AntiCheatHelper {
   public static boolean outOfMiningRange(BlockPos pos, Origin origin, double range) {
      if (origin == Origin.VANILLA) {
         double deltaX = Venomhack.mc.player.getX() - ((double)pos.getX() + 0.5);
         double deltaY = Venomhack.mc.player.getY() - ((double)pos.getY() + 0.5) + 1.5;
         double deltaZ = Venomhack.mc.player.getZ() - ((double)pos.getZ() + 0.5);
         return deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ > range * range;
      } else {
         Vec3d eyesPos = PlayerUtils.eyePos();
         double dx = eyesPos.x - (double)pos.getX() - 0.5;
         double dy = eyesPos.y - (double)pos.getY() - 0.5;
         double dz = eyesPos.z - (double)pos.getZ() - 0.5;
         return dx * dx + dy * dy + dz * dz > range * range;
      }
   }

   public static boolean outOfPlaceRange(BlockPos pos, Origin origin, double range) {
      if (origin == Origin.VANILLA) {
         return Venomhack.mc.player.squaredDistanceTo((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5)
            >= range * range;
      } else {
         Vec3d eyesPos = PlayerUtils.eyePos();
         double dx = eyesPos.x - (double)pos.getX() - 0.5;
         double dy = eyesPos.y - (double)pos.getY() - 0.5;
         double dz = eyesPos.z - (double)pos.getZ() - 0.5;
         return dx * dx + dy * dy + dz * dz > range * range;
      }
   }

   public static boolean notInteractableStrict(int playerX, int playerY, int playerZ, BlockPos blockPos, Direction direction) {
      if (playerX == blockPos.getX() && playerY == blockPos.getY() && playerZ == blockPos.getZ()) {
         return false;
      } else {
         boolean fullBounds = Venomhack.mc.world.getBlockState(blockPos).isFullCube(Venomhack.mc.world, blockPos)
            || Analyser.pendingObsidian.containsKey(blockPos);
         Set<Direction> interactableDirections = getInteractableDirections(
            playerX - blockPos.getX(), playerY - blockPos.getY(), playerZ - blockPos.getZ(), fullBounds
         );
         return !interactableDirections.contains(direction) ? true : isDirectionBlocked(blockPos, interactableDirections, direction, fullBounds);
      }
   }

   public static Set<Direction> getInteractableDirections(int xdiff, int ydiff, int zdiff, boolean fullBounds) {
      HashSet<Direction> directions = new HashSet(6);
      if (!fullBounds) {
         if (xdiff == 0) {
            directions.add(Direction.EAST);
            directions.add(Direction.WEST);
         }

         if (zdiff == 0) {
            directions.add(Direction.SOUTH);
            directions.add(Direction.NORTH);
         }
      }

      if (ydiff == 0) {
         directions.add(Direction.UP);
         directions.add(Direction.DOWN);
      } else {
         directions.add(ydiff > 0 ? Direction.UP : Direction.DOWN);
      }

      if (xdiff != 0) {
         directions.add(xdiff > 0 ? Direction.EAST : Direction.WEST);
      }

      if (zdiff != 0) {
         directions.add(zdiff > 0 ? Direction.SOUTH : Direction.NORTH);
      }

      return directions;
   }

   public static boolean isDirectionBlocked(BlockPos block, Set<Direction> interactableDirections, Direction tDirection, boolean hasFullBounds) {
      BlockState offsetState = Venomhack.mc.world.getBlockState(block.offset(tDirection));
      if (hasFullBounds) {
         return offsetState.getBlock().collidable && offsetState.isFullCube(Venomhack.mc.world, block)
            || Analyser.pendingObsidian.containsKey(block.offset(tDirection));
      } else {
         for(Direction direction : interactableDirections) {
            offsetState = Venomhack.mc.world.getBlockState(block.offset(direction));
            if (!offsetState.isFullCube(Venomhack.mc.world, block)
               || offsetState.getBlock().collidable
               || Analyser.pendingObsidian.containsKey(block.offset(direction))) {
               return false;
            }
         }

         return true;
      }
   }

   public static boolean outOfHitRange(Entity target, Origin origin, double range, double wallsRange, boolean hitboxes) {
      double squaredDistanceVanilla = Venomhack.mc.player.squaredDistanceTo(target.getPos());
      if (squaredDistanceVanilla >= 36.0) {
         return true;
      } else if (origin == Origin.VANILLA) {
         if (squaredDistanceVanilla >= range * range) {
            return true;
         } else {
            return squaredDistanceVanilla >= wallsRange * wallsRange && !Venomhack.mc.player.canSee(target);
         }
      } else {
         if (Venomhack.mc.interactionManager.getCurrentGameMode() == GameMode.CREATIVE) {
            range = 6.0;
         } else if (target instanceof EnderDragonEntity) {
            range += 6.5;
         } else if (target instanceof GiantEntity) {
            ++range;
         }

         Vec3d eyePos = PlayerUtils.eyePos();
         double targetY = MathHelper.clamp(eyePos.y, target.getY(), target.getY() + (double)target.getHeight());
         double dx = target.getX() - eyePos.x;
         double dy = targetY - eyePos.y;
         double dz = target.getZ() - eyePos.z;
         double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
         if (hitboxes && (eyePos.x != target.getX() || eyePos.z != target.getZ())) {
            double centerToEdge = 0.0;
            float targetHalfWidth = target.getWidth() * 0.5F;
            double hitX = target.getX();
            double hitZ = target.getZ();
            double xOffset = eyePos.x - target.getX();
            double zOffset = eyePos.z - target.getZ();
            double offsetLength = Math.sqrt(xOffset * xOffset + zOffset * zOffset);
            if (offsetLength >= (double)targetHalfWidth * Math.sqrt(2.0)) {
               if (zOffset > 0.0) {
                  hitZ = target.getZ() + (double)targetHalfWidth;
               } else if (zOffset < 0.0) {
                  hitZ = target.getZ() - (double)targetHalfWidth;
               } else if (xOffset > 0.0) {
                  hitX = target.getX() + (double)targetHalfWidth;
               } else {
                  hitX = target.getX() - (double)targetHalfWidth;
               }

               double adjustedHitVecLength = Math.sqrt(
                  (hitX - target.getX()) * (hitX - target.getX()) + (hitZ - target.getZ()) * (hitZ - target.getZ())
               );
               double dotProduct = xOffset * (hitX - target.getX()) + zOffset * (hitZ - target.getZ());
               double theta = Math.min(1.0, Math.max(dotProduct / (offsetLength * adjustedHitVecLength), -1.0));
               double angle = Math.acos(theta);
               if (angle > Math.PI / 4) {
                  angle = (Math.PI / 2) - angle;
               }

               if (angle >= 0.0 && angle <= Math.PI / 4) {
                  centerToEdge = (double)targetHalfWidth / Math.cos(angle);
               }
            }

            distance -= centerToEdge;
         }

         if (distance > range) {
            return true;
         } else {
            return distance > wallsRange && !Venomhack.mc.player.canSee(target);
         }
      }
   }
}
