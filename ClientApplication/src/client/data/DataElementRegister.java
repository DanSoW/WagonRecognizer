package client.data;

public class DataElementRegister {
    public String fkNumberInvoice;
    public Integer numberWagon;
    public Boolean arrivalMark;
    public Short serialNumber;
    public Short actualSerialNumber;
    public Float sD;

    public DataElementRegister(String fkNumberInvoice,
                               Integer numberWagon,
                               Boolean arrivalMark,
                               Short serialNumber,
                               Short actualSerialNumber,
                               Float sD) {
        this.fkNumberInvoice = fkNumberInvoice;
        this.numberWagon = numberWagon;
        this.arrivalMark = arrivalMark;
        this.serialNumber = serialNumber;
        this.actualSerialNumber = actualSerialNumber;
        this.sD = sD;
    }
}
