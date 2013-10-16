othello-ai
==========

An AI to play Othello (a.k.a. Reversi)

Introduction
------------

This project was a group project written by Jeremy Birnbaum, Dan Kemp, and
myself for an AI course. The goal was to develop an AI that could play Othello
well enough to beat ourselves.

Installation
------------

There isn't really much to install here. Simply build all the Java source files
into classes.

Running
-------

The main class for this project is `edu.albany.othello.OthelloApplication`.
Running that class with no other arguments will pit two AIs against each other
repeatedly.  Running with the command line argument `demo` will run an
interactive demo:

    java edu.albany.othello.OthelloApplication demo

Simply click to play, then the AI will make its move. A lot of debugging
information is dumped to the console -- this can safely be ignored.

What's Going On
---------------

The idea behind the AI is to encode a lot of different strategies -- which we
call bots -- and then let this "council of bots" decide which move is best.
Bots are allowed to keep track of information between games, though I don't
think there's any functionality to play more than one demo game.

