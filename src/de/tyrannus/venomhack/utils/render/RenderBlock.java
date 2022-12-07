package de.tyrannus.venomhack.utils.render;

import java.util.List;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.BlockState;

public class RenderBlock {
   public BlockPos pos;
   public float ticks;
   public float damage;
   public float selfDamage;
   private BlockState state;
   protected int maxTicks;

   public static synchronized void addRenderBlock(List<RenderBlock> renderBlocks, BlockPos pos, int renderTime) {
      addRenderBlock(renderBlocks, pos, renderTime, 100, 0.0F, 0.0F, null);
   }

   public static synchronized void addRenderBlock(List<RenderBlock> renderBlocks, BlockPos pos, int renderTime, BlockState state) {
      addRenderBlock(renderBlocks, pos, renderTime, 100, 0.0F, 0.0F, state);
   }

   public static synchronized void addRenderBlock(List<RenderBlock> renderBlocks, BlockPos pos, int renderTime, int maxBlocks, float damage) {
      addRenderBlock(renderBlocks, pos, renderTime, maxBlocks, damage, 0.0F, null);
   }

   public static synchronized void addRenderBlock(
      List<RenderBlock> renderBlocks, BlockPos pos, int renderTime, int maxBlocks, float damage, float selfDamage
   ) {
      addRenderBlock(renderBlocks, pos, renderTime, maxBlocks, damage, selfDamage, null);
   }

   public static synchronized void addRenderBlock(
      List<RenderBlock> renderBlocks, BlockPos pos, int renderTime, int maxBlocks, float damage, float selfDamage, BlockState state
   ) {
      boolean found = false;

      for(RenderBlock block : renderBlocks) {
         if (block.pos.equals(pos)) {
            block.set(renderTime, damage, selfDamage, state);
            found = true;
         }
      }

      if (!found) {
         if (!renderBlocks.isEmpty()) {
            while(renderBlocks.size() >= maxBlocks) {
               renderBlocks.remove(0);
            }
         }

         renderBlocks.add(new RenderBlock(pos, renderTime, damage, selfDamage, state));
      }
   }

   public static synchronized void tick(List<RenderBlock> list) {
      for(RenderBlock block : list) {
         --block.ticks;
      }

      list.removeIf(blockx -> blockx.ticks <= 0.0F);
   }

   public RenderBlock(BlockPos pos, int ticks) {
      this.pos = pos;
      this.ticks = (float)ticks;
      this.maxTicks = ticks;
   }

   public RenderBlock(BlockPos pos, int ticks, float damage) {
      this.pos = pos;
      this.ticks = (float)ticks;
      this.damage = damage;
      this.maxTicks = ticks;
   }

   public RenderBlock(BlockPos pos, int ticks, float damage, float selfDamage, BlockState state) {
      this.pos = pos;
      this.ticks = (float)ticks;
      this.damage = damage;
      this.selfDamage = selfDamage;
      this.state = state;
      this.maxTicks = ticks;
   }

   public synchronized void set(int ticks) {
      this.ticks = (float)ticks;
   }

   public synchronized void set(int ticks, BlockState state) {
      this.ticks = (float)ticks;
      this.state = state;
   }

   public synchronized void set(int ticks, float damage) {
      this.ticks = (float)ticks;
      this.damage = damage;
   }

   public synchronized void set(int ticks, float damage, float selfDamage, BlockState state) {
      this.ticks = (float)ticks;
      this.damage = damage;
      this.selfDamage = selfDamage;
      this.state = state;
   }
}
