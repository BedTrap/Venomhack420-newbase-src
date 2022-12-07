package de.tyrannus.venomhack.modules.combat;

import de.tyrannus.venomhack.events.PacketEvent;
import de.tyrannus.venomhack.events.TickEvent;
import de.tyrannus.venomhack.modules.Module;
import de.tyrannus.venomhack.settings.Setting;
import de.tyrannus.venomhack.utils.inventory.InvUtils;
import de.tyrannus.venomhack.utils.inventory.ItemPos;
import de.tyrannus.venomhack.utils.world.BlockUtils;
import de.tyrannus.venomhack.utils.world.SwitchMode;
import java.util.ArrayList;
import java.util.List;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.Hand;
import net.minecraft.item.Items;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Blocks;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.FireBlock;
import net.minecraft.util.math.Vec3d;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.BlockBreakingProgressS2CPacket;
import net.minecraft.network.packet.c2s.play.TeleportConfirmC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.client.world.ClientWorld;

public class SelfTrap extends Module {
   private final Setting<SelfTrap.Mode> mode = this.setting("mode", "How to trap.", SelfTrap.Mode.FULL);
   private final Setting<Boolean> antiBed = this.setting("anti-bed", "Rapidly destroys beds to protect you.", Boolean.valueOf(true));
   private final Setting<Boolean> antiAnchor = this.setting("anti-anchor", "Rapidly destroys anchors to protect you.", Boolean.valueOf(true));
   private final Setting<Boolean> antiFunny = this.setting("anti-funny", "Attempts to place obsidian on your CEV - smart.", Boolean.valueOf(true));
   private final Setting<SwitchMode> switchMode = this.setting("switch-mode", "How to switch.", SwitchMode.OLD);
   private final Setting<Boolean> rotate = this.setting("rotate", "Whether or not to rotate where you are placing.", Boolean.valueOf(false));
   private final Setting<Boolean> airPlace = this.setting("air-place", "Whether or not to airplace.", Boolean.valueOf(true));
   private final Setting<Boolean> strictDirections = this.setting(
      "strict-directions", "Whether or not to use strict directions when placing.", Boolean.valueOf(false)
   );
   private final Setting<Boolean> swing = this.setting("swing", "Whether or not to swing when placing.", Boolean.valueOf(true));
   private final List<BlockPos> positions = new ArrayList();
   private BlockPos playerPos;

   public SelfTrap() {
      super(Module.Categories.COMBAT, "self-trap", "Places blocks around your head.");
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      if (mc.world != null) {
         if (this.antiBed.get()) {
            this.breakBed(this.playerPos.up());
         }

         if (this.antiAnchor.get()) {
            this.breakAnchor(this.playerPos.up(2));
         }

         if (mc.player.getBlockPos() != this.playerPos) {
            this.toggleWithError("You moved, Disabling.");
         } else {
            this.place();
            this.positions.clear();
         }
      }
   }

   @EventHandler
   private void onReceive(PacketEvent.Receive event) {
      if (this.antiFunny.get()) {
         Packet var3 = event.getPacket();
         if (var3 instanceof BlockBreakingProgressS2CPacket packet) {
            if (mc.world.getEntityById(packet.getEntityId()) == mc.player) {
               return;
            }

            if (!packet.getPos().equals(this.playerPos.up(2))) {
               return;
            }

            this.add(packet.getPos().up());
         }
      }
   }

   @EventHandler
   private void onSend(PacketEvent.Sent event) {
      if (event.getPacket() instanceof TeleportConfirmC2SPacket) {
         this.toggleWithError("You teleported. Disabling!");
      }
   }

   private void breakBed(BlockPos pos) {
      if (mc.world.getBlockState(pos).getBlock() instanceof BedBlock) {
         if (!mc.world.getDimension().comp_648()) {
            this.sendInteract(pos);
         }
      }
   }

   private void breakAnchor(BlockPos pos) {
      ClientWorld world = mc.world;
      if (world.getBlockState(pos).getBlock() instanceof RespawnAnchorBlock) {
         if (world.getLightLevel(pos) != 0) {
            if (!world.getDimension().comp_649()) {
               this.sendInteract(pos);
            }
         }
      }
   }

   private void place() {
      this.getPositions();
      ItemPos block = this.findBlock();
      if (!block.found()) {
         this.toggleWithError("No obsidian in your hotbar, Disabling!");
      } else {
         for(BlockPos position : this.positions) {
            Block getBlock = mc.world.getBlockState(position).getBlock();
            if (getBlock.equals(Blocks.AIR) || getBlock instanceof FireBlock) {
               BlockHitResult hitResult = BlockUtils.getPlaceResult(position, this.airPlace.get(), this.strictDirections.get());
               BlockUtils.justPlace(block, hitResult, this.swing.get(), this.rotate.get(), 100, this.switchMode.get());
            }
         }
      }
   }

   private void getPositions() {
      BlockPos up = this.playerPos.up();
      switch((SelfTrap.Mode)this.mode.get()) {
         case TOP:
            this.add(up.up());
            break;
         case FULL:
            this.add(up.up());
            this.add(up.north());
            this.add(up.east());
            this.add(up.south());
            this.add(up.west());
      }
   }

   private void sendInteract(BlockPos pos) {
      mc.player
         .networkHandler
         .sendPacket(
            new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, new BlockHitResult(Vec3d.ofCenter(pos), BlockUtils.getClosestDirection(pos, false), pos, false), 0)
         );
   }

   private ItemPos findBlock() {
      return InvUtils.findInHotbar(Items.OBSIDIAN);
   }

   private void add(BlockPos pos) {
      this.positions.add(pos);
   }

   @Override
   public void onEnable() {
      this.playerPos = mc.player.getBlockPos();
   }

   @Override
   public void onDisable() {
      this.positions.clear();
   }

   public static enum Mode {
      TOP,
      FULL;
   }
}
