
public class RobotWheel extends Robot{
   String robotName;
   boolean rotateState;
   public RobotWheel(String name, boolean rotation){
      robotName = name;
      Set(Robot.fast);
      Set(Robot.on);
      rotateState = rotation;
      if(rotation)
         Set(Robot.clockwise);
      else 
         Set(Robot.counter);
   }

   public double rotate(double rot){
      rot = Math.abs(rot);
      if(rot >= 1){
         Set(Robot.rotate360);
         System.out.println("rotating 360 - 1 cm");
         rot -= 1;
      }
      else if(rot >= 0.75){
         Set(Robot.rotate270);
         System.out.println("rotating 270 - .75 cm");
         rot -= 0.75;
      }
      else if(rot >= 0.5){
         Set(Robot.rotate180);
         System.out.println("rotating 180 - .5 cm");
         rot -= 0.5;
      }
      //rotate anyways - doesn't matter if you have a negative - escape sequence
      else {
         Set(Robot.rotate90);
         System.out.println("rotating 90 - .25 cm");
         rot -= 0.25;
      }
      Execute();
      return rot;
   }
   public void Execute(){
      System.out.println(robotName + " is moving " + ((rotateState) ? "clockwise." : "counter-clockwise."));
      //super.Execute();
   }
}
