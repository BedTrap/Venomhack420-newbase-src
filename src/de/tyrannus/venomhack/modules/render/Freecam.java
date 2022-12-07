package de.tyrannus.venomhack.modules.render;

import de.tyrannus.venomhack.events.KeyEvent;
import de.tyrannus.venomhack.events.OpenScreenEvent;
import de.tyrannus.venomhack.events.PacketEvent;
import de.tyrannus.venomhack.modules.Module;
import de.tyrannus.venomhack.modules.Modules;
import de.tyrannus.venomhack.modules.movement.InventoryWalk;
import de.tyrannus.venomhack.settings.Setting;
import de.tyrannus.venomhack.utils.Utils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.math.Vec3d;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.client.option.KeyBinding;

public class Freecam extends Module {
   private final Setting<Float> speed = this.setting("speed", "How fast to move around in blocks per second.", Float.valueOf(2.0F));
   private boolean forward;
   private boolean backward;
   private boolean left;
   private boolean right;
   private boolean up;
   private boolean down;

   public Freecam() {
      super(Module.Categories.RENDER, "freecam", "Allows you to move your camera around freely.");
   }

   @EventHandler
   private void onScreen(OpenScreenEvent event) {
      if (event.screen == null) {
         mc.options.forwardKey.setPressed(false);
         mc.options.backKey.setPressed(false);
         mc.options.leftKey.setPressed(false);
         mc.options.rightKey.setPressed(false);
         mc.options.jumpKey.setPressed(false);
         mc.options.sneakKey.setPressed(false);
      }
   }

   @EventHandler
   private void onPacketReceive(PacketEvent.Receive event) {
      if (event.getPacket() instanceof GameJoinS2CPacket && this.isActive()) {
         this.toggle(false);
      }
   }

   @EventHandler
   private void onKey(KeyEvent.Post event) {
      boolean pressed = event.getAction() != 0;
      if (!Utils.isKeyPressed(292)) {
         if (event.matchesBind(mc.options.forwardKey)) {
            this.forward = pressed;
         } else if (event.matchesBind(mc.options.backKey)) {
            this.backward = pressed;
         } else if (event.matchesBind(mc.options.leftKey)) {
            this.left = pressed;
         } else if (event.matchesBind(mc.options.rightKey)) {
            this.right = pressed;
         } else if (event.matchesBind(mc.options.jumpKey)) {
            this.up = pressed;
         } else {
            if (!event.matchesBind(mc.options.sneakKey)) {
               return;
            }

            this.down = pressed;
         }

         KeyBinding.setKeyPressed(event.getKey(), false);
      }
   }

   public Vec3d getPos() {
      InventoryWalk invWalk = Modules.get(InventoryWalk.class);
      return mc.currentScreen != null && !invWalk.allowWalk()
         ? mc.gameRenderer.getCamera().getPos()
         : mc.gameRenderer
            .getCamera()
            .getPos()
            .add(
               Utils.transformInput(
                  mc.gameRenderer.getCamera().getYaw(),
                  this.forward,
                  this.backward,
                  this.left,
                  this.right,
                  this.up,
                  this.down && (mc.currentScreen == null || invWalk.sneak.get()),
                  this.speed.get() * 0.05F
               )
            );
   }

   @Override
   public void onEnable() {
      mc.chunkCullingEnabled = false;
      mc.worldRenderer.reload();
   }

   @Override
   public void onDisable() {
      mc.chunkCullingEnabled = true;
      if (mc.world != null) {
         mc.worldRenderer.reload();
      }
   }
}
