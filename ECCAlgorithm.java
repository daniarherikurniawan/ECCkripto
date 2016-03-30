package ecc;

/**
 * kalo mau ganti nilai prime number, harus cek dulu titik basisnya ada gak di kurva, tinggal masukin aja ke fungsi ECC lalu dapetin PM1: PM1 ini bisa jadi basis karena ada pada kurva
 * kalo dapet y tidak ditemukan (pas nyari PM1), maka X itu ga punya pasangannya dan harus diganti
 * kalo udah dapet pasangan PM1-> bisa jadi basis
 * untuk ngecek juga bisa pake nilai kPB = b.(kB). harus sama
 * notasinya pake yang di slide halaman 60 yang elgaman ECC. b=privatekey (kalo di sini b itu variabel koefisien dari E). privatekey punya variabel sendiri
 */

import java.util.Scanner;
//import java.awt.Point;
import java.math.BigInteger;

/*hooy*/

public class ECCAlgorithm {
     /*160*/
      static BigInteger primeNumber = new BigInteger("E95E4A5F737059DC60DFC7AD95B3D8139515620F",16);//prime number 
      static BigInteger a = new BigInteger("340E7BE2A280EB74E2BE61BADA745D97E8F7C300",16); //coefficient of E
      static BigInteger b = new BigInteger("1E589A8595423412134FAA2DBDEC95C8D8675E58",16); //coefficient of E

        static Point basis = new Point(new BigInteger("BED5AF16EA3F6A4F62938C4631EB5AF7BDBCDBC3",16),
        		new BigInteger("1667CB477A1A8EC338F94741669C976316DA6321",16)); //basis as public key ---> harus ada di kurva, harus dicek dulu untuk tiap kurva.
        
        public static BigInteger functionECC(BigInteger x) {
            //int result = 1;
        	BigInteger ySquare; 
        	BigInteger y = new BigInteger("1");
        	ySquare = (a.multiply(x).add(x.pow(3)).add(new BigInteger(b+""))).mod(primeNumber) ;//; // this is the E function y^2 = x^3 + ax + b
            //System.out.println("nilai ySquare: " + ySquare + "\n");
            
            while ((y.pow(2).subtract(ySquare).mod(primeNumber)).compareTo(new BigInteger("0")) !=0 )  {
                //System.out.println("ecc --- "+y.pow(2).subtract(ySquare).mod(primeNumber));     
                //System.out.println("y sebelum: " + y);
                y=y.add(new BigInteger("1"));
                //System.out.println("y sesudah: " + y);
                if (y.compareTo(new BigInteger("1000")) == 0) {
                    y = new BigInteger("0");
                    break;
                }
            }
            
            return y;
            
        } 
        
        public static BigInteger lambdaDuplication(BigInteger xp, BigInteger yp) {
            
            BigInteger nominator = a.add(new BigInteger( ""+3*(int)Math.pow(xp.doubleValue(),2)));
            BigInteger bigDenominator = yp.multiply(new BigInteger("2"));
            
            BigInteger inverseDenominator = bigDenominator.modInverse(primeNumber);
                   
            BigInteger result = nominator.multiply(inverseDenominator).mod(primeNumber);
            //System.out.println("nominator: " + nominator + "\ndenominator: " + bigDenominator + "\nInverse denominator: " + intInverseDenominator + "\nResult: " + result);
            return result;
        }

        public static BigInteger lambdaAddition(BigInteger xp, BigInteger yp, BigInteger xq, BigInteger yq) {
        	BigInteger nominator = (yp.subtract(yq));
        	BigInteger bigDenominator = (xp.subtract(xq));

            BigInteger inverseDenominator = bigDenominator.modInverse(primeNumber);            

            BigInteger result = (nominator.multiply(inverseDenominator)).mod(primeNumber);
            //System.out.println("nominator: " + nominator + " | denominator: " + bigDenominator + " | Inverse denominator: " + inverseDenominator);
            return result;
        }
        
        public Point encrypt (Point pubKey, BigInteger plainText) {
        	Point result = new Point();
        	
        	BigInteger nilaiX = plainText;
        	BigInteger nilaiY = new BigInteger("0");
            BigInteger lambdaDup, lambdaAdd, tempLambda;
            //int privateKey = 80; //privatekey, b=privatekey buat yang di bawah2 (sesuai slide)
            int k = 1; //bilangan yang dipilih pengirim pesan selang [1,p-1]
            BigInteger xr, yr, tempX, tempY;
            BigInteger xp, yp, xq, yq;
            int i; //for iterating
           //System.out.println("proses enkripsi mulai");
            //koordinat = new Point(koordinat.getX(),nilaiBaruY);
            nilaiY = functionECC(nilaiX);
            //System.out.println("proses enkripsi mulai");
            Point PM1= new Point(nilaiX, nilaiY);
//            System.out.println("Nilai PM1: " + PM1.toString());
            /*
             * hitung koordinat xr dan yr menggunakan for loop
            */           
            //System.out.println("proses enkripsi mulai");

            Point PB = pubKey;
            //System.out.println(pubKey.toString() + "\n");

            /*
            lalu, hitung kPB = k*(privatekey*basis)
            */
            xr = new BigInteger("0");
            yr = new BigInteger("0");
            nilaiX = PB.getX();
            nilaiY = PB.getY();
            tempX = nilaiX;
            tempY = nilaiY;
            //System.out.println("proses enkripsi mulai --lambda");
            tempLambda = lambdaDuplication(tempX, tempY);
            
                xr = ((( (tempLambda.pow(2)).subtract(nilaiX).subtract(tempX)).mod(primeNumber).add(primeNumber)).mod(primeNumber));
                yr = (((tempLambda.multiply(tempX.subtract(xr))).subtract(tempY)).mod(primeNumber).add(primeNumber)).mod(primeNumber); // (a % b + b) % b modulo for giving positive value (a%b give negative!)
                //xr = (tempLambda.pow(2).subtract(nilaiX) - nilaiX - tempX)%intPrimeNumber + intPrimeNumber)%intPrimeNumber;
                //yr = (((tempLambda*(tempX-xr)) - tempY)%intPrimeNumber + intPrimeNumber)%intPrimeNumber; // (a % b + b) % b modulo for giving positive value (a%b give negative!)
                tempX = xr;
                tempY = yr;
                //System.out.println("Nilai lambda: " + tempLambda);
                //System.out.println("\ni = " + i + "\nnilai xr dan yr: " + xr + " " + yr + "\n---------------\n");
                lambdaAdd = lambdaAddition(nilaiX, nilaiY, tempX, tempY);
                tempLambda = lambdaAdd;
            
            Point kPB = new Point(PB.getX(),PB.getY());
//            System.out.println("\nNilai kPB: " + kPB.toString());
            
            
            /*
            lalu, hitung kB=k*basis
            */
            xr = new BigInteger("0");
            yr = new BigInteger("0");
            nilaiX = basis.getX();
            nilaiY = basis.getY();
            tempX = nilaiX;
            tempY = nilaiY;
            tempLambda = lambdaDuplication(tempX, tempY);

            for (i=1; i<k; i++) {
                xr = ((( (tempLambda.pow(2)).subtract(nilaiX).subtract(tempX)).mod(primeNumber).add(primeNumber)).mod(primeNumber));
                yr = (((tempLambda.multiply(tempX.subtract(xr))).subtract(tempY)).mod(primeNumber).add(primeNumber)).mod(primeNumber); // (a % b + b) % b modulo for giving positive value (a%b give negative!)
                //xr = (((int)Math.pow(tempLambda, 2) - nilaiX - tempX)%intPrimeNumber + intPrimeNumber)%intPrimeNumber;
                //yr = (((tempLambda*(tempX-xr)) - tempY)%intPrimeNumber + intPrimeNumber)%intPrimeNumber; // (a % b + b) % b modulo for giving positive value (a%b give negative!)
                tempX = xr;
                tempY = yr;
                //System.out.println("Nilai lambda: " + tempLambda);
                //System.out.println("\ni = " + i + "\nnilai xr dan yr: " + xr + " " + yr + "\n---------------\n");
                lambdaAdd = lambdaAddition(nilaiX, nilaiY, tempX, tempY);
                tempLambda = lambdaAdd;
            }
            
            /*
            hitung titik (PM + kPB) sebagai titik 2 dari PC (PC2)
            */
            xp = PM1.getX();
            yp = PM1.getY();
            xq = kPB.getX();
            yq = kPB.getY();
            lambdaAdd = lambdaAddition(xp, yp, xq, yq);
            tempLambda = lambdaAdd;
            //System.out.println(xp + " -- " + yp + " -- " + xq + " -- " + yq + " -- " + tempLambda);
            xr = ((( ((tempLambda.pow(2)).subtract(xp)).subtract(xq)).mod(primeNumber).add(primeNumber)).mod(primeNumber));
            //System.out.println("nilai xr- "+xr);
            yr = (((tempLambda.multiply(xp.subtract(xr))).subtract(yp)).mod(primeNumber).add(primeNumber)).mod(primeNumber); // (a % b + b) % b modulo for giving positive value (a%b give negative!)
            //xr = (((int)Math.pow(tempLambda, 2) - xp - xq)%intPrimeNumber + intPrimeNumber)%intPrimeNumber;
            //yr = (((tempLambda*(xp-xr)) - yp)%intPrimeNumber + intPrimeNumber)%intPrimeNumber; // (a % b + b) % b modulo for giving positive value (a%b give negative!)
          
            Point PC2 = new Point(xr,yr);
//            System.out.println("\nNilai PC2: " + PC2.toString() + " << PM + kPB");
            
            result = PC2;
    		return result;
    	}

    	private BigInteger decrypt(BigInteger priKey, Point resultCipher) {

        	Point result = new Point();
        	BigInteger tempLambda;
        	BigInteger privateKey = priKey; //privatekey, b=privatekey buat yang di bawah2 (sesuai slide)
            BigInteger xr, yr, tempX, tempY;
            BigInteger xp, yp, xq, yq;
    		//lalu, hitung bkB = privatekey*(k*basis)
            xr = new BigInteger("0");
            yr = new BigInteger("0");
            tempX = basis.getX();
            tempY = basis.getY();
            tempLambda = lambdaDuplication(tempX, tempY);
            BigInteger i = new BigInteger("1");
            for (i = BigInteger.valueOf(1); i.compareTo(privateKey) < 0;  i = i.add(BigInteger.ONE)) {
            //for (i; i<privateKey; i++) {                
                xr = ((( (tempLambda.pow(2)).subtract(basis.getX()).subtract(tempX)).mod(primeNumber).add(primeNumber)).mod(primeNumber));
                yr = (((tempLambda.multiply(tempX.subtract(xr))).subtract(tempY)).mod(primeNumber).add(primeNumber)).mod(primeNumber); // (a % b + b) % b modulo for giving positive value (a%b give negative!)
                tempX = xr;
                tempY = yr;
                //System.out.println("Nilai lambda: " + tempLambda);
                //System.out.println("\ni = " + i + "\nnilai xr dan yr: " + xr + " " + yr + "\n---------------\n");
                tempLambda = lambdaAddition(basis.getX(),basis.getY(), tempX, tempY);
            }
            Point bkB = new Point(xr,yr);
//            System.out.println("\nNilai b.(kB): " + bkB.toString());
            //compute inverse point of bkB
            
            Point inversebkB = new Point(bkB.getX(),((bkB.getY()).multiply(new BigInteger("-1"))).mod(primeNumber));
//            System.out.println("\nNilai inverse b.(kB): " + inversebkB.toString());
            //proses pengurangan PC2 - bkB = PC2 + inversebkB = PM (should be!!)
            
            
            xp = resultCipher.getX();
            yp = resultCipher.getY();
            //System.out.println(yp+"  "+functionECC(xp)+" "+xp);
            xq = inversebkB.getX();
            yq = inversebkB.getY();
            //System.out.println("xp: " + xp + " | yp: " + yp + " | xq: " + xq +" | yq: " + yq);
            
            tempLambda = lambdaAddition(xp, yp, xq, yq);
            xr = ((( (tempLambda.pow(2)).subtract(xp).subtract(xq)).mod(primeNumber).add(primeNumber)).mod(primeNumber));
            yr = (((tempLambda.multiply(xp.subtract(xr))).subtract(yp)).mod(primeNumber).add(primeNumber)).mod(primeNumber); // (a % b + b) % b modulo for giving positive value (a%b give negative!)
            //xr = (((int)Math.pow(tempLambda, 2) - xp - xq)%intPrimeNumber + intPrimeNumber)%intPrimeNumber;
            //yr = (((tempLambda*(xp-xr)) - yp)%intPrimeNumber + intPrimeNumber)%intPrimeNumber; // (a % b + b) % b modulo for giving positive value (a%b give negative!)
            Point PM2 = new Point(xr,yr);
//            System.out.println("\nNilai PM2: " + PM2.toString()); //PM2: PM setelah dekripsi, harus sama dengan PM1
            result = PM2;
    		return result.getX();
    	}
    	

    	private Point generatePubKey(BigInteger priKey) {
    		BigInteger tempLambda, privateKey = priKey , xr, yr, tempX, tempY;
            //pertama, hitung PB = privatekey*basis
            xr = new BigInteger("0");
            yr = new BigInteger("0");
            tempX = basis.getX() ;
            tempY = basis.getY();
            tempLambda = lambdaDuplication(tempX, tempY);
            BigInteger i = new BigInteger("1");
            for (i = BigInteger.valueOf(1); i.compareTo(privateKey) < 0;  i = i.add(BigInteger.ONE)) {
            	//System.out.println("proses generate pub key ke-" + i);
                xr = ((( (tempLambda.pow(2)).subtract(basis.getX()).subtract(tempX)).mod(primeNumber).add(primeNumber)).mod(primeNumber));
                yr = (((tempLambda.multiply(tempX.subtract(xr))).subtract(tempY)).mod(primeNumber).add(primeNumber)).mod(primeNumber); // (a % b + b) % b modulo for giving positive value (a%b give negative!)
                //xr = (((int)Math.pow(tempLambda, 2) - (int) basis.getX() - tempX)%intPrimeNumber + intPrimeNumber)%intPrimeNumber;
                //yr = (((tempLambda*(tempX-xr)) - tempY)%intPrimeNumber + intPrimeNumber)%intPrimeNumber; // (a % b + b) % b modulo for giving positive value (a%b give negative!)
                tempX = xr;
                tempY = yr;
                tempLambda = lambdaAddition(basis.getX(), basis.getY(), tempX, tempY);
            }
            Point PB = new Point(xr,yr);
            //System.out.println("\nNilai PB: " + PB.toString());
            
    		return PB;
    	}
    	public BigInteger generatePrivateKey() {
    		
    	}
    	
	public static void main(String[] args) {
		
		ECCAlgorithm ecc = new ECCAlgorithm();
		Point pubKey = new Point(); 
		BigInteger priKey = new BigInteger("");
        //System.out.println("tes");
        /*main process*/
        BigInteger i = new BigInteger("1");
        for (i = BigInteger.valueOf(0); i.compareTo(new BigInteger("255")) <= 0;  i = i.add(BigInteger.ONE)) {
		//for (int i = 0; i <= 255; i++) {
            //System.out.println("tes" + i);
	        BigInteger plainText = i; //arbitrary plaintext
	        pubKey = ecc.generatePubKey(priKey);
			Point resultCipher = ecc.encrypt(pubKey, plainText);
			BigInteger resultPlain = ecc.decrypt(priKey, resultCipher);
			
			if (resultPlain.compareTo(plainText) == 0) {
	            System.out.println(plainText+" = "+resultPlain+"");
	        }else{
	            System.out.println("\n-----------------------------\nPM1=PM2, kode tidak berhasil didekripsi.");
	        }
		}
		
	}


}