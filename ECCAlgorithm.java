package ecc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * kalo mau ganti nilai prime number, harus cek dulu titik basisnya ada gak di kurva, tinggal masukin aja ke fungsi ECC lalu dapetin PM1: PM1 ini bisa jadi basis karena ada pada kurva
 * kalo dapet y tidak ditemukan (pas nyari PM1), maka X itu ga punya pasangannya dan harus diganti
 * kalo udah dapet pasangan PM1-> bisa jadi basis
 * untuk ngecek juga bisa pake nilai kPB = b.(kB). harus sama
 * notasinya pake yang di slide halaman 60 yang elgaman ECC. b=privatekey (kalo di sini b itu variabel koefisien dari E). privatekey punya variabel sendiri
 */


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
//import java.awt.Point;
import java.math.BigInteger;

/*hooy*/

public class ECCAlgorithm {
	/*160*/
	public static String path = "/home/daniar/documents/KriptografiProject/file/";
	static BigInteger primeNumber = new BigInteger("E95E4A5F737059DC60DFC7AD95B3D8139515620F",16);//prime number 
	static BigInteger a = new BigInteger("340E7BE2A280EB74E2BE61BADA745D97E8F7C300",16); //coefficient of E
	static BigInteger b = new BigInteger("1E589A8595423412134FAA2DBDEC95C8D8675E58",16); //coefficient of E
	public ArrayList<Point> arrayPointCipher;
	public ArrayList<Integer> arrayDecryptResult;
	public ArrayList<Integer> arrayPlainInt;
	public Map<BigInteger, Point> arrayPointAddition ;

	static Point basis = new Point(new BigInteger("BED5AF16EA3F6A4F62938C4631EB5AF7BDBCDBC3",16),
			new BigInteger("1667CB477A1A8EC338F94741669C976316DA6321",16)); //basis as public key ---> harus ada di kurva, harus dicek dulu untuk tiap kurva.

	public ECCAlgorithm	(){
		arrayPlainInt = new ArrayList<Integer> ();
		arrayPointAddition = new HashMap<BigInteger, Point>();
		arrayPointCipher = new ArrayList<Point>();
		arrayDecryptResult = new ArrayList<Integer> ();
	}
	
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

	public Point encrypt (Point pubKey, Integer plainText) {
		Point result = new Point();

		BigInteger nilaiX = BigInteger.valueOf(plainText);
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

	private int decrypt(BigInteger priKey, Point resultCipher, Map<BigInteger, Point> arrayPointAddition2) {

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
		System.out.println("Size   "+arrayPointAddition2.size());
		if (privateKey.compareTo(new BigInteger(arrayPointAddition2.size()+"")) <= 0){

			System.out.print("cukup dari file cdscs");
			xr = arrayPointAddition2.get(privateKey).getX();
			yr = arrayPointAddition2.get(privateKey).getY();
		}else{

			System.out.println(privateKey+" masukkkkk "+privateKey);
			System.out.print("masuk cdsc ");
			for (i = BigInteger.valueOf(1); i.compareTo(privateKey) < 0;  i = i.add(BigInteger.ONE)) {
				xr = ((( (tempLambda.pow(2)).subtract(basis.getX()).subtract(tempX)).mod(primeNumber).add(primeNumber)).mod(primeNumber));
				yr = (((tempLambda.multiply(tempX.subtract(xr))).subtract(tempY)).mod(primeNumber).add(primeNumber)).mod(primeNumber); // (a % b + b) % b modulo for giving positive value (a%b give negative!)
				tempX = xr;
				tempY = yr;

				tempLambda = lambdaAddition(basis.getX(),basis.getY(), tempX, tempY);
			}
		}
		//        System.out.println(xr+" tidak masuuuk"+yr );
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
		return result.getX().intValue();
	}


	public Point generatePubKey(BigInteger priKey) {
		BigInteger tempLambda, privateKey = priKey , xr, yr, tempX, tempY;
		//pertama, hitung PB = privatekey*basis
		xr = new BigInteger("0");
		yr = new BigInteger("0");
		tempX = basis.getX() ;
		tempY = basis.getY();
		tempLambda = lambdaDuplication(tempX, tempY);

		BigInteger i = new BigInteger("1");

		if (privateKey.compareTo(new BigInteger(arrayPointAddition.size()+"")) <= 0){
			xr = arrayPointAddition.get(privateKey).getX();
			yr = arrayPointAddition.get(privateKey).getY();
			System.out.print("cukup dari file ");
		}else{
			System.out.println(privateKey+" masukkkkk "+privateKey);

			arrayPointAddition.put(BigInteger.ONE, new Point(tempX, tempY));
			
			for (i = new BigInteger("1"); i.compareTo(privateKey) < 0;  i = i.add(BigInteger.ONE)) {
				//System.out.println("proses generate pub key ke-" + i);
				xr = ((( (tempLambda.pow(2)).subtract(basis.getX()).subtract(tempX)).mod(primeNumber).add(primeNumber)).mod(primeNumber));
				yr = (((tempLambda.multiply(tempX.subtract(xr))).subtract(tempY)).mod(primeNumber).add(primeNumber)).mod(primeNumber); // (a % b + b) % b modulo for giving positive value (a%b give negative!)

				arrayPointAddition.put(i.add(BigInteger.ONE), new Point(xr,yr));

				tempX = xr;
				tempY = yr;
				tempLambda = lambdaAddition(basis.getX(), basis.getY(), tempX, tempY);
			}
			try {
				writeHashMapToFile(arrayPointAddition);
			} catch (ClassNotFoundException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		Point PB = new Point(xr,yr);
		//System.out.println("\nNilai PB: " + PB.toString());

		return PB;
	}

	public BigInteger generatePrivateKey() {
		//    		BigInteger n = new BigInteger("E95E4A5F737059DC60DF5991D45029409E60FC09", 16);
		BigInteger n = new BigInteger("F7305", 16);
		Random rand = new Random();
		BigInteger result = new BigInteger(n.bitLength(), rand);
		while( result.compareTo(n) >= 0 ) {
			result = new BigInteger(n.bitLength(), rand);
		}
		return result;
	}

	public static void writeHashMapToFile(Map<BigInteger, Point> arrayPointAddition2) throws IOException, ClassNotFoundException {
	  try{
		FileOutputStream fout = new FileOutputStream(path+"generated/map.hash");
		ObjectOutputStream oos = new ObjectOutputStream(fout);   
		oos.writeObject(arrayPointAddition2);
		oos.close();
		System.out.println("Done");
		   
	   }catch(Exception ex){
		   ex.printStackTrace();
	   }
	}
	
	public static HashMap<BigInteger, Point> readHashMapFromFile()  {
		FileInputStream f;
		HashMap<BigInteger, Point> fileObj2 = null ;
		try {
			File file = new File(path+"generated/map.hash");
			f = new FileInputStream(file);
			ObjectInputStream s = new ObjectInputStream(f);
			fileObj2 = (HashMap<BigInteger, Point>) s.readObject();
			s.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fileObj2;
	}

	public void writeCipherToFile(ArrayList<Point> arrayPointCipher, String filename) {
	  try{
		FileOutputStream fout = new FileOutputStream(path+"cipher/"+filename);
		ObjectOutputStream oos = new ObjectOutputStream(fout);   
		oos.writeObject(arrayPointCipher);
		System.out.println("Done write"+arrayPointCipher.toString().getBytes().length);
		oos.close();
//		System.out.println("Done write");
		   
	   }catch(Exception ex){
		   ex.printStackTrace();
	   }
	}
	
	public static double getFileSize(String filename) {
      File file = new File(filename);
      if (!file.exists() || !file.isFile()) {
         System.out.println("File doesn\'t exist");
         return -1;
      }
      
      return file.length()/1000.0;
   }
	
	public static byte[] convertIntegers(ArrayList<Integer> integers)
	{
	    byte[] ret = new byte[integers.size()];
	    for (int i=0; i < ret.length; i++)
	    {
	        ret[i] = (byte) integers.get(i).intValue();
	    }
	    return ret;
	}
	
	public void writeGeneratedKeyToFile(Point pubKey, BigInteger priKey) {
	  try{
		FileOutputStream fout = new FileOutputStream(path+"generated/kunci.pub");
		ObjectOutputStream oos = new ObjectOutputStream(fout);   
		oos.writeObject(pubKey);
		oos.close();
		System.out.println("pubKey Done");
		

		fout = new FileOutputStream(path+"generated/kunci.pri");
		oos = new ObjectOutputStream(fout);   
		oos.writeObject(priKey);
		oos.close();
		System.out.println("priKey Done");
		
		try(  PrintWriter out = new PrintWriter(path+"generated/plain.key" )  ){
		    out.println( "{\n\tprikey : "+priKey+",\n\tpubKey : "+pubKey.toString() +"\n}");
		}
		System.out.println("plainKey Done");
		   
	   }catch(Exception ex){
		   ex.printStackTrace();
	   }
	}
	
	public  ArrayList<Point> readCipherFromFile(String filename) {
		File file = new File(path+"cipher/"+filename);
		ArrayList<Point> fileObj2 = null ;
		FileInputStream f;
		try {
			f = new FileInputStream(file);
			ObjectInputStream s = new ObjectInputStream(f);
			fileObj2 = (ArrayList<Point>) s.readObject();
			s.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fileObj2;
	}
	
	public static Point readPubKeyFromFile() {
		File file = new File(path+"uploaded/kunci.pub");
		Point fileObj2 = null ;
		FileInputStream f;
		try {
			f = new FileInputStream(file);
			ObjectInputStream s = new ObjectInputStream(f);
			fileObj2 = (Point) s.readObject();
			s.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fileObj2;
	}
	
	public static BigInteger readPriKeyFromFile() {
		File file = new File(path+"uploaded/kunci.pri");
		BigInteger fileObj2 = null ;
		FileInputStream f;
		try {
			f = new FileInputStream(file);
			ObjectInputStream s = new ObjectInputStream(f);
			fileObj2 = (BigInteger) s.readObject();
			s.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fileObj2;
	}
	
	
	public static void main(String[] args) {
		ECCAlgorithm ecc = new ECCAlgorithm();
		
		Point pubKey = new Point(); 
		
		String filename = "test.txt";
		BigInteger priKey  = new BigInteger("0");
//				ecc.generatePrivateKey();
//		
//		pubKey = ecc.generatePubKey(priKey);
//		ecc.writeGeneratedKeyToFile(pubKey,priKey);
		
		/*main process*/

		priKey = ecc.readPriKeyFromFile();
		pubKey = ecc.readPubKeyFromFile();
		System.out.println("key read!");
		
		ecc.arrayPlainInt = ecc.readFile(path+"uploaded/"+filename);
		ecc.arrayPointCipher = ecc.encryptListInt(pubKey, ecc.arrayPlainInt);
		ecc.writeCipherToFile(ecc.arrayPointCipher, filename);
		

		ecc.arrayPointAddition = ecc.readHashMapFromFile();
		ecc.arrayPointCipher = ecc.readCipherFromFile(filename);
		ecc.arrayDecryptResult = ecc.decryptListPoint(priKey, ecc.arrayPointCipher, ecc.arrayPointAddition);		
		ecc.writeDecryptedFile(ecc.arrayDecryptResult, filename);
		
		System.out.println("finish!");

	}
	
	
	public ArrayList<Point> encryptListInt(Point pubKey, ArrayList<Integer>  arrayInt){
		ArrayList<Point> result = new ArrayList<Point>();
		System.out.println("start encrypt !");
		BigInteger i = new BigInteger("1");
		for (i = BigInteger.valueOf(0);  i.compareTo(new BigInteger(""+(arrayInt.size()-1))) <= 0;  i = i.add(BigInteger.ONE)) {
			Integer plainText = arrayInt.get(i.intValue()); //arbitrary plaintext
			Point resultCipher = encrypt(pubKey, plainText);
			result.add(resultCipher);

		}
		return result;
	}
	
	public ArrayList<Integer> decryptListPoint(BigInteger priKey, ArrayList<Point>  arrayPoint, Map<BigInteger, Point> arrayPointAddition2){
		ArrayList<Integer> result = new ArrayList<Integer>();
		BigInteger i = new BigInteger("1");
		for (i = BigInteger.valueOf(0);  i.compareTo(new BigInteger(""+(arrayPoint.size()-1))) <= 0;  i = i.add(BigInteger.ONE)) {
			Integer resultPlain = decrypt(priKey, arrayPoint.get(i.intValue()), arrayPointAddition2);
			result.add(resultPlain);
			System.out.println(resultPlain);
		}
		return result;
	}
	
	public static String byteToUnsignedHex(int i) {
        String hex = Integer.toHexString(i);
        while(hex.length() < 8){
            hex = "0" + hex; 
        }
        return hex;
    }

    public static String ByteArrToHex(byte[] arr) {
        StringBuilder builder = new StringBuilder(arr.length * 8);
        for (int b : arr) {
            builder.append(byteToUnsignedHex(b));
        }
        String result = builder.toString();
        return result.replaceAll("(.{2})(?!$)", "$1 ");
    }
	
	public void writeDecryptedFile(ArrayList<Integer> myList , String filename){
		BufferedOutputStream bos;
		try {
			bos = new BufferedOutputStream(
			        new FileOutputStream(new File(path + "result/"+filename)));
			for (int i = 0; i < myList.size(); i++) {
            	bos.write(myList.get(i).intValue());
    		}
	        bos.close();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	 public ArrayList<Integer> readFile(String path){
			FileInputStream fis = null;
			ArrayList<Integer> myList = new ArrayList<Integer>();
	    	try {
				fis = new FileInputStream(path);
				int content;
				while ((content = fis.read()) != -1) {
		            myList.add(content);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (fis != null)
						fis.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
			return myList;
		}
}