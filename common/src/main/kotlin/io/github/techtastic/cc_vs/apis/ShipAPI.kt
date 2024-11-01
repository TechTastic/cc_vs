package io.github.techtastic.cc_vs.apis

import dan200.computercraft.api.lua.ILuaAPI
import dan200.computercraft.api.lua.LuaFunction
import io.github.techtastic.cc_vs.mixin.ShipObjectWorldAccessor
import io.github.techtastic.cc_vs.util.CCVSUtils.toLua
import net.minecraft.server.level.ServerLevel
import org.joml.Vector3d
import org.joml.Vector4d
import org.joml.primitives.AABBi
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.apigame.constraints.VSConstraintAndId
import org.valkyrienskies.mod.common.shipObjectWorld
import kotlin.math.asin
import kotlin.math.atan2

open class ShipAPI(val ship: ServerShip, val level: ServerLevel) : ILuaAPI {
    var names: ArrayList<String> = arrayListOf("ship", this.ship.slug ?: "ship")

    override fun getNames(): Array<String> = names.toTypedArray()

    override fun update() {
        names[1] = this.ship.slug ?: "ship"

        super.update()
    }

    @LuaFunction
    fun getId(): Long =
        this.ship.id

    @LuaFunction
    fun getMass(): Double =
        this.ship.inertiaData.mass

    @LuaFunction
    fun getMomentOfInertiaTensorToSave(): List<List<Double>> =
        this.ship.inertiaData.momentOfInertiaTensorToSave.toLua()

    @LuaFunction
    fun getMomentOfInertiaTensor(): List<List<Double>> =
        this.ship.inertiaData.momentOfInertiaTensor.toLua()

    @LuaFunction
    fun getName(): String = this.ship.slug ?: "no-name"

    @LuaFunction
    fun getOmega(): Map<String, Double> =
        this.ship.omega.toLua()

    @LuaFunction
    fun getQuaternion(): Map<String, Double> =
        this.ship.transform.shipToWorldRotation.toLua()

    @LuaFunction
    fun getRoll(): Double {
        val rotMatrix = this.getTransformationMatrix()
        return atan2(rotMatrix[1][0], rotMatrix[1][1])
    }

    @LuaFunction
    fun getYaw(): Double {
        val rotMatrix = this.getTransformationMatrix()
        return atan2(-rotMatrix[0][2], rotMatrix[2][2])
    }

    @LuaFunction
    fun getPitch(): Double =
        -asin(this.getTransformationMatrix()[1][2])

    @LuaFunction
    fun getScale(): Map<String, Double> =
        this.ship.transform.shipToWorldScaling.toLua()

    @LuaFunction
    fun getShipyardPosition(): Map<String, Double> =
        this.ship.transform.positionInShip.toLua()

    @LuaFunction
    fun getSize(): Map<String, Any> {
        val aabb = this.ship.shipAABB ?: AABBi(0, 0, 0, 0, 0, 0)
        return mapOf(
            Pair("x", aabb.maxX() - aabb.minX()),
            Pair("y", aabb.maxY() - aabb.minY()),
            Pair("z", aabb.maxZ() - aabb.minZ())
        )
    }

    @LuaFunction
    fun getVelocity(): Map<String, Double> =
        this.ship.velocity.toLua()

    @LuaFunction
    fun getWorldspacePosition(): Map<String, Double> =
        this.ship.transform.positionInWorld.toLua()

    @LuaFunction
    fun transformPositionToWorld(x: Double, y: Double, z: Double): Map<String, Double> =
        this.ship.shipToWorld.transformPosition(Vector3d(x, y, z)).toLua()

    @LuaFunction
    fun isStatic(): Boolean = this.ship.isStatic

    @LuaFunction
    fun setName(name: String) {
        this.ship.slug = name
    }

    @LuaFunction
    fun getTransformationMatrix(): List<List<Double>> {
        val transform = this.ship.transform.shipToWorld
        val matrix: MutableList<List<Double>> = mutableListOf()

        for (i in 0..3) {
            val row = transform.getRow(i, Vector4d())
            matrix.add(i, listOf(row.x, row.y, row.z, row.w))
        }

        return matrix.toList()
    }

    @LuaFunction
    fun getConstraints(): List<*> {
        val accessor = level.shipObjectWorld as ShipObjectWorldAccessor
        return accessor.shipIdToConstraints.getOrDefault(ship.id, setOf()).map { id ->
            accessor.constraints[id]?.let { VSConstraintAndId(id, it) }
        }.map { combo -> combo?.toLua() }
    }
}