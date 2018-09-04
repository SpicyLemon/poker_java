/******************************************************\
* deck.java                                            *
* by Daniel Wedul                                      *
* Last Modified 5/27/02                                *
>******************************************************<
* This class describes a deck of playing cards         *
*  A deck is basically a stack of 52 cards             *
*  pile is the array of cards, cardsleft is the number *
*  of cards left in the deck                           *
* the "top" of the deck is the end of the array        *
* a joker may be added to the deck to make it 53 cards *
>******************************************************<
* The shuffle works as follows.                        *
* It goes through each entry in the deck               *
*  at each entry it picks another entry and the cards  *
*  are swapped. This is done up to 50 times.           *
\******************************************************/

import java.util.Random;

class deck
{
    private int cardsleft;
    private int numberofcards;
    private boolean joker;
    private card[] pile = new card[53];
    private Random generator = new Random();
    //note: if there is no joker, card pile[52] is not used

    /**************\
    * constructors *
    \**************/

    // just a regular 52 card deck
    public deck()
    {
		numberofcards = 52;
		joker = false;
		createdeck();
    } // end constructor/

    // a deck with a joker as long as j is true
    public deck(boolean j)
    {
		joker = j;
		numberofcards = 52;
		if (j)
		{
		    numberofcards = 53;
		    pile[52] = new card(15,5);
		}
		createdeck();
    }
    
    /******************\
    * end constructors *
    \******************/

    // get size, returns the number of cards left in the deck
    public int getsize()
    {
		return cardsleft;
    } // end getsize

    // a method to get the top card in the deck
    public card draw()
    {
		card retval = new card(0,0);
		if (cardsleft >= 1)
		{
		    cardsleft--;
		    retval = pile[cardsleft];
		    pile[cardsleft] = new card(0,0);
		}
		return retval;
    } // end draw 

    // a method to put a card back in the deck
    public card returncard(card a)
    {
		card retval = new card(0,0);
		if (cardsleft < numberofcards)
		{
		    pile[cardsleft] = a;
		    cardsleft++;
		}
		return retval;
    } // end returncard
	
    // a method to test if there are no cards left
    public boolean isempty()
    {
		return (cardsleft < 1);
    } // end isempty 

    // a method to test if the deck is complete
    public boolean ismissingcards()
    {
		card tester;	//the card in question
		boolean found = false; //assume it isn't found
		int problems = 0;  //the number of cards missing
		//if there are less than numberofcards cards in the deck it isn't full
		if(cardsleft < numberofcards) return true;
	
		deck a = new deck(joker);	// create a good deck
	
		    // go through each card in the good deck
		for (int i = 0;i<numberofcards;i++)    
		{
		    tester = a.draw();  
		    found = false;
		    for (int j=0;j<numberofcards;j++)  // go through each card in the deck
		    {
				if (tester.issameas(pile[j]) )   
				{
				    found = true;
				    j = numberofcards;   //exits for loop
				}
		    }
	
		    if (!found)
		    {
				problems++;
				tester.printcard();
		    }
		}

		if (problems != 0) System.out.println(" are missing");

		// if problems != 0 then the deck is short, return true.
		return (problems != 0);

    } // end ismissingcards 

    // a method to shuffle the deck
    public void shuffle()
    {
		Random generator = new Random();

		for(int i=0;i<cardsleft;i++)
		{
		    for(int j=0;j<(Math.abs(generator.nextInt()%(numberofcards - 1)))+1;j++)
		    {
				int swapindex = Math.abs(generator.nextInt() % cardsleft);
				card temp = pile[i];
				pile[i] = pile[swapindex];
				pile[swapindex] = temp;
		    }
		}
    }

    // a method to cut the deck
    public boolean cut()
    {
		// find the pivot card
		int depth = cardsleft / 2 + (generator.nextInt() % 9)-4;
		card[] top = new card[depth];
	
		// copy all cards before the pivot to another array
		for (int i=0;i<depth;i++)
		{
		    top[i] = pile[i];
		}
		// copy all cards after and including the pivot to
		//  the beginning of the array
		for (int i=0;i<cardsleft - depth;i++)
		{
		    pile[i]=pile[depth+i];
		}
		// copy the cards from the temp array to the end of the array
		for (int i=0;i<depth;i++)
		{
		    pile[cardsleft - depth+i]=top[i];
		}
		//return that it worked
		return true;
    } // end cut 

    // a method to print all cards in the deck
    public void printdeck()
    {
		for (int i=0;i<cardsleft;i++)
		{
		    if ( (i%13 == 0) && (i != 0) )
		    {
				System.out.println("");
		    }
		    else
		    {
				if (i != 0) System.out.print (" ");
		    }
		    if (pile[i] != null)
		    	pile[i].printcard();
		    else
				System.out.print ("Er");

		}
		System.out.println("");
    } // end printdeck 

    private void createdeck()
    {
       	int i;
		int j;
		for (j=1;j<=4;j++)
		{
		    for (i=1;i<=13;i++)
		    {
				pile[13*(j-1)+i-1] = new card(i,j);
		    }
		}
		cardsleft =  numberofcards;
    } // end creatdeck

} // end class deck


//end deck.java
