package org.blocovermelho.extension.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.monster.EnderMan;
import org.blocovermelho.extension.Settings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EnderMan.class)
public class DisableEndermanGreifing {
    @WrapOperation(method= "registerGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/goal/GoalSelector;addGoal(ILnet/minecraft/world/entity/ai/goal/Goal;)V", ordinal = 6))
    void bvext$noop_endermanGriedingPlace (GoalSelector instance, int i, Goal goal, Operation<Void> original) {
        if (!Settings.disableEndermanGriefing) {
            return;
        }

        original.call(instance, i, goal);
    }

    @WrapOperation(method= "registerGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/goal/GoalSelector;addGoal(ILnet/minecraft/world/entity/ai/goal/Goal;)V", ordinal = 7))
    void bvext$noop_endermanGriedingPick (GoalSelector instance, int i, Goal goal, Operation<Void> original) {
        if (!Settings.disableEndermanGriefing) {
            return;
        }

        original.call(instance, i, goal);
    }
}
