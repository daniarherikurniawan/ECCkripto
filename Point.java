package ecc;

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
public class Point {
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
        return ("(" + x + "," + y + ")"); 
    }
}
