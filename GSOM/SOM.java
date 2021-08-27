import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.io.*;

public class SOM
{
	static int			TEST_DOCUMENTS = 3938, //3911,
						TRAIN_DOCUMENTS = 15752, //15598,
						INPUTS = 5000,
						MAX_ITERATIONS = 20,
						BATCH = 10000,
						nodes = 4;
	
	double	SIGMA0 = 1.0, //(double)CLUSTERS / 2.0,
			LAMBDA = (double)MAX_ITERATIONS / Math.log(SIGMA0),
			LAMBDA2 = (double)MAX_ITERATIONS / Math.log(4.0),
			LRATE0 = 0.1,
			GT = -5000 * Math.log(.55);
	
	Random r = new Random();
	static int[][] input = new int[TRAIN_DOCUMENTS][INPUTS+1];
	static ArrayList<ArrayList<Neuron>> neurons;
	
	static int[] inputMap;
	
	static String[] dictionary;
	static final String directory = "Files//Lemma2//";
	
	public static void main(String[] args)
	{
		SOM som = new SOM();
		System.out.println("Training.");
		som.loadTrainSet2();
		System.out.println("Reading tests.");
		som.loadTestSet();
		som.drawMap();
		
		dictionary = new String[INPUTS];
		som.readDictionary();
		double totalError = 0.0;
		
		for(int i = 0; i < neurons.size(); i++)
		{
			for(int j = 0; j < neurons.get(0).size(); j++)
			{
				Neuron t = neurons.get(i).get(j);
				if(t == null)	continue;
				
				double error = (t.BMU > 0) ? t.error2/(double)t.BMU : 0;
				totalError += error;
				
				System.out.print(i + " " + j + " " + t.BMU + "\t" + error + "\t");
				int[] indexes = t.getHighestFive();
				for(int k = 0; k < indexes.length; k++)
					System.out.print(dictionary[indexes[k]] + " ");
				System.out.print("\n");
				t.printResults(i,j);
			}
		}
		System.out.println(totalError + " " + totalError/(double)nodes);
	}
	
	public SOM()
	{
		// Initialize neurons/weights
		
		neurons = new ArrayList<>();
		
		for(int i = 0; i < 2; i++) // Create 2x2 layer
		{
			ArrayList<Neuron> temp = new ArrayList<>();
			temp.add(new Neuron(INPUTS));
			temp.add(new Neuron(INPUTS));
			neurons.add(temp);
		}
	}
	
	public void readDictionary()
	{
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(directory + "Dictionary.txt"), 1024 * 10000);
			String line = br.readLine();
			int count = 0;
			
			while(line != null)
			{
				dictionary[count] = line;
				count++;
				line = br.readLine();
			}
		}
		catch(Exception e) { e.printStackTrace(); }
	}
	/*
	public void loadTrainSet()
	{
		int
			iterations = 1,
			currentIndex = 0;
		
		while(iterations <= MAX_ITERATIONS)
		{
			System.out.println(iterations);
			try
			{
				BufferedReader br = new BufferedReader(new FileReader(directory + "training.txt"), 1024 * 10000);
				String line = br.readLine();
				
				while(line != null)
				{
					if(currentIndex == BATCH)
					{
						train(currentIndex, iterations);
						currentIndex = 0;
					}
					
					String[] temp = line.split("\t");
					for(int i = 0; i < temp.length; i++)
					{
						input[currentIndex][i] = Integer.parseInt(temp[i]);
					}
					currentIndex++;
					line = br.readLine();
				}
				train(currentIndex, iterations);
				currentIndex = 0;
			}
			catch(Exception e) { e.printStackTrace(); break; }
		
			iterations++;
		}
	}*/
	
	public void loadTrainSet2()
	{
		int
			iterations = 1,
			currentIndex = 0;
		
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(directory + "training.txt"), 1024 * 10000);
			String line = br.readLine();
			
			while(line != null)
			{
				String[] temp = line.split("\t");
				for(int i = 0; i < temp.length; i++)
				{
					input[currentIndex][i] = Integer.parseInt(temp[i]);
				}
				currentIndex++;
				line = br.readLine();
			}
			while(nodes < 64 && iterations <= MAX_ITERATIONS)
			{
				grow(currentIndex, iterations);
				System.out.println("Nodes: " + nodes);
				iterations++;
			}
			iterations = 1;
			while(iterations <= MAX_ITERATIONS)
			{
				System.out.println("Iteration: " + iterations);
				//if(iterations < 4)	grow(currentIndex, iterations);
				//else				
				smooth(currentIndex, iterations);
				iterations++;
			}
		}
		catch(Exception e) { e.printStackTrace(); }
	}
	
	public void grow(int maxIndex, int iterations)
	{
		for(int k = 0; k < maxIndex*0.8; k++)
		{
			double best = Double.MAX_VALUE;
			int BMUi = 0, BMUj = 0;
			int d = r.nextInt(maxIndex);
			
			// Go through every neuron, find the BMU -- weights will be most similar to input vector
			
			for(int i = 0; i < neurons.size(); i++)
			{
				double dist = 0;
				for(int j = 0; j < neurons.get(0).size(); j++)
				{
					Neuron n = neurons.get(i).get(j);
					if(n == null)	continue;
					
					dist = n.getCosSimilarity(input[d]);
					
					if(dist < best)
					{
						best = dist;
						BMUi = i;
						BMUj = j;
					}
				}
			}
			
			// Calculate nearest neighbors of BMU
			
			double radius = 4.0; //SIGMA0 * Math.exp((double)-iterations/LAMBDA);
			int r = (int) radius,
				end1 = (r+BMUi < neurons.size()) ? r+BMUi : neurons.size()-1,
				end2 = (r+BMUj < neurons.get(0).size()) ? r+BMUj : neurons.get(0).size()-1;
			
			for(int i = (r-BMUi >= 0) ? r-BMUi : 0; i < end1; i++)
			{
				for(int j = (r-BMUj >= 0) ? r-BMUj : 0; j < end2; j++)
				{
					Neuron n = neurons.get(i).get(j);
					if(n == null)	continue;
					double dist = inRange(radius, BMUi, BMUj, i, j); // HERE
					if(dist >= 0.0)
					{
						// The neighbors (neurons in radius) are adjusted to be more like the input vector
						double learnR = LRATE0 * (1.0 - 3.8/nodes) * Math.exp((double)-iterations/LAMBDA2); // Same function as radius
						double theta = Math.exp(-dist*dist/2.0*radius*radius);
						n.changeWeights(learnR, input[d], theta);
					}
				}
			}
			
			Neuron BMU = neurons.get(BMUi).get(BMUj);
			BMU.error += best;
			
			if(BMU.error > GT)
			{
				BMU.error = GT/2.0;
				expand(BMUi, BMUj);
			}
		}
	}
	
	public void smooth(int maxIndex, int iterations)
	{
		for(int k = 0; k < maxIndex*0.8; k++)
		{
			double best = Double.MAX_VALUE;
			int BMUi = 0, BMUj = 0;
			int d = r.nextInt(maxIndex);
			
			// Go through every neuron, find the BMU -- weights will be most similar to input vector
			
			for(int i = 0; i < neurons.size(); i++)
			{
				double dist = 0;
				for(int j = 0; j < neurons.get(0).size(); j++)
				{
					Neuron n = neurons.get(i).get(j);
					if(n == null)	continue;
					
					dist = n.getCosSimilarity(input[d]);
					
					if(dist < best)
					{
						best = dist;
						BMUi = i;
						BMUj = j;
					}
				}
			}
			
			double radius = 1.0; //SIGMA0 * Math.exp((double)-iterations/LAMBDA);
			int r = 1,
				end1 = (r+BMUi < neurons.size()) ? r+BMUi : neurons.size()-1,
				end2 = (r+BMUj < neurons.get(0).size()) ? r+BMUj : neurons.get(0).size()-1;
			
			for(int i = (r-BMUi >= 0) ? r-BMUi : 0; i < end1; i++)
			{
				for(int j = (r-BMUj >= 0) ? r-BMUj : 0; j < end2; j++)
				{
					Neuron n = neurons.get(i).get(j);
					if(n == null)	continue;
					
					double dist = 1.0; //inRange(radius, BMUi, BMUj, i, j);
					if(dist >= 0.0)
					{
						// The neighbors (neurons in radius) are adjusted to be more like the input vector
						double learnR = LRATE0 * Math.exp((double)-iterations/LAMBDA); // Same function as radius
						double theta = Math.exp(-dist*dist/2.0*radius*radius);
						n.changeWeights(learnR, input[d], theta);
					}
				}
			}
		}
	}
	
	public void loadTestSet()
	{
		int currentIndex = 0;
		
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(directory + "testing.txt"), 1024 * 10000);
			String line = br.readLine();
			
			while(line != null)
			{
				//if(currentIndex == BATCH)
				//{
				//	test(currentIndex);
				//	currentIndex = 0;
				//}
				
				String[] temp = line.split("\t");
				for(int i = 0; i < temp.length; i++)
				{
					input[currentIndex][i] = Integer.parseInt(temp[i]);
				}
				currentIndex++;
				line = br.readLine();
			}
			test(currentIndex);
		}
		catch(Exception e) { e.printStackTrace(); }
	}

	public void test(int maxIndex)
	{
	
		try
		{
			BufferedReader
				br1 = new BufferedReader(new FileReader(directory + "testingCats.txt")),
				br2 = new BufferedReader(new FileReader(directory + "testingTitles.txt"));
			
			for(int d = 0; d < maxIndex; d++)
			{
				int BMUi = 0, BMUj = 0;
				double best = Double.MAX_VALUE;
				
				for(int i = 0; i < neurons.size(); i++)
				{
					for(int j = 0; j < neurons.get(0).size(); j++)
					{
						Neuron temp = neurons.get(i).get(j);
						if(temp == null)	continue;
						double dist = temp.getCosSimilarity(input[d]);
						
						if(dist < best)
						{
							best = dist;
							BMUi = i;
							BMUj = j;
						}
					}
				}
				Neuron n = neurons.get(BMUi).get(BMUj);
				n.BMU++;
				n.error2 += best;
				
				n.cats.add(br1.readLine());
				n.titles.add(br2.readLine());
			}
		}
		catch(Exception e) { e.printStackTrace(); }
	}
	
	
	public double inRange(double radius, int BMUi, int BMUj, int i, int j)
	{
		double	magnitude = Math.sqrt((double)((BMUi - i)*(BMUi - i)) + (double)((BMUj - j)*(BMUj - j)));
		
		return (radius >= magnitude) ? magnitude : -1.0;
	}
	
	public void drawMap()
	{
		JFrame f = new JFrame("SOM");
		DrawPanel p = new DrawPanel(neurons);
		f.setSize(1000,1000);
		f.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		Container content = f.getContentPane();
		content.add(p);
		f.setVisible(true);
	}
	
	public void expand(int BMUi, int BMUj)
	{
		int	x = BMUj,
			y = BMUi;
			
		// Expand grid if necessary
		if(BMUj == 0)
		{
			for(ArrayList<Neuron> list : neurons)
			{
				list.add(0, null);
			}
			x++;
		}
		else if(BMUj == neurons.get(0).size()-1)
		{
			for(ArrayList<Neuron> list : neurons)
			{
				list.add(null);
			}
		}
		
		if(BMUi == 0)
		{
			ArrayList<Neuron> nulls = new ArrayList<>(); // Blank arraylist
			for(int i = 0; i < neurons.get(0).size(); i++)
			{
				nulls.add(null);
			}
			
			neurons.add(0, nulls);
			y++;
		}
		else if(BMUi == neurons.size()-1)
		{
			ArrayList<Neuron> nulls = new ArrayList<>(); // Blank arraylist
			for(int i = 0; i < neurons.get(0).size(); i++)
			{
				nulls.add(null);
			}
			neurons.add(nulls);
		}
		
		// Check for open nodes
		ArrayList<Neuron> growth = new ArrayList<>();
		ArrayList<Integer> locations = new ArrayList<>();
		if(neurons.get(y-1).get(x) == null) // Above
		{
			growth.add(createNew(x,y,1));
			locations.add(1);
		}
		if(neurons.get(y+1).get(x) == null) // Below
		{
			growth.add(createNew(x,y,2));
			locations.add(2);
		}
		if(neurons.get(y).get(x-1) == null) // Left
		{
			growth.add(createNew(x,y,3));
			locations.add(3);
		}
		if(neurons.get(y).get(x+1) == null) // Right
		{
			growth.add(createNew(x,y,4));
			locations.add(4);
		}
		
		nodes += locations.size();
		
		if(locations.size() == 0) // Distribute weights when new nodes are not created
		{
			neurons.get(y).get(x+1).distributeWeights(neurons.get(y).get(x));
			neurons.get(y).get(x-1).distributeWeights(neurons.get(y).get(x));
			neurons.get(y+1).get(x).distributeWeights(neurons.get(y).get(x));
			neurons.get(y-1).get(x).distributeWeights(neurons.get(y).get(x));
		}
		else
		{
			for(int l : locations)
			{
				switch(l)
				{
					case 1:
						neurons.get(y-1).set(x, growth.get(0));
						break;
					case 2:
						neurons.get(y+1).set(x, growth.get(0));
						break;
					case 3:
						neurons.get(y).set(x-1, growth.get(0));
						break;
					case 4:
						neurons.get(y).set(x+1, growth.get(0));
						break;
					default: System.out.println("Error! More locations than new nodes."); break;
				}
				growth.remove(0);
			}
		}
	}
	
	public Neuron createNew(int x, int y, int direction)
	{
		Neuron n1 = neurons.get(y).get(x), result = null;
		
		switch(direction)
		{
			case 1: // Above
				if(neurons.get(y-1).get(x) != null)
				{
					return new Neuron(INPUTS, n1, neurons.get(y-1).get(x), 1);
				}
				if(y-2 >= 0)
				{
					if(neurons.get(y-2).get(x) != null)
						return new Neuron(INPUTS, n1, neurons.get(y+2).get(x), 2);
				}
				if(neurons.get(y).get(x+1) != null)
				{
					return new Neuron(INPUTS, n1, neurons.get(y).get(x+1), 3);
				}
				if(neurons.get(y).get(x-1) != null)
				{
					return new Neuron(INPUTS, n1, neurons.get(y).get(x-1), 3);
				}
				else
				{
					return new Neuron(INPUTS, n1, null, 4);
				}
				
			case 2: // Below
				if(neurons.get(y+1).get(x) != null)
				{
					return new Neuron(INPUTS, n1, neurons.get(y-1).get(x), 1);
				}
				if(y+2 <= neurons.size()-1)
				{
					if(neurons.get(y+2).get(x) != null)
						return new Neuron(INPUTS, n1, neurons.get(y+2).get(x), 2);
				}
				if(neurons.get(y).get(x+1) != null)
				{
					return new Neuron(INPUTS, n1, neurons.get(y).get(x+1), 3);
				}
				if(neurons.get(y).get(x-1) != null)
				{
					return new Neuron(INPUTS, n1, neurons.get(y).get(x-1), 3);
				}
				else
				{
					return new Neuron(INPUTS, n1, null, 4);
				}
				
			case 3: // Left
				if(neurons.get(y).get(x+1) != null)
				{
					return new Neuron(INPUTS, n1, neurons.get(y).get(x+1), 1);
				}
				if(x-2 <= 0)
				{
					if(neurons.get(y).get(x-2) != null)
						return new Neuron(INPUTS, n1, neurons.get(y).get(x-2), 2);
				}
				if(neurons.get(y-1).get(x) != null)
				{
					return new Neuron(INPUTS, n1, neurons.get(y-1).get(x), 3);
				}
				if(neurons.get(y+1).get(x) != null)
				{
					return new Neuron(INPUTS, n1, neurons.get(y+1).get(x), 3);
				}
				else
				{
					return new Neuron(INPUTS, n1, null, 4);
				}
				
			case 4: // Right
				if(neurons.get(y).get(x-1) != null)
				{
					return new Neuron(INPUTS, n1, neurons.get(y).get(x-1), 1);
				}
				if(x+2 <= neurons.get(0).size()-1)
				{
					if(neurons.get(y).get(x+2) != null)
						return new Neuron(INPUTS, n1, neurons.get(y).get(x+2), 2);
				}
				if(neurons.get(y-1).get(x) != null)
				{
					return new Neuron(INPUTS, n1, neurons.get(y-1).get(x), 3);
				}
				if(neurons.get(y+1).get(x) != null)
				{
					return new Neuron(INPUTS, n1, neurons.get(y+1).get(x), 3);
				}
				else
				{
					return new Neuron(INPUTS, n1, null, 4);
				}
			
			default: break;
		}
		
		return null;
	}
}