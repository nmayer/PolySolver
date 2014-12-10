// for GUI components
import javax.swing.*;

// for event handling
import javax.swing.event.*;
import java.awt.event.*;

// for Graphics, Color
import java.awt.*;

// A panel which can create Lill diagrams and animate
// the finding of roots via Lill's method
public class Diagram extends JPanel
{  
   // default rotation speed, deg/refresh
   private static final double DEFAULT_DTHETA = .001;

   // coefficients of polynomial
   private double[] coeffs;
   
   // animation rotation speed, deg/refresh
   private double dtheta;
   
   // timer to recalculate path for animation
   private javax.swing.Timer sweeper;
   
   // timer used to restart animation after pause
   // when a root is found
   private javax.swing.Timer resume;

   // degree of polynomial currently depicted
   private int degree;
   
   // pixels per unit coefficient
   private double scale;
   
   // coordinates of the start point, to center the diagram
   // (in coefficient units, xy plane with positive y up)
   private double[] origin;
   
   // starting angle for Lill path animation
   private double angle0;
   
   // current angle of Lill path
   private double angle;
   
   // locations of path corners, measured in pixels along
   // the relevant Lill diagram axes
   private double[] path;
   
   // flag to display or hide Lill path
   private boolean showPath;
   
   // sign (+/- 1.0) indicating whether the current path
   // represents a negative or positive value of the polynomial
   // change in sign indicates a found root
   private double errDir;
   
   // flag to pause animation and pass root up to the PolySolverTopPanel
   private boolean isRoot;
   
   // Constructs a new Diagram, initializing necessary elements
   public Diagram()
   {
      // user input to control dtheta could be implemented
      dtheta = DEFAULT_DTHETA;
      origin = new double[2];
      
      // Timer to refresh during animation
      sweeper = new Timer(2,
                  new ActionListener()
                  {
                     // called whenever the timer goes off,
                     // every 2 ms after it's started
                     public void actionPerformed(ActionEvent e)
                     {
                        // draw new path
                        computePath();
                        repaint();
                        
                        // pause at roots and save the path
                        if (isRoot)
                        {
                           sweeper.stop();
                           resume.start();
                           ((PolySolverTopPanel) getParent()).foundRoot(angle);
                        }
                        
                        // increment angle
                        angle += dtheta;
                        
                        // check if done
                        if (angle - angle0 > Math.PI)
                        {
                           sweeper.stop();
                           showPath = false;
                        }
                     }
                  });
      
      // Timer to restart animation after a pause
      resume = new Timer(1000,
                  new ActionListener()
                  {
                     public void actionPerformed(ActionEvent e)
                     {
                        SwingUtilities.invokeLater(
                           new Runnable()
                           {
                              public void run()
                              {
                                 sweeper.start();
                              }
                           });
                     }
                  });
      resume.setRepeats(false);
   }
   
   // configures instance variables based on input polynomial,
   // initiates animation
   public void solve(int degree, double[] coeffs)
   {
      this.degree = degree;
      this.coeffs = coeffs;
      
      // based on asymptotics and starting angles of animation
      errDir = Math.signum(coeffs[degree]);
      
      path = new double[degree + 1];
            
      // start just past the vertical
      angle0 = - Math.signum(coeffs[degree]) * Math.PI/2 + .00001;
      angle = angle0;
      
      showPath = true;
      
      // For threading reasons, just calling sweeper.start doesn't work
      SwingUtilities.invokeLater(
            new Runnable()
            {
               public void run()
               {
                  sweeper.start();
               }
            });
   }
   
   // Computes the data needed to draw the path based on angle
   // (i.e. fills the array path).
   private void computePath()
   {
      // Mistaken method call
      if (path == null)
      {
         return;
      }
      
      double ratio = Math.tan(angle);
      path[degree] = 0;
      
      for (int i = degree - 1; i >= 0; i--)
      {
         // compute base of similar triangle, scale for height                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      
         path[i] = (coeffs[i+1] - path[i+1]) * ratio;
      }
      
      double newErrDir = Math.signum(coeffs[0] - path[0]);
      if (newErrDir == 0 || newErrDir * errDir < 0)
      {
         isRoot = true;
      }
      else
      {
         isRoot = false;
      }
      errDir = newErrDir;
   }
   
   // Jump straight to the path defined by the given angle.
   // Called when the found root buttons are clicked.
   public void showPath(double angle)
   {  
      this.angle = angle;
      computePath();
      showPath = true;
      repaint();
   }
   
   // Paints the diagram, line by line.
   public void paintComponent(Graphics g)
   {
      super.paintComponent(g);
      setBackground(Color.WHITE);
      
      // no polynomial to draw
      if (coeffs == null)
      {
         return;
      }
   
      // to keep track of bounds, for computing scale
      double x_min = 0;
      double x_max = 0;
      double y_min = 0;
      double y_max = 0;
      
      // general coordinate vars
      double x = 0;
      double y = 0;
      // to keep track of switching "positive" direction
      // in Lill diagram
      int sgn = 1;
      
      // compute bounds of diagram, in coefficient units
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
      
      // compute start point, based on bounds
      origin[0] = -(x_min + x_max) / 2;
      origin[1] = -(y_min + y_max) / 2;
      
      // compute scale factor, pixels/unit coefficient
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
         // fill at most half the length or width
         scale = Math.min(getWidth() / (2 * (x_max - x_min)),
                          getHeight() / (2 * (y_max - y_min)));
      }
      
      // convert starting point into pixel coordinates
      x = getWidth() / 2 + scale * origin[0];
      y = getHeight() / 2 - scale * origin[1];
      
      // draw dot at start
      g.fillOval((int) x - 2, (int) y - 2, 4, 4);
      
      // auxillary variables for drawing lines
      double x2, y2;
      double x_path = x;
      double y_path = y;
      sgn = 1;
      
      for (int i = degree; i >= 0; i -= 2, sgn *= -1)
      {
         // draw the next horizontal line
         x2 = x + sgn * coeffs[i] * scale;
         g.drawLine((int) x, (int) y, (int) x2, (int) y);
         
         // draw next path segment (ends on that horizontal)
         if (showPath)
         {
            x_path = x + sgn * path[i] * scale;
            g.setColor(Color.RED);
            g.drawLine((int) x, (int) y_path, (int) x_path, (int) y);
            g.setColor(Color.BLACK);
         }
         x = x2;
         
         if (i >= 1)
         {
            // draw next vertical line
            y2 = y - sgn * coeffs[i-1] * scale;
            g.drawLine((int) x2, (int) y, (int) x2, (int) y2);
            
            // draw next path segment (ends on that vertical)
            if (showPath)
            {
               y_path = y - sgn * path[i-1] * scale;
               g.setColor(Color.RED);
               g.drawLine((int) x_path, (int) y, (int) x2, (int) y_path);
               g.setColor(Color.BLACK);
            }
            y = y2;
         }
      }
      
      // draw end point
      g.fillOval((int) x - 2, (int) y - 2, 4, 4);
      
      // Create string representation of polynomial
      String poly = "";
      for (int i = degree; i >= 0; i--)
      {
         while (coeffs[i] == 0 && i > 0)
         {
            i--;
         }
         
         if (coeffs[i] >= 0 && i != degree)
         {
            poly += "+";
         }
         
         poly += coeffs[i] + " ";
         
         if (i > 0)
         {
             poly += "x";
         }
         
         if (i > 1)
         {
            poly += "^" + i;
         }
         
         poly += " ";
      }
      
      // display polynomial in the corner
      g.drawString(poly, 10, 20);
   }
}
