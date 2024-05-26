package io.github.techtastic.cc_vs.fabric;

import dan200.computercraft.ComputerCraft;
import dan200.computercraft.shared.computer.core.ServerComputer;
import io.github.techtastic.cc_vs.fabric.config.CCVSConfig;

public class PlatformUtilsImpl {
    public static boolean canTeleport() {
        return CCVSConfig.CAN_TELEPORT.get();
    }

    public static boolean isCommandOnly() {
        return CCVSConfig.COMMAND_ONLY.get();
    }

    public static boolean exposePhysTick() {
        return CCVSConfig.EXPOSE_PHYS_TICK.get();
    }

    public static ServerComputer getComputerByID(int id) {
        return ComputerCraft.serverComputerRegistry.get(id);
    }
}
