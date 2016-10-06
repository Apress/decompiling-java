public class NewMath
{
  public static void main(String [] args)
    {
	 String local1="12";
	 int [] locarray = {2,3,4,5};
	 System.out.println(locarray.length);
     int local2=Integer.parseInt(local1);
     double local3=convertToDouble(local2++);
     int local4=++local2;
     local4+=257;
     System.out.println(local3 + " " + local4);
     return;
    }
  public static double convertToDouble(int local1)
    {
     return (double) local1;
    }
}