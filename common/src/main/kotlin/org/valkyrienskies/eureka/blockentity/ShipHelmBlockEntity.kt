package org.valkyrienskies.eureka.blockentity

import net.minecraft.commands.arguments.EntityAnchorArgument
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction.Axis
import net.minecraft.core.Registry
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.TranslatableComponent
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.level.block.HorizontalDirectionalBlock
import net.minecraft.world.level.block.StairBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.TickableBlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.Half
import org.joml.Vector3d
import org.joml.Vector3dc
import org.valkyrienskies.core.api.ServerShip
import org.valkyrienskies.core.api.ServerShipProvider
import org.valkyrienskies.core.api.getAttachment
import org.valkyrienskies.core.api.saveAttachment
import org.valkyrienskies.core.api.shipValue
import org.valkyrienskies.core.game.ships.ShipData
import org.valkyrienskies.eureka.EurekaBlockEntities
import org.valkyrienskies.eureka.EurekaConfig
import org.valkyrienskies.eureka.block.ShipHelmBlock
import org.valkyrienskies.eureka.gui.shiphelm.ShipHelmScreenMenu
import org.valkyrienskies.eureka.ship.EurekaShipControl
import org.valkyrienskies.eureka.util.ShipAssembler
import org.valkyrienskies.mod.common.ValkyrienSkiesMod
import org.valkyrienskies.mod.common.dimensionId
import org.valkyrienskies.mod.common.entity.ShipMountingEntity
import org.valkyrienskies.mod.common.getShipObjectManagingPos
import org.valkyrienskies.mod.common.shipObjectWorld
import org.valkyrienskies.mod.common.util.toDoubles
import org.valkyrienskies.mod.common.util.toJOML
import org.valkyrienskies.mod.common.util.toJOMLD

class ShipHelmBlockEntity :
    BlockEntity(EurekaBlockEntities.SHIP_HELM.get()), MenuProvider, ServerShipProvider, TickableBlockEntity {

    override var ship: ServerShip? = null // TODO ship is not being set in vs2?
        get() = field ?: (level as ServerLevel).getShipObjectManagingPos(this.blockPos)
    val control by shipValue<EurekaShipControl>()
    val assembled get() = ship != null
    val aligning get() = control?.aligning ?: false
    var shouldDisassembleWhenPossible = false

    override fun createMenu(id: Int, playerInventory: Inventory, player: Player): AbstractContainerMenu {
        return ShipHelmScreenMenu(id, playerInventory, this)
    }

    override fun getDisplayName(): Component {
        return TranslatableComponent("gui.vs_eureka.ship_helm")
    }

    // Needs to get called server-side
    fun spawnSeat(blockPos: BlockPos, state: BlockState, level: ServerLevel): ShipMountingEntity {
        val newPos = blockPos.relative(state.getValue(HorizontalDirectionalBlock.FACING))
        val newState = level.getBlockState(newPos)
        val newShape = newState.getShape(level, newPos)
        val newBlock = newState.block
        var height = 0.5
        if (!newState.isAir) {
            height = if (
                newBlock is StairBlock &&
                (!newState.hasProperty(StairBlock.HALF) || newState.getValue(StairBlock.HALF) == Half.BOTTOM)
            )
                0.5 // Valid StairBlock
            else
                newShape.max(Axis.Y)
        }
        val entity = ValkyrienSkiesMod.SHIP_MOUNTING_ENTITY_TYPE.create(level)!!.apply {
            val seatEntityPos: Vector3dc = Vector3d(newPos.x + .5, (newPos.y - .5) + height, newPos.z + .5)
            moveTo(seatEntityPos.x(), seatEntityPos.y(), seatEntityPos.z())

            lookAt(
                EntityAnchorArgument.Anchor.EYES,
                state.getValue(HorizontalDirectionalBlock.FACING).normal.toDoubles().add(position())
            )

            isController = true
        }

        level.addFreshEntityWithPassengers(entity)
        return entity
    }

    override fun tick() {
        if (shouldDisassembleWhenPossible && ship?.getAttachment<EurekaShipControl>()?.canDisassemble == true) {
            this.disassemble()
        }
    }

    // Needs to get called server-side
    fun assemble() {
        val level = level as ServerLevel

        // Check the block state before assembling to avoid creating an empty ship
        val blockState = level.getBlockState(blockPos)
        if (blockState.block !is ShipHelmBlock) return

        val ship: ShipData =
            level.shipObjectWorld.createNewShipAtBlock(blockPos.toJOML(), false, 1.0, level.dimensionId)
        ship.saveAttachment(EurekaShipControl(ship.shipTransform.shipPositionInWorldCoordinates.y()))
        ShipAssembler.fillShip(
            level,
            ship,
            blockPos
        ) { !EurekaConfig.SERVER.blockBlacklist.contains(Registry.BLOCK.getKey(it.block).toString()) }
    }

    fun disassemble() {
        val ship = ship ?: return
        val level = level ?: return
        val control = control ?: return

        if (!control.canDisassemble) {
            shouldDisassembleWhenPossible = true
            control.aligning = true
            return
        }

        val inWorld = ship.shipToWorld.transformPosition(this.blockPos.toJOMLD())

        ShipAssembler.unfillShip(
            level as ServerLevel,
            ship,
            control.aligningTo,
            this.blockPos,
            BlockPos(inWorld.x, inWorld.y, inWorld.z)
        )
        // ship.die() TODO i think we do need this no? or autodetecting on all air

        shouldDisassembleWhenPossible = false
    }

    fun align() {
        val control = control ?: return
        control.aligning = !control.aligning
    }

    fun sit(player: Player, force: Boolean = false): Boolean =
        player.startRiding(spawnSeat(blockPos, blockState, level as ServerLevel), force)

    companion object {
        val supplier = { ShipHelmBlockEntity() }
    }
}
