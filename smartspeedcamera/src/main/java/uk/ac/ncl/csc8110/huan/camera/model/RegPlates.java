package uk.ac.ncl.csc8110.huan.camera.model;

import com.google.gson.Gson;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by huan on 2016/12/11.
 */
public class RegPlates {
    private List<RegPlate> regs;


    public List<RegPlate> getRegs() {
        return regs;
    }

    public void setRegs(List<RegPlate> regs) {
        this.regs = regs;
    }
    public void addRegs(String reg,Integer type){
        if(this.regs == null) this.regs = new ArrayList<RegPlate>();
        this.regs.add(new RegPlate(reg,type));
    }

    public RegPlate getRandomReg(){
        return regs.get(RandomUtils.nextInt(0,regs.size()));
    }

    public static void main(String [] args){
        String fileName = "RandomPlates";
        int max = 500;
        RegPlates regPlates = new RegPlates();
        for(int i =0;i<max;i++){
            regPlates.addRegs(randomReg(),reandomType());
        }
        Gson gson = new Gson();
        try  {
            Writer writer = new FileWriter(fileName);
            String s = gson.toJson(regPlates);
            writer.write(s);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static String randomReg(){
        String p1 = RandomStringUtils.randomAlphabetic(2).toUpperCase();
        int p2 = RandomUtils.nextInt(10, 100);
        String p3 = RandomStringUtils.randomAlphabetic(3).toUpperCase();
        return p1 + p2 + " " + p3;
    }
    private static Integer reandomType(){
        return RandomUtils.nextInt(0,3);
    }
}
