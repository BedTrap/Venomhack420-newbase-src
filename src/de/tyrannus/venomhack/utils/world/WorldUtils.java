package de.tyrannus.venomhack.utils.world;

import de.tyrannus.venomhack.Venomhack;
import de.tyrannus.venomhack.utils.RangeUtils;
import de.tyrannus.venomhack.utils.Utils;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.Hand;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.world.World;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Blocks;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Box2;
import net.minecraft.util.math.Vec3d;
import net.minecraft.block.BlockState;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.world.RaycastContext;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.class_2847;
import net.minecraft.world.RaycastContext.class_242;
import net.minecraft.world.RaycastContext.class_3960;

public class WorldUtils extends Utils {
   public static final Box2[] CITY_WITH_BURROW = new Box2[]{
      new Box2(0, 0, 0), new Box2(1, 0, 0), new Box2(-1, 0, 0), new Box2(0, 0, 1), new Box2(0, 0, -1)
   };
   public static final Box2[] CITY = new Box2[]{
      new Box2(1, 0, 0), new Box2(-1, 0, 0), new Box2(0, 0, 1), new Box2(0, 0, -1)
   };

   public static boolean isSurrounded(LivingEntity target, boolean doubles, boolean onlyBlastProof) {
      BlockPos blockPos = target.getBlockPos();
      int air = 0;

      for(Direction direction : Direction.values()) {
         if (direction != Direction.UP) {
            BlockState state = Venomhack.mc.world.getBlockState(blockPos.offset(direction));
            if (state.getMaterial().isReplaceable() || onlyBlastProof && state.getBlock().getBlastResistance() < 600.0F) {
               if (!doubles || direction == Direction.DOWN) {
                  return false;
               }

               ++air;

               for(Direction dir : Direction.values()) {
                  if (dir != direction.getOpposite() && dir != Direction.UP) {
                     BlockState state2 = Venomhack.mc.world.getBlockState(blockPos.offset(direction).offset(dir));
                     if (state2.getMaterial().isReplaceable() || onlyBlastProof && state2.getBlock().getBlastResistance() < 600.0F) {
                        return false;
                     }
                  }
               }
            }
         }
      }

      return air < 2;
   }

   public static boolean isTrapped(LivingEntity target) {
      for(Box2 city : CITY) {
         BlockState state = Venomhack.mc.world.getBlockState(target.getBlockPos().add(city).up());
         if (state.getMaterial().isReplaceable() || state.getBlock() instanceof BedBlock) {
            return false;
         }
      }

      BlockState state = Venomhack.mc.world.getBlockState(target.getBlockPos().up(2));
      return !state.getMaterial().isReplaceable() && !(state.getBlock() instanceof BedBlock) && isSurrounded(target, false, false);
   }

   public static boolean notSafe(LivingEntity player) {
      return !isSurrounded(player, true, true) && !isBurrowed(player);
   }

   public static void mine(BlockPos blockPos, boolean swing, boolean rotate) {
      if (!rotate) {
         Venomhack.mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(class_2847.START_DESTROY_BLOCK, blockPos, Direction.UP));
         Venomhack.mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(class_2847.STOP_DESTROY_BLOCK, blockPos, Direction.UP));
         if (swing) {
            Venomhack.mc.player.swingHand(Hand.MAIN_HAND);
         } else {
            Venomhack.mc.player.networkHandler.sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
         }
      }
   }

   public static byte getSurroundBreak(LivingEntity target, BlockPos pos) {
      if (target.getPos().squaredDistanceTo(Vec3d.ofCenter(pos)) > 16.0) {
         return 0;
      } else {
         float reach = Venomhack.mc.interactionManager.getReachDistance();
         BlockPos targetBlock = target.getBlockPos();
         pos = new BlockPos(pos).up();
         byte value = 0;

         for(Box2 vec3i : CITY) {
            if (Venomhack.mc.world.getBlockState(targetBlock.add(vec3i)).getBlock() != Blocks.BEDROCK
               && RangeUtils.isWithinRange(RangeUtils.Origin.Feet, Vec3d.ofCenter(targetBlock.add(vec3i)), (double)reach)) {
               boolean x = vec3i.equals(CITY[0]) || vec3i.equals(CITY[2]);
               Box2 offsetVec = new Box2(x ? 0 : 1, 0, x ? 1 : 0);
               if (targetBlock.add(vec3i.multiply(2)).equals(pos)) {
                  return 5;
               }

               if (targetBlock.add(vec3i.multiply(2)).add(offsetVec).equals(pos)) {
                  value = 4;
               }

               if (targetBlock.add(vec3i.multiply(2)).add(offsetVec.multiply(-1)).equals(pos)) {
                  value = 4;
               }

               if (targetBlock.add(vec3i.multiply(2)).down().equals(pos)) {
                  value = 3;
               }

               if (targetBlock.add(vec3i.multiply(2)).down().add(offsetVec).equals(pos)) {
                  value = 2;
               }

               if (targetBlock.add(vec3i.multiply(2)).down().add(offsetVec.multiply(-1)).equals(pos)) {
                  value = 2;
               }

               if (targetBlock.add(vec3i).add(offsetVec).equals(pos)) {
                  value = 1;
               }

               if (targetBlock.add(vec3i).down().add(offsetVec).equals(pos)) {
                  value = 1;
               }

               if (targetBlock.add(vec3i).add(offsetVec.multiply(-1)).equals(pos)) {
                  value = 1;
               }

               if (targetBlock.add(vec3i).down().add(offsetVec.multiply(-1)).equals(pos)) {
                  value = 1;
               }

               if (targetBlock.add(vec3i).down().equals(pos)) {
                  value = 1;
               }

               if (targetBlock.add(offsetVec).down().equals(pos)) {
                  value = 1;
               }

               if (targetBlock.add(offsetVec.multiply(-1)).down().equals(pos)) {
                  value = 1;
               }
            }
         }

         return value;
      }
   }

   public static boolean isFucked(LivingEntity target) {
      int count = 0;
      int count2 = 0;
      if (isBurrowed(target)) {
         return false;
      } else {
         if (!Venomhack.mc.world.getBlockState(target.getBlockPos().add(1, 0, 0)).getMaterial().isReplaceable()) {
            ++count;
         }

         if (!Venomhack.mc.world.getBlockState(target.getBlockPos().add(-1, 0, 0)).getMaterial().isReplaceable()) {
            ++count;
         }

         if (!Venomhack.mc.world.getBlockState(target.getBlockPos().add(0, 0, 1)).getMaterial().isReplaceable()) {
            ++count;
         }

         if (!Venomhack.mc.world.getBlockState(target.getBlockPos().add(0, 0, -1)).getMaterial().isReplaceable()) {
            ++count;
         }

         if (count == 4) {
            return false;
         } else if (count == 3) {
            return true;
         } else if (Venomhack.mc.world.getBlockState(target.getBlockPos().add(0, 2, 0)).getBlock().collidable) {
            return true;
         } else {
            if (!Venomhack.mc.world.getBlockState(target.getBlockPos().add(1, 1, 0)).getMaterial().isReplaceable()) {
               ++count2;
            }

            if (!Venomhack.mc.world.getBlockState(target.getBlockPos().add(-1, 1, 0)).getMaterial().isReplaceable()) {
               ++count2;
            }

            if (!Venomhack.mc.world.getBlockState(target.getBlockPos().add(0, 1, 1)).getMaterial().isReplaceable()) {
               ++count2;
            }

            if (!Venomhack.mc.world.getBlockState(target.getBlockPos().add(0, 1, -1)).getMaterial().isReplaceable()) {
               ++count2;
            }

            return count2 == 4;
         }
      }
   }

   public static boolean isBurrowed(LivingEntity target) {
      return Venomhack.mc
            .world
            .getBlockState(mutable.set(target.getX() + 0.3, target.getY(), target.getZ() + 0.3))
            .getBlock()
            .collidable
         || Venomhack.mc
            .world
            .getBlockState(mutable.set(target.getX() + 0.3, target.getY(), target.getZ() - 0.3))
            .getBlock()
            .collidable
         || Venomhack.mc
            .world
            .getBlockState(mutable.set(target.getX() - 0.3, target.getY(), target.getZ() + 0.3))
            .getBlock()
            .collidable
         || Venomhack.mc
            .world
            .getBlockState(mutable.set(target.getX() - 0.3, target.getY(), target.getZ() - 0.3))
            .getBlock()
            .collidable;
   }

   public static boolean isObbyBurrowed(LivingEntity target) {
      return Venomhack.mc
               .world
               .getBlockState(mutable.set(target.getX() + 0.3, target.getY(), target.getZ() + 0.3))
               .getBlock()
               .getBlastResistance()
            > 600.0F
         || Venomhack.mc
               .world
               .getBlockState(mutable.set(target.getX() + 0.3, target.getY(), target.getZ() - 0.3))
               .getBlock()
               .getBlastResistance()
            > 600.0F
         || Venomhack.mc
               .world
               .getBlockState(mutable.set(target.getX() - 0.3, target.getY(), target.getZ() + 0.3))
               .getBlock()
               .getBlastResistance()
            > 600.0F
         || Venomhack.mc
               .world
               .getBlockState(mutable.set(target.getX() - 0.3, target.getY(), target.getZ() - 0.3))
               .getBlock()
               .getBlastResistance()
            > 600.0F;
   }

   public static boolean isSurroundBroken(LivingEntity target) {
      BlockPos targetBlockPos = target.getBlockPos();

      for(Box2 block : CITY) {
         if (isBlockSurroundBroken(targetBlockPos.add(block))) {
            return true;
         }
      }

      return false;
   }

   public static boolean isBlockSurroundBroken(BlockPos pos) {
      if (Venomhack.mc.world.getBlockState(pos).isOf(Blocks.BEDROCK)) {
         return false;
      } else if (!RangeUtils.isWithinRange(RangeUtils.Origin.Feet, Vec3d.ofCenter(pos), (double)Venomhack.mc.interactionManager.getReachDistance())) {
         return false;
      } else {
         return !Venomhack.mc.world.getOtherEntities(null, new Box(pos), Entity::canHit).isEmpty();
      }
   }

   public static boolean obbySurrounded(LivingEntity entity) {
      BlockPos pos = entity.getBlockPos();

      for(Box2 city : CITY) {
         if (!isBlastRes(Venomhack.mc.world.getBlockState(pos.add(city)).getBlock())) {
            return false;
         }
      }

      return true;
   }

   public static boolean isBlastRes(Block block) {
      return block == Blocks.RESPAWN_ANCHOR && getWorld() == World.NETHER || block.getBlastResistance() >= 600.0F;
   }

   public static boolean isSelfTrapBlock(LivingEntity target, BlockPos pos) {
      for(Box2 city : CITY_WITH_BURROW) {
         for(int i = 0; i < 3; ++i) {
            if (pos.equals(target.getBlockPos().add(city.up(i)))) {
               return true;
            }
         }
      }

      return false;
   }

   public static RegistryKey<World> getWorld() {
      return Venomhack.mc.world.getRegistryKey();
   }

   public static BlockState state(BlockPos pos) {
      return Venomhack.mc.world.getBlockState(pos);
   }

   public static BlockState state(Vec3d pos) {
      return Venomhack.mc.world.getBlockState(mutable.set(pos.x, pos.y, pos.z));
   }

   public static BlockState state(int x, int y, int z) {
      return Venomhack.mc.world.getBlockState(mutable.set(x, y, z));
   }

   public static BlockState state(double x, double y, double z) {
      return Venomhack.mc.world.getBlockState(mutable.set(x, y, z));
   }

   public static BlockPos[] playerBlocks(PlayerEntity player) {
      Box box = player.getBoundingBox();
      return new BlockPos[]{
         new BlockPos(box.maxX, box.minY, box.maxZ),
         new BlockPos(box.maxX, box.minY, box.minZ),
         new BlockPos(box.minX, box.minY, box.maxZ),
         new BlockPos(box.minX, box.minY, box.minZ),
         new BlockPos(box.maxX, box.maxY, box.maxZ),
         new BlockPos(box.maxX, box.maxY, box.minZ),
         new BlockPos(box.minX, box.maxY, box.maxZ),
         new BlockPos(box.minX, box.maxY, box.minZ)
      };
   }

   public static double getPlayerSpeed(boolean vertical) {
      double dX = Venomhack.mc.player.getX() - Venomhack.mc.player.prevX;
      double dY = Venomhack.mc.player.getY() - Venomhack.mc.player.prevY;
      double dZ = Venomhack.mc.player.getZ() - Venomhack.mc.player.prevZ;
      double distance = Math.sqrt(dX * dX + dZ * dZ + (vertical ? dY * dY : 0.0));
      return distance * 20.0;
   }

   public static boolean canSeeBlock(BlockPos hitBlock, Vec3d origin) {
      RaycastContext raycastContext = new RaycastContext(
         origin, Vec3d.ofCenter(hitBlock), class_3960.COLLIDER, class_242.NONE, Venomhack.mc.player
      );
      BlockHitResult result = Venomhack.mc.world.raycast(raycastContext);
      return result.getBlockPos().equals(hitBlock);
   }

   public static boolean isGoodForSurround(Item item) {
      return item == Items.OBSIDIAN
         || item == Items.ANCIENT_DEBRIS
         || item == Items.CRYING_OBSIDIAN
         || item == Items.ANVIL
         || item == Items.CHIPPED_ANVIL
         || item == Items.DAMAGED_ANVIL
         || item == Items.ENCHANTING_TABLE
         || item == Items.ENDER_CHEST
         || item == Items.NETHERITE_BLOCK
         || getWorld() == World.NETHER && item == Items.RESPAWN_ANCHOR;
   }

   public static List<BlockPos> getCube(int radius) {
      ArrayList<BlockPos> list = new ArrayList();
      if (Venomhack.mc.world == null) {
         return list;
      } else {
         int x = -radius;

         while(x <= radius) {
            x += Venomhack.mc.player.getBlockX();
            if ((double)x < Venomhack.mc.world.getWorldBorder().getBoundWest() || (double)x >= Venomhack.mc.world.getWorldBorder().getBoundEast()) {
               break;
            }

            int y = -radius;

            while(true) {
               label52: {
                  if (y <= radius) {
                     y += Venomhack.mc.player.getBlockY() + 1;
                     if (y < Venomhack.mc.world.getBottomY()) {
                        break label52;
                     }

                     if (y < Venomhack.mc.world.getTopY()) {
                        int z = -radius;

                        while(true) {
                           if (z > radius) {
                              break label52;
                           }

                           z += Venomhack.mc.player.getBlockZ();
                           if ((double)z < Venomhack.mc.world.getWorldBorder().getBoundNorth()
                              || (double)z >= Venomhack.mc.world.getWorldBorder().getBoundSouth()) {
                              break label52;
                           }

                           list.add(new BlockPos(x, y, z));
                           ++z;
                        }
                     }
                  }

                  ++x;
                  break;
               }

               ++y;
            }
         }

         return list;
      }
   }
}
