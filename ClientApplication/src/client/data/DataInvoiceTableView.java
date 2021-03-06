package client.data;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class DataInvoiceTableView {
    private SimpleStringProperty numberInvoice;
    private SimpleStringProperty nameSupplier;
    private SimpleIntegerProperty totalWagons;
    private SimpleStringProperty arrivalTrainDate;
    private SimpleStringProperty departureTrainDate;

    public DataInvoiceTableView(String numberInvoice,
                                String nameSupplier,
                                short totalWagons,
                                String arrivalDate,
                                String departureDate) {
        this.numberInvoice = new SimpleStringProperty(numberInvoice);
        this.nameSupplier = new SimpleStringProperty(nameSupplier);
        this.totalWagons = new SimpleIntegerProperty(totalWagons);
        this.arrivalTrainDate = new SimpleStringProperty(arrivalDate);
        this.departureTrainDate = new SimpleStringProperty(departureDate);
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

    public String getNameSupplier() {
        return nameSupplier.get();
    }

    public SimpleStringProperty nameSupplierProperty() {
        return nameSupplier;
    }

    public void setNameSupplier(String nameSupplier) {
        this.nameSupplier.set(nameSupplier);
    }

    public int getTotalWagons() {
        return totalWagons.get();
    }

    public SimpleIntegerProperty totalWagonsProperty() {
        return totalWagons;
    }

    public void setTotalWagons(int totalWagons) {
        this.totalWagons.set(totalWagons);
    }

    public String getArrivalTrainDate() {
        return arrivalTrainDate.get();
    }

    public SimpleStringProperty arrivalTrainDateProperty() {
        return arrivalTrainDate;
    }

    public void setArrivalTrainDate(String arrivalTrainDate) {
        this.arrivalTrainDate.set(arrivalTrainDate);
    }

    public String getDepartureTrainDate() {
        return departureTrainDate.get();
    }

    public SimpleStringProperty departureTrainDateProperty() {
        return departureTrainDate;
    }

    public void setDepartureTrainDate(String departureTrainDate) {
        this.departureTrainDate.set(departureTrainDate);
    }
}
