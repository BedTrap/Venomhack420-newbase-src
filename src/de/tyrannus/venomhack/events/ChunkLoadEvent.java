package de.tyrannus.venomhack.events;

import net.minecraft.world.chunk.WorldChunk;

public class ChunkLoadEvent {
   private static final ChunkLoadEvent INSTANCE = new ChunkLoadEvent();
   private WorldChunk chunk;

   private ChunkLoadEvent() {
   }

   public static ChunkLoadEvent get(WorldChunk chunk) {
      INSTANCE.chunk = chunk;
      return INSTANCE;
   }

   public WorldChunk getChunk() {
      return this.chunk;
   }
}
