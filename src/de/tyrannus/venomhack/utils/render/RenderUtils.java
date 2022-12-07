package de.tyrannus.venomhack.utils.render;

import com.mojang.blaze3d.systems.RenderSystem;
import de.tyrannus.venomhack.Venomhack;
import de.tyrannus.venomhack.events.RenderEvent;
import java.awt.Color;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.Camera;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.DrawMode;
import net.minecraft.Immediate;
import org.lwjgl.opengl.GL46;

public class RenderUtils {
   public static void drawEntityBox(RenderEvent event, Entity entity, RenderMode mode, float width, Color lineColor, Color sideColor, boolean chroma) {
      if (mode.lines()) {
         drawBoxLines(event, entity, width, lineColor);
      }

      if (mode.sides()) {
         drawBoxSides(event, entity, sideColor, chroma);
      }
   }

   private static void drawBoxLines(RenderEvent event, Entity entity, float width, Color color) {
      drawBox(RenderMode.LINES, event, entity, width, color, false);
   }

   private static void drawBoxSides(RenderEvent event, Entity entity, Color color, boolean chroma) {
      drawBox(RenderMode.SIDES, event, entity, 0.0F, color, chroma);
   }

   private static void drawBox(RenderMode mode, RenderEvent event, Entity entity, float width, Color color, boolean chroma) {
      Box box = entity.getBoundingBox();
      double xThing = (entity.getX() - entity.lastRenderX) * (double)event.getTickDelta();
      double yThing1 = (entity.getY() - entity.lastRenderY) * (double)event.getTickDelta();
      double yThing2 = box.getYLength() + yThing1;
      double zThing = (entity.getZ() - entity.lastRenderZ) * (double)event.getTickDelta();
      double x1 = entity.lastRenderX - box.getXLength() * 0.5 + xThing;
      double y1 = entity.lastRenderY + yThing1;
      double z1 = entity.lastRenderZ - box.getZLength() * 0.5 + zThing;
      double x2 = entity.lastRenderX + box.getXLength() * 0.5 + xThing;
      double y2 = entity.lastRenderY + yThing2;
      double z2 = entity.lastRenderZ + box.getZLength() * 0.5 + zThing;
      drawBox(mode, x1, y1, z1, x2, y2, z2, width, color, chroma);
   }

   public static void drawBlock(RenderMode mode, BlockPos pos, float width, Color lineColor, Color sideColor, boolean chroma) {
      if (mode.lines()) {
         drawBlock(RenderMode.LINES, pos, width, lineColor, false);
      }

      if (mode.sides()) {
         drawBlock(RenderMode.SIDES, pos, width, sideColor, chroma);
      }
   }

   private static void drawBlock(RenderMode mode, BlockPos pos, float width, Color color, boolean chroma) {
      drawBox(
         mode,
         (double)pos.getX(),
         (double)pos.getY(),
         (double)pos.getZ(),
         (double)(pos.getX() + 1),
         (double)(pos.getY() + 1),
         (double)(pos.getZ() + 1),
         width,
         color,
         chroma
      );
   }

   private static void drawBox(RenderMode mode, double x1, double y1, double z1, double x2, double y2, double z2, float width, Color color, boolean chroma) {
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder buffer = tessellator.getBuffer();
      if (mode == RenderMode.LINES) {
         RenderSystem.setShader(GameRenderer::getRenderTypeLinesShader);
         buffer.begin(class_5596.LINES, VertexFormats.LINES);
         RenderSystem.lineWidth(width);
      } else {
         RenderSystem.setShader(GameRenderer::getPositionColorShader);
         buffer.begin(class_5596.QUADS, VertexFormats.POSITION_COLOR);
      }

      GL46.glDisable(2929);
      RenderSystem.disableCull();
      RenderSystem.disableTexture();
      RenderSystem.enableBlend();
      VenomMatrix4f matrix = new VenomMatrix4f();
      Matrix3f matrix3f = new Matrix3f();
      matrix3f.loadIdentity();
      matrix.loadIdentity();
      Camera camera = Venomhack.mc.gameRenderer.getCamera();
      Quaternion pitchquar = Vec3f.POSITIVE_X.getDegreesQuaternion(camera.getPitch());
      Quaternion yawquar = Vec3f.POSITIVE_Y.getDegreesQuaternion(camera.getYaw() + 180.0F);
      matrix.multiply(pitchquar);
      matrix.multiply(yawquar);
      matrix3f.multiply(pitchquar);
      matrix3f.multiply(yawquar);
      matrix.multiplyByTranslation(-camera.getPos().x, -camera.getPos().y, -camera.getPos().z);
      if (mode == RenderMode.LINES) {
         drawLineBox(matrix, matrix3f, buffer, x1, y1, z1, x2, y2, z2, color);
      } else {
         drawBox(matrix, buffer, x1, y1, z1, x2, y2, z2, color, chroma);
      }

      tessellator.draw();
      GL46.glEnable(2929);
      RenderSystem.enableCull();
      RenderSystem.enableTexture();
      RenderSystem.disableBlend();
   }

   private static void drawBox(
      VenomMatrix4f matrix, VertexConsumer vertexConsumer, double x1, double y1, double z1, double x2, double y2, double z2, Color color, boolean chroma
   ) {
      float red = (float)color.getRed() / 255.0F;
      float green = (float)color.getGreen() / 255.0F;
      float blue = (float)color.getBlue() / 255.0F;
      float alpha = (float)color.getAlpha() / 255.0F;
      drawPlane(vertexConsumer, matrix, x1, y1, z1, x2, y1, z1, x2, y1, z2, x1, y1, z2, red, green, blue, alpha, false);
      drawPlane(vertexConsumer, matrix, x1, y1, z2, x1, y2, z2, x1, y2, z1, x1, y1, z1, red, green, blue, alpha, chroma);
      drawPlane(vertexConsumer, matrix, x2, y1, z1, x2, y2, z1, x2, y2, z2, x2, y1, z2, red, green, blue, alpha, chroma);
      drawPlane(vertexConsumer, matrix, x1, y1, z1, x1, y2, z1, x2, y2, z1, x2, y1, z1, red, green, blue, alpha, chroma);
      drawPlane(vertexConsumer, matrix, x2, y1, z2, x2, y2, z2, x1, y2, z2, x1, y1, z2, red, green, blue, alpha, chroma);
      if (chroma) {
         drawPlane(vertexConsumer, matrix, x1, y2, z2, x2, y2, z2, x2, y2, z1, x1, y2, z1, blue, green, red, alpha, false);
      } else {
         drawPlane(vertexConsumer, matrix, x1, y2, z2, x2, y2, z2, x2, y2, z1, x1, y2, z1, red, green, blue, alpha, false);
      }
   }

   public static void drawLineBox(
      VenomMatrix4f matrix, Matrix3f matrix3f, VertexConsumer vertexConsumer, double x1, double y1, double z1, double x2, double y2, double z2, Color color
   ) {
      int colorInt = color.getRGB();
      VertexUtils.vertex(vertexConsumer, matrix, x1, y1, z1).color(colorInt).normal(matrix3f, 1.0F, 0.0F, 0.0F).next();
      VertexUtils.vertex(vertexConsumer, matrix, x2, y1, z1).color(colorInt).normal(matrix3f, 1.0F, 0.0F, 0.0F).next();
      VertexUtils.vertex(vertexConsumer, matrix, x1, y1, z1).color(colorInt).normal(matrix3f, 0.0F, 1.0F, 0.0F).next();
      VertexUtils.vertex(vertexConsumer, matrix, x1, y2, z1).color(colorInt).normal(matrix3f, 0.0F, 1.0F, 0.0F).next();
      VertexUtils.vertex(vertexConsumer, matrix, x1, y1, z1).color(colorInt).normal(matrix3f, 0.0F, 0.0F, 1.0F).next();
      VertexUtils.vertex(vertexConsumer, matrix, x1, y1, z2).color(colorInt).normal(matrix3f, 0.0F, 0.0F, 1.0F).next();
      VertexUtils.vertex(vertexConsumer, matrix, x2, y1, z1).color(colorInt).normal(matrix3f, 0.0F, 1.0F, 0.0F).next();
      VertexUtils.vertex(vertexConsumer, matrix, x2, y2, z1).color(colorInt).normal(matrix3f, 0.0F, 1.0F, 0.0F).next();
      VertexUtils.vertex(vertexConsumer, matrix, x2, y2, z1).color(colorInt).normal(matrix3f, 1.0F, 0.0F, 0.0F).next();
      VertexUtils.vertex(vertexConsumer, matrix, x1, y2, z1).color(colorInt).normal(matrix3f, 1.0F, 0.0F, 0.0F).next();
      VertexUtils.vertex(vertexConsumer, matrix, x1, y2, z1).color(colorInt).normal(matrix3f, 0.0F, 0.0F, 1.0F).next();
      VertexUtils.vertex(vertexConsumer, matrix, x1, y2, z2).color(colorInt).normal(matrix3f, 0.0F, 0.0F, 1.0F).next();
      VertexUtils.vertex(vertexConsumer, matrix, x1, y2, z2).color(colorInt).normal(matrix3f, 0.0F, 1.0F, 0.0F).next();
      VertexUtils.vertex(vertexConsumer, matrix, x1, y1, z2).color(colorInt).normal(matrix3f, 0.0F, 1.0F, 0.0F).next();
      VertexUtils.vertex(vertexConsumer, matrix, x1, y1, z2).color(colorInt).normal(matrix3f, 1.0F, 0.0F, 0.0F).next();
      VertexUtils.vertex(vertexConsumer, matrix, x2, y1, z2).color(colorInt).normal(matrix3f, 1.0F, 0.0F, 0.0F).next();
      VertexUtils.vertex(vertexConsumer, matrix, x2, y1, z2).color(colorInt).normal(matrix3f, 0.0F, 0.0F, 1.0F).next();
      VertexUtils.vertex(vertexConsumer, matrix, x2, y1, z1).color(colorInt).normal(matrix3f, 0.0F, 0.0F, 1.0F).next();
      VertexUtils.vertex(vertexConsumer, matrix, x1, y2, z2).color(colorInt).normal(matrix3f, 1.0F, 0.0F, 0.0F).next();
      VertexUtils.vertex(vertexConsumer, matrix, x2, y2, z2).color(colorInt).normal(matrix3f, 1.0F, 0.0F, 0.0F).next();
      VertexUtils.vertex(vertexConsumer, matrix, x2, y1, z2).color(colorInt).normal(matrix3f, 0.0F, 1.0F, 0.0F).next();
      VertexUtils.vertex(vertexConsumer, matrix, x2, y2, z2).color(colorInt).normal(matrix3f, 0.0F, 1.0F, 0.0F).next();
      VertexUtils.vertex(vertexConsumer, matrix, x2, y2, z1).color(colorInt).normal(matrix3f, 0.0F, 0.0F, 1.0F).next();
      VertexUtils.vertex(vertexConsumer, matrix, x2, y2, z2).color(colorInt).normal(matrix3f, 0.0F, 0.0F, 1.0F).next();
   }

   private static void drawPlane(
      VertexConsumer vertexConsumer,
      VenomMatrix4f matrices,
      double x1,
      double y1,
      double z1,
      double x2,
      double y2,
      double z2,
      double x3,
      double y3,
      double z3,
      double x4,
      double y4,
      double z4,
      float red,
      float green,
      float blue,
      float alpha,
      boolean chroma
   ) {
      VertexUtils.vertex(vertexConsumer, matrices, x1, y1, z1).color(red, green, blue, alpha).next();
      if (chroma) {
         VertexUtils.vertex(vertexConsumer, matrices, x2, y2, z2).color(blue, green, red, alpha).next();
         VertexUtils.vertex(vertexConsumer, matrices, x3, y3, z3).color(blue, green, red, alpha).next();
      } else {
         VertexUtils.vertex(vertexConsumer, matrices, x2, y2, z2).color(red, green, blue, alpha).next();
         VertexUtils.vertex(vertexConsumer, matrices, x3, y3, z3).color(red, green, blue, alpha).next();
      }

      VertexUtils.vertex(vertexConsumer, matrices, x4, y4, z4).color(red, green, blue, alpha).next();
   }

   public static void drawEntityTag(RenderEvent event, LivingEntity entity, Color txtColor, Color sideColor) {
      MatrixStack matrices = event.getMatrices();
      Camera camera = Venomhack.mc.gameRenderer.getCamera();
      matrices.push();
      matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(camera.getPitch()));
      matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(camera.getYaw() + 180.0F));
      matrices.translate(
         entity.getX() - camera.getPos().x,
         entity.getY() + (double)entity.getHeight() + 0.5 - camera.getPos().y,
         entity.getZ() - camera.getPos().z
      );
      matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-camera.getYaw()));
      matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(camera.getPitch()));
      matrices.scale(-0.025F, -0.025F, 1.0F);
      class_4598 immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
      String text = entity.getEntityName();
      Venomhack.mc
         .textRenderer
         .draw(
            text,
            (float)Venomhack.mc.textRenderer.getWidth(text) * -0.5F,
            0.0F,
            txtColor.getRGB(),
            true,
            matrices.peek().getPositionMatrix(),
            immediate,
            false,
            sideColor.getRGB(),
            15728880,
            false
         );
      immediate.draw();
      matrices.pop();
   }

   public static Color getChromaColor(float alpha, float chromaSpeed) {
      float speed = 10000.0F / chromaSpeed;
      Color hsb = Color.getHSBColor((float)(System.currentTimeMillis() % (long)((int)speed)) / speed, 1.0F, 1.0F);
      return new Color((float)hsb.getRed() / 255.0F, (float)hsb.getGreen() / 255.0F, (float)hsb.getBlue() / 255.0F, alpha / 255.0F);
   }
}
