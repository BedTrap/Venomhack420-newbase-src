package de.tyrannus.venomhack.modules.misc;

import de.tyrannus.venomhack.Venomhack;
import de.tyrannus.venomhack.events.PacketEvent;
import de.tyrannus.venomhack.modules.Module;
import de.tyrannus.venomhack.settings.Setting;
import de.tyrannus.venomhack.utils.ChatUtils;
import java.lang.reflect.Field;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.KeepAliveS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import net.minecraft.network.packet.c2s.play.KeepAliveC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class PacketLogger extends Module {
   private final Setting<Boolean> chat = this.setting("chat", "Whether to log the packets to chat.", Boolean.valueOf(true));
   private final Setting<Boolean> sent = this.setting("send", "Logs packets being sent.", Boolean.valueOf(true));
   private final Setting<Boolean> received = this.setting("receive", "Logs packets being received.", Boolean.valueOf(false));
   private final Setting<Boolean> keepAlive = this.setting("keep-alive", "Whether to log keep alive packets.", Boolean.valueOf(false));
   private final Setting<Boolean> playerMove = this.setting("player-move", "Whether to log player move packets.", Boolean.valueOf(false));

   public PacketLogger() {
      super(Module.Categories.MISC, "packet-logger", "Logs incoming and outgoing packets.");
   }

   @EventHandler
   private void onReceive(PacketEvent.Receive event) {
      if (this.received.get()) {
         Packet<?> packet = event.getPacket();
         if (!packet.getClass().getName().contains("C2S")) {
            if (!(packet instanceof WorldTimeUpdateS2CPacket)) {
               if (!(packet instanceof KeepAliveS2CPacket) || this.keepAlive.get()) {
                  StringBuilder builder = new StringBuilder("Received: ").append(event.getPacket().getClass().getSimpleName()).append(" ");

                  try {
                     for(Field field : packet.getClass().getDeclaredFields()) {
                        field.setAccessible(true);
                        builder.append(field.getName()).append(" ").append(field.get(packet)).append(" ");
                     }
                  } catch (IllegalAccessException var8) {
                     var8.printStackTrace();
                  }

                  if (this.chat.get()) {
                     ChatUtils.sendMsg(builder.toString());
                  } else {
                     Venomhack.LOGGER.info(builder.toString());
                  }
               }
            }
         }
      }
   }

   @EventHandler
   private void onSent(PacketEvent.Sent event) {
      if (this.sent.get()) {
         Packet<?> packet = event.getPacket();
         if (!packet.getClass().getName().contains("S2C")) {
            if (!(packet instanceof PlayerMoveC2SPacket) || this.playerMove.get()) {
               if (!(packet instanceof KeepAliveC2SPacket) || this.keepAlive.get()) {
                  StringBuilder builder = new StringBuilder("Sent: ").append(event.getPacket().getClass().getSimpleName()).append(" ");

                  try {
                     for(Field field : packet.getClass().getDeclaredFields()) {
                        field.setAccessible(true);
                        builder.append(field.getName()).append(" ").append(field.get(packet)).append(" ");
                     }
                  } catch (IllegalAccessException var8) {
                     var8.printStackTrace();
                  }

                  if (this.chat.get()) {
                     ChatUtils.sendMsg(builder.toString());
                  } else {
                     Venomhack.LOGGER.info(builder.toString());
                  }
               }
            }
         }
      }
   }
}
