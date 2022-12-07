package de.tyrannus.venomhack.utils.players;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.player.PlayerEntity;

public class Friends {
   public static final List<String> FRIENDS = new ArrayList<>();

   public static boolean isFriend(String name) {
      for(String friend : FRIENDS) {
         if (friend.equals(name)) {
            return true;
         }
      }

      return false;
   }

   public static boolean isFriend(PlayerEntity player) {
      return isFriend(player.getEntityName());
   }

   public static boolean add(String name) {
      return FRIENDS.contains(name) ? false : FRIENDS.add(name);
   }

   public static boolean remove(String name) {
      return FRIENDS.remove(name);
   }

   public static void load() {
      try {
         for(JsonElement jsonFriend : JsonParser.parseReader(new FileReader("venomhack//friends.json")).getAsJsonArray()) {
            FRIENDS.add(jsonFriend.getAsString());
         }
      } catch (FileNotFoundException | JsonIOException var3) {
         var3.printStackTrace();
      }
   }
}
