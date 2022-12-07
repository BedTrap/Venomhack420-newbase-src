package de.tyrannus.venomhack.events;

import net.minecraft.client.network.PlayerListEntry;

public class PlayerListChangeEvent {
   protected PlayerListEntry player;

   public PlayerListEntry getPlayer() {
      return this.player;
   }

   public static class Join extends PlayerListChangeEvent {
      private static final PlayerListChangeEvent.Join INSTANCE = new PlayerListChangeEvent.Join();

      public static PlayerListChangeEvent.Join get(PlayerListEntry player) {
         INSTANCE.player = player;
         return INSTANCE;
      }
   }

   public static class Leave extends PlayerListChangeEvent {
      private static final PlayerListChangeEvent.Leave INSTANCE = new PlayerListChangeEvent.Leave();

      public static PlayerListChangeEvent.Leave get(PlayerListEntry player) {
         INSTANCE.player = player;
         return INSTANCE;
      }
   }
}
