package thaPack;

public class SekvensiellRadix { // metodane her var static
	
	
	/*
	 * 1. Lag random tall i int[]
	 * 2. finn ukesopg. med opg a, gjer den
	 * 3. finn ukesopg. med opg b, gjer den
	 * 4. knot c
	 * 5. knot d
	 * 
	 * */
	
	
	
	
	
	public static void main(String[] args){ //Djeegege
		
		/*
		SekvensiellRadix sr = new SekvensiellRadix();
		int[] t = {36, 12, 83, 53, 3, 95, 45, 12, 52, 16}; 
		sr.radix2(t);
		
		for(int i = 0; i < t.length ; i++){
			System.out.println(t[i]);
		}*/
	}
	// ver2. sorterer ogsŒ tall mellom 2**32 og 2**31
	//class SekvensiellRadix{
	void radix2(int [] a) {
	      // 2 digit radixSort: a[]
	      int max = a[0], numBit = 2, n =a.length;
	     // a) finn max verdi i a[]
	      for (int i = 1 ; i < n ; i++)
	           if (a[i] > max) max = a[i];
	      while (max >= (1L<<numBit) )numBit++; // antall siffer i max

	      // bestem antall bit i siffer1 og siffer2
	        int bit1 = numBit/2,
	              bit2 = numBit-bit1;
	      int[] b = new int [n];
	      radixSort( a,b, bit1, 0);    // f¿rste siffer fra a[] til b[]
	      radixSort( b,a, bit2, bit1);// andre siffer, tilbake fra b[] til a[]
	 } // end


	/** Sort a[] on one digit ; number of bits = maskLen, shiftet up ÔshiftÕ bits */
	void radixSort ( int [] a, int [] b, int maskLen, int shift){
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
