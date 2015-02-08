package com.snilius.ledman.data;

/**
 * @author victor
 * @since 2/8/15
 */
public class Status {
    private String r;
    private String g;
    private String b;

    public String getR() {
        return r;
    }

    public void setR(String r) {
        this.r = r;
    }

    public String getG() {
        return g;
    }

    public void setG(String g) {
        this.g = g;
    }

    public String getB() {
        return b;
    }

    public void setB(String b) {
        this.b = b;
    }

    public boolean isOn(){
        float fr = Float.parseFloat(r);
        float fg = Float.parseFloat(g);
        float fb = Float.parseFloat(b);

        return (fr>0||fg>0||fb>0) ? true : false;
    }
}
