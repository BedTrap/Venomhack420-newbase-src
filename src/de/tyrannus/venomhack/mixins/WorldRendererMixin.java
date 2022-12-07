package de.tyrannus.venomhack.mixins;

import de.tyrannus.venomhack.Venomhack;
import de.tyrannus.venomhack.events.RenderEvent;
import de.tyrannus.venomhack.modules.Modules;
import de.tyrannus.venomhack.modules.render.BlockOutline;
import de.tyrannus.venomhack.modules.render.NoRender;
import java.awt.Color;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({WorldRenderer.class})
public abstract class WorldRendererMixin {
   @Shadow
   private static void drawCuboidShapeOutline(
      MatrixStack matrices,
      VertexConsumer vertexConsumer,
      VoxelShape shape,
      double offsetX,
      double offsetY,
      double offsetZ,
      float red,
      float green,
      float blue,
      float alpha
   ) {
   }

   @Inject(
      method = {"renderWeather"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onRenderWeather(LightmapTextureManager manager, float f, double d, double e, double g, CallbackInfo ci) {
      NoRender noRender = Modules.get(NoRender.class);
      if (noRender.isActive() && noRender.weather.get()) {
         ci.cancel();
      }
   }

   @Inject(
      method = {"tickRainSplashing"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onTickRainSplashing(Camera camera, CallbackInfo ci) {
      NoRender noRender = Modules.get(NoRender.class);
      if (noRender.isActive() && noRender.weather.get()) {
         ci.cancel();
      }
   }

   @Inject(
      method = {"drawCuboidShapeOutline"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static void onDrawBlockOutline(
      MatrixStack matrices,
      VertexConsumer vertexConsumer,
      VoxelShape shape,
      double offsetX,
      double offsetY,
      double offsetZ,
      float red,
      float green,
      float blue,
      float alpha,
      CallbackInfo ci
   ) {
      BlockOutline blockOutline = Modules.get(BlockOutline.class);
      if (blockOutline.isActive()) {
         if (!blockOutline.vanilla.get()) {
            ci.cancel();
         } else {
            Color color = blockOutline.lineColor.get();
            if (red != (float)color.getRed() / 255.0F || green != (float)color.getGreen() / 255.0F || blue != (float)color.getBlue() / 255.0F) {
               drawCuboidShapeOutline(
                  matrices,
                  vertexConsumer,
                  shape,
                  offsetX,
                  offsetY,
                  offsetZ,
                  (float)color.getRed() / 255.0F,
                  (float)color.getGreen() / 255.0F,
                  (float)color.getBlue() / 255.0F,
                  (float)color.getAlpha() / 255.0F
               );
               ci.cancel();
            }
         }
      }
   }

   @Inject(
      method = {"render"},
      at = {@At("RETURN")}
   )
   private void onRenderPost(
      MatrixStack matrices,
      float tickDelta,
      long limitTime,
      boolean renderBlockOutline,
      Camera camera,
      GameRenderer gameRenderer,
      LightmapTextureManager lightmapTextureManager,
      Matrix4f positionMatrix,
      CallbackInfo ci
   ) {
      Venomhack.EVENTS.post(RenderEvent.Flat.get(new MatrixStack(), tickDelta));
   }
}
