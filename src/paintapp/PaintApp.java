package paintapp;

import java.util.Stack;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;

public class PaintApp extends Application {

    @Override
    public void start(Stage primaryStage) {

        /**
         * Array list for all shapes that will store each and every shape from
         * canvas
         */
        Stack<Shape> undoHistory = new Stack();

        /**
         * Toggle buttons for controls
         */
        ToggleButton linebtn = new ToggleButton("Line");
        ToggleButton rectbtn = new ToggleButton("Rectange");
        ToggleButton circlebtn = new ToggleButton("Circle");

        ToggleButton[] toolsArr = {linebtn, rectbtn, circlebtn};

        ToggleGroup tools = new ToggleGroup();

        /**
         * for all toggle buttons
         */
        for (ToggleButton tool : toolsArr) {
            tool.setMinWidth(90);
            tool.setToggleGroup(tools);
            tool.setCursor(Cursor.HAND);
        }

        /**
         * color picker
         */
        ColorPicker cpLine = new ColorPicker(Color.BLACK);
        ColorPicker cpFill = new ColorPicker(Color.TRANSPARENT);

        // Text field to inpur width of line 
        TextField textField = new TextField();
        Label lbfill = new Label("Fill Color");
        Label lbout = new Label("Outline Color");
        Label lb = new Label(" Line Width ");

        /**
         * creating undo button
         */
        Button undo = new Button("Undo");
        undo.setMinWidth(90);
        undo.setCursor(Cursor.HAND);
        undo.setTextFill(Color.ALICEBLUE);
        undo.setStyle("-fx-background-color: #666;");

        /**
         * Vertical box for controls
         */
        VBox btns = new VBox(10);
        btns.getChildren().addAll(linebtn, rectbtn, circlebtn, lbout, cpLine, lbfill, cpFill, lb, textField, undo);
        btns.setPadding(new Insets(5));
        btns.setStyle("-fx-background-color: #999");
        btns.setPrefWidth(100);

        /* ----------Drow Canvas---------- */
        Canvas canvas = new Canvas(1080, 790);
        GraphicsContext gc;
        gc = canvas.getGraphicsContext2D();

        Line line = new Line();
        Rectangle rect = new Rectangle();
        Circle circ = new Circle();

        /**
         * Main program logic for mouse events
         *
         */
        // 1) Mouse pressed event
        canvas.setOnMousePressed(e -> {
            double a = 0;

            // Exception handling if user input wrong input while entring the width of line 
            try {
                if (textField.getText().equals("")) {
                    a = 0;
                } else {
                    a = Double.parseDouble(textField.getText());
                }
            } catch (IllegalArgumentException ex) {

                // this willl promt a dailouge box as a warning
                new Alert(Alert.AlertType.WARNING, "Invalid Line Width").showAndWait();

            }
            if (linebtn.isSelected()) {

                if (a > 0) {

                    gc.setLineWidth(a);
                    gc.setStroke(cpLine.getValue());
                    line.setStartX(e.getX());
                    line.setStartY(e.getY());
                } else {
                    gc.setLineWidth(1);
                    gc.setStroke(cpLine.getValue());
                    line.setStartX(e.getX());
                    line.setStartY(e.getY());

                }
            } else if (rectbtn.isSelected()) {

                if (a > 0) {
                    gc.setLineWidth(a);
                    gc.setStroke(cpLine.getValue());
                    gc.setFill(cpFill.getValue());
                    rect.setX(e.getX());
                    rect.setY(e.getY());
                } else {
                    gc.setLineWidth(1);
                    gc.setStroke(cpLine.getValue());
                    gc.setFill(cpFill.getValue());
                    rect.setX(e.getX());
                    rect.setY(e.getY());

                }
            } else if (circlebtn.isSelected()) {

                if (a > 0) {
                    gc.setLineWidth(a);
                    gc.setStroke(cpLine.getValue());
                    gc.setFill(cpFill.getValue());
                    circ.setCenterX(e.getX());
                    circ.setCenterY(e.getY());
                } else {
                    gc.setLineWidth(1);
                    gc.setStroke(cpLine.getValue());
                    gc.setFill(cpFill.getValue());
                    circ.setCenterX(e.getX());
                    circ.setCenterY(e.getY());

                }
            }
        });

        // 2) Mouse Release event 
        canvas.setOnMouseReleased(e -> {
            if (linebtn.isSelected()) {
                line.setEndX(e.getX());
                line.setEndY(e.getY());
                gc.strokeLine(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY());

                undoHistory.push(new Line(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY()));
            } else if (rectbtn.isSelected()) {
                rect.setWidth(Math.abs((e.getX() - rect.getX())));
                rect.setHeight(Math.abs((e.getY() - rect.getY())));

                if (rect.getX() > e.getX()) {
                    rect.setX(e.getX());
                }

                if (rect.getY() > e.getY()) {
                    rect.setY(e.getY());
                }

                gc.fillRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
                gc.strokeRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());

                undoHistory.push(new Rectangle(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight()));

            } else if (circlebtn.isSelected()) {
                circ.setRadius((Math.abs(e.getX() - circ.getCenterX()) + Math.abs(e.getY() - circ.getCenterY())) / 2);

                if (circ.getCenterX() > e.getX()) {
                    circ.setCenterX(e.getX());
                }
                if (circ.getCenterY() > e.getY()) {
                    circ.setCenterY(e.getY());
                }

                gc.fillOval(circ.getCenterX(), circ.getCenterY(), circ.getRadius(), circ.getRadius());
                gc.strokeOval(circ.getCenterX(), circ.getCenterY(), circ.getRadius(), circ.getRadius());

                undoHistory.push(new Circle(circ.getCenterX(), circ.getCenterY(), circ.getRadius()));
            }
            Shape lastUndo = undoHistory.lastElement();
            lastUndo.setFill(gc.getFill());
            lastUndo.setStroke(gc.getStroke());
            lastUndo.setStrokeWidth(gc.getLineWidth());

        });

        // color picker
        cpLine.setOnAction(e -> {
            gc.setStroke(cpLine.getValue());
        });
        cpFill.setOnAction(e -> {
            gc.setFill(cpFill.getValue());
        });

        // Undo
        undo.setOnAction(e -> {
            if (!undoHistory.empty()) {
                gc.clearRect(0, 0, 1080, 790);
                Shape removedShape = undoHistory.lastElement();
                if (removedShape.getClass() == Line.class) {
                    Line tempLine = (Line) removedShape;
                    tempLine.setFill(gc.getFill());
                    tempLine.setStroke(gc.getStroke());
                    tempLine.setStrokeWidth(gc.getLineWidth());

                } else if (removedShape.getClass() == Rectangle.class) {
                    Rectangle tempRect = (Rectangle) removedShape;
                    tempRect.setFill(gc.getFill());
                    tempRect.setStroke(gc.getStroke());
                    tempRect.setStrokeWidth(gc.getLineWidth());

                } else if (removedShape.getClass() == Circle.class) {
                    Circle tempCirc = (Circle) removedShape;
                    tempCirc.setStrokeWidth(gc.getLineWidth());
                    tempCirc.setFill(gc.getFill());
                    tempCirc.setStroke(gc.getStroke());

                }
                undoHistory.pop();

                for (int i = 0; i < undoHistory.size(); i++) {
                    Shape shape = undoHistory.elementAt(i);
                    if (shape.getClass() == Line.class) {
                        Line temp = (Line) shape;
                        gc.setLineWidth(temp.getStrokeWidth());
                        gc.setStroke(temp.getStroke());
                        gc.setFill(temp.getFill());
                        gc.strokeLine(temp.getStartX(), temp.getStartY(), temp.getEndX(), temp.getEndY());
                    } else if (shape.getClass() == Rectangle.class) {
                        Rectangle temp = (Rectangle) shape;
                        gc.setLineWidth(temp.getStrokeWidth());
                        gc.setStroke(temp.getStroke());
                        gc.setFill(temp.getFill());
                        gc.fillRect(temp.getX(), temp.getY(), temp.getWidth(), temp.getHeight());
                        gc.strokeRect(temp.getX(), temp.getY(), temp.getWidth(), temp.getHeight());
                    } else if (shape.getClass() == Circle.class) {
                        Circle temp = (Circle) shape;
                        gc.setLineWidth(temp.getStrokeWidth());
                        gc.setStroke(temp.getStroke());
                        gc.setFill(temp.getFill());
                        gc.fillOval(temp.getCenterX(), temp.getCenterY(), temp.getRadius(), temp.getRadius());
                        gc.strokeOval(temp.getCenterX(), temp.getCenterY(), temp.getRadius(), temp.getRadius());
                    } else if (shape.getClass() == Ellipse.class) {
                        Ellipse temp = (Ellipse) shape;
                        gc.setLineWidth(temp.getStrokeWidth());
                        gc.setStroke(temp.getStroke());
                        gc.setFill(temp.getFill());
                        gc.fillOval(temp.getCenterX(), temp.getCenterY(), temp.getRadiusX(), temp.getRadiusY());
                        gc.strokeOval(temp.getCenterX(), temp.getCenterY(), temp.getRadiusX(), temp.getRadiusY());
                    }
                }
            } else {
                new Alert(Alert.AlertType.WARNING, "There is No Action to Undo").showAndWait();
            }
        });

        /* ----------STAGE & SCENE---------- */
        BorderPane pane = new BorderPane();
        pane.setLeft(btns);
        pane.setCenter(canvas);

        Scene scene = new Scene(pane, 1080, 700);

        primaryStage.setTitle("Paint");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
