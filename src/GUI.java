import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import javax.swing.*;
import java.util.*;

/**
 * Created by logee on 30/10/2016.
 */
public class GUI extends Application{

    private static final int GAME_WIDTH = 1280;
    private static final int GAME_HEIGHT = 720;

    private static int dayCount =1;
    private static int money=10000;
    private static int currentStorage = 0;
    private static int totalStorage = 5;
    private static int storagePrice = 100;

    private Image good1 = new Image(getClass().getResource("prince1.png").toString());
    private Image good2 = new Image(getClass().getResource("prince2.png").toString());
    private Image good3 = new Image(getClass().getResource("prince3.png").toString());

    Goods g1 = new Goods(good1,1280/2-400,720/2-100,1000,6000);
    Goods g2 = new Goods(good2,1280/2-100,720/2-100,2000,15000);
    Goods g3 = new Goods(good3,1280/2+200,720/2-100,1500,10000);

    Label dayLb;
    Label moneyLb;
    Label storageLb;
    Label priceLb;

    Label g1Lb1;
    Label g1Lb2;
    Label g2Lb1;
    Label g2Lb2;
    Label g3Lb1;
    Label g3Lb2;
    Label g1Lb3;
    Label g2Lb3;
    Label g3Lb3;

    Label warning;
    Group histogram = new Group();

    class Graph {

        double xx;
        double yy;

        final double G_Height=200;
        final double G_Width=200;


        Graph(ArrayList<Integer> hist,double x,double y){
            xx=x-100.0;
            yy=y-230;


            Rectangle bg = new Rectangle();
            bg.setLayoutX(xx);
            bg.setLayoutY(yy);
            bg.setHeight(G_Height);
            bg.setWidth(G_Width);
            bg.setFill(Color.WHITE);
            histogram.getChildren().add(bg);

            List<Integer> recentTen= hist.subList(hist.size()-10,hist.size());


            int recentTenMax=Collections.max(recentTen);

            Label maxi = new Label(""+recentTenMax);
            maxi.setLayoutX(xx);
            maxi.setLayoutY(yy);
            histogram.getChildren().add(maxi);

            Label zero = new Label("0");
            zero.setLayoutX(xx);
            zero.setLayoutY(yy+180);
            histogram.getChildren().add(zero);




            double one = 180.0/recentTenMax;

            for (int i=0;i<9;i++){
                Line line = new Line();
                line.setStartX((i+1)*18+xx);
                line.setStartY(yy+200-recentTen.get(i)*one);
                line.setEndX((i+2)*18+xx);
                line.setEndY(yy+200-recentTen.get(i+1)*one);
                histogram.getChildren().add(line);
            }




        }
    }


    class Goods extends ImageView {

        int minPrice;
        int maxPrice;

        int currentPrice;

        int num;

        int spending;

        ArrayList<Integer> priceHist = new ArrayList<>();

        Goods(Image i,int x,int y, int min,int max){
            setImage(i);
            setLayoutX(x);
            setLayoutY(y);
            setFitWidth(200);
            setFitHeight(200);
            minPrice=min;
            maxPrice=max;
            num=0;
            spending=0;

            for (int j=0;j<10;j++){
                newPrice();
            }


            setOnMouseEntered(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    histogram.getChildren().clear();
                    Graph hist= new Graph(priceHist,event.getSceneX(),event.getSceneY());
                    setOnMouseMoved(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            histogram.getChildren().clear();
                            Graph hist= new Graph(priceHist,event.getSceneX(),event.getSceneY());
                        }
                    });


                }
            });

            setOnMouseExited(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {

                  histogram.getChildren().clear();
                }
            });

        }

        void newPrice(){
            Random r = new Random();
            currentPrice=r.nextInt(maxPrice-minPrice)+minPrice;
            priceHist.add(currentPrice);
        }

        int getPrice(){
            return currentPrice;
        }

        int getNum(){
            return num;
        }

        void buyOne(){
            if(currentStorage<totalStorage){
                if (money-currentPrice<0){
                    warningLabel(2);
                }else{
                    warningLabel(0);
                    num++;
                    spending+=currentPrice;
                    currentStorage++;
                    money-=currentPrice;
                    upDateLabels();
                }

            }else {
                warningLabel(1);
            }

        }

        void sellOne(){
            if (num>0){
                warningLabel(0);
                num--;
                spending-=currentPrice;
                currentStorage--;
                money+=currentPrice;
                upDateLabels();
            }

        }

        int getSpends(){
            if (spending>=0){
                return spending;
            }else{
                return 0;
            }
        }



    }

    void makeGoods(){

        root.getChildren().add(g1);
        root.getChildren().add(g2);
        root.getChildren().add(g3);
    }

    private final Group root = new Group();

    void iniAll(){
        makeNextDay();
        makeDays();
        makeMoney();
        makeStorage();
        root.getChildren().add(dayLb);
        root.getChildren().add(moneyLb);
        root.getChildren().add(storageLb);
        root.getChildren().add(priceLb);
        upgradeStorage();
        makeBuySellBut();
        makeGoods();
        makeLabel();
        makeGoodLabel();
        root.getChildren().add(histogram);
    }

    void makeLabel(){
        warning= new Label("Trade your Princes!");
        warning.setLayoutX(1280/2-400);
        warning.setLayoutY(50);
        warning.setFont(Font.font(100));
        root.getChildren().add(warning);
    }

    void warningLabel(int i){
        /*
        0 for empty
        1 for lack of storage
        2 for lack of money
         */
        warning.setTextFill(Color.RED);

        switch (i){
            case 0:
                warning.setText(" ");
                break;
            case 1:
                warning.setText("Not enough storage!");
                break;
            case 2:
                warning.setText("Not enough money!");
                break;
        }

    }

    void makeGoodLabel(){
        g1Lb1= new Label("Price: "+g1.getPrice());
        g1Lb2=new Label("Spending: "+g1.getSpends());
        g1Lb1.setLayoutX(g1.getLayoutX());
        g1Lb1.setLayoutY(g1.getLayoutY()+200);
        g1Lb1.setFont(Font.font(25));
        g1Lb2.setLayoutX(g1.getLayoutX());
        g1Lb2.setLayoutY(g1.getLayoutY()+230);
        g1Lb2.setFont(Font.font(25));

        g2Lb1= new Label("Price: "+g2.getPrice());
        g2Lb2=new Label("Spending: "+g2.getSpends());
        g2Lb1.setLayoutX(g2.getLayoutX());
        g2Lb1.setLayoutY(g2.getLayoutY()+200);
        g2Lb1.setFont(Font.font(25));
        g2Lb2.setLayoutX(g2.getLayoutX());
        g2Lb2.setLayoutY(g2.getLayoutY()+230);
        g2Lb2.setFont(Font.font(25));

        g3Lb1= new Label("Price: "+g3.getPrice());
        g3Lb2=new Label("Spending: "+g3.getSpends());
        g3Lb1.setLayoutX(g3.getLayoutX());
        g3Lb1.setLayoutY(g3.getLayoutY()+200);
        g3Lb1.setFont(Font.font(25));
        g3Lb2.setLayoutX(g3.getLayoutX());
        g3Lb2.setLayoutY(g3.getLayoutY()+230);
        g3Lb2.setFont(Font.font(25));

        g1Lb3= new Label("Number: "+g1.getNum());
        g1Lb3.setLayoutX(g1.getLayoutX());
        g1Lb3.setLayoutY(g1.getLayoutY()+260);
        g1Lb3.setFont(Font.font(25));

        g2Lb3= new Label("Number: "+g2.getNum());
        g2Lb3.setLayoutX(g2.getLayoutX());
        g2Lb3.setLayoutY(g2.getLayoutY()+260);
        g2Lb3.setFont(Font.font(25));

        g3Lb3= new Label("Number: "+g3.getNum());
        g3Lb3.setLayoutX(g3.getLayoutX());
        g3Lb3.setLayoutY(g3.getLayoutY()+260);
        g3Lb3.setFont(Font.font(25));


        root.getChildren().add(g1Lb1);
        root.getChildren().add(g1Lb2);
        root.getChildren().add(g2Lb1);
        root.getChildren().add(g2Lb2);
        root.getChildren().add(g3Lb1);
        root.getChildren().add(g3Lb2);
        root.getChildren().add(g1Lb3);
        root.getChildren().add(g2Lb3);
        root.getChildren().add(g3Lb3);



    }

    void updateGoodsLabel(){
        g1Lb1.setText("Price: "+g1.getPrice());
        g2Lb1.setText("Price: "+g2.getPrice());
        g3Lb1.setText("Price: "+g3.getPrice());

        g1Lb2.setText("Spending: "+g1.getSpends());
        g2Lb2.setText("Spending: "+g2.getSpends());
        g3Lb2.setText("Spending: "+g3.getSpends());

        g1Lb3.setText("Number: "+g1.getNum());
        g2Lb3.setText("Number: "+g2.getNum());
        g3Lb3.setText("Number: "+g3.getNum());
    }

    void makeBuySellBut(){
        Button b1 = new Button("BUY");
        Button b2 = new Button("BUY");
        Button b3 = new Button("BUY");

        b1.setLayoutX(g1.getLayoutX());
        b1.setLayoutY(g1.getLayoutY()+260+30);


        b2.setLayoutX(g2.getLayoutX());
        b2.setLayoutY(g2.getLayoutY()+260+30);

        b3.setLayoutX(g3.getLayoutX());
        b3.setLayoutY(g3.getLayoutY()+260+30);

        Button s1 = new Button("SELL");
        Button s2 = new Button("SELL");
        Button s3 = new Button("SELL");

        s1.setLayoutX(g1.getLayoutX());
        s1.setLayoutY(g1.getLayoutY()+300+30);


        s2.setLayoutX(g2.getLayoutX());
        s2.setLayoutY(g2.getLayoutY()+300+30);

        s3.setLayoutX(g3.getLayoutX());
        s3.setLayoutY(g3.getLayoutY()+300+30);

        b1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                g1.buyOne();
                updateGoodsLabel();
            }
        });

        b2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                g2.buyOne();
                updateGoodsLabel();
            }
        });

        b3.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                g3.buyOne();
                updateGoodsLabel();
            }
        });

        s1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                g1.sellOne();
                updateGoodsLabel();
            }
        });
        s2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                g2.sellOne();
                updateGoodsLabel();
            }
        });
        s3.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                g3.sellOne();
                updateGoodsLabel();
            }
        });





        root.getChildren().add(b1);
        root.getChildren().add(b2);
        root.getChildren().add(b3);
        root.getChildren().add(s1);
        root.getChildren().add(s2);
        root.getChildren().add(s3);

    }


    void upDateLabels(){
        dayLb.setText("Day: "+dayCount);
        moneyLb.setText("Coins: "+money);
        storageLb.setText("Storage: "+currentStorage+"/"+totalStorage);
        priceLb.setText("Price: "+storagePrice+"/day");
    }

    void makeDays(){
        dayLb = new Label("Day: "+dayCount);
        dayLb.setFont(Font.font(25));
    }

    void makeMoney() {
       moneyLb = new Label("Coins: "+money);
       moneyLb.setFont(Font.font(25));
       moneyLb.setLayoutY(30);
    }

    void makeStorage(){
        storageLb = new Label("Storage: "+currentStorage+"/"+totalStorage);
        storageLb.setFont(Font.font(25));
        storageLb.setLayoutY(60);

        priceLb = new Label("Price: "+storagePrice+"/day");
        priceLb.setFont(Font.font(25));
        priceLb.setLayoutY(90);
    }

    void upgradeStorage (){
        Button upgrade = new Button("UPGRADE");
        Button downgrade = new Button("DOWNGRADE");
        upgrade.setLayoutX(1150);
        downgrade.setLayoutX(1150);
        upgrade.setLayoutY(720/2-15);
        downgrade.setLayoutY(720/2+15);

        upgrade.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (totalStorage>=5 && totalStorage<=100){
                    if (money>=storagePrice*2){
                        warningLabel(0);
                        totalStorage=totalStorage*2;
                        storagePrice+=storagePrice;
                        money-=storagePrice;
                        upDateLabels();
                    }else {
                        warningLabel(2);
                    }
                }
            }
        });

        downgrade.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (totalStorage>=10){
                    warningLabel(0);
                    totalStorage=totalStorage/2;
                    storagePrice=storagePrice/2;
                    upDateLabels();
                }
            }
        });

        root.getChildren().add(upgrade);
        root.getChildren().add(downgrade);
    }

    void makeNextDay (){
        Button button = new Button("Next Day");
        button.setLayoutX(1150);
        button.setLayoutY(650);

        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                nextDay();
            }
        });

        root.getChildren().add(button);
    }

    void nextDay(){

        if (money-storagePrice>=0){
            warningLabel(0);
            dayCount++;
            if (dayCount==50){
                if (money>=20000){
                    warningLabel(0);
                    money-=20000;
                }else {
                    warning.setTextFill(Color.RED);
                    warning.setText("You don't have 20000");
                    money-=20000;
                }

            }
            money-=storagePrice;

            upDateLabels();
            g1.newPrice();
            g2.newPrice();
            g3.newPrice();
            updateGoodsLabel();
        }else {
            warningLabel(2);
        }


    }


    @Override
    public void start(Stage primaryStage) throws Exception {

        primaryStage.setTitle("Crazy Princess");
        Scene scene= new Scene(root,GAME_WIDTH ,GAME_HEIGHT);
        scene.setFill(Color.WHITE);

        iniAll();

        primaryStage.setScene(scene);
        primaryStage.show();

    }
}
