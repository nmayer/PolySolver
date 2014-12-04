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

public class PolySolverTopPanel extends JPanel
{
	// default degree of polynomial
	private static final int DEFAULT_DEGREE = 3;
   
   // coefficient input fields per row
   private static final int TERMS_PER_ROW = 5;
   
  	// max degree of polynomial
	private static final int MAX_DEGREE = TERMS_PER_ROW * 8 - 1;
   
   // input field for degree
   private JSpinner degreeField;
   
   // solve button
   private JButton submit;
   
   // input form for coefficients
   private JPanel polyInputForm;
   
   // region for drawing diagrams
   private Diagram diagram;
   
   // degree of polynomial
   private int degree;
	
	public PolySolverTopPanel()
	{
      degree = DEFAULT_DEGREE;
      
      setLayout(new MigLayout("fillx", "[][grow]", "[][grow]"));
      // setBackground(Color.GREEN);
      
      // controls, top left
      JPanel controls = new JPanel(new MigLayout("wrap 1"));
      // controls.setBackground(Color.PINK);
      add(controls, "aligny top");
      
      // input form for coefficients
	   polyInputForm = new JPanel(new MigLayout("wrap " + 2 * TERMS_PER_ROW));
      // polyInputForm.setBackground(Color.BLUE);
      makePolyInputForm();
      
      // panel for creation and animation of diagram
      diagram = new Diagram();
      add(diagram, "cell 0 1 2 1, grow");
		
		// input for degree
		controls.add(new JLabel("Degree:", JLabel.LEFT), "split 2");
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
		controls.add(degreeField);
		
		submit = new JButton("Solve");
      submit.addActionListener(
            new ActionListener()
            {
               public void actionPerformed(ActionEvent e)
               {
                  double[] coeffs = new double[degree + 1];
                  
                  for (int i = degree; i >= 0; i--)
                  {
                     String a_i = ((JTextField) polyInputForm.getComponent(2 * (degree - i))).getText();
                     if (GenericValidator.isDouble(a_i))
                     {
                        coeffs[i] = Double.parseDouble(a_i);
                     }
                     
                     // TODO: report error if one is not a double, or if a_degree == 0
                  }
                  
                  diagram.solve(degree, coeffs);
               }
            });
		controls.add(submit, "cell 0 1, align 50%");

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
      
      add(polyInputForm, "cell 1 0, grow, wrap");   
      repaint();
	}
   
   private class Diagram extends JPanel
   {  
      // default rotation speed, deg/refresh
      private static final double DEFAULT_DTHETA = .01;
   
      // coefficients of polynomial
      private double[] coeffs;
      
      // animation rotation speed, deg/refresh
      private double dtheta;
      
      // timer to recalculate path for animation
      final private javax.swing.Timer sweeper;
   
      private int degree;
      private double scale;
      private double[] origin;
      private double angle0;
      private double angle;
      private double[] path;
      private boolean showPath;
      
      public Diagram()
      {
         dtheta = DEFAULT_DTHETA;
         origin = new double[2];
         
         sweeper = new Timer(10,
                     new ActionListener()
                     {
                        public void actionPerformed(ActionEvent e)
                        {
                           computePath();
                           repaint();
                           angle += dtheta;
                           
                           if (angle - angle0 > Math.PI)
                           {
                              sweeper.stop();
                              showPath = false;
                           }
                        }
                     });
      }
      
      public void solve(int degree, double[] coeffs)
      {
         this.degree = degree;
         this.coeffs = coeffs;
         path = new double[degree + 1];
         animate();
      }
      
      private void animate()
      {
         angle0 = - Math.signum(coeffs[degree]) * Math.PI/2 + .001;
         angle = angle0;
         
         showPath = true;
         SwingUtilities.invokeLater(
               new Runnable()
               {
                  public void run()
                  {
                     sweeper.start();
                  }
               });
               
         repaint();
      }
      
      private void computePath()
      {
         if (path == null)
         {
            return;
         }
         
         double ratio = Math.tan(angle);
         path[degree] = 0;
         
         for (int i = degree - 1; i >= 0; i--)
         {
            path[i] = (coeffs[i+1] - path[i+1]) * ratio;
         }
      }
      
      public void paintComponent(Graphics g)
      {
         super.paintComponent(g);
         setBackground(Color.WHITE);
         
         if (coeffs == null)
         {
            return;
         }
      
      
         double x_min = 0;
         double x_max = 0;
         double y_min = 0;
         double y_max = 0;
         
         double x = 0;
         double y = 0;
         int sgn = 1;
         
         for (int i = degree; i >= 0; i -= 2, sgn *= -1)
         {
            x += sgn * coeffs[i];
            
            if (x < x_min)
            {
               x_min = x;
            }
            else if (x > x_max)
            {
               x_max = x;
            }
            
            if (i >= 1)
            {
               y += sgn * coeffs[i-1];
               
               if (y < y_min)
               {
                  y_min = y;
               }
               else if (y > y_max)
               {
                  y_max = y;
               }
            }
         }
         
         origin[0] = -(x_min + x_max) / 2;
         origin[1] = -(y_min + y_max) / 2;
         
         if (x_min == x_max)
         {
            if (y_min == y_max)
            {
               scale = 1;
            }
            else
            {
               scale = getHeight() / (2 * (y_max - y_min));
            }
         }
         else if (y_min == y_max)
         {
            scale = getWidth() / (2 * (x_max - x_min));
         }
         else
         {
            scale = Math.min(getWidth() / (2 * (x_max - x_min)), getHeight() / (2 * (y_max - y_min)));
         }
         
         x = getWidth() / 2 + scale * origin[0];
         y = getHeight() / 2 - scale * origin[1];
         
         g.fillOval((int) x - 2, (int) y - 2, 4, 4);
         
         double x2, y2;
         double x_path = x;
         double y_path = y;
         sgn = 1;
         
         for (int i = degree; i >= 0; i -= 2, sgn *= -1)
         {
            x2 = x + sgn * coeffs[i] * scale;
            g.drawLine((int) x, (int) y, (int) x2, (int) y);
            if (showPath)
            {
               x_path = x + sgn * path[i] * scale;
               g.setColor(Color.RED)
               g.drawLine((int) x, (int) y_path, (int) x_path, (int) y);
            }
            x = x2;
            
            if (i >= 1)
            {
               y2 = y - sgn * coeffs[i-1] * scale;
               g.drawLine((int) x2, (int) y, (int) x2, (int) y2);
               if (showPath)
               {
                  y_path = y - sgn * path[i-1] * scale;
                  g.drawLine((int) x_path, (int) y, (int) x2, (int) y_path);
               }
               y = y2;
            }
         }
         
         g.fillOval((int) x - 2, (int) y - 2, 4, 4);
      }
   }
}