
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.imageio.ImageIO;

/**
 *
 * @author jballari
 */
public class SC {

    
    static boolean debug = false;
    static int scrollLength = 3;
    static int scrollbar_width = 22;
    static int sampler_width_percentage = 20;
    static int sampler_height = 75;
    
    
    static int width = 0;
    static int height = 0;
    static int lastSamplerHeight = -1;
    static int mHeight = 0;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {

        int dummy;
        switch(args.length) {
        
            case 5:
                dummy = Integer.parseInt(args[4]);
                if(dummy != -1) {
                    sampler_height = dummy;
                } 
            case 4:
                dummy = Integer.parseInt(args[3]);
                if(dummy != -1) {
                    sampler_width_percentage = dummy;
                } 
            case 3:
                dummy = Integer.parseInt(args[2]);
                if(dummy != -1) {
                    scrollbar_width = dummy;
                } 
            case 2:
                dummy = Integer.parseInt(args[1]);
                if(dummy != -1) {
                    scrollLength = dummy;
                } 
            case 1:
                String s = args[0];
                if(s.equalsIgnoreCase("help") || s.equalsIgnoreCase("/?")) {
                    System.out.println("");
                    System.out.println("usage: java SC <debug> <mouseWheel_notches> <scroll_bar_width> <sampler_width_percentage> "
                            + "<sampler_height>");
                    
                    System.out.println("");
                    System.out.println("debug: true or false, default value is false");
                    System.out.println("mouseWheel_notches: number of notches the mouse wheel is to be rotated for each screen shot, "
                            + "default value is 3");
                    System.out.println("scroll_bar_width: number of pixels that needs to be stripped from the right side of the final "
                            + "image which contains the scroll bar, default value is 22");
                    System.out.println("sampler_width_percentage: the percentage of width from the center that needs to be taken for "
                            + "an image sample, default value is 20");
                    System.out.println("sampler_height: the height in pixels that needs to taken for an Image sample, default value"
                            + "is 75");
                    System.out.println("");
                    System.out.println("Note: to leave any of these values to default use -1");
                    System.exit(0);
                }
                if(s.equalsIgnoreCase("true")) {
                    debug = true;
                }
            default:
                break;
        
        }
        
        System.out.println("");
        System.out.println("Click on the Scroll bar of the web page and dont move");
        System.out.println("");
        long start = System.currentTimeMillis();
        // createing an Instance of the Bot
        Robot bot = new Robot();

        
        
        // getting primary screen width and height
        height = Toolkit.getDefaultToolkit().getScreenSize().height;
        width = Toolkit.getDefaultToolkit().getScreenSize().width;
        if(debug) {
            System.out.println("Screen height is " + height + " screen width is " + width);
        }
        // initializing varibles for the ScreenShot.
        // mHeight holds the height of the screenshot.
        BufferedImage screenShot = new BufferedImage(width, height * 20, BufferedImage.TYPE_INT_ARGB);
        mHeight = 0;
        
        
        
        System.out.println("");
        Thread.sleep(200);

        System.out.println("Starting.. in 3 seconds");
        Thread.sleep(1000);

        System.out.println("Starting.. in 2 seconds");
        Thread.sleep(1000);

        System.out.println("Starting.. in 1 second");
        Thread.sleep(1000);

        System.out.println("Started.");

      
        if(debug) {
            System.out.println("Pressing F11");
        }
        bot.keyPress(KeyEvent.VK_F11);
        bot.keyRelease(KeyEvent.VK_F11);
        
        // waiting for Chrome "Press F11 to exit Full Screen" dialog to disspear.
        Thread.sleep(5000);
        
        
        //bot.mouseMove(width+20, height/2);
        
        
        // Taking the initial Snap Shot
        BufferedImage image1 = bot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
        
        // stripping the Right side of the screen which contains the vertical scroll bar from the inital image.
        image1 = image1.getSubimage(0, 0, image1.getWidth()-scrollbar_width, image1.getHeight());
        
        
        if(debug) {
            ImageIO.write(image1, "png", new File("image1.png"));
        }
        
        
        //Thread.sleep(300);
        
        
        // image2: second snapShot which is taken after scrolling.
        // image3: is used to store the new porition which appreas in image2 after scrolling.
        BufferedImage image2, image3;
        
        // getting Graphics object from the screenShot.
        Graphics g = screenShot.getGraphics();
        
        // writing the initial snapshot
        g.drawImage(image1, 0, 0, null);
        mHeight += image1.getHeight();        
        Thread.sleep(300);
        


        while (true) {
            
            bot.mouseWheel(scrollLength);
            // waiting for scroll to complete
            Thread.sleep(500);
            image2 = bot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
            image2 = image2.getSubimage(0, 0, image2.getWidth()-scrollbar_width, image2.getHeight());
            if(debug) {
                ImageIO.write(image2, "png", new File("image2.png"));
            }
            
            image3 = diff(image1, image2);

            if (image3 == null) {
                break;
            }

            image1 = image2;

            g.drawImage(image3, 0, mHeight, null);
            mHeight += image3.getHeight();

        }

        g.dispose();
        screenShot = screenShot.getSubimage(0, 0, screenShot.getWidth()-scrollbar_width, mHeight);
        String filename = "screenshot_"+
                        (new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime()))+
                        ".png" ;
        System.out.println("\nSaving screenshot as " + filename);
        System.out.println("");
        ImageIO.write(screenShot, "png", 
                new File(filename) );
        Thread.sleep(300);

        if(debug) {
            System.out.println("Pressing F11 again");
        }
        bot.keyPress(KeyEvent.VK_F11);
        bot.keyRelease(KeyEvent.VK_F11);
        long end = System.currentTimeMillis();
        
        DecimalFormat formatter = new DecimalFormat("#0.00000");
        
        System.out.println("Execution time is " + formatter.format((end - start) / 1000d) + " seconds");
    }

    private static BufferedImage diff(BufferedImage image1, BufferedImage image2) throws Exception {

        if (equalImages(image1, image2)) {
            return null;
        }
        
        int smapler_width = (int) ((image1.getWidth()) * (sampler_width_percentage * 0.01));
        int xoffset = (image1.getWidth()/2) - (smapler_width/2);
        
        if(debug) {
            System.out.println("sampler_width = " + smapler_width+" , xoffset = "+ xoffset + " , Image Width = " + image1.getWidth());
        }
        
        BufferedImage sampler = image1.getSubimage(xoffset, image1.getHeight() - sampler_height, smapler_width, sampler_height);
        if(debug) {
            ImageIO.write(sampler, "png", new File("sampler.png"));
        }
        //Thread.sleep(300);
        
        // start sampling from lastSamplerHeight
        if(lastSamplerHeight != -1) {
            if(debug) {
                System.out.println("lastSamplerHeight value is " + lastSamplerHeight);
            }
            BufferedImage partOfImage2 = image2.getSubimage(xoffset, lastSamplerHeight, smapler_width, sampler_height);
            //ImageIO.write(partOfImage2, "png", new File("partOFIamge2_"+i+".png"));
            
            if(equalImages(sampler, partOfImage2)) {
                BufferedImage diffImage = image2.getSubimage(0, lastSamplerHeight+sampler_height, image2.getWidth(), image2.getHeight()-lastSamplerHeight-sampler_height);
                return diffImage;
            }
        
        }
        
        for (int i = 0; i < image2.getHeight(); i++) {
            //System.out.println("i value is " + i);
            BufferedImage partOfImage2 = image2.getSubimage(xoffset, i, smapler_width, sampler_height);
            //ImageIO.write(partOfImage2, "png", new File("partOFIamge2_"+i+".png"));
            
            if(equalImages(sampler, partOfImage2)) {
                BufferedImage diffImage = image2.getSubimage(0, i+sampler_height, image2.getWidth(), image2.getHeight()-i-sampler_height);
                lastSamplerHeight = i;
                return diffImage;
            }
        }
        

        return null;
    }

    private static boolean equalImages(BufferedImage image1, BufferedImage image2) throws Exception {

        int img1Height = image1.getHeight();
        int img1Width = image1.getWidth();
        int img2Height = image2.getHeight();
        int img2Width = image2.getWidth();

        if (img1Height != img2Height || img1Width != img2Width) {
            throw new Exception("unkown error: 1234");
        }

        boolean isSame = true;
        for (int i = 0; i < img1Width; i++) {
            for (int j = 0; j < img1Height; j++) {
                if (image1.getRGB(i, j) != image2.getRGB(i, j)) {
                    isSame = false;
                    break;
                }
            }
        }

        return isSame;
    }

}
