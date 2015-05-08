package Oblig4;

import java.util.concurrent.CyclicBarrier;

public class ParallellRadix {
	int antKjerner;
	RadixTrad[] tradHotell;
	int[] a;
	int[] b;
	int[][] count;
	int accum[];
	int max;
	int numBit;
	int n;
	int bit1, bit2;
	
	CyclicBarrier synkroniserMaxVerdi;
	CyclicBarrier synkroniserFrekvensSummer;
	CyclicBarrier synkroniserAkkumulering;
	CyclicBarrier synkroniserOverforing;
	CyclicBarrier synkroniserJmp;
	
	public ParallellRadix(int[]a) {
		antKjerner = 1;//Runtime.getRuntime().availableProcessors();
		//System.out.println("forst Kjerner: "+ antKjerner);
		tradHotell = new RadixTrad[antKjerner];
		this.a = a;
		b = new int[a.length];
		numBit = 2;
		n = a.length;
		count = new int[a.length][antKjerner];
		accum = new int[count.length];
		//makeDoubleArray(b);
	}
	
	public void doParallellRadixSort(){
		
		
		synkroniserMaxVerdi = new CyclicBarrier(antKjerner, new finMax());
		synkroniserFrekvensSummer = new CyclicBarrier(antKjerner, new summerFrekvens());
		synkroniserAkkumulering = new CyclicBarrier(antKjerner, new finnJumptallAkkumulering());
		synkroniserOverforing = new CyclicBarrier(antKjerner, new ttt());
		synkroniserJmp = new CyclicBarrier(antKjerner, new printTest());
		
		System.out.println("Kjerner: "+ antKjerner);
		
		for(int i = 0; i < antKjerner; i++){
			RadixTrad r = new RadixTrad(i, this);
			tradHotell[i] = r;
		}
		for(int i = 0; i < antKjerner; i++){
			//System.out.println("starter ");
			tradHotell[i].start();
		}
		
		
		/* Her ventes det pa at alle tradene skal bli ferdige */
		for(int i = 0; i < tradHotell.length ; i++){
			try {
				tradHotell[i].join();
			} catch (InterruptedException e) {
				
				e.printStackTrace();
			}
		} 
		
		//printCount();
		printAogB();
		
		System.out.println("DONE!!");
		//while (max >= (1L<<numBit) )numBit++; // antall siffer i max

		// bestem antall bit i siffer1 og siffer2
		//int bit1 = numBit/2, bit2 = numBit-bit1;
		//int[] b = new int [n];
		
	
		
	}

	private void printAogB() {
		System.out.println("a[] er: ");
		for(int i = 0; i < a.length; i++){
			System.out.println("\tPa plass a["+i+"] er: "+a[i]+" \t\tPa plass b["+i+"] er: "+b[i]);
		}
	}
	
	class finMax implements Runnable{

		public void run() {

			max = tradHotell[0].max;
			for(int i = 1; i < tradHotell.length; i++){
				if(max < tradHotell[i].max){
					max = tradHotell[i].max;
				}
			}
			while (max >= (1L<<numBit) )numBit++; // antall siffer i max
			
			bit1 = numBit/2; 
			bit2 = numBit-bit1;
			
			count = new int[(1<<bit1)][antKjerner];
			//accum = new int[count.length];
			
			printTrad();  // TODO: ta vekk denne
		}
	}
	
	
	class summerFrekvens implements Runnable{

		public void run() {
 			System.out.println("\nFrekvensen: ");
			printCount();
			
			for(int i = 0; i < count.length; i ++){
				//count[]
				for(int j = 1; j < count[i].length; j ++){
					count[i][j] += count[i][j-1];
				}
			}
			
			
			/*
			for(int i = 0; i < count.length; i ++){
				//count[]
				for(int j = 1; j < count[i].length; j ++){
					count[i][0] += count[i][j];
				}
			}*/
			System.out.println("\nSummerte dobbel Frekvenser: ");
			printCount();
		}		
	}
	
	class finnJumptallAkkumulering implements Runnable{

		public void run() {
			System.out.println("\nAkkumulering uten jmp: ");
			printCount();
			
			for(int i = 1; i < antKjerner; i ++){
				tradHotell[i].myJmp = tradHotell[i-1].jmp + tradHotell[i-1].myJmp;
			}
			tradHotell[0].myJmp = 0;
		}		
	}
	
	class ttt implements Runnable{
		public void run() {
			printAogB();
		}
	}
	
	class printTest implements Runnable{
		public void run(){
			System.out.println("Oppdatert Jump:");
			//printCount();
			printAccum();
		}
	}

	
	
	
	
	
	
	
	
	
	/*
	public synchronized void setMax(int m){
		if(m > max) max = m;
	}*/
	
	
	
	
	
	
	
	
	/***
	 * Debugging metode for a sjekke at alt er ok.
	 */
	public void printTrad(){
		System.out.println("\nTradene: ");
		
		for(int i = 0; i < antKjerner; i++){
			System.out.println("\tPa plass "+ i + " er trad: " + tradHotell[i] + " ID: " + tradHotell[i].id + " start og stopp: " + tradHotell[i].start + " - " + tradHotell[i].stopp + " max: " + tradHotell[i].max);
		}
		
		System.out.println("Max: "+ max);
		
	}
 
	public void printCount(){
		for(int i = 0; i < count.length; i++){
			for(int j = 0; j < count[i].length; j++){
				System.out.println("\tPa plass count["+i+"]["+j+"] er: " + count[i][j]);
			
			}
		}	
	}
	
	public void printFrekv(){
		for(int i = 0; i < count.length; i ++){
			System.out.println("\tPa plass count["+i+"]: "+count[i][0]);
		}
	}
	
	public void printB(){
		for(int i = 0; i < b.length; i ++){
			System.out.println("Pa plass b[" + i + "]: " + b[i]);
		}
	}
	
	public void printAccum(){
		for(int i = 0; i < accum.length; i ++){
			System.out.println("\tPa plass accum["+i+"] er: "+accum[i]);
		}
		
	}
	
	
	
	//lag en tradarray
	// bruk tradarray til a lose finne hogste
}
