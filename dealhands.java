//This Program just deals hands and prints the 
// cards that were dealt... no hand testing.

class dealhands {
    public static void main(String [] args) {
	deck a = new deck();
	card [] pockets = new card[18];
	card [] board = new card[5];
	card [] burn = new card[3];

	a.shuffle();
	a.cut();
	for(int i=0;i<18;i=i+2) pockets[i] = a.draw();
	for(int i=1;i<18;i=i+2) pockets[i] = a.draw();
	burn[0] = a.draw();
	for(int i=0;i<3;i++) board[i] = a.draw();
	burn[1] = a.draw();
	board[3] = a.draw();
	burn[2] = a.draw();
	board[4] = a.draw();

	System.out.print ("\n     ");
	for(int i=0;i<5;i++) board[i].printcard();
	System.out.println("\n");
	for(int i=0;i<18;i=i+2) {
	    pockets[i].printcard();
	    System.out.print ("  ");
	}
	System.out.println("");
	for(int i=1;i<18;i=i+2) {
	    pockets[i].printcard();
	    System.out.print ("  ");
	}
	System.out.println("\n");

    } // end main
} // end class dealhands

//end dealhands.java	

