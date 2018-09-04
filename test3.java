// This tests a specific hand to make sure that
// the hand testers work.

class test3 {
    public static void main(String [] Args) {
		card [] a = new card[7];
		a[0] = new card(1,1);
		a[1] = new card(2,1);
		a[2] = new card(3,1);
		a[3] = new card(4,1);
		a[4] = new card(5,1);
		a[5] = new card(6,2);
		a[6] = new card(7,2);
		hand h = new hand(a);
		h.printhandarray();
		h.showhandstrength();
    }
}
