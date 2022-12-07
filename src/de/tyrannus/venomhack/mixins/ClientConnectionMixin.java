package de.tyrannus.venomhack.mixins;

import de.tyrannus.venomhack.Venomhack;
import de.tyrannus.venomhack.events.PacketEvent;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ClientConnection.class})
public class ClientConnectionMixin {
   @Inject(
      method = {"send(Lnet/minecraft/network/Packet;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void send(Packet<?> packet, CallbackInfo ci) {
      if (((PacketEvent.Send)Venomhack.EVENTS.post(PacketEvent.Send.get(packet))).isCancelled()) {
         ci.cancel();
      }
   }

   @Inject(
      method = {"send(Lnet/minecraft/network/Packet;)V"},
      at = {@At("TAIL")}
   )
   private void sent(Packet<?> packet, CallbackInfo ci) {
      Venomhack.EVENTS.post(PacketEvent.Sent.get(packet));
   }

   @Inject(
      method = {"handlePacket"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static <T extends PacketListener> void receive(Packet<T> packet, PacketListener listener, CallbackInfo ci) {
      if (((PacketEvent.Receive)Venomhack.EVENTS.post(PacketEvent.Receive.get(packet))).isCancelled()) {
         ci.cancel();
      }
   }
}
