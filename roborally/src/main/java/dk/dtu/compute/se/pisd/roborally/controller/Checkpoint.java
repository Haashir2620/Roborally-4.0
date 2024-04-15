package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Space;

public class Checkpoint {

    public static Board Space;
    private dk.dtu.compute.se.pisd.roborally.model.Space space;

    private boolean complete;

    //hvilket checkpoint det er i gamecontroller logik
    public int getCheckpointnumber() {
        return checkpointnumber;
    }
    //hvilket checkpoint det er i appcontrolleren
    public void setCheckpointnumber(int checkpointnumber) {
        this.checkpointnumber = checkpointnumber;
    }

    public int checkpointnumber;

    private int value;
    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public Space getSpace() {
        return space;
    }

    //hvor checkpoints er placeret
    public void setSpace(Space space) {
        this.space = space;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public void Checkpoint(){
        value = 1;
        space = null;
        complete = false;
        checkpointnumber= 0;
    }

    }

