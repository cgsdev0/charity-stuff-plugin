package dev.cgs.mc.charity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.entity.Player;

public class TeamManager {

    private static TeamManager instance;

    private List<Team> teams;

    private TeamManager() {
      teams = new ArrayList<>();
      teams.add(new Team(Team.Leader.JAKE));
      teams.add(new Team(Team.Leader.BADCOP));
    }

    public Team fromPlayer(Player player) {
      // TODO
      return teams.get(0);
    }

    public List<String> getKeys() {
    return Stream.of(Team.Leader.values())
               .map(Enum::name)
               .collect(Collectors.toList());
    }

    public Team fromLeader(Team.Leader leader) {
      return teams.stream().filter(team -> team.getLeader() == leader).findFirst().orElse(null);
    }

    public static void onEnable() {
        if (instance != null) {
            throw new IllegalStateException("TeamManager is already initialized.");
        }
        instance = new TeamManager();
    }

    public static TeamManager get() {
        if (instance == null) {
            throw new IllegalStateException("TeamManager not initialized yet.");
        }
        return instance;
    }

    public static void onDisable() {
        instance = null;
    }
}
