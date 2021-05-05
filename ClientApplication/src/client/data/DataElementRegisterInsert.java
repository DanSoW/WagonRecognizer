package client.data;

public class DataElementRegisterInsert {
    public String fkNumberInvoice;
    public Integer numberWagon;
    public Short serialNumber;
    public Float sD;

    public DataElementRegisterInsert(String fkNumberInvoice, Integer numberWagon, Short serialNumber, Float sD) {
        this.fkNumberInvoice = fkNumberInvoice;
        this.numberWagon = numberWagon;
        this.serialNumber = serialNumber;
        this.sD = sD;
    }
}
