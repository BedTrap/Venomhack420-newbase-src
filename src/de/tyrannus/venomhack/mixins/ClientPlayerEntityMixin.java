package de.tyrannus.venomhack.mixins;

import de.tyrannus.venomhack.Venomhack;
import de.tyrannus.venomhack.commands.Commands;
import de.tyrannus.venomhack.events.PlayerMoveEvent;
import de.tyrannus.venomhack.events.SendMessageEvent;
import de.tyrannus.venomhack.events.TickEvent;
import de.tyrannus.venomhack.modules.Modules;
import de.tyrannus.venomhack.modules.movement.Moses;
import de.tyrannus.venomhack.modules.movement.NoSlow;
import de.tyrannus.venomhack.modules.movement.Sprint;
import de.tyrannus.venomhack.modules.movement.Velocity;
import net.minecraft.text.Text;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ClientPlayerEntity.class})
public abstract class ClientPlayerEntityMixin {
   @Shadow
   public Input input;
   @Unique
   private SendMessageEvent sendMessageEvent;

   @Inject(
      method = {"sendChatMessageInternal"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onMessageSend(String message, Text preview, CallbackInfo ci) {
      if (Commands.executeCommand(message)) {
         ci.cancel();
      }

      this.sendMessageEvent = (SendMessageEvent)Venomhack.EVENTS.post(SendMessageEvent.get(message, false));
      if (this.sendMessageEvent.isCancelled()) {
         ci.cancel();
      }
   }

   @ModifyVariable(
      method = {"sendChatMessageInternal"},
      at = @At("HEAD"),
      argsOnly = true
   )
   private String onMessageSend(String message) {
      return this.sendMessageEvent.getMessage();
   }

   @Inject(
      method = {"sendCommandInternal"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onCommandSend(String command, Text preview, CallbackInfo ci) {
      this.sendMessageEvent = (SendMessageEvent)Venomhack.EVENTS.post(SendMessageEvent.get(command, true));
      if (this.sendMessageEvent.isCancelled()) {
         ci.cancel();
      }
   }

   @ModifyVariable(
      method = {"sendCommandInternal"},
      at = @At("HEAD"),
      argsOnly = true
   )
   private String onCommandSend(String command) {
      return this.sendMessageEvent.getMessage();
   }

   @Inject(
      method = {"tick"},
      at = {@At("HEAD")}
   )
   private void onPreTick(CallbackInfo ci) {
      Venomhack.EVENTS.post(TickEvent.Pre.get());
   }

   @Inject(
      method = {"tick"},
      at = {@At("RETURN")}
   )
   private void onPostTick(CallbackInfo ci) {
      Venomhack.EVENTS.post(TickEvent.Post.get());
   }

   @Inject(
      method = {"tick"},
      at = {@At("HEAD")}
   )
   private void onPreMove(CallbackInfo ci) {
      Venomhack.EVENTS.post(PlayerMoveEvent.Pre.get());
   }

   @Inject(
      method = {"tick"},
      at = {@At("RETURN")}
   )
   private void onPostMove(CallbackInfo ci) {
      Venomhack.EVENTS.post(PlayerMoveEvent.Post.get());
   }

   @Redirect(
      method = {"tickMovement"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/input/Input;hasForwardMovement()Z"
)
   )
   private boolean multiSprint(Input instance) {
      Sprint sprint = Modules.get(Sprint.class);
      return !sprint.isActive() || !sprint.multiDirectional.get() || Venomhack.mc.player.forwardSpeed == 0.0F && Venomhack.mc.player.sidewaysSpeed == 0.0F
         ? this.input.hasForwardMovement()
         : true;
   }

   @Inject(
      method = {"tickMovement"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/tutorial/TutorialManager;onMovement(Lnet/minecraft/client/input/Input;)V"
)}
   )
   private void itemSlowDown(CallbackInfo ci) {
      NoSlow noSlow = Modules.get(NoSlow.class);
      if (noSlow.isActive() && noSlow.items.get() && Venomhack.mc.player.isUsingItem()) {
         this.input.movementSideways *= 5.0F;
         this.input.movementForward *= 5.0F;
      }
   }

   @Inject(
      method = {"pushOutOfBlocks"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void pushOutOfBlocks(CallbackInfo info) {
      Velocity velocity = Modules.get(Velocity.class);
      if (velocity.isActive() && velocity.blocks.get()) {
         info.cancel();
      }
   }

   @Inject(
      method = {"isSubmergedInWater"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void touchGrass(CallbackInfoReturnable<Boolean> cir) {
      if (Modules.isActive(Moses.class)) {
         cir.setReturnValue(false);
      }
   }
}
