/*
 *  This file is part of the initial project provided for the
 *  course "Project in Software Development (02362)" held at
 *  DTU Compute at the Technical University of Denmark.
 *
 *  Copyright (C) 2019, 2020: Ekkart Kindler, ekki@dtu.dk
 *
 *  This software is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; version 2 of the License.
 *
 *  This project is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this project; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.*;
import org.jetbrains.annotations.NotNull;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class GameController {

    final public Board board;

    public GameController(@NotNull Board board) {
        this.board = board;
    }

    /**
     * This is just some dummy controller operation to make a simple move to see something
     * happening on the board. This method should eventually be deleted!
     *
     * @param space the space to which the current player should move
     * @author s224558
     */

    public void moveCurrentPlayerToSpace(@NotNull Space space) {
            // TODO Assignment V1: method should be implemented by the students:
            //   - the current player should be moved to the given space
            //     (if it is free()
            //   - and the current player should be set to the player
            //     following the current player
            //   - the counter of moves in the game should be increased by one
            //     if and when the player is moved (the counter and the status line
            //     message needs to be implemented at another place)
            Player currentPlayer = board.getCurrentPlayer();
            if (currentPlayer == null || space == null) {
                return;
            }

            Space currentSpace = currentPlayer.getSpace();
            if (currentSpace == null) {
                return;
            }

            if (space.getPlayer() == null) {
                currentSpace.setPlayer(null);
                space.setPlayer(currentPlayer);
                currentPlayer.setSpace(space);

                // Opdaterer spillets bevægelsestæller
                board.setMoveCounter(board.getMoveCounter() + 1);
            } else {
                Space nextSpace = board.getNeighbour(space, currentPlayer.getHeading());
                // Tjekker, om det er muligt at skubbe den anden spiller
                if (nextSpace != null && nextSpace.getPlayer() == null) {
                    // Skubber den anden spiller ind i det næste rum
                    Player otherPlayer = space.getPlayer();
                    otherPlayer.setSpace(nextSpace);
                    nextSpace.setPlayer(otherPlayer);

                    // Flytter den nuværende spiller ind i målrummet
                    currentSpace.setPlayer(null);
                    space.setPlayer(currentPlayer);
                    currentPlayer.setSpace(space);

                    // Opdaterer spillets bevægelsestæller
                    board.setMoveCounter(board.getMoveCounter() + 1);
                }
            }

            // Opdaterer den nuværende spiller til den næste i rækken
            int currentPlayerIndex = board.getPlayerNumber(currentPlayer);
            int nextPlayerIndex = (currentPlayerIndex + 1) % board.getPlayersNumber();
            board.setCurrentPlayer(board.getPlayer(nextPlayerIndex));
        }




    // XXX: V2
    public void startProgrammingPhase() {
        board.setPhase(Phase.PROGRAMMING);
        board.setCurrentPlayer(board.getPlayer(0));
        board.setStep(0);

        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            if (player != null) {
                for (int j = 0; j < Player.NO_REGISTERS; j++) {
                    CommandCardField field = player.getProgramField(j);
                    field.setCard(null);
                    field.setVisible(true);
                }
                for (int j = 0; j < Player.NO_CARDS; j++) {
                    CommandCardField field = player.getCardField(j);
                    field.setCard(generateRandomCommandCard());
                    field.setVisible(true);
                }
            }
        }
    }

    // XXX: V2
    private CommandCard generateRandomCommandCard() {
        Command[] commands = Command.values();
        int random = (int) (Math.random() * commands.length);
        return new CommandCard(commands[random]);
    }

    // XXX: V2
    public void finishProgrammingPhase() {
        makeProgramFieldsInvisible();
        makeProgramFieldsVisible(0);
        board.setPhase(Phase.ACTIVATION);
        board.setCurrentPlayer(board.getPlayer(0));
        board.setStep(0);
    }

    // XXX: V2
    private void makeProgramFieldsVisible(int register) {
        if (register >= 0 && register < Player.NO_REGISTERS) {
            for (int i = 0; i < board.getPlayersNumber(); i++) {
                Player player = board.getPlayer(i);
                CommandCardField field = player.getProgramField(register);
                field.setVisible(true);
            }
        }
    }

    // XXX: V2
    private void makeProgramFieldsInvisible() {
        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            for (int j = 0; j < Player.NO_REGISTERS; j++) {
                CommandCardField field = player.getProgramField(j);
                field.setVisible(false);
            }
        }
    }

    // XXX: V2
    public void executePrograms() {
        board.setStepMode(false);
        continuePrograms();
        //seStepmode er på false, fordi vi skal kører programmet og de rikke behov for step
    }

    // XXX: V2
    public void executeStep() {
        board.setStepMode(true);
        continuePrograms();
        //setAStepmode er true her, fordi vi er i stepmode i denne tilfælde

    }

    // XXX: V2
    private void continuePrograms() {
        do {
            executeNextStep();
        } while (board.getPhase() == Phase.ACTIVATION && !board.isStepMode());
    }

    // XXX: V2

    /**
     * executes a commandcard and goes to the next player. if the board is not in activation phase, then it won't work.
     * and there is a currentplayer from the board. the step has to be between those 5 cards. if the card is null,
     * then it won't go through. after executing command, then it will go to the next player by setting currentplayer on board,
     * to the next player
     */
    private void executeNextStep() {
        Player currentPlayer = board.getCurrentPlayer();
        if (board.getPhase() == Phase.ACTIVATION && currentPlayer != null) {
            int step = board.getStep();
            if (step >= 0 && step < Player.NO_REGISTERS) {
                CommandCard card = currentPlayer.getProgramField(step).getCard();
                //V3 Vi laver en if Statement, fordi vi skal sætte pause på spillet, da vi skal gå tilbage til GUI
                if (card != null) {
                    Command command = card.command;
                    //Her ser vi om den er interactive eller ej
                    if (command.isInteractive()) {
                        board.setPhase(Phase.PLAYER_INTERACTION);
                        return;
                    }
                    executeCommand(currentPlayer, command);

                }
                int nextPlayerNumber = board.getPlayerNumber(currentPlayer) + 1;
                if (nextPlayerNumber < board.getPlayersNumber()) {
                    board.setCurrentPlayer(board.getPlayer(nextPlayerNumber));
                } else {
                    step++;

                    if (step < Player.NO_REGISTERS) {
                        makeProgramFieldsVisible(step);
                        board.setStep(step);
                        board.setCurrentPlayer(board.getPlayer(0));
                    } else {
                        startProgrammingPhase();
                    }
                }
            } else {
                // this should not happen
                assert false;
            }
        } else {
            // this should not happen
            assert false;
        }
    }

    public void executeCommandOptionAndContinue(@NotNull Command option) { // denne metode bliver kaldt i playerview
        Player currentPlayer = board.getCurrentPlayer();
        if (board.getPhase() == Phase.PLAYER_INTERACTION && currentPlayer != null) {
            int step = board.getStep();
            if (step >= 0 && step < Player.NO_REGISTERS) {
                CommandCard card = currentPlayer.getProgramField(step).getCard();
                if (card != null) {
                    Command command = card.command;
                    executeCommand(currentPlayer, option);
                    if (command.isInteractive()) {
                        board.setPhase(Phase.ACTIVATION);
                        //return;
                    }
                    int nextPlayerNumber = board.getPlayerNumber(currentPlayer) + 1;
                    if (nextPlayerNumber < board.getPlayersNumber()) {
                        board.setCurrentPlayer(board.getPlayer(nextPlayerNumber));
                    } else {
                        step++;
                        if (step < Player.NO_REGISTERS) {
                            makeProgramFieldsVisible(step);
                            board.setStep(step);
                            board.setCurrentPlayer(board.getPlayer(0));
                        } else {
                            startProgrammingPhase();
                        }
                    }
                }
            } else {
                // this should not happen
                assert false;
            }
        } else {
            // this should not happen
            assert false;
        }

        if (!board.isStepMode() && board.getPhase() == Phase.ACTIVATION) {
            continuePrograms();
        }
    }


    // XXX: V2
    private void executeCommand(@NotNull Player player, Command command) {
        if (player != null && player.board == board && command != null) {
            // XXX This is a very simplistic way of dealing with some basic cards and
            //     their execution. This should eventually be done in a more elegant way
            //     (this concerns the way cards are modelled as well as the way they are executed).

            switch (command) {
                case FORWARD:
                    this.moveForward(player);
                    break;
                case RIGHT:
                    this.turnRight(player);
                    break;
                case LEFT:
                    this.turnLeft(player);
                    break;
                case FAST_FORWARD:
                    this.fastForward(player);
                    break;
                case Move2:
                    this.Move2(player);
                    break;
                case Uturn:
                    this.Uturn(player);
                    break;

                case Moveback:
                    this.moveBack(player);
                    break;
                default:
                    // DO NOTHING (for now)
            }
        }
    }

    // TODO Assignment V2

    /**
     * s224558
     * Moves the current players robot one space i the robots current direction
     *
     * @param player The player which Robot is getting moved one space in the current direction
     *               This method first insures that both the current space and the neigbouring space exist on the board
     *               It then checks if the neighbouring space is occupied by another player both either setting the players space to
     *               the neighbouring space or if the space is occupied it will run a method called moveToSpace which pushes the other
     *               player away before moving onto the space
     */
    public void moveForward(@NotNull Player player) {
        if (player.board == board) {
            Space space = player.getSpace();
            Heading heading = player.getHeading();
            Space target = board.getNeighbour(space, heading);
            if (target != null) {
                try {
                    moveToSpace(player, target, heading);
                    player.setSpace(target);
                } catch (ImpossibleMoveException e) {
                }
            }
        }
    }



    // TODO Assignment V2

    /**
     *
     * The method moves the current robot 3 spaces forward in the robots current direction.
     * Before moving the robot the method checks if every space is free.
     *
     * @param player
     */
    public void fastForward(@NotNull Player player) {
        this.moveForward(player);
        this.moveForward(player);
        this.moveForward(player);


    }

    // TODO Assignment V2

    /**
     * s224558
     * The robots direction turns to the right
     *
     * @param player is the current players robot
     */
    public void turnRight(@NotNull Player player) {
        Heading heading = player.getHeading();
        player.setHeading(heading.next());

    }

    // TODO Assignment V2

    /**
     *
     * The robots direction turns to the left
     *
     * @param player is the current players robot
     */
    public void turnLeft(@NotNull Player player) {
        Heading heading = player.getHeading();
        player.setHeading(heading.prev());

    }

    /**
     * @param player is the current players robot
     * @auther s224558
     * The robots direction turns around
     */
    public void Uturn(@NotNull Player player) {
        int i;
        for (i = 0; i < 2; ++i) {
            Heading heading = player.getHeading();
            player.setHeading(heading.prev());
        }
    }

    /**
     * @author
     * @param player
     */
    public void Move2(@NotNull Player player) {
        this.moveForward(player);
        this.moveForward(player);

    }

    /**
     * @s224558
     * @param player
     */

    public void moveBack(@NotNull Player player) {
        Heading backDirection = player.getHeading().prev().prev(); // Beregner den modsatte retning
        Space currentSpace = player.getSpace(); // Det nuværende felt spilleren er på
        Space backSpace = board.getNeighbour(currentSpace, backDirection); // Finder det bagvedliggende felt

        if (backSpace != null && backSpace.getPlayer() == null && backSpace.getWall() == null) {
            player.setSpace(backSpace); // Flytter spilleren til det bagvedliggende felt


            }
        }



    public boolean moveCards(@NotNull CommandCardField source, @NotNull CommandCardField target) {
        CommandCard sourceCard = source.getCard();
        CommandCard targetCard = target.getCard();
        if (sourceCard != null && targetCard == null) {
            target.setCard(sourceCard);
            source.setCard(null);
            return true;
        } else {
            return false;
        }
    }

    /**
     * A method called when no corresponding controller operation is implemented yet. This
     * should eventually be removed.
     */
    public void notImplemented() {
        // XXX just for now to indicate that the actual method is not yet implemented
        assert false;
    }

    /**
     * @param player
     * @param space
     * @param heading
     * @throws ImpossibleMoveException
     */
    public void moveToSpace(
            @NotNull Player player,
            @NotNull Space space,
            @NotNull Heading heading) throws ImpossibleMoveException {

        // Tjek om det ønskede felt er optaget af en anden spiller.
        Player other = space.getPlayer();
        if (other != null) {
            // Hvis ja, find det næste felt i den givne retning.
            Space target = board.getNeighbour(space, heading);
            if (target != null) {
                // Hvis næste felt er ledigt, flyt den anden spiller derhen.
                moveToSpace(other, target, heading);
            } else {
                // Hvis næste felt ikke er ledigt, kast en exception.
                throw new ImpossibleMoveException(player, space, heading);
            }
        }
        // Flyt spilleren til det ønskede felt, hvis det er ledigt eller blevet lediggjort.
        player.setSpace(space);
    }

}
