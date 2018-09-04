/********************************************************************\
* poker.java                                                         *
* by Daniel Wedul                                                    *
* Last modified: 5/29/02                                             *
\********************************************************************/


import mpi.*;

public class poker
{
    public static void main(String [] args) throws MPIException
    {
	int myid = 0;
	int numprocs = 0;
	Intracomm world;
	Status stat;

	MPI.Init(args);
	world = MPI.COMM_WORLD;

	myid = world.Rank();
	numprocs = world.Size();

	final int numberofplayers = 9;
	final int iter = 1000000;
	deck a = new deck();

	card [][] pocket = new card[2][numberofplayers];
	card [] board = new card[5];
	card [] burn = new card[3];
	float [][] winner = new float[13][13];
	int [] count = new int[169];

	for(int i=0;i<13;i++)
	    for(int j=0;j<13;j++)
		winner[i][j] = 0;

	hand [] h = new hand[numberofplayers];

	int myiter = iter/numprocs;
	if (myiter * numprocs != iter)
	{
	    int left = iter - myiter * numprocs;
	    if (myid < left) myiter++;
	}

	for(int i=0;i<myiter;i++)
	{
	    for(int j=0;j<3;j++) a.shuffle();
	    a.cut();	
	    for(int j=0;j<3;j++) a.shuffle();
	    a.cut();

	    //deal the pockets
	    for(int j=0;j<2;j++)
		for(int k=0;k<numberofplayers;k++)
		    pocket[j][k] = a.draw();
	    //burn card
	    burn[0] = a.draw();
	    //deal flop
	    for (int j=0;j<3;j++)
		board[j]=a.draw();
	    //burn card
	    burn[1] = a.draw();
	    //turn card
	    board[3] = a.draw();
	    //burn card
	    burn[2] = a.draw();
	    //river card
	    board[4] = a.draw();

	    //make hands
	    for(int j=0;j<numberofplayers;j++)
		h[j] = new hand(pocket[0][j],pocket[1][j], board);

	    // sort the hands by rank
	    for (int k=0;k<numberofplayers-1;k++)
	    {
		for(int j=k+1;j<numberofplayers;j++)
		{
		    if (h[j].getrank() < h[k].getrank())
		    {
			hand temp = h[k];
			h[k] = h[j];
			h[j] = temp;
			card t = pocket[0][k];
			pocket[0][k] = pocket[0][j];
			pocket[0][j] = t;
			t = pocket[1][k];
			pocket[1][k] = pocket[1][j];
			pocket[1][j] = t;
		    }
		}
	    }

	    //put greater pocket card first and record hand
	    for (int j = 0;j<numberofplayers;j++)
	    {
		if (pocket[1][j].islt(pocket[0][j]))
		{
		    card t = pocket[0][j];
		    pocket[0][j] = pocket[1][j];
		    pocket[1][j] = t;
		}
		count[13*pocket[0][j].getvalue()+pocket[1][j].getvalue()-14]++;
	    }

	    //keep track of the winners
	    int numwin = 1;
	    while ( (numwin <= numberofplayers -1 )
                   && (h[numwin-1].getrank() == h[numwin].getrank()) ) 
		numwin++;
	    for (int j=0;j<numwin;j++)
	    {
		int i1 = pocket[0][j].getvalue() - 1;
		int i2 = pocket[1][j].getvalue() - 1;
		winner[i1][i2] = winner[i1][i2] + 1 / (float) numwin;
	    }

	    //return cards to deck
	    for(int j=0;j<3;j++) a.returncard(burn[j]);
	    for(int j=0;j<5;j++) a.returncard(board[j]);
	    for(int j=0;j<2;j++)
		for(int k=0;k<numberofplayers;k++)
		    a.returncard(pocket[j][k]);
	}

	float [] win = new float[169];

	for (int i=0;i<13;i++)
	{
	    for (int j=0;j<13;j++)
	    {
		win[i*13+j] = winner[i][j];
	    }
	}
	
	float [] wintot = new float[169];
	int [] counttot = new int[169];

	world.Allreduce( win, 0, wintot, 0, 169, MPI.FLOAT, MPI.SUM);
	world.Allreduce( count, 0, counttot, 0, 169, MPI.INT, MPI.SUM);

	if (myid == 0)
	{		
	    //find max NUMBER of wins
	    float max = 0;
	    int maxi1 = 0;
	    int maxi2 = 0;
	    int maxc = 0;
	    int maxc1 = 0;
	    int maxc2 = 0;
	    for(int i=0;i<13;i++)
	    {
	        for(int j=0;j<13;j++)
	        {
		    if (wintot[13*i+j] > max) 
	 	    {
		        max = wintot[13*i+j];
		        maxi1 = i;
		        maxi2 = j;
		    }
		    if (count[13*i+j] > maxc)
		    {
			maxc = count[13*i+j];
			maxc1 = i;
			maxc2 = j;
		    }
	        }
	    }
	    System.out.println("          max: " + max + " at " + 
					(++maxi1) + "," + (++maxi2));
	    System.out.println("max occurance: " + maxc + " at " +
					(++maxc1) + "," + (++maxc2));
	    //find the highest and lowest ratio of occurances to wins
	    max = 0;
	    float low = 100;
	    int lowi1 = 0;
	    int lowi2 = 0;
	    for(int i=0;i<13;i++)
	    {
	    	for(int j=0;j<13;j++)
	    	{
		    wintot[13*i+j] = wintot[13*i+j]/(float)count[13*i+j];
	    	    if (wintot[13*i+j] > max)
	    	    {
		    	max = wintot[13*i+j];
		    	maxi1 = i;
		    	maxi2 = j;
	   	    }
		    if (wintot[13*i+j] < low)
		    {
			low = wintot[13*i+j];
			lowi1 = i;
			lowi2 = j;

		    }
	    	}
	    }
	    System.out.println("     best bet: " + max + " at " +
					(++maxi1) + "," + (++maxi2));
	    System.out.println("    worst bet: " + low + " at " +
					(++lowi1) + "," + (++lowi2));
	    for(int i=0;i<13;i++)
	    {
		for(int j=0;j<13;j++)
		{
		    System.out.print (" " + wintot[13*i+j] + " ");
		}
		System.out.println("");
	    }
	} //if
	MPI.Finalize();
    }//main
}//poker

// end file poker.java
