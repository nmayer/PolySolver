// for GUI components
import javax.swing.*;

// for event handling
import javax.swing.event.*;

// for BorderLayout
import java.awt.*;

// MigLayout
import net.miginfocom.swing.*;

class PolySolverTopPanel extends JPanel
{
	// default degree of polynomial
	private static final int DEFAULT_DEGREE = 3;
	
	// max degree of polynomial
	private static final int MAX_DEGREE = 20;
	
	PolySolverTopPanel()
	{
		setLayout(new BorderLayout());
		
		// control panel
		final JPanel controls = new JPanel();
		controls.setMaximumSize(getSize());
		controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));
		
		// top control, degree of polynomial
		JPanel degreeControl = new JPanel();
		degreeControl.add(new JLabel("Degree:", JLabel.LEFT));
		final JSpinner degreeField = new JSpinner(new SpinnerNumberModel(DEFAULT_DEGREE,
																1, MAX_DEGREE, 1));
		degreeField.addChangeListener(
				new ChangeListener()
				{
					public void stateChanged(ChangeEvent e)
					{
						// remove previous polynomial form
						controls.remove(1);
						
						// make a new one with the right number of terms
                  int deg = ((Integer) degreeField.getValue()).intValue();										  
						JPanel polyInput = makePolyInput(deg);
                  polyInput.setPreferredSize(new Dimension(300, 38*(deg / 7 + 1)));
                  controls.add(polyInput, 1);
						controls.getParent().validate();
						controls.repaint();
					}
				});
		degreeControl.add(degreeField);
		controls.add(degreeControl);
		
		// input form for polynomial
	   controls.add(makePolyInput(((Integer) degreeField.getValue())
                                                   .intValue()), 1);
		
		JButton submit = new JButton("Solve");
		submit.setAlignmentX((float) 0.5);
		controls.add(submit);
		
		add(controls, BorderLayout.NORTH);
		setVisible(true);
	}
	
	private static JPanel makePolyInput(int deg)
	{
		JPanel polyInput = new JPanel();
      
      for (int term = deg, i = 0; term >= 0; term--, i++)
		{
			polyInput.add(new JTextField(2));
			if (term > 1)
			{
				polyInput.add(new JLabel("x^" + term + " +", JLabel.CENTER));
			}
			else if (term == 1)
			{
				polyInput.add(new JLabel("x +", JLabel.CENTER));
			}
		}
		
      polyInput.setLayout(new MigLayout("wrap 12"));
		return polyInput;
	}
}