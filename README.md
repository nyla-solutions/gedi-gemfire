#GEDI Overview
This document explains how to use and develop components of the Grid Enterprise Data Integration (GEDI) solution. 

Grid Enterprise Data Integration (GEDI) allows  components to use GemFire based data services. 
The goal of this solution is that  grid client applications can be altered to access data on a gemfire/geode grid with minimal effort along with improved flexibility and performance.

##Operations

##clusterDiff
*(client-side & server-side)*

 This utility will perform a comparison of a source and target cluster.
	
*Outline of checks*
	
1. Checks locator/remote-locators are connected
2. Checks Gateway Senders/Receivers are running and connected
3. Checks that all gateway enabled regions entries to equals	
4. Checks if there are any pending entries in the gateway sender queue 
	   of the source cluster.
5. Region by region comparison of entries across the source and target cluster

