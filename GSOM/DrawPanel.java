import java.util.*;
import java.awt.*;
import javax.swing.*;

/*******************************************************************************
 *
 * class DrawPanel
 *
 *	Draws the u-matrix representing the SOM. A single hexagon is created
 *  according to specifications and copied, then translated to new positions.
 *
 *******************************************************************************/

class DrawPanel extends JPanel
{
	ArrayList<ArrayList<Neuron>> map;
	int
		maxBMU = -1,
		minBMU = 10000;
	double
		minDist = Double.MAX_VALUE,
		maxDist = Double.MIN_VALUE,
		minCos = Double.MAX_VALUE,
		maxCos = Double.MIN_VALUE;
	
	
	// Constructor. Calcs distance for all neurons.
	public DrawPanel(ArrayList<ArrayList<Neuron>> map)
	{
		this.map = map;
		int i = 0;
		for(ArrayList<Neuron> l : map)
		{
			int j = 0;
			for(Neuron n : l)
			{
				if(n == null)
				{
					j++;
					continue;
				}
				
				double cos1 = -2.0, cos2 = -2.0;
				if(n.BMU > maxBMU)	maxBMU = n.BMU;
				if(n.BMU < minBMU)	minBMU = n.BMU;
				
				if(i+1 < map.size())			cos1 = getCos(map.get(i).get(j), map.get(i+1).get(j));
				if(j+1 < map.get(0).size())		cos2 = getCos(map.get(i).get(j), map.get(i).get(j+1));
				
				if(cos1 != -2.0)
				{
					if(cos1 > maxCos)	maxCos = cos1;
					if(cos1 < minCos)	minCos = cos1;
				}
				if(cos2 != -2.0)
				{
					if(cos2 > maxCos)	maxCos = cos2;
					if(cos2 < minCos)	minCos = cos2;
				}
				j++;
			}
			i++;
		}
	}
	
	public void paintComponent(Graphics g)
	{
		Graphics2D  g2 = (Graphics2D)g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		super.paintComponent(g2);
		
		//double totalCos = maxCos - minCos;
		double totalBMU = (double)(maxBMU - minBMU);
		int x = 5, y = 5, offset = 14, width = 8;
		
		int i = 0;
		for(ArrayList<Neuron> l : map)
		{
			int j = 0,
				currY = i * offset + y;
			for(Neuron n : l)
			{
				if(n == null)
				{
					j++;
					continue;
				}
				int currX = j*offset + x;
				double cos1 = -2.0, cos2 = -2.0;
				
				if(i+1 < map.size())			cos1 = getCos(map.get(i).get(j), map.get(i+1).get(j));
				if(j+1 < map.get(0).size())		cos2 = getCos(map.get(i).get(j), map.get(i).get(j+1));
				
				if(cos1 != -2.0)
				{
				}
				if(cos2 != -2.0)
				{
				}
				
				//int c = (int) (255.0 * ((double)(n.BMU - minBMU) /totalBMU));
				
				double dist = ((double)(n.BMU - minBMU))/totalBMU;
				int c = (int)(dist * 255.0); // Get gray color
				
				g2.setColor(new Color(c,0,0));
				g2.fillRect(currX, currY, width, width);
				g2.setColor(Color.BLACK);
				g2.drawRect(currX, currY, width, width);
				
				j++;
			}
			i++;
		}
	}
	
	// Simple Euclidean distance.
	public double getDist(Neuron n1, Neuron n2)
	{
		double euclid = 0.0;
		for(int i = 0; i < n1.weights.length; i++)
		{
			euclid += (n1.weights[i] - n2.weights[i]) * (n1.weights[i] - n2.weights[i]);
		}
		euclid = Math.sqrt(euclid);
		minDist = (minDist > euclid) ? euclid : minDist;
		maxDist = (maxDist < euclid) ? euclid : maxDist;
		return euclid;
	}
	
	// Cos Similarity.
	public double getCos(Neuron n1, Neuron n2)
	{
		if(n1 == null || n2 == null)
			return -2.0;
		
		double similarity = 0.0, magnitudeW = 0.0, magnitudeI = 0.0;
		
		for(int i = 0; i < n1.weights.length; i++)
		{
			similarity += n1.weights[i] * n2.weights[i];
			magnitudeW += n1.weights[i] * n1.weights[i];
			magnitudeI += n2.weights[i] * n2.weights[i];
		}
		similarity /= (Math.sqrt(magnitudeI) * Math.sqrt(magnitudeW));
		minCos = (minCos > similarity) ? similarity : minCos;
		maxCos = (maxCos < similarity) ? similarity : maxCos;
		
		return similarity;
	}
}