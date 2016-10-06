public class Recurses {
	public static void main(String[] args)
	{
		System.out.println(recurse(25));
		return;
	}

	public static String recurse(int num)
	{
		if (num!=0)
		   return "crap! " + recurse(num-1);
		else
		   return "dammit!";
	}
}