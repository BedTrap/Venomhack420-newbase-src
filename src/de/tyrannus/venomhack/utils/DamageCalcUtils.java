package de.tyrannus.venomhack.utils;

import de.tyrannus.venomhack.Venomhack;
import de.tyrannus.venomhack.events.OpenScreenEvent;
import de.tyrannus.venomhack.utils.players.PlayerUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.DamageUtil;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.world.BlockView;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.GameMode;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.RaycastContext;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.world.explosion.Explosion.class_4179;
import net.minecraft.util.hit.HitResult.class_240;
import net.minecraft.world.RaycastContext.class_242;
import net.minecraft.world.RaycastContext.class_3960;

public class DamageCalcUtils {
   public static Explosion explosion;

   public static float explosionDamage(LivingEntity target, Vec3d pos, int explosionPower) {
      return explosionDamage(target, pos, false, 0, 0, false, false, true, explosionPower);
   }

   public static float explosionDamage(LivingEntity target, Vec3d pos, boolean ignoreTerrain, boolean predict, boolean withExposure, int explosionPower) {
      return explosionDamage(target, pos, predict, 1, 1, ignoreTerrain, false, withExposure, explosionPower);
   }

   public static float explosionDamage(
      LivingEntity target,
      Vec3d pos,
      boolean predict,
      int predictTicks,
      int antiStepOffset,
      boolean ignoreTerrain,
      boolean placing,
      boolean withExposure,
      int explosionPower
   ) {
      Vec3d targetPos = target.getPos();
      if (target instanceof PlayerEntity player) {
         if (PlayerUtils.getGameMode(player) == GameMode.CREATIVE) {
            return 0.0F;
         }

         if (predict) {
            targetPos = PlayerUtils.predictPos(player, predictTicks, antiStepOffset);
         }
      }

      double modDistance = targetPos.distanceTo(pos);
      if (modDistance > (double)(explosionPower * 2)) {
         return 0.0F;
      } else {
         float exposure = 1.0F;
         if (withExposure) {
            exposure = getExposure(pos, target, predict, predictTicks, antiStepOffset, ignoreTerrain, placing);
         }

         float impact = (float)(1.0 - 0.5 * modDistance / (double)explosionPower) * exposure;
         float damage = (impact * impact + impact) * (float)explosionPower * 7.0F + 1.0F;
         switch(Venomhack.mc.world.getDifficulty()) {
            case PEACEFUL:
               return 0.0F;
            case EASY:
               damage = damage < 2.0F ? damage : damage * 0.5F + 1.0F;
               break;
            case HARD:
               damage = (float)((double)damage * 1.5);
         }

         damage = DamageUtil.getDamageLeft(damage, (float)target.getArmor(), (float)target.getAttributeInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS).getValue());
         if (target.hasStatusEffect(StatusEffects.RESISTANCE)) {
            damage = (float)((double)damage * (1.0 - (double)(target.getStatusEffect(StatusEffects.RESISTANCE).getAmplifier() + 1) * 0.2));
         }

         int i = EnchantmentHelper.getProtectionAmount(target.getArmorItems(), DamageSource.explosion(explosion));
         if (i > 0) {
            damage = DamageUtil.getInflictedDamage(damage, (float)i);
         }

         return damage < 0.0F ? 0.0F : damage;
      }
   }

   public static float getExposure(Vec3d source, Entity entity, boolean predict, boolean ignoreTerrain) {
      return getExposure(source, entity, predict, ignoreTerrain, false);
   }

   private static float getExposure(Vec3d source, Entity entity, boolean predict, boolean ignoreTerrain, boolean placing) {
      return getExposure(source, entity, predict, 1, 1, ignoreTerrain, placing);
   }

   private static float getExposure(
      Vec3d source, Entity entity, boolean predict, int predictTicks, int antiStepOffset, boolean ignoreTerrain, boolean placing
   ) {
      Box box = entity.getBoundingBox();
      if (predict && entity instanceof PlayerEntity player) {
         box = PlayerUtils.predictBox(player, predictTicks, antiStepOffset);
      }

      double xWidthReciprocal = 1.0 / ((box.maxX - box.minX) * 2.0 + 1.0);
      double yHeightReciprocal = 1.0 / ((box.maxY - box.minY) * 2.0 + 1.0);
      double zWidthReciprocal = 1.0 / ((box.maxZ - box.minZ) * 2.0 + 1.0);
      if (!(xWidthReciprocal < 0.0) && !(yHeightReciprocal < 0.0) && !(zWidthReciprocal < 0.0)) {
         double g = (1.0 - Math.floor(1.0 / xWidthReciprocal) * xWidthReciprocal) * 0.5;
         double h = (1.0 - Math.floor(1.0 / zWidthReciprocal) * zWidthReciprocal) * 0.5;
         float nonSolid = 0.0F;
         int total = 0;

         for(float x = 0.0F; x <= 1.0F; x = (float)((double)x + xWidthReciprocal)) {
            for(float y = 0.0F; y <= 1.0F; y = (float)((double)y + yHeightReciprocal)) {
               for(float z = 0.0F; z <= 1.0F; z = (float)((double)z + zWidthReciprocal)) {
                  double lerpX = MathHelper.lerp((double)x, box.minX, box.maxX);
                  double lerpY = MathHelper.lerp((double)y, box.minY, box.maxY);
                  double lerpZ = MathHelper.lerp((double)z, box.minZ, box.maxZ);
                  if (raycast(
                           new RaycastContext(new Vec3d(lerpX + g, lerpY, lerpZ + h), source, class_3960.COLLIDER, class_242.NONE, entity),
                           ignoreTerrain,
                           placing
                        )
                        .getType()
                     == class_240.MISS) {
                     ++nonSolid;
                  }

                  ++total;
               }
            }
         }

         return nonSolid / (float)total;
      } else {
         return 0.0F;
      }
   }

   private static BlockHitResult raycast(RaycastContext context, boolean ignoreTerrain, boolean placing) {
      return (BlockHitResult)BlockView.raycast(
         context.getStart(),
         context.getEnd(),
         context,
         (raycastContext, blockPos) -> {
            BlockState blockState = Venomhack.mc.world.getBlockState(blockPos);
            Vec3d vec3d = raycastContext.getStart();
            Vec3d vec3d2 = raycastContext.getEnd();
            BlockPos posEnd = new BlockPos(vec3d2);
            if (blockPos.equals(posEnd.down())) {
               if (placing) {
                  blockState = Blocks.OBSIDIAN.getDefaultState();
               }
            } else if (blockPos.equals(posEnd)) {
               if (blockState.isOf(Blocks.RESPAWN_ANCHOR) || blockState.getBlock() instanceof BedBlock) {
                  blockState = Blocks.AIR.getDefaultState();
               }
            } else if (blockState.getBlock() instanceof BedBlock
               && Venomhack.mc.world.getBlockState(posEnd).getBlock() instanceof BedBlock
               && blockPos.offset(BedBlock.getOppositePartDirection(blockState)).equals(posEnd)) {
               blockState = Blocks.AIR.getDefaultState();
            }
   
            if (blockState.getBlock().getBlastResistance() < 600.0F && ignoreTerrain) {
               blockState = Blocks.AIR.getDefaultState();
            }
   
            VoxelShape voxelShape = raycastContext.getBlockShape(blockState, Venomhack.mc.world, blockPos);
            BlockHitResult blockHitResult = Venomhack.mc.world.raycastBlock(vec3d, vec3d2, blockPos, voxelShape, blockState);
            VoxelShape voxelShape2 = VoxelShapes.empty();
            BlockHitResult blockHitResult2 = voxelShape2.raycast(vec3d, vec3d2, blockPos);
            double d = blockHitResult == null ? Double.MAX_VALUE : raycastContext.getStart().squaredDistanceTo(blockHitResult.getPos());
            double e = blockHitResult2 == null ? Double.MAX_VALUE : raycastContext.getStart().squaredDistanceTo(blockHitResult2.getPos());
            return d <= e ? blockHitResult : blockHitResult2;
         },
         raycastContext -> {
            Vec3d vec3d = raycastContext.getStart().subtract(raycastContext.getEnd());
            return BlockHitResult.createMissed(
               raycastContext.getEnd(),
               Direction.getFacing(vec3d.x, vec3d.y, vec3d.z),
               new BlockPos(raycastContext.getEnd())
            );
         }
      );
   }

   public static void init() {
      Venomhack.EVENTS.subscribe(DamageCalcUtils.class);
   }

   @EventHandler
   private static void onGameJoined(OpenScreenEvent event) {
      if (event.screen == null && Venomhack.mc.world != null) {
         explosion = new Explosion(Venomhack.mc.world, null, 0.0, 0.0, 0.0, 6.0F, false, class_4179.DESTROY);
      }
   }
}
