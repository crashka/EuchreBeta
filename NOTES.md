# Notes #

This file contains notes on the original EuchreBeta implementation that are helpful for
navigating the code and logic.

## Data Structurers/Arrays ##

### Hands cards[24] ###

| Player | Index | Position |
| ------ | ----- | -------- |
| South  | 0-4   | 0        |
| West   | 5-9   | 1        |
| North  | 10-14 | 2        |
| East   | 15-19 | 3        |
| Turn   | 20    |          |
| Buried | 21-23 |          |

### Play Sequence ###

#### Bidding ####

| Var | Role       |
| --- | ---------- |
| aa  | 1st to bid |
| bb  | 2nd to bid |
| cc  | 3rd to bid |
| dd  | 4th to bid (dealer) |

#### First Trick ####

| Var | Role        |
| --- | ----------- |
| aa  | lead        |
| bb  | 2nd to play |
| cc  | 3rd to play |
| dd  | 4th to play (dealer) |

#### Subsequent Tricks ####

| Var     | Role        |
| ------- | ----------- |
| aa\<N\> | lead (N-1 trick taker) |
| bb\<N\> | 2nd to play |
| cc\<N\> | 3rd to play |
| dd\<N\> | 4th to play |

### Card Values ###

| Rank      | Spades | Hearts | Diamonds | Clubs | suitx |
| --------- | ------ | ------ | -------- | ----- | ----- |
| **9**     | 0      | 1      | 2        | 3     | 0     |
| **10**    | 4      | 5      | 6        | 7     | 1     |
| **J**     | 8      | 9      | 10       | 11    | 2     |
| **Q**     | 12     | 13     | 14       | 15    | 3     |
| **K**     | 16     | 17     | 18       | 19    | 4     |
| **A**     | 20     | 21     | 22       | 23    | 5     |
| **L**     | 24     | 25     | 26       | 27    | 6     |
| **R**     | 28     | 29     | 30       | 31    | 7     |
| **rankx** | 0      | 1      | 2        | 3     |       |

## Prepare/Update Play Algorithm ##

```java
public void preparePlay(int declarer, int fintp, int lone, int round) {
    // if first round contract AND seat 2 didn't call lone, have dealer swap cards

    // change value of bowers to reflect proper suit and hierarchy

    // initialize 'own' values to -1

    // identify where each card is

    //** calculate suit lengths for each [player][suit]

    //  establish length of suits in play (varies from 4 to 7)

    //  calculate values of ace / king / queen arrays (non-trump)

    // if a player going lone, assign voids to all partner's suits

    // calculate boss (highest) rank in each suit

    //** invoke method calc to calculate values for cv[lead][][] and cv[toss][][]

    // determine number of non-trump void suits for each player
}

public void updatePlay1() {
    // if defending against lone, modify priority of discards

    // calculate boss (highest) rank in each suit; first reset to zero

    // check if any player is the only one with a particular suit

    // re-check who holds bowers; first re-set to zero
}

public void updatePlay2() {
    // if defending against lone, modify priority of discards

    // calculate if any player is single suited

    // calculate boss (highest) rank in each suit; first reset to zero

    // check if any player is the only one with a particular suit

    // re-check who holds bowers; first re-set to zero
}

public void updatePlay3() {
    // calculate if any player is single suited

    // calculate boss (highest) rank in each suit; first reset to zero

    // check if any player is the only one with a particular suit

    // re-check who holds bowers; first re-set to zero
}

public void updatePlay4() {
    // nothing to do
}

public void updatePlay5() {
    // nothing to do
}
```

## Play Logic ##

- player11
  - lone == cc: skip (v11 = -1)
  - else: play
- player12
  - lone == dd: v12 = v11-1
  - else: play
- player13
  - lone == aa: v13 = v12-1
  - else: play
- player14
  - lone == bb: v14 = v13-1
  - else: play

- player21
  - play
- player22
  - lone == dd2: v22 = v21-1
  - else: play
- player23
  - lone == aa2: v23 = v22-1
  - else: play
- player24
  - lone == bb2: v24 = v23-1
  - else: play

- player51
- player52
- player53
- player54

## Autoplay Sequence ##

- prepareGame
- prepareDeal
- prepareBid
- bid11
- bid12
- bid13
- bid14
- bid21
- bid22
- bid23
- bid24
- preparePlay
- play11
- play12
- play13
- play14
- updatePlay1
- play21
- play22
- play23
- play24
- updatePlay2

.
.
.

- play51
- play52
- play53
- play54
- updatePlay5
- updateDeal
- updateGame

## Endpoint Sequence ##

- prepareGame
- prepareDeal(cards)
- prepareBid(pos = 2)
- bid11(post)
- bid12(get)
- bid13(post)
- bid14(post)
- bid21(post)
- bid22(get)
- bid23(post)
- bid24(post)
- preparePlay
- play11(post)
- play12(get)
- play13(post)
- play14(post)
- updatePlay1
- play21(post|get)
- play22(post|get)
- play23(post|get)
- play24(post|get)
- updatePlay2

.
.
.

- play51(post|get)
- play52(post|get)
- play53(post|get)
- play54(post|get)
- updatePlay5
- updateDeal
- updateGame

## Bug Fixes ##

### Programming Errors ###

### Visibility Errors ###
