# EuchreBeta

## About

This is a euchre-playing program written by Ray Brunsberg.  Ray was an excellent card
player, which stands to reason, since Ray excelled at just about everything he put his
mind to (as well as many things he didn't particularly care to put his mind to).  Ray
learned Java in order to give this program life.  His stated goal was to "give devoted
amateurs a way to improve their game" (see [Additional Work](#additional-work) below), but
I think he also wanted to challenge himself with trying to capture and disseminate the
logic behind his card playing skills.  I know that he was happy with the way the program
was evolving, and had many plans for continuing to develop it, but he also knew that it
would never be as smart as he was.

## Branches

### [master](https://github.com/crashka/EuchreBeta/tree/master)

This branch contains Ray's original code.  It compiles to a standalone program with a Java
Swing UI.  It is a single-player game, with the other three hands played by Ray's
algorithm.  This is the program that Ray brought to the 2018 Beta Euchre Tournament in New
Orleans, where it was a big hit.  Here is a funny quote from Ray after the event:

> I am at least heartened by the number of people who approached me during the weekend
> with questions like, "In such and such a situation, how would your program have played",
> as if it were gospel. Perhaps I just project authority!

There will be no additional code check-ins to this branch (only documentation allowed).
This is Ray's branch.

### [autoplay](https://github.com/crashka/EuchreBeta/tree/autoplay)

This branch contains the core of Ray's card-playing logic.  It is basically the original
code with the UI elements removed and autonomous four-handed game playing (with all four
hands using Ray's core algorithm) enabled.  Some structural changes have been made, mostly
done to ensure consistency between replicated code blocks and patterns, which were useful
in identifying bugs (mostly minor).  I have tried my hardest to preserve all of Ray's
comments and his overall style in the code.  The refactoring for this branch was like
having a long conversation with Ray, and getting a glimpse into the mind of Ray.

**To Do**

- Validate bug fix represented by commit
[47f73ad](https://github.com/crashka/EuchreBeta/commit/47f73ad63f94b22dcd5064a349235191fc86204d)
*\[can someone help with this?]*

### [bug_fixes](https://github.com/crashka/EuchreBeta/tree/bug_fixes)

This branch was used to develop and validate bug fixes for the `autoplay` branch.  This
was branched off of `autoplay` immediately after the UI elements had been removed and the
autonomous four-handed game playing sequence was enabled.  All of the fixes in this branch
are manually merged back into `autoplay`.

### [engine](https://github.com/crashka/EuchreBeta/tree/engine)

*\[Default branch]* This branch is used to support the
[EuchreEndpoint](https://github.com/crashka/EuchreEndpoint) interface.  This was branched
off of `autoplay` after all of the planned functional and structural work was completed,
since it represents a distinct use case (even if `autoplay` never has additional commits).

**Versions**

- **v0.9** – Working version during development of initial integration with
[EuchreEndpoint](https://github.com/crashka/EuchreEndpoint) (completed integration will be
v1.0)

## Related Projects

### [EuchreEndpoint](https://github.com/crashka/EuchreEndpoint)

HTTP-based wrapper for the EuchreBeta game-playing engine, which can then be invoked by
euchre tournament-hosting platforms
(e.g. [euchre-plt](https://github.com/crashka/euchre-plt))

## Additional Work

### Teaching/Learning Tool

Ray wanted to add a "tutorial" option which would intervene whenever a player would make a
move different that that the algorithm would have played.  As he described it:

> I think this will provide an excellent basis for improving my algorithm, as users will
> be able to take issue with my suggested play and we can discuss. Coupled with this will
> be the option to replay a given hand, so they can review who had what cards (and give me
> full details of the hand) as well as try different bidding / play to see how
> alternatives work out.

This would be a great feature to add to either this code base, or a Ray Brunsberg-inspired
successor.

### Machine Learning Model

After the 2018 New Orleans event, I talked to Ray about the idea of using his algorithm as
the basis for training machine learning models that would either assimilate aspects of its
strategies and capabilities, or develop the ability to outplay it.  He was excited by the
prospect.

Once the EuchreBeta engine is wired into the
[euchre-plt](https://github.com/crashka/euchre-plt) framework (by way of
[EuchreEndpoint](https://github.com/crashka/EuchreEndpoint)), we should be able to use the
`ML` module (within euchre-plt) to start developing these models, and seeing where they
rank with respect to the original, other heuristic algorithms, and otherwise-developed ML
models.

## License

This project is licensed under the terms of the MIT License.
