# Java Rock Paper Scissors
Multithreaded implementation of the Rock, Paper, Scissors game, written in Java, where each player is represented by a separate thread. There is no user input - every player makes their own random choice. Players win when they're the only possible winner in a given round, i.e. when the "weapon" they picked can beat every else's.

## Rules
* The X number of threads are playing Rock, Paper, Scissors.
* Each reveals their pick after a brief random delay, ranging from 100 to 500 ms.
* Each says what it picks.
* The number of rounds is not limited. The game ends when a single thread reaches the Y number of wins.
