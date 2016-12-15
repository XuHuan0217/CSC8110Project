package uk.ac.ncl.csc8110.huan.camera.model;

/**
 * Created by huan on 2016/12/13.
 */
public class RegPlate {
    private String reg;
    private Integer type;

    public RegPlate(String reg, Integer type) {
        this.reg = reg;
        this.type = type;
    }
    public RegPlate(){}

    public String getReg() {
        return reg;
    }

    public void setReg(String reg) {
        this.reg = reg;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
