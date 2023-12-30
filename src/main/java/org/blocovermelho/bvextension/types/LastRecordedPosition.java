package org.blocovermelho.bvextension.types;

import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public record LastRecordedPosition(BlockPos pos , RegistryKey<World> world, float yaw, float pitch) {
}
