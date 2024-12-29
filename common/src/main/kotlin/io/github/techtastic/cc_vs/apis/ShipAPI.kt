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
import org.valkyrienskies.core.apigame.joints.VSJointAndId
import org.valkyrienskies.mod.api.VsApi
import org.valkyrienskies.mod.api.positionToWorld
import org.valkyrienskies.mod.common.shipObjectWorld

open class ShipAPI(open val ship: ServerShip, val level: ServerLevel) : ILuaAPI {
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
    fun getMomentOfInertiaTensor(): List<List<Double>> =
        this.ship.inertiaData.inertiaTensor.toLua()

    @LuaFunction
    fun getName(): String = this.ship.slug ?: "no-name"

    @LuaFunction
    fun getAngularVelocity(): Map<String, Double> =
        this.ship.angularVelocity.toLua()

    @LuaFunction
    fun getQuaternion(): Map<String, Double> =
        this.ship.transform.shipToWorldRotation.toLua()

    @LuaFunction
    fun getEulerAnglesXYZ() =
        this.ship.transform.shipToWorldRotation.getEulerAnglesXYZ(Vector3d()).toLua()

    @LuaFunction
    fun getEulerAnglesYXZ() =
        this.ship.transform.shipToWorldRotation.getEulerAnglesYXZ(Vector3d()).toLua()

    @LuaFunction
    fun getEulerAnglesZXY() =
        this.ship.transform.shipToWorldRotation.getEulerAnglesZXY(Vector3d()).toLua()

    @LuaFunction
    fun getEulerAnglesZYX() =
        this.ship.transform.shipToWorldRotation.getEulerAnglesZYX(Vector3d()).toLua()

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
        this.ship.positionToWorld(Vector3d(x, y, z)).toLua()

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
    fun getJoints(): List<*> {
        val accessor = level.shipObjectWorld as ShipObjectWorldAccessor
        return accessor.shipIdToJoints.getOrDefault(ship.id, setOf()).map { id ->
            accessor.joints[id]?.let { VSJointAndId(id, it).toLua() } }
    }
}