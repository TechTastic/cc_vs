package io.github.techtastic.cc_vs.util

import dan200.computercraft.shared.computer.core.ComputerFamily
import dan200.computercraft.shared.computer.core.ServerComputer
import io.github.techtastic.cc_vs.CCVSMod
import io.github.techtastic.cc_vs.PlatformUtils
import io.github.techtastic.cc_vs.apis.ExtendedShipAPI
import io.github.techtastic.cc_vs.apis.ShipAPI
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import org.joml.Matrix3dc
import org.joml.Quaterniondc
import org.joml.Vector3d
import org.joml.Vector3dc
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.mod.common.getShipManagingPos

object CCVSUtils {
    fun applyShipAPIsToComputer(computer: ServerComputer, level: ServerLevel, ship: ServerShip?) {
        if (ship == null)
            return

        if (!PlatformUtils.isCommandOnly() || computer.family == ComputerFamily.COMMAND)
            computer.addAPI(ExtendedShipAPI(computer.apiEnvironment, ship, level))
        else
            computer.addAPI(ShipAPI(ship))
    }

    fun vectorToTable(vec: Vector3dc): Map<String, Double> = mapOf(
        Pair("x", vec.x()),
        Pair("y", vec.y()),
        Pair("z", vec.z())
    )

    fun quatToTable(q: Quaterniondc): Map<String, Double> = mapOf(
        Pair("x", q.x()),
        Pair("y", q.y()),
        Pair("z", q.z()),
        Pair("w", q.w())
    )

    fun momentToTable(moment: Matrix3dc): List<List<Double>> {
        val tensor: MutableList<List<Double>> = mutableListOf()

        for (i in 0..2) {
            val row = moment.getRow(i, Vector3d())
            tensor.add(i, listOf(row.x, row.y, row.z))
        }

        return tensor
    }

    fun getShip(level: ServerLevel, pos: BlockPos) = level.getShipManagingPos(pos)

    fun getComputerByID(id: Int) = CCVSMod.context.registry()[id]
}