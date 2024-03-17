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
package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.model.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import org.jetbrains.annotations.NotNull;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class SpaceView extends StackPane implements ViewObserver {

    final public static int SPACE_HEIGHT = 40; // 60; // 75;
    final public static int SPACE_WIDTH = 40;  // 60; // 75;

    public final Space space;


    public SpaceView(@NotNull Space space) {
        this.space = space;

        // XXX the following styling should better be done with styles
        this.setPrefWidth(SPACE_WIDTH);
        this.setMinWidth(SPACE_WIDTH);
        this.setMaxWidth(SPACE_WIDTH);

        this.setPrefHeight(SPACE_HEIGHT);
        this.setMinHeight(SPACE_HEIGHT);
        this.setMaxHeight(SPACE_HEIGHT);

        if ((space.x + space.y) % 2 == 0) {
            this.setStyle("-fx-background-color: white;");
        } else {
            this.setStyle("-fx-background-color: black;");
        }

        updatePlayer();

        // This space view should listen to changes of the space
        space.attach(this);
        update(space);
    }

    private void updatePlayer() {
        this.getChildren().clear();

        Player player = space.getPlayer();
        if (player != null) {
            Polygon arrow = new Polygon(0.0, 0.0,
                    10.0, 20.0,
                    20.0, 0.0);
            try {
                arrow.setFill(Color.valueOf(player.getColor()));
            } catch (Exception e) {
                arrow.setFill(Color.MEDIUMPURPLE);
            }

            arrow.setRotate((90 * player.getHeading().ordinal()) % 360);
            this.getChildren().add(arrow);
        }
    }

    @Override
    //meget vigtig ift at opdatere view og instanser i spillet
    public void updateView(Subject subject) {
        if (subject == this.space) {
            updatePlayer();
            addwall(this.space);
            addCheckpoints(this.space);
            addConveyerbelt();
        }
    }


    private void addCheckpoints(Space space) {
        Checkpoint checkpoint = space.getCheckpoint();

        if (checkpoint != null) {
            Text text;
            Circle circle;

            switch (checkpoint.getCheckpointnumber()) {
                case 1:
                    circle = new Circle(10, 10, 10, Color.DARKORANGE);
                    text = new Text("1");
                    break;
                case 2:
                    circle = new Circle(10, 10, 10, Color.DARKORANGE);
                    text = new Text("2");
                    break;
                case 3:
                    circle = new Circle(10, 10, 10, Color.DARKORANGE);
                    text = new Text("3");
                    break;
                case 4:
                    circle = new Circle(10, 10, 10, Color.DARKORANGE);
                    text = new Text("4");
                    break;
                case 5:
                    circle = new Circle(10, 10, 10, Color.DARKORANGE);
                    text = new Text("5");
                    break;
                case 6:
                    circle = new Circle(10, 10, 10, Color.DARKORANGE);
                    text = new Text("6");
                    break;
                default:
                    return; // Ingen checkpoint, derfor tilføjer vi ikke noget
            }
            // Sætter skrifttypen til sort, fed, størrelse 20
            text.setFont(Font.font("Black", FontWeight.BOLD, 15));
            // Opretter en StackPane for at kombinere circle og text visuelt
            //stack er javeFx, hvor man kan sætte tekst oven på en cirkel
            StackPane stack = new StackPane();
            stack.getChildren().addAll(circle, text); // Tilføjer cirkel og tekst til stack

            // Centrerer stack på den angivne plads
            stack.setLayoutX(space.x * SPACE_WIDTH + SPACE_WIDTH / 2);
            stack.setLayoutY(space.y * SPACE_HEIGHT + SPACE_HEIGHT / 2);

            this.getChildren().add(stack); // Tilføjer stack til dette view

        }
    }


    public void addwall(Space space) {


        Canvas canvas = new Canvas(SPACE_HEIGHT, SPACE_WIDTH);
        GraphicsContext gc = canvas.getGraphicsContext2D();


        if (space.getWall() != null) {


            switch (space.getWall().getHeading()) {


                case NORTH:
                    gc.setStroke(Color.RED);
                    gc.setLineWidth(5);
                    gc.setLineCap(StrokeLineCap.ROUND);
                    gc.strokeLine(2, SPACE_HEIGHT - 38, SPACE_WIDTH - 2, SPACE_HEIGHT - 38);

                    break;
                //nord
                case EAST:


                    gc.setStroke(Color.RED);
                    gc.setLineWidth(5);
                    gc.setLineCap(StrokeLineCap.ROUND);
                    gc.strokeLine(38, SPACE_HEIGHT - 2, SPACE_WIDTH - 2, SPACE_HEIGHT - 38);

                    break;

                //west
                case WEST:
                    gc.setStroke(Color.RED);
                    gc.setLineWidth(5);
                    gc.setLineCap(StrokeLineCap.ROUND);
                    gc.strokeLine(2, SPACE_HEIGHT - 2, SPACE_WIDTH - 38, SPACE_HEIGHT - 38);

                    break;
                //south

                case SOUTH:

                    gc.setStroke(Color.RED);
                    gc.setLineWidth(5);
                    gc.setLineCap(StrokeLineCap.ROUND);
                    gc.strokeLine(2, SPACE_HEIGHT - 2, SPACE_WIDTH - 2, SPACE_HEIGHT - 2);

                    break;

                default:
                    break;
            }
            this.getChildren().add(canvas);

        }
    }


    public void addConveyerbelt() {
        Conveyerbelt conveyerbelt = space.getConveyerbelt();
        if (conveyerbelt != null) {
            Heading heading = conveyerbelt.getHeading();
            // Justerer størrelsen på pilen til at være mindre end firkanten
            double arrowBase = 25; // Bredde af pilens base
            double arrowHeight = 25 * 0.6; // Højden af pilen
            Color arrowColor = Color.YELLOW; // Farve for pilen

            // Beregner offset for at centrere pilen i firkanten
            double offsetX = (35 - arrowBase) / 2;
            double offsetY = (35 - arrowHeight) / 2;

            switch (heading) {
                case NORTH: {
                    Polygon arrowN = new Polygon(
                            arrowBase / 2.0 + offsetX, offsetY,
                            offsetX, arrowHeight + offsetY,
                            arrowBase + offsetX, arrowHeight + offsetY);
                    arrowN.setFill(arrowColor);
                    this.getChildren().add(arrowN);
                    break;
                }
                case EAST: {
                    Polygon arrowE = new Polygon(
                            offsetX, arrowBase / 2.0 + offsetY,
                            arrowHeight + offsetX, offsetY,
                            arrowHeight + offsetX, arrowBase + offsetY);
                    arrowE.setFill(arrowColor);
                    this.getChildren().add(arrowE);
                    break;
                }
                case SOUTH: {
                    Polygon arrowS = new Polygon(
                            arrowBase / 2.0 + offsetX, arrowHeight + offsetY,
                            arrowBase + offsetX, offsetY,
                            offsetX, offsetY);
                    arrowS.setFill(arrowColor);
                    this.getChildren().add(arrowS);
                    break;
                }
                case WEST: {
                    Polygon arrowW = new Polygon(
                            arrowHeight + offsetX, arrowBase / 2.0 + offsetY,
                            offsetX, arrowBase + offsetY,
                            offsetX, offsetY);
                    arrowW.setFill(arrowColor);
                    this.getChildren().add(arrowW);
                    break;
                }
            }
        }
    }
}