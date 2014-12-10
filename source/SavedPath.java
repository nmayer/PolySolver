// for GUI components
import javax.swing.*;

// for event handling
import javax.swing.event.*;
import java.awt.event.*;

// A button storing an angle value, capable of instructing
// a diagram to draw the corresponding Lill path.
public class SavedPath extends JButton implements ActionListener
{
   // generating angle of the saved path
   private double angle;
   
   // Diagram to generate the path
   private Diagram diagram;
   
   // Constructs a SavedPath, storing the given angle and linked
   // to the given Diagram.
   public SavedPath(double angle, Diagram diagram)
   {
      super(String.format("%.2f", -Math.tan(angle)));
      this.angle = angle;
      this.diagram = diagram;
      addActionListener(this);
   }
   
   // Called when button is clicked.
   // Tells the Diagram to display the angle.
   public void actionPerformed(ActionEvent e)
   {
      diagram.showPath(angle);
   }
}