package com.rockhoppertech.music.fx.pianoroll;

/*
 * #%L
 * Rocky Music FX
 * %%
 * Copyright (C) 1996 - 2014 Rockhopper Technologies
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="http://genedelisa.com/">Gene De Lisa</a>
 * 
 */
public class BeatHeader extends Region {

    private static final Logger logger = LoggerFactory
            .getLogger(BeatHeader.class);

    private PianorollPane pianorollPane;

    public BeatHeader() {
        getStyleClass().setAll("beatheader-control");
        this.setStyle("-fx-background-color: gray;");
        // this.setWidth(this.beatWidth * 64d);
        // this.setPrefWidth(this.beatWidth * 64d);
        this.setHeight(40);
        this.setPrefHeight(40);
        this.setWidth(800d);
        this.orientation = Orientation.HORIZONTAL;
        // this.units = 60d;
        // this.increment = units / 2;
        this.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                double beat = pianorollPane.getBeatFromX(e.getX());
                pianorollPane.setCurrentInsertBeat(beat);
                logger.debug("beat {}", beat);
            }
        });
    }

    /**
     * @return the pianorollPane
     */
    public PianorollPane getPianorollPane() {
        return pianorollPane;
    }

    /**
     * @param pianorollPane
     *            the pianorollPane to set
     */
    public void setPianorollPane(PianorollPane pianorollPane) {
        this.pianorollPane = pianorollPane;
        this.setLayoutX(pianorollPane.getLayoutX());
        // this.setPrefWidth(this.pianorollPane.getPrefWidth());
        this.setPrefWidth(this.pianorollPane.getWidth());
        this.prefWidthProperty().bindBidirectional(
                this.pianorollPane.prefWidthProperty());

        // this.layoutXProperty().bindBidirectional(
        // this.pianorollPane.layoutXProperty());

        this.unitsProperty.bind(pianorollPane.beatWidthProperty());
        this.increment = getUnits() / 2d;
        unitsProperty.addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> arg0,
                                Number old, Number newWidth) {
                logger.debug("listener new beatwidth or units {}", newWidth);
                increment = getUnits() / 2d;
                logger.debug("listener new increment {}", increment);
                drawLines();
            }
        });

        this.drawLines();
        // this.drawSnapGrid();
    }
    void removeGridLines() {
        ObservableList<Node> kids = this.getChildren();
        ObservableList<Node> lines = FXCollections.observableArrayList();
        for (Node n : kids) {
            if (n instanceof Line) {
                lines.add(n);
                logger.debug("removing line {}", n);
            }
        }
        this.getChildren().removeAll(lines);
    }
   /* void drawSnapGrid() {

        final double width = this.getLayoutBounds().getWidth();
        final double height = this.getLayoutBounds().getHeight();
        logger.debug("creating snapgrid {}", width);

        final double yoffset = 0d;
        for (double x = pianorollPane.getSnapX(); x < width; x += pianorollPane
                .getSnapX()) {
            Line line = new Line(x,
                    yoffset,
                    x,
                    height);
            line.setStroke(Color.RED);
            logger.debug("created line {}", line.getLayoutX());
            this.getChildren().add(line);
        }
    }*/

    private Orientation orientation;
    /** the line height */
    public static final double SIZE = 35;
    private double increment;
    private DoubleProperty unitsProperty = new SimpleDoubleProperty(60d);

    public DoubleProperty unitsProperty() {
        return unitsProperty;
    }
    public double getUnits() {
        return unitsProperty.get();
    }
    public void setUnits(double d) {
        unitsProperty.set(d);
    }

    public void drawLines() {
        //removeGridLines();
        getChildren().clear();

        // some vars we need
        double end = 0;
        double start = 0;
        double tickLength = 0;
        String text = null;

        // TODO but when we scroll, we run out. width is bound to pianoroll already.

        // use clipping bounds to calculate first tick and last tick location
        if (orientation == Orientation.HORIZONTAL) {
            //start = (this.getLayoutX() / increment) * increment;
            start = 0d;
            end = (((this.getLayoutX() + getLayoutBounds().getWidth()) / increment) + 1)
                    * increment;
            end = this.getLayoutX() + getLayoutBounds().getWidth() ;


         } else {
            start = (this.getLayoutY() / increment) * increment;
            end = (((this.getLayoutY() + getLayoutBounds().getHeight()) / increment) + 1)
                    * increment;
        }
        logger.debug("lines start {} end {}", start, end);

        // Make a special case of 0 to display the number
        // within the rule and draw a units label
        if (start == 0) {
            // text = Integer.toString(0) + (isMetric ? " cm" : " in");
            tickLength = 10;
            if (orientation == Orientation.HORIZONTAL) {
                Line line = new Line(0,
                        SIZE - 1,
                        0,
                        SIZE - tickLength - 1);
                line.setStroke(new Color(1, 1, 1, 1));
                this.getChildren().add(line);
                // g.drawString(text, 2, 21);
            } else {
                Line line = new Line(SIZE - 1,
                        0,
                        SIZE - tickLength - 1,
                        0);
                // g.drawString(text, 9, 10);
                this.getChildren().add(line);
            }
            text = null;
            start = increment;
        }

        // ticks and labels
        for (double i = start; i < end; i += increment) {
           // logger.debug("tick loop i {}", i);

            // FIXME some rounding error here
            // logger.debug("mod " + (i % units));
            if (i % getUnits() == 0) {
                tickLength = 10;
                // beats are 1 based
                text = String.format("%3.0f", (i / getUnits()) + 1);
                logger.debug("header text {}", text);
            } else {
                // logger.debug("null text ");
                tickLength = 7;
                text = null;
            }

            if (tickLength != 0) {
                if (orientation == Orientation.HORIZONTAL) {
                    Line line = new Line(i,
                            SIZE - 1,
                            i,
                            SIZE - tickLength - 1);
                    this.getChildren().add(line);
                    if (text != null) {
                        Text txt = new Text(text);
                        txt.setLayoutX(i - 3);
                        txt.setLayoutY(21);
                        this.getChildren().add(txt);
                    }
                } else {
                    Line line = new Line(SIZE - 1,
                            i,
                            SIZE - tickLength - 1,
                            i);
                    this.getChildren().add(line);
                    if (text != null) {
                        Text txt = new Text(text);
                        txt.setLayoutX(9);
                        txt.setLayoutY(i + 3);
                        this.getChildren().add(txt);
                    }
                }
            }
        }
    }

}
