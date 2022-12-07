package de.tyrannus.venomhack.modules.movement;

import de.tyrannus.venomhack.events.KeyEvent;
import de.tyrannus.venomhack.events.PlayerMoveEvent;
import de.tyrannus.venomhack.gui.ClickGuiScreen;
import de.tyrannus.venomhack.modules.Module;
import de.tyrannus.venomhack.modules.Modules;
import de.tyrannus.venomhack.modules.render.Freecam;
import de.tyrannus.venomhack.settings.Setting;
import de.tyrannus.venomhack.utils.Utils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.item.ItemGroup;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.gui.screen.ingame.JigsawBlockScreen;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.AnvilScreen;
import net.minecraft.client.gui.screen.ingame.BookEditScreen;
import net.minecraft.client.gui.screen.ingame.CommandBlockScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.StructureBlockScreen;
import net.minecraft.client.gui.screen.ingame.SignEditScreen;

public class InventoryWalk extends Module {
   private final Setting<Boolean> chat = this.setting("chat-walk", "Whether to be able to walk in the chat gui.", Boolean.valueOf(false));
   private final Setting<Boolean> esc = this.setting("esc-walk", "Whether to be able to walk in the escape screen.", Boolean.valueOf(true));
   public final Setting<Boolean> sneak = this.setting("gui-sneak", "Whether to sneak in guis.", Boolean.valueOf(false));
   private final Setting<Boolean> rotate = this.setting("arrow-rotate", "Allows you to rotate your view with arrow keys.", Boolean.valueOf(true));
   private final Setting<Float> rotateSpeed = this.setting(
      "rotate-speed", "The speed at which to rotate when arrow keys are pressed, in degrees.", Float.valueOf(4.0F), this.rotate::get
   );

   public InventoryWalk() {
      super(Module.Categories.MOVEMENT, "inventory-walk", "Allows you to move as normally inside of guis.");
   }

   public boolean allowWalk() {
      if (!this.isActive()) {
         return false;
      } else {
         Screen screen = mc.currentScreen;
         if (screen == null) {
            return false;
         } else if (screen instanceof ChatScreen
            || screen instanceof AnvilScreen
            || screen instanceof BookEditScreen
            || screen instanceof SignEditScreen
            || screen instanceof CommandBlockScreen
            || screen instanceof StructureBlockScreen
            || screen instanceof JigsawBlockScreen) {
            return this.chat.get();
         } else if (screen instanceof CreativeInventoryScreen editScreen) {
            return this.chat.get() || editScreen.getSelectedTab() != ItemGroup.SEARCH.getIndex();
         } else {
            return screen instanceof GameMenuScreen ? this.esc.get() : true;
         }
      }
   }

   @EventHandler
   private void onKey(KeyEvent.Pre event) {
      if (mc.currentScreen != null) {
         if (this.allowWalk()
            && (
               event.matchesBind(mc.options.forwardKey)
                  || event.matchesBind(mc.options.backKey)
                  || event.matchesBind(mc.options.rightKey)
                  || event.matchesBind(mc.options.leftKey)
                  || event.matchesBind(mc.options.jumpKey)
                  || event.matchesBind(mc.options.sprintKey)
                  || event.matchesBind(mc.options.sneakKey) && this.sneak.get()
            )) {
            KeyBinding.setKeyPressed(event.getKey(), event.getAction() != 0);
         }
      }
   }

   @EventHandler
   private void onPlayerMove(PlayerMoveEvent.Pre event) {
      if (this.rotate.get() && this.allowWalk()) {
         boolean freecam = Modules.isActive(Freecam.class);
         float pitch = freecam ? mc.gameRenderer.getCamera().getPitch() : mc.player.getPitch();
         float yaw = freecam ? mc.gameRenderer.getCamera().getYaw() : mc.player.getYaw();
         float pitchIncrement = 0.0F;
         float yawIncrement = 0.0F;
         if (Utils.isKeyPressed(265)) {
            pitchIncrement -= this.rotateSpeed.get();
         }

         if (Utils.isKeyPressed(264)) {
            pitchIncrement += this.rotateSpeed.get();
         }

         if (Utils.isKeyPressed(263)) {
            yawIncrement -= this.rotateSpeed.get();
         }

         if (Utils.isKeyPressed(262)) {
            yawIncrement += this.rotateSpeed.get();
         }

         pitch = MathHelper.clamp(pitch + pitchIncrement, -90.0F, 90.0F);
         yaw += yawIncrement;
         if (freecam) {
            mc.gameRenderer.getCamera().setRotation(yaw, pitch);
         } else {
            mc.player.setYaw(yaw);
            mc.player.setPitch(pitch);
         }
      }
   }

   @Override
   public void onDisable() {
      if (mc.currentScreen instanceof ClickGuiScreen) {
         mc.options.forwardKey.setPressed(false);
         mc.options.backKey.setPressed(false);
         mc.options.rightKey.setPressed(false);
         mc.options.leftKey.setPressed(false);
         mc.options.jumpKey.setPressed(false);
         mc.options.sprintKey.setPressed(false);
         mc.options.sneakKey.setPressed(false);
      }
   }
}
