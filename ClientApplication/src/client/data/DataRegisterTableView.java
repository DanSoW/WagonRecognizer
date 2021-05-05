package client.data;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class DataRegisterTableView {
    private SimpleStringProperty numberInvoice;
    private SimpleIntegerProperty numberWagon;
    private SimpleStringProperty arrivalMark;
    private SimpleIntegerProperty serialNumber;
    private SimpleIntegerProperty actualSerialNumber;
    private SimpleFloatProperty sD;

    public DataRegisterTableView(String numberInvoice,
                                 int numberWagon,
                                 String arrivalMark,
                                 short serialNumber,
                                 short actualSerialNumber,
                                 float sD) {
        this.numberInvoice = new SimpleStringProperty(numberInvoice);
        this.numberWagon = new SimpleIntegerProperty(numberWagon);
        this.arrivalMark = new SimpleStringProperty(arrivalMark);
        this.serialNumber = new SimpleIntegerProperty(serialNumber);
        this.actualSerialNumber = new SimpleIntegerProperty(actualSerialNumber);
        this.sD = new SimpleFloatProperty(sD);
    }

    public String getArrivalMark() {
        return arrivalMark.get();
    }

    public SimpleStringProperty arrivalMarkProperty() {
        return arrivalMark;
    }

    public void setArrivalMark(String arrivalMark) {
        this.arrivalMark.set(arrivalMark);
    }

    public float getsD() {
        return sD.get();
    }

    public SimpleFloatProperty sDProperty() {
        return sD;
    }

    public void setsD(float sD) {
        this.sD.set(sD);
    }

    public String getNumberInvoice() {
        return numberInvoice.get();
    }

    public SimpleStringProperty numberInvoiceProperty() {
        return numberInvoice;
    }

    public void setNumberInvoice(String numberInvoice) {
        this.numberInvoice.set(numberInvoice);
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

    public int getSerialNumber() {
        return serialNumber.get();
    }

    public SimpleIntegerProperty serialNumberProperty() {
        return serialNumber;
    }

    public void setSerialNumber(int serialNumber) {
        this.serialNumber.set(serialNumber);
    }

    public int getActualSerialNumber() {
        return actualSerialNumber.get();
    }

    public SimpleIntegerProperty actualSerialNumberProperty() {
        return actualSerialNumber;
    }

    public void setActualSerialNumber(int actualSerialNumber) {
        this.actualSerialNumber.set(actualSerialNumber);
    }
}
