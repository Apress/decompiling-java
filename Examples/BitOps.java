public class BitOps
{
  public static void main(String [] args)
    {
	 int local1=13;
     int shift1=1,shift2=2,shift3=3;
     int local2,local3,local4,local5,local6,local7;
     local2 = local1 << shift1;
     local3 = local1 >> shift2;
     local4 = local1 >>> shift3;
     local5 = local1 & local2;
     local6 = local1 | local4;
     local7 = local1 ^ local3;
    }
}