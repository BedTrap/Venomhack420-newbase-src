package de.tyrannus.venomhack.modules.combat;

import de.tyrannus.venomhack.events.RenderEvent;
import de.tyrannus.venomhack.events.TickEvent;
import de.tyrannus.venomhack.modules.Module;
import de.tyrannus.venomhack.modules.combat.autocrystal.Origin;
import de.tyrannus.venomhack.settings.Setting;
import de.tyrannus.venomhack.utils.AntiCheatHelper;
import de.tyrannus.venomhack.utils.inventory.InvUtils;
import de.tyrannus.venomhack.utils.inventory.ItemPos;
import de.tyrannus.venomhack.utils.players.Friends;
import de.tyrannus.venomhack.utils.render.RenderMode;
import de.tyrannus.venomhack.utils.render.RenderUtils;
import de.tyrannus.venomhack.utils.world.BlockUtils;
import de.tyrannus.venomhack.utils.world.WorldUtils;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.Hand;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.item.PickaxeItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.block.BlockState;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.hit.BlockHitResult;

public class FunnyCrystal extends Module {
   private final Setting<Float> range = this.setting("range", "The place range.", Float.valueOf(5.0F), 3.0F, 6.0F);
   private final Setting<Boolean> onlySurrounded = this.setting("only-surrounded", "Only funny crystals if the target is surrounded.", Boolean.valueOf(true));
   private final Setting<Integer> minHealth = this.setting("min-health", "Pauses funnycrystal if you are low on health.", Integer.valueOf(12), 0.0F, 36.0F);
   public final Setting<Boolean> render = this.setting("render", "Renders a box around the target.", Boolean.valueOf(false));
   private final Setting<RenderMode> renderMode = this.setting("render-mode", "How to r ender lines & sides.", RenderMode.LINES, this.render::get);
   private final Setting<Float> lineWidth = this.setting(
      "line-width", "The line width.", Float.valueOf(1.5F), () -> this.render.get() && this.renderMode.get().lines(), 0.0F, 3.0F
   );
   private final Setting<Color> lineColor = this.setting(
      "line-color", "The color of the outline.", Color.RED, () -> this.render.get() && this.renderMode.get().lines()
   );
   private final Setting<Color> sideColor = this.setting(
      "side-color", "The side color.", new Color(255, 0, 0, 100), () -> this.render.get() && this.renderMode.get().sides()
   );
   private final Setting<Boolean> chromaLines = this.setting(
      "chroma-lines", "Chroma Lines.", Boolean.valueOf(false), () -> this.render.get() && this.renderMode.get().lines()
   );
   private final Setting<Boolean> chromaSides = this.setting(
      "chroma-sides", "Chroma Sides.", Boolean.valueOf(false), () -> this.render.get() && this.renderMode.get().sides()
   );
   private final Setting<Float> chromaSpeed = this.setting(
      "chroma-speed", "How fast to cycle between colors.", Float.valueOf(3.0F), () -> this.chromaLines.get() || this.chromaSides.get()
   );
   private final Setting<Integer> chromaLinesAlpha = this.setting(
      "chroma-line-alpha",
      "The opacity of the chroma effect for lines.",
      Integer.valueOf(255),
      () -> this.render.get() && this.renderMode.get().lines() && this.chromaLines.get(),
      0.0F,
      255.0F
   );
   private final Setting<Integer> chromaSidesAlpha = this.setting(
      "chroma-side-alpha",
      "The opacity of the chroma effect for sides.",
      Integer.valueOf(100),
      () -> this.render.get() && this.renderMode.get().sides(),
      0.0F,
      255.0F
   );
   private final List<PlayerEntity> targets = new ArrayList();
   private EndCrystalEntity targetCrystal;
   private BlockPos currentPos;

   public FunnyCrystal() {
      super(Module.Categories.COMBAT, "funny-crystal", "Places crystals above the target's head to deal damage.");
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      if (mc.player != null) {
         for(Entity entity : mc.world.getEntities()) {
            if (entity instanceof PlayerEntity player
               && player != mc.player
               && !Friends.isFriend(player)
               && player.getBlockPos().up().isWithinDistance(mc.player.getEyePos(), (double)this.range.get().floatValue())
               && (!this.onlySurrounded.get() || WorldUtils.isSurrounded(player, true, false))) {
               this.targets.add(player);
               this.targets.sort(Comparator.comparingDouble(p -> p.squaredDistanceTo(mc.player)));
            }
         }

         if (!this.targets.isEmpty()) {
            PlayerEntity target = (PlayerEntity)this.targets.get(0);
            if (target.isDead()) {
               this.toggleWithError("Eliminated, toggling.");
            } else if (this.isBurrowed(target)) {
               this.toggleWithError("Target is burrowed, toggling.");
            } else {
               this.currentPos = target.getBlockPos().up(2);
               if (this.currentPos != null) {
                  if (!this.stateOf(this.currentPos.up()).isAir()) {
                     this.toggleWithError("Not enough space, toggling.");
                  } else if (AntiCheatHelper.outOfMiningRange(this.currentPos, Origin.NCP, (double)this.range.get().floatValue())) {
                     this.toggleWithError("Target out of range, toggling.");
                  } else {
                     float hp = mc.player.getHealth() + mc.player.getAbsorptionAmount();
                     if (!(hp <= (float)this.minHealth.get().intValue())) {
                        for(Entity entity : mc.world.getEntities()) {
                           if (entity instanceof EndCrystalEntity crystal && crystal.getBlockPos().equals(this.currentPos.up())) {
                              this.targetCrystal = crystal;
                              break;
                           }
                        }

                        if (this.stateOf(this.currentPos).isAir()) {
                           if (this.targetCrystal != null) {
                              this.breakCrystal(this.targetCrystal);
                           }

                           this.targetCrystal = null;
                           this.placeBlock(this.currentPos);
                        }

                        if (this.targetCrystal == null) {
                           this.placeCrystal(this.currentPos);
                        }

                        this.mine(this.currentPos);
                        this.targetCrystal = null;
                        this.targets.clear();
                     }
                  }
               }
            }
         }
      }
   }

   private BlockState stateOf(BlockPos pos) {
      return mc.world.getBlockState(pos);
   }

   private void placeBlock(BlockPos pos) {
      ItemPos obsidian = InvUtils.findInHotbar(Items.OBSIDIAN);
      if (!obsidian.found()) {
         this.toggleWithError("No obsidian found, toggling.");
      } else {
         BlockHitResult hitResult = BlockUtils.getPlaceResult(pos, true, false);
         BlockUtils.justPlace(obsidian, hitResult, true, false, 0);
      }
   }

   private void breakCrystal(EndCrystalEntity crystal) {
      sendPacket(PlayerInteractEntityC2SPacket.attack(crystal, mc.player.isSneaking()));
   }

   private void placeCrystal(BlockPos pos) {
      ItemPos crystal = InvUtils.findInHotbar(Items.END_CRYSTAL);
      if (!crystal.found()) {
         this.toggleWithError("No crystals found, toggling.");
      } else {
         mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(crystal.slot()));
         mc.player.getInventory().selectedSlot = crystal.slot();
         mc.player
            .networkHandler
            .sendPacket(
               new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, new BlockHitResult(Vec3d.ofCenter(pos), BlockUtils.getClosestDirection(pos, false), pos, false), 0)
            );
      }
   }

   private void mine(BlockPos pos) {
      ItemPos pickaxe = InvUtils.findInHotbar(i -> i instanceof PickaxeItem);
      if (!pickaxe.found()) {
         this.toggleWithError("No pickaxe found, toggling.");
      } else {
         mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(pickaxe.slot()));
         mc.player.getInventory().selectedSlot = pickaxe.slot();
         WorldUtils.mine(pos, false, false);
      }
   }

   private boolean isBurrowed(PlayerEntity player) {
      return this.stateOf(player.getBlockPos()).getBlock().getBlastResistance() >= 600.0F;
   }

   @EventHandler
   private void onRender(RenderEvent.Flat event) {
      if (this.currentPos != null) {
         Color lineCol = this.chromaLines.get()
            ? RenderUtils.getChromaColor((float)this.chromaLinesAlpha.get().intValue(), this.chromaSpeed.get())
            : this.lineColor.get();
         Color sideCol = this.chromaSides.get()
            ? RenderUtils.getChromaColor((float)this.chromaSidesAlpha.get().intValue(), this.chromaSpeed.get())
            : this.sideColor.get();
         RenderUtils.drawBlock(this.renderMode.get(), this.currentPos, this.lineWidth.get(), lineCol, sideCol, this.chromaSides.get());
      }
   }

   @Override
   public void onDisable() {
      this.targets.clear();
      this.targetCrystal = null;
      this.currentPos = null;
   }
}
