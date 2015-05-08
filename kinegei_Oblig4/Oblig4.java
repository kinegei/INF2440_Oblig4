import java.util.concurrent.CyclicBarrier;
import java.util.Random;

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



 class SekvensiellRadix {

	
	public void radix2(int [] a) {	
		// 2 digit radixSort: a[]
		int max = a[0], numBit = 2, n = a.length;
		// a) finn max verdi i a[]
		
		for (int i = 1 ; i < n ; i++)
			if (a[i] > max) max = a[i];
		
		while (max >= (1L<<numBit) )numBit++; // antall siffer i max

		// bestem antall bit i siffer1 og siffer2
		int bit1 = numBit/2, bit2 = numBit-bit1;
		int[] b = new int [n];
		
		radixSort( a,b, bit1, 0);    // første siffer fra a[] til b[]
		radixSort( b,a, bit2, bit1);// andre siffer, tilbake fra b[] til a[]
	} // end


	/** Sort a[] on one digit ; number of bits = maskLen, shiftet up ‘shift’ bits */
	public void radixSort ( int [] a, int [] b, int maskLen, int shift){
		int  acumVal = 0, j, n = a.length;
		int mask = (1<<maskLen) -1;
		int [] count = new int [mask+1];

		// b) count=the frequency of each radix value in a
		for (int i = 0; i < n; i++) {
			count[(a[i]>> shift) & mask]++;
		}

		// c) Add up in 'count' - accumulated values
		for (int i = 0; i <= mask; i++) {
			j = count[i];
			count[i] = acumVal;
			acumVal += j;
		}
		// d) move numbers in sorted order a to b
		for (int i = 0; i < n; i++) {
			b[count[(a[i]>>shift) & mask]++] = a[i];
		}
	}// end radixSort
}

 class RandomIntArray {
	
	public int[] makeArray(int t){
		int[] arr = new int[t];
		Random r = new Random();
		
		for(int i = 0; i < t ; i++){
			arr[i] = r.nextInt(90) + 10;
		}
		
		return arr;
	}
}



/***
 * Denne klassen
 * @author kinegei
 *
 */
 class RadixTrad extends Thread {
	int id; // brukes ikke enda
	int[]a;
	int[]b;
	int[][]cnt;
	int antTrader;
	ParallellRadix pr;
	
	int start, stopp;
	int max;
	int jmp;
	int myJmp;
	
	int[] accum;
	
	public RadixTrad(int id, ParallellRadix p) {
		this.id = id;
		pr = p;
		a = pr.a;
		b = pr.b; 
		antTrader = pr.antKjerner;
		cnt = pr.count;
 		accum = pr.accum;
	}
	
	public void run(){
		setStartStopp(a.length);
		
		//Finn maxverdi i sitt omrade
		finnMax();
		
		//Vent pa at alle har funnet max i sitt omrade
		try{
			pr.synkroniserMaxVerdi.await();
			
		}catch(Exception e){
			return;
		}
		//System.out.println("Funne max for: " + id);
		
		//fyllCount();
		//tellFrekvensen();
		radixsort(pr.bit1, 0, a, b);
		
		pr.count = new int[(1<<pr.bit2)][antTrader];
		
		radixsort(pr.bit2, pr.bit1, b, a);
	}
	
	private void radixsort(int maskLen, int shift, int[]en, int[] to){
		int  acumVal = 0, j = 0, n = en.length;
		int mask = (1<<maskLen) -1;
		
		
		//b) teller frekvensen
		setStartStopp(pr.count.length);
		for (int i = start; i <= stopp; i++) {
			pr.count[(en[i]>> shift) & mask][id]++; //00011011 >> 00000011 & 01111 = 000011 = 3
		}
		//System.out.println("Funne frekvens for " + id);
		
		try{
			pr.synkroniserFrekvensSummer.await();
		}catch(Exception e){
			return;
		}
		
		
		//c) Akkumuler verdier
		setStartStopp(pr.count.length);
		for(int i = start; i <= stopp; i++){
			//System.out.println("Id: " + id + " i: " + i + " acumVal: " + acumVal);
			j = pr.count[i][antTrader-1];
			//pr.count[i][antTrader-1] = acumVal;
			accum[i] = acumVal;
			acumVal += j;
		}
		
		
		//jmp = pr.count[stopp][0] + j;
		jmp = accum[stopp] + j;
		System.out.println("- Id: "+ id + "   jmp: "+ jmp);
		//System.out.println("Id: " + id + " j: "+ j + "  pr.count[stopp][0]: " + pr.count[stopp][0] + " = jmp:" + jmp);
		
		try{
			
			pr.synkroniserAkkumulering.await();
		}catch(Exception e){
			return;
		}
		
		System.out.println("id: "+id+" myJmp: " + myJmp);
		
		
		
		for(int i = start; i <= stopp; i++){
			accum[i] += myJmp;
		}
		
		
		System.out.println("har oppdatert myjmp for: "+ id);
		
		
		
		try{
			pr.synkroniserJmp.await();
		}catch(Exception e){
			return;
		}
		
		
		//d) flytt til b[]
		
		setStartStopp(en.length);
		
		
		
		System.out.println("Ny start stopp: "+ start+ " - " + stopp );
		
		for(int i = start; i <= stopp; i++){ 
			//System.out.println("ID: "+ id +" i: "+ i +" flytter: "+ en[i] + " til "+ pr.count[(en[i]>>shift+(myJmp)) & mask][0] + " (en[i]>>shift+(myJmp)) & mask: "+ ((en[i]>>shift+(myJmp)) & mask));
			//to[];
			int lilleTall = (pr.count[((en[i]>>shift) & mask)][id]++);
			int storeTall = (accum[((en[i]>>shift) & mask)]);
			to[lilleTall+storeTall] = en[i];  //to[(pr.count[((en[i]>>shift) & mask)][0]++)] = en[i];
			//System.out.println("\tID: "+ id +" flytta: "+ en[i] + " med i: "+i+" til to[] pa: "+ pr.count[(en[i]>>shift) & mask][0] + ". (en[i]>>shift) & mask: "+ ((en[i]>>shift) & mask));
			System.out.println("Id: "+id+ "  i: "+i+"  Lille: "+ lilleTall + "  Store: " + storeTall);
		}
		
		try{
			pr.synkroniserOverforing.await();
		}catch(Exception e){
			return;
		}
		
		
	}
	

	
	
	

	
	private void finnMax(){
		max = a[start];
		for(int i = start + 1; i <= stopp; i++){
			if(a[i] > max){
				max = a[i];
			}
		}
		//pr.setMax(max);
		//System.out.println("ID:" + id + " max: " + max);
	}
	
	
	private void setStartStopp(int l){
		start = id*(l/antTrader);   // var id*(a.length/antTrader)
		if(id == antTrader-1){
			stopp = l -1;
		}else{
			stopp = start - 1 + (l/antTrader);
		}
		//System.out.println("Id: " + id + "  Start & stopp: " + start + " - " + stopp);
	}

}



 class ParallellRadix {
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
		antKjerner = Runtime.getRuntime().availableProcessors();
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
	
}
