package de.tyrannus.venomhack.settings.settings;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import de.tyrannus.venomhack.settings.IChange;
import de.tyrannus.venomhack.settings.IVisible;
import java.util.List;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.Identifier;

public class ItemListSetting extends ListSetting<Item> {
   public ItemListSetting(String name, String description, IChange<List<Item>> onChanged, IVisible visible, Item... defaultValue) {
      super(name, description, onChanged, visible, defaultValue);
   }

   public Item transform(JsonElement element) {
      return Item.byRawId(element.getAsInt());
   }

   @Override
   public String storeValue() {
      StringBuilder builder = new StringBuilder();

      for(int i = 0; i < ((List)this.get()).size(); ++i) {
         builder.append(((List)this.get()).get(i));
         if (i < ((List)this.get()).size() - 1) {
            builder.append(", ");
         }
      }

      return builder.toString();
   }

   @Override
   public boolean parseValue(String value) {
      Item item = (Item)Registry.ITEM.get(new Identifier("minecraft:" + value));
      if (item == Items.AIR) {
         return false;
      } else {
         ((List)this.get()).add(item);
         return true;
      }
   }

   @Override
   public Object storeSetting() {
      JsonArray array = new JsonArray();

      for(Item item : (List)this.get()) {
         array.add(Item.getRawId(item));
      }

      return array;
   }
}
