package de.tyrannus.venomhack.mixins;

import de.tyrannus.venomhack.Venomhack;
import de.tyrannus.venomhack.events.RenderEvent;
import de.tyrannus.venomhack.gui.HudEditorScreen;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.Immediate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({InGameHud.class})
public class InGameHudMixin {
   @Inject(
      method = {"render"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/gui/hud/InGameHud;renderStatusEffectOverlay(Lnet/minecraft/client/util/math/MatrixStack;)V"
)}
   )
   private void onRender(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
      if (!(Venomhack.mc.currentScreen instanceof HudEditorScreen)) {
         class_4598 immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
         Venomhack.EVENTS.post(RenderEvent.Hud.get(matrices, immediate, tickDelta));
         immediate.draw();
      }
   }
}
