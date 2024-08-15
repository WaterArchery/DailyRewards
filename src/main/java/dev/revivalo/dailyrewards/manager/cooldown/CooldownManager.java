package dev.revivalo.dailyrewards.manager.cooldown;

import dev.revivalo.dailyrewards.DailyRewardsPlugin;
import dev.revivalo.dailyrewards.configuration.data.DataManager;
import dev.revivalo.dailyrewards.manager.reward.Reward;
import dev.revivalo.dailyrewards.user.User;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class CooldownManager {

	public static void setCooldown(final User user, Reward reward) {
        long cooldown = reward.getCooldown();
		new BukkitRunnable() {
            @Override
            public void run() {
                DataManager.updateValues(
                        user.getPlayer().getUniqueId(),
                        user,
                        new HashMap<String, Object>() {{
                            put(reward.getRewardName(), System.currentTimeMillis() + cooldown);
                        }}
                );
            }
        }.runTaskAsynchronously(DailyRewardsPlugin.get());
	}
}
