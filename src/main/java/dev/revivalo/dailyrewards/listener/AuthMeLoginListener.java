package dev.revivalo.dailyrewards.listener;

import dev.revivalo.dailyrewards.DailyRewardsPlugin;
import dev.revivalo.dailyrewards.user.UserHandler;
import fr.xephi.authme.events.LoginEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

public class AuthMeLoginListener implements Listener, LoginListener<LoginEvent> {

    @EventHandler
    public void onLogin(LoginEvent event) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Player player = event.getPlayer();
                if (player != null) {
                    DailyRewardsPlugin.getRewardManager().processAutoClaimForUser(UserHandler.getUser(event.getPlayer()));
                }
            }
        }.runTaskLaterAsynchronously(DailyRewardsPlugin.get(), 40);
    }

}
