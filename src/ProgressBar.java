import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import java.beans.*;
import java.util.Random;
 
public class ProgressBar extends JPanel
                             implements ActionListener, 
                                        PropertyChangeListener {
 
    private JProgressBar progressBarX, progressBarY, progressBarZ;
    private JButton startButton;
    private JTextArea taskOutput;
    protected MasterArm arm;
    protected TaskX taskX;
    protected TaskY taskY;
    protected TaskZ taskZ;
    public static double xInc, yInc, zInc;
    public static double x, y, z;
    public static int waitTime = 100;
 
    class Task extends SwingWorker<Void, Void> {
       protected double prevVal;
        /*
         * Executed in event dispatching thread
         */
        @Override
        public void done() {
            Toolkit.getDefaultToolkit().beep();
            startButton.setEnabled(true);
            setCursor(null); //turn off the wait cursor
            taskOutput.append("Done!\n");
        }

      @Override
      protected Void doInBackground() throws Exception {
         return null;
      }
    }
    
    class TaskX extends Task{
       /*
        * Main task. Executed in background thread.
        */
       @Override
       public Void doInBackground() {
          Random random = new Random();
          double progress = 0;
          firePropertyChange("X", null, 0);
          while(progress < 100){
             prevVal = progress;
             progress = (int) Math.min(arm.getXArmProgress(), 100);
             firePropertyChange("X", (int)prevVal, (int)progress);
          }
          return null;
       }
    }
    
    class TaskY extends Task{
       /*
        * Main task. Executed in background thread.
        */
       @Override
       public Void doInBackground() {
          Random random = new Random();
          double progress = 0;
          firePropertyChange("Y", null, 0);
          while(progress < 100){
             prevVal = progress;
             progress = (int) Math.min(arm.getYArmProgress(), 100);
             firePropertyChange("Y", (int)prevVal, (int)progress);
          }
          return null;
       }
    }
    
    class TaskZ extends Task{
       /*
        * Main task. Executed in background thread.
        */
       @Override
       public Void doInBackground() {
          Random random = new Random();
          double progress = 0;
          firePropertyChange("Z", null, 0);
          while(progress < 100){
             prevVal = progress;
             progress = (int) Math.min(arm.getZArmProgress(), 100);
             firePropertyChange("Z", (int)prevVal, (int)progress);
          }
          return null;
       }
    }
    
    public ProgressBar() {
        super(new BorderLayout());
 
        //Create the demo's UI.
        startButton = new JButton("Start");
        startButton.setActionCommand("start");
        startButton.addActionListener(this);
 
        progressBarX = new JProgressBar(0, 100);
        progressBarX.setValue(0);
        progressBarX.setStringPainted(true);
        progressBarY = new JProgressBar(0, 100);
        progressBarY.setValue(0);
        progressBarY.setStringPainted(true);
        progressBarZ = new JProgressBar(0, 100);
        progressBarZ.setValue(0);
        progressBarZ.setStringPainted(true);
 
        taskOutput = new JTextArea(5, 20);
        taskOutput.setMargin(new Insets(5,5,5,5));
        taskOutput.setEditable(false);
 
        JPanel panel = new JPanel();
        panel.add(startButton);
        panel.add(progressBarX);
        panel.add(progressBarY);
        panel.add(progressBarZ);
 
        add(panel, BorderLayout.PAGE_START);
        add(new JScrollPane(taskOutput), BorderLayout.CENTER);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
 
    }
 
    /**
     * Invoked when the user presses the start button.
     */
    public void actionPerformed(ActionEvent evt) {
        startButton.setEnabled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        //Instances of javax.swing.SwingWorker are not reusuable, so
        //we create new instances as needed.
        arm = new MasterArm(x, y, z, xInc, yInc, zInc, waitTime);
        Thread armThread = new Thread(arm);
        armThread.start();
        
        taskX = new TaskX();
        taskX.addPropertyChangeListener(this);
        taskX.execute();
        taskY = new TaskY();
        taskY.addPropertyChangeListener(this);
        taskY.execute();
        taskZ = new TaskZ();
        taskZ.addPropertyChangeListener(this);
        taskZ.execute();
    }
 
    /**
     * Invoked when task's progress property changes.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        if ("X".equals(evt.getPropertyName())) {
            int progress = (Integer) evt.getNewValue();
            progressBarX.setValue(progress);
            taskOutput.append(String.format(
                    "Completed %d%% of X Arm task.\n", progress));
        } 
        else if("Y".equals(evt.getPropertyName())) {
            int progress = (Integer) evt.getNewValue();
            progressBarY.setValue(progress);
            taskOutput.append(String.format(
                    "Completed %d%% of Y Arm task.\n", progress));
        } 
        else if("Z".equals(evt.getPropertyName())) {
            int progress = (Integer) evt.getNewValue();
            progressBarZ.setValue(progress);
            taskOutput.append(String.format(
                    "Completed %d%% of Z Arm task.\n", progress));
        } 
    }
 
 
    /**
     * Create the GUI and show it. As with all GUI code, this must run
     * on the event-dispatching thread.
     */
    private static void createAndShowGUI( double xT, double yT, double zT, double xI, double yI, double zI, int time) {
        //Create and set up the window.
        x = xT;
        y = yT;
        z = zT;
        xInc = xI;
        yInc = yI;
        zInc = zI;
        waitTime = time;
        JFrame frame = new JFrame("ProgressBarDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 
        //Create and set up the content pane.
        JComponent newContentPane = new ProgressBar();
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);
 
        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
 
    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
               //this means 150 is x's goal, 100 is y's goal, 125 is z's goal
               //if want to be synchronous, then give equal increments
               //if want to be asynchronous, then give unequal increments
                createAndShowGUI(50, 200, 10, .5, 2, .1, 5);
            }
        });
    }
}