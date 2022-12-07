package de.tyrannus.venomhack.modules.misc;

import de.tyrannus.venomhack.events.OpenScreenEvent;
import de.tyrannus.venomhack.events.TickEvent;
import de.tyrannus.venomhack.modules.Module;
import de.tyrannus.venomhack.settings.Setting;
import de.tyrannus.venomhack.utils.inventory.InvUtils;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntIterator;
import java.util.List;
import java.util.function.Predicate;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.item.BedItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Recipe;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CraftingScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;

public class AutoCrafter extends Module {
   private final Setting<Integer> delay = this.setting("delay", "How many ticks to wait in between crafting.", Integer.valueOf(2));
   private final Setting<Boolean> close = this.setting("close-on-finished", "Closes the crafting table gui when done.", Boolean.valueOf(true));
   private final Setting<List<Item>> items = this.listSetting("items", "Which items to craft.", new Item[]{Items.PURPLE_BED});
   private final Setting<Boolean> fastUnstackables = this.setting("fast-unstackables", "Speeds up crafting of unstackable items.", Boolean.valueOf(true));
   public final Setting<Integer> maxBeds = this.setting(
      "max-bed-amount",
      "How many beds to get max via crafting.",
      Integer.valueOf(36),
      () -> this.items.get().stream().anyMatch(item -> item instanceof BedItem),
      0.0F,
      36.0F
   );
   private int delayLeft;
   private boolean didAnything;
   private CraftingScreen screen;

   public AutoCrafter() {
      super(Module.Categories.MISC, "auto-crafter", "Crafts items automatically.");
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      CraftingScreen currentScreen = null;
      Screen var4 = mc.currentScreen;
      if (var4 instanceof CraftingScreen c) {
         currentScreen = c;
      }

      if (currentScreen != null) {
         if (this.delayLeft <= 0) {
            CraftingScreenHandler handler = (CraftingScreenHandler)currentScreen.getScreenHandler();

            for(RecipeResultCollection recipeResultCollection : mc.player.getRecipeBook().getOrderedResults()) {
               for(Recipe<?> recipe : recipeResultCollection.getRecipes(true)) {
                  Item item = recipe.getOutput().getItem();
                  if (this.items.get().contains(item)) {
                     boolean isBed = recipe.getOutput().getItem() instanceof BedItem;
                     int emptySlots = InvUtils.countEmptySlots();
                     int bedCount = InvUtils.count((Predicate<Item>)(item1 -> item1 instanceof BedItem));
                     if (emptySlots > 0 && (!isBed || bedCount < this.maxBeds.get())) {
                        if (this.fastUnstackables.get() && !item.getDefaultStack().isStackable()) {
                           if (this.delayLeft == this.delay.get()) {
                              --this.delayLeft;
                              return;
                           }

                           for(int i = 0; i < (isBed ? Math.min(this.maxBeds.get() - bedCount, emptySlots) : emptySlots); ++i) {
                              mc.interactionManager.clickRecipe(handler.syncId, recipe, false);
                           }
                        } else {
                           mc.interactionManager.clickRecipe(handler.syncId, recipe, item.getDefaultStack().isStackable());
                        }

                        mc.interactionManager.clickSlot(handler.syncId, 0, 1, SlotActionType.QUICK_MOVE, mc.player);
                        mc.player.getInventory().updateItems();
                        this.didAnything = true;
                        this.delayLeft = this.delay.get();
                        break;
                     }

                     if (this.didAnything) {
                        this.close(handler);
                     }
                  }
               }
            }

            if (this.didAnything && this.delayLeft <= 0 && this.noBedMaterials()) {
               this.close(handler);
            }
         } else {
            --this.delayLeft;
         }
      }
   }

   @EventHandler
   private void onOpenScreen(OpenScreenEvent event) {
      Screen var3 = event.screen;
      if (var3 instanceof CraftingScreen c) {
         this.screen = c;
      }
   }

   public boolean noBedMaterials() {
      Int2IntOpenHashMap colours = new Int2IntOpenHashMap();
      short planks = 0;

      for(int i = 0; i < 36; ++i) {
         ItemStack stack = mc.player.getInventory().getStack(i);
         Item item = stack.getItem();
         Block block = Block.getBlockFromItem(item);
         if (block.getDefaultState().getMaterial().equals(Material.WOOL)) {
            int color = block.getDefaultMapColor().id;

            for(Item item2 : this.items.get()) {
               if (item2 instanceof BedItem && Block.getBlockFromItem(item2).getDefaultMapColor().id == color) {
                  if (planks > 2 && stack.getCount() > 2) {
                     return false;
                  }

                  colours.put(color, colours.getOrDefault(color, 0) + stack.getCount());
                  break;
               }
            }
         } else if (planks < 3
            && (stack.getItem().getName().getString().contains("Planks") || stack.getItem().getName().getString().contains("bretter"))) {
            planks = (short)(planks + stack.getCount());
         }
      }

      if (planks > 2) {
         IntIterator var10 = colours.values().iterator();

         while(var10.hasNext()) {
            Integer woolCount = (Integer)var10.next();
            if (woolCount > 2) {
               return false;
            }
         }
      }

      return true;
   }

   private void close(ScreenHandler handler) {
      if (this.close.get()) {
         mc.player.closeHandledScreen();
         this.screen = null;
         this.didAnything = false;
         mc.player.getInventory().updateItems();
      }
   }

   @Override
   public void onEnable() {
      this.delayLeft = 0;
      this.screen = null;
      this.didAnything = false;
   }
}
