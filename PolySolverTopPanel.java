// for GUI components
import javax.swing.*;

// for event handling
import javax.swing.event.*;
import java.awt.event.*;

// for BorderLayout
import java.awt.*;

// MigLayout
import net.miginfocom.swing.*;

// for double validation
import org.apache.commons.validator.*;

class PolySolverTopPanel extends JPanel
{
	// default degree of polynomial
	private static final int DEFAULT_DEGREE = 3;
	
	// max degree of polynomial
	private static final int MAX_DEGREE = 49;
   
   // coefficient input fields per row
   private static final int TERMS_PER_ROW = 5;
   
   // input field for degree
   private JSpinner degreeField;
   
   // solve button
   private JButton submit;
   
   // input form for coefficients
   private JPanel polyInputForm;
   
   // region for drawing diagrams
   private JPanel diagram;
   
   // degree of polynomial
   private int degree;
   
   // coefficients of polynomial
   private double[] coeffs;
	
	PolySolverTopPanel()
	{
      degree = DEFAULT_DEGREE;
      
      setLayout(new MigLayout("insets 20"));
      
      // panel for creation and animation of diagram
      diagram = new JPanel();
      add(diagram, "south, grow");
      
      // input form for coefficients
	   polyInputForm = new JPanel(new MigLayout("wrap " + 2 * TERMS_PER_ROW));
      makePolyInputForm();
		
		// input for degree
		add(new JLabel("Degree:", JLabel.LEFT), "split 2");
		degreeField = new JSpinner(new SpinnerNumberModel(DEFAULT_DEGREE, 1, MAX_DEGREE, 1));
		degreeField.addChangeListener(
				new ChangeListener()
				{
					public void stateChanged(ChangeEvent e)
					{
						degree = ((Integer) degreeField.getValue()).intValue();
                  makePolyInputForm();
                  validate();
                  polyInputForm.repaint();
					}
				});
		add(degreeField, "wrap");
		
		submit = new JButton("Solve");
      submit.addActionListener(
            new ActionListener()
            {
               public void actionPerformed(ActionEvent e)
               {
                  coeffs = new double[degree + 1];
                  
                  for (int i = degree; i >= 0; i--)
                  {
                     String a_i = ((JTextField) polyInputForm.getComponent(2 * (degree - i))).getText();
                     if (GenericValidator.isDouble(a_i))
                     {
                        coeffs[i] = Double.parseDouble(a_i);
                        diagram.add(new JLabel(a_i));
                     }
                  }
                  validate();
                  diagram.repaint();
               }
            });
		add(submit, "wrap");

		setVisible(true);
	}
	
	private void makePolyInputForm()
	{    
      polyInputForm.removeAll();
      
      for (int term = degree; term >= 0; term--)
		{
			polyInputForm.add(new JTextField(2));
			if (term > 1)
			{
				polyInputForm.add(new JLabel("x^" + term + " +", JLabel.CENTER));
			}
			else if (term == 1)
			{
				polyInputForm.add(new JLabel("x +", JLabel.CENTER));
			}
		}
      
      add(polyInputForm, "east, grow");
	}
}