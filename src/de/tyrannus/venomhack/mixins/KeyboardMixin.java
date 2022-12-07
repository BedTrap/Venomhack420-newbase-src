package de.tyrannus.venomhack.mixins;

import de.tyrannus.venomhack.Venomhack;
import de.tyrannus.venomhack.events.KeyEvent;
import net.minecraft.client.Keyboard;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Keyboard.class})
public class KeyboardMixin {
   @Inject(
      method = {"onKey"},
      at = {@At("HEAD")}
   )
   private void onKeyHead(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
      Venomhack.EVENTS.post(KeyEvent.Pre.get(InputUtil.fromKeyCode(key, scancode), action, false));
   }

   @Inject(
      method = {"onKey"},
      at = {@At("RETURN")}
   )
   private void onKeyReturn(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
      Venomhack.EVENTS.post(KeyEvent.Post.get(InputUtil.fromKeyCode(key, scancode), action, false));
   }
}
