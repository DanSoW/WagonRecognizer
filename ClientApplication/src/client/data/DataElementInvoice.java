package client.data;

public class DataElementInvoice {
    public String numberInvoice;
    public String nameSupplier;
    public Short totalWagons;
    public String arrivalTrainDate;
    public String departureTrainDate;

    public DataElementInvoice(String numberInvoice, String nameSupplier, Short totalWagons, String arrivalTrainDate, String departureTrainDate) {
        this.numberInvoice = numberInvoice;
        this.nameSupplier = nameSupplier;
        this.totalWagons = totalWagons;
        this.arrivalTrainDate = arrivalTrainDate;
        this.departureTrainDate = departureTrainDate;
    }
}
