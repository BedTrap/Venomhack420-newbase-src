package de.tyrannus.venomhack.events;

import meteordevelopment.orbit.ICancellable;

public class Cancellable implements ICancellable {
   private boolean cancelled;

   public void setCancelled(boolean cancelled) {
      this.cancelled = cancelled;
   }

   public boolean isCancelled() {
      return this.cancelled;
   }
}
