import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Main extends Application {

    private Canvas canvas;
    private GraphicsContext g;
    private Slider slider, amount,deltaX,deltaY;
    private BorderPane root;
    private double left=-90.,right=-60.0; //degrees
    private double[] mirrorsX, mirrorsY;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Mirrors");
        mirrorsX = new double[]{100., 700., 400.};
        mirrorsY = new double[]{100., 100., 100. + Math.sqrt(3) * 300.};

        slider = new Slider(left, right, (left+right)/2);
        slider.setSnapToTicks(true);
        //slider.setMinorTickCount(9999);
        slider.setBlockIncrement(0.001);
        slider.setShowTickMarks(true);
        amount = new Slider(12, 2000, 12);
        amount.setBlockIncrement(1);
        amount.setSnapToTicks(false);
        deltaX = new Slider(0., 1.5, 1.5);
        deltaX.setBlockIncrement(0.001);
        deltaX.setSnapToTicks(false);
        deltaY = new Slider(0., 2.5, 2.5);
        deltaY.setBlockIncrement(0.001);
        deltaY.setSnapToTicks(false);

        VBox bottom = new VBox(10, makeInput("Rotate: ", slider), makeInput("Render: ", amount),
                makeInput("DeltaX: ", deltaX), makeInput("DeltaY: ", deltaY));
        bottom.setStyle("-fx-padding: 10px; -fx-border-color: #444; -fx-border-width: 4px 0 0 0");
        bottom.setAlignment(Pos.CENTER);
        root = new BorderPane();
        drawCanvas();
        root.setStyle("-fx-border-color: #444; -fx-border-width: 4px");
        root.setBottom(bottom);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    private HBox makeInput(String text, Slider slider) {
        Label label = new Label();
        label.setStyle("-fx-font: 14pt monospace");
        if (Math.floor(slider.getBlockIncrement())==Math.ceil(slider.getBlockIncrement())){
            label.textProperty().bind(slider.valueProperty().asString(text + "%8.0f"));
        }else {
            label.textProperty().bind(slider.valueProperty().asString(text + "%8.3f"));
        }
        slider.setPrefWidth(1000);
        slider.valueProperty().addListener(e -> drawCanvas());
        HBox box = new HBox(10, slider, label);
        box.setAlignment(Pos.CENTER);
        return box;
    }

    private void drawCanvas() {
        canvas = new Canvas(800, 800);
        g = canvas.getGraphicsContext2D();
        g.setLineWidth(1);
        root.setCenter(canvas);
        g.strokePolygon(mirrorsX, mirrorsY, 3);
        if (Math.abs(slider.getValue()) <= 60) return;
        double x = 400., y = 100. + Math.sqrt(3) * 300.;
        double alpha = Math.toRadians(slider.getValue());
        int prevMirror = -1;
        int renderRange = (int)amount.getValue();
        while ((renderRange--) > 0) {
            g.moveTo(x, y);
            double new_x, new_y, x2 = x + (100. - y) / Math.tan(alpha), y2 = 100.;
            if (x2 >= 100. && x2 <= 700. && prevMirror != 1) {
                new_x = x2;
                prevMirror = 1;
                new_y = 100.;
                alpha = -alpha;
            } else {
                x2 = (100 - 100 * Math.sqrt(3) + x * Math.tan(alpha) - y) / (Math.tan(alpha) - Math.sqrt(3));
                y2 = Math.sqrt(3) * (x2 - 100) + 100;
                if (x2 >= 100. && x2 <= 400.0 && y2 >= 100.0 && y2 <= 100. + Math.sqrt(3) * 300. && prevMirror != 2) {
                    prevMirror = 2;
                    new_x = x2;
                    new_y = y2;
                    alpha = Math.atan((Math.sqrt(3) + Math.tan(alpha)) / (Math.sqrt(3) * Math.tan(alpha) - 1));
                } else {
                    prevMirror = 3;
                    x2 = (100 + 700 * Math.sqrt(3) + x * Math.tan(alpha) - y) / (Math.tan(alpha) + Math.sqrt(3));
                    y2 = Math.sqrt(3) * (700 - x2) + 100;
                    new_x = x2;
                    new_y = y2;
                    alpha = Math.atan((Math.sqrt(3) - Math.tan(alpha)) / (Math.sqrt(3) * Math.tan(alpha) + 1));
                }
            }
            g.lineTo(new_x, new_y);
            x = new_x;
            y = new_y;
            if (Math.abs(x - 400.) < deltaX.getValue() && Math.abs(y - 100. - Math.sqrt(3) * 300.) < deltaY.getValue()) {
                g.setStroke(Color.GREEN);
                break;
            } else if (Math.abs(x - 700.) < deltaX.getValue() && Math.abs(y - 100.) < deltaY.getValue()) {
                g.setStroke(Color.PURPLE);
                break;
            } else if (Math.abs(x - 100.) < deltaX.getValue() && Math.abs(y - 100.) < deltaY.getValue()) {
                g.setStroke(Color.BLUE);
                break;
            } else {
                g.setStroke(Color.RED);
            }
        }
        g.stroke();
        System.out.println("Final cords: " + x + " " + y);
    }

    public static void main(String[] args) {
        launch(args);
    }
}

//b==y-tg(alpha)*x
//y=tg(alpha)*(x-x1)+y2
//y=100.
//y=sqrt(3)*(x-100)+100
//y=sqrt(3)*(700-x)+100