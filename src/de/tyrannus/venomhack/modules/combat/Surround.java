package de.tyrannus.venomhack.modules.combat;

import de.tyrannus.venomhack.Venomhack;
import de.tyrannus.venomhack.events.PacketEvent;
import de.tyrannus.venomhack.events.PlayerMoveEvent;
import de.tyrannus.venomhack.events.RenderEvent;
import de.tyrannus.venomhack.events.TickEvent;
import de.tyrannus.venomhack.modules.Module;
import de.tyrannus.venomhack.modules.Modules;
import de.tyrannus.venomhack.modules.combat.autocrystal.Origin;
import de.tyrannus.venomhack.modules.movement.Step;
import de.tyrannus.venomhack.settings.Setting;
import de.tyrannus.venomhack.utils.Analyser;
import de.tyrannus.venomhack.utils.AntiCheatHelper;
import de.tyrannus.venomhack.utils.RangeUtils;
import de.tyrannus.venomhack.utils.Timer;
import de.tyrannus.venomhack.utils.inventory.InvUtils;
import de.tyrannus.venomhack.utils.inventory.ItemPos;
import de.tyrannus.venomhack.utils.players.PlayerUtils;
import de.tyrannus.venomhack.utils.render.RenderBlock;
import de.tyrannus.venomhack.utils.render.RenderMode;
import de.tyrannus.venomhack.utils.render.RenderUtils;
import de.tyrannus.venomhack.utils.world.BlockUtils;
import de.tyrannus.venomhack.utils.world.SwitchMode;
import de.tyrannus.venomhack.utils.world.WorldUtils;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.Hand;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.world.World;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.BlockBreakingProgressS2CPacket;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.block.BlockState;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.hit.BlockHitResult;

public class Surround extends Module {
   private final Setting<Boolean> center = this.setting("center", "Will move you to inside the hole so you can surround.", Boolean.valueOf(true));
   private final Setting<Boolean> hardSnap = this.setting(
      "hard-center", "Will align you at the exact center of the hole.", Boolean.valueOf(false), this.center::get
   );
   private final Setting<Boolean> antiSurroundBreak = this.setting(
      "protect", "Places blocks around the city block that is being mined.", Boolean.valueOf(false)
   );
   private final Setting<Boolean> antiPhase = this.setting(
      "anti-phase", "Places blocks around players that are standing in your surround", Boolean.valueOf(true)
   );
   private final Setting<Boolean> onlyGround = this.setting("ground", "Won't attempt to place while you're not standing on ground.", Boolean.valueOf(false));
   private final Setting<SwitchMode> switchMode = this.setting(
      "switch-mode", "How to switch slots. New might bypass certain anticheats better.", SwitchMode.OLD
   );
   private final Setting<Float> placeRange = this.setting("place-range", "How far you are able to place at max.", Float.valueOf(5.0F), 0.0F, 6.0F);
   private final Setting<Boolean> strictDirections = this.setting("strict-directions", "Places only on visible sides.", Boolean.valueOf(false));
   private final Setting<Boolean> airPlace = this.setting(
      "air-place", "Places blocks midair, will try to find support blocks when off.", Boolean.valueOf(true)
   );
   private final Setting<Integer> bpt = this.setting("blocks-per-tick", "How many blocks to place per tick max.", Integer.valueOf(5), 1.0F, 5.0F);
   private final Setting<Integer> delay = this.setting("place-delay", "Delay between placing in ms", Integer.valueOf(25), 25.0F, 250.0F);
   private final Setting<Boolean> attackCrystals = this.setting("attack-crystals", "Whether to attack crystals that are in the way.", Boolean.valueOf(true));
   private final Setting<Integer> attackSwapPenalty = this.setting(
      "swap-penalty",
      "For how long to wait in ms after switching to obsidian until attacking.",
      Integer.valueOf(0),
      null,
      this.attackCrystals::get,
      0.0F,
      500.0F
   );
   private final Setting<Integer> attackMinAge = this.setting(
      "min-age", "How many ticks the cystal has to be alive for until u can attack it.", Integer.valueOf(0), this.attackCrystals::get, 0.0F, 5.0F
   );
   private final Setting<Float> attackRange = this.setting("attack-range", "Maximum attack range.", Float.valueOf(4.0F), this.attackCrystals::get, 0.0F, 6.0F);
   private final Setting<Integer> attackDelay = this.setting(
      "attack-delay", "How many ticks to wait between attacks.", Integer.valueOf(1), this.attackCrystals::get, 1.0F, 5.0F
   );
   private final Setting<Boolean> yToggle = this.setting("y-toggle", "Will toggle off when you move upwards.", Boolean.valueOf(true));
   private final Setting<Boolean> auto = this.setting(
      "auto-surround", "Automatically turns on surround when in an obsidian hole.", Boolean.valueOf(false), value -> this.toggleSurroundListener()
   );
   private final Setting<Boolean> toggleStep = this.setting("toggle-step", "Toggles off step when activating surround.", Boolean.valueOf(false));
   private final Setting<Boolean> toggleBack = this.setting("toggle-back", "Toggles on speed and/or step when turning off surround.", Boolean.valueOf(false));
   private final Setting<Boolean> swing = this.setting("swing", "Renders your swing client-side.", Boolean.valueOf(true));
   private final Setting<Boolean> render = this.setting("render", "Renders the block where it is placing a crystal.", Boolean.valueOf(false));
   private final Setting<Integer> renderTime = this.setting("render-time", "Ticks to render the block for.", Integer.valueOf(8), this.render::get, 0.0F, 20.0F);
   private final Setting<RenderMode> renderMode = this.setting("shape-mode", "How the shapes are rendered.", RenderMode.BOTH, this.render::get);
   private final Setting<Float> linesWidth = this.setting(
      "line-width", "Width of the rendered lines.", Float.valueOf(1.5F), () -> this.render.get() && this.renderMode.get().lines()
   );
   private final Setting<Color> lineColor = this.setting(
      "line-color", "The line color.", new Color(0, 0, 255, 200), () -> this.render.get() && this.renderMode.get().lines()
   );
   private final Setting<Color> sideColor = this.setting(
      "side-color", "The side color.", new Color(0, 0, 255, 10), () -> this.render.get() && this.renderMode.get().sides()
   );
   private BlockPos playerPos;
   private boolean hasCentered;
   private final List<BlockPos> extras = new CopyOnWriteArrayList();
   private final List<RenderBlock> renderBlocks = Collections.synchronizedList(new ArrayList<>());
   private int attackDelayLeft;
   private int blocksPlaced;
   private final Timer swapPenaltyTimer = new Timer();
   private final Timer placeTimer = new Timer();
   private final ConcurrentHashMap<Integer, Long> toRemoveWithTime = new ConcurrentHashMap<>();
   private final Surround.StaticListener surroundListener = new Surround.StaticListener();
   private static final Direction[] ORDERED_DIRECTIONS_ARRAY = new Direction[]{
      Direction.EAST, Direction.WEST, Direction.SOUTH, Direction.NORTH, Direction.DOWN, Direction.UP
   };

   public Surround() {
      super(Module.Categories.COMBAT, "surround", "Surrounds your legs with blast proof blocks.");
      if (this.auto.get()) {
         this.toggleSurroundListener();
      }
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      if (this.yToggle.get()
         && this.playerPos != null
         && (
               mc.world.getBlockState(mc.player.getBlockPos()).isOf(Blocks.ENDER_CHEST)
                  ? Math.ceil(mc.player.getY())
                  : (double)mc.player.getBlockY()
            )
            > (double)this.playerPos.getY()) {
         this.toggle(false);
      } else {
         this.blocksPlaced = 0;
         if (!this.onlyGround.get() || mc.player.isOnGround()) {
            if (this.center.get() && !this.hasCentered) {
               BlockUtils.centerPlayer(this.hardSnap.get());
               this.hasCentered = true;
            }

            this.playerPos = new BlockPos(
               (double)mc.player.getBlockX(),
               mc.world.getBlockState(mc.player.getBlockPos()).isOf(Blocks.ENDER_CHEST)
                  ? Math.ceil(mc.player.getY())
                  : (double)mc.player.getBlockY(),
               (double)mc.player.getBlockZ()
            );
            if (!this.yToggle.get()
               || this.playerPos == null
               || (
                     mc.world.getBlockState(mc.player.getBlockPos()).isOf(Blocks.ENDER_CHEST)
                        ? Math.ceil(mc.player.getY())
                        : (double)mc.player.getBlockY()
                  )
                  <= (double)this.playerPos.getY()) {
               this.place();
            }
         }
      }
   }

   @EventHandler
   public void onPacketReceive(PacketEvent.Receive event) {
      if (this.playerPos != null && mc.player != null && mc.world != null) {
         for(Integer key : this.toRemoveWithTime.keySet()) {
            if (System.currentTimeMillis() - 100L > this.toRemoveWithTime.getOrDefault(key, Long.MAX_VALUE)) {
               this.toRemoveWithTime.remove(key);
            }
         }

         if (!this.yToggle.get()
            || this.playerPos == null
            || (
                  mc.world.getBlockState(mc.player.getBlockPos()).isOf(Blocks.ENDER_CHEST)
                     ? Math.ceil(mc.player.getY())
                     : (double)mc.player.getBlockY()
               )
               <= (double)this.playerPos.getY()) {
            this.place();
         }

         Packet result = event.getPacket();
         if (result instanceof BlockBreakingProgressS2CPacket packet) {
            if (!this.antiSurroundBreak.get()) {
               return;
            }

            if (mc.world.getEntityById(packet.getEntityId()) == mc.player) {
               return;
            }

            if (mc.world.getBlockState(packet.getPos()).getBlock().getHardness() < 0.0F) {
               return;
            }

            if (!this.getPlacePositions(false).contains(packet.getPos())) {
               return;
            }

            this.extras.add(packet.getPos().north());
            this.extras.add(packet.getPos().west());
            this.extras.add(packet.getPos().south());
            this.extras.add(packet.getPos().east());
         } else {
            result = event.getPacket();
            if (result instanceof BlockUpdateS2CPacket packet) {
               if (!this.placeTimer.passedMillis((long)this.delay.get().intValue())) {
                  return;
               }

               if (!packet.getState().getMaterial().isReplaceable() && packet.getState().getBlock().getHardness() != 0.0F) {
                  return;
               }

               ItemPos result = this.findBlock();
               if (!result.found()) {
                  return;
               }

               if (!this.getPlacePositions(false).contains(packet.getPos())) {
                  return;
               }

               this.doPlace(packet.getState(), result, packet.getPos(), false);
            }
         }
      }
   }

   @EventHandler
   private void onPacketSend(PacketEvent.Sent event) {
      if (event.getPacket() instanceof UpdateSelectedSlotC2SPacket) {
         this.swapPenaltyTimer.reset();
      }
   }

   @EventHandler
   private void onPostTick(TickEvent.Post event) {
      this.extras.clear();
      synchronized(this.renderBlocks) {
         RenderBlock.tick(this.renderBlocks);
      }

      --this.attackDelayLeft;
   }

   private boolean doPlace(BlockState state, ItemPos result, BlockPos pos, boolean attackRotate) {
      if (BlockUtils.invalidPos(pos)) {
         return false;
      } else {
         boolean isBed = state.getBlock() instanceof BedBlock && mc.world.getRegistryKey() != World.OVERWORLD;
         if (!isBed && !state.getMaterial().isReplaceable() && state.getBlock().getHardness() != 0.0F) {
            return false;
         } else {
            BlockHitResult hitResult = BlockUtils.getPlaceResult(pos, this.airPlace.get(), this.strictDirections.get());
            if (hitResult == null) {
               return false;
            } else {
               VoxelShape placeShape = this.getPlaceState(result).getCollisionShape(mc.world, pos, ShapeContext.absent());
               Entity crystal = null;

               try {
                  label111: {
                     Iterator var9 = mc.world
                        .getOtherEntities(null, placeShape.isEmpty() ? new Box(pos) : placeShape.getBoundingBox().offset(pos), Entity::canHit)
                        .iterator();

                     while(true) {
                        if (!var9.hasNext()) {
                           break label111;
                        }

                        Entity entity = (Entity)var9.next();
                        if (!(entity instanceof EndCrystalEntity)) {
                           break;
                        }

                        if (crystal == null && !this.toRemoveWithTime.containsKey(entity.getId())) {
                           if (!this.attackCrystals.get()
                              || this.attackDelayLeft > 0
                              || !this.swapPenaltyTimer.passedMillis((long)this.attackSwapPenalty.get().intValue())
                              || !RangeUtils.isWithinRange(RangeUtils.Origin.Feet, entity, (double)this.attackRange.get().floatValue())
                              || entity.age < this.attackMinAge.get()) {
                              break;
                           }

                           crystal = entity;
                        }
                     }

                     return false;
                  }
               } catch (ConcurrentModificationException var13) {
               }

               if (crystal != null) {
                  this.toRemoveWithTime.put(crystal.getId(), System.currentTimeMillis());
                  this.attackDelayLeft = this.attackDelay.get();
                  if (!attackRotate) {
                     this.attackCrystal(crystal, result.getHand());
                  }
               }

               if (isBed) {
                  mc.player
                     .networkHandler
                     .sendPacket(
                        new PlayerInteractBlockC2SPacket(
                           Hand.MAIN_HAND, new BlockHitResult(Vec3d.ofCenter(pos), BlockUtils.getClosestDirection(pos, false), pos, false), 0
                        )
                     );
               } else if (state.getBlock().getHardness() == 0.0F && !state.isAir()) {
                  WorldUtils.mine(pos, false, false);
               }

               BlockUtils.justPlace(result, hitResult, this.swing.get(), false, 100, this.switchMode.get());
               if (this.getPlaceState(result).getBlock() == Blocks.OBSIDIAN) {
                  Analyser.pendingObsidian.putIfAbsent(pos, System.currentTimeMillis());
               }

               this.placeTimer.reset();
               ++this.blocksPlaced;
               synchronized(this.renderBlocks) {
                  RenderBlock.addRenderBlock(this.renderBlocks, pos, this.renderTime.get());
                  return true;
               }
            }
         }
      }
   }

   private void add(Set<BlockPos> set, BlockPos pos) {
      if (this.airPlace.get()) {
         set.add(pos);
      } else if (!BlockUtils.invalidPos(pos)) {
         if (!AntiCheatHelper.outOfPlaceRange(pos, Origin.NCP, (double)this.placeRange.get().floatValue())) {
            BlockState state = mc.world.getBlockState(pos);
            if (state.getMaterial().isReplaceable() || state.getBlock().getHardness() == 0.0F) {
               ItemPos result = this.findBlock();
               if (result.found()) {
                  BlockState placeState = this.getPlaceState(result);
                  if (this.findNeighbour(set, pos, 0, placeState, (int)Math.floor(PlayerUtils.eyePos().y))) {
                     set.add(pos);
                  }
               }
            }
         }
      }
   }

   private boolean findNeighbour(Set<BlockPos> set, BlockPos pos, int iteration, BlockState placeState, int playerEyeY) {
      for(Direction direction : Direction.values()) {
         BlockPos neighbour = pos.offset(direction);
         if ((
               !this.strictDirections.get()
                  || !AntiCheatHelper.notInteractableStrict(
                     mc.player.getBlockX(), playerEyeY, mc.player.getBlockZ(), neighbour, direction.getOpposite()
                  )
            )
            && (
               set.contains(neighbour)
                  || Analyser.pendingObsidian.containsKey(neighbour)
                  || !mc.world.getBlockState(neighbour).getMaterial().isReplaceable()
            )) {
            set.add(pos);
            return true;
         }
      }

      if ((double)(iteration + 1) > Math.ceil((double)this.placeRange.get().floatValue()) * 2.0) {
         return false;
      } else {
         for(Direction direction : ORDERED_DIRECTIONS_ARRAY) {
            BlockPos neighbour = pos.offset(direction);
            if (BlockUtils.invalidPos(neighbour)) {
               return false;
            }

            if (AntiCheatHelper.outOfPlaceRange(neighbour, Origin.NCP, (double)this.placeRange.get().floatValue())) {
               return false;
            }

            if (this.strictDirections.get() && placeState.isFullCube(mc.world, neighbour)) {
               Set<Direction> strictDirs = AntiCheatHelper.getInteractableDirections(
                  mc.player.getBlockX() - neighbour.getX(),
                  playerEyeY - neighbour.getY(),
                  mc.player.getBlockZ() - neighbour.getZ(),
                  true
               );
               Direction oppositeDirection = direction.getOpposite();
               if (!strictDirs.contains(oppositeDirection)
                  || AntiCheatHelper.isDirectionBlocked(neighbour, strictDirs, oppositeDirection, true)
                  || set.contains(pos)) {
                  continue;
               }
            }

            if (this.attackCrystals.get()) {
               VoxelShape placeShape = placeState.getCollisionShape(mc.world, neighbour);
               if (!mc.world
                  .getOtherEntities(
                     null,
                     placeShape.isEmpty() ? new Box(neighbour) : placeShape.getBoundingBox().offset(neighbour),
                     entity -> entity.canHit() && !(entity instanceof EndCrystalEntity)
                  )
                  .isEmpty()) {
                  continue;
               }
            } else if (!mc.world.canPlace(placeState, neighbour, ShapeContext.absent())) {
               continue;
            }

            if (this.findNeighbour(set, neighbour, ++iteration, placeState, playerEyeY)) {
               return false;
            }
         }

         return false;
      }
   }

   public Set<BlockPos> getPlacePositions(boolean withExtra) {
      HashSet<BlockPos> positions = new HashSet(12);
      if (this.playerPos == null) {
         this.playerPos = mc.player.getBlockPos();
      }

      try {
         label74:
         for(BlockPos city : BlockUtils.getCity(mc.player, true, true)) {
            BlockState state = mc.world.getBlockState(city);
            if (!state.isOf(Blocks.BEDROCK)) {
               if (city.getY() == this.playerPos.getY()) {
                  for(Entity entity : mc.world
                     .getOtherEntities(
                        null,
                        new Box(
                           (double)city.getX(),
                           (double)city.getY(),
                           (double)city.getZ(),
                           (double)city.getX() + 0.9999,
                           (double)city.getY() + 0.9999,
                           (double)city.getZ() + 0.9999
                        ),
                        Entity::canHit
                     )) {
                     if (!this.attackCrystals.get() || !(entity instanceof EndCrystalEntity)) {
                        if (!(entity instanceof PlayerEntity)) {
                           continue label74;
                        }

                        PlayerEntity player = (PlayerEntity)entity;
                        if (PlayerUtils.smartVelocity(player).length() == 0.0) {
                           if (this.antiPhase.get()) {
                              for(BlockPos blockPos : BlockUtils.getCity(player, false, true)) {
                                 this.add(positions, new BlockPos(blockPos.getX(), city.getY(), blockPos.getZ()));
                              }
                           }
                           continue label74;
                        }
                     }
                  }
               }

               this.add(positions, city);
            }
         }
      } catch (ConcurrentModificationException var11) {
      }

      if (withExtra) {
         for(BlockPos block : this.extras) {
            this.add(positions, block);
         }
      }

      return positions;
   }

   private void place() {
      if (this.placeTimer.passedMillis((long)this.delay.get().intValue())) {
         if (!this.onlyGround.get() || mc.player.isOnGround()) {
            ItemPos result = this.findBlock();
            if (result.found()) {
               ArrayList<BlockPos> blockList = new ArrayList(this.getPlacePositions(true));
               blockList.sort(Comparator.comparingDouble(o -> o.getSquaredDistance(mc.player.getPos())));

               for(BlockPos pos : blockList) {
                  if (this.doPlace(mc.world.getBlockState(pos), result, pos, false) && this.blocksPlaced >= this.bpt.get()) {
                     break;
                  }
               }
            }
         }
      }
   }

   private BlockState getPlaceState(ItemPos result) {
      Item item = result.item();
      return item instanceof BlockItem blockItem ? blockItem.getBlock().getDefaultState() : Blocks.AIR.getDefaultState();
   }

   private void attackCrystal(Entity entity, Hand hand) {
      mc.player.networkHandler.sendPacket(PlayerInteractEntityC2SPacket.attack(entity, mc.player.isSneaking()));
      swing(hand, this.swing.get());
   }

   private ItemPos findBlock() {
      ItemPos result = InvUtils.findInHotbar(Items.OBSIDIAN);
      if (!result.found()) {
         result = InvUtils.findInHotbar(WorldUtils::isGoodForSurround);
      }

      return result;
   }

   @EventHandler
   private void onRender(RenderEvent.Flat event) {
      if (this.render.get() && !this.renderBlocks.isEmpty()) {
         synchronized(this.renderBlocks) {
            for(RenderBlock block : this.renderBlocks) {
               RenderUtils.drawBlock(this.renderMode.get(), block.pos, this.linesWidth.get(), this.lineColor.get(), this.sideColor.get(), false);
            }
         }
      }
   }

   @Override
   public void onEnable() {
      this.hasCentered = false;
      this.playerPos = null;
      this.attackDelayLeft = 0;
      this.renderBlocks.clear();
      this.swapPenaltyTimer.setMs((long)this.attackSwapPenalty.get().intValue());
      this.placeTimer.setMs((long)this.delay.get().intValue());
      this.toRemoveWithTime.clear();
      synchronized(this.renderBlocks) {
         this.renderBlocks.clear();
      }

      Step step = Modules.get(Step.class);
      if (this.toggleStep.get() && step.isActive()) {
         step.toggle(false);
      }
   }

   @Override
   public void onDisable() {
      if (this.toggleBack.get()) {
         Step step = Modules.get(Step.class);
         if (this.toggleStep.get() && !step.isActive()) {
            step.toggle(false);
         }
      }
   }

   private void toggleSurroundListener() {
      if (this.auto.get()) {
         Venomhack.EVENTS.subscribe(this.surroundListener);
      } else {
         Venomhack.EVENTS.unsubscribe(this.surroundListener);
      }
   }

   private class StaticListener {
      @EventHandler
      private void surroundListener(PlayerMoveEvent.Post event) {
         if (!Surround.this.isActive()
            && (!Surround.this.onlyGround.get() || Surround.mc.player.isOnGround())
            && WorldUtils.obbySurrounded(Surround.mc.player)) {
            Surround.this.toggle(false);
         }
      }
   }
}
