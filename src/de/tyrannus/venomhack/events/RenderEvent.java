package de.tyrannus.venomhack.events;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.Immediate;

public class RenderEvent {
   protected MatrixStack matrices;
   protected float tickDelta;

   public MatrixStack getMatrices() {
      return this.matrices;
   }

   public float getTickDelta() {
      return this.tickDelta;
   }

   public static class Flat extends RenderEvent {
      private static final RenderEvent.Flat INSTANCE = new RenderEvent.Flat();

      private Flat() {
      }

      public static RenderEvent.Flat get(MatrixStack matrices, float tickDelta) {
         INSTANCE.matrices = matrices;
         INSTANCE.tickDelta = tickDelta;
         return INSTANCE;
      }
   }

   public static class Game extends RenderEvent {
      private static final RenderEvent.Game INSTANCE = new RenderEvent.Game();

      private Game() {
      }

      public static RenderEvent.Game get(MatrixStack matrices) {
         INSTANCE.matrices = matrices;
         return INSTANCE;
      }
   }

   public static class Hud extends RenderEvent {
      private float tickDelta;
      private class_4598 immediate;
      private static final RenderEvent.Hud INSTANCE = new RenderEvent.Hud();

      private Hud() {
      }

      public static RenderEvent.Hud get(MatrixStack matrices, class_4598 immediate, float tickDelta) {
         INSTANCE.matrices = matrices;
         INSTANCE.tickDelta = tickDelta;
         INSTANCE.immediate = immediate;
         return INSTANCE;
      }

      @Override
      public float getTickDelta() {
         return this.tickDelta;
      }

      public class_4598 getImmediate() {
         return this.immediate;
      }
   }
}
