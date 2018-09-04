# poker_java
This is a java poker simulator that I wrote back in 2001.  I used to have it shared on my website, but I don't have that up anymore so I thought I'd put it in github.

When I was writing my javascript poker simulator, I think I found a couple small bugs in it, but I don't remember what they were, and I don't think I fixed them. Right now, I'm thinking it had to do with the joker, but it might have been in hand ranking section. Use it at your own risk.

## Contents
card.java, deck.java, and hand.java contain the classes.
All the other files contain runnable programs.
The files in folder TestHands contain executable files that create a sample hand and test the hand for proper rank.
The files in the folder MPI contain similar executable files, but utilize Message Passing Interface (MPI) to use multiple computers/processors to run the simulations.  This was originally written for a Beowulf cluster.  I've no clue if it will work on a standard computer, but I wrote it, so it's there.