/**
 * For demonstration purposes and for avoiding some JavaFX warnings, the RoboRally
 * application is now configured as a Java module.
 */
module roborally {

    requires javafx.controls;
    requires org.jetbrains.annotations;

    exports dk.dtu.compute.se.pisd.roborally;

    exports dk.dtu.compute.se.pisd.roborally.model;
    exports dk.dtu.compute.se.pisd.roborally.view;
    exports dk.dtu.compute.se.pisd.roborally.controller;

    exports dk.dtu.compute.se.pisd.designpatterns.observer;
}