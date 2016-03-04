Simple [Hex](https://en.wikipedia.org/wiki/Hex_%28board_game%29) bot written in Java using simple Monte Carlo simulations for determining the best next move.

To initiate the bot call:

```java
Stroj mn = new Stroj_mn();
```

New game can be started using:

```java
mn.novaPartija();
```

After the opponent makes his move, it can be transmitted to bot using `mn.sprejmiPotezo(Polje field)`, where `Polje` is an object containing x and y coordinates of the field.

Next move can be requested using `mn.izberiPotezo()` which will return a `Polje` object containing coordinates of the selected field.

A game can be finished by calling `mn.rezultat(boolean won)`, where `won` indicates, whether the bot won the current match.
