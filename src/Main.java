import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.text.DecimalFormat;

// jar cfve client.jar Main Main.class Main$1.class

public class Main extends Application {

    final DecimalFormat df = new DecimalFormat("#.###");

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        final Stage window = primaryStage;
        window.getIcons().add(new Image("https://cdn2.iconfinder.com/data/icons/picons-basic-3/57/basic3-105_calculator-512.png"));
        window.setTitle("Euler's Method Calculator");
        window.setResizable(false);
        final GridPane calculationPane = new GridPane();
        final Scene login = new Scene(calculationPane, 500, 320);

        calculationPane.setPadding(new Insets(10, 10, 10, 10));
        calculationPane.setMaxWidth(250);
        calculationPane.setMaxHeight(150);
        calculationPane.setHgap(5);
        calculationPane.setVgap(5);
        Image bgImg = new Image("https://i.imgur.com/Mx9llms.png", 600, 600, false, true);
        calculationPane.setBackground(new Background(new BackgroundImage(bgImg, BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT)));

        HBox hb = new HBox();
        hb.setSpacing(25);

        VBox vb1 = new VBox();
        vb1.setSpacing(10);
        Label lelx = new Label("X Values: ");
        vb1.getChildren().add(lelx);

        VBox vb2 = new VBox();
        vb2.setSpacing(10);
        Label lely = new Label("Y Values: ");
        vb2.getChildren().add(lely);

        VBox vb3 = new VBox();
        vb3.setSpacing(10);
        Label lelyp = new Label("Y' Values: ");
        vb3.getChildren().add(lelyp);

        TextArea solutionX = new TextArea();
        solutionX.setPrefWidth(100);
        solutionX.setPrefHeight(300);
        solutionX.setEditable(false);
        vb1.getChildren().add(solutionX);

        TextArea solutionY = new TextArea();
        solutionY.setPrefWidth(100);
        solutionY.setPrefHeight(300);
        solutionY.setEditable(false);
        vb2.getChildren().add(solutionY);

        TextArea solutionYP = new TextArea();
        solutionYP.setPrefWidth(100);
        solutionYP.setPrefHeight(300);
        solutionYP.setEditable(false);
        vb3.getChildren().add(solutionYP);

        hb.getChildren().add(vb1);
        hb.getChildren().add(vb2);
        hb.getChildren().add(vb3);

        VBox vb = new VBox();
        vb.setSpacing(12);

        TextField inX = new TextField();
        inX.setPrefWidth(100);
        inX.setText("Initial X Value");

        TextField inY = new TextField();
        inY.setPrefWidth(100);
        inY.setText("Initial Y Value");

        TextField dx = new TextField();
        dx.setPrefWidth(100);
        dx.setText("dx");

        TextField yPrime = new TextField();
        yPrime.setPrefWidth(100);
        yPrime.setText("dy/dx");

        TextField startX = new TextField();
        startX.setPrefWidth(100);
        startX.setText("Starting X");

        TextField endX = new TextField();
        endX.setPrefWidth(100);
        endX.setText("Ending X");

        Button calculateButton = new Button("Calculate");
        calculateButton.setPrefWidth(100);

        Label credits = new Label("Code: Justin Chang");
        Label credits2 = new Label("String to Math: Boann");

        vb.getChildren().add(inX);
        vb.getChildren().add(inY);
        vb.getChildren().add(dx);
        vb.getChildren().add(yPrime);
        vb.getChildren().add(startX);
        vb.getChildren().add(endX);
        vb.getChildren().add(calculateButton);
        vb.getChildren().add(credits);
        vb.getChildren().add(credits2);
        hb.getChildren().add(vb);

        calculateButton.setOnAction(actionEvent -> {
            calculate(Double.parseDouble(inX.getText()), Double.parseDouble(inY.getText()), Double.parseDouble(dx.getText()), yPrime.getText(), Double.parseDouble(startX.getText()), Double.parseDouble(endX.getText()), solutionX, solutionY, solutionYP);
        });

        calculationPane.getChildren().add(hb);
        window.setScene(login);
        primaryStage.show();
    }

    public double calculate(double inX, double inY, double dx, String yPrime, double startX, double endX, TextArea X,
                            TextArea Y, TextArea YP) {
        if (inX > endX) {
            return 0;
        }
        double derivativeV = eval(varToVal(inX, inY, yPrime));
        X.setText(X.getText() + df.format(inX) + "\n");
        Y.setText(Y.getText() + df.format(inY) + "\n");
        YP.setText(YP.getText() + df.format(derivativeV) + "\n");
        return calculate(inX + dx, inY + 1.0 * derivativeV * dx, dx, yPrime, startX, endX, X, Y, YP);
    }

    public String varToVal(double X, double Y, String yPrime) {
        String out = "";
        for (int i = 0; i < yPrime.length(); i++)
            if (yPrime.charAt(i) == 'X' || yPrime.charAt(i) == 'x')
                out += X;
            else if (yPrime.charAt(i) == 'Y' || yPrime.charAt(i) == 'y')
                out += Y;
            else
                out += yPrime.charAt(i);
        return out;
    }

    /*
     * Credits to @Boann on Stack Overflow.
     */
    public static double eval(final String str) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char) ch);
                return x;
            }

            // Grammar:
            // expression = term | expression `+` term | expression `-` term
            // term = factor | term `*` factor | term `/` factor
            // factor = `+` factor | `-` factor | `(` expression `)`
            //        | number | functionName factor | factor `^` factor

            double parseExpression() {
                double x = parseTerm();
                for (; ; ) {
                    if (eat('+')) x += parseTerm(); // addition
                    else if (eat('-')) x -= parseTerm(); // subtraction
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (; ; ) {
                    if (eat('*')) x *= parseFactor(); // multiplication
                    else if (eat('/')) x /= parseFactor(); // division
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor(); // unary plus
                if (eat('-')) return -parseFactor(); // unary minus

                double x;
                int startPos = this.pos;
                if (eat('(')) { // parentheses
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                } else if (ch >= 'a' && ch <= 'z') { // functions
                    while (ch >= 'a' && ch <= 'z') nextChar();
                    String func = str.substring(startPos, this.pos);
                    x = parseFactor();
                    if (func.equals("sqrt")) x = Math.sqrt(x);
                    else if (func.contains("pow")) x = Math.pow(x, Integer.parseInt(func.substring(3)));
                    else if (func.equals("sin")) x = Math.sin(x);
                    else if (func.equals("cos")) x = Math.cos(x);
                    else if (func.equals("tan")) x = Math.tan(x);
                    else throw new RuntimeException("Unknown function: " + func);
                } else {
                    throw new RuntimeException("Unexpected: " + (char) ch);
                }

                if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation

                return x;
            }
        }.parse();
    }

}
