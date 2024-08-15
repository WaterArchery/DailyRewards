package dev.revivalo.dailyrewards.user;

import dev.revivalo.dailyrewards.DailyRewardsPlugin;
import dev.revivalo.dailyrewards.configuration.data.DataManager;
import dev.revivalo.dailyrewards.manager.Setting;
import dev.revivalo.dailyrewards.manager.cooldown.Cooldown;
import dev.revivalo.dailyrewards.manager.reward.RewardType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class User {
    private final Player player;
    private Map<String, Object> data;

    public User(Player player, Map<String, Object> data) {
        this.player = player;
        this.data = data;
    }

    public CompletableFuture<Cooldown> getCooldownOfReward(RewardType rewardType) {
        if (data.get(rewardType.toString()) != null) {
            return CompletableFuture.completedFuture(
                    new Cooldown(Long.parseLong(String.valueOf(data.get(rewardType.toString()))))
            );
        }

        return DailyRewardsPlugin.get().completableFuture(() -> {
            Map<String, Object> fetchedData = DataManager.getPlayerData(player);
            setData(fetchedData);

            if (fetchedData.isEmpty()) {
                return null;
            }
            return new Cooldown(Long.parseLong(String.valueOf(fetchedData.get(rewardType.toString()))));
        });
    }


    public Set<RewardType> getAvailableRewards(){
        Set<RewardType> availableRewards = new HashSet<>();
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            final RewardType rewardType = RewardType.findByName(entry.getKey());
            if (rewardType == null) continue;
            if (!rewardType.isEnabled()) continue;
            if (new Cooldown(Long.parseLong(String.valueOf(entry.getValue()))).isClaimable()) availableRewards.add(RewardType.findByName(entry.getKey()));
        }
        return availableRewards;
    }

    public boolean isOnline() {
        return player.isOnline();
    }

    /**
     *
     * @return True if the setting was enabled
     */
    public boolean toggleSetting(Setting setting, boolean set) {
        data.put(setting.getTag(), set ? "1" : "0");
        User user = this;

        new BukkitRunnable() {
            @Override
            public void run() {
                DataManager.updateValues(
                        player.getUniqueId(),
                        user,
                        new HashMap<String, Object>() {{
                            put(setting.getTag(), hasSettingEnabled(setting) ? 1L : 0);
                        }}
                );
            }
        }.runTaskAsynchronously(DailyRewardsPlugin.get());

        return set;
    }

    public boolean hasSettingEnabled(Setting setting) {
        if (setting == null) return false;
        String value = String.valueOf(data.get(setting.getTag()));
        if (value == null || value.equalsIgnoreCase("null")) return false;

        return 1 == Long.parseLong(value);
    }

    public void updateData(Map<String, Object> changes) {
        data.putAll(changes);
    }

    public Player getPlayer() {
        return player;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }
}
