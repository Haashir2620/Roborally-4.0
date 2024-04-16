package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;

public class Pit extends FieldAction {
    @Override
    public boolean doAction(GameController gameController, Space space) {
        Player player = space.getPlayer();
        //return false, hvis player null
        if (player == null) {
            return false;
        }
        //logik for hvad der sker når spiller falder i pit, og når spilleren mister liv
        player.setHp(player.getHp() - 1);
        Space randomSpace = gameController.board.getRandomSpace();
        while (randomSpace.getPlayer() != null && randomSpace != space) {
            randomSpace = gameController.board.getRandomSpace();
        }
        if (player.getHp() <= 0) {
            player.setHp(4);
            player.setCheckpointValue(0);
        }
        return true;
    }
}
