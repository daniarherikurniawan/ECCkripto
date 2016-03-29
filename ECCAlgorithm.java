import java.util.Scanner;



public class ECCAlgorithm {
    
    
        public static int functionECC(int x) {
            //int result = 1;
            int ySquare;
            int y = 1;
            int primeNumber = 11;
            ySquare = ((int)Math.pow(x, 3) + x + 6)%primeNumber ;
            System.out.println("nilai ySquare: " + ySquare + "\n");
            
            while ( ((int)Math.pow(y,2)-ySquare)%primeNumber != 0 )  {
                System.out.println((int)Math.pow(y,2));                
                y += 1;
            }
            
            return y;
            
        } 

	public static void main(String[] args) {
            Point koordinat= new Point(8,2);
            int nilaiX = koordinat.getX();
            int powerX;
            int nilaiY;
            nilaiY = functionECC(nilaiX);
            powerX = (int)Math.pow(nilaiX,3);
            System.out.println("\n" + nilaiX + " dan " + nilaiY);
            
	}

}