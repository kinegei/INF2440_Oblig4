package Oblig4;

import java.util.Random;

public class RandomIntArray {
	
	public int[] makeArray(int t){
		int[] arr = new int[t];
		Random r = new Random();
		
		for(int i = 0; i < t ; i++){
			arr[i] = r.nextInt(90) + 10;
		}
		
		return arr;
	}
}
