package de.tyrannus.venomhack.events;

import net.minecraft.client.gui.screen.Screen;

public class OpenScreenEvent extends Cancellable {
   private static final OpenScreenEvent INSTANCE = new OpenScreenEvent();
   public Screen screen;

   public static OpenScreenEvent get(Screen screen) {
      INSTANCE.screen = screen;
      INSTANCE.setCancelled(false);
      return INSTANCE;
   }
}
