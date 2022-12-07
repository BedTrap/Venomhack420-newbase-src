package de.tyrannus.venomhack.events;

import net.minecraft.network.Packet;

public class PacketEvent extends Cancellable {
   protected Packet<?> packet;

   private PacketEvent() {
   }

   public Packet<?> getPacket() {
      return this.packet;
   }

   public static class Receive extends PacketEvent {
      private static final PacketEvent.Receive INSTANCE = new PacketEvent.Receive();

      private Receive() {
      }

      public static PacketEvent.Receive get(Packet<?> packet) {
         INSTANCE.setCancelled(false);
         INSTANCE.packet = packet;
         return INSTANCE;
      }
   }

   public static class Send extends PacketEvent {
      private static final PacketEvent.Send INSTANCE = new PacketEvent.Send();

      private Send() {
      }

      public static PacketEvent.Send get(Packet<?> packet) {
         INSTANCE.setCancelled(false);
         INSTANCE.packet = packet;
         return INSTANCE;
      }
   }

   public static class Sent extends PacketEvent {
      private static final PacketEvent.Sent INSTANCE = new PacketEvent.Sent();

      private Sent() {
      }

      public static PacketEvent.Sent get(Packet<?> packet) {
         INSTANCE.packet = packet;
         return INSTANCE;
      }
   }
}
