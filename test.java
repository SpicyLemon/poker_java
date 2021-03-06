// This program simply plays 10000 hands of 9 handed
// poker and displays a matrix of all the hands and 
// how often they won

class test
{
    public static void main(String [] args)
    {
		final int numberofplayers = 9;
		final int iter = 10000;
		deck a = new deck();

		card [][] pocket = new card[2][numberofplayers];
		card [] board = new card[5];
		card [] burn = new card[3];
		float [][] winner = new float[13][13];
		for(int i=0;i<13;i++)
		    for(int j=0;j<13;j++)
				winner[i][j] = 0;

		hand [] h = new hand[numberofplayers];

		for(int i=0;i<iter;i++)
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

		    if (pocket[1][0].isgt(pocket[0][0]))
		    {
				card t = pocket[0][0];
				pocket[0][0] = pocket[1][0];
				pocket[1][0] = t;
		    }

		    String besthand = h[0].gethandname();
		    String sp = "";
	//	    pocket[0][0].printcard();
	//	    pocket[1][0].printcard();
		    if ( h[0].getrank() == h[1].getrank() ) sp = " ....... split";
	//	    System.out.println(": "+ besthand +": "+ h[0].getrank() + sp);

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
		//print the winner matrix
		//and find max value
		float max = 0;
		int maxi1 = 0;
		int maxi2 = 0;
		for(int i=0;i<13;i++)
		{
		    for(int j=0;j<13;j++)
		    {
				if (winner[i][j] > max) 
				{
				    max = winner[i][j];
				    maxi1 = i;
				    maxi2 = j;
				}
				System.out.print (" " + winner[i][j] + " ");
		    }
		    System.out.println("");
		}
		System.out.println("max: "+ max +"at "+(++maxi1)+","+(++maxi2));
    }
}
