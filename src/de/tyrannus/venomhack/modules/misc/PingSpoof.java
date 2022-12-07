package de.tyrannus.venomhack.modules.misc;

import de.tyrannus.venomhack.events.PacketEvent;
import de.tyrannus.venomhack.modules.Module;
import de.tyrannus.venomhack.settings.Setting;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.KeepAliveC2SPacket;
import net.minecraft.network.packet.c2s.play.ResourcePackStatusC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayPongC2SPacket;

public class PingSpoof extends Module {
   private final Setting<Integer> delay = this.setting("spoof-amount", "Delay in ms to add to your ping.", Integer.valueOf(50), 0.0F, 500.0F);
   private final Setting<Boolean> cc = this.setting("cc-mode", "Makes ping spoof work on crystalpvp.cc.", Boolean.valueOf(false));
   private final ConcurrentHashMap<Packet<?>, Long> packets = new ConcurrentHashMap<>();

   public PingSpoof() {
      super(Module.Categories.MISC, "ping-spoof", "Makes your ping appear higher than it actually is.");
   }

   @EventHandler
   private void onPacketSend(PacketEvent.Send event) {
      if (!mc.isInSingleplayer()) {
         Packet<?> packet = event.getPacket();
         if (packet instanceof KeepAliveC2SPacket || this.cc.get() && (packet instanceof ResourcePackStatusC2SPacket || packet instanceof PlayPongC2SPacket)) {
            synchronized(this.packets) {
               if (this.packets.containsKey(packet)) {
                  this.packets.remove(packet);
                  return;
               }

               this.packets.put(packet, System.currentTimeMillis());
               event.cancel();
            }
         }
      }
   }

   @EventHandler
   private void onPacketRecieve(PacketEvent.Receive event) {
      if (mc.player != null) {
         synchronized(this.packets) {
            for(Entry<Packet<?>, Long> packet : this.packets.entrySet()) {
               if (packet.getValue() + (long)this.delay.get().intValue() <= System.currentTimeMillis()) {
                  sendPacket((Packet<?>)packet.getKey());
               }
            }
         }
      }
   }

   @Override
   protected void onDisable() {
      synchronized(this.packets) {
         for(Entry<Packet<?>, Long> packet : this.packets.entrySet()) {
            if (packet.getValue() + (long)this.delay.get().intValue() <= System.currentTimeMillis()) {
               sendPacket((Packet<?>)packet.getKey());
            }
         }
      }
   }

   @Override
   public String getArrayText() {
      return mc.getNetworkHandler() != null && mc.player != null && mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid()) != null
         ? (int)Math.max(0.0, (double)(mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid()).getLatency() - this.delay.get()) * 0.5) + "ms"
         : "0ms";
   }
}
