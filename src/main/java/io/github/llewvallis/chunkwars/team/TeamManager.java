package io.github.llewvallis.chunkwars.team;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.Optional;

public class TeamManager {

    public static TeamManager instance;

    private Scoreboard scoreboard;
    private Team light;
    private Team dark;

    public void configScoreboard() {
        scoreboard = Bukkit.getServer().getScoreboardManager().getMainScoreboard();

        light = getOrRegisterTeam("light");
        dark = getOrRegisterTeam("dark");

        configTeam(light, ChatColor.YELLOW);
        configTeam(dark, ChatColor.DARK_PURPLE);
    }

    private Team getOrRegisterTeam(String name) {
        Team team = scoreboard.getTeam(name);
        if (team == null) {
            team = scoreboard.registerNewTeam(name);
        }

        return team;
    }

    private void configTeam(Team team, ChatColor color) {
        team.setAllowFriendlyFire(false);
        team.setCanSeeFriendlyInvisibles(true);
        team.setColor(color);
        team.setPrefix(color.toString());
        team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.FOR_OTHER_TEAMS);
    }

    public boolean isPlayerOnTeam(Player player, GameTeam team) {
        return getTeam(team).hasEntry(player.getName());
    }

    public Optional<GameTeam> getPlayerTeam(Player player) {
        Team team = scoreboard.getEntryTeam(player.getName());

        if (light.equals(team)) {
            return Optional.of(GameTeam.LIGHT);
        } else if (dark.equals(team)) {
            return Optional.of(GameTeam.DARK);
        } else {
            return Optional.empty();
        }
    }

    public boolean addPlayerToTeam(Player player, GameTeam team) {
        if (isPlayerOnTeam(player, team)) {
            return false;
        } else {
            TeamChangeEvent e = new TeamChangeEvent(player, getPlayerTeam(player), Optional.of(team));

            for (Team otherTeam : scoreboard.getTeams()) {
                otherTeam.removeEntry(player.getName());
            }

            getTeam(team).addEntry(player.getName());

            Bukkit.getPluginManager().callEvent(e);
            return true;
        }
    }

    public boolean removePlayerFromTeam(Player player) {
        boolean changed = false;

        TeamChangeEvent e = new TeamChangeEvent(player, getPlayerTeam(player), Optional.empty());

        for (Team team : scoreboard.getTeams()) {
            changed |= team.removeEntry(player.getName());
        }

        if (changed) {
            Bukkit.getPluginManager().callEvent(e);
        }

        return changed;
    }

    private Team getTeam(GameTeam team) {
        return team == GameTeam.LIGHT ? light : dark;
    }
}
