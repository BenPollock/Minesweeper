/***************************************************************
PROGRAM: BasicMinesweeper
Author: Ben Pollock
Date: Jan 25 09
Comments: A minesweeper game with 2 different game modes, random and pick.
	  Powerup mode is currently in demo form and doesn't exactly work.
	  There is a high score system.
	  Game is played by clicking on the square you want to open.
	  Right click to flag.
	  The program is commented enough, at least to how much I need it.
***************************************************************/
import java.applet.*;
import java.awt.*;
import java.awt.Color;
import java.awt.event.*;

import java.io.*;
import java.net.*;
import java.util.*;           // networking stuff
import java.text.*;
import java.lang.Object.*;

public class BasicMinesweeper extends Applet implements Runnable, MouseListener, MouseMotionListener, KeyListener
{
    ///////////////////////VARIABLES////////////////////////////////////


    static Graphics c;                   // The output console
    static Image doubleBufferImage; // the hidden buffer
    static Graphics doubleBufferG; // the unit for the hidden buffer
    static int size = 25;   // the amount of peices in the board
    static int minecount;  // amount of mines
    static Image losingScreen;   // screen displayed when you lose
    static Image winScreen;   // screen displayed when you win
    static int minesadded, clickedPlay;   // amount of mines decided so far
    static int squarex[] [] = new int [1000] [1000]; // coordinates of nonclicked pieces
    static int squarey[] [] = new int [1000] [1000];
    // static Image[] [] flagdraw = new Image [1000] [1000]; // the flags
    static Image val[] = new Image [10];   // helps speed up the image loading
    static Image pickSize;     // the image where you pick the field size
    static Image pickMines;     // the image where you pick the amount of mines
    static Image titleScreen;         // the title screen
    static Image mainScreen;   // the main pic
    static int RandomIze = 0; // only run the random size/mines once
    static int FIRSTTIMEMINES = 0;  // makes sure the game doesnt skip from size to mine screen
    //static Image[] [] squares = new Image [1000] [1000]; // the top squares
    static Image[] [] bottomSquares = new Image [1000] [1000]; // the actual squares
    static Image chatScreen;          // the bottom part of the screen
    static int squareType[] [] = new int [1000] [1000];  // 0 for no mines, 9 for mine, 1-8 for amount of mines near square
    static int powerUp[] [] = new int [1000] [1000];   // 0 no powerup, one ghost, two health, three score, four gun, five bomb
    static boolean onHover[] [] = new boolean [1000] [1000]; // if the piece is hovered over
    static boolean clickedYet[] [] = new boolean [1000] [1000]; // if the piece has been clicked yet
    static boolean flagged[] [] = new boolean [1000] [1000];  // if the square has been flagged
    static int cursorx = 0;      // coordinates of cursor
    static int cursory = 0;
    static int score = 0;         // the score
    static int poopx, poopy = 0;     // other stuff
    static Image bluething;        // the thing to see if you are over something
    static Image powerGhost, powerGun, powerBomb, powerHealth, powerScore;         // images for the powerups
    static long starttime, newtime;            // the times used to determine how long to load or what your score is
    static int starttime2;
    static int flagAmount = 0;  // amount of flags left
    static Color Zero, outside, inside;  // the colors
    static int a, b = 0;
    static int resetTime, startAgain = 0;  // resetTime counts if the timer should be set, startAgain counts if the timer for exiting the lose/win screen should be set
    static char mode = '9';              // the game mode
    static long dontStart = 0;      // this should be not global, it tells you if you should move on from the lose screen
    Font stats = new Font ("Arial", Font.BOLD, 30);
    Font title = new Font ("Arial", Font.BOLD, 30);         // different fonts used for titles or stats
    Font finalScore = new Font ("Arial", Font.BOLD, 80);


    //socket stuff
    Socket mySocket = null;
    PrintWriter out = null;
    BufferedReader in = null;
    //
    String fromServer; // the message from the server
    String fromUser; // the to the server
    //

    TextField nameField; // the textfield for the name
    Button submit; // the submit button
    Button playAgain; // the play again button
    Button sound; // turns sound on and off
    private String name; // the String from the textfield
    int bx, by, bz, bdx, bdy, bdz, bcc = -1, obcc = -1, bSize, mx, my, lose = 0, paddleC, paddleOC, score1 = 0, topScore = 0, ox, oy, odx, ody, fontC, laserC = 0, OlaserC = 0, OlaserT = 0, laserT = 0, ballC = 0, soundT = 1;
    float bcx, bcy, mcx, mcy;

    static Font loseFont = new Font ("Impact", 0, 50); // lose font
    static Font scoreFont = new Font ("Impact", 0, 15); // score font






    ////////////////////////////////////////////////////////////////////


    public void connect () throws IOException // change the ip to the servers ip in this method and have the server running to have high scores saved
    {
	//
	// attempt to contact the server (24.150.233.211 is Mr. C's house)
	// change the IP to ??.??.??.?? for the in-school server
	//
	try
	{

	    mySocket = new Socket ("216.121.208.42", 1234); //  <===========specify the correct IP here
	    out = new PrintWriter (mySocket.getOutputStream (), true);
	    in = new BufferedReader (new InputStreamReader (mySocket.getInputStream ()));
	}
	catch (UnknownHostException e)
	{
	    System.err.println ("Don't know about host");
	    System.in.read ();
	    System.exit (1);
	}
	catch (IOException e)
	{
	    System.err.println ("Couldn't get I/O for the connection");
	    System.in.read ();
	    //  System.exit (1);
	}
	//
	// establish connection to communicate with the server
	//
	BufferedReader stdIn = new BufferedReader (new InputStreamReader (System.in));
	String fromServer;
	String fromUser;
	//
	// loop to communicate with the server
	//
    }


    ///////////////APPLET PART OF PROGRAM////////////////////////////////

    public void init ()
	// gets called ONE TIME when the applet first ignites

    {
	setLayout (null);
	c = getGraphics ();
	setFocusable (true);              // for some reason i have to add this for keyboard to work
	addMouseMotionListener (this);
	addMouseListener (this);             // allows for keyboard and mouse input
	addKeyListener (this);
	//  minecount = 10;
	//   minesadded = 0;
	//R 206   G 205    B 255
	Zero = new Color (206, 205, 255);
	outside = new Color (63, 56, 255);         // makes colours for the squares
	inside = new Color (5, 0, 188);
	losingScreen = getImage (getDocumentBase (), "losescreen.png");
	winScreen = getImage (getDocumentBase (), "winscreen.png");
	mainScreen = getImage (getDocumentBase (), "maingui2.png");
	powerGhost = getImage (getDocumentBase (), "ghost.png");
	powerGun = getImage (getDocumentBase (), "gun.png");                      // loads all the images
	powerHealth = getImage (getDocumentBase (), "crosshealth.png");
	powerBomb = getImage (getDocumentBase (), "bomb.png");
	powerScore = getImage (getDocumentBase (), "scoreincrease.png");
	titleScreen = getImage (getDocumentBase (), "minesweeper.png");

	submit = new Button ("Submit");                 // the buttons and textfields
	submit.setBounds (350, 560, 70, 20);            // ^
	playAgain = new Button ("Play Again");          // ^
	playAgain.setBounds (10, 540, 70, 30);         // ^
	nameField = new TextField ("Enter name here");
	nameField.setBounds (200, 560, 100, 20);        // ^
	clickedPlay = 0;
	nameField.setColumns (5);
	for (int j = 0 ; j < size ; j++)
	{
	    for (int i = 0 ; i < size ; i++)
	    {
		// squares [j] [i] = getImage (getDocumentBase (), "normalsquare.png");
		// flagdraw [j] [i] = getImage (getDocumentBase (), "flaggedsquare.png");
		squarex [j] [i] = (j * 16);
		squarey [j] [i] = (i * 16);                  // sets all the variables
		onHover [j] [i] = false;
		flagged [j] [i] = false;
		clickedYet [j] [i] = false;
		powerUp [j] [i] = 0;
		// int putMine = (int) (Math.random () * 50);
		// if (putMine == 0 && minesadded < minecount)      // adds random mines
		// {
		//     squareType [j] [i] = 9;
		//     minesadded++;
		// }


	    }
	}
	// while (minesadded < (minecount + 1))
	// {
	//     for (int j = 0 ; j < size ; j++)
	//     {
	//         for (int i = 0 ; i < size ; i++)
	//         {
	//             int putMine = (int) (Math.random () * (size * 10));
	//             if (putMine == 0 && minesadded < minecount + 1)      // adds random mines
	//             {
	//                 squareType [j] [i] = 9;
	//                 minesadded++;
	//             }
	//         }
	//     }
	// }
	//      flagAmount = minecount;



	bluething = getImage (getDocumentBase (), "hoversquare.png");
	chatScreen = getImage (getDocumentBase (), "peach.png");                         // loads more images
	pickSize = getImage (getDocumentBase (), "sizepick.png");
	pickMines = getImage (getDocumentBase (), "minepick.png");

	//squares [3] [1] = getImage (getDocumentBase (), "hoversquare.png");


	//IN THIS PART I SET THE NUMBER OF EACH SQUARE

	for (int j = 0 ; j < size ; j++)
	{
	    for (int i = 0 ; i < size ; i++)
	    {
		if (squareType [j] [i] != 9)
		{
		    int numMineAround = 0;
		    if (j != 0)
			if (squareType [j - 1] [i] == 9)
			    numMineAround++;
		    if (j != 0 && i != 0)
			if (squareType [j - 1] [i - 1] == 9)
			    numMineAround++;
		    if (j != 0 && i != (size - 1))
			if (squareType [j - 1] [i + 1] == 9)        // determines how many mines are around each piece
			    numMineAround++;                        // avoids exceeding array size
		    if (j != (size - 1))
			if (squareType [j + 1] [i] == 9)
			    numMineAround++;
		    if (j != (size - 1) && i != 0)
			if (squareType [j + 1] [i - 1] == 9)
			    numMineAround++;
		    if (j != (size - 1) && j != (size - 1))
			if (squareType [j + 1] [i + 1] == 9)
			    numMineAround++;
		    if (i != 0)
			if (squareType [j] [i - 1] == 9)
			    numMineAround++;
		    if (i != (size - 1))
			if (squareType [j] [i + 1] == 9)
			    numMineAround++;

		    squareType [j] [i] = numMineAround;

		}
	    }
	}

	val [0] = getImage (getDocumentBase (), "basenumber.png");
	val [1] = getImage (getDocumentBase (), "square1.png");
	val [2] = getImage (getDocumentBase (), "square2.png");
	val [3] = getImage (getDocumentBase (), "square3.png");
	val [4] = getImage (getDocumentBase (), "square4.png");            // loads the number images
	val [5] = getImage (getDocumentBase (), "square5.png");
	val [6] = getImage (getDocumentBase (), "square6.png");
	val [7] = getImage (getDocumentBase (), "square7.png");
	val [8] = getImage (getDocumentBase (), "square8.png");
	val [9] = getImage (getDocumentBase (), "minesquare.png");



	//FINISHED


	for (int j = 0 ; j < size ; j++)
	{
	    for (int i = 0 ; i < size ; i++)
	    {
		// if (squareType [j] [i] == 9)
		//     bottomSquares [j] [i] = getImage (getDocumentBase (), "minesquare.png");
		// if (squareType [j] [i] == 8)
		//     bottomSquares [j] [i] = getImage (getDocumentBase (), "square8.png");
		// if (squareType [j] [i] == 7)
		//     bottomSquares [j] [i] = getImage (getDocumentBase (), "square7.png");
		// if (squareType [j] [i] == 6)
		//     bottomSquares [j] [i] = getImage (getDocumentBase (), "square6.png");
		// if (squareType [j] [i] == 5)
		//     bottomSquares [j] [i] = getImage (getDocumentBase (), "square5.png");           // draws
		// if (squareType [j] [i] == 4)
		//     bottomSquares [j] [i] = getImage (getDocumentBase (), "square4.png");
		// if (squareType [j] [i] == 3)
		//     bottomSquares [j] [i] = getImage (getDocumentBase (), "square3.png");
		// if (squareType [j] [i] == 2)
		//     bottomSquares [j] [i] = getImage (getDocumentBase (), "square2.png");
		// if (squareType [j] [i] == 1)
		//     bottomSquares [j] [i] = getImage (getDocumentBase (), "square1.png");
		// if (squareType [j] [i] == 0)
		//     bottomSquares [j] [i] = getImage (getDocumentBase (), "basenumber.png");

		bottomSquares [j] [i] = val [squareType [j] [i]];
	    }
	}



	//squares [2] [3] = getImage (getDocumentBase (), "square5.png");





	starttime = System.currentTimeMillis ();
	resetTime = 0;
    }


    public void server () throws IOException // sends the server your name a score and then sends a G to reply with the updated list
    {
	//System.out.println ("server");
	out.println ("M" + name + "," + score);     // the 'D' determines which score set is use
	fromServer = in.readLine ();                // get 9and discard) the server's reply
	//System.out.println ("server " + fromServer);
	out.println ("M");                          // request a list of high scores
	fromServer = in.readLine ();                // get the server's reply (the scoreboard)
	//System.out.println ("server " + fromServer);
    }



    public void start ()
	// initializes some of the more advanced features
    {
	Thread mines = new Thread (this);     //define a thread
	mines.start ();
    }


    public void stop ()
	//called when execution is finished
    {
    }


    public void destroy ()
	// cleans up when the applet is removed from memory
    {
    }


    public void run ()
	// contains program segments that get executed
    {
	while (true)
	{
	    repaint (); // redraw the screen
	    try // go to sleep for 20 milliseconds
	    {
		Thread.sleep (0);
	    }
	    catch (InterruptedException ex)  // at the end of the sleep do nothing
	    {
	    }
	}
    }


    public boolean action (Event l, Object o)
    { //IF A BUTTON IS PRESSED....
	c = getGraphics ();
	if (l.target == submit) // gets the terxt out of the textfield and limits the text to 30 chars long and replaces any "," with "."
	{
	    name = nameField.getText ();
	    if (name.length () >= 30)
	    {
		name = name.substring (0, 29);
	    }
	    name = name.replace (',', '.');
	    try
	    {
		connect ();
		server ();
	    }
	    catch (IOException e)
	    {

	    }
	    finally
	    {
	    }
	    lose = 5;
	    submit.setEnabled (false);
	    remove (submit);
	    remove (nameField);

	}
	if (l.target == playAgain) // resets the game and its current score
	{
	    clickedPlay = 1;
	}

	return true;
	// }
    }


    public void drawBoard (Graphics g)  // draws the game board and the text that is displayed on it
    {
	if (lose == 5)
	{
	    fontC += 7;
	    if (fontC > 75)
		fontC = 0;
	    StringTokenizer token = new StringTokenizer (fromServer, ",");
	    int cnt = 1;
	    while (token.hasMoreTokens ())
	    {
		g.setFont (scoreFont);
		if (cnt == 1)
		{
		    if (fontC <= 25)
			g.setColor (Color.yellow);
		    if (fontC > 25 && fontC <= 50)
			g.setColor (Color.red);
		    if (fontC > 50 && fontC <= 75)
			g.setColor (Color.orange);
		}
		else
		    g.setColor (Color.white);
		//   g.drawString (cnt + " )  " + token.nextToken () + " . . . . . . " + token.nextToken (), 180, 27 + cnt * 17);
		g.drawString (cnt + " )  " + token.nextToken (), 50, 85 + cnt * 17);
		g.drawString (token.nextToken (), 530, 85 + cnt * 17);
		cnt++;
	    }
	}
	g.setFont (scoreFont);
	g.setColor (Color.white);
	// g.drawString ("SCORE :  " + score + "       HIGH SCORE :  " + topScore, 10, 19);
    }


    public void paint (Graphics g)
    {
	g.setFont (stats);
	if (mode == '9')
	    paintLoad (g);
	if (mode == '0')
	    paint0 (g);
	if (mode == '1')
	    paintHalf (g);
	if (mode == '2')
	    paintQuarter (g);         // goes to the paint function depending on the game mode
	if (mode == '3')
	    paint1 (g);
	if (mode == '4')
	    paint4 (g);
	if (mode == '5')
	    paintHalf2 (g);
	if (mode == '6')
	    paintQuarter2 (g);
	if (mode == '7')
	    paint7 (g);
    }


    public void paintLoad (Graphics g)                 // starts at the beginning, loads up all the images in the background
    {
	g.drawImage (chatScreen, 100, 430, this);
	for (int i = 0 ; i < 10 ; i++)
	    g.drawImage (val [i], 0, 0, this);
	g.drawImage (mainScreen, 0, 0, this);
	g.drawImage (pickSize, 0, 0, this);
	g.drawImage (pickMines, 0, 0, this);
	g.drawImage (losingScreen, 0, 0, this);
	g.drawImage (bluething, 0, 0, this);
	g.drawImage (winScreen, 0, 0, this);
	mode = '0';

    }


    public void paint0 (Graphics g)
    {
	g.drawImage (titleScreen, 0, 0, this);            // the title screen mode

    }



    public void paintHalf (Graphics g)
    {
	remove (nameField);
	remove (submit);
	remove (playAgain);
	g.drawImage (pickSize, 0, 0, this);        // displays the size pick screen


    }


    public void paintQuarter (Graphics g)
    {
	g.drawImage (pickMines, 0, 0, this);   // displays the mine pick screen
    }


    public void paintHalf2 (Graphics g)
    {
	remove (nameField);                         // displays the size pick screen in powerup mode
	remove (submit);
	remove (playAgain);
	g.drawImage (pickSize, 0, 0, this);


    }


    public void paintQuarter2 (Graphics g)
    {
	g.drawImage (pickMines, 0, 0, this);      // displays the mine pick screen in powerup mode
    }




    public void paint1 (Graphics g)           // BASIC MODE
	// used to draw the content of the window (called by windows & java program)
    {
	//  System.out.println ("shed");


	//  setParameters (g);                            // basically does everything




	if (System.currentTimeMillis () - starttime < 15 * minecount) // load
	{
	    // g.setColor (Color.black);
	    // g.fillRect (0, 0, 600, 600);
	    // g.setColor (Color.gray);
	    // g.fillRect (0, 0, 400, 400);
	    g.drawImage (mainScreen, 0, 0, this);


	    g.drawImage (chatScreen, 100, 430, this);
	    g.setColor (Color.blue);
	    g.drawRect (100, 430, 400, 140);
	    g.setColor (Color.lightGray);
	    g.drawString ("LOADING.....", 300, 500);            // creates the loading screen
	    for (int j = 0 ; j < size ; j++)
	    {
		for (int i = 0 ; i < size ; i++)
		{
		    if (squareType [j] [i] != 0)
			g.drawImage (bottomSquares [j] [i], squarex [j] [i], squarey [j] [i], this);
		    else
		    {
			g.setColor (Zero);
			g.fillRect (squarex [j] [i], squarey [j] [i], 16, 16);                     // draws all squares
		    }
		    g.setColor (inside);
		    g.fillRect (squarex [j] [i], squarey [j] [i], 16, 16);
		    g.setColor (outside);
		    g.drawRect (squarex [j] [i], squarey [j] [i], 16, 16);

		    //   g.drawImage (squares [j] [i], squarex [j] [i], squarey [j] [i], this);
		    // g.setColor (Color.black);
		    // g.fillRect (0, 0, 400, 400);

		}
	    }

	}


	else
	{
	    if (resetTime == 0)
	    {
		resetTime++;
		newtime = System.currentTimeMillis ();
	    }

	    if (checkLose () == false)
	    {
		if (checkWin () == false)                // what is drawn when everything is done loading
		{
		    // g.setColor (Color.black);
		    // g.fillRect (0, 0, 600, 600);
		    // g.setColor (Color.gray);
		    // g.fillRect (0, 0, 400, 400);
		    g.drawImage (mainScreen, 0, 0, this);
		    g.drawImage (chatScreen, 100, 430, this);             // draws background
		    g.setColor (Color.blue);
		    g.drawRect (100, 430, 400, 140);


		    starttime2 = (int) (System.currentTimeMillis () - newtime);               // draws the background
		    String timer = Integer.toString ((int) (starttime2 / 1000));
		    //  g.setFont (title);
		    g.setColor (Color.lightGray);
		    g.setFont (stats);
		    g.drawString ("Score: ", 410, 150);
		    score = ((500 - (int) (starttime2 / 1000)) * 1) + (30 - size) * 2 + (minecount * 30);
		    String score2 = Integer.toString (score);
		    g.drawString (score2, 505, 150);
		    g.drawString ("Timer: ", 410, 200);                          // draws the timer
		    g.drawString (timer, 505, 200);
		    g.drawString ("Mines: ", 410, 250);
		    String mineString = Integer.toString (minecount);          // draws all the stats
		    g.drawString (mineString, 505, 250);
		    g.drawString ("Flags: ", 410, 300);
		    String flagString = Integer.toString (flagAmount);
		    g.drawString ("Size: ", 410, 350);
		    String sizeString = Integer.toString (size);
		    g.drawString (sizeString, 505, 350);
		    g.drawString (flagString, 505, 300);
		    for (int j = 0 ; j < size ; j++)
		    {
			for (int i = 0 ; i < size ; i++)
			{
			    if (squareType [j] [i] != 0)
				g.drawImage (bottomSquares [j] [i], squarex [j] [i], squarey [j] [i], this);
			    else
			    {
				g.setColor (Zero);
				g.fillRect (squarex [j] [i], squarey [j] [i], 16, 16);
			    }
			    if (clickedYet [j] [i] == false)
			    {
				//g.drawImage (squares [j] [i], squarex [j] [i], squarey [j] [i], this);    // draws all the squares
				g.setColor (inside);
				g.fillRect (squarex [j] [i], squarey [j] [i], 16, 16);

				g.setColor (outside);
				g.drawRect (squarex [j] [i], squarey [j] [i], 16, 16);                 // draws all the squares
			    }
			    else
			    {
				g.setColor (Color.black);
				g.drawRect (squarex [j] [i], squarey [j] [i], 16, 16);
			    }

			    if (flagged [j] [i] == true)
			    {
				//g.drawImage (flagdraw [j] [i], squarex [j] [i], squarey [j] [i], this);}
				int polygonfillx[] = new int [3];
				int polygonfilly[] = new int [3];
				polygonfillx [0] = (j * 16) + 5;
				polygonfillx [1] = (j * 16) + 9;
				polygonfillx [2] = (j * 16) + 9;                 // draws the flag
				polygonfilly [0] = (i * 16) + 5;
				polygonfilly [1] = (i * 16) + 1;
				polygonfilly [2] = (i * 16) + 9;
				g.setColor (Color.white);
				g.fillRect ((j * 16) + 9, (i * 16) + 2, 1, 10);
				g.setColor (Color.red);
				g.fillPolygon (polygonfillx, polygonfilly, 3);
			    }
			}
		    }
		    if (clickedYet [poopx] [poopy] == false && flagged [poopx] [poopy] == false && cursorx < (size * 16) && cursory < (size * 16))
			g.drawImage (bluething, cursorx, cursory, this);
		}
		else                                 //  WHAT IS DISPLAYED WHEN YOU WIN
		{
		    //312, 42

		    submit.setEnabled (true);

		    g.drawImage (winScreen, 0, 0, this);
		    g.setColor (Color.white);
		    g.setFont (finalScore);
		    String score2 = Integer.toString (score);       // shows the final score
		    g.drawString (score2, 312, 75);
		    drawBoard (g);

		    if (lose == 0)
		    {
			try
			{
			    if (lose != 3)
			    {
				getText (g); // displays the buttons and textfields when the player wins
				add (submit);
				add (nameField);
				add (playAgain);
			    }
			}
			catch (IOException e)
			{
			}
			finally
			{
			}
		    }

		    if (clickedPlay == 1)               // resets if the user presses play again
		    {
			mode = '1';
			reset ();
		    }

		}
	    }


	    else            // the lose screen
	    {
		if (startAgain == 0)
		{
		    dontStart = System.currentTimeMillis ();        // sets the timer for exiting the lose screen
		    startAgain++;
		}
		else
		{
		    if (System.currentTimeMillis () - dontStart >= 10000)
		    {
			mode = '1';
			reset ();                   // resets after 10s
		    }
		    else if (System.currentTimeMillis () - dontStart > 3000 && System.currentTimeMillis () - dontStart < 10000)
		    {
			// g.setColor (Color.black);
			// g.fillRect (0, 0, 600, 600);
			// g.setColor (Color.white);
			// g.drawString ("YOU LOSE", 200, 200);
			g.drawImage (losingScreen, 0, 0, this);  //displays the losing screen


		    }
		    else
		    {
			// g.setColor (Color.black);
			// g.fillRect (0, 0, 600, 600);
			// g.setColor (Color.gray);
			// g.fillRect (0, 0, 400, 400);
			g.drawImage (mainScreen, 0, 0, this);
			g.drawImage (chatScreen, 100, 430, this);             // draws background
			g.setColor (Color.blue);
			g.drawRect (100, 430, 400, 140);


			starttime2 = (int) (System.currentTimeMillis () - newtime);               // draws the background
			String timer = Integer.toString ((int) (starttime2 / 1000));
			//  g.setFont (title);
			g.setColor (Color.lightGray);
			g.setFont (stats);
			g.drawString ("Score: ", 410, 150);
			g.drawString ("0", 505, 150);
			g.drawString ("Timer: ", 410, 200);                          // draws the timer
			g.drawString ("NA", 505, 200);
			g.drawString ("Mines: ", 410, 250);
			String mineString = Integer.toString (minecount);
			g.drawString (mineString, 505, 250);
			g.drawString ("Flags: ", 410, 300);
			String flagString = Integer.toString (flagAmount);
			g.drawString ("Size: ", 410, 350);
			String sizeString = Integer.toString (size);
			g.drawString (sizeString, 505, 350);
			g.drawString (flagString, 505, 300);
			for (int j = 0 ; j < size ; j++)
			{
			    for (int i = 0 ; i < size ; i++)
			    {
				if (squareType [j] [i] != 0)
				    g.drawImage (bottomSquares [j] [i], squarex [j] [i], squarey [j] [i], this);    // shows the location of the mines
				else
				{
				    g.setColor (Zero);
				    g.fillRect (squarex [j] [i], squarey [j] [i], 16, 16);
				}
				if (squareType [j] [i] != 9 && clickedYet [j] [i] == false)
				{
				    //g.drawImage (squares [j] [i], squarex [j] [i], squarey [j] [i], this);    // draws all the squares
				    g.setColor (inside);
				    g.fillRect (squarex [j] [i], squarey [j] [i], 16, 16);

				    g.setColor (outside);
				    g.drawRect (squarex [j] [i], squarey [j] [i], 16, 16);
				}
				else
				{
				    g.setColor (Color.black);
				    g.drawRect (squarex [j] [i], squarey [j] [i], 16, 16);
				}

				if (flagged [j] [i] == true)
				{
				    //g.drawImage (flagdraw [j] [i], squarex [j] [i], squarey [j] [i], this);}
				    int polygonfillx[] = new int [3];
				    int polygonfilly[] = new int [3];
				    polygonfillx [0] = (j * 16) + 5;
				    polygonfillx [1] = (j * 16) + 9;
				    polygonfillx [2] = (j * 16) + 9;                 // draws the flag
				    polygonfilly [0] = (i * 16) + 5;
				    polygonfilly [1] = (i * 16) + 1;
				    polygonfilly [2] = (i * 16) + 9;
				    g.setColor (Color.white);
				    g.fillRect ((j * 16) + 9, (i * 16) + 2, 1, 10);
				    g.setColor (Color.red);
				    g.fillPolygon (polygonfillx, polygonfilly, 3);
				}
			    }
			}
		    }
		}
	    }
	}
    }



    public void paint4 (Graphics g)           // BASIC MODE
	// used to draw the content of the window (called by windows & java program)
    {
	//  System.out.println ("shed");


	//  setParameters (g);                            // basically does everything

	if (RandomIze == 0)
	    createRandom ();              // creates random field and mine values


	if (System.currentTimeMillis () - starttime < 15 * minecount) // load
	{
	    // g.setColor (Color.black);
	    // g.fillRect (0, 0, 600, 600);
	    // g.setColor (Color.gray);
	    // g.fillRect (0, 0, 400, 400);
	    g.drawImage (mainScreen, 0, 0, this);
	    g.drawImage (chatScreen, 100, 430, this);    // loads
	    g.setColor (Color.blue);
	    g.drawRect (100, 430, 400, 140);
	    g.setColor (Color.lightGray);
	    g.drawString ("LOADING.....", 300, 500);
	    for (int j = 0 ; j < size ; j++)
	    {
		for (int i = 0 ; i < size ; i++)
		{
		    if (squareType [j] [i] != 0)
			g.drawImage (bottomSquares [j] [i], squarex [j] [i], squarey [j] [i], this);   // displays squares
		    else
		    {
			g.setColor (Zero);
			g.fillRect (squarex [j] [i], squarey [j] [i], 16, 16);
		    }
		    g.setColor (inside);
		    g.fillRect (squarex [j] [i], squarey [j] [i], 16, 16);
		    g.setColor (outside);
		    g.drawRect (squarex [j] [i], squarey [j] [i], 16, 16);

		    //   g.drawImage (squares [j] [i], squarex [j] [i], squarey [j] [i], this);
		    // g.setColor (Color.black);
		    // g.fillRect (0, 0, 400, 400);

		}
	    }

	}


	else
	{
	    if (resetTime == 0)
	    {
		resetTime++;
		newtime = System.currentTimeMillis ();
	    }

	    if (checkLose () == false)
	    {
		if (checkWin () == false)
		{
		    // g.setColor (Color.black);
		    // g.fillRect (0, 0, 600, 600);
		    // g.setColor (Color.gray);
		    // g.fillRect (0, 0, 400, 400);
		    g.drawImage (mainScreen, 0, 0, this);
		    g.drawImage (chatScreen, 100, 430, this);             // draws background
		    g.setColor (Color.blue);
		    g.drawRect (100, 430, 400, 140);


		    starttime2 = (int) (System.currentTimeMillis () - newtime);               // draws the background
		    String timer = Integer.toString ((int) (starttime2 / 1000));
		    //  g.setFont (title);
		    g.setColor (Color.lightGray);
		    g.setFont (stats);
		    g.drawString ("Score: ", 410, 150);
		    score = ((500 - (int) (starttime2 / 1000)) * 1) + (size * 2) + (minecount * 30);
		    String score2 = Integer.toString (score);
		    g.drawString (score2, 505, 150);
		    g.drawString ("Timer: ", 410, 200);                          // draws the timer
		    g.drawString (timer, 505, 200);
		    g.drawString ("Mines: ", 410, 250);
		    String mineString = Integer.toString (minecount);
		    g.drawString (mineString, 505, 250);
		    g.drawString ("Flags: ", 410, 300);
		    String flagString = Integer.toString (flagAmount);
		    g.drawString ("Size: ", 410, 350);
		    String sizeString = Integer.toString (size);
		    g.drawString (sizeString, 505, 350);
		    g.drawString (flagString, 505, 300);
		    for (int j = 0 ; j < size ; j++)
		    {
			for (int i = 0 ; i < size ; i++)
			{
			    if (squareType [j] [i] != 0)
				g.drawImage (bottomSquares [j] [i], squarex [j] [i], squarey [j] [i], this);
			    else
			    {
				g.setColor (Zero);
				g.fillRect (squarex [j] [i], squarey [j] [i], 16, 16);
			    }
			    if (clickedYet [j] [i] == false)
			    {
				//g.drawImage (squares [j] [i], squarex [j] [i], squarey [j] [i], this);    // draws all the squares
				g.setColor (inside);
				g.fillRect (squarex [j] [i], squarey [j] [i], 16, 16);

				g.setColor (outside);
				g.drawRect (squarex [j] [i], squarey [j] [i], 16, 16);
			    }
			    else
			    {
				g.setColor (Color.black);
				g.drawRect (squarex [j] [i], squarey [j] [i], 16, 16);
			    }

			    if (flagged [j] [i] == true)
			    {
				//g.drawImage (flagdraw [j] [i], squarex [j] [i], squarey [j] [i], this);}
				int polygonfillx[] = new int [3];
				int polygonfilly[] = new int [3];
				polygonfillx [0] = (j * 16) + 5;
				polygonfillx [1] = (j * 16) + 9;
				polygonfillx [2] = (j * 16) + 9;                 // draws the flag
				polygonfilly [0] = (i * 16) + 5;
				polygonfilly [1] = (i * 16) + 1;
				polygonfilly [2] = (i * 16) + 9;
				g.setColor (Color.white);
				g.fillRect ((j * 16) + 9, (i * 16) + 2, 1, 10);
				g.setColor (Color.red);
				g.fillPolygon (polygonfillx, polygonfilly, 3);
			    }
			}
		    }
		    if (clickedYet [poopx] [poopy] == false && flagged [poopx] [poopy] == false && cursorx < (size * 16) && cursory < (size * 16))
			g.drawImage (bluething, cursorx, cursory, this);
		}
		else                                 //  WHAT IS DISPLAYED WHEN YOU WIN
		{
		    //312, 42

		    submit.setEnabled (true);

		    g.drawImage (winScreen, 0, 0, this);
		    g.setColor (Color.white);
		    g.setFont (finalScore);
		    String score2 = Integer.toString (score);       // shows the final score
		    g.drawString (score2, 312, 75);
		    drawBoard (g);

		    if (lose == 0)
		    {
			try
			{
			    if (lose != 3)
			    {
				getText (g); // displays the buttons and textfields when the player wins
				add (submit);
				add (nameField);
				add (playAgain);
			    }
			}
			catch (IOException e)
			{
			}
			finally
			{
			}
		    }

		    if (clickedPlay == 1)
		    {
			mode = '1';
			reset ();
		    }




		    //     if (startAgain == 0)
		    //     {
		    //         dontStart = System.currentTimeMillis ();                // sets exit timer
		    //         startAgain++;
		    //     }
		    //     else
		    //         if (System.currentTimeMillis () - dontStart > 5000)
		    //         {
		    //             mode = '1';            // goes back to beginning
		    //             reset ();
		    //         }
		}
	    }


	    else
	    {
		if (startAgain == 0)
		{
		    dontStart = System.currentTimeMillis ();
		    startAgain++;
		}
		else
		{
		    if (System.currentTimeMillis () - dontStart >= 10000)
		    {
			mode = '1';
			reset ();
		    }
		    else if (System.currentTimeMillis () - dontStart > 3000 && System.currentTimeMillis () - dontStart < 10000)
		    {
			// g.setColor (Color.black);
			// g.fillRect (0, 0, 600, 600);
			// g.setColor (Color.white);
			// g.drawString ("YOU LOSE", 200, 200);
			g.drawImage (losingScreen, 0, 0, this);


		    }
		    else
		    {
			// g.setColor (Color.black);
			// g.fillRect (0, 0, 600, 600);
			// g.setColor (Color.gray);
			// g.fillRect (0, 0, 400, 400);
			g.drawImage (mainScreen, 0, 0, this);
			g.drawImage (chatScreen, 100, 430, this);             // draws background
			g.setColor (Color.blue);
			g.drawRect (100, 430, 400, 140);


			starttime2 = (int) (System.currentTimeMillis () - newtime);               // draws the background
			String timer = Integer.toString ((int) (starttime2 / 1000));
			//  g.setFont (title);
			g.setColor (Color.lightGray);
			g.setFont (stats);
			g.drawString ("Score: ", 410, 150);
			g.drawString ("0", 505, 150);
			g.drawString ("Timer: ", 410, 200);                          // draws the timer
			g.drawString ("NA", 505, 200);
			g.drawString ("Mines: ", 410, 250);
			String mineString = Integer.toString (minecount);
			g.drawString (mineString, 505, 250);
			g.drawString ("Flags: ", 410, 300);
			String flagString = Integer.toString (flagAmount);
			g.drawString ("Size: ", 410, 350);
			String sizeString = Integer.toString (size);
			g.drawString (sizeString, 505, 350);
			g.drawString (flagString, 505, 300);
			for (int j = 0 ; j < size ; j++)
			{
			    for (int i = 0 ; i < size ; i++)
			    {
				if (squareType [j] [i] != 0)
				    g.drawImage (bottomSquares [j] [i], squarex [j] [i], squarey [j] [i], this);
				else
				{
				    g.setColor (Zero);
				    g.fillRect (squarex [j] [i], squarey [j] [i], 16, 16);
				}
				if (squareType [j] [i] != 9 && clickedYet [j] [i] == false)
				{
				    //g.drawImage (squares [j] [i], squarex [j] [i], squarey [j] [i], this);    // draws all the squares
				    g.setColor (inside);
				    g.fillRect (squarex [j] [i], squarey [j] [i], 16, 16);

				    g.setColor (outside);
				    g.drawRect (squarex [j] [i], squarey [j] [i], 16, 16);
				}
				else
				{
				    g.setColor (Color.black);
				    g.drawRect (squarex [j] [i], squarey [j] [i], 16, 16);
				}

				if (flagged [j] [i] == true)
				{
				    //g.drawImage (flagdraw [j] [i], squarex [j] [i], squarey [j] [i], this);}
				    int polygonfillx[] = new int [3];
				    int polygonfilly[] = new int [3];
				    polygonfillx [0] = (j * 16) + 5;
				    polygonfillx [1] = (j * 16) + 9;
				    polygonfillx [2] = (j * 16) + 9;                 // draws the flag
				    polygonfilly [0] = (i * 16) + 5;
				    polygonfilly [1] = (i * 16) + 1;
				    polygonfilly [2] = (i * 16) + 9;
				    g.setColor (Color.white);
				    g.fillRect ((j * 16) + 9, (i * 16) + 2, 1, 10);
				    g.setColor (Color.red);
				    g.fillPolygon (polygonfillx, polygonfilly, 3);
				}
			    }
			}
		    }
		}
	    }
	}
    }


    public void paint7 (Graphics g)           // BASIC MODE
	// used to draw the content of the window (called by windows & java program)
    {
	//  System.out.println ("shed");

	if (RandomIze == 0)
	    setPowerups ();


	if (System.currentTimeMillis () - starttime < 15 * minecount) // load
	{
	    // g.setColor (Color.black);
	    // g.fillRect (0, 0, 600, 600);
	    // g.setColor (Color.gray);
	    // g.fillRect (0, 0, 400, 400);
	    g.drawImage (mainScreen, 0, 0, this);


	    g.drawImage (chatScreen, 100, 430, this);
	    g.setColor (Color.blue);
	    g.drawRect (100, 430, 400, 140);
	    g.setColor (Color.lightGray);
	    g.drawString ("LOADING.....", 300, 500);
	    for (int j = 0 ; j < size ; j++)
	    {
		for (int i = 0 ; i < size ; i++)
		{
		    if (squareType [j] [i] != 0)
			g.drawImage (bottomSquares [j] [i], squarex [j] [i], squarey [j] [i], this);
		    else
		    {
			if (powerUp [j] [i] != 0)
			{
			    if (powerUp [j] [i] == 1)
				g.drawImage (powerGhost, squarex [j] [i], squarey [j] [i], this);
			    else if (powerUp [j] [i] == 2)
				g.drawImage (powerHealth, squarex [j] [i], squarey [j] [i], this);
			    else if (powerUp [j] [i] == 3)
				g.drawImage (powerScore, squarex [j] [i], squarey [j] [i], this);
			    else if (powerUp [j] [i] == 4)
				g.drawImage (powerGun, squarex [j] [i], squarey [j] [i], this);
			    else if (powerUp [j] [i] == 5)
				g.drawImage (powerBomb, squarex [j] [i], squarey [j] [i], this);
			}

			//}
			else
			{
			    g.setColor (Zero);
			    g.fillRect (squarex [j] [i], squarey [j] [i], 16, 16);
			}
		    }
		    g.setColor (inside);
		    g.fillRect (squarex [j] [i], squarey [j] [i], 16, 16);
		    g.setColor (outside);
		    g.drawRect (squarex [j] [i], squarey [j] [i], 16, 16);

		    //   g.drawImage (squares [j] [i], squarex [j] [i], squarey [j] [i], this);
		    // g.setColor (Color.black);
		    // g.fillRect (0, 0, 400, 400);

		}
	    }

	}


	else
	{
	    if (resetTime == 0)
	    {
		resetTime++;
		newtime = System.currentTimeMillis ();
	    }

	    if (checkLose () == false)
	    {
		if (checkWin () == false)
		{
		    // g.setColor (Color.black);
		    // g.fillRect (0, 0, 600, 600);
		    // g.setColor (Color.gray);
		    // g.fillRect (0, 0, 400, 400);
		    g.drawImage (mainScreen, 0, 0, this);
		    g.drawImage (chatScreen, 100, 430, this);             // draws background
		    g.setColor (Color.blue);
		    g.drawRect (100, 430, 400, 140);


		    starttime2 = (int) (System.currentTimeMillis () - newtime);               // draws the background
		    String timer = Integer.toString ((int) (starttime2 / 1000));
		    //  g.setFont (title);
		    g.setColor (Color.lightGray);
		    g.setFont (stats);
		    g.drawString ("Score: ", 410, 150);
		    score = ((500 - (int) (starttime2 / 1000)) * 1) + (30 - size) * 2 + (minecount * 30);
		    String score2 = Integer.toString (score);
		    g.drawString (score2, 505, 150);
		    g.drawString ("Timer: ", 410, 200);                          // draws the timer
		    g.drawString (timer, 505, 200);
		    g.drawString ("Mines: ", 410, 250);
		    String mineString = Integer.toString (minecount);
		    g.drawString (mineString, 505, 250);
		    g.drawString ("Flags: ", 410, 300);
		    String flagString = Integer.toString (flagAmount);
		    g.drawString ("Size: ", 410, 350);
		    String sizeString = Integer.toString (size);
		    g.drawString (sizeString, 505, 350);
		    g.drawString (flagString, 505, 300);
		    for (int j = 0 ; j < size ; j++)
		    {
			for (int i = 0 ; i < size ; i++)
			{
			    if (squareType [j] [i] != 0)
				g.drawImage (bottomSquares [j] [i], squarex [j] [i], squarey [j] [i], this);
			    else
			    {
				if (powerUp [j] [i] != 0)
				{
				    if (powerUp [j] [i] == 1)
					g.drawImage (powerGhost, squarex [j] [i], squarey [j] [i], this);
				    else if (powerUp [j] [i] == 2)
					g.drawImage (powerHealth, squarex [j] [i], squarey [j] [i], this);
				    else if (powerUp [j] [i] == 3)
					g.drawImage (powerScore, squarex [j] [i], squarey [j] [i], this);
				    else if (powerUp [j] [i] == 4)
					g.drawImage (powerGun, squarex [j] [i], squarey [j] [i], this);
				    else if (powerUp [j] [i] == 5)
					g.drawImage (powerBomb, squarex [j] [i], squarey [j] [i], this);
				}
				else
				{

				    g.setColor (Zero);
				    g.fillRect (squarex [j] [i], squarey [j] [i], 16, 16);
				}
			    }
			    if (clickedYet [j] [i] == false)
			    {
				//g.drawImage (squares [j] [i], squarex [j] [i], squarey [j] [i], this);    // draws all the squares
				g.setColor (inside);
				g.fillRect (squarex [j] [i], squarey [j] [i], 16, 16);

				g.setColor (outside);
				g.drawRect (squarex [j] [i], squarey [j] [i], 16, 16);
			    }
			    else
			    {
				g.setColor (Color.black);
				g.drawRect (squarex [j] [i], squarey [j] [i], 16, 16);
			    }

			    if (flagged [j] [i] == true)
			    {
				//g.drawImage (flagdraw [j] [i], squarex [j] [i], squarey [j] [i], this);}
				int polygonfillx[] = new int [3];
				int polygonfilly[] = new int [3];
				polygonfillx [0] = (j * 16) + 5;
				polygonfillx [1] = (j * 16) + 9;
				polygonfillx [2] = (j * 16) + 9;                 // draws the flag
				polygonfilly [0] = (i * 16) + 5;
				polygonfilly [1] = (i * 16) + 1;
				polygonfilly [2] = (i * 16) + 9;
				g.setColor (Color.white);
				g.fillRect ((j * 16) + 9, (i * 16) + 2, 1, 10);
				g.setColor (Color.red);
				g.fillPolygon (polygonfillx, polygonfilly, 3);
			    }
			}
		    }
		    if (clickedYet [poopx] [poopy] == false && flagged [poopx] [poopy] == false && cursorx < (size * 16) && cursory < (size * 16))
			g.drawImage (bluething, cursorx, cursory, this);
		}
		else                                 //  WHAT IS DISPLAYED WHEN YOU WIN
		{
		    //312, 42

		    submit.setEnabled (true);

		    g.drawImage (winScreen, 0, 0, this);
		    g.setColor (Color.white);
		    g.setFont (finalScore);
		    String score2 = Integer.toString (score);       // shows the final score
		    g.drawString (score2, 312, 75);
		    drawBoard (g);

		    if (lose == 0)
		    {
			try
			{
			    if (lose != 3)
			    {
				getText (g); // displays the buttons and textfields when the player wins
				add (submit);
				add (nameField);
				add (playAgain);
			    }
			}
			catch (IOException e)
			{
			}
			finally
			{
			}
		    }

		    if (clickedPlay == 1)
		    {
			mode = '1';
			reset ();
		    }




		    //     if (startAgain == 0)
		    //     {
		    //         dontStart = System.currentTimeMillis ();                // sets exit timer
		    //         startAgain++;
		    //     }
		    //     else
		    //         if (System.currentTimeMillis () - dontStart > 5000)
		    //         {
		    //             mode = '1';            // goes back to beginning
		    //             reset ();
		    //         }
		}
	    }


	    else
	    {
		if (startAgain == 0)
		{
		    dontStart = System.currentTimeMillis ();
		    startAgain++;
		}
		else
		{
		    if (System.currentTimeMillis () - dontStart >= 10000)
		    {
			mode = '1';
			reset ();
		    }
		    else if (System.currentTimeMillis () - dontStart > 3000 && System.currentTimeMillis () - dontStart < 10000)
		    {
			// g.setColor (Color.black);
			// g.fillRect (0, 0, 600, 600);
			// g.setColor (Color.white);
			// g.drawString ("YOU LOSE", 200, 200);
			g.drawImage (losingScreen, 0, 0, this);


		    }
		    else
		    {
			// g.setColor (Color.black);
			// g.fillRect (0, 0, 600, 600);
			// g.setColor (Color.gray);
			// g.fillRect (0, 0, 400, 400);
			g.drawImage (mainScreen, 0, 0, this);
			g.drawImage (chatScreen, 100, 430, this);             // draws background
			g.setColor (Color.blue);
			g.drawRect (100, 430, 400, 140);


			starttime2 = (int) (System.currentTimeMillis () - newtime);               // draws the background
			String timer = Integer.toString ((int) (starttime2 / 1000));
			//  g.setFont (title);
			g.setColor (Color.lightGray);
			g.setFont (stats);
			g.drawString ("Score: ", 410, 150);
			g.drawString ("0", 505, 150);
			g.drawString ("Timer: ", 410, 200);                          // draws the timer
			g.drawString ("NA", 505, 200);
			g.drawString ("Mines: ", 410, 250);
			String mineString = Integer.toString (minecount);
			g.drawString (mineString, 505, 250);
			g.drawString ("Flags: ", 410, 300);
			String flagString = Integer.toString (flagAmount);
			g.drawString ("Size: ", 410, 350);
			String sizeString = Integer.toString (size);
			g.drawString (sizeString, 505, 350);
			g.drawString (flagString, 505, 300);
			for (int j = 0 ; j < size ; j++)
			{
			    for (int i = 0 ; i < size ; i++)
			    {
				if (squareType [j] [i] != 0)
				    g.drawImage (bottomSquares [j] [i], squarex [j] [i], squarey [j] [i], this);
				else
				{
				    g.setColor (Zero);
				    g.fillRect (squarex [j] [i], squarey [j] [i], 16, 16);
				}
				if (squareType [j] [i] != 9 && clickedYet [j] [i] == false)
				{
				    //g.drawImage (squares [j] [i], squarex [j] [i], squarey [j] [i], this);    // draws all the squares
				    g.setColor (inside);
				    g.fillRect (squarex [j] [i], squarey [j] [i], 16, 16);

				    g.setColor (outside);
				    g.drawRect (squarex [j] [i], squarey [j] [i], 16, 16);
				}
				else
				{
				    g.setColor (Color.black);
				    g.drawRect (squarex [j] [i], squarey [j] [i], 16, 16);
				}

				if (flagged [j] [i] == true)
				{
				    //g.drawImage (flagdraw [j] [i], squarex [j] [i], squarey [j] [i], this);}
				    int polygonfillx[] = new int [3];
				    int polygonfilly[] = new int [3];
				    polygonfillx [0] = (j * 16) + 5;
				    polygonfillx [1] = (j * 16) + 9;
				    polygonfillx [2] = (j * 16) + 9;                 // draws the flag
				    polygonfilly [0] = (i * 16) + 5;
				    polygonfilly [1] = (i * 16) + 1;
				    polygonfilly [2] = (i * 16) + 9;
				    g.setColor (Color.white);
				    g.fillRect ((j * 16) + 9, (i * 16) + 2, 1, 10);
				    g.setColor (Color.red);
				    g.fillPolygon (polygonfillx, polygonfilly, 3);
				}
			    }
			}
		    }
		}
	    }
	}
    }



    ///////////////////////////////////////////////////////////////////////////

    ////////MOUSE/KEYBOARD COMMANDS/////////////////////////


    public void mouseEntered (MouseEvent e)
    {
	// called when the pointer enters the applet's rectangular area
    }


    public void mouseExited (MouseEvent e)
    {
	// called when the pointer leaves the applet's rectangular area
    }


    public void mouseClicked (MouseEvent e)  // when the user clicks on a size/mine button
    {

	if (mode == '1')
	{
	    int locx = e.getX ();
	    int locy = e.getY ();


	    if (locx > 6 && locx < 168) // column 1
	    {
		if (locy > 148 && locy < 188)
		{
		    size = 8;
		    mode = '2';
		}
		else if (locy > 225 && locy < 265)
		{
		    size = 9;
		    mode = '2';
		}
		else if (locy > 298 && locy < 345)         // determine size
		{
		    size = 10;
		    mode = '2';
		}
		else if (locy > 369 && locy < 416)
		{
		    size = 11;
		    mode = '2';
		}
		else if (locy > 440 && locy < 484)
		{
		    size = 12;
		    mode = '2';
		}
		else if (locy > 516 && locy < 560)
		{
		    size = 13;
		    mode = '2';
		}
	    }
	    if (locx > 216 && locx < 383)  // column 2
	    {
		if (locy > 148 && locy < 188)
		{
		    size = 14;
		    mode = '2';
		}
		else if (locy > 225 && locy < 265)
		{
		    size = 15;
		    mode = '2';
		}
		else if (locy > 298 && locy < 345)         // determine size
		{
		    size = 16;
		    mode = '2';
		}
		else if (locy > 369 && locy < 416)
		{
		    size = 17;
		    mode = '2';
		}
		else if (locy > 440 && locy < 484)
		{
		    size = 18;
		    mode = '2';
		}
		else if (locy > 516 && locy < 560)
		{
		    size = 19;
		    mode = '2';
		}
	    }
	    if (locx > 427 && locx < 593)  // column 3
	    {
		if (locy > 148 && locy < 188)
		{
		    size = 20;
		    mode = '2';
		}
		else if (locy > 225 && locy < 265)
		{
		    size = 21;
		    mode = '2';
		}
		else if (locy > 298 && locy < 345)         // determine size
		{
		    size = 22;
		    mode = '2';
		}
		else if (locy > 369 && locy < 416)
		{
		    size = 23;
		    mode = '2';
		}
		else if (locy > 440 && locy < 484)
		{
		    size = 24;
		    mode = '2';
		}
		else if (locy > 516 && locy < 560)
		{
		    size = 25;
		    mode = '2';
		}
	    }

	    if (locy > 75 && locy < 122 && locx > 216 && locx < 382)
		mode = '0';
	    repaint ();
	    locx = 0;
	    locy = 0;
	    reset ();
	}



	if (mode == '2')             // pick your mines
	{
	    if (FIRSTTIMEMINES == 1)
	    {
		int locx = e.getX ();
		int locy = e.getY ();

		if (locy > 75 && locy < 122 && locx > 216 && locx < 382)
		    mode = '1';






		if (locx > 6 && locx < 168) // column 1
		{
		    if (locy > 148 && locy < 188)
		    {
			minecount = 3;
			mode = '3';
		    }
		    else if (locy > 225 && locy < 265)
		    {
			minecount = 5;
			mode = '3';
		    }
		    else if (locy > 298 && locy < 345)         // determine mines
		    {
			minecount = 10;
			mode = '3';
		    }
		    else if (locy > 369 && locy < 416)
		    {
			minecount = 15;
			mode = '3';
		    }
		    else if (locy > 440 && locy < 484)
		    {
			minecount = 20;
			mode = '3';
		    }
		    else if (locy > 516 && locy < 560)
		    {
			minecount = 25;
			mode = '3';
		    }
		}
		if (locx > 216 && locx < 383)  // column 2
		{
		    if (locy > 148 && locy < 188)
		    {
			minecount = 30;
			mode = '3';
		    }
		    else if (locy > 225 && locy < 265)
		    {
			minecount = 35;
			mode = '3';
		    }
		    else if (locy > 298 && locy < 345)         // determine mines
		    {
			minecount = 40;
			mode = '3';
		    }
		    else if (locy > 369 && locy < 416)
		    {
			minecount = 45;
			mode = '3';
		    }
		    else if (locy > 440 && locy < 484)
		    {
			minecount = 50;
			mode = '3';
		    }
		    else if (locy > 516 && locy < 560)
		    {
			minecount = 60;
			mode = '3';
		    }
		}
		if (locx > 427 && locx < 593)  // column 3
		{
		    if (locy > 148 && locy < 188)
		    {
			if (size > 8)
			{
			    minecount = 80;
			    mode = '3';
			}
		    }
		    else if (locy > 225 && locy < 265)
		    {
			if (size > 10)
			{
			    minecount = 100;
			    mode = '3';
			}
		    }
		    else if (locy > 298 && locy < 345)         // determine mines
		    {
			if (size > 12)
			{
			    minecount = 150;
			    mode = '3';
			}
		    }
		    else if (locy > 369 && locy < 416)
		    {
			if (size > 14)
			{
			    minecount = 200;
			    mode = '3';
			}
		    }
		    else if (locy > 440 && locy < 484)
		    {
			if (size > 17)
			{
			    minecount = 300;
			    mode = '3';
			}
		    }
		    else if (locy > 516 && locy < 560)
		    {
			if (size > 22)
			{
			    minecount = 500;
			    mode = '3';
			}
		    }
		}
		reset ();
	    }
	    else
	    {
		FIRSTTIMEMINES++;
	    }



	}


	if (mode == '5')
	{
	    int locx = e.getX ();
	    int locy = e.getY ();


	    if (locx > 6 && locx < 168) // column 1
	    {
		if (locy > 148 && locy < 188)
		{
		    size = 8;
		    mode = '6';
		}
		else if (locy > 225 && locy < 265)
		{
		    size = 9;
		    mode = '6';
		}
		else if (locy > 298 && locy < 345)         // determine size
		{
		    size = 10;
		    mode = '6';
		}
		else if (locy > 369 && locy < 416)
		{
		    size = 11;
		    mode = '6';
		}
		else if (locy > 440 && locy < 484)
		{
		    size = 12;
		    mode = '6';
		}
		else if (locy > 516 && locy < 560)
		{
		    size = 13;
		    mode = '6';
		}
	    }
	    if (locx > 216 && locx < 383)  // column 2
	    {
		if (locy > 148 && locy < 188)
		{
		    size = 14;
		    mode = '6';
		}
		else if (locy > 225 && locy < 265)
		{
		    size = 15;
		    mode = '6';
		}
		else if (locy > 298 && locy < 345)         // determine size
		{
		    size = 16;
		    mode = '6';
		}
		else if (locy > 369 && locy < 416)
		{
		    size = 17;
		    mode = '6';
		}
		else if (locy > 440 && locy < 484)
		{
		    size = 18;
		    mode = '6';
		}
		else if (locy > 516 && locy < 560)
		{
		    size = 19;
		    mode = '6';
		}
	    }
	    if (locx > 427 && locx < 593)  // column 3
	    {
		if (locy > 148 && locy < 188)
		{
		    size = 20;
		    mode = '6';
		}
		else if (locy > 225 && locy < 265)
		{
		    size = 21;
		    mode = '6';
		}
		else if (locy > 298 && locy < 345)         // determine size
		{
		    size = 22;
		    mode = '6';
		}
		else if (locy > 369 && locy < 416)
		{
		    size = 23;
		    mode = '6';
		}
		else if (locy > 440 && locy < 484)
		{
		    size = 24;
		    mode = '6';
		}
		else if (locy > 516 && locy < 560)
		{
		    size = 25;
		    mode = '6';
		}
	    }

	    if (locy > 75 && locy < 122 && locx > 216 && locx < 382)
		mode = '0';
	    repaint ();
	    locx = 0;
	    locy = 0;
	    reset ();
	}



	if (mode == '6')             // pick your mines
	{
	    if (FIRSTTIMEMINES == 1)
	    {
		int locx = e.getX ();
		int locy = e.getY ();

		if (locy > 75 && locy < 122 && locx > 216 && locx < 382)
		    mode = '5';






		if (locx > 6 && locx < 168) // column 1
		{
		    if (locy > 148 && locy < 188)
		    {
			minecount = 3;
			mode = '7';
		    }
		    else if (locy > 225 && locy < 265)
		    {
			minecount = 5;
			mode = '7';
		    }
		    else if (locy > 298 && locy < 345)         // determine mines
		    {
			minecount = 10;
			mode = '7';
		    }
		    else if (locy > 369 && locy < 416)
		    {
			minecount = 15;
			mode = '7';
		    }
		    else if (locy > 440 && locy < 484)
		    {
			minecount = 20;
			mode = '7';
		    }
		    else if (locy > 516 && locy < 560)
		    {
			minecount = 25;
			mode = '7';
		    }
		}
		if (locx > 216 && locx < 383)  // column 2
		{
		    if (locy > 148 && locy < 188)
		    {
			minecount = 30;
			mode = '7';
		    }
		    else if (locy > 225 && locy < 265)
		    {
			minecount = 35;
			mode = '7';
		    }
		    else if (locy > 298 && locy < 345)         // determine mines
		    {
			minecount = 40;
			mode = '7';
		    }
		    else if (locy > 369 && locy < 416)
		    {
			minecount = 45;
			mode = '7';
		    }
		    else if (locy > 440 && locy < 484)
		    {
			minecount = 50;
			mode = '7';
		    }
		    else if (locy > 516 && locy < 560)
		    {
			minecount = 60;
			mode = '7';
		    }
		}
		if (locx > 427 && locx < 593)  // column 3
		{
		    if (locy > 148 && locy < 188)
		    {
			if (size > 8)
			{
			    minecount = 80;
			    mode = '7';
			}
		    }
		    else if (locy > 225 && locy < 265)
		    {
			if (size > 10)
			{
			    minecount = 100;
			    mode = '7';
			}
		    }
		    else if (locy > 298 && locy < 345)         // determine mines
		    {
			if (size > 12)
			{
			    minecount = 150;
			    mode = '7';
			}
		    }
		    else if (locy > 369 && locy < 416)
		    {
			if (size > 14)
			{
			    minecount = 200;
			    mode = '7';
			}
		    }
		    else if (locy > 440 && locy < 484)
		    {
			if (size > 17)
			{
			    minecount = 300;
			    mode = '7';
			}
		    }
		    else if (locy > 516 && locy < 560)
		    {
			if (size > 22)
			{
			    minecount = 500;
			    mode = '7';
			}
		    }
		}
		reset ();
	    }
	    else
	    {
		FIRSTTIMEMINES++;
	    }



	}


	if (mode == '3' || mode == '4' || mode == '7')             // clears the square that's been clicked
	{
	    if (FIRSTTIMEMINES == 1)
	    {
		int locx = e.getX ();
		int locy = e.getY ();

		if (e.getButton () == 1)
		{
		    for (int j = 0 ; j < size ; j++)
		    {
			for (int i = 0 ; i < size ; i++)
			{

			    if (locx > squarex [j] [i] && locx < squarex [j] [i] + 16 && locy > squarey [j] [i] && locy < squarey [j] [i] + 16)
			    {
				if (flagged [j] [i] != true)
				{
				    // {
				    //     flagAmount++;
				    //     flagged [j] [i] = false;
				    // }

				    clickedYet [j] [i] = true;                    // shows the part if clicked
				    if (squareType [j] [i] == 9)
					showStatus ("YOU SUCK");
				    if (squareType [j] [i] == 0)
					expandZeros (j, i);
				}

			    }


			}
		    }
		}

		else
		{
		    for (int j = 0 ; j < size ; j++)
		    {
			for (int i = 0 ; i < size ; i++)
			{

			    if (locx > squarex [j] [i] && locx < squarex [j] [i] + 16 && locy > squarey [j] [i] && locy < squarey [j] [i] + 16)
			    {
				if (flagged [j] [i] == false && clickedYet [j] [i] == false)
				{
				    //    squares [j] [i] = getImage (getDocumentBase (), "flaggedsquare.png");   // flags/unflags a square
				    flagged [j] [i] = true;
				    flagAmount--;
				}
				else if (flagged [j] [i] == true && clickedYet [j] [i] == false)
				{
				    // squares [j] [i] = getImage (getDocumentBase (), "normalsquare.png");
				    flagged [j] [i] = false;
				    flagAmount++;
				}
				repaint ();

			    }


			}
		    }
		}
	    }
	    else
	    {
		FIRSTTIMEMINES++;
	    }
	}




    }


    public void mousePressed (MouseEvent e)
    {

    }


    public void mouseReleased (MouseEvent e)
    {
    }


    public void mouseMoved (MouseEvent e)
    { // called during motion when no buttons are down
	cursorx = e.getX ();
	cursory = e.getY ();
	showStatus ("Mouse at (" + cursorx + "," + cursory + ")");
	poopx = (int) (cursorx / 16);
	poopy = (int) (cursory / 16);
	cursorx = (poopx * 16);
	cursory = (poopy * 16);


    }


    public void mouseDragged (MouseEvent e)
    {
    }


    public void keyPressed (KeyEvent e)             // if the user presses a key on the mode screen
    {
	if (mode == '0')
	{
	    mode = e.getKeyChar ();
	    if (mode != '1' && mode != '4' && mode != '5')
		mode = '0';
	}
    }


    public void keyReleased (KeyEvent e)
    {
    }


    public void keyTyped (KeyEvent e)
    {

    }









    //////////////////////////////////////////////////////////////////////

    public static void setPowerups ()                            // makes the powerup locations
    {
	for (int j = 0 ; j < size ; j++)
	    for (int i = 0 ; i < size ; i++)
	    {
		if (squareType [j] [i] == 0)
		{
		    int rog = (int) (Math.random () * 300);
		    if (rog == 0)
		    {
			powerUp [j] [i] = (int) (Math.random () * 5 + 1);
		    }
		}
	    }
	RandomIze = 1;
    }


    public static void expandZeros (int j, int i)   // if the user clicks a square with 0
    {
	//clickedYet [j] [i] = true;

	//System.out.println (j + "    " + i);
	// if (j != 0 && j != (size - 1) && i != 0 && i != (size - 1))
	// {

	//   }

	// if (j != 0)

	if (j > 0)
	{
	    if (clickedYet [j - 1] [i] == false && flagged [j - 1] [i] == false)
	    {
		clickedYet [j - 1] [i] = true;
		if (squareType [j - 1] [i] == 0)
		    expandZeros (j - 1, i);
	    }
	}


	//  if (j != 0 && i != 0)    (j > -1 && i > -1)
	if (j > 0 && i > 0)
	{
	    if (clickedYet [j - 1] [i - 1] == false && flagged [j - 1] [i - 1] == false)    //bad
	    {
		clickedYet [j - 1] [i - 1] = true;
		if (squareType [j - 1] [i - 1] == 0)
		    expandZeros (j - 1, i - 1);
	    }
	}


	//if (j != 0 && i != (size - 1))
	if (j > 0 && i < size - 1)
	{
	    if (clickedYet [j - 1] [i + 1] == false && flagged [j - 1] [i + 1] == false)
	    {
		clickedYet [j - 1] [i + 1] = true;
		if (squareType [j - 1] [i + 1] == 0)
		    expandZeros (j - 1, i + 1);

	    }
	}


	//if (j != (size - 1))
	if (j < size - 1)
	{
	    if (clickedYet [j + 1] [i] == false && flagged [j + 1] [i] == false)
	    {
		clickedYet [j + 1] [i] = true;
		if (squareType [j + 1] [i] == 0)
		    expandZeros (j + 1, i);
	    }
	}


	// if (j != (size - 1) && i != 0)
	if (j < size - 1 && i > 0)
	{
	    if (clickedYet [j + 1] [i - 1] == false && flagged [j + 1] [i - 1] == false)
	    {
		clickedYet [j + 1] [i - 1] = true;
		if (squareType [j + 1] [i - 1] == 0)
		    expandZeros (j + 1, i - 1);
	    }
	}


	// if (j != (size - 1) && j != (size - 1))
	if (j < size - 1 && i < size - 1)
	{
	    if (clickedYet [j + 1] [i + 1] == false && flagged [j + 1] [i + 1] == false)
	    {
		clickedYet [j + 1] [i + 1] = true;
		if (squareType [j + 1] [i + 1] == 0)
		    expandZeros (j + 1, i + 1);

	    }
	}


	//   if (i != 0)
	if (i > 0)
	{
	    if (clickedYet [j] [i - 1] == false && flagged [j] [i - 1] == false)
	    {
		clickedYet [j] [i - 1] = true;
		if (squareType [j] [i - 1] == 0)
		    expandZeros (j, i - 1);
	    }
	}


	if (i < size - 1)
	    // if (i != (size - 1))
	    {
		if (clickedYet [j] [i + 1] == false && flagged [j] [i + 1] == false)
		{
		    clickedYet [j] [i + 1] = true;
		    if (squareType [j] [i + 1] == 0)
			expandZeros (j, i + 1);
		}
	    }
    }


    ////////////CORE PROGRAM////////////////////////////////////////////////


    public static boolean checkWin ()    // returns true if game is won
    {
	for (int j = 0 ; j < size ; j++)
	{
	    for (int i = 0 ; i < size ; i++)
	    {
		if (squareType [j] [i] != 9)
		{
		    if (clickedYet [j] [i] != true)
			return false;
		}
	    }
	}


	return true;
    }


    public static boolean checkLose ()         // returns true if mine is clicked
    {
	for (int j = 0 ; j < size ; j++)
	{
	    for (int i = 0 ; i < size ; i++)
	    {
		if (squareType [j] [i] == 9)
		{
		    if (clickedYet [j] [i] == true)
			return true;
		}
	    }
	}


	return false;
    }


    public static void setParameters2 (int x, int y, Graphics c)
    {
	int poop = 0;
    }


    public void createRandom ()
    {
	size = (int) (Math.random () * 17 + 9);
	minecount = 9999999;
	while (minecount > (size * size))
	{
	    minecount = (int) (Math.random () * 497 + 3);          // creates random sizes and mine counts
	}


	reset ();
	RandomIze = 1;
    }


    public void reset ()                     // basically like init except runs during the program, resets all variables
    {
	for (int j = 0 ; j < size ; j++)
	{
	    for (int i = 0 ; i < size ; i++)
	    {
		squarex [j] [i] = (j * 16);
		squarey [j] [i] = (i * 16);
		onHover [j] [i] = false;
		flagged [j] [i] = false;
		clickedYet [j] [i] = false;
		powerUp [j] [i] = 0;
		// int putMine = (int) (Math.random () * 50);
		// if (putMine == 0 && minesadded < minecount)      // adds random mines
		// {
		//     squareType [j] [i] = 9;
		//     minesadded++;
		// }


	    }
	}


	RandomIze = 0;
	clickedPlay = 0;
	lose = 0;
	for (int j = 0 ; j < size ; j++)
	    for (int i = 0 ; i < size ; i++)        // resets square type
		squareType [j] [i] = 0;

	FIRSTTIMEMINES = 0;

	minesadded = 0;   // resets mines
	while (minesadded < (minecount + 0))
	{
	    for (int j = 0 ; j < size ; j++)
	    {
		for (int i = 0 ; i < size ; i++)
		{
		    int putMine = (int) (Math.random () * (size * 10));
		    if (putMine == 0 && minesadded < minecount)      // adds random mines
		    {
			squareType [j] [i] = 9;
			minesadded++;
		    }
		    // if (minesadded == minecount)
		    //     break;
		}
	    }
	}


	flagAmount = minecount;           // resets minecount



	bluething = getImage (getDocumentBase (), "hoversquare.png");
	//squares [3] [1] = getImage (getDocumentBase (), "hoversquare.png");


	//IN THIS PART I SET THE NUMBER OF EACH SQUARE

	for (int j = 0 ; j < size ; j++)
	{
	    for (int i = 0 ; i < size ; i++)
	    {
		if (squareType [j] [i] != 9)
		{
		    int numMineAround = 0;
		    if (j != 0)
			if (squareType [j - 1] [i] == 9)
			    numMineAround++;
		    if (j != 0 && i != 0)
			if (squareType [j - 1] [i - 1] == 9)
			    numMineAround++;
		    if (j != 0 && i != (size - 1))
			if (squareType [j - 1] [i + 1] == 9)        // determines how many mines are around each piece
			    numMineAround++;                        // avoids exceeding array size
		    if (j != (size - 1))
			if (squareType [j + 1] [i] == 9)
			    numMineAround++;
		    if (j != (size - 1) && i != 0)
			if (squareType [j + 1] [i - 1] == 9)
			    numMineAround++;
		    if (j != (size - 1) && j != (size - 1))
			if (squareType [j + 1] [i + 1] == 9)
			    numMineAround++;
		    if (i != 0)
			if (squareType [j] [i - 1] == 9)
			    numMineAround++;
		    if (i != (size - 1))
			if (squareType [j] [i + 1] == 9)
			    numMineAround++;

		    squareType [j] [i] = numMineAround;

		}

	    }
	}


	score = 0;



	//FINISHED



	for (int j = 0 ; j < size ; j++)
	{
	    for (int i = 0 ; i < size ; i++)
	    {
		bottomSquares [j] [i] = val [squareType [j] [i]];
	    }
	}


	//squares [2] [3] = getImage (getDocumentBase (), "square5.png");


	resetTime = 0;
	startAgain = 0;
	dontStart = 0;
	starttime = System.currentTimeMillis ();
    }


    public void getText (Graphics g) throws IOException // used to submit a score
    {
	submit.setEnabled (true);
	add (submit);
	add (nameField);
	lose = 3;
    }


    public void update (Graphics g)
    {
	if (doubleBufferImage == null) // if this is the first time, create the buffer
	{
	    doubleBufferImage = createImage (this.getSize ().width, this.getSize ().height);
	    doubleBufferG = doubleBufferImage.getGraphics ();
	}


	doubleBufferG.setColor (getBackground ()); // clear the screen
	doubleBufferG.fillRect (0, 0, this.getSize ().width, this.getSize ().height);
	doubleBufferG.setColor (getForeground ()); // draw to the hidden buffer
	paint (doubleBufferG);

	g.drawImage (doubleBufferImage, 0, 0, this); // move the hidden buffer to the screen
    }


    /////////////////////////////////////////////////////////////////////////
}




