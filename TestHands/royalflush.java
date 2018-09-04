class royalflush {
    public static void main(String [] Args) {
	card [] a = new card[7];
	a[0] = new card(1,1);
	a[1] = new card(13,1);
	a[2] = new card(12,1);
	a[3] = new card(11,1);
	a[4] = new card(10,1);
	a[5] = new card(3,2);
	a[6] = new card(5,2);
	hand h = new hand(a);
	h.printhandarray();
	h.showhandstrength();
    }
}
