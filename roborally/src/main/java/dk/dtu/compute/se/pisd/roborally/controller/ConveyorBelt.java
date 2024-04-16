package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import org.jetbrains.annotations.NotNull;

public class ConveyorBelt extends FieldAction {

    private Heading heading;


    public Heading getHeading() {
        return heading;
    }

    public void setHeading(Heading heading) {
        this.heading = heading;
    }

    /**
     * Implementation of the action of a conveyor belt. Needs to be implemented for A3.
     */
    @Override
    public boolean doAction(@NotNull GameController gameController, @NotNull Space space) {
        Player player = space.getPlayer();
        if (player == null) {
            return false; // Hvis der ikke er nogen spiller, afslut tidligt
        }

        Heading heading = player.getHeading(); //  henter retningen fra spilleren
        Space space2 = gameController.board.getNeighbour(space, heading);

        if (space2 != null && space2.getPlayer() == null) { // Tjek om nabopladsen er tom
            try {
                gameController.moveToSpace(player, space2, heading);
                player.setSpace(space2); // Opdater spillerens placering
            } catch (ImpossibleMoveException e) {
                // Log exception eller håndter den som nødvendigt
            }
            return true;
        } else {
            return false; // Returner false hvis der ikke er nogen gyldig naboplads
        }
    }

    }

