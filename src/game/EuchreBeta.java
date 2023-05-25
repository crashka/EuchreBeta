package game;

import java.lang.Math;
import java.util.*;

public class EuchreBeta {

    // unified RNG for repeatability in testing (see main())
    static final Random rgen = new Random();

    // define strings for card ranks, suits and players
    public static int[] cards = new int[28];        // 24 cards + 4 placeholders for suit images
    public static String[] position = {"South", "West", "North", "East", "fifth"};
    public static String[] suitx = {"spades", "hearts", "diamonds", "clubs"};
    public static String[] rankx = {"9", "10", "Jack", "Queen", "King", "Ace", "Jack", "Jack"};
    public static String cardname[][] = new String[4][8];

    // define a few more global variables
    public static int game = 10; // points needed to win a game (can modify this to extend play)

    // *** Method "naming" for assigning names to cards ***
    public static String[][] naming(String cardname[][], String suit[], String rank[]) {
        for (int i=0; i<4; i++) {
            for (int j=0; j<8; j++) {
                cardname[i][j] = (rank[j] + " of " + suit[i]);
            }
        }
        return cardname;
    }

    // *** Method "shuffle" for shuffling cards (twice) ***
    public static int[] shuffle(int cards[]) {
        for (int i=0; i<24; i++) {
            int randomPosition = rgen.nextInt(24);
            int temp = cards[i];
            cards[i] = cards[randomPosition];
            cards[randomPosition] = temp;
        }
        for (int i=0; i<24; i++) {
            int randomPosition = rgen.nextInt(24);
            int temp = cards[i];
            cards[i] = cards[randomPosition];
            cards[randomPosition] = temp;
        }
        return cards;
    }

    // *** Method "order" for putting cards in order (human player only) ***
    public static int[] order(int cards[], int trump) {
        // values for cards, if trump not yet determined
        int[] ord = new int [] {20,16,12,8,4,0,21,17,13,9,5,1,22,18,14,10,6,2,23,19,15,11,7,3};
        int c[] = new int[6]; // keep temporary new order of given player's cards; 6th registry denotes
        // next spot to write to
        if (trump == 4) { // before trump has been determined
            for (int i=0; i<24; i++) {
                for (int j=0; j<5; j++) {
                    if (cards[j] == ord[i]) {
                        c[c[5]] = cards[j];
                        c[5]++;
                    }
                }
            }
        } else { // trump has been determined
            for (int i=trump+28; i>= 0; i=i-4) { // first look at cards in trump suit
                for (int j=0; j<5; j++) { // look at a given player's 5 cards
                    if (cards[j] == i) {
                        c[c[5]] = cards[j];
                        c[5]++;
                    }
                }
            }
            if (trump != 0) { // spades suit (if spades not trump)
                for (int i=20; i>=0; i=i-4) {
                    for (int j=0; j<5; j++) {
                        if (cards[j] == i) {
                            c[c[5]] = cards[j];
                            c[5]++;
                        }
                    }
                }
            }
            if (trump != 1) { // hearts suit (if hearts not trump)
                for (int i=21; i>=0; i=i-4) {
                    for (int j=0; j<5; j++) {
                        if (cards[j] == i) {
                            c[c[5]] = cards[j];
                            c[5]++;
                        }
                    }
                }
            }
            if (trump != 2) {
                for (int i=22; i>=0; i=i-4) { // diamonds suit (if diamonds not trump)
                    for (int j=0; j<5; j++) {
                        if (cards[j] == i) {
                            c[c[5]] = cards[j];
                            c[5]++;
                        }
                    }
                }
            }
            if (trump != 3) { // clubs suit (if clubs not trump)
                for (int i=23; i>=0; i=i-4) {
                    for (int j=0; j<5; j++) {
                        if (cards[j] == i) {
                            c[c[5]] = cards[j];
                            c[5]++;
                        }
                    }
                }
            }
        }
        for (int i=0; i<5; i++) {
            cards[i] = c[i];
        }
        return cards;
    }

    // *** Method "bidder11" for determining 1st round bid of 1st player to bid ***
    public static int bidder11(int psup, int psnx, int psnxnx, int right, int left, int acet, int aces, int uprk,
                               int upst, double bsup, double bsnx, double sum, int vd, double bestc, int pts, int game) {
        int call = 0; // value of docall returned: 0 = pass, 1 = bid with partner, 2 = bid alone
        if (psup == 5) { // have 5 trump
            call = 2;
        } else if (psup == 4) { // have 4 trump
            if (right == 1) { // if have R, bid alone
                call = 2;
            } else if (uprk == 2) { // if R turned, bid wp
                if (left == 1 && psnx > 1) {
                    call = 0; // if have L AND off-suit next, pass
                } else if (left == 1 && acet == 0 && aces == 0) {
                    call = 0; // if have L AND no A of trump AND no off-suit A, pass
                } else {
                    call = 1; // else, bid wp
                }
            } else if (left == 1) { // have L
                if (aces == 1) {
                    call = 2; // if have an off-suit A, bid alone
                } else if (acet == 0) {
                    call = 2; // if have A of trump, bid alone
                } else if (psnx > 1) {
                    call = 0; // if have off-suit next, pass
                } else if (bsup > 19.5) {
                    call = 2; // if have 19.6+ pts, bid alone
                } else {
                    call = 1; // else bid wp
                }
            } else {
                call = 1; // else bid wp
            }
        } else if (uprk == 2) {
            call = 0; // if R turned, pass
        } else if (right + left == 2) { // have both bowers
            if (psup == 3) { // have 3 trump
                if (psnx > 1) { // have off-suit next
                    if (bsup > 27 || (bsup > bsnx)) {
                        call = 2; // bid alone with 27+ pts OR if turn suit pts > next suit pts
                    }
                } else if (aces > 0 || sum > 11.85) {
                    call = 2;
                }
            } else if (psup == 2 && psup >= psnxnx) { // have 2 trump
                if (uprk > 3 && sum > 13) {
                    call = 2; // bid alone if A or K turned AND summins > 13
                } else if (uprk < 4 && sum > 14) {
                    call = 2; // bid alone if 9, 10 or Q turned AND summins > 14
                }
            }
        } else if (aces == 3) { // have 3 off-suit aces
            if (bsup > 17 && left == 0) {
                call = 2; // with 17.1+ points AND if don't have L, bid alone
            } else if (psup == 2 && left == 0) {
                call = 1; // if have 2 trump AND don't have L
            }
        } else if (psup == 3 && psup > psnxnx) {
            // have 3 trump AND trump suit longer than next suit
            if (acet == 1 && sum > 14) {
                call = 2; // bid alone if have A of trump AND all off-suits led by A
            } else if ((sum > 14 || (acet == 1 && (aces > 0 || vd == 2)))
                       && bsup > 18.4 && (bsup > bsnx)) {
                call = 2; // bid alone with 18.5+ pts AND only one weakness (no A trump OR only one off-suit
                // not led by A AND bid points exceeding next bid points)
            } else if ((sum > 14 || (acet == 1 && (aces > 0 || vd == 2)))
                       && bsup > 15.9 && (bsup - bsnx) > 5) {
                call = 2; // bid alone with 16+ pts AND only one weakness (no A trump OR only one off-suit
                // not led by A AND bid points exceeding next bid points by at least 5)
            } else if ((acet == 1 || vd == 1 || aces > 0) && (bsup > 17.9 ||
                                                              sum > 13.5 || (sum > 12.5 && acet == 1)) &&
                       psnx < 2) {
                call = 2; // bid alone with 2 weaknesses AND (18+ bid pts or summins > 12) AND no off-suit next
            } else {
                call = 0; // else pass
            }
        } else if (psup == 2 && right == 1 && left == 0 &&
                   psnx == 0) {
            // have 2 trump including R (and NOT L) AND no cards in next suit
            if (bsup > 14 && bsup > bestc) {
                call = 2; // if > 14 pts AND best suit, bid alone
            } else if (bsup > 12.8 && bsup > bestc) {
                call = 1; // if > 12.8 pts AND best suit, bid wp
            } else if (bsup > 7 && bsup > (bestc+5)) {
                call = 1; // if > 7 pts AND best suit by 5+ pts, bid wp
            }
        }
        if (call == 2 && pts > game-3) {
            call = 1; // don't bid alone if within 2 pts of winning game, bid wp instead
        }
        return call;
    }

    // *** Method "bidder12" for determining 1st round bid of 2nd player to bid ***
    public static int bidder12(int psup, int psnx, int right, int left, int acet, int kingt, int uprk,
                               int upst, double bsup, double sum, int vd, int pts, int game) {
        int call = 0; // value of docall returned: 0 = pass, 1 = bid with partner, 2 = bid alone
        if (psup == 5) { // have 5 trump, bid alone
            call = 2;
        } else if (psup == 4) { // have 4 trump
            if (((right == 1) || (left == 1 && uprk == 2))
                && sum > 12) {
                call = 2; // R is highest trump in hand (or L with R turned) AND off-suit Q+
            } else if (left == 1 && sum > 13.5) {
                call = 2; // L is highest trump in hand AND off-suit K+
            } else if (acet == 1 && sum > 14.5) {
                call = 2; // A is highest trump in hand and off-suit A
            } else if (kingt == 1 && sum > 14.5 && uprk == 5) {
                call = 2; // K is highest trump in hand AND off-suit A AND A turned
            } else {
                call = 1; // else, bid wp
            }
        } else if (psup == 3) { // have 3 trump
            if ((right == 1 && (bsup + sum) > 33)
                // with R AND pts + summins > 33, bid alone
                || (left == 1 && sum > 13.8) // with L AND summins > 13.8, bid alone
                || (acet == 1 && sum > 14.5) // with A AND summins > 14.5, bid alone
                || (kingt == 1 && sum > 14.5 && uprk == 5)) {
                // with K AND summins > 14.5 AND A turned, bid alone
                call = 2;
            } else {
                call = 1;
            }
        } else if (psup == 2) { // have 2 trump
            if (right == 1) { // have R
                if (left == 1 && (bsup > 19.5 ||
                                  sum > 14.5)) { // also have left AND (19.6+ pts OR summins > 14.5)
                    call = 2;
                } else if (sum > 14.5) { // don't have L but summins > 14.5
                    call = 2;
                } else {
                    call = 1;
                }
            } else if (left == 1) { // have L
                if (bsup > 17) {
                    call = 2; // bid alone with 17.1+ pts
                } else if (bsup > 11.5) {
                    call = 1; // bid wp with 11.6+ pts
                } else if (vd == 2 && psnx == 0) {
                    call = 1; // bid wp if 2-suited (off-suit is green suit)
                }
            } else if (acet == 1) { // have A
                if (sum == 15) {
                    call = 2; // have 3 off-suit aces
                } else if (bsup > 6) {
                    call = 1; // have 6.1+ pts
                }
            } else if (kingt == 1 && bsup > 6) {
                call = 1; // have K AND 6.1+ pts.
            }
        }
        if (call == 2 && pts > game-3) {
            call = 1; // don't bid alone if within 2 pts of winning game, bid wp instead
        }
        return call;
    }

    // *** Method "bidder13" for determining 1st round bid of 3rd player to bid ***
    public static int bidder13(int psup, int psnx, int right, int left, int acet, int uprk, int upst,
                               double bsup, double sum, int vd, int pts, int game) {
        int call = 0; // value of docall returned: 0 = pass, 1 = bid with partner, 2 = bid alone
        if (psup == 5) { // have 5 trump
            call = 2;
        } else if (psup == 4) { // have 4 trump
            if (right == 1 && bsup > 24) {
                call = 2; // have R AND 24.1+ pts
            } else if (left == 1 && bsup > 22.5) {
                call = 2; // have L AND 22.6+ pts
            } else if (acet == 1 && left == 0 && bsup > 23) {
                call = 2; // have A (and not L) AND 23.1+ pts
            } else if ((acet == 1 || left == 1) &&
                       sum > 14.5 && uprk < 6) {
                call = 2; // have an off-suit A AND higher trump than turn
            } else {
                call = 1; // else bid wp
            }
        } else if (psup == 3) { // have 3 trump
            if (uprk == 2) {
                call = 0; // pass if R turned
            } else if (bsup > 30 || (bsup > 28 &&
                                     psnx == 0)) {
                call = 2; // bid alone with 30.1+ pts, or 28.1+ pts AND off-suit not next
            } else if (bsup > 23) {
                call = 1; // bid wp with 23.1+ pts
            }
        } else if (right == 1 && left == 1
                   && psup == 2 && bsup > 12) {
            call = 1; // bid wp if have both bowers AND only 2 trump AND pts > 12
        }
        if (call == 2 && pts > game-3) {
            call = 1; // don't bid alone if within 2 pts of winning game, bid wp instead
        }
        return call;
    }

    // *** Method "bidder14" for determining 1st round bid of 4th player to bid ***
    public static int bidder14(int psup, int psnx, int right, int left, int acet, int aces, int kingt, int uprk,
                               int upst, double bsup, double sum, int vd, int pts, int game) {
        int call = 0; // value of docall returned: 0 = pass, 1 = bid with partner, 2 = bid alone
        if (psup == 5) { // have 5 trump
            call = 2;
        } else if (psup == 4) { // have 4 trump
            if (right == 1 && (sum > 12.5 || psnx == 1)) {
                call = 2; // have R AND either Q+ off-suit OR any next off-suit
            } else if (((left == 1 || acet == 1) && sum > 13.5) ||
                       (left == 1 && acet == 1 && sum > 12.5)) {
                call = 2; // have L or A AND K+ off-suit, or L AND A AND Q+ off-suit
            } else if (kingt == 1 && sum > 14.5) {
                call = 2; // have K of trump AND A off-suit
            } else {
                call = 1; // else bid wp
            }
        } else if (psup == 3) { // have 3 trump
            if (bsup > 22.5 || (bsup > 19 && sum > 13.5) ||
                (bsup > 10 && sum > 14.8)) {
                call = 2; // if 22.6+ pts OR 19.1+ pts and summins > 13.5 OR 10.1+ pts and summins > 14.8
            } else if (bsup > 0.5) {
                call = 1; // else bid wp with .6+ pts
            }
        } else if (psup == 2) { // have 2 trump
            if (right == 1) { // have R
                if (sum > 13.5 && vd < 2 && (left == 1 || acet == 1)) {
                    call = 2; // bid alone if have L or A AND summins > 13.5 AND don't have 2 void suits
                } else if (sum > 14 && vd < 2) {
                    call = 2; // bid alone with any other trump AND summins > 14 AND don't have 2 voids
                } else {
                    call = 1; // else bid wp
                }
            } else if (left == 1 || acet == 1 || kingt == 1) { // have L or A or K
                if (sum > 14 && vd == 0) { // have 3 off-suit aces
                    call = 2; // bid alone
                }
            } else if (sum > 14 && vd == 0) { // have 3 off-suit aces, and Q or 10 is highest trump
                call = 1; // bid wp
            }
        } else if (right == 1 && aces == 3) {
            call = 1; // bid wp with just R AND all 3 off-suit Aces
        }
        if (call == 2 && pts > game-3) {
            call = 1; // don't bid alone if within 2 pts of winning game, bid wp instead
        }
        return call;
    }

    // *** Method "bid" for declaring bid, first bidding round ***
    public static int[] bid(int docall, int dlr, String cname, int turns) {
        int bidtest[] = {-1,-1,4,0}; // lone, declarer, trump suit, bid
        if (docall == 2) {
            System.out.println(position[dlr] + " calls " + cname + " as trump, going alone. " + "\n");
            bidtest[0] = dlr;
            bidtest[1] = dlr;
            bidtest[2] = turns;
            bidtest[3] = 2;
        } else if (docall == 1) {
            System.out.println(position[dlr] + " calls " + cname + " as trump. " + "\n");
            bidtest[1] = dlr;
            bidtest[2] = turns;
            bidtest[3] = 1;
        } else {
            bidtest[3] = 0;
            System.out.println(position[dlr] + " passes" + "\n");
        }
        return bidtest;
    }

    // *** Method "bidder21" for determining 2nd round bid of 1st player to bid ***
    public static int bidder21(int psup, int psnx, int right, int left, int acet, int aces, int kingt,
                               int upst, double bsup, double sum, int bests, int pts, int game) {
        int call = 0; // value of docall returned: 0 = pass, 1 = bid with partner, 2 = bid alone
        if (psup == 5) { // have 5 trump
            call = 2;
        } else if (psup == 4 && bests == 3-upst) {
            // have 4 trump and best suit is next
            if (right == 1) {
                call = 2; // bid alone if R high trump
            } else if ((right != 0 || left != 0
                        || acet != 0) && psnx == 1) {
                call = 2; // bid alone with A+ trump AND next off-suit
            } else if ((left == 1 || acet == 1) &&
                       sum > 12.5) {
                call = 2; // bid alone with L or A AND Q+ off-suit
            } else if (kingt == 1 && sum > 14.5) {
                call = 2; // bid alone with K AND A off-suit
            } else {
                call = 1;
            }
        } else if (psup == 4) {
            // have 4 trump and best suit is green
            if (right == 1) {
                call = 2; // bid alone if R high trump
            } else if (bsup > 17.5) {
                call = 2; // bid alone with 17.6+ pts
            } else {
                call = 1;
            }
        } else if (psup == 3 && bests == 3-upst) {
            // have 3 trump and best suit is next
            if (right == 1 && left == 1 && bsup > 22) {
                call = 2; // bid alone with both bowers AND 22.1+ pts
            } else if ((right + left == 1) && bsup > 18.5) {
                call = 2; // bid alone with one bower AND 18.6+ pts
            } else if ((right + left == 0) && bsup > 15.5) {
                call = 2; // bid alone with no bowers AND 15.6+ pts
            } else {
                call = 1; // else bid wp
            }
        } else if (psup == 3) { // have 3 trump and best suit is green
            if (bsup > 19.5) {
                call = 2; // bid alone with 19.6+ pts
            } else if (bsup > 1.5) {
                call = 1; // bid wp with 1.6+ pts
            }
        } else if (psup == 2 && bests == 3-upst) { // have 2 trump and best suit is next
            if ((right == 1 || left == 1) && bsup > 16) {
                call = 2; // have a bower AND 16.1+ pts
            } else if ((right + left == 0) && bsup > 14.5) {
                call = 2; // don't have a bower AND 14.6+ pts
            } else {
                call = 1; // else bid wp * always bid with 2 next trump! negative eo, but even worse if pass
            }
        } else if (psup == 2) { // have 2 trump and best suit is green
            if ((right + left == 2)
                && aces > 0) {
                call = 2; // bid alone with both bowers AND an off-suit A
            } else if ((right == 1 || left == 1)
                       && bsup > 13 && sum > 14.4) {
                call = 2; // bid alone with a bower AND all non-next suits A-led AND 13.1+ pts
            } else if (bsup > 9.5) {
                call = 1; // bid wp with 9.6+ pts
            }
        } else if (psup == 1 && bests == 3-upst &&
                   bsup > 0) {
            call = 1; // bid wp with 1 trump AND 0+ pts (if next is best suit)
        }
        if (call == 2 && pts > game-3) {
            call = 1; // don't bid alone if within 2 pts of winning game, bid wp instead
        }
        return call;
    }

    // *** Method "bidder22" for determining 2nd round bid of 2nd player to bid ***
    public static int bidder22(int psup, int psnx, int right, int left, int acet, int aces, int kingt,
                               int upst, int uprk, double bsup, double sum, int bests, int pts, int game) {
        int call = 0; // value of docall returned: 0 = pass, 1 = bid with partner, 2 = bid alone
        if (psup == 5) { // have 5 trump
            call = 2;
        } else if (psup == 4 && bests == 3-upst) {
            // have 4 trump and best suit is next
            if (right == 1) {// if have R, bid alone
                call = 2;
            } else if (left == 1 && sum > 13.7) {
                // have L AND K+ off-suit, bid alone
                call = 2;
            } else if (left == 0 && acet == 1
                       && bsup > 14) { // have A (but not L) AND 14.1+ pts, bid alone
                call = 2;
            } else if (kingt == 1 && sum > 13.7) {
                // have K of trump AND K+ off-suit, bid alone
                call = 2;
            } else { // else bid wp
                call = 1;
            }
        } else if (psup == 4) { // have 4 trump and best suit is green
            if (right == 1 && (sum > 11.5
                               || psnx == 1)) {
                // have R AND J+ off-suit or next off-suit
                call = 2;
            } else if (uprk == 2 && left == 1 &&
                       (sum > 11.5 ||
                        psnx == 1)) {
                // R turned AND have L AND have J+ off-suit or next off-suit, bid alone
                call = 2;
            } else if (uprk != 2 && left == 1 &&
                       sum > 12.5) {
                // R not turned AND have L AND have Q+ off-suit, bid alone
                call = 2;
            } else if (sum > 13.5) { // if K+ off-suit, bid alone
                call = 2;
            } else {
                call = 1; // else bid wp
            }
        } else if (psup == 3 && bests == 3-upst) {
            // have 3 trump and best suit is next
            if (right == 1) { // have R
                if (bsup > 19.5) {
                    call = 2; // bid alone with 19.6+ pts
                } else {
                    call = 1; // else bid wp
                }
            } else if (left == 1) { // have L
                if (bsup > 17) {
                    call = 2; // bid alone with 17.1+ pts
                } else if (bsup > 10.5) {
                    call = 1; // bid wp with 10.6+ pts
                }
            } else if (bsup > 10.5 && sum > 13.8) {
                call = 2; // bid alone with 10.6+ pts AND summins > 13.8
            } else if (bsup > 11) {
                call = 1; // bid wp with 11.1+ pts
            }
        } else if (psup == 3) {
            // have 3 trump and best suit is green
            if (right + left == 2
                && sum > 12.8) {
                call = 2; // if have both bowers AND summins > 12.8, bid alone
            } else if (bsup > 14.5 && sum > 13.8) {
                call = 2; // if 14.6+ pts AND summins > 13.8, bid alone
            } else {
                call = 1; // else bid wp
            }
        } else if (psup == 2 && bests == 3-upst) {
            // if have 2 trump and best suit is next
            if (right == 1) { // have R
                if (bsup > 0.5) {
                    call = 1; // bid wp with 0.6+ pts
                }
            } else if (left == 1) { // have L
                if (sum > 14.4 && bsup > 8.5) {
                    call = 1; // bid wp with all off-suits Ace-led AND 8.6+ pts
                }
            } else if (bsup > 15) {
                call = 2; // bid alone with 15.1+ pts
            } else if (bsup > 11.5) {
                call = 1; // bid wp with 11.6+ pts
            }
        } else if (psup == 2) {
            // have 2 trump and best suit is green
            if (aces == 3) { // have 3 off-suit aces
                if (right == 1 || left == 1
                    || acet == 1) { // have A+ trump
                    call = 2;
                } else {
                    call = 1;
                }
            } else if (bsup + sum > 28.5) {
                call = 2; // if pts + summins > 28.5, bid alone
            } else if (bsup+5 > 0) {
                call = 1; // bid wp with -4.9 or more pts
            }
        } else if (psup == 1 &&
                   bests != 3-upst && bsup > 6) {
            // with one trump in green suit AND 6.1+ pts, bid wp
            call = 1;
        }
        if (call == 2 && pts > game-3) {
            call = 1; // don't bid alone if within 2 pts of winning game, bid wp instead
        }
        return call;
    }

    // *** Method "bidder23" for determining 2nd round bid of 3rd player to bid ***
    public static int bidder23(int psup, int right, int left, int acet, int aces, int kingt, int upst,
                               int uprk, double bsup, double sum, int bests, int pts, int game) {
        int call = 0; // value of docall returned: 0 = pass, 1 = bid with partner, 2 = bid alone
        if (psup == 5) { // have 5 trump
            call = 2;
        } else if (psup == 4 && bests == 3-upst) { // have 4 trump in next suit
            if (uprk == 2) { // R turned
                if ((right == 1 || acet == 1) &&
                    sum > 12.4) {
                    call = 2; // have R or A of trump AND Q+ off-suit
                } else if (kingt == 1 && sum > 14.4) {
                    call = 2; // have K trump AND A off-suit
                } else {
                    call = 1;
                }
            } else if (right == 1) {
                call = 2; // have R
            } else if (left == 1 && sum > 13.5) {
                call = 2; // have L AND K+ off-suit
            } else if (acet == 1 && bsup > 12.4) {
                call = 2; // have A trump AND 12.5+ pts
            } else if (kingt == 1 && sum > 14.4) {
                call = 2; // have K AND A off-suit
            } else {
                call = 1;
            }
        } else if (psup == 4) { // have 4 trump in green suit
            if (right == 1 && sum > 11.4) {
                call = 2; // have R AND J+ off-suit
            } else if ((left == 1 || acet == 1)
                       && sum > 14.4) {
                call = 2; // have L or A AND A off-suit
            } else {
                call = 1; // bid wp
            }
        } else if (psup == 3 && bests == 3-upst) { // have 3 trump in next
            if (bsup > 23.4) {
                call = 2; // have 23.5+ pts
            } else {
                call = 1;
            }
        } else if (psup == 3) { // have 3 trump in green suit
            if ((right + left == 2)
                && bsup > 22) {
                call = 2; // have both bowers AND 22.1+ pts
            } else if ((right + left < 2)
                       && bsup > 19) {
                call = 2; // don't have both bowers BUT 19.1+ pts
            } else if ((right + left == 2)
                       || (bsup > 7 &&
                           (right == 1 || aces > 0))) {
                call = 1; // have both bowers OR 7.1+ pts AND either an off-A or R
            }
        } else if (aces == 3) { // have 3 off-suit aces
            if (bests == 3-upst && bsup > 16.5) {
                call = 2; // bid alone if 16.6+ pts in next suit
            } else if (bsup > 18.5) {
                call = 2; // bid alone with 18.6+ pts in green suit
            } else {
                call = 1; // else bid wp
            }
        } else if (psup == 2 && bests == 3-upst) { // 2 trump, next suit
            if (bsup > 18) {
                call = 2; // bid alone with 18.1+ pts
            } else if (bsup+4 > 0) {
                call = 1; // bid wp with -3.9 or more pts
            }
        } else if (psup == 2) { // 2 trump, green suit
            if (bsup+2 > 0) {
                call = 1; // bid wp with -1.9 or more pts
            }
        } else if (psup == 1 && bests == 3-upst &&
                   bsup > 0) { // 1 trump, next suit
            call = 1; // bid wp with 0.1+ pts in next suit
        }
        if (call == 2 && pts > game-3) {
            call = 1; // don't bid alone if within 2 pts of winning game, bid wp instead
        }
        return call;
    }

    // *** Method "bidder24" for determining 2nd round bid of 4th player to bid ***
    public static int bidder24(int psup, int psnx, int right, int left, int acet, int upst,
                               int uprk, double bsup, double sum, int bests, int pts, int game) {
        int call = 0; // value of docall returned: 0 = pass, 1 = bid with partner, 2 = bid alone
        if (psup == 5) { // have 5 trump
            call = 2;
        } else if (psup == 4 && bests == 3-upst) { // have 4 trump in next suit
            if (uprk == 2 && bsup > 17) {
                call = 2; // bid alone with R turned AND 17.1+ pts
            } else if (bsup > 20) {
                call = 2; // bid alone with 20.1+ pts
            } else {
                call = 1; // else bid wp
            }
        } else if (psup == 4) { // have 4 trump in green suit
            if (uprk == 2) { // R turned
                if (right == 1 && (sum > 12.4 ||
                                   psnx == 1)) {
                    call = 2; // bid alone with R AND either Q+ off-suit or any next off-suit
                } else if (sum > 14.4) {
                    call = 2;
                } else {
                    call = 1;
                }
            } else if (right == 1 || ((acet == 1 ||
                                       left == 1) && sum > 13.4) ||
                       sum > 14.4) {
                call = 2; // bid alone with R, OR L or A AND K+ off-suit, OR A off-suit
            } else {
                call = 1; // else bid wp
            }
        } else if (psup == 3 && bests == 3-upst && bsup > 2) {
            call = 1; // bid wp with 3 trump in next AND 2.1+ pts
        } else if (psup == 3) { // have 3 trump in green suit
            if ((right + left == 2) && bsup > 21.3) {
                call = 2; // bid alone with both bowers AND 21.4+ pts
            }  else if (right + left == 2) {
                call = 1; // bid wp with both bowers
            } else if (sum > 14.7) {
                call = 2; // bid alone with summins > 14.7
            } else if (bsup > 7) {
                call = 1; // bid wp with 7.1+ pts
            }
        } else if (psup == 2 && bsup > 6) {
            call = 1; // bid wp with 2 trump AND 6.1+ pts
        }
        if (call == 2 && pts > game-3) {
            call = 1; // don't bid alone if within 2 pts of winning game, bid wp instead
        }
        return call;
    }

    // *** Method "bid" for declaring bid, second bidding round ***
    public static int[] bid(int docall, int dlr, int dlrs) {
        int[] bidtest = {-1,-1,4,0};
        if (docall == 2) {
            System.out.println(position[dlr] + " calls " + suitx[dlrs] + " as trump, going alone. " + "\n");
            bidtest[0] = dlr;
            bidtest[1] = dlr;
            bidtest[2] = dlrs;
            bidtest[3] = 2;
        } else if (docall == 1) {
            System.out.println(position[dlr] + " calls " + suitx[dlrs] + " as trump. " + "\n");
            bidtest[1] = dlr;
            bidtest[2] = dlrs;
            bidtest[3] = 1;
        } else {
            bidtest[3] = 0;
            System.out.println(position[dlr] + " passes" + "\n");
        }
        return bidtest;
    }

    // *** Method "playfirst" for play of cards ***
    public static int playfirst(int play, int fintp, int hitrump, int sectrump, int lotrump, int lead,
                                int maxsuit, int minsuit, int suitace, int worsts, int worstr, int who, int slength, int cards[]) {
        if (worsts < 0 || worstr < 0) {
            assert (play >= 0 && play < 6) || (play == 5 && suitace != -1);
        }
        int cardplay = -1;
        int m = -1;
        int n = -1;
        // values of play1: 0 = high trump; 1 = second high trump; 2 = low trump;
        // 3 = maxsuit; 4 = minsuit; 5 = best A; 6 = worst card (default)
        if (play == 0) {
            m = fintp;
            n = hitrump;
        } else if (play == 1) {
            m = fintp;
            n = sectrump;
        } else if (play == 2) {
            m = fintp;
            n = lotrump;
        } else if (play == 3) {
            if (lead == -1) { // **can also mean lead same suit!
                m = minsuit;
            } else {
                m = lead;
            }
            n = maxsuit;
        } else if (play == 4) {
            m = lead;
            n = minsuit;
        } else if (play == 5 && suitace != -1) {
            if (slength == -1) {
                n = sectrump;
            } else {
                n = 5;
            }
            m = suitace;
        } else { // play worst card
            m = worsts;
            n = worstr;
        }
        cardplay = m + n*10;
        return cardplay;
    }

    // *** Method "wintrick" to tally tricks won ***
    public static int wintrick(int winner, int turn) {
        System.out.println("Player " + position[winner] + " wins trick " + turn + ".\n");
        System.out.println("----------------------------------------------------------");
        int result = -1;
        if (winner == 0 || winner == 2) {
            result = 0;
        } else {
            result = 1;
        }
        return result;
    }

    // *** Method "swapcard" to determine card dealer should swap, if 1st round bid ***
    public static int swapcard(int cards[], int seat, int dealer) {
        // seat 0 = dealer alone; seat 1 = dealer wp; seat 2 = 1st seat alone, ...
        int aced = 0; // number of off-suit aces held by dealer
        int kingd = 0; // number of off-suit kings held by dealer
        int swap = -1; // the eventual best card to swap
        int tempswap = 100; // the current best swap value of dealer's card
        int code = -1; // determines which array of swapnew to use
        // assign set values for certain card arrays
        int[][] swapnew = new int[][] {{23,24,29,25,26,27},{11,13,28,16,18,20},{3,7,28,13,17,20},
                                       {5,6,28,7,9,19},{4,5,28,7,9,19},{6,7,28,8,9,19},{1,5,28,11,15,19},{10,12,14,15,17,22},
                                       {4,8,10,14,18,22},{1,2,3,4,7,21},{1,2,3,6,8,21},{1,2,3,4,5,21},{2,6,9,12,16,21}};
        int [] psuit = new int[4]; // for dealer [suit], length of suit
        for (int i=0+dealer*5; i<5+dealer*5; i++) { // dealer's cards
            psuit[cards[i]%4]++;
            if (cards[i]/4 == 2 && cards[i]%4 == 3-cards[20]%4) { // rank is Jack, suit is next
                psuit[cards[20]%4]++; // trump suit increased by 1
                psuit[3-cards[20]%4]--; // next suit decreased by 1
            }
            if (cards[i]/4 == 5 && cards[i]%4 != cards[20]%4) {
                aced ++; // count dealer's off-suit aces
            }
            if (cards[i]/4 == 4 && cards[i]%4 != cards[20]%4) {
                kingd ++; // count dealer's off-suit kings
            }
        }
        for (int i=0+dealer*5; i<5+dealer*5; i++) { // dealer's cards
            if (seat == 8) { // denotes pre-bidding determination of 5th player's cards
                if (cards[i]%4 == cards[20]%4) {
                    code = 0; // trump suit
                } else if ((cards[i]%4 == 3-cards[20]%4) && psuit[cards[i]%4] > 1) {
                    code = 1; // doubleton+ next suit
                } else if ((cards[i]%4 == 3-cards[20]%4) && psuit[cards[i]%4] < 2) {
                    code = 4; // singleton next suit
                } else if (psuit[cards[i]%4] > 1) {
                    code = 7; // doubleton green suit
                } else {
                    code = 10; // singleton green suit
                }
            } else { // someone has bid and dealer needs to swap cards
                if (cards[i]%4 == cards[20]%4) {
                    code = 0; // trump suit
                } else if ((cards[i]%4 == 3-cards[20]%4) && psuit[cards[i]%4] > 1 &&
                           (seat == 2 || seat == 6)) {
                    code = 2; // doubleton+ next suit AND opponent bidding alone
                } else if ((cards[i]%4 == 3-cards[20]%4) && psuit[cards[i]%4] > 1) {
                    code = 1; // doubleton+ next suit
                } else if ((cards[i]%4 == 3-cards[20]%4) && psuit[cards[i]%4] < 2 &&
                           seat == 0) {
                    code = 3; // singleton next suit AND dealer bidding alone
                } else if ((cards[i]%4 == 3-cards[20]%4) && psuit[cards[i]%4] < 2 &&
                           seat == 1) {
                    code = 4; // singleton next suit AND dealer bidding wp
                } else if ((cards[i]%4 == 3-cards[20]%4) && psuit[cards[i]%4] < 2 &&
                           (seat == 3 || seat == 5 || seat == 7)) {
                    code = 5; // singleton next suit AND any player but dealer bidding wp
                } else if ((cards[i]%4 == 3-cards[20]%4) && psuit[cards[i]%4] < 2 &&
                           (seat == 2 || seat == 6)) {
                    code = 6; // singleton next suit AND opponent bidding alone
                } else if (psuit[cards[i]%4] > 1 && (seat == 2 || seat == 6)) {
                    code = 8; // doubleton+ green suit AND opponent bidding alone
                } else if (psuit[cards[i]%4] > 1) {
                    code = 7; // doubleton+ green suit, all other situations
                } else if (seat == 0) {
                    code = 9; // singleton green suit AND dealer bidding alone
                } else if (seat == 1) {
                    code = 10; // singleton green suit AND dealer bidding wp
                } else if (seat == 2 || seat == 6) {
                    code = 12; // singleton green suit AND opponent bidding alone
                } else {
                    code = 11; // singleton green suit AND any player but dealer bidding wp
                }
            }
            if (swapnew[code][cards[i]/4] < tempswap) {
                tempswap = swapnew[code][cards[i]/4];
                swap = i;
            }
        }
        if (seat == 6 && psuit[3-cards[20]%4] == 1) {
            // 3rd seat going alone, dealer has just one card in next suit
            for (int i=0+dealer*5; i<5+dealer*5; i++) {
                if (cards[i]%4 == 3-cards[20]%4 && cards[i]/4 != 2) { // next suit but NOT L
                    swap = i; // void dealer in next suit; partner will lead next suit if can
                }
            }
        }
        if (seat == 0 && psuit[cards[20]%4] == 2 && aced == 2 && kingd == 1) {
            // dealer bidding alone with 3 trump, holding A and A-K off-suit
            for (int i=0+dealer*5; i<5+dealer*5; i++) { // dealer's cards
                if (cards[i]/4 == 5 && psuit[cards[i]%4] == 1) {
                    swap = i; // discard singleton ace
                }
            }
        }
        return swap; // number of card to be swapped out with turn card
    }

    // *** Method "trump" to determine bidscore for each player / trump suit ***
    public static double trump(int cards[], int post, int tsuit, int turns, int turnr) {
        // input player's cards and seat, trump suit, turn card suit and turn card rank
        double bscore = 0;
        double mx[] = new double[4]; // maxsuit for each suit
        double nx[] = new double[4]; // 2nd highest rank for each suit
        int ps[] = new int[4]; // length of each suit
        int bw = 0; // count of bowers
        int rb = 0; // indicates have R
        int rl = 0; // indicates have L
        int bt = 0; // count of trump higher than turn card
        int ac = 0; // count of off-suit aces
        int vd = 0; // count of void suits
        int oc = 0; // count # of off suits led by K-, and length of such suits
        double[] vtrump = new double [] {2.2,3,9.9,3.9,5,6.2}; // values for various trump cards
        for (int i=0; i<4; i++) { // initialize mx values
            mx[i] = -1;
        }
        for (int i=0; i<5; i++) { // cycle through player's hand
            // count number of bowers
            if (cards[i+post*5]/4 == 2 && cards[i+post*5]%4 == tsuit) {
                rb = 1;// have R
                bw ++;
            }
            if (cards[i+post*5]/4 == 2 && cards[i+post*5]%4 == 3-tsuit) {
                bw ++; // have L
                rl = 1;
            }
            // count number of off-suit aces
            if (cards[i+post*5]/4 == 5 && cards[i+post*5]%4 != tsuit) {
                ac ++; // count one more off-suit ace
            }
            // calculate suit length
            ps[cards[i+post*5]%4]++; // tally length of each suit
            if (cards[i+post*5]%4 == 3-tsuit && cards[i+post*5]/4 == 2) { // card is L
                ps[tsuit]++; // increase length of trump suit
                ps[3-tsuit]--; // decrease length of next suit
            }
            // calculate max rank of each suit, also second highest rank
            if (cards[i+post*5]%4 != 3-tsuit || cards[i+post*5]/4 != 2) {
                // don't count L as max rank
                if (cards[i+post*5]/4 > mx[cards[i+post*5]%4]) { // higher rank
                    nx[cards[i+post*5]%4] = mx[cards[i+post*5]%4];
                    mx[cards[i+post*5]%4] = cards[i+post*5]/4;
                } else if (cards[i+post*5]/4 > nx[cards[i+post*5]%4]) {
                    nx[cards[i+post*5]%4] = cards[i+post*5]/4;
                }
            }
            // incorporate point value of trump cards
            if (cards[i+post*5]%4 == tsuit) {
                bscore += vtrump[cards[i+post*5]/4];
            }
            if (cards[i+post*5]%4 == 3-tsuit && cards[i+post*5]/4 == 2) {
                bscore += 7.7; // incorporate point value of L
            }
            // for 3rd seat, 1st round, count number of trump higher than turn
            if (post == 3 && ((turnr != 2 && cards[i+post*5]/4 > turnr) ||
                              (cards[i+post*5]/4 == 2 && (cards[i+post*5]%4 == turns || cards[i+post*5]%4 == 3-turns)))) {
                bt++;
            }
        }
        for (int i=0; i<4; i++) { // cycle through suits
            if (ps[i] == 0) {
                vd++; // count void suits
                mx[i] = 4.9; // correct mx value for void suits
            }
            if (mx[i] == 5 && ps[i] > 1 && nx[i] < 4) { //
                mx[i] = 4.7; // correct mx value for A-x suits where x != K
            }
        }
        // correction for off-suits
        oc = 0; // reinitialize count
        for (int i=0; i<4; i++) { // cycle through suits
            if (i != tsuit) {
                if (mx[i] > 4.5) { // suit led by A
                    if (ps[i] == 1) { // singleton A
                        bscore += 1.9;
                    } else if (nx[i] == 4) { // A-K...
                        bscore += 2.1; // doubleton A-K
                    } else if (ps[i] == 2) {
                        bscore += 1.2; // doubleton A-x
                    } else if (ps[i] > 0) {
                        bscore += 0.8; // A-x-x
                    }
                }
                if (mx[i] == 4) { // suit led by K
                    if (turnr == 5 && i == turns) { // A turned, and looking at turned suit
                        if (ps[i] == 1) {
                            bscore += 1.9; // treat single K like an A
                        } else {
                            bscore += 1.2; // treat doubleton+ K like an A
                        }
                    } else {
                        bscore -= 0.4; // -0.4 for K-high suit
                        oc++;
                        oc+= ps[i] - 1;
                    }
                }
                if (mx[i] == 3) { // suit led by Q
                    if ((turnr == 5 || turnr == 4) && i == turns) {
                        // A or K turned, and looking at turned suit
                        bscore -= 0.4; // treat like K-high suit
                        oc++;
                        oc+= ps[i] - 1;
                    } else {
                        bscore -= 1.9; // -1.9 for Q-high suit
                        oc++;
                        oc+= ps[i] - 1;
                        if (i == 3-tsuit) {
                            bscore += 0.5; // add back 0.5 for next suit
                        }
                    }
                }
                if (mx[i] == 2 || mx[i] == 1) { // suit led by J or 10
                    bscore -= 2.8; // -2.8 for J- or 10-high suits
                    oc++;
                    oc+= ps[i] - 1;
                    if (i == 3-tsuit) {
                        bscore += 0.5; // add back 0.5 for next suit
                    }
                }
                if (mx[i] == 0) { // singleton 9 suit
                    bscore -= 4; // -4 for singleton 9 suits
                    oc++;
                    oc+= ps[i] - 1;
                    if (i == 3-tsuit) {
                        bscore += 0.5; // add back 0.5 for next suit
                    }
                }
            }
        }
        if (oc != 0) {
            bscore -= (oc - 1); // correct for multiple off-suits K-, or length of these suits 2+
        }
        // correction for multiple off-Aces
        if (ac == 2) {
            bscore += 2.7; // +2.7 pts for 2 off-Aces
        }
        if (ac == 3) {
            bscore += 4.3; // +4.3 pts for 3 off-Aces
        }
        // correction for voids
        if (ps[tsuit] > 2) {
            if (vd == 1) {
                bscore += 0.5; // +0.5 if 1 void AND 3+ trump
            }
            if (vd == 2) {
                bscore += 2; // +2 if 1 void AND 3+ trump
            }
        }
        // correction for trump suit length
        if (ps[tsuit] == 4) {
            bscore++; // +1 if 4 trump
        } else if (ps[tsuit] == 2) {
            if (bw == 2) {
                bscore -= 2; // -2 if 2 trump, both bowers
            } else {
                bscore -= 3.5; // -3.5 if 2 trump, not both bowers
            }
        } else if (ps[tsuit] == 1) {
            bscore -= 7; // -7 if 1 trump
        }
        // correction for bowers
        if (ps[tsuit] > 2 && bw == 2) {
            bscore += 3.4;
        } else if (ps[tsuit] > 2 && rb == 1) {
            bscore++;
        } else if (bw == 0) {
            bscore -= 1.5;
        }
        // correction for no R / no off-Ace
        if (rb == 0 && ac == 0) {
            bscore -= 0.7; // -0.7 if no bower AND no off-Ace
        }
        // special cases
        // 1st seat, value of turn card, 1st round
        if (post == 1 && tsuit == turns) { // 1st round
            if (turnr == 0) {
                bscore += 0.8; // +0.8 if 9 turned
            } else if (turnr == 1) {
                bscore += 0.6; // +0.6 if 10 turned
            } else if (turnr == 3) {
                bscore += 0.4; // +0.4 if Q turned
            } else if (turnr == 4) {
                bscore += 0.2; // +0.2 if K turned
            }
        }
        // 1st seat, trump rank, 1st round
        if (post == 1) {
            if (turnr == 2 && turns == tsuit) {
                bscore -= 1.7; // R turned
            } else if ((rb == 1 || rl == 1) && turns == tsuit) {
                bscore += 1; // have R or L
            } else if (mx[tsuit] != 2 && mx[tsuit] > turnr && turns == tsuit) {
                bscore ++; // +1 if have higher trump than turn
            } else if (turns == tsuit){
                bscore -= 0.7; // -0.7 if have lower trump than turn
            }
        }
        // 1st seat, 2nd round, next bid
        if (tsuit == 3-turns && turnr == 2 && post == 1) {
            bscore += 2; // add 2 if bidding next and R turned
        }
        if (tsuit == 3-turns && post == 1) {
            bscore += 0.5; // add 0.5 if bidding next
        }
        // 1st seat, 2nd round
        if (tsuit != turns && post == 1) { // not turned suit (2nd round bid)
            if (ac == 1) {
                bscore += 1.2; // have 1 off-suit Ace
            } else if (ac == 2) {
                bscore += 2; // have 2 off-suit Aces
            } else if (ac == 3) {
                bscore += 3; // have 3 off-suit Aces
            }
            if (ps[turns] > 0) {
                bscore += 2; // have any cards in next suit
            }
        }
        // 2nd seat, turn suit (1st round)
        if (turnr == 2 && post == 2 && tsuit == turns) {
            bscore += 4; // R turned (1st round)
        } else if (rl == 0 && rb == 0 && turnr != 2 && turnr > mx[turns] && post == 2
                   && tsuit == turns) {
            bscore++; // R not turned AND turn is higher than highest trump in hand
        }
        // 2nd seat, 2nd round, next
        if (turnr == 2 && tsuit == 3-turns && post == 2) {
            bscore += 3; // R turned and bidding next suit (2nd round)
        }
        // 3rd seat, first round
        if (post == 3 && ps[turns] > 2) {
            if (bt > 2) {
                bscore += 5.5; // have 3 trump higher than turn
            } else if (bt == 2) {
                bscore += 2; // have 2 trump higher than turn
            }
        }
        // dealer, 2nd round, next suit
        if (post == 0 && tsuit == 3-turns && turnr == 2) {
            bscore += 3; // +3 points if R turned and bidding next suit
        }
        return bscore;
    }

    // *** Method "calc" to calculate values of cv[lead][][] and cv[toss][][] ***
    public static double[][][] calc(int cards[], int fintp, int own[][][], int playst[][], double max[][][],
                                    double nax[][][], int lone, int dealer) {
        double[][] discard = new double[][] {{10,11,0,12,13,14,15,16},{.71,.82,0,.91,.93,1.15,0,0},
                                             {.7,.8,.84,.9,.92,1.2,0,0}};
        // assign set values for certain cards
        double[][][] cvtemp = new double[2][4][8];
        // calculate cv values for established trump
        for (int k=0; k<2; k++) {
            for (int i=0; i<24; i++) { // cycle through cards
                if (cards[i]%4 == fintp) { // trump suit
                    cvtemp[k][cards[i]%4][cards[i]/4] = discard[0][cards[i]/4];
                } else if (cards[i]%4 == 3-fintp) { // next suit
                    cvtemp[k][cards[i]%4][cards[i]/4] = discard[1][cards[i]/4];
                } else { // green suit
                    cvtemp[k][cards[i]%4][cards[i]/4] = discard[2][cards[i]/4];
                }
            }
        }
        // adjust for singleton suits
        for (int k=0; k<4; k++) { // player
            for (int i=0; i<4; i++) { // suit
                for (int j=0; j<8; j++) { // rank
                    if (own[k][i][j] > -1 && own[k][i][j] < 4) {
                        if (i != fintp && playst[own[k][i][j]][i] == 1) { // non-trump, singleton suit
                            if (i == 3-fintp) { // next suit, every card but L
                                cvtemp[1][i][j] = cvtemp[1][i][j] - 0.01;
                                cvtemp[0][i][j] = Math.pow(cvtemp[0][i][j],5); // raw value raised to 5th power
                            } else { // green suit, every card
                                cvtemp[1][i][j] = cvtemp[1][i][j] - 0.01;
                                cvtemp[0][i][j] = Math.pow(cvtemp[0][i][j],10); // raw value raised to 10th power
                            }
                        }
                    }
                }
            }
        }
        for (int k=0; k<4; k++) { // player
            for (int i=0; i<4; i++) { // suit
                if ((max[k][fintp][i] == 5 || max[k][fintp][i] == 4.7) && i != fintp) {
                    if (playst[k][i] > 2) { // if length of suit headed by Ace is 3+, reduce value of the ace
                        // by 0.06 (not a good lead)
                        cvtemp[0][i][5] -= 0.06;
                    }
                    if (playst[k][i] > 1 && playst[k][fintp] == 0) {
                        for (int j=0; j<4; j++) {
                            if (own[k][i][j] == k) {
                                // if have no trump and A-x or A-x-x, make x the card to throw off (if Q or lower)
                                cvtemp[1][i][j] = cvtemp[1][i][j]/3;
                            }
                        }
                    }
                }
                if (max[k][fintp][i] == 4 && i != fintp) { // non-trump suit headed by K
                    if (playst[k][i] == 2) { // have K-x
                        if (own[k][i][3] == k) { // player has K-Q
                            cvtemp[0][i][4] = cvtemp[0][i][4]/2; // K value halved, so better lead (lower value)
                        }
                        for (int j=0; j<3; j++) {
                            if (own[k][i][j] == k) {
                                cvtemp[0][i][j] = cvtemp[0][i][j]/2; // if x value is J or lower, 1/2 it so better lead
                            }
                        }
                    }
                }
            }
        }
        // adjust cvtoss values for 9-10 combos (worthless if not signaling or voiding)
        for (int k=0; k<4; k++) { // player
            for (int i=0; i<4; i++) { // suit
                if (max[k][fintp][i] == 1 && playst[k][i] == 2 && i != fintp) {
                    cvtemp[1][i][0] = .695;
                    cvtemp[1][i][1] = .695;
                }
            }
        }
        // if a player going lone, re-evaluate cv[1][][] values for defending players
        //(want to keep best 2-card off suit)
        if (lone > -1) {
            int a = 0;
            if (lone == 0 || lone == 2) {
                a = 1;
            }
            for (int i=a; i<4; i=i+2) { // two players who are defending against lone
                for (int j=i*5; j<i*5+5; j++) { // cycle through the 5 cards held by the defender
                    if (cards[j]/4 == 5) { // card is an Ace
                        if (cards[j]%4 == 3-fintp) { // suit is next
                            if (playst[i][3-fintp] == 1) {
                                cvtemp[1][3-fintp][5] = 1.04; // singleton Ace value
                            } else if (playst[i][3-fintp] == 2) {
                                cvtemp[1][3-fintp][5] = 1.02; // doubleton Ace value
                            } else {
                                cvtemp[1][3-fintp][5] = 1; // tripleton+ Ace value
                            }
                        } else if (cards[j]%4 != fintp) { // suit is green
                            if (playst[i][cards[j]%4] == 1) {
                                cvtemp[1][cards[j]%4][5] = 1.05; // singleton Ace value
                            } else if (playst[i][cards[j]%4] == 2) {
                                cvtemp[1][cards[j]%4][5] = 1.03; // doubleton Ace value
                            } else {
                                cvtemp[1][cards[j]%4][5] = 1.01; // tripleton+ Ace value
                            }
                        }
                    } else if (cards[j]/4 == 4 && cards[j]%4 != fintp) { // card is a K
                        if (cards[j]%4 == 3-fintp && playst[i][3-fintp] == 1) {
                            cvtemp[1][3-fintp][4] = .96; // singleton next suit K value
                        } else if (playst[i][cards[j]%4] == 1) {
                            cvtemp[1][cards[j]%4][4] = .98; // singleton green suit K value
                        } else if (nax[i][fintp][cards[j]%4] == 4) {
                            cvtemp[1][cards[j]%4][4] = .4; // K value when have Ace in the same suit
                        } else if (cards[j]%4 == 3-fintp) { // next suit
                            if (playst[i][3-fintp] == 2 && nax[i][fintp][3-fintp] == 0) {
                                cvtemp[1][3-fintp][4] = .86; // doubleton next suit K value (K-9)
                            } else if (playst[i][3-fintp] == 2 && nax[i][fintp][3-fintp] == 1) {
                                cvtemp[1][3-fintp][4] = .84; // doubleton next suit K value (K-10)
                            } else if (playst[i][3-fintp] == 2 && nax[i][fintp][3-fintp] == 3) {
                                cvtemp[1][3-fintp][4] = .66; // doubleton next suit K value (K-Q)
                            } else if (playst[i][3-fintp] == 3 && nax[i][fintp][3-fintp] == 1) {
                                cvtemp[1][3-fintp][4] = .59; // tripleton next suit K value (K-10-9)
                            } else if (playst[i][3-fintp] == 3 && nax[i][fintp][3-fintp] == 3) {
                                cvtemp[1][3-fintp][4] = .48; // tripleton next suit K value (K-Q-x)
                            }
                        } else if (cards[j]%4 != fintp) { // green suit
                            if (playst[i][cards[j]%4] == 2 && nax[i][fintp][cards[j]%4] == 0) {
                                cvtemp[1][cards[j]%4][4] = .95; // doubleton green suit K value (K-9)
                            } else if (playst[i][cards[j]%4] == 2 && nax[i][fintp][cards[j]%4] == 1) {
                                cvtemp[1][cards[j]%4][4] = .93; // doubleton green suit K value (K-10)
                            } else if (playst[i][cards[j]%4] == 2 && nax[i][fintp][cards[j]%4] == 2) {
                                cvtemp[1][cards[j]%4][4] = .91; // doubleton green suit K value (K-J)
                            } else if (playst[i][cards[j]%4] == 2 && nax[i][fintp][cards[j]%4] == 3) {
                                cvtemp[1][cards[j]%4][4] = .89; // doubleton green suit K value (K-Q)
                            } else if (playst[i][cards[j]%4] == 3 && (nax[i][fintp][cards[j]%4] == 2 ||
                                                                      nax[i][fintp][cards[j]%4] == 1)) {
                                cvtemp[1][cards[j]%4][4] = .82; // tripleton green suit K value (K-J-x or K-10-9)
                            } else if (playst[i][cards[j]%4] == 3 && nax[i][fintp][cards[j]%4] == 3) {
                                cvtemp[1][cards[j]%4][4] = .7; // tripleton green suit K value (K-Q-x)
                            }
                        }
                    } else if (cards[j]/4 == 3 && cards[j]%4 != fintp) { // card is a Q
                        if (max[i][fintp][cards[j]%4] > 4.2 && nax[i][fintp][cards[j]%4] == 3) {
                            cvtemp[1][cards[j]%4][3] = .4; // Q value when have Ace in the same suit and not K
                        } else if (playst[i][cards[j]%4] == 3 && max[i][fintp][cards[j]%4] > 4.2 &&
                                   nax[i][fintp][cards[j]%4] == 4) {
                            cvtemp[1][cards[j]%4][3] = .38; // Q value when have A-K-Q
                        } else if (cards[j]%4 == 3-fintp) { // suit is next
                            if (playst[i][3-fintp] == 1) {
                                cvtemp[1][3-fintp][3] = .67; // singleton Q value
                            } else if (playst[i][3-fintp] == 2) {
                                if (max[i][fintp][3-fintp] == 4) {
                                    cvtemp[1][3-fintp][3] = .65; // Q value when have K-Q
                                } else if (nax[i][fintp][3-fintp] == 0) {
                                    cvtemp[1][3-fintp][3] = .5; // Q value when have Q-9
                                } else if (nax[i][fintp][3-fintp] == 1) {
                                    cvtemp[1][3-fintp][3] = .44; // Q value when have Q-10
                                }
                            } else if (playst[i][3-fintp] == 3) {
                                if (max[i][fintp][3-fintp] == 4) {
                                    cvtemp[1][3-fintp][3] = .48; // Q value when have K-Q-x
                                } else if (max[i][fintp][3-fintp] == 3) {
                                    cvtemp[1][3-fintp][3] = .36; // Q value when have Q-10-9
                                }
                            }
                        } else if (cards[j]%4 != fintp) { // green suit
                            if (playst[i][cards[j]%4] == 1) {
                                cvtemp[1][cards[j]%4][3] = .87; // singleton Q value
                            } else if (playst[i][cards[j]%4] == 2) {
                                if (max[i][fintp][cards[j]%4] == 4) {
                                    cvtemp[1][cards[j]%4][3] = .88; // Q value when have K-Q
                                } else if (nax[i][fintp][cards[j]%4] == 0) {
                                    cvtemp[1][cards[j]%4][3] = .75; // Q value when have Q-9
                                } else if (nax[i][fintp][cards[j]%4] == 1) {
                                    cvtemp[1][cards[j]%4][3] = .73; // Q value when have Q-10
                                } else if (nax[i][fintp][cards[j]%4] == 2) {
                                    cvtemp[1][cards[j]%4][3] = .63; // Q value when have Q-J
                                }
                            } else if (playst[i][cards[j]%4] == 3) {
                                if (max[i][fintp][cards[j]%4] == 4) {
                                    cvtemp[1][cards[j]%4][3] = .68; // Q value when have K-Q-x
                                } else if (max[i][fintp][cards[j]%4] == 3) {
                                    if (nax[i][fintp][cards[j]%4] == 1) {
                                        cvtemp[1][cards[j]%4][3] = .57; // Q value when have Q-10-9
                                    } else if (nax[i][fintp][cards[j]%4] == 2) {
                                        cvtemp[1][cards[j]%4][3] = .46; // Q value when have Q-J-x
                                    }
                                }
                            }
                        }
                    } else if (cards[j]/4 == 2 && cards[j]%4 != fintp) { // card is a J (only green suit)
                        if (max[i][fintp][cards[j]%4] > 4.2 && nax[i][fintp][cards[j]%4] == 2) {
                            cvtemp[1][cards[j]%4][2] = .4; // J value when have Ace in suit and J is 2nd highest
                        } else if (playst[i][cards[j]%4] == 1) {
                            cvtemp[1][cards[j]%4][2] = .64; // J value for singleton J
                        } else if (playst[i][cards[j]%4] == 2) {
                            if (max[i][fintp][cards[j]%4] == 4) {
                                cvtemp[1][cards[j]%4][2] = .9; // J value when have K-J
                            } else if (max[i][fintp][cards[j]%4] == 3) {
                                cvtemp[1][cards[j]%4][2] = .62; // J value when have Q-J
                            } else if (nax[i][fintp][cards[j]%4] == 0) {
                                cvtemp[1][cards[j]%4][2] = .47; // J value when have J-9
                            } else if (nax[i][fintp][cards[j]%4] == 1) {
                                cvtemp[1][cards[j]%4][2] = .42; // J value when have J-10
                            }
                        } else if (playst[i][cards[j]%4] == 3) {
                            if ((max[i][fintp][cards[j]%4] > 4.2 || max[i][fintp][cards[j]%4] == 4) &&
                                (nax[i][fintp][cards[j]%4] == 4 || nax[i][fintp][cards[j]%4] == 3)) {
                                cvtemp[1][cards[j]%4][2] = .38; // J value when have A-K-J or A-Q-J or K-Q-J
                            } else if (max[i][fintp][cards[j]%4] == 4 && nax[i][fintp][cards[j]%4] == 2) {
                                cvtemp[1][cards[j]%4][2] = .8; // J value when have K-J-x
                            } else if (max[i][fintp][cards[j]%4] == 3 && nax[i][fintp][cards[j]%4] == 2) {
                                cvtemp[1][cards[j]%4][2] = .46; // J value when have Q-J-x
                            } else if (max[i][fintp][cards[j]%4] == 2) {
                                cvtemp[1][cards[j]%4][2] = .36; // J value when have J-10-9
                            }
                        }
                    } else if (cards[j]/4 == 1 && cards[j]%4 != fintp) { // card is a 10
                        if (max[i][fintp][cards[j]%4] > 4.2 && nax[i][fintp][cards[j]%4] == 1) {
                            cvtemp[1][cards[j]%4][1] = .4; // 10 value when have A-10 or A-10-9
                        } else if ((max[i][fintp][cards[j]%4] > 4.2 && nax[i][fintp][cards[j]%4] != 1) ||
                                   (max[i][fintp][cards[j]%4] == 4 && nax[i][fintp][cards[j]%4] != 1)) {
                            cvtemp[1][cards[j]%4][1] = .38; // 10 value when have A-x-10 or K-x-10
                        } else if (cards[j]%4 == 3-fintp) { // suit is next
                            if (playst[i][3-fintp] == 1) {
                                cvtemp[1][3-fintp][1] = .44; // 10 value for singleton 10
                            } else if (playst[i][3-fintp] == 2) {
                                if (max[i][fintp][3-fintp] == 4) {
                                    cvtemp[1][3-fintp][1] = .83; // 10 value when have K-10
                                } else if (max[i][fintp][3-fintp] == 3) {
                                    cvtemp[1][3-fintp][1] = .44; // 10 value when have Q-10
                                } else if (max[i][fintp][3-fintp] == 1) {
                                    cvtemp[1][3-fintp][1] = .35; // 10 value when have 10-9
                                }
                            } else if (playst[i][3-fintp] == 3) {
                                if (max[i][fintp][3-fintp] == 4 && nax[i][fintp][3-fintp] == 1) {
                                    cvtemp[1][3-fintp][1] = .58; // 10 value when have K-10-9
                                } else if (max[i][fintp][3-fintp] == 3 && nax[i][fintp][3-fintp] == 1) {
                                    cvtemp[1][3-fintp][1] = .36; // 10 value when have Q-10-9
                                }
                            }
                        } else if (cards[j]%4 != fintp) { // green suit
                            if (playst[i][cards[j]%4] == 1) {
                                cvtemp[1][cards[j]%4][1] = .42; // 10 value for singleton 10
                            } else if (playst[i][cards[j]%4] == 2) {
                                if (max[i][fintp][cards[j]%4] == 4) {
                                    cvtemp[1][cards[j]%4][1] = .92; // 10 value when have K-10
                                } else if (max[i][fintp][cards[j]%4] == 3) {
                                    cvtemp[1][cards[j]%4][1] = .72; // 10 value when have Q-10
                                } else if (max[i][fintp][cards[j]%4] == 2) {
                                    cvtemp[1][cards[j]%4][1] = .42; // 10 value when have J-10
                                } else if (max[i][fintp][cards[j]%4] == 1) {
                                    cvtemp[1][cards[j]%4][1] = .35; // 10 value when have 10-9
                                }
                            } else if (playst[i][cards[j]%4] == 3) {
                                if (max[i][fintp][cards[j]%4] == 4 && nax[i][fintp][cards[j]%4] == 1) {
                                    cvtemp[1][cards[j]%4][1] = .8; // 10 value when have K-10-9
                                } else if (max[i][fintp][cards[j]%4] == 3 && nax[i][fintp][cards[j]%4] == 1) {
                                    cvtemp[1][cards[j]%4][1] = .56; // 10 value when have Q-10-9
                                } else if (max[i][fintp][cards[j]%4] == 3 && nax[i][fintp][cards[j]%4] == 2) {
                                    cvtemp[1][cards[j]%4][1] = .46; // 10 value when have Q-J-10
                                } else if (max[i][fintp][cards[j]%4] == 1) {
                                    cvtemp[1][cards[j]%4][1] = .36; // 10 value when have J-10-9
                                }
                            }
                        }
                    } else if (cards[j]/4 == 0 && cards[j]%4 != fintp) { // card is a 9
                        if (playst[i][cards[j]%4] == 1) {
                            cvtemp[1][cards[j]%4][0] = .34; // 9 value for singleton
                        } else if (max[i][fintp][cards[j]%4] > 4.2) {
                            cvtemp[1][cards[j]%4][0] = .4; // 9 value when A-9
                        } else if ((max[i][fintp][cards[j]%4] > 4.2 || max[i][fintp][cards[j]%4] > 4) &&
                                   (playst[i][cards[j]%4] == 3)) {
                            cvtemp[1][cards[j]%4][0] = .38; // 9 value when A-x-9 or K-x-9
                        } else if (playst[i][cards[j]%4] == 2) {
                            if (cards[j]%4 == 3-fintp) { // next suit
                                if (max[i][fintp][cards[j]%4] == 4) {
                                    cvtemp[1][cards[j]%4][0] = .85; // 9 value when have K-9
                                } else if (max[i][fintp][cards[j]%4] == 3) {
                                    cvtemp[1][cards[j]%4][0] = .49; // 9 value when have Q-9
                                } else if (max[i][fintp][cards[j]%4] == 1) {
                                    cvtemp[1][cards[j]%4][0] = .35; // 9 value when have 10-9
                                }
                            } else if (cards[j]%4 != fintp) { // green suit
                                if (max[i][fintp][cards[j]%4] == 4) {
                                    cvtemp[1][cards[j]%4][0] = .94; // 9 value when have K-9
                                } else if (max[i][fintp][cards[j]%4] == 3) {
                                    cvtemp[1][cards[j]%4][0] = .74; // 9 value when have Q-9
                                } else if (max[i][fintp][cards[j]%4] == 2) {
                                    cvtemp[1][cards[j]%4][0] = .46; // 9 value when have J-9
                                } else if (max[i][fintp][cards[j]%4] == 1) {
                                    cvtemp[1][cards[j]%4][0] = .35; // 9 value when have 10-9
                                }
                            }
                        } else if (playst[i][cards[j]%4] == 3) {
                            if (cards[j]%4 == 3-fintp) { // next suit
                                if (max[i][fintp][cards[j]%4] == 4 && nax[i][fintp][cards[j]%4] == 3) {
                                    cvtemp[1][cards[j]%4][0] = .45; // 9 value when have K-Q-9
                                } else if (max[i][fintp][cards[j]%4] == 3) {
                                    cvtemp[1][cards[j]%4][0] = .36; // 9 value when have Q-10-9
                                }
                            } else if (cards[j]%4 != fintp) { // green suit
                                if (max[i][fintp][cards[j]%4] == 3 && nax[i][fintp][cards[j]%4] == 1) {
                                    cvtemp[1][cards[j]%4][0] = .38; // 9 value when have Q-10-9
                                } else if (max[i][fintp][cards[j]%4] == 3 && nax[i][fintp][cards[j]%4] == 2) {
                                    cvtemp[1][cards[j]%4][0] = .45; // 9 value when have Q-J-9
                                } else if (max[i][fintp][cards[j]%4] == 2) {
                                    cvtemp[1][cards[j]%4][0] = .36; // 9 value when have J-10-9
                                }
                            }
                        }
                    }
                }
            }
            // calculate 'beat' values for 2 defending players / suit
            // will not be accurate for trump suit AND any suit led by an Ace
            double beat[][] = new double [4][4];
            for (int i=a; i<4; i=i+2) { // two players who are defending against lone
                for (int j=0; j<4; j++) { // suit
                    if (max[i][fintp][j] < 4.1) { // K high or worse
                        beat[i][j] = max[i][fintp][j] - playst[i][j] + 1;
                        if (j == 3-fintp && max[i][fintp][j] > 2) {
                            beat[i][j]--; // correction for next suit (no J)
                        }
                    } else if (max[i][fintp][j] > 4.1) {
                        beat[i][j] = -1; // beat value for suits led by an Ace
                    }
                    if (j == fintp) {
                        beat[i][j] = -2; // beat value for trump suit
                    }
                }
            }
            // determine the number of singleton, doubleton and tripleton off-suits for 2 defending players
            int sig[] = new int [4]; // count of singleton suits
            int dub[] = new int [4]; // count of doubleton suits
            int trip[] = new int [4]; // count of tripleton suits
            for (int i=a; i<4; i=i+2) { // two players who are defending against lone
                for (int j=0; j<4; j++) { // suit
                    if (playst[i][j] == 1 && j != fintp) {
                        sig[i]++;
                    } else if (playst[i][j] == 2 && j != fintp) {
                        dub[i]++;
                    } else if (playst[i][j] == 3 && j != fintp) {
                        trip[i]++;
                    }
                }
            }
            // determine scenario for 2 defending players
            int scen[] = new int [4];
            for (int i=a; i<4; i=i+2) { // two players who are defending against lone
                if (sig[i] == 1 && dub[i] == 0 && trip[i] == 0) {
                    scen[i] = 1;
                } else if (sig[i] == 2 && dub[i] == 0 && trip[i] == 0) {
                    scen[i] = 2;
                } else if (sig[i] == 0 && dub[i] == 1 && trip[i] == 0) {
                    scen[i] = 3;
                } else if (sig[i] == 0 && dub[i] == 0 && trip[i] == 1) {
                    scen[i] = 4;
                } else if (sig[i] == 3 && dub[i] == 0 && trip[i] == 0) {
                    scen[i] = 5;
                } else if (sig[i] == 1 && dub[i] == 1 && trip[i] == 0) {
                    scen[i] = 6;
                } else if (sig[i] == 1 && dub[i] == 0 && trip[i] == 1) {
                    scen[i] = 7;
                } else if (sig[i] == 0 && dub[i] == 2 && trip[i] == 0) {
                    scen[i] = 8;
                } else if (sig[i] == 2 && dub[i] == 1 && trip[i] == 0) {
                    scen[i] = 9;
                } else if (sig[i] == 2 && dub[i] == 0 && trip[i] == 1) {
                    scen[i] = 10;
                } else if (sig[i] == 0 && dub[i] == 1 && trip[i] == 1) {
                    scen[i] = 11;
                } else if (sig[i] == 1 && dub[i] == 2 && trip[i] == 0) {
                    scen[i] = 12;
                }
            }
            // determine best singleton card for 2 defending players
            int bsig[] = new int [4]; // card number for each [player] which is best singleton to play
            int tempsig[] = new int [4]; // best bsig card so far (need to look at all 5 cards)
            int tempsigval[] = new int [4]; // value of best bsig card so far (ranges from 9 for green K to 1 for next 9)
            for (int i=0; i<4; i++) {
                bsig[i] = -1; // initialize to -1
                tempsig[i] = -1; // initialize to -1
            }
            for (int i=a; i<4; i=i+2) { // two players who are defending against lone
                for (int j=i*5; j<i*5+5; j++) { // card
                    if (playst[i][cards[j]%4] == 1 && cards[j]%4 != fintp) { // non-trump singletons
                        if (cards[j]/4 == 4 && cards[j]%4 == 3-fintp && (tempsig[i] == -1 || tempsigval[i] < 8)) {
                            tempsig[i] = j; // next K may be best singleton
                            tempsigval[i] = 8;
                        } else if (cards[j]/4 == 4 && (tempsig[i] == -1 || tempsigval[i] < 9)) {
                            tempsig[i] = j; // green K may be best singleton
                            tempsigval[i] = 9;
                        } else if (cards[j]/4 == 3 && cards[j]%4 == 3-fintp && (tempsig[i] == -1 || tempsigval[i] < 6)) {
                            tempsig[i] = j; // next Q may be best singleton
                            tempsigval[i] = 6;
                        } else if (cards[j]/4 == 3 && (tempsig[i] == -1 || tempsigval[i] < 7)) {
                            tempsig[i] = j; // green Q may be best singleton
                            tempsigval[i] = 7;
                        } else if (cards[j]/4 == 2 && cards[j]%4 != 3-fintp && (tempsig[i] == -1 || tempsigval[i] < 5)) {
                            tempsig[i] = j; // green J may be best singleton
                            tempsigval[i] = 5;
                        } else if (cards[j]/4 == 1 && cards[j]%4 == 3-fintp && (tempsig[i] == -1 || tempsigval[i] < 3)) {
                            tempsig[i] = j; // next 10 may be best singleton
                            tempsigval[i] = 3;
                        } else if (cards[j]/4 == 1 && (tempsig[i] == -1 || tempsigval[i] < 4)) {
                            tempsig[i] = j; // green 10 may be best singleton
                            tempsigval[i] = 4;
                        } else if (cards[j]/4 == 0 && cards[j]%4 == 3-fintp && (tempsig[i] == -1 || tempsigval[i] < 1)) {
                            tempsig[i] = j; // next 9 may be best singleton
                            tempsigval[i] = 1;
                        } else if (cards[j]/4 == 0 && (tempsig[i] == -1 || tempsigval[i] < 2)) {
                            tempsig[i] = j; // green 9 may be best singleton
                            tempsigval[i] = 2;
                        }
                    }
                }
                if (tempsig[i] != -1) {
                    bsig[i] = tempsig[i]; // definitively determine best singleton
                }
            }
            // calculate best card to lead for two defending players (depends on scenario)
            for (int i=a; i<4; i=i+2) { // two players who are defending against lone
                if ((scen[i] == 1 || scen[i] == 2 || scen[i] == 5 || scen[i] == 9) && bsig[i] != -1) {
                    cvtemp[0][cards[bsig[i]]%4][cards[bsig[i]]/4] = .05; // set value of best singleton to 0.05
                } else if (scen[i] == 6 || scen[i] == 7) {
                    if (bsig[i] != -1) {
                        cvtemp[0][cards[bsig[i]]%4][cards[bsig[i]]/4] = .05; // set value of best singleton to 0.05,
                        //because is not an Ace
                    } else {
                        for (int j=i*5; j<i*5+5; j++) { // card
                            if (cards[j]%4 != fintp && playst[i][cards[j]%4] > 1 &&
                                max[i][fintp][cards[j]%4] == cards[j]/5 && cards[j]/5 != 5) {
                                cvtemp[0][cards[j]%4][cards[j]/4] = .05;
                                // set value of highest card from doubleton or tripleton to 0.05 (if not an Ace)
                            }
                        }
                    }
                } else if (scen[i] == 3 || scen[i] == 4) {
                    for (int j=i*5; j<i*5+5; j++) { // card
                        if (cards[j]%4 != fintp && playst[i][cards[j]%4] > 0 &&
                            max[i][fintp][cards[j]%4] == cards[j]/5 && cards[j]/5 != 5) {
                            cvtemp[0][cards[j]%4][cards[j]/4] = .05; // set value of highest card from lone off-suit
                            // to 0.05 (if not an Ace)
                        }
                    }
                } else if (scen[i] == 8 || scen[i] == 11) {
                    double tempbeat[] = new double [4]; // best beat value so far for [player]
                    int tempcard[] = new int [4]; // number of card with best beat value so far
                    for (int k=0; k<4; k++) {
                        tempcard[k] = 10;
                        tempbeat[k] = 10;
                    }
                    for (int j=i*5; j<i*5+5; j++) { // card
                        if (cards[j]%4 != fintp && max[i][fintp][cards[j]%4] == cards[j]/4 &&
                            (beat[i][cards[j]%4] < tempbeat[i] || (beat[i][cards[j]%4] == tempbeat[i] &&
                                                                   cards[j]%4 != 3-fintp && cards[tempcard[i]]%4 == 3-fintp) ||
                             (beat[i][cards[j]%4] == beat[i][tempcard[i]%4] &&
                              cards[j]%4 != 3-fintp && cards[tempcard[i]]%4 != 3-fintp &&
                              playst[i][cards[j]%4] == 3))) {
                            tempbeat[i] = beat[i][cards[j]%4];
                            tempcard[i] = j;
                        }
                    }
                    if (tempbeat[i] != 10) {
                        cvtemp[0][cards[tempcard[i]]%4][cards[tempcard[i]]/4] = .05;
                        // set value of highest card from worse off-suit to 0.05 (if not an Ace)
                    }
                } else if (scen[i] == 10) {
                    double tempbeat[] = new double [4];
                    int tempcard[] = new int [4];
                    for (int k=0; k<4; k++) {
                        tempcard[k] = 10;
                        tempbeat[k] = 10;
                    }
                    for (int j=i*5; j<i*5+5; j++) { // card
                        if (cards[j]%4 != fintp && max[i][fintp][cards[j]%4] == cards[j]/5 &&
                            (beat[i][cards[j]%4] < tempbeat[i] || (beat[i][cards[j]%4] == tempbeat[i] &&
                                                                   playst[i][cards[j]%4] == 1 && playst[i][cards[tempcard[i]]%4] == 3) ||
                             (beat[i][cards[j]%4] == beat[i][tempcard[i]%4] && playst[i][cards[j]%4] == 1 &&
                              playst[i][cards[tempcard[i]]%4] == 1 && cards[j]%4 == 3-fintp &&
                              cards[tempcard[i]]%4 != 3-fintp))) {
                            tempbeat[i] = beat[i][cards[j]%4];
                            tempcard[i] = j;
                        }
                    }
                    if (tempbeat[i] != 10) {
                        cvtemp[0][cards[tempcard[i]]%4][cards[tempcard[i]]/4] = .05;
                        // set value of highest card from worse off-suit to 0.05 (if not an Ace)
                    }
                } else if (scen[i] == 12) {
                    double tempbeat[] = new double [4]; // rank of highest ranked card
                    int tempcard[] = new int [4]; // highest ranked card (so far)
                    for (int k=0; k<4; k++) {
                        tempcard[k] = 10;
                        tempbeat[k] = -1;
                    }
                    for (int j=i*5; j<i*5+5; j++) { // card
                        if (cards[j]%4 != fintp && max[i][fintp][cards[j]%4] == cards[j]/5 &&
                            (max[i][fintp][cards[j]%4] > tempbeat[i] || (max[i][fintp][cards[j]%4] == tempbeat[i] &&
                                                                         playst[i][cards[j]%4] > 1 && playst[i][cards[tempcard[i]]%4] == 1) ||
                             (max[i][fintp][cards[j]%4] == tempbeat[i] && playst[i][cards[j]%4] > 1 &&
                              playst[i][cards[tempcard[i]]%4] > 1 &&
                              cards[j]%4 == 3-fintp && cards[tempcard[i]]%4 != 3-fintp))) {
                            tempbeat[i] = cards[j]/5;
                            tempcard[i] = j;
                        }
                    }
                    if (tempbeat[i] != -1) {
                        cvtemp[0][cards[tempcard[i]]%4][cards[tempcard[i]]/4] = .05;
                        // set value of highest card from worse off-suit to 0.05 (if not an Ace)
                    }
                }
            }
        }
        return cvtemp;
    }

    public static void main(String args[]) throws InterruptedException {
        int nGames = 1;
        if (args.length > 0) {
            rgen.setSeed(Long.parseLong(args[0]));

            if (args.length > 1) {
                nGames = Integer.parseInt(args[1]);
            }
        }

        for (int i=0; i<nGames; i++) {
            EuchreBeta.go();
        }
    }

    public static void go() {

        // Variables for shell program
        int declarer = 0;  //  marker denoting which player is declarer (0 = South, 1 = West, 2 = North, 3 = East)
        int dealer = 0;  // which player is dealer for current hand (0 = South, etc.)
        int docall = 0; // how computer player bids
        int top = 0; // points of currently winning team
        int round = 0; // bid 1st round (0) or 2nd round (1)
        int lone = -1; // will get value if a player goes lone (0 = South, etc.)
        int[] points = new int[4];  // points for each player
        int[] trick = new int[4]; // tricks won
        int seat = -1; // see notes (pertains to bidding situation)
        int fintp = 4;  //  marker denoting the suit which is declared trump (spades = 0, hearts = 1, diamonds = 2,
                        // clubs = 3, 4 = trump not declared)

        // Assign random seat as first dealer
        int randomDealer = rgen.nextInt(4);
        dealer = randomDealer;

        // these variables don't reset when new hand is played (same game)
        int pns = 0; // N/S game pts
        int pew = 0; // E/W game pts

        while (top < game) { // play until one team reaches the threshold winning score
            lone = -1; // reset value of 'lone'
            round = 0; // reset value of 'round'
            declarer = -1; // reset value of 'declarer'
            fintp = 4; // reset value of 'fintp'
            dealer = dealer%4;
            final int aa = (dealer+1)%4; // position after dealer
            final int bb = (dealer+2)%4; // position of dealer's partner
            final int cc = (dealer+3)%4; // position before dealer
            final int dd = dealer; // dealer

            // Reset trick count to zero
            for (int i=0; i<4; i++) {
                trick[i] = 0;
            }

            // initialize card values for deck
            for (int i=0; i<24; i++) {
                cards[i] = i;
            }

            // invoke method for shuffling cards
            cards = shuffle(cards);

            // print out shuffled deck for diagnostic purposes
            for (int i=0; i<24; i++) {
                System.out.println(cards[i]);
            }

            // invoke method for naming cards
            cardname = naming(cardname,suitx,rankx);

            // invoke method to put cards in order
            cards = order(cards, fintp);

            // Create arrays to store cards for each player (value of 1 means player has the card)
            int[][][] own = new int[4][4][8]; // keeps track of where each card is, from [player]'s perspective
            // [player][suit][rank]
            int[][] right = new int[5][4]; // =1 if [player][trumpsuit] has the right bower
            int[][] left = new int[5][4]; // =1 if [player][trumpsuit] has the left bower
            int[][] acet = new int[5][4]; // =1 if [player][trumpsuit] has the ace of trump
            int[][] kingt = new int[5][4]; // =1 if [player][trumpsuit] has the king of trump

            int[][] ace = new int[4][4]; // length of suit headed by non-trump A for [player][suit]
            int[][] king = new int[4][4]; // [player][suit], =0 if no king, =1 if singleton K, =2 if K-x, =3 if K-x-x+
            int[][] queen = new int[4][4]; // [player][suit], =0 if no queen, =1 if singleton Q, =2 if Q-x, =3 if Q-x-x+
            int[][] aces = new int[5][4]; // counts non-trump aces for each [player][trumpsuit]
            int[][] length = new int[4][4]; // records length in play (potentially) of all [suits] once trump is declared
            // (4 to 7)

            //  Create arrays to show which suits each player has, and how many, for each potential trump suit
            int[][][] playersuit = new int[5][4][4];  //  [player][trumpsuit][suit]
            int[][] playst = new int[4][4]; // [player][suit] length of suits once trump has been established

            //  Create array to keep track of the boss card in each suit (potentially remaining in play)
            int[][] boss = new int[4][4]; // [player][suit] = rank of card which is potentially boss (0 - 7)
            //  Create variables to help calculate trump score for each player / suit (0 = spade, 1 = heart,
            // 2 = diamond, 3 = club)
            double bidscore[][] = new double[5][4];
            double[][][] cv = new double[2][4][8]; // assign values to cards [lead or toss][suit][rank]

            //  Initialize counter for who is dealer (starts with South player at 0) and bidding position
            int win1 = 5;  // winner of 1st trick
            int win2 = 5;  // winner of 2nd trick
            int win3 = 5;  // winner of 3rd trick
            int win4 = 5;  // winner of 4th trick
            int win5 = 5;  // winner of 5th trick

            //  Create arrays to keep track of play of cards; [x][y], where x = trick and y = player
            int[][] suit = new int[5][4]; // track suit played [trick][player] = 0 - 3
            int[][] rank = new int[5][4]; // track rank of card played [trick][player] = 0 - 7
            final int upst = cards[20]%4; // suit of turned card
            final int uprk = cards[20]/4; // rank of turned card
            double summins[][] = new double[5][4]; // sum of highest ranking cards in each off-suit for [player][trumpsuit]
            int bests[] = new int[4]; // best suit to bid 2nd round for [player]
            double bestc[] = new double[4]; // point count of best suit to bid 2nd round for [player]
            int[] bidx = new int[4]; // results of bidding method which gives lone, declarer and fintp for each bid

            //  Keep track of which suits other players are known to be void in
            int[][] voids = new int[4][4]; // [player][suit] = 1 if void
            int[][] solo = new int[4][4]; // [player][suit] = 1 if only player left with that suit
            int[][] hint = new int[4][4]; // [player][suit] = 1 if player threw off that suit (signal?)
            int[] sit = new int[10]; // 10 special situations
            double [][][] max = new double[5][4][4]; // maximum rank for each [player][trumpsuit][suit]
            double [][][] nax = new double[5][4][4]; // 2nd highest rank for each [player][trumpsuit][suit]
            int[] vd = new int[4]; // number of void suits for given [player] (trump suit already determined)
            int[] ss = new int[4]; // if [player] is single-suited, from round 3 on (value = suit, or -1 by default)
            int cksuit[] = new int[4]; // local variable used to help calculate ss[]
            int[][] potbid = new int[2][4]; // bidding for [round][player; 0 = pass, 1 = wp, 2 = alone

            // New calculation of best card to discard
            seat = 8;
            int cswap = swapcard(cards, seat, dealer); // determines # of card swapped by dealer for turn card
            // only for purposes of creating bidding hand for dealer, with turn card as trump

            // New method for calculating bidscore (determining best bid for each player)
            int noteswap = 0; // denotes if creating new hand for dealer (with swapped card)
            for (int i=0; i<4; i++) { // don't need player 0/trump, or player 4/non-trump
                for (int j=0; j<4; j++) {
                    if (i == dealer && j == cards[20]%4) { // swap out turn card for dealer
                        int temp = cards[cswap];
                        cards[cswap] = cards[20];
                        cards[20] = temp;
                        noteswap = 1;
                    }
                    int post = i; // position of player (relative to dealer)
                    int tsuit = j; // which suit is trump (turn card suit means round 1, else round 2)
                    bidscore[i][j] = trump(cards, post, tsuit, upst, uprk);
                    if (noteswap == 1) { // swap back turn card
                        int temp = cards[cswap];
                        cards[cswap] = cards[20];
                        cards[20] = temp;
                        noteswap = 0;
                    }
                }
            }

            // calculate suit lengths for each [player][trumpsuit][suit]
            for (int h=0; h<4; h++) { // player
                for (int i=0; i<4; i++) { // trump
                    if (h == dealer && i == cards[20]%4) { // swap out turn card for dealer
                        int temp = cards[cswap];
                        cards[cswap] = cards[20];
                        cards[20] = temp;
                        noteswap = 1;
                    }
                    for (int j=0; j<4; j++) { // suit
                        for (int k=0; k<5; k++) { // cards
                            if (cards[k+h*5]%4 == j) {
                                playersuit[h][i][j]++; // increment count by 1
                            }
                            if (cards[k+h*5]/4 == 2 && cards[k+h*5]%4 == 3-i && j==0) {
                                playersuit[h][i][i]++; // adjust for bowers, without double counting
                                playersuit[h][i][3-i]--;
                            }
                        }
                    }
                    if (noteswap == 1) { // swap back turn card
                        int temp = cards[cswap];
                        cards[cswap] = cards[20];
                        cards[20] = temp;
                        noteswap = 0;
                    }
                }
            }

            // check for higher trump cards for each combination of player / suit
            // also count off-suit aces
            for (int i=0; i<24; i++) { // cards
                if (cards[i]/4 == 2) {
                    right[i/5][cards[i]%4]++; // have right bower
                    left[i/5][3-cards[i]%4]++; // have left bower
                }
                if (cards[i]/4 == 5) {
                    acet[i/5][cards[i]%4]++; // have ace of trump
                    for (int j=0; j<4; j++) {
                        if (j != cards[i]%4) {
                            aces[i/5][j]++; // count off-suit aces
                        }
                    }
                }
                if (cards[i]/4 == 4) {
                    kingt[i/5][cards[i]%4]++; // have king of trump
                }
            }
            if (cards[20]/4 == 2) {
                right[dealer][cards[20]%4] = 1; // dealer has right bower which is turned
            }
            if (cards[20]/4 == 5) {
                acet[dealer][cards[20]%4] = 1; // dealer has ace of trump in suit of turned card
            }
            if (cards[20]/4 == 4) {
                kingt[dealer][cards[20]%4] = 1; // dealer has king of trump in suit of turned card
            }

            // calculate maximum rank for each player/trumpsuit/suit, to then calculate summins
            // initialize max value
            for (int i=0; i<4; i++) { // player
                for (int j=0; j<4; j++) { // trump suit
                    for (int k=0; k<4; k++) { // suit
                        max[i][j][k] = -1;
                    }
                }
            }
            // calculate max and nax values (nax is next highest)
            for (int i=0; i<24; i++) { // cards
                for (int j=0; j<4; j++) { // trump suit
                    if (cards[i]/4 > max[i/5][j][cards[i]%4] && j != cards[i]%4 && (cards[i]/4 != 2 || cards[i]%4 != 3-j)) {
                        nax[i/5][j][cards[i]%4] = max[i/5][j][cards[i]%4];
                        max[i/5][j][cards[i]%4] = cards[i]/4;
                    }
                    else if (cards[i]/4 > nax[i/5][j][cards[i]%4] && j != cards[i]%4 && (cards[i]/4 != 2 ||
                                                                                         cards[i]%4 != 3-j)) {
                        nax[i/5][j][cards[i]%4] = cards[i]/4;
                    }
                }
            }
            // correct max values
            for (int i=0; i<4; i++) { // player
                for (int j=0; j<4; j++) { // trump suit
                    for (int k=0; k<4; k++) { // suit
                        if (max[i][j][k] == 5) {
                            if (playersuit[i][j][k] > 1 && nax[i][j][k] != 4) {
                                max[i][j][k] = 4.7;
                            }
                        }
                        if (playersuit[i][j][k] == 0) {
                            max[i][j][k] = 4.9;
                        }
                    }
                }
            }
            // calculate number of void suits for each player (assuming turn card is trump)
            for (int i=0; i<4; i++) { // player
                for (int j=0; j<4; j++) { // suit
                    if (playersuit[i][upst][j] == 0 && j != upst) {
                        vd[i]++;
                    }
                }
            }
            // calculate summins
            for (int i=0; i<4; i++) { // player
                for (int j=0; j<4; j++) { // trump suit
                    for (int k=0; k<4; k++) { // suit
                        if (j != k) {
                            summins[i][j] += max[i][j][k];
                        }
                    }
                }
            }
            // Initialize values for best 2nd round suit
            for (int i=0; i<4; i++) { // player
                bestc[i] = -20;
                bests[i] = 3-upst; // initially set best suit to next
            }

            // Determine best suit to bid 2nd round
            for (int i=0; i<4; i++) { // player
                for (int j=0; j<4; j++) { // trump suit
                    if (j != upst && bestc[i] < bidscore[i][j]) { // can't bid turned suit 2nd round
                        bestc[i] = bidscore[i][j];
                        bests[i] = j;
                    }
                }
            }

            //  List human player's cards
            System.out.println("\n" + "South's cards:");
            for (int i=0; i<5; i++) {
                System.out.println(cardname[cards[(i)]%4][cards[(i)]/4]);
            }

            System.out.println("\n" + "turn card: " + cardname[cards[20]%4][cards[20]/4] + "\n");

            // ********************************************************
            // ********************************************************

            // use booleans to track who has bid what and when
            boolean bidround = false; // bid round 1 or 2
            boolean bidyes = false; // pass or bid
            boolean wpalone = false; // bid wp or alone
            boolean bidder1 = false; // 0/0 = W; 0/1 = N; 1/0 = E; 1/1 = S
            boolean bidder2 = false;
            boolean bidsuit1 = false; // 0/0 = spades; 0/1 = hearts; 1/0 = diamonds; 1/1 = clubs
            boolean bidsuit2 = false;

            // use booleans to see if thread t1 has dealt with these bidders
            boolean firstbid1 = false;
            boolean secondbid1 = false;
            boolean thirdbid1 = false;
            boolean fourthbid1 = false;
            boolean firstbid2 = false;
            boolean secondbid2 = false;
            boolean thirdbid2 = false;
            boolean fourthbid2 = false;

            // use booleans to see if player has bid
            boolean[] bidwp = new boolean[4];
            boolean[] bidalone = new boolean[4];
            for (int i=0; i<4; i++) {
                bidwp[i] = false;
                bidalone[i] = false;
            }

            // use booleans to see if human bidder selects trump, round 2
            boolean picks = false;
            boolean pickh = false;
            boolean pickd = false;
            boolean pickc = false;

            // use booleans to see if human dealer has swapped a card with turn
            boolean swap0 = false;
            boolean swap1 = false;
            boolean swap2 = false;
            boolean swap3 = false;
            boolean swap4 = false;
            boolean swapper = false;

            // use booleans to see if thread 5 has finished each trick
            boolean trick1 = false;
            boolean trick2 = false;
            boolean trick3 = false;
            boolean trick4 = false;
            boolean trick5 = false;
            boolean stk1 = false; // don't update trick score until everyone has played
            boolean stk2 = false;
            boolean stk3 = false;
            boolean stk4 = false;
            boolean stk5 = false;

            // use booleans to show which card human has played
            boolean pc11 = false;
            boolean pc12 = false;
            boolean pc13 = false;
            boolean pc14 = false;
            boolean pc15 = false;
            boolean pc21 = false;
            boolean pc22 = false;
            boolean pc23 = false;
            boolean pc24 = false;
            boolean pc31 = false;
            boolean pc32 = false;
            boolean pc33 = false;
            boolean pc41 = false;
            boolean pc42 = false;
            boolean pcdone1 = false;
            boolean pcdone2 = false;
            boolean pcdone3 = false;
            boolean pcdone4 = false;

            // game and trick tally table
            int tns = 0; // N/S tricks
            int tew = 0; // E/W tricks

            // XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

            // first bidder
            docall = bidder11(playersuit[aa][upst][upst], playersuit[aa][upst][3-upst], playersuit[aa][3-upst][3-upst],
                              right[aa][upst], left[aa][upst], acet[aa][upst], aces[aa][upst], uprk, upst, bidscore[aa][upst],
                              bidscore[aa][3-upst], summins[aa][upst], vd[aa], bestc[aa], points[aa], game);

            bidx = bid(docall, aa, cardname[cards[20]%4][cards[20]/4], upst);

            potbid[0][aa] = bidx[3];
            if (bidx[3] == 1) {
                bidwp[aa] = true;
                bidyes = true;
            }
            if (bidx[3] == 2) {
                bidalone[aa] = true;
                bidyes = true;
                wpalone = true;
            }
            if (bidx[3] > 0) { // some bid made
                if (aa == 3) {
                    bidder1 = true; // East bids
                } else if (aa == 2) {
                    bidder2 = true; // North bids
                }
            }
            if (bidx[2] < 1) {
                bidsuit1 = true; // diamonds or clubs bid
            }
            if (bidx[2] == 1 || bidx[2] == 3) {
                bidsuit2 = true; // hearts or clubs bid
            }

            lone = bidx[0];
            declarer = bidx[1];
            fintp = bidx[2];
            firstbid1 = true; // signal of having reached this point

            // second bidder
            // only need to proceed if previous players passed
            if (!bidyes) {
                docall = bidder12(playersuit[bb][upst][upst], playersuit[bb][upst][3-upst], right[bb][upst],
                                  left[bb][upst], acet[bb][upst], kingt[bb][upst], uprk, upst, bidscore[bb][upst],
                                  summins[bb][upst], vd[bb], points[bb], game);

                bidx = bid(docall, bb, cardname[cards[20]%4][cards[20]/4], upst);

                potbid[0][bb] = bidx[3];
                if (bidx[3] == 1) {
                    bidwp[bb] = true;
                    bidyes = true;
                }
                if (bidx[3] == 2) {
                    bidalone[bb] = true;
                    bidyes = true;
                    wpalone = true;
                }
                if (bidx[3] > 0) { // some bid made
                    if (bb == 3) {
                        bidder1 = true; // East bids
                    } else if (bb == 2) {
                        bidder2 = true; // North bids
                    }
                }
                if (bidx[2] < 1) {
                    bidsuit1 = true; // diamonds or clubs bid
                }
                if (bidx[2] == 1 || bidx[2] == 3) {
                    bidsuit2 = true; // hearts or clubs bid
                }

                lone = bidx[0];
                declarer = bidx[1];
                fintp = bidx[2];
                secondbid1 = true; // signal of having reached this point
            }

            // third bidder
            // only need to proceed if previous players passed
            if (!bidyes) {
                docall = bidder13(playersuit[cc][upst][upst], playersuit[cc][upst][3-upst], right[cc][upst], left[cc][upst],
                                  acet[cc][upst], uprk, upst, bidscore[cc][upst], summins[cc][upst], vd[cc], points[cc], game);

                bidx = bid(docall, cc, cardname[cards[20]%4][cards[20]/4], upst);

                potbid[0][cc] = bidx[3];
                if (bidx[3] == 1) {
                    bidwp[cc] = true;
                    bidyes = true;
                }
                if (bidx[3] == 2) {
                    bidalone[cc] = true;
                    bidyes = true;
                    wpalone = true;
                }
                if (bidx[3] > 0) { // some bid made
                    if (cc == 3) {
                        bidder1 = true; // East bids
                    } else if (cc == 2) {
                        bidder2 = true; // North bids
                    }
                }
                if (bidx[2] < 1) {
                    bidsuit1 = true; // diamonds or clubs bid
                }
                if (bidx[2] == 1 || bidx[2] == 3) {
                    bidsuit2 = true; // hearts or clubs bid
                }

                lone = bidx[0];
                declarer = bidx[1];
                fintp = bidx[2];
                thirdbid1 = true; // signal of having reached this point
            }

            // dealer
            // only need to proceed if previous players passed
            if (!bidyes) {
                docall = bidder14(playersuit[dd][upst][upst], playersuit[dd][upst][3-upst], right[dd][upst], left[dd][upst],
                                  acet[dd][upst], aces[dd][upst], kingt[dd][upst], uprk, upst, bidscore[dd][upst], summins[dd][upst],
                                  vd[dd], points[dd], game);

                bidx = bid(docall, dd, cardname[cards[20]%4][cards[20]/4], upst);

                potbid[0][dd] = bidx[3];
                if (bidx[3] == 1) {
                    bidwp[dd] = true;
                    bidyes = true;
                }
                if (bidx[3] == 2) {
                    bidalone[dd] = true;
                    bidyes = true;
                    wpalone = true;
                }
                if (bidx[3] > 0) { // some bid made
                    if (dd == 3) {
                        bidder1 = true; // East bids
                    } else if (dd == 2) {
                        bidder2 = true; // North bids
                    }
                }
                if (bidx[2] < 1) {
                    bidsuit1 = true; // diamonds or clubs bid
                }
                if (bidx[2] == 1 || bidx[2] == 3) {
                    bidsuit2 = true; // hearts or clubs bid
                }

                lone = bidx[0];
                declarer = bidx[1];
                fintp = bidx[2];
                fourthbid1 = true; // signal of having reached this point
            }

            //  if all players pass, have second round of bidding
            if (!bidyes) {
                System.out.println("No one bids in the first round");
                round = 1;
            }

            //  if seat 2 didn't call lone AND some player declared, have dealer swap cards
            if (lone != bb && declarer != -1) {
                // Re-calculate of best card to discard
                seat = ((declarer-dealer+4)%4)*2 + 1 - (lone+6)/6; // see spreadsheet for meaning
                cswap = swapcard(cards, seat, dealer); // determines # of card swapped by dealer for turn card

                int temp = cards[cswap]; // dealer swaps cards
                cards[cswap] = cards[20];
                cards[20] = temp;
            }

            // XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
            //  second round of bidding, if necessary

            // first bidder
            // only need to proceed if previous players passed
            if (!bidyes) {
                docall = bidder21(playersuit[aa][bests[aa]][bests[aa]], playersuit[aa][bests[aa]][3-bests[aa]],
                                  right[aa][bests[aa]], left[aa][bests[aa]], acet[aa][bests[aa]], aces[aa][bests[aa]],
                                  //kingt[aa][bests[aa]], upst, bidscore[aa][bests[aa]], summins[aa][bests[aa]], bests[(dealer+1)%4],
                                  kingt[aa][bests[aa]], upst, bidscore[aa][bests[aa]], summins[aa][bests[aa]], bests[aa],
                                  points[aa], game);

                bidx = bid(docall, aa, bests[aa]);

                potbid[1][aa] = bidx[3];
                if (bidx[3] == 1) {
                    bidwp[aa] = true;
                    bidyes = true;
                }
                if (bidx[3] == 2) {
                    bidalone[aa] = true;
                    bidyes = true;
                    wpalone = true;
                }
                if (bidx[3] > 0) { // some bid made
                    if (aa == 3) {
                        bidder1 = true; // East bids
                    } else if (aa == 2) {
                        bidder2 = true; // North bids
                    }
                }

                lone = bidx[0];
                declarer = bidx[1];
                fintp = bidx[2];
                // now code for which suit was declared
                if (bidx[2] == 1) { // hearts
                    bidsuit2 = true;
                } else if (bidx[2] == 2) { // diamonds
                    bidsuit1 = true;
                } else if (bidx[2] == 3) { // clubs
                    bidsuit2 = true;
                    bidsuit1 = true;
                } else {
                }
                firstbid2 = true; // signal of having reached this point
                bidround = true;
            }

            // second bidder
            // only need to proceed if previous players passed
            if (!bidyes) {
                docall = bidder22(playersuit[bb][bests[bb]][bests[bb]], playersuit[bb][bests[bb]][3-bests[bb]],
                                  right[bb][bests[bb]], left[bb][bests[bb]], acet[bb][bests[bb]], aces[bb][bests[bb]],
                                  kingt[bb][bests[bb]], upst, uprk,bidscore[bb][bests[bb]], summins[bb][bests[bb]],
                                  //bests[(dealer+1)%4], points[bb], game);
                                  bests[bb], points[bb], game);

                bidx = bid(docall, bb, bests[bb]);
                potbid[1][bb] = bidx[3];
                if (bidx[3] == 1) {
                    bidwp[bb] = true;
                    bidyes = true;
                }
                if (bidx[3] == 2) {
                    bidalone[bb] = true;
                    bidyes = true;
                    wpalone = true;
                }
                if (bidx[3] > 0) { // some bid made
                    if (bb == 3) {
                        bidder1 = true; // East bids
                    } else if (bb == 2) {
                        bidder2 = true; // North bids
                    }
                }

                lone = bidx[0];
                declarer = bidx[1];
                fintp = bidx[2];
                // now code for which suit was declared
                if (bidx[2] == 1) {
                    bidsuit2 = true;
                } else if (bidx[2] == 2) {
                    bidsuit1 = true;
                } else if (bidx[2] == 3) {
                    bidsuit2 = true;
                    bidsuit1 = true;
                } else {
                }
                secondbid2 = true; // signal of having reached this point
                bidround = true;
            }

            // third bidder
            // only need to proceed if previous players passed
            if (!bidyes) {
                docall = bidder23(playersuit[cc][bests[cc]][bests[cc]], right[cc][bests[cc]], left[cc][bests[cc]],
                                  acet[cc][bests[cc]], aces[cc][bests[cc]], kingt[cc][bests[cc]], upst, uprk,
                                  //bidscore[cc][bests[cc]], summins[cc][bests[cc]], bests[(dealer+1)%4], points[cc], game);
                                  bidscore[cc][bests[cc]], summins[cc][bests[cc]], bests[cc], points[cc], game);

                bidx = bid(docall, cc, bests[cc]);

                potbid[1][cc] = bidx[3];
                if (bidx[3] == 1) {
                    bidwp[cc] = true;
                    bidyes = true;
                }
                if (bidx[3] == 2) {
                    bidalone[cc] = true;
                    bidyes = true;
                    wpalone = true;
                }
                if (bidx[3] > 0) { // some bid made
                    if (cc == 3) {
                        bidder1 = true; // East bids
                    } else if (cc == 2) {
                        bidder2 = true; // North bids
                    }
                }

                lone = bidx[0];
                declarer = bidx[1];
                fintp = bidx[2];
                // now code for which suit was declared
                if (bidx[2] == 1) {
                    bidsuit2 = true;
                } else if (bidx[2] == 2) {
                    bidsuit1 = true;
                } else if (bidx[2] == 3) {
                    bidsuit2 = true;
                    bidsuit1 = true;
                } else {
                }
                thirdbid2 = true; // signal of having reached this point
                bidround = true;
            }

            // last bidder (dealer)
            // only need to proceed if previous players passed
            if (!bidyes) {
                docall = bidder24(playersuit[dd][bests[dd]][bests[dd]], playersuit[dd][bests[dd]][3-bests[dd]],
                                  right[dd][bests[dd]], left[dd][bests[dd]], acet[dd][bests[dd]], upst, uprk,
                                  //bidscore[dd][bests[dd]], summins[dd][bests[dd]], bests[(dealer+1)%4], points[dd], game);
                                  bidscore[dd][bests[dd]], summins[dd][bests[dd]], bests[dd], points[dd], game);

                bidx = bid(docall, dd, bests[dd]);

                potbid[1][dd] = bidx[3];
                if (bidx[3] == 1) {
                    bidwp[dd] = true;
                    bidyes = true;
                }
                if (bidx[3] == 2) {
                    bidalone[dd] = true;
                    bidyes = true;
                    wpalone = true;
                }
                if (bidx[3] > 0) { // some bid made
                    if (dd == 3) {
                        bidder1 = true; // East bids
                    } else if (dd == 2) {
                        bidder2 = true; // North bids
                    }
                }

                lone = bidx[0];
                declarer = bidx[1];
                fintp = bidx[2];
                // now code for which suit was declared
                if (bidx[2] == 1) {
                    bidsuit2 = true;
                } else if (bidx[2] == 2) {
                    bidsuit1 = true;
                } else if (bidx[2] == 3) {
                    bidsuit2 = true;
                    bidsuit1 = true;
                } else {
                }
                fourthbid2 = true; // signal of having reached this point
                bidround = true;
            }

            // skip play if no one bids second round
            if (declarer == -1) {
                System.out.println("No one bids in second round\n");
            } else {

                // change value of bowers to reflect proper suit and hierarchy
                for (int i=0; i<24; i++) {
                    if (fintp == 0) { // spades is trump
                        if (cards[i] == 8) { // R
                            cards[i] = 28;
                        }
                        if (cards[i] == 11) { // L
                            cards[i] = 24;
                        }
                    }
                    if (fintp == 1) { // hearts is trump
                        if (cards[i] == 9) { // R
                            cards[i] = 29;
                        }
                        if (cards[i] == 10) { // L
                            cards[i] = 25;
                        }
                    }
                    if (fintp == 2) { // diamonds is trump
                        if (cards[i] == 10) { // R
                            cards[i] = 30;
                        }
                        if (cards[i] == 9) { // L
                            cards[i] = 26;
                        }
                    }
                    if (fintp == 3) { // clubs is trump
                        if (cards[i] == 11) { // R
                            cards[i] = 31;
                        }
                        if (cards[i] == 8) { // L
                            cards[i] = 27;
                        }
                    }
                }

                // initialize 'own' values to -1
                for (int i=0; i<4; i++) { // players
                    for (int j=0; j<4; j++) { // suits
                        for (int k=0; k<8; k++) { // ranks
                            own[i][j][k] = -1;
                        }
                    }
                }

                // identify where each card is
                for (int j=0; j<20; j++) { // cards
                    own[j/5][cards[j]%4][cards[j]/4] = j/5;
                }
                if (round == 0) { // dealer swapped a card; all players know turn card is in dealer's hand
                    // only dealer knows card discarded
                    own[dealer][cards[20]%4][cards[20]/4] = -1;
                    for (int i=0; i<4; i++) {
                        own[i][cards[cswap]%4][cards[cswap]/4] = dealer;
                    }
                }
                if (round == 1) {
                    for (int i=0; i<4; i++) {
                        own[i][cards[20]%4][cards[20]/4] = -1;
                    }
                }

                //** calculate suit lengths for each [player][suit]
                for (int i=0; i<4; i++) { // player
                    for (int j=0; j<4; j++) { // suit
                        for (int k=0; k<5; k++) { // cards
                            if (cards[k+i*5]%4 == j) {
                                playst[i][j]++; // increment count by 1
                            }
                        }
                    }
                }

                //  establish length of suits in play (varies from 4 to 7)
                for (int i=0; i<4; i++) { // player
                    for (int j=0; j<4; j++) { // suit
                        if (j == fintp) {
                            length[i][j] = 7;
                        } else if (j == 3-fintp) {
                            length[i][j] = 5;
                        } else {
                            length[i][j] = 6;
                        }
                    }
                    if (round == 1) {
                        length[i][cards[20]%4]--;
                    }
                }
                if (round == 0) {
                    length[dd][cards[20]%4]--;
                }

                //  calculate values of ace / king / queen arrays (non-trump)
                for (int i=0; i<4; i++) { // reset off-ace count to zero
                    aces[i][fintp] = 0;
                }
                for (int k=0; k<4; k++) { // player
                    for (int i=0; i<4; i++) { // suit
                        if ((max[k][fintp][i] == 5 || max[k][fintp][i] == 4.7) && i != fintp) {
                            ace[k][i] = playst[k][i]; // length of ace-led suit
                            aces[k][fintp]++; // number of non-trump aces
                        }
                        if (max[k][fintp][i] == 4 && i != fintp) { // non-trump suit headed by K
                            king[k][i] = playst[k][i]; // length of K-led suit
                        }
                        if (max[k][fintp][i] == 3 && i != fintp) { // non-trump suit headed by Q
                            queen[k][i] = playst[k][i]; // length of Q-led suit
                        }
                    }
                }

                // if a player going lone, assign voids to all partner's suits
                if (lone > -1) {
                    for (int j=0; j<4; j++){
                        voids[(lone+2)%4][j] = 1;
                    }
                }

                // calculate boss (highest) rank in each suit
                for (int i=0; i<4; i++) { // player
                    for (int j=0; j<4; j++) { // suit
                        for (int k=0; k<8; k++) { // rank
                            if (own[i][j][k] > -1 && own[i][j][k] < 4 && k > boss[i][j]) {
                                boss[i][j] = k;
                            }
                        }
                    }
                }

                //** invoke method calc to calculate values for cv[lead][][] and cv[toss][][]
                cv = calc(cards, fintp, own, playst, max, nax, lone, dealer);

                // determine number of non-trump void suits for each player
                for (int i=0; i<4; i++) {
                    vd[i] = 0; // initialize vd value
                    for (int j=0; j<4; j++) {
                        if (playst[i][j] == 0 && j != fintp) {
                            vd[i]++;
                        }
                    }
                }

                // put each players cards in order (trump suit first, then spades - hearts - diamond - clubs in that order;
                // highest to lowest rank within each suit
                cards = order(cards, fintp);

                // establish name of left bower
                cardname[fintp][6] = "Jack of " + suitx[3-fintp];

                final int flone = lone; // indicates if a player declared alone (for use in threads)

                // XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
                //  play out the hand
                // XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

                //  first to play is left of the dealer
                int wturn = 1; // trick 1
                double p11 = 20;
                double p11a = 20;
                int m11 = -1;
                int n11 = -1;
                int v11 = -1;
                int maxsuit = -1;
                int minsuit = 20;
                double playace = 0; // value of A played
                int suitace = -1; // suit of A played
                int hitrump = -1;
                int sectrump = -1;
                int lotrump = 8;
                int worsts = -1; // worst suit
                int worstr = -1; // worst rank
                int worstsa = -1; // alternate worst suit
                int worstra = -1; // alternate worst rank
                int play1 = -1;

                if (lone != cc) { // partner in 3rd seat NOT going alone
                    win1 = aa;
                    for (int i=0; i<4; i++) { // establishes best ace to lead; not trump AND 5+ length suit
                        if (own[aa][i][5] == aa && cv[0][i][5] > playace && i != fintp && length[aa][i] > 4 &&
                            (i != cards[20]%4 || round == 0)) {
                            playace = cv[0][i][5];
                            suitace = i;
                        }
                    }
                    for (int i=0; i<8; i++) {
                        if (own[aa][fintp][i] == aa) {
                            if (i > hitrump) {
                                hitrump = i; // calculates rank of highest trump (0 - 7)
                            }
                            if (i < lotrump) {
                                lotrump = i; // calculates rank of lowest trump (0 - 7)
                            }
                        }
                    }
                    for (int i=0; i<4; i++) {  // find lowest card from shortest suit (lowest cv[0] value)
                        for (int j=0; j<8; j++) {
                            if (p11 > cv[0][i][j] && cv[0][i][j] > 0 && own[aa][i][j] == aa) {
                                p11 = cv[0][i][j];
                                worsts = i;
                                worstr = j;
                            }
                        }
                    }
                    for (int i=0; i<4; i++) {  // find 2nd lowest card from shortest suit (alternate cv[0])
                        for (int j=0; j<8; j++) {
                            if (p11a > cv[0][i][j] && cv[0][i][j] > 0 && own[aa][i][j] == aa && p11 < cv[0][i][j]) {
                                p11a = cv[0][i][j];
                                worstsa = i;
                                worstra = j;
                            }
                        }
                    }
                    // if worst card in hand is in turned suit, and bid was made 2nd round, assign next worst card as worst card
                    if (worsts == upst && round == 1 && worsts != worstsa) {
                        worsts = worstsa;
                        worstr = worstra;
                    }

                    if (lone == aa && playst[aa][fintp] > 0) { // if going lone, lead highest trump (if have trump)
                        play1 = 0;
                    } else if (lone == dd || lone == bb) { // if defending against lone
                        if (aces[aa][fintp] > 1) { // if have more than one A, play one
                            play1 = 5;
                        } else { // else play worst card
                            play1 = 6;
                        }
                    } else if (declarer == cc) { // partner in 3rd seat is declarer
                        if (hitrump > 5 || playst[aa][fintp] > 2) {
                            // if have bower or 3+ trump, lead best trump
                            play1 = 0;
                        } else if (playst[aa][fintp] > 0) { // else lead worst trump
                            play1 = 2;
                        } else if (playace > 1.195) { // else lead a 'good A'
                            play1 = 5;
                        } else { // else lead worst card
                            play1 = 6;
                        }
                    } else if (declarer == aa) { // if declared w/ partner
                        if (right[aa][fintp] + left[aa][fintp] == 2 &&
                            (aces[aa][fintp] > 0 || playst[aa][fintp] > 2)) {
                            // if have both bowers AND (an off-suit A OR 3+ trump), lead R
                            play1 = 0;
                        } else if (right[aa][fintp] == 1 && acet[aa][fintp] == 1 && kingt[aa][fintp] == 1) {
                            // if have R + A + K of trump, lead R
                            play1 = 0;
                        } else if (left[aa][fintp] == 1 && acet[aa][fintp] == 1 && (aces[aa][fintp] > 0 ||
                                                                                    playst[aa][fintp] > 2)) { // if have L + A of trump AND (an off-A OR 3+ trump), lead L
                            play1 = 0;
                        } else if (left[aa][fintp] == 1 && playst[aa][fintp] == 2 && aces[aa][fintp] > 0 &&
                                   vd[aa] == 2 && round == 1) { // if have L-x of trump AND an off-A AND 2-suited, lead L
                            play1 = 0;
                        } else if (suitace != -1) {
                            if (round == 0 && declarer == aa && right[aa][fintp] == 1 && playst[aa][fintp] == 2 &&
                                (ace[aa][suitace] == 3 || (ace[aa][suitace] == 2 && own[aa][suitace][4] == aa))) {
                                play1 = 0; // if round 1 AND declarer AND have R + another trump AND either (have A-x-x or A-K
                                // off-suit), lead R, then A 2nd trick
                                sit[0] = 1; // 1st special situation: will win this trick, should lead off-suit A 2nd trick
                            }
                        } else if (playst[aa][fintp] == 3 && round == 1 && right[aa][fintp] == 0) {
                            // bid 2nd round with 3 trump AND don't have R
                            if (playace > 0) { // lead an off-suit A if have one
                                play1 = 5;
                            } else if (uprk == 2) { // if R turned, lead high trump
                                play1 = 0;
                            } else { // else lead low trump
                                play1 = 2;
                            }
                        } else if (aces[aa][fintp] > 0 && ((hitrump < 6 && playst[aa][fintp] > 1) || (fintp == 3-upst &&
                                                                                                      (right[aa][fintp] + left[aa][fintp] == 1) && playst[aa][fintp] > 2))) {
                            // if have an off-suit A AND (have 2+ non-bower trump OR [next is trump AND have at least three trump
                            // headed by only one bower]), lead low trump
                            play1 = 2;
                        } else if (playst[aa][fintp] == 4) { // if have 4 trump, lead low trump
                            play1 = 2;
                        } else {
                            play1 = 6;
                        }
                    } else if (declarer == bb) { // if 2nd seat declared
                        if (right[aa][fintp] + left[aa][fintp] == 2) {
                            // if have both bowers, lead R
                            play1 = 0;
                            // if have 3+ trump AND R not turned, lead low trump
                        } else if (uprk != 2 && playst[aa][fintp] > 2) {
                            play1 = 2;
                        } else if (uprk == 2 && vd[aa] == 0 && aces[aa][fintp] == 0 && hitrump < 4 &&
                                   playst[aa][fintp] == 1) { // if R is turn card AND are 4-suited AND have no aces AND
                            // a single trump Q-, lead trump
                            play1 = 0;
                        } else if (playst[aa][fintp] == 2 && vd[aa] > 0 && aces[aa][fintp] > 0 &&
                                   suitace > -1) {
                            // if have 2 trump AND are 2- or 3-suited AND have an off-suit Ace
                            if (hitrump > 5) { // if have a bower, lead best Ace
                                play1 = 5;
                            } else { // else lead low trump
                                play1 = 2;
                            }
                        } else if (playst[aa][fintp] == 2 && vd[aa] > 0 && aces[aa][fintp] == 0) {
                            // if have 2 trump AND are 2- or 3-suited AND no off-suit Ace
                            if (uprk == 2 || right[aa][fintp] == 1) { // if R turned OR have R, lead worst card
                                play1 = 6;
                            } else { // else lead low trump
                                play1 = 2;
                            }
                        } else if (playace > 1.195) { // else lead a 'good A'
                            play1 = 5;
                        } else { // else lead worst card
                            play1 = 6;
                        }
                    } else if (declarer == dd) { // dealer declared
                        if (playst[aa][fintp] == 2 && vd[aa] > 0 &&
                            aces[aa][fintp] == 0 && hitrump < 4) { // have 2 trump AND 2- or 3-suited AND
                            // have no Ace AND Q- is best trump, lead low trump
                            play1 = 2;
                        } else if (playace > 1.195) { // else lead a 'good A'
                            play1 = 5;
                        } else { // else lead worst card
                            play1 = 6;
                        }
                    }

                    // if leading a non-trump non-ace and have the A of that suit as well, lead the A instead
                    if (worsts == suitace && play1 == 6) {
                        play1 = 5;
                    }

                    int cardplay = playfirst(play1, fintp, hitrump, sectrump, lotrump, m11, maxsuit, minsuit, suitace,
                                             worsts, worstr, aa, 0, cards);
                    m11 = cardplay%10;
                    n11 = (cardplay/10)%10;

                    System.out.println("Player " + position[aa] + " leads the " + cardname[m11][n11] + ".\n");
                    for (int i=0; i<4; i++) {
                        own[i][m11][n11] = -1;
                        length[i][m11]--;
                    }
                    playst[aa][m11] = playst[aa][m11] - 1;
                    cv[0][m11][n11] = 0;
                    cv[1][m11][n11] = 0;
                    if (n11 == 5 && m11 != fintp) {
                        aces[aa][fintp]--;
                    }
                    if (n11 == 5 && m11 == fintp) {
                        acet[aa][fintp] = 0;
                    }
                    suit[0][0] = m11;
                    rank[0][0] = n11;
                    if (m11 == fintp) {
                        v11 = 10+n11;
                    } else {
                        v11 = n11;
                    }
                }

                //  second to play, 1st trick
                double p12 = 20;
                play1 = -1;
                int m12 = -1;
                int n12 = -1;
                int v12 = -1;
                playace = 0;
                suitace = -1;
                maxsuit = -1; // highest
                minsuit = 20; // lowest
                hitrump = -1;
                sectrump = -1;
                lotrump = 8;
                worsts = -1; // worst suit
                worstr = -1; // worst rank

                if (lone == cc) {// 3rd seat going alone, assign m11 a temp value to avoid errors
                    m11 = 0;
                }
                if (lone == dd) {
                    m12 = m11;  // if dealer going alone, skip North, so assign same value to their 'play' as West
                    n12 = n11;
                    v12 = v11-1;
                } else { // dealer NOT going alone
                    if (lone != cc && playst[bb][m11] == 0) { // void in led suit
                        voids[bb][m11] = 1;
                    }
                    for (int i=0; i<4; i++) { // identify best ace
                        if (own[bb][i][5] == bb && cv[0][i][5] > playace && i != fintp) {
                            playace = cv[0][i][5];
                            suitace = i;
                        }
                    }
                    for (int i=0; i<4; i++) { // identify worst card
                        for (int j=0; j<8; j++) {
                            if (playst[bb][fintp] > 0) { // if have trump use cv[0][][] to determine worst card
                                if (p12 > cv[0][i][j] && cv[0][i][j] > 0 && own[bb][i][j] == bb) {
                                    p12 = cv[0][i][j];
                                    worsts = i;
                                    worstr = j;
                                }
                            } else { // if void in trump, use 'cvtoss' to determine worst card
                                if (p12 > cv[1][i][j] && cv[1][i][j] > 0 && own[bb][i][j] == bb) {
                                    p12 = cv[1][i][j];
                                    worsts = i;
                                    worstr = j;
                                }
                            }
                        }
                    }
                    if (lone != cc) { // only do calculation if 3rd seat not going lone
                        for (int j=0; j<8; j++) { // calculate highest and lowest of led suit
                            if (own[bb][m11][j] == bb) {
                                if (j > maxsuit) {
                                    maxsuit = j;
                                }
                                if (j < minsuit) {
                                    minsuit = j;
                                }
                            }
                        }
                    }
                    for (int j=0; j<8; j++) { // calculate highest, second highest, and lowest trump
                        if (own[bb][fintp][j] == bb) {
                            if (hitrump == -1) {
                                hitrump = j;
                                sectrump = j;
                            } else if (j > hitrump) {
                                sectrump = hitrump;
                                hitrump = j;
                            }
                            if (j < lotrump) {
                                lotrump = j;
                            }
                        }
                    }

                    if (lone == bb && playst[bb][m11] == 0 && playst[bb][fintp] > 0) { // 2nd seat going alone
                        // and can't follow suit but have trump
                        play1 = 1;
                    } else if (lone == cc) { // 3rd seat going alone, so 2nd seat is first lead
                        if (playst[bb][3-fintp] > 0) {
                            worsts = 3-fintp;
                            for (int i=0; i<8; i++) { // play highest of next suit (hopefully partner [dealer] is void and not declarer)
                                if (own[bb][3-fintp][i] == bb) {
                                    worstr = i;
                                    play1 = 6; // special case: play 'worst card' but assign proper values
                                }
                            }
                        } else if (aces[bb][fintp] > 1) { // have 2+ aces, then play one
                            play1 = 5;
                        } else { // otherwise play worst card
                            play1 = 6;
                        }
                        m11 = worsts; // pretend West played North's worst suit (irrelevant)
                        n11 = 0; // pretend West played a 9
                    } else if (playst[bb][m11] > 0) { // can follow suit
                        if (fintp == upst && uprk == 2 && m11 == upst) { // trump led and know partner has R
                            play1 = 4;
                        } else if (maxsuit > n11) { // can beat lead
                            play1 = 3;
                        } else {
                            play1 = 4;
                        }
                    } else if (m11 != fintp && playst[bb][fintp] > 0) { // can't follow suit but can trump
                        if (n11 < 4 && declarer == cc) { // if Q- led AND 3rd seat declarer, trump with 2nd highest
                            play1 = 1;
                        } else if (declarer == dd && hitrump == 7 && playst[bb][fintp] == 1 && rank[0][0] < 5) {
                            play1 = 6; // if dealer declared AND have only one trump, R, AND lead was not an A, throw off [trying for 5 tricks]
                        } else if (declarer == dd && hitrump > 5 ) { // trump with bower if partner (dealer) declared AND have a bower
                            play1 = 0;
                        } else if (declarer == bb) { // play low trump if declared
                            play1 = 2;
                        } else if (declarer != bb && n11 > 3 && lotrump != 7) {
                            // trump low if not declarer AND K or A led (but not w/ R)
                            play1 = 2;
                        } else { // play worst card
                            play1 = 6;
                            if (worsts != fintp) {
                                hint[bb][worsts] = 1;
                            }
                        }
                    } else { // play worst card
                        play1 = 6;
                        if (worsts != fintp) {
                            hint[bb][worsts] = 1;
                        }
                    }

                    int cardplay = playfirst(play1, fintp, hitrump, sectrump, lotrump, m11, maxsuit, minsuit, suitace, worsts,
                                             worstr, bb, playst[bb][m11], cards);
                    m12 = cardplay%10;
                    n12 = (cardplay/10)%10;

                    if (lone == cc) { // if 3rd seat going alone, assign m11 the value of m12, the true lead
                        m11 = m12;
                    }

                    System.out.println("Player " + position[bb] + " plays the " + cardname[m12][n12] + ".\n");
                    for (int i=0; i<4; i++) {
                        own[i][m12][n12] = -1;
                        length[i][m12]--;
                    }
                    playst[bb][m12] = playst[bb][m12] - 1;
                    cv[0][m12][n12] = 0;
                    cv[1][m12][n12] = 0;
                    if (n12 == 5 && m12 != fintp) {
                        aces[bb][fintp]--;
                    }
                    if (n12 == 5 && m12 == fintp) {
                        acet[bb][fintp] = 0;
                    }
                    suit[0][1] = m12;
                    rank[0][1] = n12;
                    if (m12 == fintp) {
                        v12 = 10+n12;
                    } else if (m12 == m11) {
                        v12 = n12;
                    } else {
                        v12 = -1;
                    }
                    if (v12 > v11) {
                        win1 = bb;
                    }
                }

                //  third to play, 1st trick
                double p13 = 20;
                play1 = 6;
                int m13 = -1;
                int n13 = -1;
                int v13 = -1;
                maxsuit = -1;
                minsuit = 20;
                hitrump = -1;
                lotrump = 8;
                sectrump = -1;
                int lowtrump = 0; // lowest trump that beats 2nd seat if 2nd seat trumped
                worsts = -1; // worst suit
                worstr = -1; // worst rank

                if (lone == aa) { // if 1st seat going alone (3rd seat skipped), pretend both played same cards
                    m13 = m11;
                    n13 = n11;
                    v13 = v12-1;
                } else {
                    if (playst[cc][m11] == 0) { // void in led suit
                        voids[cc][m11] = 1;
                    }
                    for (int i=0; i<4; i++) {  // identify worst card
                        for (int j=0; j<8; j++) {
                            if (playst[cc][fintp] > 0) {
                                if (p13 > cv[0][i][j] && cv[0][i][j] > 0 && own[cc][i][j] == cc) {
                                    p13 = cv[0][i][j];
                                    worsts = i;
                                    worstr = j;
                                }
                            } else {
                                if (p13 > cv[1][i][j] && cv[1][i][j] > 0 && own[cc][i][j] == cc) {
                                    p13 = cv[1][i][j];
                                    worsts = i;
                                    worstr = j;
                                }
                            }
                        }
                    }
                    for (int j=0; j<8; j++) {
                        if (own[cc][m11][j] == cc) { // find best and worst card if following suit
                            if (j > maxsuit) {
                                maxsuit = j;
                            }
                            if (j < minsuit) {
                                minsuit = j;
                            }
                        }
                    }
                    for (int j=0; j<8; j++) { // calculate highest, second highest, and lowest trump
                        if (own[cc][fintp][j] == cc) {
                            if (hitrump == -1) {
                                hitrump = j;
                                sectrump = j;
                            } else if (j > hitrump) {
                                sectrump = hitrump;
                                hitrump = j;
                            }
                            if (j < lotrump) {
                                lotrump = j;
                            }
                        }
                    }

                    if (playst[cc][m11] > 0) { // can follow suit
                        if ((maxsuit > n11+1 && win1 == aa) || (win1 == bb && maxsuit > n12 && m12 == m11)) {
                            play1 = 3; // play highest card of suit to take lead if:
                            // partner winning AND can beat by 2 ranks, OR opponent winning and can beat (and they followed suit)
                        } else {
                            play1 = 4; // play lowest card of suit
                        }
                    } else if (m11 != fintp && playst[cc][fintp] > 0) { // void in suit led but able to trump
                        for (int j=7; j>=0; j--) { // determine lowest trump to take lead IF 2nd player trumped
                            if (own[cc][fintp][j] == cc && j > n12 && m12 == fintp) {
                                lowtrump = j;
                            }
                        }
                        if (lone == cc) { // 3rd seat going lone: trump with second highest (if not bower), else lowest
                            if (sectrump < 6) {
                                play1 = 1;
                            } else {
                                play1 = 2;
                            }
                        } else if (lone == dd) { // dealer going lone: trump with highest, unless have R or protected L or A,
                            // in which case play worst card
                            if (right[cc][fintp] == 1 || (left[cc][fintp] == 1 && playst[cc][fintp] > 1) ||
                                (acet[cc][fintp] == 1 && playst[cc][fintp] > 2)) {
                                play1 = 6;
                                if (worsts != fintp) {
                                    hint[cc][worsts] = 1;
                                }
                            } else {
                                play1 = 0;
                            }

                        } else if (m12 != fintp && (win1 == bb || (n11 < boss[cc][m11] && lotrump != 7))) { // if 2nd seat winning
                            // w/o trump OR 1st seat (partner) played lower than boss card, play lowest trump (but play worst
                            // card if only R)
                            play1 = 2;
                        } else if (m12 == fintp && lowtrump > n12) { // if 2nd seat trumped but can overtrump, do so with lowest
                            // trump that takes lead
                            lotrump = lowtrump;
                            play1 = 2;
                        } else {
                            play1 = 6;
                            if (worsts != fintp) {
                                hint[cc][worsts] = 1;
                            }
                        }
                    } else { // throw off
                        play1 = 6;
                        if (worsts != fintp) {
                            hint[cc][worsts] = 1;
                        }
                    }

                    int cardplay = playfirst(play1, fintp, hitrump, sectrump, lotrump, m11, maxsuit, minsuit, suitace, worsts,
                                             worstr, cc, playst[cc][m11], cards);
                    m13 = cardplay%10;
                    n13 = (cardplay/10)%10;

                    System.out.println("Player " + position[cc] + " plays the " + cardname[m13][n13] + ".\n");
                    for (int i=0; i<4; i++) {
                        own[i][m13][n13] = -1;
                        length[i][m13]--;
                    }
                    playst[cc][m13] = playst[cc][m13] - 1;
                    cv[0][m13][n13] = 0;
                    cv[1][m13][n13] = 0;
                    if (n13 == 5 && m13 != fintp) {
                        aces[cc][fintp]--;
                    }
                    if (n13 == 5 && m13 == fintp) {
                        acet[cc][fintp] = 0;
                    }
                    suit[0][2] = m13;
                    rank[0][2] = n13;
                    if (m13 == fintp) {
                        v13 = 10+n13;
                    } else if (m13 == m11) {
                        v13 = n13;
                    } else {
                        v13 = -1;
                    }
                    if (v13 > v11 && v13 > v12) {
                        win1 = cc;
                    }
                }

                //  fourth to play (dealer), 1st trick
                double p14 = 20;
                play1 = 6;
                int m14 = -1;
                int n14 = -1;
                int v14 = -1;
                maxsuit = -1;
                minsuit = 20;
                hitrump = -1; // overtrump 3rd seat in this instance
                sectrump = -1;
                lotrump = 8;
                worsts = -1; // worst suit
                worstr = -1; // worst rank

                if (lone == bb) { // if 2nd seat going alone (dealer skipped), pretend dealer played 9 of led suit
                    m14 = m11;
                    n14 = 0;
                    v14 = v13-1;
                } else {
                    if (playst[dd][m11] == 0) { // void in led suit
                        voids[dd][m11] = 1;
                    }
                    for (int i=0; i<4; i++) { // identify worst card
                        for (int j=0; j<8; j++) {
                            if (playst[dd][fintp] > 0) {
                                if (p14 > cv[0][i][j] && cv[0][i][j] > 0 && own[dd][i][j] == dd) {
                                    p14 = cv[0][i][j];
                                    worsts = i;
                                    worstr = j;
                                }
                            } else {
                                if (p14 > cv[1][i][j] && cv[1][i][j] > 0 && own[dd][i][j] == dd) {
                                    p14 = cv[1][i][j];
                                    worsts = i;
                                    worstr = j;
                                }
                            }
                        }
                    }

                    if (win1 == aa) { // first seat is winning
                        if (playst[dd][m11] > 0) { // can follow suit
                            for (int j=7; j>=0; j--) { // calculate lowest card which beats 1st seat, and lowest card in suit
                                if (own[dd][m11][j] == dd) {
                                    if (j > n11) { // maxsuit given a value IF can win
                                        maxsuit = j;
                                        play1 = 3;
                                    } else if (j < minsuit && maxsuit == -1) { // minsuit is default to use if maxsuit still = -1
                                        minsuit = j;
                                        play1 = 4;
                                    }
                                }
                            }
                        } else if (playst[dd][fintp] > 0) { // if can't follow suit, but can trump
                            if (declarer == dd && (right[dd][fintp] + left[dd][fintp] == 2) && playst[dd][fintp] == 2) {
                                // default to playing worst card if declared as dealer and only have both bowers
                                play1 = 6;
                            } else if (declarer == bb && playst[dd][fintp] == 4 && worstr == 5 && (right[dd][fintp] +
                                                                                                   left[dd][fintp] == 2)) {
                                hitrump = 7;
                                play1 = 0; // partner declared AND have 4 trump (including both bowers) AND off-suit is an Ace,
                                // win w/ R
                            } else {
                                for (int j=7; j>=0; j--) {
                                    if (own[dd][fintp][j] == dd && j < lotrump) { // lotrump given a value IF can trump for win
                                        lotrump = j;
                                        play1 = 2;
                                    }
                                }
                            }
                        } else {
                            play1 = 6;
                        }
                    }
                    if (win1 == bb) { // partner in 2nd seat is winning
                        if (playst[dd][m11] > 0) { // can follow suit (if can't follow suit, default will be to throw off worst card)
                            for (int j=7; j>=0; j--) { // calculate lowest card which beats partner, and lowest card in suit
                                if (own[dd][m11][j] == dd) {
                                    if (j > n12) {
                                        maxsuit = j; // maxsuit given a value IF beats partner
                                    }
                                    if (j < minsuit) {
                                        minsuit = j; // minsuit is default lowest card to follow suit
                                    }
                                }
                            }
                            if (m12 != fintp && declarer == bb && (right[dd][fintp] + left[dd][fintp] > 0) && maxsuit > -1) {
                                // if 2nd seat declared AND is winning
                                // w/o trump AND I can beat him AND I have a bower, player higher card (to lead bower next trick)
                                play1 = 3;
                            } else {
                                play1 = 4;
                            }
                        } else {
                            play1 = 6;
                        }
                    }
                    if (win1 == cc) { // 3rd seat winning
                        if (playst[dd][m11] > 0) { // can follow suit
                            for (int j=7; j>=0; j--) {
                                if (own[dd][m11][j] == dd) {
                                    if (j > n13 && m13 == m11) {
                                        maxsuit = j; // lowest card which beats 3rd seat, if they followed lead
                                        play1 = 3;
                                    } else if (j < minsuit && maxsuit == -1) {
                                        minsuit = j;
                                        play1 = 4;
                                    }
                                }
                            }
                        } else if (playst[dd][fintp] > 0 ) { // can't follow suit but can trump
                            for (int j=7; j>=0; j--) {
                                if (own[dd][fintp][j] == dd) {
                                    if (j > n13 && m13 == fintp) { // trump card which takes lead (beating 3rd seat if they also trumped)
                                        hitrump = j;
                                        play1 = 0;
                                    } else if (j < minsuit && m13 != fintp) {
                                        lotrump = j;
                                        play1 = 2;
                                    }
                                }
                            }
                        }
                    }

                    int cardplay = playfirst(play1, fintp, hitrump, sectrump, lotrump, m11, maxsuit, minsuit, suitace, worsts,
                                             worstr, dd, playst[dd][m11], cards);
                    m14 = cardplay%10;
                    n14 = (cardplay/10)%10;

                    System.out.println("Player " + position[dd] + " plays the " + cardname[m14][n14] + ".\n");
                    for (int i=0; i<4; i++) {
                        own[i][m14][n14] = -1;
                        length[i][m14]--;
                    }
                    playst[dd][m14] = playst[dd][m14] - 1;
                    cv[0][m14][n14] = 0;
                    cv[1][m14][n14] = 0;
                    if (n14 == 5 && m14 != fintp) {
                        aces[dd][fintp]--;
                    }
                    if (n14 == 5 && m14 == fintp) {
                        acet[dd][fintp] = 0;
                    }
                    suit[0][3] = m14;
                    rank[0][3] = n14;
                    if (m14 == fintp) {
                        v14 = 10+n14;
                    } else if (m14 == m11) {
                        v14 = n14;
                    } else {
                        v14 = -1;
                    }
                    if (v14 > v11 && v14 > v12 && v14 > v13) {
                        win1 = dd;
                    }
                }

                // calculate stats for first trick
                int tricktally = wintrick(win1, wturn);
                trick[tricktally]++;
                trick[tricktally+2]++;

                if (tricktally == 0) {
                    tns++;
                } else {
                    tew++;
                }

                //  if defending against lone, modify priority of discards
                if (lone > -1) {
                    for (int i=0; i<4; i++) { // cycle through players
                        for (int j=0; j<4; j++) { // cycle through suits
                            if (voids[i][j] == 1 && lone == i) {
                                for (int k=0; k<8; k++) { // cycle through ranks
                                    cv[1][j][k] = cv[1][j][k]/10;
                                }
                            }
                        }
                    }
                }

                // calculate boss (highest) rank in each suit; first reset to zero
                for (int i=0; i<4; i++) { // player
                    for (int j=0; j<4; j++) { // suit
                        solo[i][j] = 0;
                        solo[i][j] = 0;
                        solo[i][j] = 0;
                        solo[i][j] = 0;
                        boss[i][j] = 0;
                    }
                }
                for (int i=0; i<4; i++) { // player
                    for (int j=0; j<4; j++) { // suit
                        for (int k=0; k<8; k++) { // rank
                            if (own[i][j][k] > -1 && own[i][j][k] < 4 && k > boss[i][j]) {
                                boss[i][j] = k;
                            }
                        }
                    }
                }

                // check if any player is the only one with a particular suit
                for (int i=0; i<4; i++) { // suit
                    if (playst[0][i] > 0 && ((voids[1][i] + voids[2][i] + voids[3][i] == 3) || length[0][i] == playst[0][i])) {
                        solo[0][i] = 1;
                    }
                    if (playst[1][i] > 0 && ((voids[0][i] + voids[2][i] + voids[3][i] == 3) || length[1][i] == playst[1][i])) {
                        solo[1][i] = 1;
                    }
                    if (playst[2][i] > 0 && ((voids[0][i] + voids[1][i] + voids[3][i] == 3) || length[2][i] == playst[2][i])) {
                        solo[2][i] = 1;
                    }
                    if (playst[3][i] > 0 && ((voids[0][i] + voids[1][i] + voids[2][i] == 3) || length[3][i] == playst[3][i])) {
                        solo[3][i] = 1;
                    }
                }

                // re-check who holds bowers; first re-set to zero
                for (int i=0; i<4; i++) { // player
                    for (int j=0; j<4; j++) { // suit
                        right[i][j] = 0;
                        left[i][j] = 0;
                    }
                    for (int j=0; j<4; j++) { // suit
                        if (own[i][j][6] > -1) {
                            left[own[i][j][6]][j] = 1;
                        }
                        if (own[i][j][7] > -1) {
                            right[own[i][j][7]][j] = 1;
                        }
                    }
                }

                // establish new final shortcuts for second trick
                final int aa2 = win1; // 2nd trick lead
                final int bb2 = (win1+1)%4; // second to play
                final int cc2 = (win1+2)%4; // third to play
                final int dd2 = (win1+3)%4; // fourth to play

                // XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
                // play out second trick; first to play is winner of first trick
                wturn = 2; // trick 2
                double p21 = 20;
                int m21 = -1;
                int n21 = -1;
                int v21 = -1;
                hitrump = -1;
                lotrump = 20;
                maxsuit = -1;
                minsuit = suit[0][aa];
                playace = 1.05;
                suitace = -1;
                worsts = -1;
                worstr = -1;
                play1 = -1;
                win2 = win1;

                for (int i=0; i<4; i++) { // establishes best ace to led; not trump AND 5+ length suit
                    if (own[win1][i][5] == win1 && cv[0][i][5] > playace && i != fintp && length[win1][i] > 4) {
                        playace = cv[0][i][5];
                        suitace = i;
                    }
                }
                for (int i=0; i<8; i++) {
                    if (own[win1][fintp][i] == win1) {
                        if (i > hitrump) {
                            hitrump = i; // calculates rank of highest trump (0 - 7)
                        }
                        if (i < lotrump) {
                            lotrump = i; // calculates rank of lowest trump (0 - 7)
                        }
                    }
                }
                for (int j=0; j<8; j++) { // find max rank of same suit to re-play
                    if (own[win1][suit[0][aa]][j] == win1) { // find highest of same suit to lead
                        maxsuit = j;
                        minsuit = suit[0][aa]; // suit that win1 played on 1st trick
                    }
                }
                for (int i=0; i<4; i++) {  // find lowest card from shortest suit (lowest cv[0] value)
                    for (int j=0; j<8; j++) {
                        if (p21 > cv[0][i][j] && cv[0][i][j] > 0 && own[win1][i][j] == win1) {
                            p21 = cv[0][i][j];
                            worsts = i;
                            worstr = j;
                        }
                    }
                }
                if (lone == win1) { // if going lone
                    if (playst[win1][fintp] > 1 || hitrump == boss[win1][fintp]) { // if have 2+ trump or boss trump, play highest
                        play1 = 0;
                    } else if (suitace != -1) { // if have a good A to play
                        play1 = 5;
                    }
                } else if (lone == bb2 || lone == dd2) { // if opponent going lone
                    if (suit[0][aa] != fintp && playst[win1][suit[0][aa]] > 0) { // if won against lone w/o trump AND
                        //  have another of the same suit, play it (highest)
                        play1 = 3;
                    } else if (aces[win1][fintp] > 1) { // else if have 2 or more aces, play one
                        play1 = 5;
                    }
                } else if (win1 == declarer) { // I declared and get to lead
                    if (sit[0] == 1) {
                        play1 = 5; // 1st special situation: won 1st trick with R, should lead A 2nd trick
                    } else if (win1 == aa) { // win from 1st seat
                        if (rank[0][0] == 7 && (hitrump == boss[win1][fintp] || solo[win1][fintp] == 1) &&
                            playst[win1][fintp] > 1) { // if won with
                            // R and still have boss trump (or only trump) AND 2+ trump, play highest trump
                            play1 = 0;
                        } else if (rank[0][0] == 6 && own[win1][fintp][5] == win1 && playst[win1][fintp] > 1) { // if won with
                            // L AND have A of trump AND 2+ trump, play hi trump
                            play1 = 0;
                        } else if (rank[0][0] == 5 && suit[0][0] != fintp) { // won with non-trump A
                            if (playst[win1][fintp] > 1) { // if have 2+ trump, play lo trump
                                play1 = 2;
                            } else if (playst[win1][suit[0][aa]] > 0) { // else play same suit if possible
                                play1 = 3;
                            }
                        }
                    } else if (win1 == dealer || win1 == (dealer+2)%4) { // win from 2nd seat or as dealer
                        if (win1 == (dealer+2)%4 && uprk == 2 && round == 0 && playst[win1][fintp] > 0 && lotrump < 6 &&
                            aces[win1][fintp] > 0) {
                            // if in 2nd seat AND made dealer pick up R AND have A- trump AND at least one off-suit A,
                            // lead lo trump
                            play1 = 2;
                        } else if (win1 == (dealer+2)%4 && uprk == 5 && round == 0 && left[win1][fintp] == 1 && lotrump < 5) {
                            play1 = 2; // if in 2nd seat AND made dealer pick up A AND have L + a lower trump, lead lo trump
                        } else if (suit[0][aa] != fintp) { // if won w/o trump
                            if ((right[win1][fintp] + left[win1][fintp] > 0) && playst[win1][fintp] > 1) {
                                // if have a bower AND 2+ trump, lead hi trump
                                play1 = 0;
                            } else if (playst[win1][fintp] < 3 && playst[win1][suit[0][aa]] > 0 && (win1 == dealer || m12 != m14)) {
                                // repeat suit if possible AND have 2- trump, unless partner (dealer) followed suit
                                play1 = 3;
                            } else if (playst[win1][fintp] > 0) { // else play hi trump if have 3+ trump
                                play1 = 0;
                            }
                        } else if (suit[0][aa] == fintp) { // if won with trump
                            if (hitrump == boss[win1][fintp] && (aces[win1][fintp] > 0 || playst[win1][fintp] > 1)) { // if have
                                // boss trump AND (an off A OR
                                // 2+ trump), lead hi trump
                                play1 = 0;
                            } else if (aces[win1][fintp] > 0 && suit[0][0] == fintp) { // if have an off A AND trump led, lead A
                                play1 = 5;
                            }
                        }
                    } else if (win1 == ((dealer+3)%4) && suit[0][0] == fintp) { // win from 3rd seat, partner led trump
                        if (hitrump == boss[win1][fintp] && aces[win1][fintp] > 0) {
                            // if have boss trump AND an off A, lead hi trump
                            play1 = 0;
                        } else if (aces[win1][fintp] > 0) { // if have off A, lead it
                            play1 = 5;
                        }
                    } else if (win1 == (dealer+3)%4) { // win from 3rd seat, partner void in trump (would have led trump if
                        // had any)
                        if (suit[0][2] != fintp && hitrump == boss[win1][fintp] && playst[win1][fintp] > 1) {
                            // if won w/o trump AND have boss trump AND 2+ trump, play hi trump
                            play1 = 0;
                        } else if (suit[0][2] == fintp && playst[win1][fintp] > 0) { // won with trump, and have more trump
                            if (hitrump == boss[win1][fintp] || (left[win1][fintp] + acet[win1][fintp] == 2)) {
                                play1 = 0; // have boss trump OR L/A, play hi trump
                            } else if (playst[win1][fintp] > 1) { // else play lo trump (if have trump)
                                play1 = 2;
                            }
                        }
                    }
                } else if (declarer == cc2) { // partner declared
                    if (right[win1][fintp] + left[win1][fintp] > 0) { // if have bower, lead it
                        play1 = 0;
                    } else if (suit[0][aa] != fintp && playst[win1][suit[0][aa]] > 0 &&
                               suit[0][(win1-dealer+5)%4] != suit[0][aa]) {
                        // if won w/o trump AND partner threw off AND have another of same suit, repeat suit
                        play1 = 3;
                    } else if (win1 == aa) { // won from 1st seat
                        if (playst[win1][fintp] > 0 && aces[win1][fintp] > 0) { // if have trump AND an off-A, play lo trump
                            play1 = 2;
                        } else if (aces[win1][fintp] > 0) { // if have no trump but off-A, play off-A
                            play1 = 5;
                        }
                    } else if (win1 == (dealer+2)%4) { // won from 2nd seat
                        if (suit[0][1] != fintp) { // 1st trick won w/o trump
                            if ((playst[win1][fintp] + aces[win1][fintp] > 2) && playst[(dealer+2)%4][fintp] > 0) {
                                // if # of trump + # of off-Aces > 2, AND have trump, lead lo trump
                                play1 = 2;
                            }
                        } else { // 1st trick won w/ trump
                            if (suit[0][0] == fintp && aces[win1][fintp] > 0) { // if trump led AND have an off-A, lead off-A
                                play1 = 5;
                            } else if (aces[win1][fintp] > 0 && playst[win1][fintp] > 0) { // if trump not led AND have an off-A,
                                // lead lo trump
                                play1 = 2;
                            }
                        }
                    } else if (win1 == (dealer+3)%4) { // won from 3rd seat
                        if (playst[win1][fintp] > 0 && aces[win1][fintp] > 0) { // if have trump AND an off-A, lead lo trump
                            play1 = 2;
                        } else if (suit[0][0] == fintp && aces[win1][fintp] > 0) { // if trump led AND have an off-A, lead off-A
                            play1 = 5;
                        } else if (aces[win1][fintp] > 1) { // if have no trump AND 2+ off-Aces, lead off-A
                            play1 = 5;
                        }
                    } else if (win1 == dealer) { // win as dealer
                        if ((right[win1][fintp] + left[win1][fintp] > 0) && suit[0][1] != fintp) {
                            // have a bower AND partner didn't play trump 1st trick, lead lo trump
                            play1 = 0;
                        } else if (playst[win1][fintp] > 0 && suit[0][win1] != fintp) {
                            play1 = 2; // if have trump and didn't win first trick with trump, play low trump
                        } else if (aces[win1][fintp] > 0) {
                            play1 = 5; // if have an A, play one
                        }
                    }
                } else if (bb2 == declarer || dd2 == declarer) { // defending
                    if (suit[0][aa] != fintp && playst[win1][suit[0][aa]] > 0) {
                        // if won w/o trump, re-lead same suit if possible
                        play1 = 3;
                    } else if (aces[win1][fintp] > 0) { // if have an off-A, play it
                        play1 = 5;
                    }
                }
                if (play1 == -1) {
                    play1 = 6;
                }
                //if leading a non-trump non-ace and have the A of that suit as well, lead the A instead
                if (worsts == suitace && play1 == 6) {
                    play1 = 5;
                }

                int cardplay = playfirst(play1, fintp, hitrump, sectrump, lotrump, m21, maxsuit, minsuit, suitace, worsts,
                                         worstr, win1, 0, cards);
                m21 = cardplay%10;
                n21 = (cardplay/10)%10;

                System.out.println("Player " + position[win1] + " leads the " + cardname[m21][n21] + ".\n");
                for (int i=0; i<4; i++) {
                    own[i][m21][n21] = -1;
                    length[i][m21]--;
                }
                playst[win1][m21] = playst[win1][m21] - 1;
                cv[0][m21][n21] = 0;
                cv[1][m21][n21] = 0;
                if (n21 == 5 && m21 != fintp) {
                    aces[win1][fintp]--;
                }
                suit[1][0] = m21;
                rank[1][0] = n21;
                if (m21 == fintp) {
                    v21 = 10+n21;
                } else {
                    v21 = n21;
                }

                // second to play, 2nd trick
                double p22 = 20;
                play1 = -1;
                int m22 = -1;
                int n22 = -1;
                int v22 = -1;
                hitrump = -1;
                sectrump = -1;
                lotrump = 20;
                maxsuit = -1; // highest trump
                minsuit = 20; // lowest trump
                worsts = -1; // worst suit
                worstr = -1; // worst rank

                if (lone == dd2) {
                    m22 = m21;  // if partner going alone, skip me, so assign same value to my 'play' as win1
                    n22 = n21;
                    v22 = v21-1;
                } else {  // partner not going alone
                    if (playst[bb2][m21] == 0) { // void in led suit
                        voids[bb2][m21] = 1;
                    }
                    for (int i=0; i<4; i++) {  // identify worst card
                        for (int j=0; j<8; j++) {
                            if (playst[bb2][fintp] > 0) {
                                if (p22 > cv[0][i][j] && cv[0][i][j] > 0 && own[bb2][i][j] == bb2) {
                                    p22 = cv[0][i][j];
                                    worsts = i;
                                    worstr = j;
                                }
                            } else {
                                if (p22 > cv[1][i][j] && cv[1][i][j] > 0 && own[bb2][i][j] == bb2) {
                                    p22 = cv[1][i][j];
                                    worsts = i;
                                    worstr = j;
                                }
                            }
                        }
                    }

                    if (playst[bb2][m21] > 0) { // can follow suit
                        for (int j=0; j<8; j++) {
                            if (own[bb2][m21][j] == bb2) {
                                if (j > maxsuit) {
                                    maxsuit = j;
                                }
                                if (j < minsuit) {
                                    minsuit = j;
                                }
                            }
                        }
                        if (maxsuit > n21) {
                            play1 = 3;
                        } else {
                            play1 = 4;
                        }
                    } else if (m21 != fintp && playst[bb2][fintp] > 0) { // can't follow suit but can trump
                        for (int j=0; j<8; j++) {
                            if (own[bb2][fintp][j] == bb2) { // determine best and worst cards in trump suit
                                if (hitrump == -1) {
                                    hitrump = j;
                                    sectrump = j;
                                } else if (j > hitrump) {
                                    sectrump = hitrump;
                                    hitrump = j;
                                }
                                if (j < lotrump) {
                                    lotrump = j;
                                }
                            }
                        }
                        if (lone == win1) { // opponent going alone, trump with lowest
                            play1 = 2;
                        } else if (lone == bb2) { // if going lone, trump with second highest
                            play1 = 1;
                        } else if (declarer == dd2 && (right[bb2][fintp] + left[bb2][fintp] > 0) &&
                                   (dealer != bb2 || (uprk != 2 || round != 0))) {
                            // if partner declared AND have bower AND did not pick up R as dealer, trump w/ bower
                            play1 = 0;
                        } else if (declarer == dd2 && lotrump < 7) { // if partner declared and have non-R trump, play lo trump
                            play1 = 2;
                        } else if (rank[1][0] > 3 || (declarer == bb2 && playst[bb2][fintp] > 2) || ((declarer == win1 ||
                                                                                                      declarer == cc2)
                                                                                                     && playst[bb2][fintp] > 1)) { // if A or K led OR have 3+ trump as declarer OR have 2+ trump
                            // as defender, play lo trump
                            play1 = 2;
                        } else {
                            play1 = 6;
                            if (worsts != fintp) {
                                hint[bb2][worsts] = 1;
                            }
                        }
                    } else { // throw off
                        play1 = 6;
                        if (worsts != fintp) {
                            hint[bb2][worsts] = 1;
                        }
                    }

                    cardplay = playfirst(play1, fintp, hitrump, sectrump, lotrump, m21, maxsuit, minsuit, suitace, worsts,
                                         worstr, bb2, playst[bb2][m21], cards);
                    m22 = cardplay%10;
                    n22 = (cardplay/10)%10;

                    System.out.println("Player " + position[bb2] + " plays the " + cardname[m22][n22] + ".\n");
                    for (int i=0; i<4; i++) {
                        own[i][m22][n22] = -1;
                        length[i][m22]--;
                    }
                    playst[bb2][m22] = playst[bb2][m22] - 1;
                    cv[0][m22][n22] = 0;
                    cv[1][m22][n22] = 0;
                    if (n22 == 5 && m22 != fintp) {
                        aces[bb2][fintp]--;
                    }
                    suit[1][1] = m22;
                    rank[1][1] = n22;
                    if (m22 == fintp) {
                        v22 = 10+n22;
                    } else if (m22 == m21) {
                        v22 = n22;
                    } else {
                        v22 = -1;
                    }
                    if (v22 > v21) {
                        win2 = bb2;
                    }
                }

                // third to play, 2nd trick
                double p23 = 20;
                play1 = -1;
                int m23 = -1;
                int n23 = -1;
                int v23 = -1;
                maxsuit = -1; // highest trump
                minsuit = 20; // lowest trump
                hitrump = -1;
                sectrump = -1;
                lotrump = 20;
                worsts = -1; // worst suit
                worstr = -1; // worst rank

                if (lone == win1) {
                    m23 = m21;  // if partner going alone, skip me, so assign same value to my 'play' as win1
                    n23 = n21;
                    v23 = v22-1;
                } else { // partner not going alone
                    if (playst[cc2][m21] == 0) { // void in led suit
                        voids[cc2][m21] = 1;
                    }
                    for (int i=0; i<4; i++) {  // identify worst card
                        for (int j=0; j<8; j++) {
                            if (playst[cc2][fintp] > 0) {
                                if (p23 > cv[0][i][j] && cv[0][i][j] > 0 && own[cc2][i][j] == cc2) {
                                    p23 = cv[0][i][j];
                                    worsts = i;
                                    worstr = j;
                                }
                            } else {
                                if (p23 > cv[1][i][j] && own[cc2][i][j] == cc2) {
                                    p23 = cv[1][i][j];
                                    worsts = i;
                                    worstr = j;
                                }
                            }
                        }
                    }

                    if (playst[cc2][m21] > 0) { // can follow suit
                        for (int j=0; j<8; j++) {
                            if (own[cc2][m21][j] == cc2) {
                                if (j > maxsuit) {
                                    maxsuit = j;
                                }
                                if (j < minsuit) {
                                    minsuit = j;
                                }
                            }
                        }
                        if (maxsuit > n21+1 && win2 == win1) { // if partner winning AND can beat them by 2+ ranks, play hi
                            // card and take lead
                            play1 = 3;
                        } else if (maxsuit > n22 && win2 == bb2 && m21 == m22) { // if opponent winning w/o trump AND can beat
                            // them, play hi card
                            play1 = 3;
                        } else { // if can't take lead, throw off
                            play1 = 4;
                        }
                    } else if (m21 != fintp && playst[cc2][fintp] > 0) { // can't follow suit but can trump (and trump not led)
                        for (int j=0; j<8; j++) {
                            if (own[cc2][fintp][j] == cc2) {
                                if (hitrump == -1) {
                                    hitrump = j;
                                    sectrump = j;
                                } else if (j > hitrump) {
                                    sectrump = hitrump;
                                    hitrump = j;
                                }
                                if (j < lotrump) {
                                    lotrump = j;
                                }
                            }
                        }
                        if (lone == dd2) { // opponent playing after me is going lone
                            if (n21 == boss[cc2][m21] && playst[cc2][fintp] > 1 && left[cc2][fintp] == 1) {
                                // if p led boss card (so won 1st trick) AND I have guarded L, play off (hope for euchre)
                                play1 = 6;
                                if (worsts != fintp) {
                                    hint[cc2][worsts] = 1;
                                }
                            } else { // else play high trump
                                play1 = 0;
                            }
                        } else if (lone == bb2 && win2 == bb2 && m22 != fintp) {
                            // if opponent playing before me is going lone and is winning w/o trump, play lo trump
                            play1 = 2;
                        } else if (lone == bb2 && win2 == win1) {
                            // if opponent playing before me is going alone and followed suit and p is winning, play off
                            play1 = 6;
                            if (worsts != fintp) {
                                hint[cc2][worsts] = 1;
                            }
                        } else if (m22 != fintp) { // opponent followed suit (non-trump) or played off
                            if (n21 == boss[cc2][m21] && length[cc2][m21] > 2) {
                                // if p leading with boss card AND 3+ left in that suit, play off
                                play1 = 6;
                                if (worsts != fintp) {
                                    hint[cc2][worsts] = 1;
                                }
                            } else if (m21 == m11 || playst[cc2][fintp] > 2) { // if p re-led same suit OR have 3+ trump,
                                // play second highest trump
                                play1 = 1;
                            } else if (declarer == win1 && (right[cc2][fintp] + left[cc2][fintp] > 0)) {
                                play1 = 0;
                            } else {
                                play1 = 2;
                            }
                        } else if (m22 == fintp) { // opponent (2nd player) trumped
                            if (lotrump > n22) {
                                play1 = 2;
                            } else if (sectrump > n22) {
                                play1 = 1;
                            } else if (hitrump > n22) {
                                play1 = 0;
                            } else {
                                play1 = 6;
                                if (worsts != fintp) {
                                    hint[cc2][worsts] = 1;
                                }
                            }
                        }
                    } else { //throw off
                        play1 = 6;
                        if (worsts != fintp) {
                            hint[cc2][worsts] = 1;
                        }
                    }

                    cardplay = playfirst(play1, fintp, hitrump, sectrump, lotrump, m21, maxsuit, minsuit, suitace, worsts,
                                         worstr, cc2, playst[cc2][m21], cards);
                    m23 = cardplay%10;
                    n23 = (cardplay/10)%10;

                    System.out.println("Player " + position[cc2] + " plays the " + cardname[m23][n23] + ".\n");
                    for (int i=0; i<4; i++) {
                        own[i][m23][n23] = -1;
                        length[i][m23]--;
                    }
                    playst[cc2][m23] = playst[cc2][m23] - 1;
                    cv[0][m23][n23] = 0;
                    cv[1][m23][n23] = 0;
                    if (n23 == 5 && m23 != fintp) {
                        aces[cc2][fintp]--;
                    }
                    suit[1][2] = m23;
                    rank[1][2] = n23;
                    if (m23 == fintp) {
                        v23 = 10+n23;
                    } else if (m23 == m21) {
                        v23 = n23;
                    } else {
                        v23 = -1;
                    }
                    if (v23 > v21 && v23 > v22) {
                        win2 = cc2;
                    }
                }

                // fourth to play, 2nd trick
                double p24 = 20;
                play1 = -1;
                int m24 = -1;
                int n24 = -1;
                int v24 = -1;
                maxsuit = -1; // highest card following suit
                minsuit = 20; // lowest card following suit
                hitrump = 20;
                lotrump = 20;
                worsts = -1;
                worstr = -1;

                if (lone == bb2) {
                    m24 = m22;  // if partner going alone, skip me, pretend I played 9 of led suit
                    n24 = 0;
                    v24 = v23-1;
                } else { // partner not going alone
                    if (playst[dd2][m21] == 0) { // void in led suit
                        voids[dd2][m21] = 1;
                    }
                    for (int i=0; i<4; i++) {
                        for (int j=0; j<8; j++) {
                            if (playst[dd2][fintp] > 0) {
                                if (p24 > cv[0][i][j] && cv[0][i][j] > 0 && own[dd2][i][j] == dd2) {
                                    p24 = cv[0][i][j];
                                    worsts = i;
                                    worstr = j;
                                    if (worsts != fintp) {
                                        hint[dd2][worsts] = 1;
                                    }
                                }
                            } else {
                                if (p24 > cv[1][i][j] && cv[1][i][j] > 0 && own[dd2][i][j] == dd2) {
                                    p24 = cv[1][i][j];
                                    worsts = i;
                                    worstr = j;
                                    if (worsts != fintp) {
                                        hint[dd2][worsts] = 1;
                                    }
                                }
                            }
                        }
                    }
                    for (int j=7; j>=0; j--) { // calculate lowest trump which beats 3rd player (if trumped and winning)
                        if (own[dd2][fintp][j] == dd2 && j < hitrump && m23 == fintp && j > n23) {
                            hitrump = j;
                        }
                        if (own[dd2][fintp][j] == dd2 && j < lotrump) { // calculate lowest trump if can win trick and first
                            // to trump
                            lotrump = j;
                        }
                    }
                    for (int j=7; j>=0; j--) { // calculate maxsuit and minsuit
                        if (own[dd2][m21][j] == dd2) { // have this card in hand
                            if (win2 == win1 && j > n21) { // 1st seat winning, identify lowest card which wins trick
                                maxsuit = j;
                            } else if (win2 == bb2 && declarer == bb2 && m22 != fintp &&
                                       (own[dd2][fintp][7] == dd2 || own[dd2][fintp][6] == dd2) && j > n22) {
                                // 2nd seat winning, identify lowest card which wins trick IF partner declared and have a
                                // bower to lead next round
                                maxsuit = j;
                            } else if (win2 == cc2 && j > n23) { // 3rd seat winning, identify lowest card which wins trick
                                maxsuit = j;
                            } else { // else identify worst card in suit to play
                                minsuit = j;
                            }
                        }
                    }

                    if (playst[dd2][m21] > 0) { // can follow suit
                        if (maxsuit != -1) {
                            play1 = 3;
                        } else {
                            play1 = 4;
                        }
                    } else if (playst[dd2][fintp] > 0) { // can't follow suit but can trump
                        if (win2 == win1) { // player who led trick is winning, play lowest trump
                            play1 = 2;
                        } else if (win2 == cc2) { // 3rd to play winning
                            if (m23 == fintp && hitrump != 20) { // overtrump if possible
                                play1 = 0;
                            } else if (m23 != fintp) { // play lowest trump if first to trump
                                play1 = 2;
                            }
                        }
                    }

                    cardplay = playfirst(play1, fintp, hitrump, sectrump, lotrump, m21, maxsuit, minsuit, suitace, worsts,
                                         worstr, dd2, playst[dd2][m21], cards);
                    m24 = cardplay%10;
                    n24 = (cardplay/10)%10;

                    System.out.println("Player " + position[dd2] + " plays the " + cardname[m24][n24] + ".\n");
                    for (int i=0; i<4; i++) {
                        own[i][m24][n24] = -1;
                        length[i][m24]--;
                    }
                    playst[dd2][m24] = playst[dd2][m24] - 1;
                    cv[0][m24][n24] = 0;
                    cv[1][m24][n24] = 0;
                    if (n24 == 5 && m24 != fintp) {
                        aces[dd2][fintp]--;
                    }
                    suit[1][3] = m24;
                    rank[1][3] = n24;
                    if (m24 == fintp) {
                        v24 = 10+n24;
                    } else if (m24 == m21) {
                        v24 = n24;
                    } else {
                        v24 = -1;
                    }
                    if (v24 > v21 && v24 > v22 && v24 > v23) {
                        win2 = dd2;
                    }
                }

                // calculate stats for second trick
                tricktally = wintrick(win2, wturn);
                trick[tricktally]++;
                trick[tricktally+2]++;
                if (tricktally == 0) {
                    tns++;
                } else {
                    tew++;
                }

                //  if defending against lone, modify priority of discards
                if (lone > -1) {
                    for (int j=0; j<4; j++) { // cycle through suits
                        for (int k=0; k<8; k++) { // cycle through ranks
                            if (own[dealer][j][k] != lone && voids[lone][j] == 1) {
                                cv[1][j][k] = cv[1][j][k]/10;
                            }
                        }
                    }
                }

                // calculate if any player is single suited
                for (int i=0; i<4; i++) {
                    cksuit[i] = -1;
                    ss[i] = -1;
                }
                for (int i=0; i<4; i++) { // player
                    for (int j=0; j<4; j++) { // suit
                        for (int k=0; k<8; k++) { // rank
                            if (own[i][j][k] == i) {
                                if (cksuit[i] == -1 || cksuit[i] == j) {
                                    cksuit[i] = j;
                                } else {
                                    cksuit[i] = -2;
                                }
                            }
                        }
                    }
                    if (cksuit[i] > -1) {
                        ss[i] = cksuit[i];
                    }
                }

                // calculate boss (highest) rank in each suit; first reset to zero
                for (int i=0; i<4; i++) { // player
                    for (int j=0; j<4; j++) { // suit
                        solo[i][j] = 0;
                        solo[i][j] = 0;
                        solo[i][j] = 0;
                        solo[i][j] = 0;
                        boss[i][j] = 0;
                    }
                }
                for (int i=0; i<4; i++) { // player
                    for (int j=0; j<4; j++) { // suit
                        for (int k=0; k<8; k++) { // rank
                            if (own[i][j][k] > -1 && own[i][j][k] < 4 && k > boss[i][j]) {
                                boss[i][j] = k;
                            }
                        }
                    }
                }

                // check if any player is the only one with a particular suit
                for (int i=0; i<4; i++) { // suit
                    if (playst[0][i] > 0 && ((voids[1][i] + voids[2][i] + voids[3][i] == 3) || length[0][i] == playst[0][i])) {
                        solo[0][i] = 1;
                    }
                    if (playst[1][i] > 0 && ((voids[0][i] + voids[2][i] + voids[3][i] == 3) || length[1][i] == playst[1][i])) {
                        solo[1][i] = 1;
                    }
                    if (playst[2][i] > 0 && ((voids[0][i] + voids[1][i] + voids[3][i] == 3) || length[2][i] == playst[2][i])) {
                        solo[2][i] = 1;
                    }
                    if (playst[3][i] > 0 && ((voids[0][i] + voids[1][i] + voids[2][i] == 3) || length[3][i] == playst[3][i])) {
                        solo[3][i] = 1;
                    }
                }

                // re-check who holds bowers; first re-set to zero
                for (int i=0; i<4; i++) { // player
                    for (int j=0; j<4; j++) { // suit
                        right[i][j] = 0;
                        left[i][j] = 0;
                    }
                    for (int j=0; j<4; j++) { // suit
                        if (own[i][j][6] > -1) {
                            left[own[i][j][6]][j] = 1;
                        }
                        if (own[i][j][7] > -1) {
                            right[own[i][j][7]][j] = 1;
                        }
                    }
                }

                // establish new final shortcuts for third trick
                final int aa3 = win2; // 3rd trick lead
                final int bb3 = (win2+1)%4; // second to play
                final int cc3 = (win2+2)%4; // third to play
                final int dd3 = (win2+3)%4; // fourth to play

                // XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
                // play out third trick; first to play is winner of second trick
                wturn = 3; // trick 3
                play1 = -1;
                double p31 = 20;
                double p32 = -1;
                int m31 = -1;
                int n31 = -1;
                int v31 = -1;
                hitrump = -1;
                sectrump = -1;
                lotrump = 20;
                maxsuit = -1;
                minsuit = suit[1][(win2-win1+4)%4];
                int bos = -1;
                suitace = -1;
                worsts = -1; // worst suit
                worstr = -1; // worst rank
                win3 = win2;

                for (int j=0; j<8; j++) { // calculate highest and lowest trump
                    if (own[aa3][fintp][j] == aa3) {
                        if (j > hitrump) {
                            hitrump = j;
                        }
                        if (j < lotrump) {
                            lotrump = j;
                        }
                    }
                }
                for (int i=0; i<4; i++) { // calculate if have boss card in any non-trump suit
                    if (own[aa3][i][boss[aa3][i]] == aa3 && i != fintp) {
                        bos = i;
                        cv[0][i][boss[aa3][i]] = .95; // make this boss card the best one to lead
                    }
                }
                for (int j=0; j<8; j++) { // find max rank of same suit to re-play
                    if (own[aa3][suit[1][(win2-win1+4)%4]][j] == aa3) { // find highest of same suit to lead
                        maxsuit = j; // actually the rank
                        minsuit = suit[1][(win2-win1+4)%4]; // the suit
                    }
                }
                for (int i=0; i<4; i++) { // determine best card to lead
                    for (int j=0; j<8; j++) {
                        if (cv[0][i][j] > p32 && own[aa3][i][j] == aa3 && i != fintp) {
                            p32 = cv[0][i][j];
                            suitace = i; // suit
                            sectrump = j; // rank
                        }
                    }
                }
                for (int i=0; i<4; i++) { // determine worst card to lead
                    for (int j=0; j<8; j++) {
                        if (cv[0][i][j] < p31 && cv[0][i][j] != 0 && own[aa3][i][j] == aa3 && i != fintp) {
                            p31 = cv[0][i][j];
                            worsts = i;
                            worstr = j;
                        }
                    }
                }

                // if single-suited, play highest
                if (playst[aa3][fintp] == 3) { // have 3 trump left, lead highest
                    play1 = 0;
                } else if (ss[aa3] < 4 && ss[aa3] > -1) {
                    play1 = 5;
                } else if (lone == aa3) { // if going lone
                    if (own[aa3][fintp][boss[aa3][fintp]] == aa3 || solo[aa3][fintp] == 1 || playst[aa3][fintp] > 1) {
                        // if have boss trump or 2+ trump or only player with trump, lead highest trump
                        play1 = 0;
                    } else if (voids[bb3][fintp] == 1 && voids[dd3][fintp] == 1 && playst[aa3][fintp] > 1) {
                        //if know both opponents are void in trump, lead highest trump
                        play1 = 0;
                    } else { // lead best non-trump
                        play1 = 5;
                    }
                } else if (lone == bb3 || lone == dd3) { // if opponent going lone
                    if (suit[1][(win2-win1+4)%4] != fintp && playst[aa3][suit[1][(win2-win1+4)%4]] > 0) { // if won w/o trump
                        // lead same suit if have it
                        play1 = 3;
                    } else { // lead best non-trump
                        play1 = 5;
                    }
                } else if (declarer == aa3) { // I am declarer
                    if (solo[aa3][fintp] == 1) { // if have all the remaining trump, lead highest
                        play1 = 0;
                    } else if (playst[aa3][fintp] > 1 && ((fintp == upst && uprk == 2 && dealer == cc3) ||
                                                          (fintp == 3-upst && uprk == 2 && length[aa3][fintp] > 5))) {
                        // if partner (dealer) picked up R OR bid next 2nd round and bower was buried, lead low trump
                        play1 = 2;
                    } else if ((own[aa3][fintp][boss[aa3][fintp]] == aa3 || solo[aa3][fintp] == 1 || playst[aa3][fintp] > 1) &&
                               (voids[bb3][fintp] + voids[dd3][fintp] != 2) && trick[aa3] != 1) {
                        // if have boss trump or only player left with trump or have 2+ trump,
                        // AND opponents may still have trump AND have won 2 tricks
                        play1 = 0; // lead high trump
                    } else {
                        play1 = 5; // lead best non-trump
                    }
                } else if (declarer == cc3) { // partner is declarer
                    if (solo[aa3][fintp] == 1) { // if have all remaining trump, lead highest
                        play1 = 0;
                    } else if ((right[aa3][fintp] + left[aa3][fintp]) > 0 && bos > -1) {
                        // if have a bower AND a boss off-suit, lead highest trump
                        play1 = 0;
                    } else { // lead best non-trump
                        play1 = 5;
                    }
                } else if ((declarer == bb3 || declarer == dd3) && playst[aa3][fintp] > 1 && length[aa3][fintp] < 6 &&
                           bos != -1) {
                    // defending AND have 2 trump AND round of trump already played AND have a boss off-suit, lead low trump
                    play1 = 2;
                } else {
                    play1 = 6;
                }

                // if leading a non-trump non-ace and have the A of that suit as well, lead the A instead
                if (worsts == suitace && play1 == 6) {
                    play1 = 5;
                }

                cardplay = playfirst(play1, fintp, hitrump, sectrump, lotrump, m31, maxsuit, minsuit, suitace, worsts,
                                     worstr, aa3, -1, cards);
                m31 = cardplay%10;
                n31 = (cardplay/10)%10;

                System.out.println("Player " + position[aa3] + " leads the " + cardname[m31][n31] + ".\n");
                for (int i=0; i<4; i++) {
                    own[i][m31][n31] = -1;
                    length[i][m31]--;
                }
                playst[aa3][m31] = playst[aa3][m31] - 1;
                cv[0][m31][n31] = 0;
                cv[1][m31][n31] = 0;
                if (n31 == 5 && m31 != fintp) {
                    aces[aa3][fintp]--;
                }
                suit[2][0] = m31;
                rank[2][0] = n31;
                if (m31 == fintp) {
                    v31 = 10+n31;
                } else {
                    v31 = n31;
                }

                // second to play, 3rd trick
                p32 = 20;
                play1 = -1;
                int m32 = -1;
                int n32 = -1;
                int v32 = -1;
                maxsuit = -1;
                sectrump = -1;
                minsuit = 20;
                hitrump = -1;
                lotrump = 20;
                playace = 1.195;
                suitace = -1;
                worsts = -1; // worst suit
                worstr = -1; // worst rank
                //            int keep = 0; // if have 2 trump AND a boss card off-suit, trump low if can ??

                if (lone == dd3) {
                    m32 = m31;  // if partner going alone, skip me, so assign same value to my 'play' as win1
                    n32 = n31;
                    v32 = v31-1;
                } else {  // partner not going alone
                    if (playst[bb3][m31] == 0) { // void in led suit
                        voids[bb3][m31] = 1;
                    }
                    for (int i=0; i<4; i++) {  // identify worst card
                        for (int j=0; j<8; j++) {
                            if (playst[bb3][fintp] > 0) {
                                if (p32 > cv[0][i][j] && cv[0][i][j] > 0 && own[bb3][i][j] == bb3) {
                                    p32 = cv[0][i][j];
                                    worsts = i;
                                    worstr = j;
                                }
                            } else {
                                if (p32 > cv[1][i][j] && cv[1][i][j] > 0 && own[bb3][i][j] == bb3) {
                                    p32 = cv[1][i][j];
                                    worsts = i;
                                    worstr = j;
                                }
                            }
                        }
                    }
                    for (int j=0; j<8; j++) { // identify best and worst rank in led suit
                        if (own[bb3][m31][j] == bb3) {
                            if (j > maxsuit) {
                                maxsuit = j;
                            }
                            if (j < minsuit) {
                                minsuit = j;
                            }
                        }
                    }
                    for (int j=0; j<8; j++) { // identify highest, 2nd highest and lowest trump
                        if (own[bb3][fintp][j] == bb3) {
                            if (hitrump == -1) {
                                hitrump = j;
                                sectrump = j;
                            } else if (j > hitrump) {
                                sectrump = hitrump;
                                hitrump = j;
                            }
                            if (j < lotrump) {
                                lotrump = j;
                            }
                        }
                    }

                    if (playst[bb3][m31] > 0) { // can follow suit
                        if (maxsuit > n31) { // if can take lead, play highest
                            play1 = 3;
                        } else { // else play lowest
                            play1 = 4;
                        }
                    } else if (m31 != fintp && playst[bb3][fintp] > 0) { // can't follow suit but can trump
                        if (declarer == dd3) { // partner declared
                            if (right[bb3][fintp] + left[bb3][fintp] > 0) {
                                // trump with bower if have one and dealer (partner) is declarer
                                play1 = 0;
                            } else { // else if have no bower, trump with lowest
                                play1 = 2;
                            }
                        } else if (lone == bb3) { // if going lone, trump with second highest
                            play1 = 1;
                        } else if ((n31 == boss[bb3][m31] && lotrump != 7) || lone == aa3 || length[bb3][fintp] < 5 ||
                                   trick[bb3] == 0) {
                            // play lowest trump if defending against lone OR boss card led (and lowest trump is not right) OR
                            // < 5 trump left OR lost 1st 2 tricks
                            play1 = 2;
                        } else {
                            play1 = 6;
                            if (worsts != fintp) {
                                hint[cc3][worsts] = 1;
                            }
                        }
                    } else { // throw off
                        play1 = 6;
                        if (worsts != fintp) {
                            hint[cc3][worsts] = 1;
                        }
                    }

                    cardplay = playfirst(play1, fintp, hitrump, sectrump, lotrump, m31, maxsuit, minsuit, suitace, worsts,
                                         worstr, bb3, playst[bb3][m31], cards);
                    m32 = cardplay%10;
                    n32 = (cardplay/10)%10;

                    System.out.println("Player " + position[bb3] + " plays the " + cardname[m32][n32] + ".\n");
                    for (int i=0; i<4; i++) {
                        own[i][m32][n32] = -1;
                        length[i][m32]--;
                    }
                    playst[bb3][m32] = playst[bb3][m32] - 1;
                    cv[0][m32][n32] = 0;
                    cv[1][m32][n32] = 0;
                    if (n32 == 5 && m32 != fintp) {
                        aces[bb3][fintp]--;
                    }
                    suit[2][1] = m32;
                    rank[2][1] = n32;
                    if (m32 == fintp) {
                        v32 = 10+n32;
                    } else if (m32 == m31) {
                        v32 = n32;
                    } else {
                        v32 = -1;
                    }
                    if (v32 > v31) {
                        win3 = bb3;
                    }
                }

                // third to play, 3rd trick
                double p33 = 20;
                play1 = -1;
                int m33 = -1;
                int n33 = -1;
                int v33 = -1;
                maxsuit = -1; // highest trump
                minsuit = 20; // lowest trump
                hitrump = -1;
                sectrump = -1;
                lotrump = 20;
                worsts = -1; // worst suit
                worstr = -1; // worst rank

                if (lone == aa3) {
                    m33 = m31;  // if partner going alone, skip me, so assign same value to my 'play' as win2
                    n33 = n31;
                    v33 = v32-1;
                } else { // partner not going alone
                    if (playst[cc3][m31] == 0) { // void in led suit
                        voids[cc3][m31] = 1;
                    }
                    for (int i=0; i<4; i++) {  // identify worst card
                        for (int j=0; j<8; j++) {
                            if (playst[cc3][fintp] > 0) {
                                if (p33 > cv[0][i][j] && cv[0][i][j] > 0 && own[cc3][i][j] == cc3) {
                                    p33 = cv[0][i][j];
                                    worsts = i;
                                    worstr = j;
                                }
                            } else {
                                if (p33 > cv[1][i][j] && cv[1][i][j] > 0 && own[cc3][i][j] == cc3) {
                                    p33 = cv[1][i][j];
                                    worsts = i;
                                    worstr = j;
                                }
                            }
                        }
                    }
                    for (int j=0; j<8; j++) { // identify highest and lowest rank, following suit
                        if (own[cc3][m31][j] == cc3) {
                            if (j > maxsuit) {
                                maxsuit = j;
                            }
                            if (j < minsuit) {
                                minsuit = j;
                            }
                        }
                    }
                    for (int j=0; j<8; j++) { // identify highest, 2nd highest and lowest trump
                        if (own[cc3][fintp][j] == cc3) {
                            if (hitrump == -1) {
                                hitrump = j;
                                sectrump = j;
                            } else if (j > hitrump) {
                                sectrump = hitrump;
                                hitrump = j;
                            }
                            if (j < lotrump) {
                                lotrump = j;
                            }
                        }
                    }

                    if (playst[cc3][m31] > 0) { // can follow suit
                        if (maxsuit > n31+1 && win3 == win2) {
                            play1 = 3;
                        } else if (maxsuit > n32 && win3 == bb3 && m31 == m32) {
                            play1 = 3;
                        } else {
                            play1 = 4;
                        }
                    } else if (m31 != fintp && playst[cc3][fintp] > 0) { // can't follow suit but can trump
                        if ((right[cc3][fintp] + left[cc3][fintp] > 0) &&
                            declarer == aa3 && (n31 != boss[cc3][m31] || (win3 == bb3) && m32 != fintp)) {
                            // trump with bower if have one AND partner is declarer AND either partner didn't play boss or 2nd
                            // player is winning w/o trump
                            play1 = 0;
                        } else if (lone == dd3) { // if next to play is going lone, play highest trump
                            play1 = 0;
                        } else if (win3 == bb3 && ((m32 == fintp && lotrump > n32) || m32 != fintp)) { // if 2nd to play is
                            // winning AND (they played trump AND my lowest trump is higher) OR they didn't play trump,
                            // play lowest trump
                            play1 = 2;
                        } else if (win3 == bb3 && m32 == fintp && sectrump > n32) { // if 2nd to play is winning w/ trump and
                            // my 2nd highest trump beats them, play 2nd highest trump
                            play1 = 1;
                        } else if (win3 == bb3 && m32 == fintp && maxsuit > n32) { // if 2nd to play is winning w/ trump and
                            // my highest trump beats them, play highest trump
                            play1 = 0;
                        } else if (win3 == aa3 && n31 != boss[cc3][m31]) {
                            play1 = 2; // if partner is winning BUT didn't play boss card, play lowest trump
                        } else {
                            play1 = 6;
                            if (worsts != fintp) {
                                hint[cc3][worsts] = 1;
                            }
                        }
                    } else { //throw off
                        play1 = 6;
                        if (worsts != fintp) {
                            hint[cc3][worsts] = 1;
                        }
                    }

                    cardplay = playfirst(play1, fintp, hitrump, sectrump, lotrump, m31, maxsuit, minsuit, suitace, worsts,
                                         worstr, cc3, playst[cc3][m31], cards);
                    m33 = cardplay%10;
                    n33 = (cardplay/10)%10;

                    System.out.println("Player " + position[cc3] + " plays the " + cardname[m33][n33] + ".\n");
                    for (int i=0; i<4; i++) {
                        own[i][m33][n33] = -1;
                        length[i][m33]--;
                    }
                    playst[cc3][m33] = playst[cc3][m33] - 1;
                    cv[0][m33][n33] = 0;
                    cv[1][m33][n33] = 0;
                    if (n33 == 5 && m33 != fintp) {
                        aces[cc3][fintp]--;
                    }
                    suit[2][2] = m33;
                    rank[2][2] = n33;
                    if (m33 == fintp) {
                        v33 = 10+n33;
                    } else if (m33 == m31) {
                        v33 = n33;
                    } else {
                        v33 = -1;
                    }
                    if (v33 > v31 && v33 > v32) {
                        win3 = cc3;
                    }
                }

                // fourth to play, 3rd trick
                double p34 = 20;
                play1 = -1;
                int m34 = -1;
                int n34 = -1;
                int v34 = -1;
                hitrump = 20;
                lotrump = 20;
                maxsuit = -1; // highest trump
                minsuit = 20; // lowest trump
                worsts = -1; // worst suit
                worstr = -1; // worst rank

                if (lone == bb3) {
                    m34 = m32;  // if partner going alone, skip me, pretend I played 9 of led suit
                    n34 = 0;
                    v34 = v33-1;
                } else { // partner not going alone
                    if (playst[dd3][m31] == 0) { // void in led suit
                        voids[dd3][m31] = 1;
                    }
                    for (int j=7; j>=0; j--) { // calculate lowest trump which beats 3rd player (if trumped and winning)
                        if (own[dd3][fintp][j] == dd3 && j < hitrump && m33 == fintp && j > n33) {
                            hitrump = j;
                        }
                        if (own[dd3][fintp][j] == dd3 && j < lotrump) { // calculate lowest trump if can win trick and first to trump
                            lotrump = j;
                        }
                    }
                    for (int j=7; j>=0; j--) { // calculate maxsuit and minsuit
                        if (own[dd3][m31][j] == dd3) { // have this rank in hand
                            if (win3 == aa3 && j > n31) { // 1st to play winning, identify lowest card which wins trick
                                maxsuit = j;
                            } else if (win3 == bb3 && declarer == bb3 && m32 != fintp && (right[dd3][fintp] +
                                                                                          left[dd3][fintp] > 0) && j > n32) {
                                // 2nd to play winning, identify lowest card which wins trick IF partner declared and have a bower
                                // to lead next round
                                maxsuit = j;
                            } else if (win3 == cc3 && j > n31) { // 3rd to play winning, identify lowest card which wins trick
                                maxsuit = j;
                            } else { // else identify worst card in suit to play
                                minsuit = j;
                            }
                        }
                    }

                    if (playst[dd3][m31] > 0) { // can follow suit
                        if (maxsuit != -1) {
                            play1 = 3;
                        } else {
                            play1 = 4;
                        }
                    } else if (playst[dd3][fintp] > 0) { // can't follow suit but can trump
                        if (win3 == win2) { // player who led trick is winning, play lowest trump
                            play1 = 2;
                        } else if (win3 == cc3) { // 3rd to play winning
                            if (m33 == fintp && hitrump != 20) { // overtrump if possible
                                play1 = 0;
                            } else if (m33 != fintp) { // play lowest trump if first to trump
                                play1 = 2;
                            }
                        } else {
                            play1 = 6;
                        }
                    } else {
                        play1 = 6;
                    }
                    for (int i=0; i<4; i++) { // determine worst card
                        for (int j=0; j<8; j++) {
                            if (playst[dd3][fintp] > 0) {
                                if (p34 > cv[0][i][j] && cv[0][i][j] > 0 && own[dd3][i][j] == dd3) {
                                    p34 = cv[0][i][j];
                                    worsts = i;
                                    worstr = j;
                                    if (worsts != fintp) {
                                        hint[dd3][worsts] = 1;
                                    }
                                }
                            } else {
                                if (p34 > cv[1][i][j] && cv[1][i][j] > 0 && own[dd3][i][j] == dd3) {
                                    p34 = cv[1][i][j];
                                    worsts = i;
                                    worstr = j;
                                    if (worsts != fintp) {
                                        hint[dd3][worsts] = 1;
                                    }
                                }
                            }
                        }
                    }

                    cardplay = playfirst(play1, fintp, hitrump, sectrump, lotrump, m31, maxsuit, minsuit, suitace, worsts,
                                         worstr, dd3, playst[dd3][m31], cards);
                    m34 = cardplay%10;
                    n34 = (cardplay/10)%10;

                    System.out.println("Player " + position[dd3] + " plays the " + cardname[m34][n34] + ".\n");
                    for (int i=0; i<4; i++) {
                        own[i][m34][n34] = -1;
                        length[i][m34]--;
                    }
                    playst[dd3][m34] = playst[dd3][m34] - 1;
                    cv[0][m34][n34] = 0;
                    cv[1][m34][n34] = 0;
                    if (n34 == 5 && m34 != fintp) {
                        aces[dd3][fintp]--;
                    }
                    suit[2][3] = m34;
                    rank[2][3] = n34;
                    if (m34 == fintp) {
                        v34 = 10+n34;
                    } else if (m34 == m31) {
                        v34 = n34;
                    } else {
                        v34 = -1;
                    }
                    if (v34 > v31 && v34 > v32 && v34 > v33) {
                        win3 = dd3;
                    }
                }

                // calculate stats for third trick
                tricktally = wintrick(win3, wturn);
                trick[tricktally]++;
                trick[tricktally+2]++;
                if (tricktally == 0) {
                    tns++;
                } else {
                    tew++;
                }

                // calculate if any player is single suited
                for (int i=0; i<4; i++) {
                    cksuit[i] = -1;
                    ss[i] = -1;
                }
                for (int i=0; i<4; i++) { // player
                    for (int j=0; j<4; j++) { // suit
                        for (int k=0; k<8; k++) { // rank
                            if (own[i][j][k] == i) {
                                if (cksuit[i] == -1 || cksuit[i] == j) {
                                    cksuit[i] = j;
                                } else {
                                    cksuit[i] = -2;
                                }
                            }
                        }
                    }
                    if (cksuit[i] > -1) {
                        ss[i] = cksuit[i];
                    }
                }

                // calculate boss (highest) rank in each suit; first reset to zero
                for (int i=0; i<4; i++) { // player
                    for (int j=0; j<4; j++) { // suit
                        solo[i][j] = 0;
                        solo[i][j] = 0;
                        solo[i][j] = 0;
                        solo[i][j] = 0;
                        boss[i][j] = 0;
                    }
                }
                for (int i=0; i<4; i++) { // player
                    for (int j=0; j<4; j++) { // suit
                        for (int k=0; k<8; k++) { // rank
                            if (own[i][j][k] > -1 && own[i][j][k] < 4 && k > boss[i][j]) {
                                boss[i][j] = k;
                            }
                        }
                    }
                }

                // check if any player is the only one with a particular suit
                for (int i=0; i<4; i++) { // suit
                    if (playst[0][i] > 0 && ((voids[1][i] + voids[2][i] + voids[3][i] == 3) || length[0][i] == playst[0][i])) {
                        solo[0][i] = 1;
                    }
                    if (playst[1][i] > 0 && ((voids[0][i] + voids[2][i] + voids[3][i] == 3) || length[1][i] == playst[1][i])) {
                        solo[1][i] = 1;
                    }
                    if (playst[2][i] > 0 && ((voids[0][i] + voids[1][i] + voids[3][i] == 3) || length[2][i] == playst[2][i])) {
                        solo[2][i] = 1;
                    }
                    if (playst[3][i] > 0 && ((voids[0][i] + voids[1][i] + voids[2][i] == 3) || length[3][i] == playst[3][i])) {
                        solo[3][i] = 1;
                    }
                }

                // re-check who holds bowers; first re-set to zero
                for (int i=0; i<4; i++) { // player
                    for (int j=0; j<4; j++) { // suit
                        right[i][j] = 0;
                        left[i][j] = 0;
                    }
                    for (int j=0; j<4; j++) { // suit
                        if (own[i][j][6] > -1) {
                            left[own[i][j][6]][j] = 1;
                        }
                        if (own[i][j][7] > -1) {
                            right[own[i][j][7]][j] = 1;
                        }
                    }
                }

                // establish new final shortcuts for fourth trick
                final int aa4 = win3; // 4th trick lead
                final int bb4 = (win3+1)%4; // second to play
                final int cc4 = (win3+2)%4; // third to play
                final int dd4 = (win3+3)%4; // fourth to play

                // XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
                // play out fourth trick; first to play is winner of third trick
                wturn = 4; // turn 4
                play1 = -1;
                double p41 = 20;
                double p42 = -1;
                int m41 = -1;
                int n41 = -1;
                int v41 = -1;
                hitrump = -1;
                lotrump = 20;
                maxsuit = -1;
                minsuit = suit[2][(win3-win2+4)%4];
                playace = 1.195;
                suitace = -1;
                bos = -1;
                worsts = -1; // worst suit
                worstr = -1; // worst rank
                win4 = win3;

                for (int j=0; j<8; j++) { // calculate highest and lowest trump
                    if (own[aa4][fintp][j] == aa4) {
                        if (j > hitrump) {
                            hitrump = j;
                        }
                        if (j < lotrump) {
                            lotrump = j;
                        }
                    }
                }
                for (int i=0; i<4; i++) { // calculate if have boss card in any non-trump suit * could have 2 of them!
                    if (own[aa4][i][boss[aa4][i]] == aa4 && i != fintp) {
                        bos = i;
                        cv[0][i][boss[aa4][i]] = .95; // make this boss card the best one to lead
                    }
                }
                for (int j=0; j<8; j++) { // find max rank of same suit to re-play
                    if (own[aa4][suit[2][(win3-win2+4)%4]][j] == aa4) { // find highest of same suit to lead
                        maxsuit = j;
                        minsuit = suit[2][(win3-win2+4)%4];
                    }
                }
                for (int i=0; i<4; i++) { // determine best card to lead
                    for (int j=0; j<8; j++) {
                        if (cv[0][i][j] > p42 && cv[0][i][j] != 0 && own[aa4][i][j] == aa4 && i != fintp) {
                            p42 = cv[0][i][j];
                            suitace = i;
                            sectrump = j;
                        }
                    }
                }
                for (int i=0; i<4; i++) { // determine worst card to lead
                    for (int j=0; j<8; j++) {
                        if (cv[0][i][j] < p41 && cv[0][i][j] != 0 && own[aa4][i][j] == aa4 && i != fintp) {
                            p41 = cv[0][i][j];
                            worsts = i;
                            worstr = j;
                        }
                    }
                }

                // is single-suited, play highest
                if (playst[aa4][fintp] == 2) { // have 2 trump left, lead highest
                    play1 = 0;
                } else if (ss[aa4] < 4 && ss[aa4] > -1) {
                    play1 = 5;
                } else if (lone == aa4) { // if going lone
                    if (own[aa4][fintp][boss[aa4][fintp]] == aa4 || solo[aa4][fintp] == 1 || (playst[aa4][fintp] > 0 &&
                                                                                              trick[aa4] == 1)) {
                        // if have boss trump OR are the only player with trump OR have only won 1 trick, lead highest trump
                        play1 = 0;
                    } else if (((voids[bb4][fintp] == 1 && voids[dd4][fintp] == 1) || trick[aa4] == 3) &&
                               playst[aa4][fintp] > 0) {
                        // if both opponents void in trump OR have won 3 tricks, lead highest trump
                        play1 = 0;
                    } else { // lead best non-trump
                        play1 = 5;
                    }
                } else if (lone == bb4 || lone == dd4) { // if opponent going lone
                    if (hitrump == boss[aa4][fintp] || solo[(aa4)%4][fintp] == 1) { // if have boss trump or the only trump,
                        // lead highest trump
                        play1 = 0;
                    }
                    else if (suit[2][(aa4-win2+4)%4] != fintp && playst[aa4][suit[2][(win3-win2+4)%4]] > 0) { // if won w/o
                        // trump lead same suit if have it
                        play1 = 3;
                    } else { // else lead best non-trump
                        play1 = 5;
                    }
                } else if (hitrump == boss[aa4][fintp] || solo[aa4][fintp] == 1) {
                    // if have a boss trump, OR are the only player w/ trump, lead highest trump
                    play1 = 0;
                } else if ((voids[bb4][fintp] == 1 && voids[dd4][fintp] == 1) && bos != -1) {
                    // if have boss card in an off-suit AND opponents are void in trump, lead it
                    play1 = 5;
                } else if (trick[aa4] == 3 && aces[aa4][fintp] == 1 && playst[aa4][fintp] == 1 &&
                           (length[aa4][fintp] - playst[aa4][fintp] < 4)) {
                    // if partnership has won first 3 tricks AND have 1 trump left + boss off-suit AND 3 or less trump left,
                    // play hi trump
                    play1 = 0;
                } else if (declarer == aa4) { // I am declarer
                    if ((length[aa4][fintp] - playst[aa4][fintp] < 2 && own[aa4][fintp][boss[aa4][fintp]] == aa4) ||
                        solo[aa4][fintp] == 1) {
                        // if have boss trump AND only 2 or less trump left, or are the only player with trump left, lead it
                        play1 = 0;
                    } else if (bos != -1 && own[aa4][fintp][boss[aa4][fintp]] == aa4) { // have boss trump AND a boss off-suit card
                        play1 = 0;
                    } else {
                        play1 = 5;
                    }
                } else if (declarer == cc4) { // partner is declarer
                    if ((length[aa4][fintp] - playst[aa4][fintp] < 2 && own[aa4][fintp][boss[aa4][fintp]] == aa4) ||
                        solo[aa4][fintp] == 1) {
                        play1 = 0; // if have the only trump left, or have the boss trump and only one other trump outstanding,
                        // lead highest trump
                    } else if ((right[aa4][fintp] + left[aa4][fintp]) > 0) {
                        play1 = 0; // if have a bower, lead it
                    } else if (suitace > -1) {
                        play1 = 5; // if have a good ace, lead it
                    } else if (trick[aa4] == 1 && playst[aa4][fintp] > 0) {
                        play1 = 0; // if have trump and have only won 1 trick, lead highest trump
                    } else {
                        play1 = 6;
                    }
                } else {
                    play1 = 6;
                }

                cardplay = playfirst(play1, fintp, hitrump, sectrump, lotrump, m41, maxsuit, minsuit, suitace, worsts,
                                     worstr, aa4, -1, cards);
                m41 = cardplay%10;
                n41 = (cardplay/10)%10;

                System.out.println("Player " + position[aa4] + " leads the " + cardname[m41][n41] + ".\n");
                for (int i=0; i<4; i++) {
                    own[i][m41][n41] = -1;
                    length[i][m41]--;
                }
                playst[aa4][m41] = playst[aa4][m41] - 1;
                cv[0][m41][n41] = 0;
                cv[1][m41][n41] = 0;
                if (n41 == 5 && m41 != fintp) {
                    aces[aa4][fintp]--;
                }
                suit[3][0] = m41;
                rank[3][0] = n41;
                if (m41 == fintp) {
                    v41 = 10+n41;
                } else {
                    v41 = n41;
                }

                // second to play, 4th trick
                p42 = 20;
                play1 = -1;
                int m42 = -1;
                int n42 = -1;
                int v42 = -1;
                hitrump = -1;
                lotrump = 20;
                maxsuit = -1;
                minsuit = 20;
                playace = 1.195;
                suitace = -1;
                worsts = -1; // worst suit
                worstr = -1; // worst rank

                if (lone == dd4) {
                    m42 = m41;  // if partner going alone, skip me, so assign same value to my 'play' as win1
                    n42 = n41;
                    v42 = v41-2;
                } else { // partner not going alone
                    if (playst[bb4][m41] == 0) { // void in led suit
                        voids[bb4][m41] = 1;
                    }
                    for (int i=0; i<4; i++) {  // identify worst card
                        for (int j=0; j<8; j++) {
                            if (p42 > cv[1][i][j] && cv[1][i][j] > 0 && own[bb4][i][j] == bb4) {
                                p42 = cv[1][i][j];
                                worsts = i;
                                worstr = j;
                            }
                        }
                    }
                    for (int j=0; j<8; j++) { // identify highest and lowest trump
                        if (own[bb4][fintp][j] == bb4) {
                            if (j > hitrump) {
                                hitrump = j;
                            }
                            if (j < lotrump) {
                                lotrump = j;
                            }
                        }
                    }
                    for (int j=0; j<8; j++) { // identify best and worst rank in led suit
                        if (own[bb4][m41][j] == bb4) {
                            if (j > maxsuit) {
                                maxsuit = j;
                            }
                            if (j < minsuit) {
                                minsuit = j;
                            }
                        }
                    }

                    if (playst[bb4][m41] > 0) { // can follow suit
                        if (maxsuit > n41) { // if can take lead, play highest
                            play1 = 3;
                        } else { // play lowest in suit
                            play1 = 4;
                        }
                    } else if (m41 != fintp && playst[bb4][fintp] > 0) { // can't follow suit but can trump
                        if (declarer == dd4) { // partner declared
                            if (right[bb4][fintp] + left[bb4][fintp] > 0) {
                                // trump with bower if have one and dealer (partner) is declarer
                                play1 = 0;
                            } else { // else if have no bower, trump with lowest
                                play1 = 2;
                            }
                        } else if (lone == bb4) { // if going lone, trump with second highest
                            play1 = 2;
                        } else if (n41 == boss[bb4][m41] || lone == aa4 || (declarer == bb4 && playst[bb4][fintp] > 1) ||
                                   (trick[bb4] == 1)) {
                            // play lowest trump if opponent going lone OR boss led OR have 2 trump as declarer OR have
                            // only won 1 trick so far
                            play1 = 2;
                        } else { // throw off
                            play1 = 6;
                        }
                    } else { // throw off
                        play1 = 6;
                    }

                    cardplay = playfirst(play1, fintp, hitrump, sectrump, lotrump, m41, maxsuit, minsuit, suitace, worsts,
                                         worstr, bb4, playst[bb4][m41], cards);
                    m42 = cardplay%10;
                    n42 = (cardplay/10)%10;

                    System.out.println("Player " + position[bb4] + " plays the " + cardname[m42][n42] + ".\n");
                    for (int i=0; i<4; i++) {
                        own[i][m42][n42] = -1;
                        length[i][m42]--;
                    }
                    playst[bb4][m42] = playst[bb4][m42] - 1;
                    cv[0][m42][n42] = 0;
                    cv[1][m42][n42] = 0;
                    if (n42 == 5 && m42 != fintp) {
                        aces[bb4][fintp]--;
                    }
                    suit[3][1] = m42;
                    rank[3][1] = n42;
                    if (m42 == fintp) {
                        v42 = 10+n42;
                    } else if (m42 == m41) {
                        v42 = n42;
                    } else {
                        v42 = -1;
                    }
                    if (v42 > v41) {
                        win4 = bb4;
                    }
                }

                // third to play, 4th trick
                double p43 = 20;
                play1 = -1;
                int m43 = -1;
                int n43 = -1;
                int v43 = -1;
                hitrump = -1;
                lotrump = 20;
                maxsuit = -1;
                minsuit = 20;
                worsts = -1; // worst suit
                worstr = -1; // worst rank

                if (lone == aa4) {
                    m43 = m41;  // if partner going alone, skip me, so assign same value to my 'play' as win3
                    n43 = n41;
                    v43 = v42-1;
                } else { // partner not going alone
                    if (playst[cc4][m41] == 0) { // void in led suit
                        voids[cc4][m41] = 1;
                    }
                    for (int i=0; i<4; i++) {  // identify worst card
                        for (int j=0; j<8; j++) {
                            if (p43 > cv[1][i][j] && cv[1][i][j] > 0 && own[cc4][i][j] == cc4) {
                                p43 = cv[1][i][j];
                                worsts = i;
                                worstr = j;
                            }
                        }
                    }
                    for (int j=0; j<8; j++) { // identify highest and lowest trump
                        if (own[cc4][fintp][j] == cc4) {
                            if (j > hitrump) {
                                hitrump = j;
                            }
                            if (j < lotrump) {
                                lotrump = j;
                            }
                        }
                    }
                    for (int j=0; j<8; j++) { // identify best and worst rank in led suit
                        if (own[cc4][m41][j] == cc4) {
                            if (j > maxsuit) {
                                maxsuit = j;
                            }
                            if (j < minsuit) {
                                minsuit = j;
                            }
                        }
                    }

                    if (playst[cc4][m41] > 0) { // can follow suit
                        if (maxsuit > n41+1 && win4 == win3) {
                            play1 = 3;
                        } else if (maxsuit > n42 && win4 == bb4 && m41 == m42) {
                            play1 = 3;
                        } else {
                            play1 = 4;
                        }
                    } else if ( playst[cc4][fintp] > 0) { // can't follow suit but can trump
                        if ((right[cc4][fintp] + left[cc4][fintp] > 0) && declarer == aa4 &&
                            n42 != 7 && (n41 != boss[cc4][m41] || win4 == (bb4))) { // trump with bower if have one
                            // AND partner is declarer AND either partner didn't play a boss card or 2nd player is winning
                            play1 = 0;
                        } else if (lone == dd4) { // if next to play declared alone, play highest trump
                            play1 = 0;
                        } else if ((m42 == fintp && lotrump > n42) || (trick[cc4] < 3 && win4 == (bb4) && m42 != fintp)) {
                            // if previous player trumped AND my lowest trump is better, OR my team has not won all the
                            // trick so far AND
                            // previous player is winning the trick w/o trump, play lowest trump
                            play1 = 2;
                        } else if (m42 == fintp && maxsuit > n42) { // if previous player trumped AND my highest trump beats
                            // them, play highest trump
                            play1 = 0;
                        } else if (n41 != boss[cc4][m41] && m42 != fintp) {
                            // if p did not lead the boss rank AND previous player did not trump, play lowest trump
                            play1 = 2;
                        } else {
                            play1 = 6;
                        }
                    } else { //throw off
                        play1 = 6;
                    }

                    cardplay = playfirst(play1, fintp, hitrump, sectrump, lotrump, m41, maxsuit, minsuit, suitace, worsts,
                                         worstr, cc4, playst[cc4][m41], cards);
                    m43 = cardplay%10;
                    n43 = (cardplay/10)%10;

                    System.out.println("Player " + position[cc4] + " plays the " + cardname[m43][n43] + ".\n");
                    for (int i=0; i<4; i++) {
                        own[i][m43][n43] = -1;
                        length[i][m43]--;
                    }
                    playst[cc4][m43] = playst[cc4][m43] - 1;
                    cv[0][m43][n43] = 0;
                    cv[1][m43][n43] = 0;
                    if (n43 == 5 && m43 != fintp) {
                        aces[cc4][fintp]--;
                    }
                    suit[3][2] = m43;
                    rank[3][2] = n43;
                    if (m43 == fintp) {
                        v43 = 10+n43;
                    } else if (m43 == m41) {
                        v43 = n43;
                    } else {
                        v43 = -1;
                    }
                    if (v43 > v41 && v43 > v42) {
                        win4 = cc4;
                    }
                }

                // fourth to play, 4th trick
                double p44 = 20;
                play1 = -1;
                int m44 = -1;
                int n44 = -1;
                int v44 = -1;
                hitrump = 20;
                lotrump = 20;
                maxsuit = -1; // highest trump
                minsuit = 20; // lowest trump
                worsts = -1; // worst suit
                worstr = -1; // worst rank

                if (lone == bb4) {
                    m44 = m42;  // if partner going alone, skip me, pretend I played 9 of led suit
                    n44 = 0;
                    v44 = v43-1;
                } else { // partner not going alone
                    if (playst[dd4][m41] == 0) { // void in led suit
                        voids[dd4][m41] = 1;
                    }
                    for (int j=7; j>=0; j--) { // calculate lowest trump which beats 3rd player (if trumped and winning)
                        if (own[dd4][fintp][j] == dd4 && j < hitrump && m43 == fintp && j > n43) {
                            hitrump = j;
                        }
                        if (own[dd4][fintp][j] == dd4 && j < lotrump) { // calculate lowest trump if can win trick and 3rd
                            // seat didn't trump
                            lotrump = j;
                        }
                    }
                    for (int j=7; j>=0; j--) { // calculate maxsuit and minsuit
                        if (own[dd4][m41][j] == dd4) { // have this rank
                            if (win4 == win3 && j > n41) { // 1st to play winning, identify lowest card which wins trick
                                maxsuit = j;
                            } else if (win4 == bb4 && declarer == bb4 && m42 != fintp && (own[dd4][fintp][7] == dd4 ||
                                                                                          own[dd4][fintp][6] == dd4) && j > n42) {
                                // 2nd to play winning, identify lowest card which wins trick IF partner declared and have a
                                // bower to lead next round
                                maxsuit = j;
                            } else if (win4 == cc4 && j > n43) { // 3rd to play winning, identify lowest card which wins trick
                                maxsuit = j;
                            } else { // else identify worst card in suit to play
                                minsuit = j;
                            }
                        }
                    }
                    for (int i=0; i<4; i++) { // calculate worst card
                        for (int j=0; j<8; j++) {
                            if (p44 > cv[1][i][j] && cv[1][i][j] > 0 && own[dd4][i][j] == dd4) {
                                p44 = cv[1][i][j];
                                worsts = i;
                                worstr = j;
                            }
                        }
                    }

                    if (playst[dd4][m41] > 0) { // can follow suit
                        if (maxsuit != -1) {
                            play1 = 3;
                        } else {
                            play1 = 4;
                        }
                    } else if (playst[dd4][fintp] > 0) { // can't follow suit but can trump
                        if (win4 == win3) { // player who led trick is winning, play lowest trump
                            play1 = 2;
                        } else if (win4 == cc4) { // 3rd to play winning
                            if (m43 == fintp && hitrump != 20) { // overtrump if possible
                                play1 = 0;
                            } else if (m43 != fintp) { // play lowest trump if first to trump
                                play1 = 2;
                            }
                        } else {
                            play1 = 6;
                        }
                    } else {
                        play1 = 6;
                    }

                    cardplay = playfirst(play1, fintp, hitrump, sectrump, lotrump, m41, maxsuit, minsuit, suitace, worsts,
                                         worstr, dd4, playst[dd4][m41], cards);
                    m44 = cardplay%10;
                    n44 = (cardplay/10)%10;

                    System.out.println("Player " + position[dd4] + " plays the " + cardname[m44][n44] + ".\n");
                    for (int i=0; i<4; i++) {
                        own[i][m44][n44] = -1;
                        length[i][m44]--;
                    }
                    playst[dd3][m44] = playst[dd4][m44] - 1;
                    cv[0][m44][n44] = 0;
                    cv[1][m44][n44] = 0;
                    if (n44 == 5 && m44 != fintp) {
                        aces[dd4][fintp]--;
                    }
                    suit[3][3] = m44;
                    rank[3][3] = n44;
                    if (m44 == fintp) {
                        v44 = 10+n44;
                    } else if (m44 == m41) {
                        v44 = n44;
                    } else {
                        v44 = -1;
                    }
                    if (v44 > v41 && v44 > v42 && v44 > v43) {
                        win4 = dd4;
                    }
                }

                // calculate stats for fourth trick
                tricktally = wintrick(win4, wturn);
                trick[tricktally]++;
                trick[tricktally+2]++;
                if (tricktally == 0) {
                    tns++;
                } else {
                    tew++;
                }

                // establish new final shortcuts for fourth trick
                final int aa5 = win4; // 2nd trick lead
                final int bb5 = (win4+1)%4; // second to play
                final int cc5 = (win4+2)%4; // third to play
                final int dd5 = (win4+3)%4; // fourth to play

                // XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
                // play out fifth trick; first to play is winner of fourth trick
                wturn = 5; // trick 5
                win5 = win4;
                int m51 = -1;
                int n51 = -1;
                int v51 = -1;
                for (int i=0; i<4; i++) {
                    for (int j=0; j<8; j++) {
                        if (own[win4][i][j] == win4) {
                            m51 = i;
                            n51 = j;
                        }
                    }
                }

                System.out.println("Player " + position[win4] + " leads the " + cardname[m51][n51] + ".\n");
                suit[4][0] = m51;
                rank[4][0] = n51;
                if (m51 == fintp) {
                    v51 = 10+n51;
                } else {
                    v51 = n51;
                }

                // second to play, fifth trick
                int m52 = -1;
                int n52 = -1;
                int v52 = -1;
                if (lone == (win4+3)%4) {
                    m52 = m51;  // if partner going alone, skip me, so assign same value to my 'play' as win1
                    n52 = n51;
                } else {  // partner not going alone
                    for (int i=0; i<4; i++) {
                        for (int j=0; j<8; j++) {
                            if (own[(win4+1)%4][i][j] == (win4+1)%4) {
                                m52 = i;
                                n52 = j;
                                v52 = v51-2;
                            }
                        }
                    }

                    System.out.println("Player " + position[(win4+1)%4] + " plays the " + cardname[m52][n52] + ".\n");
                    suit[4][1] = m52;
                    rank[4][1] = n52;
                    if (m52 == fintp) {
                        v52 = 10+n52;
                    } else if (m52 == m51) {
                        v52 = n52;
                    } else {
                        v52 = -1;
                    }
                    if (v52 > v51) {
                        win5 = (win4+1)%4;
                    }
                }

                // third to play, fifth trick
                int m53 = -1;
                int n53 = -1;
                int v53 = -1;
                if (lone == win4) {
                    m53 = m51;  // if partner going alone, skip me, so assign same value to my 'play' as win5
                    n53 = n51;
                } else {  // partner not going alone
                    for (int i=0; i<4; i++) {
                        for (int j=0; j<8; j++) {
                            if (own[(win4+2)%4][i][j] == (win4+2)%4) {
                                m53 = i;
                                n53 = j;
                                v53 = v52-2;
                            }
                        }
                    }

                    System.out.println("Player " + position[(win4+2)%4] + " plays the " + cardname[m53][n53] + ".\n");
                    suit[4][2] = m53;
                    rank[4][2] = n53;
                    if (m53 == fintp) {
                        v53 = 10+n53;
                    } else if (m53 == m51) {
                        v53 = n53;
                    } else {
                        v53 = -1;
                    }
                    if (v53 > v51 && v53 > v52) {
                        win5 = (win4+2)%4;
                    }
                }

                // fourth to play, fifth trick
                int m54 = -1;
                int n54 = -1;
                int v54 = -1;
                if (lone == (win4+1)%4) {
                    m54 = m52;  // if partner going alone, skip me, so assign same value to my 'play' as partner
                    n54 = n52;
                } else {  // partner not going alone
                    for (int i=0; i<4; i++) {
                        for (int j=0; j<8; j++) {
                            if (own[(win4+3)%4][i][j] == (win4+3)%4) {
                                m54 = i;
                                n54 = j;
                                v54 = v53-2;
                            }
                        }
                    }

                    System.out.println("Player " + position[(win4+3)%4] + " plays the " + cardname[m54][n54] + ".\n");
                    suit[4][3] = m54;
                    rank[4][3] = n54;
                    if (m54 == fintp) {
                        v54 = 10+n54;
                    } else if (m54 == m51) {
                        v54 = n54;
                    } else {
                        v54 = -1;
                    }
                    if (v54 > v51 && v54 > v52 && v54 > v53) {
                        win5 = (win4+3)%4;
                    }
                }

                // calculate stats for fifth trick
                tricktally = wintrick(win5, wturn);
                trick[tricktally]++;
                trick[tricktally+2]++;
                if (tricktally == 0) {
                    tns++;
                } else {
                    tew++;
                }

                // calculate winner of round, and points
                int pp = 0;
                for (int i=0; i<4; i++) {
                    if (trick[i] == 5 && lone == i) {
                        points[i] = points[i] + 4;
                        points[(i+2)%4] = points[(i+2)%4] + 4;
                        System.out.println("Player " + position[i] + " wins the hand with a lone: 4 pts!" + "\n");
                        if ((i+2)%2 == 0) {
                            pns = pns+4;
                        } else {
                            pew = pew+4;
                        }
                        pp = 1;
                    }
                }
                if (pp != 1) {
                    for (int i=0; i<2; i++) {
                        if (trick[i] == 5 && (declarer == i || declarer == (i+2)%4)) {
                            points[i] = points[i] + 2;
                            points[(i+2)%4] = points[(i+2)%4] + 2;
                            System.out.println("Players " + position[i] + " and " + position[(i+2)%4] + " win the hand with"
                                               + " 5 tricks: 2 pts!" + "\n");
                            if (i == 0) {
                                pns = pns+2;
                            } else {
                                pew = pew+2;
                            }
                        } else if (trick[i] > 2 && (declarer == i || declarer == (i+2)%4)) {
                            points[i] = points[i] + 1;
                            points[(i+2)%4] = points[(i+2)%4] + 1;
                            System.out.println("Players " + position[i] + " and " + position[(i+2)%4] + " win the hand: "
                                               + "1 pt." + "\n");
                            if (i == 0) {
                                pns++;
                            } else {
                                pew++;
                            }
                        } else if (trick[i] > 2 && (declarer == (i+1)%4 || declarer == (i+3)%4)) {
                            points[i] = points[i] + 2;
                            points[(i+2)%4] = points[(i+2)%4] + 2;
                            System.out.println("Players " + position[i] + " and " + position[(i+2)%4] + " euchre: "
                                               + "2 pts!" + "\n");
                            if (i == 0) {
                                pns = pns+2;
                            } else {
                                pew = pew+2;
                            }
                        }
                    }
                }

                top = Math.max(points[0], points[1]);

                System.out.println("Current score is " + points[0] + " for North and South and " + points[1] +
                                   " for East and West." + "\n");
                System.out.println("******************************************" + "\n");

                dealer++;

            } // no bid second round

        } // end of repeat loop

        if (top == points[0]) {
            System.out.println("North and South win the game " + points[0] + " to " + points[1] + "\n");
        } else {
            System.out.println("East and West win the game " + points[1] + " to " + points[0] + "\n");
        }
    }
}
