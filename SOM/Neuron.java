import java.util.*;
import java.io.*;

public class Neuron
{
	double[] weights;
	Random r = new Random();
	int BMU = 0;
	ArrayList<String> titles, cats;
	double error = 0.0;
	
	public Neuron(int inputs)
	{
		weights = new double[inputs];
		for(int i = 0; i < inputs; i++)
		{
			weights[i] = r.nextDouble(); // Randomized weights
		}
		titles = new ArrayList<String>();
		cats = new ArrayList<String>();
	}
	
	public void changeWeights(double lRate, int[] inputVector, double theta)
	{
		double value;
		for(int i = 0; i < weights.length; i++)
		{
			value = (double)inputVector[i]/(double)inputVector[inputVector.length-1];
			weights[i] = weights[i] + theta * lRate *  (value - weights[i]);
		}
	}
	
	public double getDistance(int[] inputVector)
	{
		double distance = 0.0;
		double value;
		for(int i = 0; i < weights.length; i++)
		{
			value = (double)inputVector[i]/(double)inputVector[inputVector.length-1];
			distance += Math.pow(value - weights[i], 2);
		}
		distance = Math.sqrt(distance);
		return distance;
	}
	
	public double getCosSimilarity(int[] inputVector)
	{
		double similarity = 0.0, magnitudeW = 0.0, magnitudeI = 0.0;
		
		for(int i = 0; i < weights.length; i++)
		{
			similarity += weights[i] * ((double)inputVector[i]/(double)inputVector[inputVector.length-1]);
			magnitudeW += weights[i] * weights[i];
			magnitudeI += Math.pow((double)inputVector[i]/(double)inputVector[inputVector.length-1], 2);
		}
		
		return 2* (1.0 - similarity / (Math.sqrt(magnitudeI) * Math.sqrt(magnitudeW)));
	}
	
	public int getHighestIndex()
	{
		int index = 0;
		double max = Double.MIN_VALUE;
		for(int i = 0; i < weights.length; i++)
		{
			if(weights[i] > max)
			{
				index = i;
				max = weights[i];
			}
		}
		
		return index;
	}
	
	public int[] getHighestFive()
	{
        double[] copy = Arrays.copyOf(weights,weights.length);
        Arrays.sort(copy);
        double[] honey = Arrays.copyOfRange(copy,copy.length - 5, copy.length);
        int[] result = new int[5];
        int resultPos = 0;
        for(int i = 0; i < weights.length; i++)
		{
            double onTrial = weights[i];
            int index = Arrays.binarySearch(honey,onTrial);
            if(index < 0) continue;
            result[resultPos++] = i;
        }
        return result;
    }
	
	public void printResults(int k, int j)
	{
		try
		{
			PrintWriter out = new PrintWriter("Results-" + k + "-" + j);
			for(int i = 0; i < cats.size(); i++)
			{
				out.println(titles.get(i) + "\t\t" + cats.get(i));
			}
			out.close();
		}
		catch(Exception e) { e.printStackTrace(); }
	}
	
}