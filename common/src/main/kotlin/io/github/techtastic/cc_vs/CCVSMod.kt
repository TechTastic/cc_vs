package io.github.techtastic.cc_vs

import dan200.computercraft.shared.computer.core.ServerContext
import dev.architectury.event.events.common.LifecycleEvent

object CCVSMod {
    const val MOD_ID = "cc_vs"
    lateinit var context: ServerContext

    @JvmStatic
    fun init() {
        LifecycleEvent.SERVER_STARTED.register { context = ServerContext.get(it) }
    }

    @JvmStatic
    fun initClient() {
    }
}
