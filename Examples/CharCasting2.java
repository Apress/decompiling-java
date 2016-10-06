public class CharCasting2 {
   public static void main(String args[]){
	    double myArray[] = new double[3];
	    char tell = 'c';
		for (int local1=0; local1<3; local1++)
			myArray[local1]=(double)local1*3.1415926;
		for (char local2=0; local2 < 128; local2++) {
		    System.out.println("ascii " + (int) local2 + " character "+ local2);
   			}
   		int local1 = (int)tell*25;
		System.out.println(tell + "me");
   }
}