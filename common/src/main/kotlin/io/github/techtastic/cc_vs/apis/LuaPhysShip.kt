package io.github.techtastic.cc_vs.apis

import dan200.computercraft.api.lua.LuaFunction
import io.github.techtastic.cc_vs.util.CCVSUtils.toLua
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl

class LuaPhysShip(private val physShip: PhysShipImpl) {

    @LuaFunction
    fun getBuoyantFactor() = this.physShip.buoyantFactor

    @LuaFunction
    fun isStatic() = this.physShip.isStatic

    @LuaFunction
    fun doFluidDrag() = this.physShip.doFluidDrag

    @LuaFunction
    fun getMass() = this.physShip.mass

    @LuaFunction
    fun getMomentOfInertia() = this.physShip.momentOfInertia.toLua()

    @LuaFunction
    fun getVelocity() = this.physShip.velocity.toLua()

    @LuaFunction
    fun getOmega() = this.physShip.omega.toLua()

    @LuaFunction
    fun getRotation() = this.physShip.transform.shipToWorldRotation

    @LuaFunction
    fun getCenterOfMassInWorldspace() = this.physShip.transform.positionInWorld

    @LuaFunction
    fun getCenterOfMassInShipyard() = this.physShip.transform.positionInShip

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