package de.tyrannus.venomhack.modules.render;

import de.tyrannus.venomhack.modules.Module;
import de.tyrannus.venomhack.settings.Setting;

public class OldAnimations extends Module {
   public final Setting<Boolean> handSwing = this.setting("hand-swing", "Modify how handswings appear", Boolean.valueOf(true));
   public final Setting<Boolean> arms = this.setting("arms", "Transform arm renders", Boolean.valueOf(false));
   public final Setting<Boolean> ignoreSelf = this.setting("ignore-self", "Will render for yourself.", Boolean.valueOf(false), this.arms::get);
   public final Setting<Float> leftArmPitch = this.setting("left-arm-pitch", "Pitch for left arm", Float.valueOf(0.6662F), this.arms::get, 0.0F, 5.0F);
   public final Setting<Float> rightArmPitch = this.setting("right-arm-pitch", "Pitch for right arm", Float.valueOf(0.6662F), this.arms::get, 0.0F, 5.0F);
   public final Setting<Float> leftArmRoll = this.setting("left-arm-roll", "Roll for left arm", Float.valueOf(0.2312F), this.arms::get, -5.0F, 5.0F);
   public final Setting<Float> rightArmRoll = this.setting("right-arm-roll", "Roll for right arm", Float.valueOf(0.2312F), this.arms::get, -5.0F, 5.0F);

   public OldAnimations() {
      super(Module.Categories.RENDER, "old-animations", "Removes the 1.9 swing progress.");
   }
}
