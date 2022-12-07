package de.tyrannus.venomhack.modules.render;

import de.tyrannus.venomhack.modules.Module;
import de.tyrannus.venomhack.settings.Setting;

public class ViewModel extends Module {
   public final Setting<Float> scaleXMain = this.setting(
      "scale-x-main", "The X scale of the items rendered in your main hand.", Float.valueOf(1.0F), 0.0F, 2.0F
   );
   public final Setting<Float> scaleYMain = this.setting(
      "scale-y-main", "The Y scale of the items rendered in your main hand.", Float.valueOf(1.0F), 0.0F, 2.0F
   );
   public final Setting<Float> scaleZMain = this.setting(
      "scale-z-main", "The Z scale of the items rendered in your main hand.", Float.valueOf(1.0F), 0.0F, 2.0F
   );
   public final Setting<Float> posXMain = this.setting("pos-x-main", "The X offset of your main hand.", Float.valueOf(0.0F), -2.0F, 2.0F);
   public final Setting<Float> posYMain = this.setting("pos-y-main", "The Y offset of your main hand.", Float.valueOf(0.0F), -2.0F, 2.0F);
   public final Setting<Float> posZMain = this.setting("pos-z-main", "The Z offset of your main hand.", Float.valueOf(0.0F), -2.0F, 2.0F);
   public final Setting<Integer> rotationXMain = this.setting("rotation-x-main", "The X rotation of your main hand.", Integer.valueOf(0), 0.0F, 360.0F);
   public final Setting<Integer> rotationYMain = this.setting("rotation-y-main", "The Y rotation of your main hand.", Integer.valueOf(0), 0.0F, 360.0F);
   public final Setting<Integer> rotationZMain = this.setting("rotation-z-main", "The Z rotation of your main hand.", Integer.valueOf(0), 0.0F, 360.0F);
   public final Setting<Float> scaleXOff = this.setting("scale-x-off", "The X scale of the items rendered in your offhand.", Float.valueOf(1.0F), 0.0F, 2.0F);
   public final Setting<Float> scaleYOff = this.setting("scale-y-off", "The Y scale of the items rendered in your offhand.", Float.valueOf(1.0F), 0.0F, 2.0F);
   public final Setting<Float> scaleZOff = this.setting("scale-z-off", "The Z scale of the items rendered in your offhand.", Float.valueOf(1.0F), 0.0F, 2.0F);
   public final Setting<Float> posXOff = this.setting("pos-x-off", "The X offset of your offhand.", Float.valueOf(0.0F), -2.0F, 2.0F);
   public final Setting<Float> posYOff = this.setting("pos-y-off", "The Y offset of your offhand.", Float.valueOf(0.0F), -2.0F, 2.0F);
   public final Setting<Float> posZOff = this.setting("pos-z-off", "The Z offset of your offhand.", Float.valueOf(0.0F), -2.0F, 2.0F);
   public final Setting<Integer> rotationXOff = this.setting("rotation-x-off", "The X rotation of your offhand.", Integer.valueOf(0), 0.0F, 360.0F);
   public final Setting<Integer> rotationYOff = this.setting("rotation-y-off", "The Y rotation of your offhand.", Integer.valueOf(0), 0.0F, 360.0F);
   public final Setting<Integer> rotationZOff = this.setting("rotation-z-off", "The Z rotation of your offhand.", Integer.valueOf(0), 0.0F, 360.0F);
   public final Setting<Float> mainSwing = this.setting("main-swing", "Static swing amount for the main hand. 0 to disable.", Float.valueOf(0.0F), 0.0F, 1.0F);
   public final Setting<Float> offSwing = this.setting("off-swing", "Static swing amount for the offhand. 0 to disable.", Float.valueOf(0.0F), 0.0F, 1.0F);
   public final Setting<Boolean> eatFix = this.setting("eat-fix", "\"fixes\" eating the animation.", Boolean.valueOf(false));

   public ViewModel() {
      super(Module.Categories.RENDER, "view-model", "Changes how items in your hands get displayed.");
   }
}
