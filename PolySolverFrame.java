import javax.swing.*;

public class PolySolverFrame extends JFrame
{
	public PolySolverFrame()
	{
		setSize(600, 600);
		JPanel topPanel = new PolySolverTopPanel();
		setContentPane(topPanel);
		setVisible(true);
      topPanel.repaint();
	}
	
	public static void main(String[] args)
	{
		PolySolverFrame frame = new PolySolverFrame();
	}
}