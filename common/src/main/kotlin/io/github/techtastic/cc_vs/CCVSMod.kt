package io.github.techtastic.cc_vs

import io.github.techtastic.cc_vs.ship.PhysTickEventHandler
import io.github.techtastic.cc_vs.ship.QueuedForcesApplier
import org.valkyrienskies.mod.api.vsApi

object CCVSMod {
    const val MOD_ID = "cc_vs"

    @JvmStatic
    fun init() {
        vsApi.registerAttachment(QueuedForcesApplier::class.java)
        vsApi.registerAttachment(PhysTickEventHandler::class.java)
    }

    @JvmStatic
    fun initClient() {
    }
}
