package dev.revivalo.dailyrewards.user;

import dev.revivalo.dailyrewards.DailyRewardsPlugin;
import dev.revivalo.dailyrewards.configuration.data.DataManager;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public final class UserHandler {
    private static final HashMap<UUID, User> usersHashMap = new HashMap<>();
    public static User addUser(final User user){
        usersHashMap.put(user.getPlayer().getUniqueId(), user);
        return user;
    }

    public static User getUser(final UUID uuid){
        return usersHashMap.get(uuid);
    }

    @Nullable
    public static User getUser(@Nullable Player player) {
        return Optional.ofNullable(player)
                .map(Player::getUniqueId)
                .map(UserHandler::getUser)
                .orElse(null);
    }

    public static void removeUser(final UUID uuid){
        final User user = usersHashMap.remove(uuid);

        if (user != null) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    DataManager.updateValues(uuid, user, user.getData());
                }
            }.runTaskAsynchronously(DailyRewardsPlugin.get());
        }
    }
}
