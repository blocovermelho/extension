package org.blocovermelho.bvextension.mixin;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.mob.EndermanEntity;
import org.blocovermelho.bvextension.Settings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EndermanEntity.class)
public abstract class DisableEndermanGriefing {
    @Redirect(method = "initGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/goal/GoalSelector;add(ILnet/minecraft/entity/ai/goal/Goal;)V", ordinal = 7))
    public void bvext$noop_endermanGriefingPick(GoalSelector instance, int priority, Goal goal) {
        if(Settings.disableEndermanGriefing) {
            return;
        } else {
            instance.add(priority, goal);
        }
    }

    @Redirect(method = "initGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/goal/GoalSelector;add(ILnet/minecraft/entity/ai/goal/Goal;)V", ordinal = 6))
    public void bvext$noop_endermanGriefingPlace(GoalSelector instance, int priority, Goal goal) {
        if(Settings.disableEndermanGriefing) {
            return;
        } else {
            instance.add(priority, goal);
        }
    }
}
