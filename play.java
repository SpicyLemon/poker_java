/**************************************************\
* play.java                                        *
* by Daniel Wedul                                  *
* Last modified 5/30/02                            *
>**************************************************<
* Usage is as follows                              *
* java play #1 #2 -1 -2 -3 -4 -5 -6 -7             *
* the #'s are optional and are integers            *
* #1 is the number of rounds to deal               *
* #2 is the number of players                      *
* the -n's are optional and are two characters     *
*   that describe a card                           *
* -1 -2 are the pocket cards to test               *
* -3 -4 -5 are the flop                            *
* -6 is the turn                                   *
* -7 is the river                                  *
>**************************************************<
* The program deals a number of rounds using the   *
* cards given and tests how many times the given   *
* pocket wins.                                     *
\**************************************************/

public class play {
    public static void main(String [] args) {
		int iter = 10000;
		int players = 10;	//counting main hand
		card [] keep = new card[7];
		keep[0] = new card('2','C');
		keep[1] = new card('2','S');

		int argsgiven = args.length;
		int cardsgiven = argsgiven - 2;
		if (cardsgiven < 0) cardsgiven = 0;
		int cardsneeded = 7 - cardsgiven;
		if (cardsneeded > 5) cardsneeded = 5;
		if (argsgiven > 0) iter = Integer.parseInt(args[0].trim());
		if (argsgiven > 1) players = Integer.parseInt(args[1].trim());
		for (int i=0;i<cardsgiven;i++) {
		    char [] k = args[i+2].toCharArray();
		    keep[i] = new card(k[0],k[1]);
		}

		card [] leftover = new card[52];
		deck a = new deck();
		int numfound = 0;
		int count = 0;
		int split = 0;
		card temp;
		//pull the two cards in question out of the deck
		while(numfound < cardsgiven) {
		    temp = a.draw();
		    boolean wanted = false;
		    for (int i=0;i<cardsgiven;i++) {
				if ( temp.issameas(keep[i]) ) wanted = true;
		    }
		    if (wanted) {
				numfound++;
		    } else {
				leftover[count] = temp;
				count++;
		    }
		}
		while(count>0) {
		    count--;
		    a.returncard(leftover[count]);
		    leftover[count] = null;
		} 		
	

		count = 0;
		// main for loop
		for (int i=0;i<iter;i++) {

		    a.shuffle();
		    a.cut();

		    //deal the hands
		    for(int j=0;j<players*2-2;j++) {
				leftover[j] = a.draw();
		    }

		    //get a board
		    card [] board = new card[5];
		    for(int j=0;j<5-cardsneeded;j++) {
				board[j] = keep[j+2];
		    }
		    for(int j=5-cardsneeded;j<5;j++) {
				board[j] = a.draw();
		    }

		    //make the hands
		    hand [] h = new hand[players];
		    h[0] = new hand(keep[0], keep[1], board);
		    for (int j=1;j<players;j++) {
				h[j] = new hand(leftover[2*j-2],leftover[2*j-1],board);
		    }

		    //find which one wins
		    int issplit = 0;
		    int winner = h[0].getrank();
		    int mine = winner;
		    for (int j=1;j<players;j++) {
				if (h[j].getrank() < winner) {
				    winner = h[j].getrank();
				}
		    }
		    for (int j=0;j<players;j++) {
				if (h[j].getrank() == winner) {
				    issplit++;
				}
		    }
		    // see if I won and record
		    if (winner == mine) {
				count++;
				if (issplit > 1) split++;
		    }

		    //put the cards back
		    for (int j=5-cardsneeded;j<5;j++) {
				a.returncard(board[j]);
		    }
		    for (int j=0;j<players*2-2;j++) {
				a.returncard(leftover[j]);
		    }
		}// for

		//print results
		System.out.println("There were " + players + " players");
		float p = 100 * (float) count / (float) iter;
		keep[0].printcard();
		keep[1].printcard();
		if (cardsgiven > 2) {
		    System.out.print (" with ");
		    for(int i=2;i<cardsgiven;i++) {
				keep[i].printcard();
		    }
		}
		System.out.println(" won " + count + " of " +
					iter + " hands, that's " + p + "%.");
		p = 100 * (float) split / (float) count;
		System.out.println("" + split + " (" + p + "%) of those " +
						"that won split the pot."); 
    } // main
}// class play


//end play.java

	
