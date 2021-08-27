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
	Neuron[][] map;
	double[][] uMat, cMat;
	int
		u,
		s = 8,
		t = s/2,
		r = (int) (s * 0.8660254037844),
		CLUSTERS;
	double
		minDist = Double.MAX_VALUE,
		maxDist = Double.MIN_VALUE,
		minCos = Double.MAX_VALUE,
		maxCos = Double.MIN_VALUE;
	Polygon original;
	
	
	// Constructor. Calcs distance for all neurons.
	public DrawPanel(Neuron[][] map, int c)
	{
		this.map = map;
		CLUSTERS = c;
		u = CLUSTERS*2-1; // A u-matrix expands the original map to 2n-1
		uMat = new double[u][u]; // It is a TRIP thinking of hex maps in rectangular boxes
		cMat = new double[u][u];
		
		for(int i = 0; i < CLUSTERS; i++)
		{
			for(int j = 0; j < CLUSTERS; j++)
			{
				calcDistance(i,j); // Calc distance for just one node
			}
		}
		int x = 10, y = 2;
		
		int[] xpoints = new int[] {x, x+s, x+s+t, x+s,   x,     x-t};
		int[] ypoints = new int[] {y, y,   y+r,   y+r+r, y+r+r, y+r};
		
		original = new Polygon(xpoints, ypoints, 6); // The polygon to be copied over and over. This simplifies stuff later.
	}
	
	public void paintComponent(Graphics g)
	{
		Graphics2D  g2 = (Graphics2D)g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		super.paintComponent(g2);
		double totalDist = maxDist - minDist;
		double totalCos = maxCos - minCos;
		
		for(int i = 0; i < u; i++)
		{
			boolean stepDown = false;
			for(int j = 0; j < u; j++)
			{
				int wOffset = j * (t + s);
				int hOffset = i * r*2;
				if(j % 2 == 0) // Either at step 0 or 1
				{
					if(stepDown)
					{
						hOffset += 2*r;
					}
					stepDown = !stepDown;
				}
				else // Half step down
				{
					hOffset += r;
				}
				
				// Euclid
				Polygon copy = new Polygon(original.xpoints, original.ypoints, original.npoints);
				copy.translate(wOffset, hOffset);
				
				double dist = (maxDist - uMat[i][j])/totalDist;
				int c = (int)(dist * 255.0); // Get gray color
				
				g2.setColor(new Color(c,c,c));
				g2.fillPolygon(copy);
				g2.setColor(Color.BLACK);
				g2.drawPolygon(copy);
				
				// Cos Similarity
				hOffset += (u+3) * 2*r;
				Polygon copy2 = new Polygon(original.xpoints, original.ypoints, original.npoints);
				copy2.translate(wOffset, hOffset);
				
				dist = (maxCos - cMat[i][j])/totalCos;
				c = 255 - (int)(dist * 255.0); // Get gray color
				
				g2.setColor(new Color(c,c,c));
				g2.fillPolygon(copy2);
				g2.setColor(Color.BLACK);
				g2.drawPolygon(copy2);
			}
		}
	}
	
	// This ugly code is necessary for hexagons
	// Basically, it fetches calculated distances if they already exist, or it calculates the distance
	// if it doesn't. Nodes to the right and below are always calculated. Nodes to the left and above
	// have been calculated already.
	public void calcDistance(int i, int j)
	{
		double mean = 0.0, cosMean = 0.0, dist = 0.0, cosDist = 0.0;
		int n = 0;
		
		if(j % 2 != 0) // Evens and odds are handled differently. Just a quirk of the map.
		{
			if(i < CLUSTERS-1) // Calculate below values
			{
				n++;
				dist = getDist(map[i][j], map[i+1][j]);
				uMat[i*2+1][j*2] = dist;
				mean += dist;
				cosDist = getCos(map[i][j], map[i+1][j]);
				cMat[i*2+1][j*2] = cosDist;
				cosMean += cosDist;
				
				if(j > 0) // Below left
				{
					n++;
					dist = getDist(map[i][j], map[i+1][j-1]);
					uMat[i*2+1][j*2-1] = dist;
					mean += dist;
					cosDist = getCos(map[i][j], map[i+1][j-1]);
					cMat[i*2+1][j*2-1] = cosDist;
					cosMean += cosDist;
				}
				if(j < CLUSTERS-1) // Below right
				{
					n++;
					dist = getDist(map[i][j], map[i+1][j+1]);
					uMat[i*2+1][j*2+1] = dist;
					mean += dist;
					cosDist = getCos(map[i][j], map[i+1][j+1]);
					cMat[i*2+1][j*2+1] = cosDist;
					cosMean += cosDist;
				}
			}
			if(j > 0) // Fetch left value
			{
				n++;
				mean += uMat[i*2][j*2-1];
				cosMean += cMat[i*2][j*2-1];
			}
			if(j < CLUSTERS-1) // Calculate right value
			{
				n++;
				dist = getDist(map[i][j], map[i][j+1]);
				uMat[i*2][j*2+1] = dist;
				mean += dist;
				cosDist = getCos(map[i][j], map[i][j+1]);
				cMat[i*2][j*2+1] = cosDist;
				cosMean += cosDist;
			}
			if(i > 0) // Fetch above value
			{
				n++;
				mean += uMat[i*2-1][j*2];
				cosMean += cMat[i*2-1][j*2];
			}
		}
		else // Even columns
		{
			if(i < CLUSTERS-1) // Calc below value
			{
				n++;
				dist = getDist(map[i][j], map[i+1][j]);
				uMat[i*2+1][j*2] = dist;
				mean += dist;
				cosDist = getCos(map[i][j], map[i+1][j]);
				cMat[i*2+1][j*2] = cosDist;
				cosMean += cosDist;
			}
			if(j > 0) // Fetch left value
			{
				n++;
				mean += uMat[i*2][j*2-1];
				cosMean += cMat[i*2][j*2-1];
			}
			if(j < CLUSTERS-1) // Calculate right value
			{
				n++;
				dist = getDist(map[i][j], map[i][j+1]);
				uMat[i*2][j*2+1] = dist;
				mean += dist;
				cosDist = getCos(map[i][j], map[i][j+1]);
				cMat[i*2][j*2+1] = cosDist;
				cosMean += cosDist;
			}
			if(i > 0) // Fetch above values
			{
				n++;
				mean += uMat[i*2-1][j*2];
				cosMean += cMat[i*2-1][j*2];
				
				if(j > 0) // Above left
				{
					n++;
					mean += uMat[i*2-1][j*2-1];
					cosMean += cMat[i*2-1][j*2-1];
				}
				if(j < CLUSTERS-1) // Above right
				{
					n++;
					mean += uMat[i*2-1][j*2+1];
					cosMean += cMat[i*2-1][j*2+1];
				}
			}
		}
		
		uMat[i*2][j*2] = mean/(double)n;
		cMat[i*2][j*2] = cosMean/(double)n;
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