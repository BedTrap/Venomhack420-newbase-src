package de.tyrannus.venomhack.modules.combat.autocrystal;

import de.tyrannus.venomhack.Venomhack;
import de.tyrannus.venomhack.utils.players.PlayerUtils;
import de.tyrannus.venomhack.utils.world.BlockUtils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.block.Blocks;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.block.BlockState;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.util.hit.BlockHitResult;

public record CalculationHelper(ArrayList<BlockPos> blocks, boolean oldMode, ConcurrentHashMap<Integer, Long> toRemove) {
   public CalculationHelper placeableBlockFilterer() {
      assert Venomhack.mc.world != null;

      WorldChunk chunk = null;
      ChunkSection section = null;
      int length = this.blocks.size();

      for(int i = 0; i < length; ++i) {
         BlockPos pos = (BlockPos)this.blocks.get(i);
         if (chunk == null || pos.getX() >> 4 != chunk.getPos().x || pos.getZ() >> 4 != chunk.getPos().z) {
            chunk = Venomhack.mc.world.getChunkManager().getChunk(pos.getX() >> 4, pos.getZ() >> 4, ChunkStatus.FULL, true);
         }

         assert chunk != null;

         if (section == null || pos.getY() >> 4 != section.getYOffset()) {
            section = chunk.getSection((pos.getY() >> 4) + 4);
         }

         BlockState state = section.getBlockState(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15);
         Block block = state.getBlock();
         if (block == Blocks.OBSIDIAN || block == Blocks.BEDROCK || state.getMaterial().isReplaceable()) {
            if (pos.getY() + 1 >> 4 != section.getYOffset()) {
               section = chunk.getSection((pos.getY() + 1 >> 4) + 4);
            }

            if (section.getBlockState(pos.getX() & 15, pos.getY() + 1 & 15, pos.getZ() & 15).isAir()) {
               if (!this.oldMode) {
                  continue;
               }

               if (pos.getY() + 2 >> 4 != section.getYOffset()) {
                  section = chunk.getSection((pos.getY() + 2 >> 4) + 4);
               }

               if (section.getBlockState(pos.getX() & 15, pos.getY() + 2 & 15, pos.getZ() & 15).isAir()) {
                  continue;
               }
            }
         }

         this.blocks.remove(i);
         --i;
         --length;
      }

      return this;
   }

   public CalculationHelper filterForReach(double reach, double wallReach, boolean strictDirections, Origin origin, ReachMode reachMode) {
      int length = this.blocks.size();

      for(int i = 0; i < length; ++i) {
         BlockPos block = (BlockPos)this.blocks.get(i);
         if (getBlockResult(block, reach, wallReach, strictDirections, origin, reachMode) == null) {
            this.blocks.remove(i);
            --i;
            --length;
         }
      }

      return this;
   }

   public static BlockHitResult getBlockResult(BlockPos pos, double reach, double wallReach, boolean strictDirections, Origin origin, ReachMode reachMode) {
      Vec3d vec3d = null;
      Direction direction = null;
      Vec3d playerPos = PlayerUtils.eyePos();
      Direction closestSide = getClosestSide(pos, reach, wallReach, strictDirections, playerPos, reachMode);
      switch(reachMode) {
         case BLOCK_CENTER:
            playerPos.distanceTo(new Vec3d((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5));
         case FACE_CENTER:
            break;
         default:
            throw new IllegalStateException("Unexpected value: " + reachMode);
      }

      if (reachMode == ReachMode.BLOCK_CENTER) {
         Serializable distance = playerPos.distanceTo(
            new Vec3d((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5)
         );
      }

      return new BlockHitResult(vec3d, direction, pos, false);
   }

   public static Direction getClosestSide(BlockPos pos, double reach, double wallReach, boolean strictDirections, Vec3d playerPos, ReachMode reachMode) {
      Direction bestDirection = null;
      double bestDistance = 69.0;

      for(Direction dir : Arrays.asList(Direction.NORTH, Direction.WEST, Direction.UP)) {
         double distance = getDistanceToFace(pos, dir, playerPos, reachMode);
         double oppositeDistance = getDistanceToFace(pos, dir.getOpposite(), playerPos, reachMode);
         if (oppositeDistance < distance) {
         }
      }

      return Direction.DOWN;
   }

   public static double getDistanceToFace(BlockPos pos, Direction direction, Vec3d playerPos, ReachMode reachMode) {
      Vec3d sideVec = BlockUtils.sideVec(pos, direction);

      return switch(reachMode) {
         case BLOCK_CENTER, ANY_FACE -> {
            double delta = 0.0625;
            double bestDistance = 69.0;

            for(double x = 0.0; x < 1.0; x += delta) {
               if (direction.getOffsetX() == 0) {
                  for(double y = 0.0; y < 1.0; y += delta) {
                     if (direction.getOffsetY() == 0) {
                        for(double z = 0.0; z < 1.0; z += delta) {
                           if (direction.getOffsetZ() == 0) {
                              double distance = playerPos.distanceTo(sideVec.add(x, y, z));
                              if (distance < bestDistance) {
                                 bestDistance = distance;
                              }
                           }
                        }
                     }
                  }
               }
            }

            yield bestDistance;
         }
         case FACE_CENTER -> playerPos.distanceTo(sideVec);
      };
   }

   public CalculationHelper filterForEntities() {
      assert Venomhack.mc.world != null;

      int length = this.blocks.size();

      for(int i = 0; i < length; ++i) {
         BlockPos pos = (BlockPos)this.blocks.get(i);
         int x = pos.getX();
         int y = pos.getY();
         int z = pos.getZ();

         label44:
         for(Entity entity : Venomhack.mc
            .world
            .getOtherEntities(null, new Box((double)x + 0.01, (double)y + 0.01, (double)z + 0.01, (double)x + 0.99, (double)y + 1.99, (double)z + 0.99))) {
            if (!entity.isSpectator()) {
               if (entity instanceof EndCrystalEntity) {
                  for(Entry<Integer, Long> entry : this.toRemove.entrySet()) {
                     if (entry.getKey() == entity.getId()) {
                        continue label44;
                     }
                  }
               }

               this.blocks.remove(i);
               --i;
               --length;
            }
         }
      }

      return this;
   }
}
