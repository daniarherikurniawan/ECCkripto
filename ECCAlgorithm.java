import java.util.Scanner;
import java.math.BigInteger;



public class ECCAlgorithm {
    
    
        public static int functionECC(int x) {
            //int result = 1;
            int ySquare; 
            int y = 1; // initialization of y
            int primeNumber = 11; //prime number
            ySquare = ((int)Math.pow(x, 3) + x + 6)%primeNumber ; // this is the E function y^2 = x^3 + ax + b
            //System.out.println("nilai ySquare: " + ySquare + "\n");
            
            while ( ((int)Math.pow(y,2)-ySquare)%primeNumber != 0 )  {
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
            
            BigInteger primeNumber = new BigInteger("11");//prime number
            int a = 1; //from function E.
            int nominator = 3*(int)Math.pow(xp,2) + a;
            BigInteger bigNominator = BigInteger.valueOf((long)nominator);
            int denominator = 2*yp;
            BigInteger bigDenominator = BigInteger.valueOf((long)denominator);
            BigInteger inverseDenominator = bigDenominator.modInverse(primeNumber);
            System.out.println("nominator: " + bigNominator + "\ndenominator: " + bigDenominator + "\nInverse denominator: " + inverseDenominator);
            return 0;
        }

	public static void main(String[] args) {
            int plainText = 2; //arbitrary plaintext
            int nilaiX = plainText;
            int nilaiY;
            int lambda;
            int privateKey = 2;
            Point basis = new Point(0,1); //basis as public key
            //System.out.println("\n" + koordinat.getY() + "\n");
            //koordinat = new Point(koordinat.getX(),nilaiBaruY);
            nilaiY = functionECC(nilaiX);
            Point PM= new Point(nilaiX,nilaiY);

            System.out.println(PM.toString());
            lambda = lambdaDuplication(nilaiX, nilaiY);
            
            //System.out.println("\n" + koordinat.getY());
            
	}

}