import java.util.Scanner;
import java.math.BigInteger;



public class ECCAlgorithm {
    
        static BigInteger primeNumber = new BigInteger("11");//prime number
        static int intPrimeNumber = primeNumber.intValue(); //integer version of prime number            
        static int a = 1; //coefficient of prime number

        public static int functionECC(int x) {
            //int result = 1;
            int ySquare; 
            int y = 1; // initialization of y
            ySquare = ((int)Math.pow(x, 3) + x + 6)%intPrimeNumber ; // this is the E function y^2 = x^3 + ax + b
            //System.out.println("nilai ySquare: " + ySquare + "\n");
            
            while ( ((int)Math.pow(y,2)-ySquare)%intPrimeNumber != 0 )  {
                //System.out.println((int)Math.pow(y,2));                
                y += 1;
                if (y == 100000) {
                    System.out.println("Nilai y tidak ditemukan");
                    y=0;
                    break;
                }
            }
            
            return y;
            
        } 
        
        public static int lambdaDuplication(int xp, int yp) {
            
            int nominator = 3*(int)Math.pow(xp,2) + a;
            BigInteger bigNominator = BigInteger.valueOf((long)nominator);
            int denominator = 2*yp;
            BigInteger bigDenominator = BigInteger.valueOf((long)denominator);
            BigInteger inverseDenominator = bigDenominator.modInverse(primeNumber);
            
            int intInverseDenominator = inverseDenominator.intValue();
            
            int result = (nominator*intInverseDenominator)%intPrimeNumber;
            //System.out.println("nominator: " + bigNominator + "\ndenominator: " + bigDenominator + "\nInverse denominator: " + intInverseDenominator + "\nResult: " + result);
            return result;
        }

        public static int lambdaAddition(int xp, int yp, int xq, int yq) {
            int nominator = Math.abs(yp - yq);
            int denominator = Math.abs(xp - xq);

            BigInteger bigNominator = BigInteger.valueOf((long)nominator);
            BigInteger bigDenominator = BigInteger.valueOf((long)denominator);
            BigInteger inverseDenominator = bigDenominator.modInverse(primeNumber);            

            int intInverseDenominator = inverseDenominator.intValue();
            int result = (nominator*intInverseDenominator)%intPrimeNumber;
            //System.out.println("nominator: " + bigNominator + "\ndenominator: " + bigDenominator + "\nInverse denominator: " + intInverseDenominator + "\nResult: " + result);
            return result;

        }
	public static void main(String[] args) {
            int plainText = 2; //arbitrary plaintext
            int nilaiX = plainText;
            int nilaiY;
            int lambdaDup;
            int lambdaAdd;
            int tempLambda;
            int privateKey = 3;
            int k = 2; //bilangan yang dipilih pengirim pesan selang [1,p-1]
            int xr, yr, tempX, tempY;
            int xp, yp, xq, yq;
            int i; //for iterating
            Point basis = new Point(2,4); //basis as public key
            //System.out.println("\n" + koordinat.getY() + "\n");
            //koordinat = new Point(koordinat.getX(),nilaiBaruY);
            nilaiY = functionECC(nilaiX);
            Point PM1= new Point(nilaiX,nilaiY);
            //System.out.println(PM.toString());
            System.out.println("\nNilai PM1: " + PM1.toString());
            
            
            /*
             * hitung koordinat xr dan yr menggunakan for loop
            */           
            
            /*
            pertama, hitung PB = privatekey*basis
            */
            xr = 0;
            yr = 0;
            nilaiX = basis.getX();
            nilaiY = basis.getY();
            tempX = nilaiX;
            tempY = nilaiY;
            lambdaDup = lambdaDuplication(tempX, tempY);
            tempLambda = lambdaDup;                                                  
            for (i=1; i<privateKey; i++) {                
                xr = (((int)Math.pow(tempLambda, 2) - nilaiX - tempX)%intPrimeNumber + intPrimeNumber)%intPrimeNumber;
                yr = (((tempLambda*(tempX-xr)) - tempY)%intPrimeNumber + intPrimeNumber)%intPrimeNumber; // (a % b + b) % b modulo for giving positive value (a%b give negative!)
                tempX = xr;
                tempY = yr;
                //System.out.println("Nilai lambda: " + tempLambda);
                //System.out.println("\ni = " + i + "\nnilai xr dan yr: " + xr + " " + yr + "\n---------------\n");
                lambdaAdd = lambdaAddition(nilaiX, nilaiY, tempX, tempY);
                tempLambda = lambdaAdd;
            }
            Point PB = new Point(xr,yr);
            System.out.println("\nNilai PB: " + PB.toString());
            
            
            /*
            lalu, hitung kPB
            */
            xr = 0;
            yr = 0;
            nilaiX = PB.getX();
            nilaiY = PB.getY();
            tempX = nilaiX;
            tempY = nilaiY;
            lambdaDup = lambdaDuplication(tempX, tempY);
            tempLambda = lambdaDup;
            
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
            Point kPB = new Point(xr,yr);
            System.out.println("\nNilai kPB: " + kPB.toString());
            
            
            /*
            lalu, hitung kB
            */
            xr = 0;
            yr = 0;
            nilaiX = basis.getX();
            nilaiY = basis.getY();
            tempX = nilaiX;
            tempY = nilaiY;
            lambdaDup = lambdaDuplication(tempX, tempY);
            tempLambda = lambdaDup;

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
            Point kB = new Point(xr,yr);
            System.out.println("\nNilai kB " + kB.toString());
            
            
            /*
            hitung titik (PM + kPB) sebagai koordinat y dari PC
            */
            xp = PM1.getX();
            yp = PM1.getY();
            xq = kPB.getX();
            yq = kPB.getY();
            lambdaAdd = lambdaAddition(xp, yp, xq, yq);
            tempLambda = lambdaAdd;
            xr = (((int)Math.pow(tempLambda, 2) - xp - xq)%intPrimeNumber + intPrimeNumber)%intPrimeNumber;
            yr = (((tempLambda*(xp-xr)) - yp)%intPrimeNumber + intPrimeNumber)%intPrimeNumber; // (a % b + b) % b modulo for giving positive value (a%b give negative!)
            Point PC1 = new Point(kB.getX(),kB.getY());
            Point PC2 = new Point(xr,yr);
            System.out.println("\nNilai PC1: " + PC1.toString() + " << kB");
            System.out.println("\nNilai PC2: " + PC2.toString() + " << PM + kPB");
            
            
            /*
            lalu, hitung b.(kB)
            */
            xr = 0;
            yr = 0;
            nilaiX = kB.getX();
            nilaiY = kB.getY();
            tempX = nilaiX;
            tempY = nilaiY;
            lambdaDup = lambdaDuplication(tempX, tempY);
            tempLambda = lambdaDup;

            for (i=1; i<privateKey; i++) {                
                xr = (((int)Math.pow(tempLambda, 2) - nilaiX - tempX)%intPrimeNumber + intPrimeNumber)%intPrimeNumber;
                yr = (((tempLambda*(tempX-xr)) - tempY)%intPrimeNumber + intPrimeNumber)%intPrimeNumber; // (a % b + b) % b modulo for giving positive value (a%b give negative!)
                tempX = xr;
                tempY = yr;
                //System.out.println("Nilai lambda: " + tempLambda);
                //System.out.println("\ni = " + i + "\nnilai xr dan yr: " + xr + " " + yr + "\n---------------\n");
                lambdaAdd = lambdaAddition(nilaiX, nilaiY, tempX, tempY);
                tempLambda = lambdaAdd;
            }
            Point bkB = new Point(xr,yr);
            System.out.println("\nNilai b.(kB): " + bkB.toString());
            /*
            compute inverse point of bkB
            */
            Point inversebkB = new Point(bkB.getX(),((-1)*(bkB.getY()))%intPrimeNumber);
            System.out.println("\nNilai inverse b.(kB): " + inversebkB.toString());
            /*
            proses pengurangan PC2 - bkB = PC2 + inversebkB = PM (should be!!)
            */
            
            xp = PC2.getX();
            yp = PC2.getY();
            xq = inversebkB.getX();
            yq = inversebkB.getY();
            System.out.println("xp: " + xp + " | yp: " + yp + " | xq: " + xq +" | yq: " + yq);
            
            lambdaAdd = lambdaAddition(xp, yp, xq, yq);
            tempLambda = lambdaAdd;
            xr = (((int)Math.pow(tempLambda, 2) - xp - xq)%intPrimeNumber + intPrimeNumber)%intPrimeNumber;
            yr = (((tempLambda*(xp-xr)) - yp)%intPrimeNumber + intPrimeNumber)%intPrimeNumber; // (a % b + b) % b modulo for giving positive value (a%b give negative!)
            Point PM2 = new Point(xr,yr);
            
	}

}