
public class ArmController {
   public static void main(String[] args){
      //runs arm as a thread so other body parts can run on their own threads too
      Thread arm = new Thread(new MasterArm(100, 30, 50, 10, 3, 5));
      arm.start();
   }
}
