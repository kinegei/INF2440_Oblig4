package Oblig4;

/***
 * Denne klassen
 * @author kinegei
 *
 */
public class RadixTrad extends Thread {
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
