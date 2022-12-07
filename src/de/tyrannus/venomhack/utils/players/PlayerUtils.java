package de.tyrannus.venomhack.utils.players;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import de.tyrannus.venomhack.Venomhack;
import de.tyrannus.venomhack.events.TickEvent;
import de.tyrannus.venomhack.utils.RangeUtils;
import de.tyrannus.venomhack.utils.inventory.ItemPos;
import de.tyrannus.venomhack.utils.world.WorldUtils;
import it.unimi.dsi.fastutil.objects.Object2BooleanArrayMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.Hand;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.block.BlockState;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.util.math.Direction.class_2351;
import org.jetbrains.annotations.Nullable;

public class PlayerUtils {
   private static final Object2BooleanArrayMap<BlockPos> collisions = new Object2BooleanArrayMap();

   public static void init() {
      Venomhack.EVENTS.subscribe(PlayerUtils.class);
   }

   public static int getLatency() {
      if (Venomhack.mc.player != null && Venomhack.mc.player.networkHandler != null) {
         PlayerListEntry networkEntry = Venomhack.mc.player.networkHandler.getPlayerListEntry(Venomhack.mc.getSession().getUuid());
         return networkEntry != null && networkEntry.getLatency() != 0 ? networkEntry.getLatency() : 100;
      } else {
         return 100;
      }
   }

   public static boolean shouldPause(boolean pauseEat) {
      if (!pauseEat) {
         return false;
      } else {
         return Venomhack.mc.player.isUsingItem()
            && (Venomhack.mc.player.getMainHandStack().isFood() || Venomhack.mc.player.getOffHandStack().isFood());
      }
   }

   @EventHandler
   private void onTick(TickEvent.Post event) {
      collisions.clear();
   }

   public static float getTotalHealth() {
      return getTotalHealth(Venomhack.mc.player);
   }

   public static float getTotalHealth(LivingEntity entity) {
      return entity.getHealth() + entity.getAbsorptionAmount();
   }

   public static Vec3d eyePos() {
      return eyePos(Venomhack.mc.player);
   }

   public static Vec3d eyePos(LivingEntity entity) {
      return entity.getPos().add(0.0, (double)entity.getEyeHeight(entity.getPose()), 0.0);
   }

   public static void swing(boolean clientSide, Hand hand) {
      if (clientSide) {
         Venomhack.mc.player.swingHand(hand);
      } else {
         Venomhack.mc.player.networkHandler.sendPacket(new HandSwingC2SPacket(hand));
      }
   }

   public static GameMode getGameMode(PlayerEntity player) {
      PlayerListEntry playerListEntry = Venomhack.mc.getNetworkHandler().getPlayerListEntry(player.getUuid());
      return playerListEntry == null ? GameMode.SPECTATOR : playerListEntry.getGameMode();
   }

   public static Vec3d predictPos(PlayerEntity player, int ticks, int antiStepOffset) {
      boolean hasStepped = false;
      Vec3d pos = player.getPos();
      Vec3d v = smartVelocity(player);
      Vec3d nextPos = pos;
      if (WorldUtils.isSurrounded(player, true, true) && v.y <= 0.0) {
         return pos;
      } else {
         for(int i = 0; i <= ticks; ++i) {
            BlockPos newPos = new BlockPos(nextPos.add(v));
            if (collisionCheck(newPos) || Math.ceil((double)player.getEyeHeight(player.getPose())) != 1.0 && collisionCheck(newPos.up())) {
               if (antiStepOffset >= 1
                  && !hasStepped
                  && collisionCheck(newPos)
                  && !collisionCheck(newPos.up())
                  && !collisionCheck(newPos.up(2))) {
                  nextPos = nextPos.add(v).add(0.0, 1.0, 0.0);
                  hasStepped = true;
               } else if (antiStepOffset >= 2
                  && !hasStepped
                  && collisionCheck(newPos.up())
                  && !collisionCheck(newPos.up(2))
                  && !collisionCheck(newPos.up(3))) {
                  nextPos = nextPos.add(v).add(0.0, 2.0, 0.0);
                  hasStepped = true;
               }
            } else {
               nextPos = nextPos.add(v);
            }
         }

         return nextPos;
      }
   }

   public static Box predictBox(PlayerEntity player, int ticks, int antiStepOffset) {
      Vec3d nextPos = predictPos(player, ticks, antiStepOffset);
      Box oldBox = player.getBoundingBox();
      double dx = oldBox.getXLength() * 0.5;
      double dy = oldBox.getYLength();
      double dz = oldBox.getZLength() * 0.5;
      return new Box(
         nextPos.x - dx, nextPos.y, nextPos.z - dz, nextPos.x + dx, nextPos.y + dy, nextPos.z + dz
      );
   }

   private static boolean collisionCheck(BlockPos pos) {
      if (collisions.containsKey(pos)) {
         return collisions.getBoolean(pos);
      } else {
         boolean collides = Venomhack.mc.world.getBlockState(pos).getBlock().collidable;
         collisions.put(pos, collides);
         return collides;
      }
   }

   public static boolean canPlace(ItemPlacementContext context, BlockItem blockItem) {
      if (!context.canPlace()) {
         return false;
      } else {
         ItemPlacementContext itemPlacementContext = blockItem.getPlacementContext(context);
         if (itemPlacementContext == null) {
            return false;
         } else {
            BlockState blockState = blockItem.getPlacementState(itemPlacementContext);
            return blockState == null ? false : blockItem.canPlace(itemPlacementContext, blockState);
         }
      }
   }

   public static void collectTargets(
      List<PlayerEntity> targets, List<PlayerEntity> friends, int targetRange, int maxTargets, boolean ignoreNakeds, boolean onlyHoled, boolean ignoreTerrain
   ) {
      List<LivingEntity> livings = new ArrayList();
      Object2BooleanMap<EntityType<?>> entities = new Object2BooleanOpenHashMap();
      entities.put(EntityType.PLAYER, true);
      collectTargets(livings, friends, targetRange, maxTargets, ignoreNakeds, onlyHoled, ignoreTerrain, entities);
      targets.clear();

      for(LivingEntity living : livings) {
         targets.add((PlayerEntity)living);
      }
   }

   public static void collectTargets(
      List<LivingEntity> targets,
      @Nullable List<PlayerEntity> friends,
      int targetRange,
      int maxTargets,
      boolean ignoreNakeds,
      boolean onlyHoled,
      boolean ignoreTerrain,
      Object2BooleanMap<EntityType<?>> entities
   ) {
      targets.clear();
      if (friends != null) {
         friends.clear();
      }

      for(Entity entity : Venomhack.mc.world.getEntities()) {
         if (entity instanceof LivingEntity living
            && !living.isDead()
            && entity != Venomhack.mc.player
            && RangeUtils.isWithinRange(RangeUtils.Origin.Feet, entity, (double)targetRange)) {
            if (entity instanceof PlayerEntity player) {
               if (getGameMode(player) != GameMode.CREATIVE) {
                  if (!Friends.isFriend(player)) {
                     if (entities.getBoolean(EntityType.PLAYER)
                        && (!ignoreNakeds || !isNaked(player))
                        && (!onlyHoled || WorldUtils.isSurrounded(player, true, ignoreTerrain))) {
                        targets.add(player);
                     }
                  } else if (friends != null) {
                     friends.add(player);
                  }
               }
            } else if (entities.getBoolean(entity.getType())) {
               targets.add(living);
            }
         }
      }

      targets.sort(Comparator.comparingDouble(e -> e.squaredDistanceTo(Venomhack.mc.player)));

      while(targets.size() > maxTargets) {
         targets.remove(targets.size() - 1);
      }
   }

   public static boolean isNaked(PlayerEntity player) {
      return (player.getInventory().armor == null || player.getInventory().armor.isEmpty())
         && (player.getOffHandStack() == null || player.getOffHandStack().isEmpty());
   }

   public static Vec3d smartVelocity(Entity entity) {
      return new Vec3d(entity.getX() - entity.prevX, entity.getY() - entity.prevY, entity.getZ() - entity.prevZ);
   }

   public static Vec3d smartPredictedPosition(Entity entity, Vec3d movement) {
      Box box = entity.getBoundingBox();
      List<VoxelShape> list = entity.world.getEntityCollisions(entity, box.stretch(movement));
      Vec3d vec3d = movement.lengthSquared() == 0.0 ? movement : adjustMovementForCollisions(entity, movement, box, Venomhack.mc.world, list);
      boolean bl = movement.x != vec3d.x;
      boolean bl2 = movement.y != vec3d.y;
      boolean bl3 = movement.z != vec3d.z;
      boolean bl4 = entity.isOnGround() || bl2 && movement.y < 0.0;
      if (entity.stepHeight > 0.0F && bl4 && (bl || bl3)) {
         Vec3d vec3d2 = adjustMovementForCollisions(
            entity, new Vec3d(movement.x, (double)entity.stepHeight, movement.z), box, Venomhack.mc.world, list
         );
         Vec3d vec3d3 = adjustMovementForCollisions(
            entity,
            new Vec3d(0.0, (double)entity.stepHeight, 0.0),
            box.stretch(movement.x, 0.0, movement.z),
            Venomhack.mc.world,
            list
         );
         if (vec3d3.y < (double)entity.stepHeight) {
            Vec3d vec3d4 = adjustMovementForCollisions(
                  entity, new Vec3d(movement.x, 0.0, movement.z), box.offset(vec3d3), Venomhack.mc.world, list
               )
               .add(vec3d3);
            if (vec3d4.horizontalLengthSquared() > vec3d2.horizontalLengthSquared()) {
               vec3d2 = vec3d4;
            }
         }

         if (vec3d2.horizontalLengthSquared() > vec3d.horizontalLengthSquared()) {
            return vec3d2.add(
               adjustMovementForCollisions(
                  entity, new Vec3d(0.0, -vec3d2.y + movement.y, 0.0), box.offset(vec3d2), Venomhack.mc.world, list
               )
            );
         }
      }

      return vec3d;
   }

   private static Vec3d adjustMovementForCollisions(
      @Nullable Entity entity, Vec3d movement, Box entityBoundingBox, World world, List<VoxelShape> collisions
   ) {
      Builder<VoxelShape> builder = ImmutableList.builderWithExpectedSize(collisions.size() + 1);
      if (!collisions.isEmpty()) {
         builder.addAll(collisions);
      }

      WorldBorder worldBorder = world.getWorldBorder();
      boolean bl = entity != null && worldBorder.canCollide(entity, entityBoundingBox.stretch(movement));
      if (bl) {
         builder.add(worldBorder.asVoxelShape());
      }

      builder.addAll(world.getBlockCollisions(entity, entityBoundingBox.stretch(movement)));
      return adjustMovementForCollisions(movement, entityBoundingBox, builder.build());
   }

   private static Vec3d adjustMovementForCollisions(Vec3d movement, Box entityBoundingBox, List<VoxelShape> collisions) {
      double d = movement.x;
      double e = movement.y;
      double f = movement.z;
      if (e != 0.0) {
         e = VoxelShapes.calculateMaxOffset(class_2351.Y, entityBoundingBox, collisions, e);
         if (e != 0.0) {
            entityBoundingBox = entityBoundingBox.offset(0.0, e, 0.0);
         }
      }

      boolean bl = Math.abs(d) < Math.abs(f);
      if (bl && f != 0.0) {
         f = VoxelShapes.calculateMaxOffset(class_2351.Z, entityBoundingBox, collisions, f);
         if (f != 0.0) {
            entityBoundingBox = entityBoundingBox.offset(0.0, 0.0, f);
         }
      }

      if (d != 0.0) {
         d = VoxelShapes.calculateMaxOffset(class_2351.X, entityBoundingBox, collisions, d);
         if (!bl && d != 0.0) {
            entityBoundingBox = entityBoundingBox.offset(d, 0.0, 0.0);
         }
      }

      if (!bl && f != 0.0) {
         f = VoxelShapes.calculateMaxOffset(class_2351.Z, entityBoundingBox, collisions, f);
      }

      return new Vec3d(d, e, f);
   }

   public static ItemStack getStackFromResult(ItemPos result) {
      if (Venomhack.mc.player != null && !Venomhack.mc.player.isDead()) {
         return result.isOffhand() ? Venomhack.mc.player.getOffHandStack() : Venomhack.mc.player.getInventory().getStack(result.slot());
      } else {
         return Items.AIR.getDefaultStack();
      }
   }

   public static Item getItemFromResult(ItemPos result) {
      return getStackFromResult(result).getItem();
   }
}
