package org.blocovermelho.extension.types;


import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public record LastRecordedPos(Vec3 pos , ResourceKey<Level> level, float yaw, float pitch) {
}
