package de.tyrannus.venomhack.mixins;

import de.tyrannus.venomhack.Venomhack;
import de.tyrannus.venomhack.events.AddMessageEvent;
import net.minecraft.text.Text;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.client.gui.hud.MessageIndicator;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ChatHud.class})
public abstract class ChatHudMixin {
   private boolean isNewMsg = true;

   @Shadow
   protected abstract void addMessage(Text var1, @Nullable MessageSignatureData var2, int var3, @Nullable MessageIndicator var4, boolean var5);

   @Inject(
      method = {"addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;ILnet/minecraft/client/gui/hud/MessageIndicator;Z)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onAddMessage(Text message, MessageSignatureData signature, int ticks, MessageIndicator indicator, boolean refresh, CallbackInfo ci) {
      if (this.isNewMsg) {
         AddMessageEvent event = (AddMessageEvent)Venomhack.EVENTS.post(AddMessageEvent.get(message, ticks));
         if (event.isCancelled()) {
            ci.cancel();
         } else if (!message.equals(event.getMessage())) {
            this.isNewMsg = false;
            this.addMessage(event.getMessage(), signature, ticks, indicator, refresh);
            this.isNewMsg = true;
            ci.cancel();
         }
      }
   }
}
