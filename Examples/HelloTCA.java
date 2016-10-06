import java.applet.Applet;
import java.awt.Graphics;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class HelloTCA extends Applet {
    public String getLocalHostName() {
		String result="";
        try {
            InetAddress address = InetAddress.getLocalHost();
            return (address.getHostName());
        }
        catch (Exception e) {
		   System.out.println(e);
           return ("Not known");
        }
    }
    public void paint(Graphics g) {
        g.drawString("Hello " + getLocalHostName() + "!", 50, 25);
    }
}
