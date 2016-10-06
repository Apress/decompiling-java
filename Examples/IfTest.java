import java.io.*;

public class IfTest {
	public static void main(String[] args)
	{
		int test = 12;
		test *= test;
		if (test>100)
		   test=test%100;
		return;
	}

}