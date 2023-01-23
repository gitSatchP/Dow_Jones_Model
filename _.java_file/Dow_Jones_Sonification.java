import processing.core.*;
import processing.data.*;
import processing.event.*;
import processing.opengl.*;

import processing.sound.*;

import java.util.HashMap;
import java.util.ArrayList;
import java.io.File;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

/**
 * A program for illustrating and sonifying data from companies in the Dow Jones stock
 * index. Companies are represented by notes, and an axis of time and number of shares
 * purchased is represented by a music staff. The vertical axis of the staff is time,
 * and the horizontal axis is number of shares purchased The program compares companies
 * on different points of this axis when their icons are dragged onto the staff, which
 * triggers either a "positive" or "negative" sound to represent if your portfolio is
 * positive or negative
 *
 * @author Satchel Peterson
 */
public class Music_Note_Sketch extends PApplet {

    // Sound files for positive and negative music
    SoundFile positiveMusic;
    SoundFile negativeMusic;

    // ArrayList to store music notes
    ArrayList<musicNote> notes = new ArrayList<musicNote>();

    // String array for different time periods
    String[] year = {"oneWeek", "oneMonth", "threeMonth", "sixMonth",
            "yearToDate", "oneYear", "twoYear", "threeYear"};

    // Variables to control sound playback
    boolean playSound = false;
    boolean spaceBar = true;
    boolean positive = true;

    // JSON objects to store company data
    JSONObject jsonCompanies;
    JSONObject companyData;

    // Variable to store the name of the company associated with the current music note
    String companyName;

    // Variable to store the index of the company in the JSON data
    int jsonIndex;

    // Variable to store the change amount of the company for a specific time period
    double changeAmount;

    /**
     * Method which initializes the sound files, loads the JSON data, creates the music
     * notes, and sets the starting position for the notes. Starts by creating instances
     * of the SoundFile class for the positive and negative music files and then loads
     * them from the specified file path. Then, stores them in the jsonCompanies and
     * companyData variables. After that, creates instances of the musicNote class with
     * specific x and y positions and adds them to the musicNote ArrayList.
     */
    public void setup(){
        // Set the size and background color of the sketch
        size(800, 800);
        background(255);

        // Load sound files
        positiveMusic = new SoundFile(this, "positiveMusic.mp3");
        negativeMusic = new SoundFile(this, "negativeMusic.mp3");

        // Load JSON data
        jsonCompanies = loadJSONObject("dow30_companies.json");
        companyData = loadJSONObject("dow30_quotes.json");

        // Create music notes and add them to the ArrayList
        int l = 100;
        for(int i = 0; i < 15; i+=1){
            companyName = jsonCompanies.getJSONObject("data").getJSONArray("companies").getJSONObject(i).getString("symbol");
            musicNote note = new musicNote(l, 160, companyName, i);
            note.createNote();
            notes.add(note);
            l+=100;
        }

        l = 100;
        for(int i = 15; i < 30; i+=1){
            companyName = jsonCompanies.getJSONObject("data").getJSONArray("companies").getJSONObject(i).getString("symbol");
            musicNote note = new musicNote(l, 650, companyName, i);
            note.createNote();
            notes.add(note);
            l+=100;
        }
    }

    /**
     * Method responsible for updating the music staff and playing the correct sound file
     * based on the positions of the notes on the staff.
     */
    public void draw(){
        // Clear the background
        background(255);
        // Draw the music staff
        createStaff();
        // Loop through the notes ArrayList and update their positions
        for(musicNote note: notes){
            note.update();
            // Check to see if notes are touching; if so move note right by 30
            for (int i = 0; i<notes.size(); i++){
                for (int j = 0; j<notes.size(); j++){
                    if(notes.get(i).xPosition == notes.get(j).xPosition &&
                            notes.get(i).yPosition == notes.get(j).yPosition && i!=j){
                        notes.get(i).xPosition += 50;
                    }
                }
            }
        }
        // Determine which notes are on the staff and perform calculations based on their positions
        musicOptions();
        // Logic for sound file playing, so that there is no overlap of sound files
        if(playSound == true && spaceBar == true){
            if(positive == true && !positiveMusic.isPlaying()){
                negativeMusic.stop();
                positiveMusic.play();
            }else if(positive == false && !negativeMusic.isPlaying()){
                positiveMusic.stop();
                negativeMusic.play();
            }
        }else{
            //Stop any sound files that may be currently playing
            positiveMusic.stop();
            negativeMusic.stop();
        }
    }

    public void createStaff(){
        // Create staff lines
        int j = 250;
        for(int i = 0; i<5; i++){
            stroke(0);
            line(76, j, 1524, j);
            j += 49;
        }
        // Create start and end bar lines
        line(76, 250, 76, 446);
        line(1524, 250, 1524, 446);
        // Text format for time labels
        textSize(10);
        stroke(100);
        // Time labels for y-axis of staff
        text("1W", 33, 253);
        text("|", 33, 273);
        text("|", 33, 283);
        text("1M", 33, 303);
        text("3M", 33, 328);
        text("6M", 33, 353);
        text("YTD", 32, 378);
        text("1Y", 33, 403);
        text("2Y", 32, 428);
        text("3Y", 32, 453);
        // Create amount of shares text for x-axis of staff
        int temp = 0;
        for(int i = 100; i < 1501; i+=140){
            text(i-(temp+100), i, 480);
            temp+=40;
        }
    }

    // index variable is used to store the index of the note in the notes ArrayList
    int index = 0;
    // index_ variable is used to store the index of the time period selected
    int index_ = 0;
    // numberOfShares variable is used to store the number of shares represented by the
    // note's x-coordinate
    int numberOfShares = 1;

    // ArrayList to keep track of the current notes in the staff
    ArrayList<musicNote> notesInStaff = new ArrayList<musicNote>();

    /**
     * Method used to determine which notes are currently on the music staff and calculate
     * the total change amount of the companies represented by these notes. Checks if a note
     * is on the staff and if it is, it adds the note to an ArrayList. Uses if statements to
     * determine the number of shares and the time period represented by the note's position,
     * and retrieves the change amount for the company for the selected time period from the
     * JSON file. Then, multiplies this by the number of shares and adds the result to the
     * total variable. Finally, checks if the total variable is greater or less than zero
     * and sets the positive variable accordingly.
     */
    public void musicOptions(){
        // Reset the playSound variable to false
        playSound = false;
        for(int i = 0; i<notes.size(); i++){
            // Check if the current note is inside the staff
            if(notes.get(i).yPosition < 466 && notes.get(i).yPosition > 237 &&
                    notes.get(i).xPosition >= 98 && notes.get(i).xPosition <= 1502){
                playSound = true;
                index = i;
                // Check if the note is already in the staff
                if(!notesInStaff.contains(notes.get(index))){
                    notesInStaff.add(notes.get(index));
                }
            }
            // Check if the note is outside the staff
            if(notes.get(i).yPosition >= 466 || notes.get(i).yPosition <=
                    237 || notes.get(i).xPosition < 98 || notes.get(i).xPosition > 1502){
                notesInStaff.remove(notes.get(i));
            }
        }
        double total = 0;
        for(int j = 0; j<notesInStaff.size(); j++){
            // Where the note is horizontally correlates to number of shares purchased
            numberOfShares =
                    PApplet.parseInt((notes.get(notesInStaff.get(j).dataIndex).xPosition - 100)/ 1.4f);
            // Ensure that the number of shares is at least 1
            if(numberOfShares<1){
                numberOfShares = 1;
            }

            // Where the note is vertically correlates to the time when the data from the
            // company was collected Since the data is collected from 8 different points
            // in time, these conditions check where the note has been dragged on the axis
            // and sets index_ to the index of that time point in the array
            if(notes.get(notesInStaff.get(j).dataIndex).yPosition >= 237 &&
                    notes.get(notesInStaff.get(j).dataIndex).yPosition < 286){
                index_ = 0;
            }

            if(notes.get(notesInStaff.get(j).dataIndex).yPosition >= 286
                    && notes.get(notesInStaff.get(j).dataIndex).yPosition < 309){
                index_ = 1;
            }

            if(notes.get(notesInStaff.get(j).dataIndex).yPosition >= 309
                    && notes.get(notesInStaff.get(j).dataIndex).yPosition < 340){
                index_ = 2;
            }

            if(notes.get(notesInStaff.get(j).dataIndex).yPosition >= 340
                    && notes.get(notesInStaff.get(j).dataIndex).yPosition < 363){
                index_ = 3;
            }

            if(notes.get(notesInStaff.get(j).dataIndex).yPosition >= 363
                    && notes.get(notesInStaff.get(j).dataIndex).yPosition < 386){
                index_ = 4;
            }

            if(notes.get(notesInStaff.get(j).dataIndex).yPosition >= 386
                    && notes.get(notesInStaff.get(j).dataIndex).yPosition < 410){
                index_ = 5;
            }

            if(notes.get(notesInStaff.get(j).dataIndex).yPosition >= 410
                    && notes.get(notesInStaff.get(j).dataIndex).yPosition < 433){
                index_ = 6;
            }

            if(notes.get(notesInStaff.get(j).dataIndex).yPosition >= 433
                    && notes.get(notesInStaff.get(j).dataIndex).yPosition < 466){
                index_ = 7;
            }
            // Gets the changeAmount data point from the JSON file for the company
            // selected at the index_ value determined by where the note icon is
            // vertically on the staff
            changeAmount =
                    companyData.getJSONObject("data").getJSONArray("quotes").getJSONObject(notesInStaff.get(j).dataIndex).getJSONObject("data").getJSONObject("changeAmount").getFloat(year[index_]);
            // Retrieve the change amount of the company for the selected time period
            // and add it to the total
            total += changeAmount * numberOfShares;
        }

        // Sets the value of the boolean variable positive to indicate which music to play
        if(total>0){
            positive = true;
        }else if(total<0){
            positive = false;
        }
    }

    /**
     * Class for a musicNote object, used as icons to represent individual companies in
     * the Dow Jones
     */
    class musicNote{
        // PShape object to store the shape of the music note
        PShape musicNote;
        // Variables to store the position of the music note
        float xPosition;
        float yPosition;
        // String variable to store the company name
        String companyName;
        // Integer variable to store the data index
        int dataIndex;

        /**
         * Constructor method to initialize the music note's properties
          */
        musicNote(float i, float j, String t, int d){
            xPosition = i;
            yPosition = j;
            companyName = t;
            dataIndex = d;
        }

        /**
         * Creates the shape of the music note
         */
        public void createNote(){
            // Create a group shape to hold the base and stem
            musicNote = createShape(GROUP);
            // Create the base of the music note
            PShape base = createShape(ELLIPSE, 0, 0, 50, 40);
            base.setFill(0);
            base.rotate(2.6f);
            musicNote.addChild(base);

            // Create the stem of the music note
            strokeWeight(6);
            PShape stem = createShape(LINE, 22, 0, 22, -125);
            stem.setStroke(color(0));
            musicNote.addChild(stem);
        }

        /**
         * Updates the position of the music note when it is being dragged by the mouse.
         * Checks if the mouse is pressed and if the distance between the mouse cursor and
         * the center of the music note is less than 30 pixels. If so, it updates the
         * position of the music note to match the position of the mouse cursor. It also
         * draws the shape of the music note at its updated position and adds the company
         * name next to it.
         */
        public void update(){
            // Check if the mouse is pressed and if the distance between the mouse
            // and the music note is less than 30 pixels
            if(mousePressed && dist(mouseX, mouseY, xPosition, yPosition)<30){
                // Update the position of the music note to match the mouse's position
                xPosition = mouseX;
                yPosition = mouseY;
            }
            // Draw the shape of the music note at its updated position
            shape(musicNote, xPosition, yPosition);
            // Set the fill color and text properties
            fill(170);
            textAlign(CENTER);
            textSize(18);
            // Draw the company name next to the music note
            text(companyName, xPosition, yPosition);
        }
    }


    public void settings() { size(1600, 800); }

    static public void main(String[] passedArgs) {
        String[] appletArgs = new String[] { "Music_Note_Sketch" };
        if (passedArgs != null) {
            PApplet.main(concat(appletArgs, passedArgs));
        } else {
            PApplet.main(appletArgs);
        }
    }
}
