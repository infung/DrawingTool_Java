import javax.swing.*;
import javax.swing.event.*;
import java.awt.Dimension;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.*;
import java.awt.*;
import java.awt.geom.*;
import javax.vecmath.*;
import java.text.NumberFormat;
public class Draw {
	public static void main(String[] args) {
		JFrame frame = new JFrame("Draw");
		JScrollPane scrollPane = new JScrollPane();
		Container p = frame.getContentPane();;
		// create drawingmodel and three views
		DrawingModel model = new DrawingModel();
		ToolbarView vTool = new ToolbarView(model);
		CanvasView vCanvas = new CanvasView(model);
		StatusbarView vStatus = new StatusbarView(model);
		vCanvas.setLayout(new SpringLayout());
		vCanvas.setPreferredSize(new Dimension(885, 490));
		p.setLayout(new BorderLayout());
		//create a layout panel to hold the three views
		scrollPane.setPreferredSize(new Dimension(900, 500));
		scrollPane.setViewportView(vCanvas);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		//add views
		p.add(vTool, BorderLayout.NORTH);;
		p.add(scrollPane);
		p.add(vStatus, BorderLayout.SOUTH);
		//create the window
		frame.setPreferredSize(new Dimension(900, 600));
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
interface View {
	public void updateView();
}
class DrawingModel {
	private ArrayList<View> views = new ArrayList<View>();
	private ArrayList<Shape> strokes = new ArrayList<Shape>();
	private Shape selectedStroke = null;
	private double scale = 1.0;
	private int rotate = 0;
	private int stroke = 0;
	private int points = 0;
	private Boolean selected = false;
	private Boolean scaleChange = false;
	private Boolean rotateChange = false;
	private Boolean moving = false;
	float strokeThickness = 2.0f; // 2.0f is dedault thickness = thin line
	public DrawingModel() {}
	public void addView(View view) {
		views.add(view);
		view.updateView();
	}
	public void setThickness(float thickness) {
		strokeThickness = thickness;
		notifyObservers();
	}
	public float getThickness() {return strokeThickness;}
	public void clearScreen() {
			strokes.clear();
			stroke = 0;
			notifyObservers();
	}
	public void addStroke(Shape strk) {
		strokes.add(strk);
		IncrementStroke();
	}
	public double getScale() {return scale;}
	public void setScale(double s) {
		scale = s;
		scaleChange = true;
		notifyObservers();
	}
	public int getRotate() {return rotate;}
	public void setRotate(int r) {
		rotate = r;
		rotateChange = true;
		notifyObservers();
	}
	public int getStroke() {return stroke;}
	public void IncrementStroke() {
		stroke++;
		notifyObservers();
	}
	public int getPoints() {return points;}
	public void setPoints(int p) {
		points = p;
		notifyObservers();
	}
	public Boolean getIsSelected() {return selected;}
	public void disSelect() {
		selectedStroke = null;
		selected = false;
		scaleChange = false;
		rotateChange = false;
		notifyObservers();
	}
	public Shape getSelectedShape() {return selectedStroke;}
	
	public void selectShape(Shape s) {
		selectedStroke = s;
		selected = true;
		notifyObservers();
	}
	public Boolean isMoving() {return moving;}
	public void setMoving() {
		moving = true;
		notifyObservers();
		moving = false;
	}
	public Boolean IsScaleChange() {return scaleChange;}
	public Boolean IsRotateChange() {return rotateChange;} 
	public void deleteSeletedShape() {
		if(selected) {
			strokes.remove(selectedStroke);
			selected = false;
			DecreaseStroke();
		}
	}
	public void DecreaseStroke() {
		stroke--;
		notifyObservers();
	}
	public ArrayList<Shape> retriveList() {return strokes;}
	private void notifyObservers() {
		for(View view : this.views) {
			view.updateView();
		}
	}
}
class ToolbarView extends JPanel {
	private DrawingModel model;
	private JButton delete = new JButton("Delete");
	private JSlider scaleSlider = new JSlider(50, 200);
	private JSlider rotateSlider = new JSlider(-180, 180);
	private JLabel scale = new JLabel("1.0");
	private JLabel rotate = new JLabel("0");
	private JLabel scaleLabel = new JLabel("Scale");
	private JLabel rotateLabel = new JLabel("Rotate");
	private JButton clear = new JButton("Clear");
	private JRadioButton thin = new JRadioButton("Thin Line");
    private JRadioButton thick = new JRadioButton("Thick Line");
	private NumberFormat formatter = NumberFormat.getNumberInstance();
	//private NumberFormat formatter2 = new DecimalFormat("#0.0");
	private double current_scale; // the value of current scale, changed when change event called
	public ToolbarView(DrawingModel m) {
		super();
		this.model = m;
		this.layoutView();
		this.registerControllers();
		this.model.addView(new View() {
				public void updateView() {
					if(!model.getIsSelected()) {
						delete.setForeground(Color.GRAY);
						scaleSlider.setForeground(Color.GRAY);
						rotateSlider.setForeground(Color.GRAY);
						scale.setForeground(Color.GRAY);
						rotate.setForeground(Color.GRAY);
						scaleLabel.setForeground(Color.GRAY);
						rotateLabel.setForeground(Color.GRAY);
						clear.setForeground(Color.RED);
						thin.setForeground(Color.GRAY);
						thick.setForeground(Color.GRAY);
					} else {
						delete.setForeground(Color.BLACK);
						scaleSlider.setForeground(Color.BLACK);
						rotateSlider.setForeground(Color.BLACK);
						scale.setForeground(Color.BLACK);
						rotate.setForeground(Color.BLACK);
						scaleLabel.setForeground(Color.BLACK);
						rotateLabel.setForeground(Color.BLACK);
						clear.setForeground(Color.RED);
						thin.setForeground(Color.BLACK);
						thick.setForeground(Color.BLACK);
					}
					current_scale = model.getScale() * 100;
					scaleSlider.setValue((int)current_scale);
					rotateSlider.setValue((int)model.getRotate());
					scale.setText(String.format("%.1f",model.getScale()));
					rotate.setText(formatter.format(model.getRotate()));
				}
		});
		this.scaleSlider.setValue(100);
	}
	private void layoutView() {
		this.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		this.add(this.delete);
		this.add(this.clear);
		ButtonGroup thicknessOption = new ButtonGroup();
        thicknessOption.add(thin);
        thicknessOption.add(thick);
        this.add(thin);
        this.add(thick);
		this.add(scaleLabel);
		this.add(this.scaleSlider);
		this.add(this.scale);
		this.add(rotateLabel);
		this.add(this.rotateSlider);
		this.add(this.rotate);
	}
	private void registerControllers() {
		this.scaleSlider.addChangeListener(new ScaleController());
		this.rotateSlider.addChangeListener(new RotateController());
		this.delete.addActionListener(new DeleteController());
		this.clear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				model.clearScreen();
			}
		});
		this.thin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.setThickness(2.0f);
			}
		});
		this.thick.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.setThickness(5.0f);
			}
		});
	}
	private class ScaleController implements ChangeListener {
		public void stateChanged(ChangeEvent e) {
			current_scale = scaleSlider.getValue();
			double s = (double)scaleSlider.getValue() / 100;
			model.setScale(s);
		}
	}
	private class RotateController implements ChangeListener {
		public void stateChanged(ChangeEvent e) {
			int r = rotateSlider.getValue();
			model.setRotate(r);
		}
	}
	private class DeleteController implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			model.deleteSeletedShape();
		}
	}
}
class StatusbarView extends JPanel {
	private DrawingModel model;
	private JLabel stroke = new JLabel("0");
	private JLabel points = new JLabel("0");
	private JLabel scale = new JLabel("1.0");
	private JLabel rotate = new JLabel("0");
	private JPanel info = new JPanel();
	private static final NumberFormat formatter = NumberFormat.getNumberInstance();
	public StatusbarView(DrawingModel m) {
		super();
		this.model = m;
		this.layoutView();
		this.model.addView(new View() {
			public void updateView() {
				stroke.setText(formatter.format(model.getStroke()));
				points.setText(formatter.format(model.getPoints()));
				scale.setText(formatter.format(model.getScale()));
				rotate.setText(formatter.format(model.getRotate()));
				if(model.getIsSelected()) {info.setVisible(true);}
				else {info.setVisible(false);}
			}
		});
	}
	private void layoutView() {
		this.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		this.add(this.stroke);
		this.add(new JLabel(" Strokes"));
		info.setLayout(new FlowLayout(FlowLayout.LEFT));	
		this.add(info);
		info.add(new JLabel(" Selection ("));
		info.add(this.points);
		info.add(new JLabel(" points, scale: "));
		info.add(this.scale);
		info.add(new JLabel(", rotation "));
		info.add(this.rotate);
		info.add(new JLabel(")"));
		info.setVisible(false);
	}
}
class CanvasView extends JPanel {
	private DrawingModel model;
	private Shape shape;
	private ArrayList<Shape> shapes = new ArrayList<Shape>();
	Graphics2D g2;
	private double oldX, oldY;
	public CanvasView(DrawingModel m) {
		super();
		this.model = m;
		this.layoutView();
		this.registerControllers();
		this.model.addView(new View() {
			public void updateView() {
				repaint();
			}
		});
		this.shape = new Shape();
	}
	private void layoutView() {
		this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		this.setMinimumSize(new Dimension(800, 500));
	}
	private void registerControllers() {
		MouseInputListener mil = new MController();
		this.addMouseListener(mil);
		this.addMouseMotionListener(mil);
	}
	private Boolean dragging = false;
	private Boolean moving = false;
	private class MController extends MouseInputAdapter {
		public void mouseClicked(MouseEvent e) {
			shapes = model.retriveList();
			ListIterator<Shape> iter = shapes.listIterator(shapes.size());
			while(iter.hasPrevious()) {
				Shape prev = iter.previous();	
				if(prev.hittest(e.getX(), e.getY())) {
					model.selectShape(prev);
					break;
				} else {
					model.disSelect();
				}
			}
		}
		public void mousePressed(MouseEvent e) {
			if(model.getIsSelected()) {
				if(!(model.getSelectedShape().hittest(e.getX(), e.getY()))) {
				model.disSelect();
				}
			} else {
				oldX = e.getX();
				oldY = e.getY();
			}
		}
		public void mouseReleased(MouseEvent e) {
			if(dragging) {
				model.addStroke(shape);
				shape = new Shape();
				//shape.setIsClosed(true);
                //shape.setIsFilled(true);
				dragging = false;
			} else if (moving) {
				model.getSelectedShape().pointsChanged = false; 
				moving = false;}
		}
		public void mouseDragged(MouseEvent e) {
			if(!model.getIsSelected()) {
				dragging = true;
				shape.addPoint(e.getX(), e.getY());
				model.setPoints(shape.npoints());
			} else {
				double xMove = e.getX() - oldX;
				double yMove = e.getY() - oldY;
				model.getSelectedShape().setTranslate(xMove, yMove);
				moving = true;
				oldX = e.getX();
				oldY = e.getY();
				model.setMoving();
			} 
		}
	}
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g2 = (Graphics2D) g; // cast to get 2D drawing methods
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  // antialiasing look nicer
                            RenderingHints.VALUE_ANTIALIAS_ON);
        shapes = model.retriveList();
        shape.setStrokeThickness(model.getThickness());
        if(shapes.size() != 0) {
        if(model.getIsSelected()) {
        	Shape str = model.getSelectedShape();
        	if(!model.IsScaleChange() && !model.IsRotateChange()) {
        		model.setScale(str.scale_change);
        		model.setRotate(str.rotate_change);
        	} else {if(model.IsScaleChange()) str.setSaleChange(model.getScale());
        		  if(model.IsRotateChange()) str.setRotateChange(model.getRotate());} 
        	str.setColour(Color.YELLOW);
        	float thicknessPrev = str.getStrokeThickness();
        	str.setStrokeThickness(5.0f + thicknessPrev - 2.0f);
        	str.draw(g2);
        	str.setStrokeThickness(thicknessPrev);
        }
        for(Shape s: shapes) {
        	s.setColour(Color.RED);
        	s.draw(g2);
        }
    	}
		if(shape != null) {shape.draw(g2);}
	}
}
class Shape {
    // shape points
    ArrayList<Point2d> points;
    public void clearPoints() {
        points = new ArrayList<Point2d>();
        pointsChanged = true;
    }
  
    // add a point to end of shape
    public void addPoint(Point2d p) {
        if (points == null) clearPoints();
        points.add(p);
        pointsChanged = true;
    }    
    // add a point to end of shape
    public void addPoint(double x, double y) {
        addPoint(new Point2d(x, y));  
    }
    public int npoints() {
        return points.size();
    }
    // shape is polyline or polygon
    Boolean isClosed = false; 
    public Boolean getIsClosed() {
        return isClosed;
    }
    public void setIsClosed(Boolean isClosed) {
        this.isClosed = isClosed;
    }    
    // if polygon is filled or not
    Boolean isFilled = false; 
    public Boolean getIsFilled() {
        return isFilled;
    }
    public void setIsFilled(Boolean isFilled) {
        this.isFilled = isFilled;
    }    
    // drawing attributes
    Color colour = Color.BLACK;
    float strokeThickness = 2.0f;
    public Color getColour() {
		return colour;
	}
	public void setColour(Color colour) {
		this.colour = colour;
	}
    public float getStrokeThickness() {
		return strokeThickness;
	}
	public void setStrokeThickness(float strokeThickness) {
		this.strokeThickness = strokeThickness;
	}
    // shape's transform
    // quick hack, get and set would be better
    float scale = 1.0f;
    // some optimization to cache points for drawing
    public Boolean pointsChanged = false; // dirty bit
    int[] xpoints, ypoints;
    int npoints = 0;
    void cachePointsArray() {
        xpoints = new int[points.size()];
        ypoints = new int[points.size()];
        for (int i=0; i < points.size(); i++) {
            xpoints[i] = (int)points.get(i).x;
            ypoints[i] = (int)points.get(i).y;
        }
        npoints = points.size();
        pointsChanged = false;
    }
	
	public Point2D.Double center;
    // get the centre of a polylines which contained in a abstract rectangle
    public Point2D.Double centrePoint() {
    	double maxX = points.get(0).x;
    	double minX = points.get(0).x;
    	double maxY = points.get(0).y;
    	double minY = points.get(0).y;
    	for(Point2d p: points) {
    		if(p.x < minX) minX = p.x;
    		if(p.x > maxX) maxX = p.x;
    		if(p.y < minY) minY = p.y;
    		if(p.y > maxY) maxY = p.y;
    	}
    	double x = (maxX + minX)/2;
    	double y = (maxY + minY)/2;
    	Point2D.Double ans = new Point2D.Double(x, y);
    	return ans;
    }
    public double scale_change = 1.0;
    public int rotate_change = 0;
    public void setSaleChange(double s) {scale_change = s;}
    public void setRotateChange(int r) {rotate_change = r;}
    public void setTranslate(double _x, double _y) {
    	for(Point2d p : points) {
    		p.x += _x;
    		p.y += _y;
    	}
    	pointsChanged = true;
    }
    //private AffineTransform myTransf;
    // let the shape draw itself
    // (note this isn't good separation of shape View from shape Model)
    public void draw(Graphics2D g2) {
        // don't draw if points are empty (not shape)
        if (points == null) return;
        // see if we need to update the cache
        if (pointsChanged) {
        	cachePointsArray();
        }
        center = centrePoint();
        AffineTransform save = g2.getTransform();
        if(scale_change == 1.0 && rotate_change == 0) {
        	// multiply in this shape's transform
       		// (uniform scale)
        	g2.scale(scale, scale);
        } else {
        	g2.translate(center.x, center.y);
        	g2.rotate(Math.toRadians(rotate_change));
        	g2.scale(scale_change, scale_change);
        	g2.translate((center.x)*(-1), (center.y)*(-1));
        }
        // call drawing functions
        g2.setColor(colour);            
        if (isFilled) {
            g2.fillPolygon(xpoints, ypoints, npoints);
        } else {
            // can adjust stroke size using scale
            scale = (float)scale_change;
        	g2.setStroke(new BasicStroke(strokeThickness / scale)); 
        	if (isClosed)
                g2.drawPolygon(xpoints, ypoints, npoints);
            else
                g2.drawPolyline(xpoints, ypoints, npoints);
        }
        //g2.transform(myTransf);
        // reset the transform to what it was before we drew the shape
        g2.setTransform(save);     
    }
    
   
    // let shape handle its own hit testing
    // (x,y) is the point to test against
    // (x,y) needs to be in same coordinate frame as shape, you could add
    // a panel-to-shape transform as an extra parameter to this function
    // (note this isn't good separation of shape Controller from shape Model)    
    public boolean hittest(double x, double y) {
    	if (points != null) {
    		Point2D dst = new Point2D.Double(x, y);
    		Point2D.Double src = new Point2D.Double(x, y);
    		AffineTransform m = new AffineTransform();
    		m.translate(center.x, center.y);
        	m.rotate(Math.toRadians(rotate_change));
        	m.scale(scale_change, scale_change);
        	m.translate((center.x)*(-1), (center.y)*(-1));
       		AffineTransform mi = new AffineTransform();
        	try {
        		mi = m.createInverse();	
    		}catch(Exception e) {
    			System.out.print("\"ERROR\"");
    		}
    		mi.transform(src, dst);
    		for(int i = 0; i < points.size() - 1; i++) {
            	double d = Line2D.ptSegDist(points.get(i).x, points.get(i).y,points.get(i+1).x, points.get(i+1).y, dst.getX(), dst.getY());
            	if(d <= 5.0) {return true;}
            }
       }
    	return false;
    }
}
