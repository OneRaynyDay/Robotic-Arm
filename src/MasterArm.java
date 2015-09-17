import java.util.concurrent.locks.ReentrantLock;


public class MasterArm implements Runnable{
   public static volatile boolean goX;
   public static volatile boolean goY;
   public static volatile boolean goZ;
   public static volatile boolean xDied;
   public static volatile boolean yDied;
   public static volatile boolean zDied;

   protected Thread x;
   protected Thread y;
   protected Thread z;
   private XArm xarm;
   private YArm yarm;
   private ZArm zarm;
   private double xThreshold, yThreshold, zThreshold;
   private int waitTime;
   //increments
   
   private double xInc, yInc, zInc;   
   public MasterArm(double x, double y, double z){
      goX = true;
      goY = false;
      goZ = false;
      xThreshold = x;
      yThreshold = y;
      zThreshold = z;
      xInc = yInc = zInc = 1;
      if(x < 0)
         xInc = -1;
      if(y < 0)
         yInc = -1;
      if(z < 0)
         zInc = -1;
      waitTime = 1000;
      xarm = new XArm(xThreshold, xInc);
      yarm = new YArm(yThreshold, yInc);
      zarm = new ZArm(zThreshold, zInc);
   }
   
   public MasterArm(double x, double y, double z, double xI, double yI, double zI){
      goX = true;
      goY = false;
      goZ = false;
      xThreshold = x;
      yThreshold = y;
      zThreshold = z;
      xInc = xI;
      yInc = yI;
      zInc = zI;
      //How are you going to have a robot increment in the wrong direction?
      if((xThreshold/xInc < 1 || yThreshold/yInc < 1 || zThreshold/zInc < 1) || (xInc % 0.25 != 0 || yInc % 0.25 != 0 || zInc % 0.25 != 0))
         throw new IllegalArgumentException();
      waitTime = 1000;
      xarm = new XArm(xThreshold, xInc);
      yarm = new YArm(yThreshold, yInc);
      zarm = new ZArm(zThreshold, zInc);
   }
   
   public MasterArm(double x, double y, double z, double xI, double yI, double zI, int time){
      goX = true;
      goY = false;
      goZ = false;
      xThreshold = x;
      yThreshold = y;
      zThreshold = z;
      xInc = xI;
      yInc = yI;
      zInc = zI;
      //How are you going to have a robot increment in the wrong direction?
      if(xThreshold/xInc < 1 || yThreshold/yInc < 1 || zThreshold/zInc < 1)
         throw new IllegalArgumentException();
      waitTime = time;
      xarm = new XArm(xThreshold, xInc);
      yarm = new YArm(yThreshold, yInc);
      zarm = new ZArm(zThreshold, zInc);
   }
   
   public void run(){
      //start the thread of the X,Y,Z arms as well
      Thread x = new Thread(xarm);
      Thread y = new Thread(yarm);
      Thread z = new Thread(zarm);
      x.start();
      y.start();
      z.start();
   }
   
   public double getXArmProgress(){
      return xarm.progress;
   }
   public double getYArmProgress(){
      return yarm.progress;
   }
   public double getZArmProgress(){
      return zarm.progress;
   }
   
   abstract class ArmComponent implements Arm{
      double progress = 0;
      RobotWheel r;
      String name;
      public void manageOperations(){
         while(progress < 100){
            waitForNext();
            rotate();
            try {
               Thread.sleep(waitTime);
            } catch (InterruptedException e) { e.printStackTrace(); }
            setNext();
         }
         System.out.println("Done motion.");
      }
      public double getProgress(){ return progress; }
   }
   
   class XArm extends ArmComponent{
      public final double THRESHOLD;
      //increment = add % every time, chunk = add x amount every time
      double increment;
      public XArm(double p, double i){
         THRESHOLD = p;
         increment = i;
         name = "XArm";
         boolean rotState = (i > 0) ? true:false;
         r = new RobotWheel(name , rotState);
      }
      
      public void rotate() {
         //how much robot needs to rotate
         //rounds to lowest
         double rot = increment;
         System.out.println(name + " needs to rotate a minimum of " + rot + " cm's.");
         while(progress < 100 && rot > 0){
            //if each rotation requires more than 
            rot = r.rotate(rot);
            try {
               Thread.sleep(waitTime);
            } catch (InterruptedException e) { e.printStackTrace(); }
         }
         progress += increment/THRESHOLD * 100;
         System.out.println("Switching to YArm - " + name + "'s progress: " + progress + "%.");
      }
      public void setNext() { goY = true; goX = false; }
      public void waitForNext() { while(!goX && !zDied); return; }
      public void run() { manageOperations(); xDied = true;}
   }
   
   class YArm extends ArmComponent{
      final double THRESHOLD;
      //increment = add % every time, chunk = add x amount every time
      double increment;
      public YArm(double p, double i){
         THRESHOLD = p;
         increment = i;
         name = "YArm";
         boolean rotState = (i > 0) ? true:false;
         r = new RobotWheel(name , rotState);
      }
      public void rotate() {
         //how much robot needs to rotate
         //rounds to lowest
         double rot = Math.abs(increment);
         System.out.println("YArm needs to rotate a minimum of " + rot+ " cm's.");
         while(progress < 100 && rot > 0){
            //if each rotation requires more than, then just go anyways
            rot = r.rotate(rot);
            try {
               Thread.sleep(waitTime);
            } catch (InterruptedException e) { e.printStackTrace(); }
         }
         progress += increment/THRESHOLD * 100;
         System.out.println("Switching to ZArm- " + name + "'s progress: " + progress + "%.");
      }   
      public void setNext() { goZ = true; goY = false;}   
      public void waitForNext() { while(!goY && !xDied); return; }   
      public void run() { manageOperations(); yDied = true;}
   }
   
   class ZArm extends ArmComponent{
      final double THRESHOLD;
      //increment = add % every time, chunk = add x amount every time
      double increment;
      public ZArm(double p, double i){
         THRESHOLD = p;
         increment = i;
         name = "ZArm";
         boolean rotState = (i > 0) ? true:false;
         r = new RobotWheel(name , rotState);
      }
      public void rotate() {      
         //how much robot needs to rotate
         //rounds to lowest
         double rot = increment;
         System.out.println(name + " needs to rotate a minimum of " + rot+ " cm's.");
         while(progress < 100 && rot > 0){
            //if each rotation requires more than 
            rot = r.rotate(rot);
            try {
               Thread.sleep(waitTime);
            } catch (InterruptedException e) { e.printStackTrace(); }
         }
         progress += increment/THRESHOLD * 100;
         System.out.println("Switching to XArm - " + name + "'s progress: " + progress + "%.");
      }
      public void setNext() { goX = true; goZ = false;}
      public void waitForNext() {  while(!goZ && !yDied); return;  }
      public void run() { manageOperations(); zDied = true;}
   }
   
}
