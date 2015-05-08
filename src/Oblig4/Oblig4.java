package Oblig4;

public class Oblig4 {
	public static void main(String[] args){ // System.out.println();
		
		SekvensiellRadix sr = new SekvensiellRadix();
		
		int u = 20;
		RandomIntArray ria = new RandomIntArray();
		
		int[] ut = ria.makeArray(u);
		
		//int[] ut = new int[u];
		
		/*
		ut[0] = 688;
		ut[1] = 788;
		ut[2] = 88;
		ut[3] = 388;
		ut[4] = 288;
		ut[5] = 988;
		ut[6] = 588;
		ut[7] = 888;
		ut[8] = 188;
		ut[9] = 488;
		*/
		
		printArray(ut, "Usortert");
		
		//sr.radix2(ut);
		//System.out.println("\n Sortert:");
		//printArray(ut, "Sortert");
		
		ParallellRadix pr = new ParallellRadix(ut);
		
		pr.doParallellRadixSort();
		
		//pr.printTrad();
		
		
		
		
		
		// dimetrodon & doubleclicks
		// finne og forsta del a

	}
	
	
	
	static public void printArray(int arr[], String s){
		System.out.println("\n"+s);
		for(int i = 0; i <  arr.length; i++){
			System.out.println("\ti: " + i + "arr[i] :" + arr[i]);
		}
		
	}
	
}
