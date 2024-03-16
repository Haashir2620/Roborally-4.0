package dk.dtu.compute.se.pisd.roborally.fileacces.model;

import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.model.Heading;

import java.util.ArrayList;
import java.util.List;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class SpaceTemplate {

    public List<Heading> walls = new ArrayList<>();
    public List<FieldAction> actions = new ArrayList<>();

    public int x;
    public int y;

}
