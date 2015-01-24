// for GUI components
import javax.swing.*;

// for event handling
import javax.swing.event.*;
import java.awt.event.*;

// for Graphics, Color
import java.awt.*;

// MigLayout
import net.miginfocom.swing.*;

// for double validation
import org.apache.commons.validator.*;

// Most of the funcionality is implemented here, for versatility
public class PolySolverTopPanel extends JPanel
{
	// default degree of polynomial
	private static final int DEFAULT_DEGREE = 3;
   
   // coefficient input fields per row
   private static final int TERMS_PER_ROW = 4;
   
  	// max degree of polynomial
	private static final int MAX_DEGREE = TERMS_PER_ROW * 5 - 1;
   
   // input field for degree
   private JSpinner degreeField;
   
   // solve button
   private JButton submit;
   
   // input form for coefficients
   private JPanel polyInputForm;
   
   // region for drawing diagrams
   private Diagram diagram;
   
   // panel to display any roots found
   private JPanel roots;
   
   // label for list of roots
   private JLabel allPaths;
   
   // error message to be displayed on bad input
   private JLabel error;
   
   // degree of polynomial
   private int degree;
	
   // Constructs a new PolySolverTopPanel.
   // Initializes all of the things and sets up the applet.
	public PolySolverTopPanel()
	{
      degree = DEFAULT_DEGREE;
      error = new JLabel();
      
      // fill the screen,
      // 2nd column (input form) and 2nd row (diagram) to grow
      setLayout(new MigLayout("fill", "[][grow]", "[][grow][]"));
      
      // controls, top left
      JPanel controls = new JPanel(new MigLayout("wrap 1"));
      add(controls, "aligny top");
      
      // input form for coefficients
	   polyInputForm = new JPanel(
            new MigLayout("wrap " + 2 * TERMS_PER_ROW));
      makePolyInputForm();
      
      // panel for creation and animation of diagram
      // row 1, extends through columns 0&1
      diagram = new Diagram();
      add(diagram, "cell 0 1 2 1, grow");
      
      // panel displaying found roots
      roots = new JPanel(new MigLayout("wrap 6"));
      allPaths = new JLabel("Roots:");
      roots.add(allPaths);
      // row 2, extends through columns 0&1
      add(roots, "cell 0 2 2 1");
		
		// input for degree
		controls.add(new JLabel("Degree:", JLabel.LEFT), "split 2");
		degreeField = new JSpinner(
         new SpinnerNumberModel(DEFAULT_DEGREE, 1, MAX_DEGREE, 1));
		degreeField.addChangeListener(
				new ChangeListener()
				{
               // called when degree input changed
					public void stateChanged(ChangeEvent e)
					{
						degree = ((Integer) degreeField.getValue()).intValue();
                  makePolyInputForm();
                  validate();
                  polyInputForm.repaint();
					}
				});
		controls.add(degreeField);
		
      // solve button, initiates diagram creation
		submit = new JButton("Solve");
      submit.addActionListener(
            new ActionListener()
            {
               // called when button clicked on
               public void actionPerformed(ActionEvent e)
               {
                  double[] coeffs = new double[degree + 1];
                  
                  // grab coefficient input from form
                  for (int i = degree; i >= 0; i--)
                  {
                     String a_i = ((JTextField) polyInputForm.
                           getComponent(2 * (degree - i))).getText();
                     if (GenericValidator.isDouble(a_i))
                     {
                        coeffs[i] = Double.parseDouble(a_i);
                     }
                     // something nonnumeric inputted
                     else
                     {
                        error.setText("<html><font color='red'>Coefficients" +
                        " must be decimal numbers.</font></html>");
                        polyInputForm.add(error, "south");
                        validate();
                        polyInputForm.repaint();
                        return;
                     }
                  }
                  
                  // 0 leading coefficient
                  if (coeffs[degree] == 0)
                  {
                     error.setText("<html><font color='red'>Leading " +
                     "coefficient cannot be 0.</font></html>");
                     polyInputForm.add(error, "south");
                     validate();
                     polyInputForm.repaint();
                     return;
                  }
                  
                  // no error found
                  polyInputForm.remove(error);
                  
                  roots.removeAll();
                  roots.add(allPaths);
                  
                  validate();
                  roots.repaint();
                  
                  // initiates animation
                  diagram.solve(degree, coeffs);
               }
            });
		controls.add(submit, "cell 0 1, align 50%");

		setVisible(true);
	}
	
   // generates an input form with the appropriate number of terms
   // (degree), within the alloted panel polyInputForm
	private void makePolyInputForm()
	{    
      // ditch old form
      polyInputForm.removeAll();
      
      // insert new coefficient input fields and terms
      for (int term = degree; term >= 0; term--)
		{
         polyInputForm.add(new JTextField(3));
			if (term > 1)
			{
            polyInputForm.add(new JLabel("<html>x<sup>" + term
                + "</sup> +</html>", JLabel.CENTER));
			}
			else if (term == 1)
			{
				polyInputForm.add(new JLabel("x +", JLabel.CENTER));
			}
		}
      
      // top row, right column
      add(polyInputForm, "cell 1 0, grow, wrap");   
      repaint();
	}
   
   // When notified by the diagram that a root has been found,
   // saves a shortcut to the solution path in a new button
   // and displays the bottom in the roots pane at the bottom.
   public void foundRoot(double lillAngle)
   {
      SavedPath found = new SavedPath(lillAngle, diagram);
      roots.add(found);
      validate();
      roots.repaint();
   }
}
