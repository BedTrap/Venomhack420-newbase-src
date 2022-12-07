package de.tyrannus.venomhack.modules.chat;

import com.mojang.blaze3d.systems.RenderSystem;
import de.tyrannus.venomhack.events.TickEvent;
import de.tyrannus.venomhack.modules.Module;
import de.tyrannus.venomhack.settings.Setting;
import de.tyrannus.venomhack.utils.TextUtils;
import de.tyrannus.venomhack.utils.players.Friends;
import de.tyrannus.venomhack.utils.players.PlayerUtils;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import java.util.List;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.Formatting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.GameMode;
import net.minecraft.text.Text;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.toast.Toast.class_369;

public class ArmorMessage extends Module {
   private final Setting<Boolean> friends = this.setting("friends", "Whether to send a notification to friends or not.", Boolean.valueOf(true));
   private final Setting<Integer> threshold = this.setting("durability-threshold", "At what durability to notify.", Integer.valueOf(25), 1.0F, 100.0F);
   private final Setting<Boolean> toastNotification = this.setting(
      "toast", "Will send the notification for your own armor as a toast instead of a chat message.", Boolean.valueOf(false)
   );
   private final Setting<String> message = this.setting(
      "message", "defines the message to send when armor runs low", "Your {piece} {grammar} low on durability! ({percent}%)"
   );
   private final Int2IntMap armor = new Int2IntOpenHashMap();

   public ArmorMessage() {
      super(Module.Categories.CHAT, "armor-message", "Sends a message in chat when your or your friends armor runs low.");
   }

   @EventHandler
   private void onTick(TickEvent.Post event) {
      for(Entity entity : mc.world.getEntities()) {
         if (entity instanceof PlayerEntity player
            && (player == mc.player || this.friends.get() && Friends.isFriend(player))
            && PlayerUtils.getGameMode(player) != GameMode.CREATIVE
            && PlayerUtils.getGameMode(player) != GameMode.SPECTATOR) {
            for(ItemStack itemStack : player.getArmorItems()) {
               if (itemStack != null && !itemStack.isEmpty() && itemStack.isDamageable()) {
                  int index = player.getId();
                  int damage = (int)TextUtils.getDurabilityInPercent(itemStack);
                  String piece = "";
                  String grammar = "";
                  if (itemStack.getItem() instanceof ElytraItem) {
                     index *= 3;
                     piece = "Elytra";
                     grammar = "is";
                  } else {
                     Item slotType = itemStack.getItem();
                     if (slotType instanceof ArmorItem armorItem) {
                        EquipmentSlot slotTypex = armorItem.getSlotType();
                        index *= slotTypex.getArmorStandSlotId();
                        switch(slotTypex) {
                           case FEET:
                              piece = "Boots";
                              grammar = "are";
                              break;
                           case LEGS:
                              piece = "Leggings";
                              grammar = "are";
                              break;
                           case CHEST:
                              piece = "Chestplate";
                              grammar = "is";
                              break;
                           default:
                              piece = "Helmet";
                              grammar = "is";
                        }
                     }
                  }

                  if (!(damage > this.threshold.get() | this.armor.put(index, damage) <= this.threshold.get())) {
                     String msg = this.message
                        .get()
                        .replace("{piece}", piece)
                        .replace("{grammar}", grammar)
                        .replace("{percent}", damage + "")
                        .replace("{player}", player.getEntityName());
                     if (player == mc.player) {
                        if (this.toastNotification.get()) {
                           mc.getToastManager().add(new ArmorMessage.ArmorToast(itemStack, "Your " + piece + " " + grammar + " on " + damage + "%"));
                        } else {
                           this.info(index, Text.literal(msg).formatted(Formatting.YELLOW));
                        }
                     } else {
                        TextUtils.sendNewMessage("msg " + player.getEntityName() + " " + msg, true);
                     }
                  }
               }
            }
         }
      }
   }

   private static record ArmorToast(ItemStack stack, String message) implements Toast {
      public class_369 draw(MatrixStack matrices, ToastManager manager, long startTime) {
         RenderSystem.setShader(GameRenderer::getPositionTexShader);
         RenderSystem.setShaderTexture(0, TEXTURE);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         manager.drawTexture(matrices, 0, 0, 0, 0, this.getWidth(), this.getHeight());
         List<OrderedText> list = ArmorMessage.mc.textRenderer.wrapLines(Text.literal(this.message), 125);
         if (list.isEmpty()) {
            return class_369.HIDE;
         } else {
            ArmorMessage.mc.textRenderer.draw(matrices, Text.literal("Armor Alert"), 30.0F, 7.0F, -256);
            ArmorMessage.mc.textRenderer.draw(matrices, (OrderedText)list.get(0), 30.0F, 18.0F, -1);
            ArmorMessage.mc.getItemRenderer().renderInGui(this.stack, 8, 8);
            return startTime >= 5000L ? class_369.HIDE : class_369.SHOW;
         }
      }
   }
}
