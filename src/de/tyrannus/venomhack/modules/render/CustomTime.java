package de.tyrannus.venomhack.modules.render;

import de.tyrannus.venomhack.events.PacketEvent;
import de.tyrannus.venomhack.events.TickEvent;
import de.tyrannus.venomhack.modules.Module;
import de.tyrannus.venomhack.settings.Setting;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;

public class CustomTime extends Module {
   private final Setting<Integer> time = this.setting("time", "The time of day.", Integer.valueOf(1000), 0.0F, 24000.0F);

   public CustomTime() {
      super(Module.Categories.RENDER, "custom-time", "Allows you to change the time of your world.");
   }

   @EventHandler
   private void onUpdate(PacketEvent.Receive event) {
      if (event.getPacket() instanceof WorldTimeUpdateS2CPacket) {
         event.cancel();
      }
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      if (mc.world != null) {
         mc.world.setTimeOfDay((long)this.time.get().intValue());
      }
   }

   @Override
   public String getArrayText() {
      return Integer.toString(this.time.get());
   }
}
