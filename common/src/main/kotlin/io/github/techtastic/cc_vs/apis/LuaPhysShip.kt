package io.github.techtastic.cc_vs.apis

import dan200.computercraft.api.lua.LuaFunction
import io.github.techtastic.cc_vs.util.CCVSUtils.momentToTable
import io.github.techtastic.cc_vs.util.CCVSUtils.quatToTable
import io.github.techtastic.cc_vs.util.CCVSUtils.vectorToTable
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl

class LuaPhysShip(private val physShip: PhysShipImpl) {

    @LuaFunction
    fun getBuoyantFactor() = this.physShip.buoyantFactor

    @LuaFunction
    fun isStatic() = this.physShip.isStatic

    @LuaFunction
    fun doFluidDrag() = this.physShip.doFluidDrag

    @LuaFunction
    fun getInertia(): Map<String, Any> {
        val inertia = this.physShip.inertia
        return mapOf(
            Pair("momentOfInertiaTensor", momentToTable(inertia.momentOfInertiaTensor)),
            Pair("mass", inertia.shipMass)
        )
    }

    @LuaFunction
    fun getPoseVel(): Map<String, Map<String, Double>> {
        val poseVel = this.physShip.poseVel
        return mapOf(
            Pair("vel", vectorToTable(poseVel.vel)),
            Pair("omega", vectorToTable(poseVel.omega)),
            Pair("pos", vectorToTable(poseVel.pos)),
            Pair("rot", quatToTable(poseVel.rot))
        )
    }

    @LuaFunction
    fun getForcesInducers(): List<String> {
        val inducers = this.physShip.forceInducers

        val list = mutableListOf<String>()
        inducers.forEach {
            list.add(it.toString())
        }

        return list
    }
}