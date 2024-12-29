package io.github.techtastic.cc_vs.fabric.mixin;

import dan200.computercraft.shared.computer.blocks.TileComputer;
import dan200.computercraft.shared.computer.core.ServerComputer;
import io.github.techtastic.cc_vs.util.CCVSUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.api.ValkyrienSkies;

@Mixin(TileComputer.class)
public class MixinTileComputer {
    @Inject(
            method = "createComputer",
            at = @At("RETURN"),
            remap = false
    )
    private void cc_vs$addShipAPI(int instanceID, int id, CallbackInfoReturnable<ServerComputer> cir) {
        ServerComputer computer = cir.getReturnValue();
        Level level = computer.getLevel();
        BlockPos pos = computer.getPosition();
        Ship ship = ValkyrienSkies.getShipManagingBlock(level, pos);

        CCVSUtils.INSTANCE.applyShipAPIsToComputer(computer, level, ship);
    }
}
