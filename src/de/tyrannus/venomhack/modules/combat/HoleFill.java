package de.tyrannus.venomhack.modules.combat;

import de.tyrannus.venomhack.events.TickEvent;
import de.tyrannus.venomhack.modules.Module;
import de.tyrannus.venomhack.modules.combat.autocrystal.Origin;
import de.tyrannus.venomhack.settings.Keybind;
import de.tyrannus.venomhack.settings.Setting;
import de.tyrannus.venomhack.utils.AntiCheatHelper;
import de.tyrannus.venomhack.utils.RangeUtils;
import de.tyrannus.venomhack.utils.inventory.InvUtils;
import de.tyrannus.venomhack.utils.inventory.ItemPos;
import de.tyrannus.venomhack.utils.players.Friends;
import de.tyrannus.venomhack.utils.players.PlayerUtils;
import de.tyrannus.venomhack.utils.render.RenderBlock;
import de.tyrannus.venomhack.utils.world.BlockUtils;
import de.tyrannus.venomhack.utils.world.WorldUtils;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import org.jetbrains.annotations.Nullable;

public class HoleFill extends Module {
   private final Setting<Origin> placeOrigin = this.setting("place-origin", "How to calculate ranges.", Origin.NCP);
   private final Setting<Float> placeRange = this.setting("place-range", "How far you can place blocks.", Float.valueOf(5.2F), 0.0F, 6.0F);
   private final Setting<Float> wallsRange = this.setting("walls-range", "How far you can place through walls.", Float.valueOf(5.2F), 0.0F, 6.0F);
   private final Setting<Boolean> eatPause = this.setting(
      "pause-while-eating", "Will only attempt to fill holes while you aren't eating.", Boolean.valueOf(true)
   );
   private final Setting<Boolean> doubles = this.setting("doubles", "Fills double holes.", Boolean.valueOf(true));
   private final Setting<Integer> placeDelay = this.setting("delay", "The delay between placements in ticks.", Integer.valueOf(1));
   private final Setting<Integer> blocksPerTick = this.setting("blocks-per-tick", "How many blocks to place per tick.", Integer.valueOf(5));
   private final Setting<Boolean> strictDirections = this.setting("strict-directions", "Places only on visible sides.", Boolean.valueOf(false));
   private final Setting<Boolean> smart = this.setting("smart", "Only fills holes within a certain range of a target.", Boolean.valueOf(true));
   private final Setting<Float> smartRadiusH = this.setting(
      "horizontal-radius-smart", "Horizontal radius from a target in which to fill holes.", Float.valueOf(2.0F), this.smart::get, 0.0F, 5.0F
   );
   private final Setting<Float> smartRadiusV = this.setting(
      "vertical-radius-smart", "Vertical radius from a target in which to fill holes.", Float.valueOf(4.0F), this.smart::get, 0.0F, 5.0F
   );
   private final Setting<Boolean> onlyMoving = this.setting(
      "only-moving", "Will only fill holes around moving targets.", Boolean.valueOf(true), this.smart::get
   );
   private final Setting<Keybind> forceFill = this.setting("force-fill", "Will fill all holes around you when pressed.", Keybind.UNBOUND, this.smart::get);
   private final Setting<Boolean> predict = this.setting(
      "predict-movement", "Will add the target's velocity to its position times the amount of predict ticks.", Boolean.valueOf(false), this.smart::get
   );
   private final Setting<Integer> predictTicks = this.setting(
      "predict-ticks", "How many ticks to predict the movement for.", Integer.valueOf(2), () -> this.smart.get() && this.predict.get()
   );
   private final Setting<Boolean> onlySafe = this.setting(
      "only-when-safe", "Will only fill holes when you are surrounded or burrowed.", Boolean.valueOf(false)
   );
   private final Setting<Float> safeRadiusH = this.setting(
      "min-horizontal-distance", "Horizontal radius from yourself in which to fill holes.", Float.valueOf(0.0F), 0.0F, 5.0F
   );
   private final Setting<Float> safeRadiusV = this.setting(
      "min-vertical-distance", "Vertical radius from yourself in which to fill holes.", Float.valueOf(0.0F), 0.0F, 5.0F
   );
   private final Setting<Boolean> swing = this.setting("swing", "Renders your hand swing client-side.", Boolean.valueOf(true));
   private final Set<BlockPos> holes = new HashSet();
   private final List<RenderBlock> renderBlocks = new ArrayList<>();
   private int delayLeft;

   public HoleFill() {
      super(Module.Categories.COMBAT, "hole-fill", "Fills holes with specified blocks.");
   }

   @Override
   public void onEnable() {
      this.delayLeft = 0;
      this.renderBlocks.clear();
      this.holes.clear();
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      RenderBlock.tick(this.renderBlocks);
      if (this.delayLeft > 0) {
         --this.delayLeft;
      } else if (!PlayerUtils.shouldPause(this.eatPause.get())) {
         if (!this.onlySafe.get() || !WorldUtils.notSafe(mc.player)) {
            ItemPos result = InvUtils.findInHotbar(Items.OBSIDIAN, Items.COBWEB);
            if (result.found()) {
               this.holes.clear();
               ((Stream)WorldUtils.getCube((int)Math.ceil((double)(this.placeRange.get() + 1.0F))).stream().parallel())
                  .forEach(
                     blockPos -> {
                        int count = 0;
                        Direction doubleHoleOffset = null;
      
                        for(Direction direction : Direction.values()) {
                           if (direction != Direction.UP) {
                              BlockState state = mc.world.getBlockState(blockPos.offset(direction));
                              if (state.getBlock().getBlastResistance() >= 600.0F) {
                                 ++count;
                              } else {
                                 if (direction == Direction.DOWN) {
                                    return;
                                 }
      
                                 if (this.doubles.get()
                                    && doubleHoleOffset == null
                                    && this.validHole(blockPos.offset(direction), null, this.getBlock(result))) {
                                    for(Direction dir : Direction.values()) {
                                       if (dir != direction.getOpposite() && dir != Direction.UP) {
                                          BlockState blockState1 = mc.world.getBlockState(blockPos.offset(direction).offset(dir));
                                          if (!(blockState1.getBlock().getBlastResistance() >= 600.0F)) {
                                             return;
                                          }
      
                                          ++count;
                                       }
                                    }
      
                                    doubleHoleOffset = direction;
                                 }
                              }
                           }
                        }
      
                        if (this.validHole(blockPos, doubleHoleOffset, this.getBlock(result))) {
                           if (count == 5 && doubleHoleOffset == null) {
                              this.holes.add(new BlockPos(blockPos));
                           } else if (count == 8 && doubleHoleOffset != null) {
                              this.holes.add(blockPos);
                              this.holes.add(blockPos.offset(doubleHoleOffset));
                           }
                        }
                     }
                  );
               int blocksPlaced = 0;

               for(BlockPos holePos : this.holes) {
                  if (BlockUtils.placeBlock(result, holePos, false, 30, false, false, this.swing.get(), this.strictDirections.get())) {
                     this.delayLeft = this.placeDelay.get();
                     if (++blocksPlaced >= this.blocksPerTick.get()) {
                        break;
                     }
                  }
               }
            }
         }
      }
   }

   private boolean validHole(BlockPos pos, @Nullable Direction doubleHoleOffset, Block block) {
      if (mc.world.getBlockState(pos).getBlock().collidable) {
         return false;
      } else if (!mc.world.getBlockState(pos.up()).getBlock().collidable
            && !mc.world.getBlockState(pos.up(2)).getBlock().collidable
         || doubleHoleOffset != null
            && !mc.world.getBlockState(pos.offset(doubleHoleOffset).up()).getBlock().collidable
            && !mc.world.getBlockState(pos.offset(doubleHoleOffset).up(2)).getBlock().collidable) {
         Vec3d holePos = Vec3d.ofCenter(pos);
         if (AntiCheatHelper.outOfPlaceRange(pos, this.placeOrigin.get(), (double)this.placeRange.get().floatValue())) {
            return false;
         } else if (AntiCheatHelper.outOfPlaceRange(pos, this.placeOrigin.get(), (double)this.wallsRange.get().floatValue())
            && !WorldUtils.canSeeBlock(pos, PlayerUtils.eyePos())) {
            return false;
         } else {
            Vec3d holePos2 = null;
            if (doubleHoleOffset != null) {
               BlockPos offsetBlock = pos.offset(doubleHoleOffset);
               holePos2 = Vec3d.ofCenter(offsetBlock);
               if (AntiCheatHelper.outOfPlaceRange(offsetBlock, this.placeOrigin.get(), (double)this.placeRange.get().floatValue())) {
                  return false;
               }

               if (AntiCheatHelper.outOfPlaceRange(offsetBlock, this.placeOrigin.get(), (double)this.wallsRange.get().floatValue())
                  && !WorldUtils.canSeeBlock(offsetBlock, PlayerUtils.eyePos())) {
                  return false;
               }
            }

            if (WorldUtils.notSafe(mc.player)) {
               if (Math.sqrt(holePos.squaredDistanceTo(mc.player.getX(), holePos.y, mc.player.getZ()))
                  < (double)this.safeRadiusH.get().floatValue()) {
                  return false;
               }

               if (Math.sqrt(holePos.squaredDistanceTo(holePos.x, mc.player.getY(), holePos.z))
                  < (double)this.safeRadiusV.get().floatValue()) {
                  return false;
               }

               if (holePos2 != null) {
                  if (Math.sqrt(holePos2.squaredDistanceTo(mc.player.getX(), holePos2.y, mc.player.getZ()))
                     < (double)this.safeRadiusH.get().floatValue()) {
                     return false;
                  }

                  if (Math.sqrt(holePos2.squaredDistanceTo(holePos2.x, mc.player.getY(), holePos2.z))
                     < (double)this.safeRadiusV.get().floatValue()) {
                     return false;
                  }
               }
            }

            if (!mc.world.canPlace(block.getDefaultState(), pos, ShapeContext.absent())) {
               return false;
            } else if (this.smart.get() && !this.forceFill.get().isPressed()) {
               for(Entity entity : mc.world.getEntities()) {
                  if (entity.isAlive()
                     && entity != mc.player
                     && entity instanceof PlayerEntity player
                     && RangeUtils.isWithinRange(RangeUtils.Origin.Feet, entity, Math.ceil((double)(this.placeRange.get() + this.smartRadiusH.get())) + 1.0)
                     && Friends.isFriend(player)
                     && (!this.onlyMoving.get() || !WorldUtils.isSurrounded(player, true, true) && PlayerUtils.smartVelocity(player).length() != 0.0)) {
                     Vec3d entityPos = entity.getPos();
                     if (this.predict.get()) {
                        entityPos = PlayerUtils.predictPos(player, this.predictTicks.get(), 0);
                     }

                     if (entity.getBlockY() > pos.getY()
                        && !(
                           Math.sqrt(holePos.squaredDistanceTo(holePos.x, entityPos.y - 0.5, holePos.z))
                              > (double)this.smartRadiusV.get().floatValue()
                        )
                        && (
                           !(
                                 Math.sqrt(holePos.squaredDistanceTo(entityPos.x, holePos.y, entityPos.z))
                                    > (double)this.smartRadiusH.get().floatValue()
                              )
                              || holePos2 != null
                                 && !(
                                    Math.sqrt(holePos2.squaredDistanceTo(entityPos.x, holePos2.y, entityPos.z))
                                       > (double)this.smartRadiusH.get().floatValue()
                                 )
                        )) {
                        return true;
                     }
                  }
               }

               return false;
            } else {
               return true;
            }
         }
      } else {
         return false;
      }
   }

   private Block getBlock(ItemPos result) {
      return Block.getBlockFromItem(result.item());
   }
}
