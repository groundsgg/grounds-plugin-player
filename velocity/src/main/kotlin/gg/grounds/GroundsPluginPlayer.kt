package gg.grounds

import com.google.inject.Inject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.ProxyServer
import gg.grounds.config.PluginConfigLoader
import gg.grounds.listener.PlayerConnectionListener
import gg.grounds.presence.PlayerPresenceService
import io.grpc.LoadBalancerRegistry
import io.grpc.NameResolverRegistry
import io.grpc.internal.DnsNameResolverProvider
import io.grpc.internal.PickFirstLoadBalancerProvider
import java.nio.file.Path
import org.slf4j.Logger

@Plugin(
    id = "plugin-player",
    name = "Grounds Player Plugin",
    version = BuildInfo.VERSION,
    description = "",
    authors = ["Grounds Development Team and contributors"],
    url = "https://github.com/groundsgg/plugin-player",
)
class GroundsPluginPlayer
@Inject
constructor(
    private val proxy: ProxyServer,
    private val logger: Logger,
    @param:DataDirectory private val dataDirectory: Path,
) {
    private val playerPresenceService = PlayerPresenceService(logger)

    init {
        logger.info("VelocityPlayerPlugin initialized")
    }

    @Subscribe
    fun onInitialize(event: ProxyInitializeEvent) {
        registerProviders()

        val config = PluginConfigLoader(logger, dataDirectory).loadOrCreate()
        val clientConfig = config.playerPresence.toClientConfig()
        playerPresenceService.configure(clientConfig)

        proxy.eventManager.register(
            this,
            PlayerConnectionListener(
                logger = logger,
                playerPresenceService = playerPresenceService,
                messages = config.messages,
            ),
        )

        logger.info(
            "PlayerPresence gRPC configured (target={}, plaintext={}, timeoutMs={})",
            clientConfig.target,
            clientConfig.plaintext,
            clientConfig.timeout.toMillis(),
        )
    }

    @Subscribe
    fun onShutdown(event: ProxyShutdownEvent) {
        playerPresenceService.close()
    }

    /**
     * Registers gRPC name resolver and load balancer providers so client channels can resolve DNS
     * targets and select endpoints when running inside Velocity's shaded environment. This manual
     * step avoids startup IllegalArgumentExceptions caused by shaded classes not being discoverable
     * via the default provider lookup.
     */
    private fun registerProviders() {
        NameResolverRegistry.getDefaultRegistry().register(DnsNameResolverProvider())
        LoadBalancerRegistry.getDefaultRegistry().register(PickFirstLoadBalancerProvider())
    }
}
