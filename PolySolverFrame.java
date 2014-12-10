// for JFrame
import javax.swing.*;

// Just a frame to display the program.
public class PolySolverFrame extends JFrame
{
	// Constructs and displays a new frame, 
   // of the appropriate size,
   // populating it with the PolySolverTopPanel which
   // carries the functionality of the program.
   public PolySolverFrame()
	{
		setSize(600, 600);
		JPanel topPanel = new PolySolverTopPanel();
		setContentPane(topPanel);
		setVisible(true);
	}
	
   // Entry point to program.
   // Simply creates a new frame, as specified above.
	public static void main(String[] args)
	{
		PolySolverFrame frame = new PolySolverFrame();
	}
}