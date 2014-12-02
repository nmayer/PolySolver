/**
 *Nat Mayer
 *CS 50
 *Final Project
 *TF: Sam Green
 *
 *Lill's Method Polynomial Solver
 *Version 1.0
 */

import javax.swing.*;

public class PolySolverApplet extends JApplet
{
	public void init()
	{
		try
		{
			SwingUtilities.invokeAndWait(
					new Runnable() 
					{
						public void run()
						{
							setContentPane(new PolySolverTopPanel());
						}
					});
		}
		catch(Exception e)
		{
			System.err.println("GUI not created successfully.");
		}
	}
}