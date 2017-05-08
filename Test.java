import java.util.*;
import java.io.*;
class Test {
	public static void main(String args[]) throws IOException {
		Random r = new Random() ;
		for(int i = 0 ; i < 1000 ; i++) {
			System.out.println(r.nextInt(1000)) ;
			System.out.println(r.nextInt(1000)) ;
		}
	}
}