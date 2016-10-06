public class StringToInt
{
  public void main(String [] local0)
    {
	 String local1="12";
     int local2=Integer.parseInt(local1);
     double local3=convertToDouble(local2);
     return;
    }
  public double convertToDouble(int local1)
    {
     return (double) local1;
    }
}