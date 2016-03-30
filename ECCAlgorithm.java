package ecc;

/**
 * kalo mau ganti nilai prime number, harus cek dulu titik basisnya ada gak di kurva, tinggal masukin aja ke fungsi ECC lalu dapetin PM1: PM1 ini bisa jadi basis karena ada pada kurva
 * kalo dapet y tidak ditemukan (pas nyari PM1), maka X itu ga punya pasangannya dan harus diganti
 * kalo udah dapet pasangan PM1-> bisa jadi basis
 * untuk ngecek juga bisa pake nilai kPB = b.(kB). harus sama
 * notasinya pake yang di slide halaman 60 yang elgaman ECC. b=privatekey (kalo di sini b itu variabel koefisien dari E). privatekey punya variabel sendiri
 */

import java.util.Scanner;
import java.awt.Point;
import java.math.BigInteger;

/*hooy*/

public class ECCAlgorithm {
    
        static BigInteger primeNumber = new BigInteger("911");//prime number
        static int intPrimeNumber = primeNumber.intValue(); //integer version of prime number            
        static int a = 1; //coefficient of E
        static int b = 6; //coefficient of E

        static Point basis = new Point(2,4); //basis as public key ---> harus ada di kurva, harus dicek dulu untuk tiap kurva.
        
        public static int functionECC(int x) {
            //int result = 1;
            int ySquare; 
            int y = 1; // initialization of y
            ySquare = ((int)Math.pow(x, 3) + a*x + b)%intPrimeNumber ; // this is the E function y^2 = x^3 + ax + b
            //System.out.println("nilai ySquare: " + ySquare + "\n");
            
            while ( ((int)Math.pow(y,2)-ySquare)%intPrimeNumber != 0 )  {
                //System.out.println((int)Math.pow(y,2));                
                y += 1;
                if (y == 100000) {
//                    System.out.println("Nilai y tidak ditemukan");
                    y=0;
                    break;
                }
            }
            
            return y;
            
        } 
        
        public static int lambdaDuplication(int xp, int yp) {
            
            int nominator = 3*(int)Math.pow(xp,2) + a;
            int denominator = 2*yp;
            
            BigInteger bigDenominator = BigInteger.valueOf((long)denominator);
            BigInteger inverseDenominator = bigDenominator.modInverse(primeNumber);
            
            int intInverseDenominator = inverseDenominator.intValue();            
            int result = (nominator*intInverseDenominator)%intPrimeNumber;
            //System.out.println("nominator: " + nominator + "\ndenominator: " + bigDenominator + "\nInverse denominator: " + intInverseDenominator + "\nResult: " + result);
            return result;
        }

        public static int lambdaAddition(int xp, int yp, int xq, int yq) {
            int nominator = (yp - yq);
            int denominator = (xp - xq);

            BigInteger bigDenominator = BigInteger.valueOf((long)denominator);
            BigInteger inverseDenominator = bigDenominator.modInverse(primeNumber);            

            int intInverseDenominator = inverseDenominator.intValue();
            int result = (nominator*intInverseDenominator)%intPrimeNumber;
            //System.out.println("nominator: " + bigNominator + "\ndenominator: " + bigDenominator + "\nInverse denominator: " + intInverseDenominator + "\nResult: " + result);
            return result;

        }
        
        public Point encrypt (Point pubKey, int plainText) {
        	Point result = new Point();
        	
        	int nilaiX = plainText;
            int nilaiY;
            int lambdaDup;
            int lambdaAdd;
            int tempLambda;
            //int privateKey = 80; //privatekey, b=privatekey buat yang di bawah2 (sesuai slide)
            int k = 1; //bilangan yang dipilih pengirim pesan selang [1,p-1]
            int xr, yr, tempX, tempY;
            int xp, yp, xq, yq;
            int i; //for iterating
            //System.out.println("\n" + koordinat.getY() + "\n");
            //koordinat = new Point(koordinat.getX(),nilaiBaruY);
            nilaiY = functionECC(nilaiX);
            Point PM1= new Point(nilaiX,nilaiY);
            //System.out.println(PM.toString());
//            System.out.println("\nNilai PM1: " + PM1.toString()); //PM1: PM sebelum enkripsi
            
            
            /*
             * hitung koordinat xr dan yr menggunakan for loop
            */           
            
            Point PB = pubKey;
//            System.out.println("\nNilai pubkey: " + PB.toString());
            
            
            /*
            lalu, hitung kPB = k*(privatekey*basis)
            */
            xr = 0;
            yr = 0;
            nilaiX = (int) PB.getX();
            nilaiY = (int) PB.getY();
            tempX = nilaiX;
            tempY = nilaiY;
            tempLambda = lambdaDuplication(tempX, tempY);
            
            for (i=1; i<k; i++) {                
                xr = (((int)Math.pow(tempLambda, 2) - nilaiX - tempX)%intPrimeNumber + intPrimeNumber)%intPrimeNumber;
                yr = (((tempLambda*(tempX-xr)) - tempY)%intPrimeNumber + intPrimeNumber)%intPrimeNumber; // (a % b + b) % b modulo for giving positive value (a%b give negative!)
                tempX = xr;
                tempY = yr;
                //System.out.println("Nilai lambda: " + tempLambda);
                //System.out.println("\ni = " + i + "\nnilai xr dan yr: " + xr + " " + yr + "\n---------------\n");
                lambdaAdd = lambdaAddition(nilaiX, nilaiY, tempX, tempY);
                tempLambda = lambdaAdd;
            }
            Point kPB = new Point(PB.x,PB.y);
//            System.out.println("\nNilai kPB: " + kPB.toString());
            
            
            /*
            lalu, hitung kB=k*basis
            */
            xr = 0;
            yr = 0;
            nilaiX = (int) basis.getX();
            nilaiY = (int) basis.getY();
            tempX = nilaiX;
            tempY = nilaiY;
            tempLambda = lambdaDuplication(tempX, tempY);

            for (i=1; i<k; i++) {                
                xr = (((int)Math.pow(tempLambda, 2) - nilaiX - tempX)%intPrimeNumber + intPrimeNumber)%intPrimeNumber;
                yr = (((tempLambda*(tempX-xr)) - tempY)%intPrimeNumber + intPrimeNumber)%intPrimeNumber; // (a % b + b) % b modulo for giving positive value (a%b give negative!)
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
            xp = (int) PM1.getX();
            yp = (int) PM1.getY();
            xq = (int) kPB.getX();
            yq = (int) kPB.getY();
            lambdaAdd = lambdaAddition(xp, yp, xq, yq);
            tempLambda = lambdaAdd;
            xr = (((int)Math.pow(tempLambda, 2) - xp - xq)%intPrimeNumber + intPrimeNumber)%intPrimeNumber;
            yr = (((tempLambda*(xp-xr)) - yp)%intPrimeNumber + intPrimeNumber)%intPrimeNumber; // (a % b + b) % b modulo for giving positive value (a%b give negative!)
          
            Point PC2 = new Point(xr,yr);
//            System.out.println("\nNilai PC2: " + PC2.toString() + " << PM + kPB");
            
            result = PC2;
    		return result;
    	}

    	private int decrypt(int priKey, Point resultCipher) {

        	Point result = new Point();
            int tempLambda;
            int privateKey = priKey; //privatekey, b=privatekey buat yang di bawah2 (sesuai slide)
            int xr, yr, tempX, tempY;
            int xp, yp, xq, yq;
            int i; //for iterating
    		//lalu, hitung bkB = privatekey*(k*basis)
            xr = 0;
            yr = 0;
            tempX = (int) basis.getX();
            tempY = (int) basis.getY();
            tempLambda = lambdaDuplication(tempX, tempY);

            for (i=1; i<privateKey; i++) {                
                xr = (int) ((((int)Math.pow(tempLambda, 2) - basis.getX() - tempX)%intPrimeNumber + intPrimeNumber)%intPrimeNumber);
                yr = (((tempLambda*(tempX-xr)) - tempY)%intPrimeNumber + intPrimeNumber)%intPrimeNumber; // (a % b + b) % b modulo for giving positive value (a%b give negative!)
                tempX = xr;
                tempY = yr;
                //System.out.println("Nilai lambda: " + tempLambda);
                //System.out.println("\ni = " + i + "\nnilai xr dan yr: " + xr + " " + yr + "\n---------------\n");
                tempLambda = lambdaAddition((int)basis.getX(), (int) basis.getY(), tempX, tempY);
            }
            Point bkB = new Point(xr,yr);
//            System.out.println("\nNilai b.(kB): " + bkB.toString());
            //compute inverse point of bkB
            
            Point inversebkB = new Point((int)bkB.getX(),(int)((-1)*(bkB.getY()))%intPrimeNumber);
//            System.out.println("\nNilai inverse b.(kB): " + inversebkB.toString());
            //proses pengurangan PC2 - bkB = PC2 + inversebkB = PM (should be!!)
            
            
            xp = (int)resultCipher.getX();
            yp = (int)resultCipher.getY();
            xq = (int)inversebkB.getX();
            yq = (int)inversebkB.getY();
            //System.out.println("xp: " + xp + " | yp: " + yp + " | xq: " + xq +" | yq: " + yq);
            
            tempLambda = lambdaAddition(xp, yp, xq, yq);
            xr = (((int)Math.pow(tempLambda, 2) - xp - xq)%intPrimeNumber + intPrimeNumber)%intPrimeNumber;
            yr = (((tempLambda*(xp-xr)) - yp)%intPrimeNumber + intPrimeNumber)%intPrimeNumber; // (a % b + b) % b modulo for giving positive value (a%b give negative!)
            Point PM2 = new Point(xr,yr);
//            System.out.println("\nNilai PM2: " + PM2.toString()); //PM2: PM setelah dekripsi, harus sama dengan PM1
            result = PM2;
    		return result.x;
    	}
    	

    	private Point generatePubKey(int priKey) {
            int tempLambda, privateKey = priKey , xr, yr, tempX, tempY;
            //pertama, hitung PB = privatekey*basis
            xr = 0;
            yr = 0;
            tempX = (int) basis.getX() ;
            tempY = (int) basis.getY();
            tempLambda = lambdaDuplication(tempX, tempY);
            for (int i=1; i<privateKey; i++) {                
                xr = (((int)Math.pow(tempLambda, 2) - (int) basis.getX() - tempX)%intPrimeNumber + intPrimeNumber)%intPrimeNumber;
                yr = (((tempLambda*(tempX-xr)) - tempY)%intPrimeNumber + intPrimeNumber)%intPrimeNumber; // (a % b + b) % b modulo for giving positive value (a%b give negative!)
                tempX = xr;
                tempY = yr;
                tempLambda = lambdaAddition((int) basis.getX(), (int) basis.getY(), tempX, tempY);
            }
            Point PB = new Point(xr,yr);
//            System.out.println("\nNilai PB: " + PB.toString());
            
    		return PB;
    	}
    	
	public static void main(String[] args) {
		
		ECCAlgorithm ecc = new ECCAlgorithm();
		Point pubKey = new Point(); 
		int priKey = 5;
        
        /*main process*/
		for (int i = 0; i <= 255; i++) {

	        int plainText = i; //arbitrary plaintext
	        pubKey = ecc.generatePubKey(priKey);
			Point resultCipher = ecc.encrypt(pubKey, plainText);
			int resultPlain = ecc.decrypt(priKey, resultCipher);
			
			if (resultPlain == plainText) {
	            System.out.println(plainText+" = "+resultPlain+"");
	        }else{
	            System.out.println("\n-----------------------------\nPM1=PM2, kode tidak berhasil didekripsi.");
	        }
		}
		
	}


}