package gg.grounds;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.plugin.Plugin;
import org.slf4j.Logger;

@Plugin(id = "grounds-plugin-player", name = "Grounds Player Plugin")
public final class GroundsPluginPlayer {
    private final Logger logger;

    @Inject
    public GroundsPluginPlayer(Logger logger) {
        this.logger = logger;

        this.logger.info("VelocityPlayerPlugin initialized");
    }

    @Subscribe
    public void onPostLogin(PostLoginEvent event) {
        String name = event.getPlayer().getUsername();
        logger.info("hello {}", name);
    }

    @Subscribe
    public void onDisconnect(DisconnectEvent event) {
        String name = event.getPlayer().getUsername();
        logger.info("bye {}", name);
    }
}
