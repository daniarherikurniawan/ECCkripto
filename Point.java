package ecc;

import java.io.Serializable;
import java.math.BigInteger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author 
 */
public class Point implements Serializable{
    private BigInteger x;
    private BigInteger y;

    public Point(BigInteger x, BigInteger y) {
        this.x = x;
        this.y = y;
    }
    public Point() {
        this.x = new BigInteger("0");
        this.y = new BigInteger("0");

    }

    
    public BigInteger getX() {
        return x;
    }

    public BigInteger getY() {
        return y;
    }
    
    
    @Override
    public String toString() {
        return new StringBuffer("{\n\t\t" + x + ",\n\t\t" + y+"\n\t}" ).toString(); 
    }
}
