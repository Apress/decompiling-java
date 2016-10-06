import java.io.*;

public class Boolean {
	public static void main(String[] args) {
		int size = 12;
		System.out.println(sizeIs(size,true));
	}

	public static String sizeIs(int size, boolean usehex)
	{
		if (!usehex)
		   return Integer.toHexString(size);
		else
		   return Integer.toBinaryString(size);
	}
}
