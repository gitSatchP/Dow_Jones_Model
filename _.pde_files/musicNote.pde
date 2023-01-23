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
  void createNote(){
    // Create a group shape to hold the base and stem
    musicNote = createShape(GROUP);
    // Create the base of the music note
    PShape base = createShape(ELLIPSE, 0, 0, 50, 40);
    base.setFill(0);
    base.rotate(2.6);
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
  void update(){
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
