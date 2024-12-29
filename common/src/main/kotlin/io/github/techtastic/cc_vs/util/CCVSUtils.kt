package io.github.techtastic.cc_vs.util

import dan200.computercraft.shared.computer.core.ComputerFamily
import dan200.computercraft.shared.computer.core.ServerComputer
import io.github.techtastic.cc_vs.PlatformUtils
import io.github.techtastic.cc_vs.apis.ExtendedShipAPI
import io.github.techtastic.cc_vs.apis.ShipAPI
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import org.joml.*
import org.valkyrienskies.core.api.ships.LoadedServerShip
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.ships.Ship
import org.valkyrienskies.core.apigame.joints.*

object CCVSUtils {
    fun applyShipAPIsToComputer(computer: ServerComputer, level: Level, ship: Ship?) {
        if (ship !is ServerShip || level !is ServerLevel)
            return

        if ((!PlatformUtils.isCommandOnly() || computer.family == ComputerFamily.COMMAND) && ship is LoadedServerShip)
            computer.addAPI(ExtendedShipAPI(computer.apiEnvironment, ship, level))
        else
            computer.addAPI(ShipAPI(ship, level))
    }

    fun Vector3dc.toLua() = mapOf(
        Pair("x", this.x()),
        Pair("y", this.y()),
        Pair("z", this.z())
    )

    fun Vector3fc.toLua() = mapOf(
        Pair("x", this.x().toDouble()),
        Pair("y", this.y().toDouble()),
        Pair("z", this.z().toDouble())
    )

    fun Quaterniondc.toLua() = mapOf(
        Pair("x", this.x()),
        Pair("y", this.y()),
        Pair("z", this.z()),
        Pair("w", this.w())
    )

    fun Matrix3dc.toLua(): List<List<Double>> {
        val tensor: MutableList<List<Double>> = mutableListOf()

        for (i in 0..2) {
            val row = this.getRow(i, Vector3d())
            tensor.add(i, listOf(row.x, row.y, row.z))
        }

        return tensor
    }

    fun VSJointAndId.toLua() = mapOf(
        "id" to this.jointId,
        "joint" to this.joint.toLua()
    )

    fun VSJointPose.toLua() = mapOf(
        "pos" to this.pos.toLua(),
        "rot" to this.rot.toLua()
    )

    fun VSJointMaxForceTorque.toLua() = mapOf(
        "maxForce" to this.maxForce,
        "maxTorque" to this.maxTorque
    )

    fun VSRevoluteJoint.VSRevoluteDriveVelocity.toLua() = mapOf(
        "velocity" to this.velocity,
        "autoWake" to this.autoWake
    )

    fun VSRackAndPinionJoint.VSRackAndPinionJointData.toLua() = mapOf(
        "rackTeeth" to this.rackTeeth,
        "pinionTeeth" to this.pinionTeeth,
        "rackLength" to this.rackLength.toDouble()
    )

    fun VSD6Joint.LinearLimitPair.toLua() = mapOf(
        "lowerLimit" to this.lowerLimit.toDouble(),
        "upperLimit" to this.upperLimit.toDouble(),
        "restitution" to this.restitution?.toDouble(),
        "bounceThreshold" to this.bounceThreshold?.toDouble(),
        "stiffness" to this.stiffness?.toDouble(),
        "damping" to this.damping?.toDouble()
    )

    fun VSD6Joint.AngularLimitPair.toLua() = mapOf(
        "lowerLimit" to this.lowerLimit.toDouble(),
        "upperLimit" to this.upperLimit.toDouble(),
        "restitution" to this.restitution?.toDouble(),
        "bounceThreshold" to this.bounceThreshold?.toDouble(),
        "stiffness" to this.stiffness?.toDouble(),
        "damping" to this.damping?.toDouble()
    )

    fun VSD6Joint.LimitCone.toLua() = mapOf(
        "yLimitAngle" to this.yLimitAngle.toDouble(),
        "zLimitAngle" to this.zLimitAngle.toDouble(),
        "restitution" to this.restitution?.toDouble(),
        "bounceThreshold" to this.bounceThreshold?.toDouble(),
        "stiffness" to this.stiffness?.toDouble(),
        "damping" to this.damping?.toDouble()
    )

    fun VSD6Joint.Hinges.toLua() = mapOf(
        "hinge0" to this.hinge0,
        "hinge1" to this.hinge1
    )

    fun VSD6Joint.LinearLimit.toLua() = mapOf(
        "extent" to this.extent.toDouble(),
        "stiffness" to this.stiffness?.toDouble(),
        "damping" to this.damping?.toDouble()
    )

    fun VSD6Joint.LimitPyramid.toLua() = mapOf(
        "yLimitAngleMin" to this.yLimitAngleMin.toDouble(),
        "yLimitAngleMax" to this.yLimitAngleMax.toDouble(),
        "zLimitAngleMin" to this.zLimitAngleMin.toDouble(),
        "zLimitAngleMax" to this.zLimitAngleMax.toDouble(),
        "restitution" to this.restitution?.toDouble(),
        "bounceThreshold" to this.bounceThreshold?.toDouble(),
        "stiffness" to this.stiffness?.toDouble(),
        "damping" to this.damping?.toDouble()
    )

    fun VSD6Joint.D6JointDrive.toLua() = mapOf(
        "driveStiffness" to this.driveStiffness.toDouble(),
        "driveDamping" to this.driveDamping.toDouble(),
        "driveForceLimit" to this.driveForceLimit.toDouble(),
        "isAcceleration" to this.isAcceleration
    )

    fun VSD6Joint.DrivePosition.toLua() = mapOf(
        "pose" to this.pose.toLua(),
        "autoWake" to this.autoWake
    )

    fun VSD6Joint.DriveVelocity.toLua() = mapOf(
        "linear" to this.linear.toLua(),
        "angular" to this.angular.toLua(),
        "autoWake" to this.autoWake
    )

    fun VSJoint.toLua(): Map<String, Any?> {
        val joint = mutableMapOf(
            "shipId0" to this.shipId0,
            "pose0" to this.pose0.toLua(),
            "shipId1" to this.shipId1,
            "pose1" to this.pose1.toLua(),
            "maxTorqueForce" to this.maxForceTorque,
            "type" to this.jointType.name
        )

        when (this) {
            is VSDistanceJoint -> {
                joint["minDistance"] = this.minDistance?.toDouble()
                joint["maxDistance"] = this.maxDistance?.toDouble()
                joint["tolerance"] = this.tolerance?.toDouble()
                joint["stiffness"] = this.stiffness?.toDouble()
                joint["damping"] = this.damping?.toDouble()
            }

            is VSPrismaticJoint -> {
                joint["linearLimitPair"] = this.linearLimitPair?.toLua()
            }

            is VSSphericalJoint -> {
                joint["limitCone"] = this.limitCone?.toLua()
            }

            is VSRevoluteJoint -> {
                joint["angularLimitPair"] = this.angularLimitPair?.toLua()
                joint["driveVelocity"] = this.driveVelocity?.toLua()
                joint["driveForceLimit"] = this.driveForceLimit?.toDouble()
                joint["driveGearRatio"] = this.driveGearRatio?.toDouble()
                joint["driveFreeSpin"] = this.driveFreeSpin
            }

            is VSGearJoint -> {
                joint["hinges"] = this.hinges?.toLua()
                joint["gearRatio"] = this.gearRatio?.toDouble()
            }

            is VSRackAndPinionJoint -> {
                joint["hinges"] = this.hinges?.toLua()
                joint["ratio"] = this.ratio?.toDouble()
                joint["data"] = this.data?.toLua()
            }

            is VSD6Joint -> {
                joint["motions"] = this.motions?.map { (axis, motion) -> axis.name to motion.name }
                joint["distanceLimit"] = this.distanceLimit?.toLua()
                joint["linearLimits"] = this.linearLimits?.map { (axis, limit) -> axis.name to limit.toLua() }
                joint["twistLimit"] = this.twistLimit?.toLua()
                joint["swingLimit"] = this.swingLimit?.toLua()
                joint["pyramidSwingLimit"] = this.pyramidSwingLimit?.toLua()
                joint["drives"] = this.drives?.map { (drive, jointDrive) -> drive.name to jointDrive.toLua() }
                joint["drivePosition"] = this.drivePosition?.toLua()
                joint["driveVelocity"] = this.driveVelocity?.toLua()
            }
        }

        return joint
    }
}