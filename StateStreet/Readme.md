* As indicated in the problem task, there are 2 separate applications running in disparate JVMs (JRE 8) and communicating over Sockets.
* The packages are segregated according to the fucntionality. Each class has its Javadoc highlighting its purpose.
* Randomizer.java (Client) & PrimeHandler.java (Server) are the 2 'main' classes.

The code in classes RandomizerClient.java & PrimeServer.java might look a little similar in that they both send & receive data over sockets.
However as the data (no of fields & their types) being sent & received is different in them, they have been implemented that way for cleaner understading.
In particular; RandomizerClient sends, receives 1,2 fields repectively. The PrimeServer is expectedly just the opposite.
Adding unnecessary inheritance or if-else clauses in an attempt to put them all in one class would've complicated things.


The no of Threads used in the application (Client & Server side) are for performance on my local machine. 
They maynot deliver the same performance on another machine.
