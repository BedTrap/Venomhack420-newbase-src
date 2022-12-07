package de.tyrannus.venomhack.modules.render.hud.elements;

import de.tyrannus.venomhack.events.PacketEvent;
import de.tyrannus.venomhack.utils.MathUtil;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import net.minecraft.util.math.MathHelper;

public class TpsHud extends TextHudElement {
   private long lastTickTime;
   private long timeDelta;

   public TpsHud() {
      super("tps-hud", "Displays the server's current ticks per second", "TPS: ", "20", 60, 20);
   }

   @EventHandler
   private void onPacketReceive(PacketEvent.Receive event) {
      if (event.getPacket() instanceof GameJoinS2CPacket) {
         this.lastTickTime = System.currentTimeMillis();
      } else if (event.getPacket() instanceof WorldTimeUpdateS2CPacket) {
         this.timeDelta = System.currentTimeMillis() - this.lastTickTime;
         this.lastTickTime = System.currentTimeMillis();
      }
   }

   @Override
   public String getRightText() {
      return String.valueOf(MathHelper.clamp(MathUtil.round(20000.0F / (float)this.timeDelta, 1), 0.0, 20.0));
   }
}
