package io.github.techtastic.cc_vs.ship

import com.fasterxml.jackson.annotation.JsonIgnore
import io.github.techtastic.cc_vs.PlatformUtils
import io.github.techtastic.cc_vs.apis.LuaPhysShip
import org.valkyrienskies.core.api.ships.*
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl

class PhysTickEventHandler: ShipForcesInducer {
    @JsonIgnore
    private val computers = mutableListOf<Int>()

    override fun applyForces(physShip: PhysShip) {
        if (!PlatformUtils.exposePhysTick())
            return

        val physShip = physShip as PhysShipImpl

        this.computers.removeIf { PlatformUtils.getComputerByID(it) == null }

        this.computers.forEach {
            PlatformUtils.getComputerByID(it)?.queueEvent("physics_tick", arrayOf(LuaPhysShip(physShip)))
        }
    }

    fun addComputer(id: Int) {
        this.computers.add(id)
    }

    companion object {
        fun getOrCreateControl(ship: LoadedServerShip): PhysTickEventHandler {
            var control = ship.getAttachment(PhysTickEventHandler::class.java)
            if (control == null) {
                control = ship.setAttachment(PhysTickEventHandler())
            }

            return control!!
        }
    }
}