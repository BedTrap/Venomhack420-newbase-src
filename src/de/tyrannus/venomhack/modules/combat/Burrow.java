package de.tyrannus.venomhack.modules.combat;

import de.tyrannus.venomhack.Venomhack;
import de.tyrannus.venomhack.events.PacketEvent;
import de.tyrannus.venomhack.events.PlayerMoveEvent;
import de.tyrannus.venomhack.events.TickEvent;
import de.tyrannus.venomhack.modules.Module;
import de.tyrannus.venomhack.settings.Setting;
import de.tyrannus.venomhack.utils.RangeUtils;
import de.tyrannus.venomhack.utils.inventory.InvUtils;
import de.tyrannus.venomhack.utils.inventory.ItemPos;
import de.tyrannus.venomhack.utils.players.Friends;
import de.tyrannus.venomhack.utils.players.PlayerUtils;
import de.tyrannus.venomhack.utils.world.BlockUtils;
import de.tyrannus.venomhack.utils.world.WorldUtils;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.Hand;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Items;
import net.minecraft.world.World;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Blocks;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.text.Text;
import net.minecraft.network.Packet;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.block.BlockState;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.util.math.BlockPos.class_2339;
import net.minecraft.util.math.Direction.class_2351;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.class_2829;
import org.jetbrains.annotations.Nullable;

public class Burrow extends Module {
   private final Setting<Burrow.SurroundBlocks> block = this.setting("block", "The block to use for Burrow.", Burrow.SurroundBlocks.OBSIDIAN);
   private final Setting<Integer> minRubberbandHeight = this.setting(
      "min-rub-height", "Maximum blocks to teleport up or down to cause a rubberband.", Integer.valueOf(4), -20.0F, 20.0F
   );
   private final Setting<Integer> maxRubberbandHeight = this.setting(
      "max-rub-height", "Minimum blocks to teleport up or down to cause a rubberband.", Integer.valueOf(8), -20.0F, 20.0F
   );
   private final Setting<Integer> minBlacklist = this.setting("min-blacklist", "Start of the blacklisted area.", Integer.valueOf(-2), -10.0F, 10.0F);
   private final Setting<Integer> maxBlacklist = this.setting("max-blacklist", "End of the blacklisted area.", Integer.valueOf(2), -10.0F, 10.0F);
   private final Setting<Boolean> attackCrystals = this.setting("attack-crystals", "Whether to attack crystals that are in the way.", Boolean.valueOf(true));
   public final Setting<Boolean> phoenixMode = this.setting(
      "phoenix-mode", "Allows you to burrow with a block above your head. Works only on pa.", Boolean.valueOf(false)
   );
   private final Setting<Boolean> center = this.setting("center", "Centers you to the middle of the block before burrowing.", Boolean.valueOf(true));
   private final Setting<Boolean> hardSnap = this.setting(
      "hard-Center", "Will align you at the exact center of your hole.", Boolean.valueOf(false), this.center::get
   );
   private final Setting<Boolean> strictDirections = this.setting("strict-directions", "Places only on visible sides.", Boolean.valueOf(false));
   private final Setting<Boolean> swing = this.setting("swing", "Whether to swing your hand client side or not.", Boolean.valueOf(false));
   private final Setting<Boolean> airPlace = this.setting("air-place", "Whether to place in midair or not.", Boolean.valueOf(true));
   public final Setting<Boolean> autoTrap = this.setting(
      "auto-burrow-trap", "Automatically activates burrow if someone is about to jump into your hole.", Boolean.valueOf(false), this::handleListener
   );
   public final Setting<Boolean> autoReburrow = this.setting(
      "reburrow", "Automatically burrows you again after someone mined your burrow block.", Boolean.valueOf(false), this::handleListener
   );
   public final Setting<Boolean> pauseEating = this.setting(
      "eat-pause", "Will not automatically burrow you while you are eating.", Boolean.valueOf(true), () -> this.autoReburrow.get() || this.autoTrap.get()
   );
   private final List<Double> packetList = new ArrayList<>(6);
   private final class_2339 mutable = new class_2339();
   private Vec3d playerPos;
   private ItemPos result;
   private BlockHitResult hitResult;
   private BlockPos playerBlock;
   private BlockState state;
   @Nullable
   private Entity entity;
   private float serverPitch;
   private static final int CHAT_ID = 972483264;
   private final Burrow.StaticListener BURROW_LISTENER = new Burrow.StaticListener();

   public Burrow() {
      super(Module.Categories.COMBAT, "burrow", "Places a block inside your legs.");
      this.handleListener(true);
   }

   @Override
   public void onEnable() {
      if (mc.player != null) {
         this.serverPitch = mc.player.getPitch();
      }

      if (!mc.player.isOnGround()) {
         this.toggleWithError(972483264, Text.translatable("burrow.ground"));
      } else {
         ItemPos result = this.block.get().getBlock();
         if ((result.isHotbar() || result.isOffhand()) && result.found()) {
            this.playerPos = mc.player.getPos();
            if (this.center.get()) {
               this.playerPos = BlockUtils.getCenterPos(this.hardSnap.get());
            }

            boolean onEchest = this.playerPos.y != Math.ceil(this.playerPos.y);
            BlockPos playerBlock = new BlockPos(
               this.playerPos.x, onEchest ? Math.ceil(this.playerPos.y) : this.playerPos.y, this.playerPos.z
            );
            if (BlockUtils.invalidPos(playerBlock)) {
               this.toggleWithError(972483264, Text.translatable("burrow.world"));
            } else {
               BlockHitResult hitResult = BlockUtils.getPlaceResult(playerBlock, this.airPlace.get(), this.strictDirections.get());
               if (hitResult == null) {
                  this.toggleWithError(972483264, Text.translatable("burrow.place"));
               } else {
                  float eyeHeight = mc.player.getEyeHeight(mc.player.getPose());
                  if (!this.phoenixMode.get() && this.collides(Math.ceil((double)eyeHeight))) {
                     this.toggleWithError(972483264, Text.translatable("burrow.headroom"));
                  } else {
                     BlockState state = mc.world.getBlockState(playerBlock);
                     if (!state.getMaterial().isReplaceable()
                        && (!(state.getBlock() instanceof BedBlock) || mc.world.getRegistryKey() == World.OVERWORLD)
                        && state.getBlock().getHardness() > 0.0F
                        && this.collides(onEchest ? 1.0 : 0.0)) {
                        this.toggleWithError(972483264, Text.translatable("burrow.burrowed"));
                     } else {
                        VoxelShape placeShape = ((BlockItem)PlayerUtils.getItemFromResult(result))
                           .getBlock()
                           .getDefaultState()
                           .getCollisionShape(mc.world, playerBlock);
                        Entity entity = this.getEntityInDaWay(placeShape, playerBlock);
                        if (entity == null || this.attackCrystals.get() && entity instanceof EndCrystalEntity) {
                           this.packetList.clear();

                           for(int rubberHeight = this.minRubberbandHeight.get(); rubberHeight <= this.maxRubberbandHeight.get(); ++rubberHeight) {
                              if ((rubberHeight < this.minBlacklist.get() || rubberHeight > this.maxBlacklist.get())
                                 && (rubberHeight <= -3 || rubberHeight >= 2)
                                 && !this.collides((double)rubberHeight)) {
                                 if (!this.collides((double)rubberHeight + Math.floor((double)eyeHeight))) {
                                    if (this.phoenixMode.get() && this.collides(Math.ceil((double)eyeHeight))) {
                                       boolean cantJump = true;

                                       for(double i = -4.0; i < 5.0; ++i) {
                                          if (i != 0.0 && i != -1.0 && !this.collides(i)) {
                                             if (this.collides(i + Math.floor((double)eyeHeight))) {
                                                if (eyeHeight > 1.0F) {
                                                   ++i;
                                                }
                                             } else {
                                                boolean cantRubberband = false;
                                                if ((double)rubberHeight - i < 5.0 && i - (double)rubberHeight < 9.0) {
                                                   cantRubberband = true;

                                                   for(int j = rubberHeight; j <= this.maxRubberbandHeight.get(); ++j) {
                                                      if ((j < this.minBlacklist.get() || j > this.maxBlacklist.get())
                                                         && (j <= -3 || j >= 3)
                                                         && (!((double)j - i < 5.0) || !(i - (double)j < 9.0))
                                                         && !this.collides((double)j)) {
                                                         if (!this.collides((double)j + Math.floor((double)eyeHeight))) {
                                                            rubberHeight = j;
                                                            cantRubberband = false;
                                                            break;
                                                         }

                                                         if (eyeHeight > 1.0F) {
                                                            ++j;
                                                         }
                                                      }
                                                   }
                                                }

                                                if (!cantRubberband) {
                                                   cantJump = false;
                                                   if (this.collides(i - 1.0)) {
                                                      if (i == 4.0) {
                                                         this.packetList.add(0.025);
                                                      }

                                                      i += 0.025;
                                                   }

                                                   this.packetList.add(i);
                                                   break;
                                                }
                                             }
                                          }
                                       }

                                       if (cantJump) {
                                          this.toggleWithError(972483264, Text.translatable("burrow.headroom"));
                                          return;
                                       }
                                    } else {
                                       this.packetList.add(0.42);
                                       this.packetList.add(0.75);
                                       this.packetList.add(1.01);
                                       this.packetList.add(1.15);
                                       if (onEchest) {
                                          double maxY = placeShape.offset(
                                                (double)playerBlock.getX(), (double)playerBlock.getY(), (double)playerBlock.getZ()
                                             )
                                             .getMax(class_2351.Y);
                                          if (this.playerPos.y + 1.15 <= maxY) {
                                             this.packetList.add(maxY - this.playerPos.y + 0.025);
                                          }
                                       }
                                    }

                                    this.packetList.add((double)rubberHeight);
                                    this.performBurrow(result, hitResult, playerBlock, state, entity);
                                    return;
                                 }

                                 if (eyeHeight > 1.0F) {
                                    ++rubberHeight;
                                 }
                              }
                           }

                           this.toggleWithError(972483264, Text.translatable("burrow.rubberband"));
                        } else {
                           this.toggleWithError(972483264, Text.translatable("burrow.entities"));
                        }
                     }
                  }
               }
            }
         } else {
            this.toggleWithError(972483264, Text.translatable("burrow.block"));
         }
      }
   }

   @EventHandler
   private void onPacketSent(PacketEvent.Sent event) {
      Packet var3 = event.getPacket();
      if (var3 instanceof PlayerMoveC2SPacket packet) {
         this.serverPitch = packet.getPitch(this.serverPitch);
      }
   }

   private void performBurrow(ItemPos result, BlockHitResult hitResult, BlockPos playerBlock, BlockState state, @Nullable Entity entity) {
      if (this.center.get()) {
         mc.player.setPosition(this.playerPos);
      }

      for(int i = 0; i < this.packetList.size(); ++i) {
         double height = this.packetList.get(i);
         if (i < this.packetList.size() - 1) {
            mc.player
               .networkHandler
               .sendPacket(new class_2829(mc.player.getX(), mc.player.getY() + height, mc.player.getZ(), true));
         } else {
            if (entity != null) {
               mc.player.networkHandler.sendPacket(PlayerInteractEntityC2SPacket.attack(entity, mc.player.isSneaking()));
               swing(result.getHand(), this.swing.get());
            }

            if (state.getBlock() instanceof BedBlock && mc.world.getRegistryKey() != World.OVERWORLD) {
               mc.player
                  .networkHandler
                  .sendPacket(
                     new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, new BlockHitResult(Vec3d.ofCenter(playerBlock), Direction.UP, playerBlock, true), 0)
                  );
            } else if (state.getBlock().getHardness() == 0.0F && !state.isAir()) {
               WorldUtils.mine(playerBlock, false, false);
            }

            BlockUtils.justPlace(result, hitResult, this.swing.get(), false, 0);
            mc.player
               .networkHandler
               .sendPacket(new class_2829(mc.player.getX(), mc.player.getY() + height, mc.player.getZ(), false));
            this.toggle(false);
         }
      }
   }

   @Nullable
   private Entity getEntityInDaWay(VoxelShape placeShape, BlockPos playerBlock) {
      EndCrystalEntity crystal = null;

      try {
         for(Entity entity : mc.world
            .getOtherEntities(
               mc.player,
               placeShape.isEmpty() ? new Box(playerBlock) : placeShape.getBoundingBox().offset(playerBlock),
               Entity::canHit
            )) {
            if (!(entity instanceof EndCrystalEntity)) {
               return entity;
            }

            EndCrystalEntity c = (EndCrystalEntity)entity;
            crystal = c;
         }
      } catch (ConcurrentModificationException var7) {
      }

      return crystal;
   }

   @EventHandler(
      priority = -200
   )
   private void onLateTick(TickEvent.Pre event) {
   }

   private boolean collides(double yOffset) {
      return mc.world
            .getBlockState(this.mutable.set(this.playerPos.x + 0.3, this.playerPos.y + yOffset, this.playerPos.z + 0.3))
            .getBlock()
            .collidable
         || mc.world
            .getBlockState(this.mutable.set(this.playerPos.x + 0.3, this.playerPos.y + yOffset, this.playerPos.z - 0.3))
            .getBlock()
            .collidable
         || mc.world
            .getBlockState(this.mutable.set(this.playerPos.x - 0.3, this.playerPos.y + yOffset, this.playerPos.z + 0.3))
            .getBlock()
            .collidable
         || mc.world
            .getBlockState(this.mutable.set(this.playerPos.x - 0.3, this.playerPos.y + yOffset, this.playerPos.z - 0.3))
            .getBlock()
            .collidable;
   }

   @Override
   public void onDisable() {
      this.playerBlock = null;
      if (mc.player != null) {
         this.serverPitch = mc.player.getPitch();
      }
   }

   private void handleListener(boolean ignored) {
      if (!this.autoTrap.get() && !this.autoReburrow.get()) {
         Venomhack.EVENTS.unsubscribe(this.BURROW_LISTENER);
      } else {
         Venomhack.EVENTS.subscribe(this.BURROW_LISTENER);
      }
   }

   private class StaticListener {
      private BlockPos pos = null;
      private int delay;

      @EventHandler
      private void surroundListener(PlayerMoveEvent.Post event) {
         --this.delay;
         if (WorldUtils.isObbyBurrowed(Burrow.mc.player)
            && Burrow.mc.world.getBlockState(Burrow.mc.player.getBlockPos()).getBlock().getBlastResistance() > 600.0F) {
            this.pos = Burrow.mc.player.getBlockPos();
         } else if (!Burrow.this.isActive()
            && Burrow.mc.player.isOnGround()
            && !WorldUtils.isBurrowed(Burrow.mc.player)
            && (
               !Burrow.this.pauseEating.get()
                  || !Burrow.mc.player.isUsingItem()
                  || !Burrow.mc.player.getMainHandStack().isFood() && !Burrow.mc.player.getOffHandStack().isFood()
            )) {
            if (Burrow.this.autoReburrow.get() && Burrow.mc.player.getBlockPos().equals(this.pos)) {
               this.pos = null;
               Burrow.this.toggle();
            } else {
               if (this.delay <= 0 && Burrow.this.autoTrap.get() && WorldUtils.isSurrounded(Burrow.mc.player, false, false)) {
                  for(AbstractClientPlayerEntity enemy : Burrow.mc.world.getPlayers()) {
                     if (!Burrow.mc.player.equals(enemy) && RangeUtils.isWithinRange(RangeUtils.Origin.Feet, enemy, 5.0)) {
                        if (Burrow.mc.player.getBlockPos().equals(enemy.getBlockPos())) {
                           break;
                        }

                        if (Friends.isFriend(enemy)
                           && !WorldUtils.isSurrounded(enemy, true, true)
                           && Burrow.mc.player.getPos().add(0.0, 1.0, 0.0).squaredDistanceTo(enemy.getPos()) <= 4.0
                           && enemy.getY() > Burrow.mc.player.getY()) {
                           Burrow.this.toggle();
                           this.delay = 20;
                           return;
                        }
                     }
                  }
               }

               this.pos = null;
            }
         }
      }
   }

   public static enum SurroundBlocks {
      OBSIDIAN,
      ENDER_CHEST,
      CRYING_OBSIDIAN,
      NETHERITE_BLOCK,
      ANCIENT_DEBRIS,
      RESPAWN_ANCHOR,
      ANVIL,
      HELD;

      public ItemPos getBlock() {
         return InvUtils.findInHotbar(item -> {
            return switch(this) {
               case OBSIDIAN -> Items.OBSIDIAN == item;
               case ANCIENT_DEBRIS -> Items.ANCIENT_DEBRIS == item;
               case CRYING_OBSIDIAN -> Items.CRYING_OBSIDIAN == item;
               case NETHERITE_BLOCK -> Items.NETHERITE_BLOCK == item;
               case ENDER_CHEST -> Items.ENDER_CHEST == item;
               case RESPAWN_ANCHOR -> Items.RESPAWN_ANCHOR == item && WorldUtils.getWorld() == World.NETHER;
               case ANVIL -> Block.getBlockFromItem(item) instanceof AnvilBlock;
               case HELD -> !Block.getBlockFromItem(item).equals(Blocks.AIR);
            };
         });
      }
   }
}
