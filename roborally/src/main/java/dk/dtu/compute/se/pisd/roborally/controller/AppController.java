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

import dk.dtu.compute.se.pisd.designpatterns.observer.Observer;
import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;


import dk.dtu.compute.se.pisd.roborally.RoboRally;

import dk.dtu.compute.se.pisd.roborally.dal.GameInDB;
import dk.dtu.compute.se.pisd.roborally.dal.IRepository;
import dk.dtu.compute.se.pisd.roborally.dal.RepositoryAccess;
import dk.dtu.compute.se.pisd.roborally.fileacces.LoadBoard;
import dk.dtu.compute.se.pisd.roborally.model.*;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class
AppController implements Observer {

    final private List<Integer> PLAYER_NUMBER_OPTIONS = Arrays.asList(2, 3, 4, 5, 6);
    final private List<String> PLAYER_COLORS = Arrays.asList("red", "green", "blue", "orange", "grey", "magenta");
    //Ny kode relevant ift JSON
    final private List<String> BOARD_OPTIONS = Arrays.asList("Board 1", "Board 2");


    final private RoboRally roboRally;

    private GameController gameController;

    public AppController(@NotNull RoboRally roboRally) {
        this.roboRally = roboRally;
    }

    public void newGame() {
        ChoiceDialog<Integer> dialog = new ChoiceDialog<>(PLAYER_NUMBER_OPTIONS.get(0), PLAYER_NUMBER_OPTIONS);
        dialog.setTitle("Player number");
        dialog.setHeaderText("Select number of players");
        Optional<Integer> result = dialog.showAndWait();

        ChoiceDialog<String> dialog2 = new ChoiceDialog<>(BOARD_OPTIONS.get(0), BOARD_OPTIONS);
        dialog2.setTitle("Choice of board");
        dialog2.setHeaderText("Select board");
        Optional<String> result2 = dialog2.showAndWait();

        String boardResult = String.valueOf(result2);


        if (result.isPresent()) {
            if (gameController != null) {

                if (!stopGame()) {
                    return;
                }
            }

            Board board = LoadBoard.loadBoard(boardResult);
            System.out.println(boardResult);


            gameController = new GameController(board);
            int no = result.get();
            for (int i = 0; i < no; i++) {
                Player player = new Player(board, PLAYER_COLORS.get(i), "Player " + (i + 1));
                board.addPlayer(player);
                player.setSpace(board.getSpace(i % board.width, i));

            }


            // Tilføjer vægge til brættet
            Wall wall1 = new Wall(Heading.SOUTH, board);
            Wall wall2 = new Wall(Heading.NORTH, board);
            Wall wall3 = new Wall(Heading.EAST, board);
            Wall wall4 = new Wall(Heading.WEST, board);
            Wall wall5 = new Wall(Heading.SOUTH, board);
            Wall wall6 = new Wall(Heading.NORTH, board);
            Wall wall7 = new Wall(Heading.EAST, board);
            Wall wall8 = new Wall(Heading.WEST, board);
            Wall wall9 = new Wall(Heading.SOUTH, board);
            Wall wall10 = new Wall(Heading.NORTH, board);
            Wall wall11 = new Wall(Heading.EAST, board);
            Wall wall12 = new Wall(Heading.WEST, board);

            board.addwall(wall1);
            board.addwall(wall2);
            board.addwall(wall3);
            board.addwall(wall4);
            board.addwall(wall5);
            board.addwall(wall6);
            board.addwall(wall7);
            board.addwall(wall8);
            board.addwall(wall9);
            board.addwall(wall10);
            board.addwall(wall11);
            board.addwall(wall12);

// Placeringer af vægge
            wall1.setSpace(board.getSpace(0, 0));
            wall2.setSpace(board.getSpace(1, 1));
            wall3.setSpace(board.getSpace(0, 2));
            wall4.setSpace(board.getSpace(2, 1));
            wall5.setSpace(board.getSpace(2, 3));
            wall6.setSpace(board.getSpace(3, 3));
            wall7.setSpace(board.getSpace(3, 4));
            wall8.setSpace(board.getSpace(3, 5));
            wall9.setSpace(board.getSpace(4, 4));
            wall10.setSpace(board.getSpace(5, 4));
            wall11.setSpace(board.getSpace(5, 2));
            wall12.setSpace(board.getSpace(5, 3));


            // Opretter og placerer det første conveyerbelt
            Conveyerbelt conveyerbelt1 = new Conveyerbelt();
            conveyerbelt1.setHeading(Heading.WEST); // Retningen for dette conveyerbelt
            conveyerbelt1.setSpace(board.getSpace(1, 3)); // række 1, kolonne 3
            board.addConveyerbelt(conveyerbelt1);
            Space space1 = board.getSpace(1, 3); // Gemmer referencen til Space
            space1.setConveyerbelt(conveyerbelt1); // Sætter conveyerbeltet på Space

// Opretter og placerer det andet conveyerbelt
            Conveyerbelt conveyerbelt2 = new Conveyerbelt();
            conveyerbelt2.setHeading(Heading.NORTH);
            conveyerbelt2.setSpace(board.getSpace(4, 6));
            Space space2 = board.getSpace(4, 6);
            space2.setConveyerbelt(conveyerbelt2);

// Opretter og placerer det tredje conveyerbelt
            Conveyerbelt conveyerbelt3 = new Conveyerbelt();
            conveyerbelt3.setHeading(Heading.EAST);
            conveyerbelt3.setSpace(board.getSpace(2, 4));
            board.addConveyerbelt(conveyerbelt3);
            Space space3 = board.getSpace(2, 4);
            space3.setConveyerbelt(conveyerbelt3);

// Opretter og placerer det fjerde conveyerbelt
            Conveyerbelt conveyerbelt4 = new Conveyerbelt();
            conveyerbelt4.setHeading(Heading.SOUTH);
            conveyerbelt4.setSpace(board.getSpace(3, 7));
            board.addConveyerbelt(conveyerbelt4);
            Space space4 = board.getSpace(3, 7);
            space4.setConveyerbelt(conveyerbelt4);

// Opretter og placerer det femte conveyerbelt
            Conveyerbelt conveyerbelt5 = new Conveyerbelt();
            conveyerbelt5.setHeading(Heading.WEST);
            conveyerbelt5.setSpace(board.getSpace(5, 2));
            board.addConveyerbelt(conveyerbelt5);
            Space space5 = board.getSpace(5, 2);
            space5.setConveyerbelt(conveyerbelt5);


            Checkpoint checkpoint1 = new Checkpoint();
            Checkpoint checkpoint2 = new Checkpoint();
            Checkpoint checkpoint3 = new Checkpoint();
            Checkpoint checkpoint4 = new Checkpoint();
            Checkpoint checkpoint5 = new Checkpoint();

            checkpoint1.setCheckpointnumber(1);
            checkpoint2.setCheckpointnumber(2);
            checkpoint3.setCheckpointnumber(3);
            checkpoint4.setCheckpointnumber(4);
            checkpoint5.setCheckpointnumber(5);

            board.addCheckpoint(checkpoint1);
            board.addCheckpoint(checkpoint2);
            board.addCheckpoint(checkpoint3);
            board.addCheckpoint(checkpoint4);
            board.addCheckpoint(checkpoint5);

            checkpoint1.setSpace(board.getSpace(0, 1));
            checkpoint2.setSpace(board.getSpace(2, 5));
            checkpoint3.setSpace(board.getSpace(7, 7));
            checkpoint4.setSpace(board.getSpace(4, 1));
            checkpoint5.setSpace(board.getSpace(0, 6));

            Space space1c = board.getSpace(0, 1);
            Space space2c = board.getSpace(2, 5);
            Space space3c = board.getSpace(7, 7);
            Space space4c = board.getSpace(4, 1);
            Space space5c = board.getSpace(0, 6);

            space1c.setCheckpoint(checkpoint1);
            space2c.setCheckpoint(checkpoint2);
            space3c.setCheckpoint(checkpoint3);
            space4c.setCheckpoint(checkpoint4);
            space5c.setCheckpoint(checkpoint5);



            // XXX: V2
            //oard.setCurrentPlayer(board.getPlayer(0));
            gameController.startProgrammingPhase();

            roboRally.createBoardView(gameController);
        }
    }

    public void saveGame() {
        IRepository repo = RepositoryAccess.getRepository();
        repo.createGameInDB(gameController.board);
    }


    public void loadGame() {
        if (gameController == null) {
            IRepository repo = RepositoryAccess.getRepository();
            List<GameInDB> gameList = repo.getGames();
            ChoiceDialog<GameInDB> dialog = new ChoiceDialog<>(gameList.get(gameList.size() - 1), gameList);
            dialog.setTitle("Games options");
            dialog.setHeaderText("Select the game to load it");
            Optional<GameInDB> result1 = dialog.showAndWait();

            if (result1.isPresent()) {
                Board board = repo.loadGameFromDB(result1.get().id);
                gameController = new GameController(board);
                roboRally.createBoardView(gameController);
            }
        }

    }


    //stopGame: Denne metode gemmer det aktuelle spil og sætter gameController til null, hvilket i praksis stopper spillet.
// Den opretter derefter brætvisningen med null, hvilket sikkert fjerner brætvisningen.
    public boolean stopGame() {
        if (gameController != null) {

            // here we save the game (without asking the user).
            saveGame();

            gameController = null;
            roboRally.createBoardView(null);
            return true;
        }
        return false;
    }
    //exit: Denne metode håndterer afslutningen af applikationen.
// Den viser en dialogboks, der spørger brugeren, om de er sikre på, at de vil afslutte RoboRally.
// Hvis brugeren bekræfter, at de vil afslutte, eller hvis der ikke er noget igangværende spil, afslutter den applikationen.
    public void exit() {
        if (gameController != null) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Exit RoboRally?");
            alert.setContentText("Are you sure you want to exit RoboRally?");
            Optional<ButtonType> result = alert.showAndWait();

            if (!result.isPresent() || result.get() != ButtonType.OK) {
                return; // return without exiting the application
            }
        }

        // If the user did not cancel, the RoboRally application will exit
        // after the option to save the game
        if (gameController == null || stopGame()) {
            Platform.exit();
        }
    }
    //isGameRunning: Denne metode returnerer en boolean, der angiver, om der i øjeblikket er et spil i gang.
// Hvis gameController ikke er null, betyder det, at der er et spil i gang.
    public boolean isGameRunning() {
        return gameController != null;
    }

    ///update: Da denne klasse implementerer Observer interfacet, er der en update metode, som skal implementeres.
// Denne metode kaldes, når objektet, som denne klasse observerer
// (dvs. et objekt af en klasse, der implementerer Subject interfacet), opdateres.
// I dette tilfælde ser det ud til, at denne metode endnu ikke er implementeret.
    @Override
    public void update(Subject subject) {

    }

   }




