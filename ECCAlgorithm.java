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
            int privateKey = 5; 
            int xr, yr, tempX, tempY;
            int k; //for iterating
            Point basis = new Point(0,1); //basis as public key
            //System.out.println("\n" + koordinat.getY() + "\n");
            //koordinat = new Point(koordinat.getX(),nilaiBaruY);
            nilaiY = functionECC(nilaiX);
            Point PM= new Point(nilaiX,nilaiY);
            System.out.println(PM.toString());
            
            /*
             * hitung koordinat xr dan yr menggunakan for loop
            */
            xr = 0;
            yr = 0;
            tempX = nilaiX;
            tempY = nilaiY;
            lambdaDup = lambdaDuplication(nilaiX, nilaiY);
            tempLambda = lambdaDup;
            for (k=1; k<privateKey; k++) {                
            xr = (((int)Math.pow(tempLambda, 2) - nilaiX - tempX)%intPrimeNumber + intPrimeNumber)%intPrimeNumber;
            yr = (((tempLambda*(tempX-xr)) - tempY)%intPrimeNumber + intPrimeNumber)%intPrimeNumber; // (a % b + b) % b modulo for giving positive value (a%b give negative!)
            tempX = xr;
            tempY = yr;
            //System.out.println("Nilai lambda: " + tempLambda);
            System.out.println("\nk = " + k + "\nnilai xr dan yr: " + xr + " " + yr + "\n---------------\n");
            lambdaAdd = lambdaAddition(nilaiX, nilaiY, tempX, tempY);
            tempLambda = lambdaAdd;
            }
            //System.out.println("\n" + koordinat.getY());
            
            
	}

}