# API Design #

## Notes ##

This version of the API represents a "shadow server", wherein the server is capable of
playing all hands, but the client controls which position actually makes the bids (and
defenses) and plays the cards for a game.  It is assumed that there is no cross-talk
between hands (i.e. cheating) on the server side.

## Endpoints ##

### Session ###

#### POST - Start a New Session ####

**Request**

**Response**

- Session Token - string
- Card Representation - int[24]
    - 9C, 10C, JC, ... 9D, 10D, JD, ... QS, KS, AS
- Suit Representation - int[4]
    - Clubs, Diamonds, Hearts, Spades
- Position Representation - int[4]
    - West, North, East, South

#### PATCH - Notification of Session Status Update ####

**Request**

- Session Token - string
- Type - string
    - "complete" - Session Complete
- Info - string (json)

**Response**

### Game ###

#### POST - Start a New Game ####

**Request**

- Session Token - string

**Response**

- Game Token - string

#### PATCH - Notification of Game Status Update ####

**Request**

- Session Token - string
- Game Token - string
- Type - string
    - "update" - Score Update
    - "complete" - Game Complete
- Info - string (json)

**Response**

### Deal ###

#### POST - Start a New Deal ####

**Request**

- Session Token - string
- Game Token - string
- Cards - int[24]
    - 0-4  : Position 0 Hand
    - 5-9  : Position 1 Hand
    - 10-14: Position 2 Hand
    - 15-19: Position 3 Hand
    - 20   : Turn Card
    - 21-23: Buries
- Dealer Position - int (0-3)

**Response**

#### PATCH - Notification of Deal Status Update ####

**Request**

- Session Token - string
- Game Token - string
- Type - string
    - "complete" - Deal Complete
- Info - string (json)

**Response**

### Bid ###

#### POST - Notification of a Bid ####

**Request**

- Session Token - string
- Game Token - string
- Round - int (0-1)
- Position - int (0-3)
- Suit - int (0-3)
- Alone - boolean

**Response**

- Suggested Suit - int (0-3)
- Suggested Alone - boolean

#### GET - Request for a Bid ####

**Request**

- Session Token - string
- Game Token - string
- Round - int (0-1)
- Position - int (0-3)

**Response**

- Suit - int (0-3)
- Alone - boolean

### Pick Up ###

#### POST - Notification of Pick Up ####

**Request**

- Session Token - string
- Game Token - string
- Round - int (0-1)
- Position - int (0-3)
- Swap Card - int (0-24)

**Response**

- Suggested Swap Card - int (0-24)

#### GET - Request for Pick Up ####

**Request**

- Session Token - string
- Game Token - string
- Round - int (0-1)
- Position - int (0-3)

**Response**

- Swap Card - int (0-24)

### Defense ###

#### POST - Notification of a Defense ####

**Request**

- Session Token - string
- Game Token - string
- Round - int (0-1)
- Position - int (0-3)
- Alone - boolean

**Response**

- Suggested Alone - boolean

#### GET - Request for a Defense ####

**Request**

- Session Token - string
- Game Token - string
- Round - int (0-1)
- Position - int (0-3)

**Response**

- Alone - boolean

### Play ###

#### POST - Notification of a Card Played ####

**Request**

- Session Token - string
- Game Token - string
- Trick - int (0-4)
- Position - int (0-3)
- Card - int (0-23)

**Response**

- Suggested Card - int (0-23)

#### GET - Request for a Card to be Played ####

**Request**

- Session Token - string
- Game Token - string
- Trick - int (0-4)
- Position - int (0-3)

**Response**

- Card - int (0-23)
