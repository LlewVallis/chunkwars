package io.github.llewvallis.chunkwars.command;

import io.github.llewvallis.chunkwars.team.GameTeam;
import io.github.llewvallis.commandbuilder.arguments.DelegateArgument;
import io.github.llewvallis.commandbuilder.arguments.StringSetArgument;

public class TeamArgument extends DelegateArgument<GameTeam> {

    public TeamArgument() {
        super(
                new StringSetArgument("light", "dark")
                        .map(name -> name.equals("light") ? GameTeam.LIGHT : GameTeam.DARK)
        );
    }
}
