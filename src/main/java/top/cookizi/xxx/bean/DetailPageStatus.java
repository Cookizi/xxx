package top.cookizi.xxx.bean;

public enum DetailPageStatus {
    FAIL(-1),
    INIT(0),
    SUCCESS(1),;
    private int code;

    DetailPageStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
