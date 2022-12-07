package de.tyrannus.venomhack.events;

import net.minecraft.entity.player.PlayerEntity;

public class PlayerDeathEvent {
   private static final PlayerDeathEvent INSTANCE = new PlayerDeathEvent();
   private PlayerEntity player;
   private int pops;
   private boolean isTarget;
   private boolean isChat;

   public static PlayerDeathEvent get(PlayerEntity player, int pops, boolean isTarget, boolean isChat) {
      INSTANCE.player = player;
      INSTANCE.pops = pops;
      INSTANCE.isTarget = isTarget;
      INSTANCE.isChat = isChat;
      return INSTANCE;
   }

   public PlayerEntity getPlayer() {
      return this.player;
   }

   public int getPops() {
      return this.pops;
   }

   public boolean isTarget() {
      return this.isTarget;
   }

   public boolean isChat() {
      return this.isChat;
   }
}
