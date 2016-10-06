public class ArrayInit
{

    public static int[] arr = {1, 8, 27, 64, 125, 216, 343, 512, 729, 1000};
	private int [] a = new int[5];
	public String mork = "From ork!";

	public void clearA()
	{
		a[0]=0;
	}

	public int getA()
	{
		return a[0];
	}

	public void setA(int x)
	{
		a[0]=x;
	}

    public void main(String args[])
    {
		int[] arr2 = {1, 8, 27, 64, 125, 216, 343, 512, 729, 1000};
        for (int i = 0; i < 10; i++)
            System.out.println("arr[" + i + "] = " + arr[i]);
    }
}