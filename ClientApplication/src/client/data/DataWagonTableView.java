package client.data;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class DataWagonTableView {
    private SimpleIntegerProperty numberWagon;
    private SimpleStringProperty arrivalDate;
    private SimpleStringProperty imagePath;
    private SimpleDoubleProperty levelCorrectRecognize;

    public DataWagonTableView(int numberWagon,
                              String arrivalDate,
                              String imagePath,
                              double levelCorrectRecognize) {
        this.numberWagon = new SimpleIntegerProperty(numberWagon);
        this.arrivalDate = new SimpleStringProperty(arrivalDate);
        this.imagePath = new SimpleStringProperty(imagePath);
        this.levelCorrectRecognize = new SimpleDoubleProperty(levelCorrectRecognize);
    }

    public int getNumberWagon() {
        return numberWagon.get();
    }

    public SimpleIntegerProperty numberWagonProperty() {
        return numberWagon;
    }

    public void setNumberWagon(int numberWagon) {
        this.numberWagon.set(numberWagon);
    }

    public String getArrivalDate() {
        return arrivalDate.get();
    }

    public SimpleStringProperty arrivalDateProperty() {
        return arrivalDate;
    }

    public void setArrivalDate(String arrivalDate) {
        this.arrivalDate.set(arrivalDate);
    }

    public String getImagePath() {
        return imagePath.get();
    }

    public SimpleStringProperty imagePathProperty() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath.set(imagePath);
    }

    public double getLevelCorrectRecognize() {
        return levelCorrectRecognize.get();
    }

    public SimpleDoubleProperty levelCorrectRecognizeProperty() {
        return levelCorrectRecognize;
    }

    public void setLevelCorrectRecognize(double levelCorrectRecognize) {
        this.levelCorrectRecognize.set(levelCorrectRecognize);
    }
}
