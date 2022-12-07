package de.tyrannus.venomhack.modules.misc;

import de.tyrannus.venomhack.modules.Module;

public class PacketPlace extends Module {
   public PacketPlace() {
      super(Module.Categories.MISC, "packet-place", "Prevents client-side prediction of placed blocks to prevent ghost blocks.");
   }
}
