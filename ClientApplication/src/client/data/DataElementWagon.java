package client.data;

public class DataElementWagon {
    public Integer numberWagon;
    public String arrivalDate;
    public String imagePath;
    public Double levelCorrectRecognize;

    public DataElementWagon(Integer numberWagon, String arrivalDate, String imagePath, Double levelCorrectRecognize) {
        this.numberWagon = numberWagon;
        this.arrivalDate = arrivalDate;
        this.imagePath = imagePath;
        this.levelCorrectRecognize = levelCorrectRecognize;
    }
}
