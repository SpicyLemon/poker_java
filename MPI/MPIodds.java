/*******************************************************************************\
* odds.java                                                                     *
* by Daniel Wedul                                                               *
* Last modified: 2/13/03                                                        *
>* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *<
* This program will run a simulation of a large number of poker hands being     *
* delt.  It will keep track of which hands win and how often.                   *
\*******************************************************************************/

import java.io.*;
import mpi.*;

public class odds {
    public static void main(String [] args) throws IOException, MPIException {
	int myid = 0;
	int numprocs = 0;
	Intracomm world;
	Status stat;

	MPI.Init(args);
	world = MPI.COMM_WORLD;
	myid = world.Rank();
	numprocs = world.Size();

	final int numberofplayers = 2;
	final int iter = 1000000;
	deck a = new deck();

	card [][] pocket = new card[2][numberofplayers];
	card [] board = new card[5];
	card [] burn = new card[3];
	float [][] winner = new float[14][14];
	int [][] count = new int[14][14];

	for(int i=0;i<14;i++) {
	    for(int j=0;j<14;j++) {
		winner[i][j] = 0;
		count[i][j] = 0;
	    }
	}

	hand [] h = new hand[numberofplayers];
	
	int myiter = iter/numprocs;
	if (myiter * numprocs != iter){
	    int left = iter - myiter * numprocs;
	    if (myid < left) myiter++;
	}

	for (int i=0; i<myiter; i++) {
	    for(int j=0; j<3; j++) a.shuffle();
	    a.cut();

	    //deal the pockets
	    for(int j=0;j<2;j++)
		for(int k=0;k<numberofplayers;k++)
		    pocket[j][k] = a.draw();
	    //burn card
	    burn[0] = a.draw();
	    //deal flop
	    for (int j=0;j<3;j++)
		board[j] = a.draw();
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
		h[j] = new hand(pocket[0][j], pocket[1][j], board);

	    //sort hands by rank
	    for (int k=0;k<numberofplayers-1;k++) {
		for(int j=k+1;j<numberofplayers;j++) {
		    if(h[j].getrank() < h[k].getrank()) {
			hand temp = h[k];
			h[k] = h[j];
			h[j] = temp;
			card t = pocket[0][k];
			pocket[0][k] = pocket[0][j];
			pocket[0][j] = t;
			t = pocket[1][k];
			pocket[1][k] = pocket[1][j];
			pocket[1][j] = t;
		    } // end if
		} // end k for loop
	    } // end j for loop

	    //put greater pocket cards in right order
	    for (int j=0; j<numberofplayers; j++) {
		int pindex1 = cti(pocket[0][j].getvaluename());
		int pindex2 = cti(pocket[1][j].getvaluename());
		if (pocket[0][j].issuitedwith(pocket[1][j])) {
		    if (pindex1 > pindex2) {
			int t = pindex1;
			pindex1 = pindex2;
			pindex2 = t;
		    }
		}
		else {
		    if (pindex2 > pindex1) {
			int t = pindex1;
			pindex1 = pindex2;
			pindex2 = t;
		    }
		}
		count[pindex1][pindex2]++;
	    }

	    //keep track of the winners
	    int numwin = 1;
	    while ( (numwin <= numberofplayers - 1 )
		   && (h[numwin-1].getrank() == h[numwin].getrank()) )
		numwin++;

	    for (int j=0; j<numwin; j++) {
		int i1 = cti(pocket[0][j].getvaluename());
		int i2 = cti(pocket[1][j].getvaluename());
		if (pocket[0][j].issuitedwith(pocket[1][j])) {
		    if (i1 > i2) {
			int t = i1;
			i1 = i2;
			i2 = t;
		    }
		}
		else {
		    if (i2 > i1) {
			int t = i1;
			i1 = i2;
			i2 = t;
		    }
		}
		winner[i1][i2] = winner[i1][i2] + 1 / (float) numwin;
	    }

	    //return cards to deck
	    for(int j=0; j<3; j++) a.returncard(burn[j]);
	    for(int j=0; j<5; j++) a.returncard(board[j]);
	    for(int j=0; j<2; j++)
		for(int k=0; k<numberofplayers; k++)
		    a.returncard(pocket[j][k]);
	}// end main for loop

	float [] win = new float[196];
	float [] wintot = new float[196];
	int [] counttot = new int[196];
	int [] counts = new int[196];

	for (int i=0; i<14; i++) {
	    for (int j=0; j<14;j++) {
		win[i*14+j] = winner[i][j];
		counts[i*14+j] = count[i][j];
	    }
	}

	world.Allreduce( win, 0, wintot, 0, 196, MPI.FLOAT, MPI.SUM);
	world.Allreduce( counts, 0, counttot, 0, 196, MPI.INT, MPI.SUM);

	for (int i=0; i<14; i++)
	    for (int j=0; j<14;j++)
		win[i*14+j] = wintot[i*14+j];

	if (myid == 0) {

	    FileWriter fw = new FileWriter ("hand_matrix.dat");
	    BufferedWriter bw = new BufferedWriter(fw);
	    PrintWriter outfile = new PrintWriter(fw);
	
	    outfile.println("   number of players: " + numberofplayers);
	    outfile.println("number of hands delt: " + iter);	
	    //find max value
	    float max = 0;
	    int maxi1 = 0;
	    int maxi2 = 0;
	    int maxc = 0;
	    int maxc1 = 0;
	    int maxc2 = 0;
	    for(int i=0; i<13; i++) {
		for(int j=0; j<14; j++) {
		    if (wintot[14*i+j] > max) {
			max = wintot[14*i+j];
			maxi1 = i;
			maxi2 = j;
		    }
		    if (counttot[14*i+j] > maxc) {
			maxc = counttot[i*14+j];
			maxc1 = i;
			maxc2 = j;
		    }
		}		
	    }
	    outfile.println("  max number of wins: " + max + " at " + itc(maxi1) + "," + itc(maxi2));
	    outfile.println("       max occurance: " + maxc+ " at " + itc(maxc1) + "," + itc(maxc2));

	    //find the highest and lowest ration of occurances to wins
	    max = 0;
	    float low = iter;
	    int lowi1 = 0;
	    int lowi2 = 0;
	    for(int i=0; i<14; i++) {
		for(int j=0; j<14; j++) {
		    wintot[14*i+j] = wintot[14*i+j]/(float)counttot[i*14+j];
		    if (wintot[14*i+j] > max) {
			max = wintot[14*i+j];
			maxi1 = i;
			maxi2 = j;
		    }
		    if (wintot[14*i+j] < low) {
			low = wintot[14*i+j];
			lowi1 = i;
			lowi2 = j;
		    }
		}
	    }
	    outfile.println("            best bet: " + max + " at " + itc(maxi1) + "," + itc(maxi2));
	    outfile.println("           worst bet: " + low + " at " + itc(lowi1) + "," + itc(lowi2));

	    outfile.println("\n *** number of wins matrix *** ");

	    for (int i=0; i<14; i++)
		outfile.print (" " + itc(i));

	    outfile.println("");

	    for (int i=0; i<13; i++) {
		outfile.print (itc(i));
		for(int j=0; j<14; j++) {
		    outfile.print (" " + win[14*i+j] + " ");
		}
		outfile.println("");
	    }

	    outfile.println("\n *** win percent matrix *** ");

	    for (int i=0; i<14; i++)
		outfile.print (" " + itc(i));

	    outfile.println("");

	    for (int i=0; i<13; i++) {
		outfile.print (itc(i));
		for(int j=0; j<14; j++) {
		    outfile.print (" " + wintot[14*i+j] + " ");
		}
		outfile.println("");
	    }

	    outfile.println("\n *** count matrix *** ");
	    for (int i=0; i<14; i++)
		outfile.print (" " + itc(i));

	    outfile.println("");
	    for (int i=0; i<13; i++) {
		outfile.print (itc(i));
		for(int j=0; j<14; j++) {
		    outfile.print (" " + counttot[14*i+j] + " ");
		}
		outfile.println("");
	    }
	

	    outfile.close();
	} //end if
	MPI.Finalize();
    }// end main


	//int to char
    public static char itc(int i) {
	char retval = 'x';
	switch(i) {
	    case(0): retval = '2'; break;
	    case(1): retval = '3'; break;
	    case(2): retval = '4'; break;
	    case(3): retval = '5'; break;
	    case(4): retval = '6'; break;
	    case(5): retval = '7'; break;
	    case(6): retval = '8'; break;
	    case(7): retval = '9'; break;
	    case(8): retval = 'T'; break;
	    case(9): retval = 'J'; break;
	    case(10): retval = 'Q'; break;
	    case(11): retval = 'K'; break;
	    case(12): retval = 'A'; break;
	    case(13): retval = 'R'; break;
	}
	return retval;
    }

	//char to int
    public static int cti(char c) {
	int retval = -1;
	switch(c) {
	    case('2'): retval = 0; break;
	    case('3'): retval = 1; break;
	    case('4'): retval = 2; break;
	    case('5'): retval = 3; break;
	    case('6'): retval = 4; break;
	    case('7'): retval = 5; break;
	    case('8'): retval = 6; break;
	    case('9'): retval = 7; break;
	    case('T'): retval = 8; break;
	    case('J'): retval = 9; break;
	    case('Q'): retval = 10; break;
	    case('K'): retval = 11; break;
	    case('A'): retval = 12; break;
	    case('R'): retval = 13; break;
	}
	return retval;
    }

}// end odds

//end file odds.java
