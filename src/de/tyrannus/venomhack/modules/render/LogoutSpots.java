package de.tyrannus.venomhack.modules.render;

import de.tyrannus.venomhack.events.PlayerListChangeEvent;
import de.tyrannus.venomhack.events.RenderEvent;
import de.tyrannus.venomhack.events.TickEvent;
import de.tyrannus.venomhack.modules.Module;
import de.tyrannus.venomhack.settings.Setting;
import de.tyrannus.venomhack.utils.render.RenderMode;
import de.tyrannus.venomhack.utils.render.RenderUtils;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.util.registry.RegistryKey;

public class LogoutSpots extends Module {
   private final Setting<Boolean> chatMsg = this.setting("chat-msg", "Sends chat messages about the player's logout.", Boolean.valueOf(false));
   private final Setting<RenderMode> renderMode = this.setting("render-mode", "How the logout spot is rendered.", RenderMode.BOTH);
   private final Setting<Float> lineWidth = this.setting("line-width", "The line width.", Float.valueOf(1.5F));
   private final Setting<Color> lineColor = this.setting("line-color", "The line color.", new Color(255, 0, 0, 255));
   private final Setting<Color> sideColor = this.setting("side-color", "The side color.", new Color(255, 0, 0, 100));
   private final Setting<Color> txtColor = this.setting("text-color", "The text color.", new Color(255, 255, 255, 255));
   private final Setting<Color> bgColor = this.setting("background-color", "The background color.", new Color(0, 0, 0, 60));
   private final List<LogoutSpots.Spot> logoutSpots = new ArrayList<>();
   private final List<PlayerEntity> players = new ArrayList();

   public LogoutSpots() {
      super(Module.Categories.RENDER, "logout-spots", "Renders where players logged out.");
   }

   @EventHandler
   private void onTick(TickEvent.Post event) {
      this.players.clear();

      for(Entity entity : mc.world.getEntities()) {
         if (entity instanceof PlayerEntity player && player != mc.player) {
            this.players.add(player);
         }
      }
   }

   @EventHandler
   private void onLog(PlayerListChangeEvent.Leave event) {
      for(PlayerEntity player : this.players) {
         if (player.getUuid().equals(event.getPlayer().getProfile().getId())) {
            this.logoutSpots.add(new LogoutSpots.Spot(player, mc.world.getRegistryKey()));
            if (this.chatMsg.get()) {
               this.info(
                  player.getGameProfile().getName()
                     + " logged out at  X: "
                     + player.getBlockX()
                     + " Y: "
                     + player.getBlockY()
                     + " Z: "
                     + player.getBlockZ()
                     + " in the "
                     + this.dim(mc.world.getRegistryKey())
                     + "."
               );
            }

            return;
         }
      }
   }

   @EventHandler
   private void onRejoin(PlayerListChangeEvent.Join event) {
      for(LogoutSpots.Spot spot : this.logoutSpots) {
         if (spot.player().getUuid().equals(event.getPlayer().getProfile().getId())) {
            this.logoutSpots.remove(spot);
            if (!this.chatMsg.get()) {
               return;
            }

            this.info(
               spot.player().getGameProfile().getName()
                  + " logged back in at X: "
                  + spot.player().getBlockX()
                  + " Y: "
                  + spot.player().getBlockY()
                  + " Z: "
                  + spot.player().getBlockZ()
                  + " in the "
                  + this.dim(spot.dimension())
                  + ", removing their logout spot. Looting time."
            );
            return;
         }
      }
   }

   @EventHandler
   private void onRender(RenderEvent.Flat event) {
      if (mc.world != null) {
         for(LogoutSpots.Spot spot : this.logoutSpots) {
            if (mc.world.getRegistryKey() == spot.dimension()) {
               RenderUtils.drawEntityBox(event, spot.player(), this.renderMode.get(), this.lineWidth.get(), this.lineColor.get(), this.sideColor.get(), false);
               RenderUtils.drawEntityTag(event, spot.player(), this.txtColor.get(), this.bgColor.get());
            }
         }
      }
   }

   @Override
   public void onDisable() {
      this.logoutSpots.clear();
      this.players.clear();
   }

   @Override
   public String getArrayText() {
      int i = 0;

      for(LogoutSpots.Spot spot : this.logoutSpots) {
         if (spot.dimension == mc.world.getRegistryKey()) {
            ++i;
         }
      }

      return Integer.toString(i);
   }

   private String dim(RegistryKey<World> registryKey) {
      return registryKey.getValue().getPath();
   }

   public static record Spot(PlayerEntity player, RegistryKey<World> dimension) {
   }
}
