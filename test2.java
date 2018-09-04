// deals 1 (default) round of 10 handed poker and
// displays each hand's cards and rank
// used to test validity of program

class test2
{
    public static void main(String [] args)
    {
		final int numberofplayers = 10;
		final int iter = 1;
		deck a = new deck(true);

		card [][] pocket = new card[2][numberofplayers];
		card [] board = new card[5];
		card [] burn = new card[3];

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

		    // display hands
		    System.out.println(" ");
		    System.out.println(" ");	    
		    System.out.print (" board: ");
		    for(int j=0;j<5;j++) board[j].printcard();
		    System.out.println(" ");
		    System.out.println(" ");
		    for(int j=0;j<numberofplayers;j++)
		    {
				System.out.print ("Pocket: ");
				pocket[0][j].printcard();
				pocket[1][j].printcard();
				System.out.print ("  Rank: " + h[j].getrank() );
				System.out.println("");
		    }
		    //return cards to deck
		    for(int j=0;j<3;j++) a.returncard(burn[j]);
		    for(int j=0;j<5;j++) a.returncard(board[j]);
		    for(int j=0;j<2;j++)
				for(int k=0;k<numberofplayers;k++)
				    a.returncard(pocket[j][k]);

		    if(a.ismissingcards())
				System.out.println("\nWARNING!: cards missing after return");
		    else
				System.out.println("\nDeck is full again");
		}
    }
}
