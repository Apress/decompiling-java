public  class ForLoop
{
  public static void main(String[] local0)
    {
	 int x=0;
     for (String local1="outoutoutdamnedspot"; !local1.equals("spot"); local1 = local1.substring(3)) //11
       {
        System.out.println(++x);
       }
     for (String local1="outoutoutdamnedspot"; !local1.equals("spot"); local1 = local1.substring(3)) //11
       {
        System.out.println(++x);
       }
     for (String local1="outoutoutdamnedspot"; !local1.equals("spot"); local1 = local1.substring(3)) //11
       {
        System.out.println(++x);
       }
     return; //132
    }
}