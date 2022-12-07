package de.tyrannus.venomhack.mixins;

import de.tyrannus.venomhack.Venomhack;
import de.tyrannus.venomhack.events.KeyEvent;
import net.minecraft.client.Mouse;
import net.minecraft.client.util.InputUtil.class_307;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Mouse.class})
public class MouseMixin {
   @Inject(
      method = {"onMouseButton"},
      at = {@At("HEAD")}
   )
   private void onMouseButtonHEAD(long window, int button, int action, int mods, CallbackInfo ci) {
      Venomhack.EVENTS.post(KeyEvent.Pre.get(class_307.MOUSE.createFromCode(button), action, true));
   }

   @Inject(
      method = {"onMouseButton"},
      at = {@At("RETURN")}
   )
   private void onMouseButtonReturn(long window, int button, int action, int mods, CallbackInfo ci) {
      Venomhack.EVENTS.post(KeyEvent.Post.get(class_307.MOUSE.createFromCode(button), action, true));
   }
}
