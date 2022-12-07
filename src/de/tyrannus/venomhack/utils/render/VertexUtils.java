package de.tyrannus.venomhack.utils.render;

import net.minecraft.client.render.VertexConsumer;

public class VertexUtils {
   public static VertexConsumer vertex(VertexConsumer vertexConsumer, VenomMatrix4f matrix, double x, double y, double z) {
      VenomVector4f vector4f = new VenomVector4f(x, y, z, 1.0);
      vector4f.transform(matrix);
      return vertexConsumer.vertex(vector4f.getX(), vector4f.getY(), vector4f.getZ());
   }
}
