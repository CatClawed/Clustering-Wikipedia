An old grad school project.

1% of Wikipedia is clustered using SOM (Self-organizing map, a neural network).

Non article pages were removed as best as possible, as well as stubs. Each word was lemmatized (reduced to a base form), and certain common words were removed.

The SOM and GSOMs were implemented by hand because it sounded fun. Euclidian distance and cosine similarity were used to determine similarity of words in articles. The results were pretty decent for SOM but not amazing for GSOM; which is to say articles that are similar tended to be in the same cluster and organized decently throughout the map in SOM.