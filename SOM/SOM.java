import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.io.*;

public class SOM
{
	static final int	CLUSTERS = 8;
	static int			TEST_DOCUMENTS = 3938, //3911,
						TRAIN_DOCUMENTS = 15752, //15598,
						INPUTS = 5000,
						MAX_ITERATIONS = 20,
						BATCH = 10000;
	
	double	SIGMA0 = (double)CLUSTERS / 2.0,
			LAMBDA = (double)MAX_ITERATIONS / Math.log(SIGMA0),
			LRATE0 = 0.1;
	
	Random r = new Random();
	//static int[][] input = new int[BATCH][INPUTS+1];
	static int[][] input = new int[TRAIN_DOCUMENTS][INPUTS+1];
	static Neuron[][] neurons;
	
	static int[] inputMap;
	
	static String[] dictionary;
	static final String directory = "Files//Lemma2//";
	
	public static void main(String[] args)
	{
		SOM som = new SOM();
		som.drawMap();
		System.out.println("Training.");
		som.loadTrainSet2();
		som.drawMap();
		System.out.println("Reading tests.");
		som.loadTestSet();
		
		dictionary = new String[INPUTS];
		som.readDictionary();
		double totalError = 0.0;
		
		for(int i = 0; i < CLUSTERS; i++)
		{
			for(int j = 0; j < CLUSTERS; j++)
			{
				double error = (neurons[i][j].BMU > 0) ? neurons[i][j].error/(double)neurons[i][j].BMU : 0;
				totalError += error;
				
				System.out.print(i + " " + j + " " + neurons[i][j].BMU + "\t" + error + "\t");
				int[] indexes = neurons[i][j].getHighestFive();
				for(int k = 0; k < indexes.length; k++)
					System.out.print(dictionary[indexes[k]] + " ");
				System.out.print("\n");
				neurons[i][j].printResults(i,j);
			}
		}
		
		System.out.println(totalError + " " + totalError/(double)(CLUSTERS*CLUSTERS));
	}
	
	public SOM()
	{
		// Initialize neurons/weights
		neurons = new Neuron[CLUSTERS][CLUSTERS];
		int k = 0;
		
		for(int i = 0; i < CLUSTERS; i++)
		{
			for(int j = 0; j < CLUSTERS; j++)
			{
				neurons[i][j] = new Neuron(INPUTS);
			}
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
	}
	
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
			while(iterations <= MAX_ITERATIONS)
			{
				System.out.println("Iteration: " + iterations);
				train(currentIndex, iterations);
				iterations++;
			}
		}
		catch(Exception e) { e.printStackTrace(); }
	}
	
	public void train(int maxIndex, int iterations)
	{
		for(int k = 0; k < maxIndex*0.8; k++)
		{
			double best = Double.MAX_VALUE;
			int BMUi = 0, BMUj = 0;
			int d = r.nextInt(maxIndex);
			
			// Go through every neuron, find the BMU -- weights will be most similar to input vector
			
			for(int i = 0; i < CLUSTERS; i++)
			{
				double dist = 0;
				for(int j = 0; j < CLUSTERS; j++)
				{
					dist = neurons[i][j].getCosSimilarity(input[d]);
					
					if(dist < best)
					{
						best = dist;
						BMUi = i;
						BMUj = j;
					}
				}
			}
						
			// Calculate nearest neighbors of BMU
			
			double radius = SIGMA0 * Math.exp((double)-iterations/LAMBDA);
			
			for(int i = 0; i < CLUSTERS; i++)
			{
				for(int j = 0; j < CLUSTERS; j++)
				{
					double dist = inRange(radius, BMUi, BMUj, i, j); // HERE
					if(dist >= 0.0)
					{
						// The neighbors (neurons in radius) are adjusted to be more like the input vector
						double learnR = LRATE0 * Math.exp((double)-iterations/LAMBDA); // Same function as radius
						double theta = Math.exp(-dist*dist/2.0*radius*radius);
						neurons[i][j].changeWeights(learnR, input[d], theta);
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
				
				for(int i = 0; i < CLUSTERS; i++)
				{
					double dist = 0;
					for(int j = 0; j < CLUSTERS; j++)
					{
						dist = neurons[i][j].getCosSimilarity(input[d]);
						
						if(dist < best)
						{
							best = dist;
							BMUi = i;
							BMUj = j;
						}
					}
				}
				neurons[BMUi][BMUj].BMU++;
				neurons[BMUi][BMUj].error += best;
				neurons[BMUi][BMUj].cats.add(br1.readLine());
				neurons[BMUi][BMUj].titles.add(br2.readLine());
			}
		}
		catch(Exception e) { e.printStackTrace(); }
	}
	
	// This is a sort of bad way to measure distance, but drawing a hex map is a pain.
	// Change to this? http://stackoverflow.com/questions/15690846/java-collision-detection-between-two-shape-objects
	public double inRange(double radius, int BMUi, int BMUj, int i, int j)
	{
		double
			magnitude,
			adjustI = (double)i,
			adjustBMU = (double)BMUi;
		
		// Odd columns are lowered a half step to attempt to emulate a hex map
		if(j % 2 != 0)		adjustI -= .5;
		if(BMUj % 2 != 0)	adjustBMU -= .5;

		magnitude = Math.sqrt((adjustBMU - adjustI)*(adjustBMU - adjustI) + (double)((BMUj - j)*(BMUj - j)));
		
		return (radius >= magnitude) ? magnitude : -1.0;
	}
	
	public void drawMap()
	{
		JFrame f = new JFrame("SOM");
		DrawPanel p = new DrawPanel(neurons, CLUSTERS);
		f.setSize(1000,1000);
		f.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		Container content = f.getContentPane();
		content.add(p);
		f.setVisible(true);
	}
}