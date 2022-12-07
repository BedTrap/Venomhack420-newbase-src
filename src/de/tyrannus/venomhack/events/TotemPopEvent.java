package de.tyrannus.venomhack.events;

import net.minecraft.entity.Entity;

public class TotemPopEvent {
   private static final TotemPopEvent INSTANCE = new TotemPopEvent();
   private Entity entity;
   private int pops;
   private boolean isTarget;

   public static TotemPopEvent get(Entity entity, int pops, boolean isTarget) {
      INSTANCE.entity = entity;
      INSTANCE.pops = pops;
      INSTANCE.isTarget = isTarget;
      return INSTANCE;
   }

   public Entity getEntity() {
      return this.entity;
   }

   public int getPops() {
      return this.pops;
   }

   public boolean isTarget() {
      return this.isTarget;
   }
}
