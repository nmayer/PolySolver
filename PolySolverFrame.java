import javax.swing.*;

public class PolySolverFrame extends JFrame
{
	public PolySolverFrame()
	{
		setSize(600, 600);
		JPanel topPanel = new PolySolverTopPanel();
		setContentPane(topPanel);
		setVisible(true);
	}
	
	public static void main(String[] args)
	{
		PolySolverFrame frame = new PolySolverFrame();
	}
}