import java.io.*;

public class DoWhile {
	public static void main(String[] args)
	{
		double finalval = 0.0;
		int test = 12;
		do{

			test += 1;
			test -= 4;
			test *= 5;
			test /= 3;
	       } while (test<=100);
		finalval += (double) test;
		return;
	}
}
