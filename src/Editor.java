import gfx.Display;
import gfx.FontLoader;
import gfx.RenderFunctions;
import input.KeyManager;
import input.MouseManager;
import utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferStrategy;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Scanner;

import static utils.Utils.println;

enum backGroundColor{
    BLACK,
    GREY,
    WHITE,
    LIGHT
}

public class Editor implements ActionListener {
    //random
    private final String version = "Version[0.05]";
    //UI Stuff
    private JMenuItem loadFile, saveFile;
    private JMenuItem incFontSize, decFontSize, loadFont;
    private JMenuItem setBackgroundColorBlack,
                      setBackgroundColorGrey,
                      setBackgroundColorWhite,
                      setBackgroundColorLight;
    private Font font;
    private int fontSize = 24;
    private String fontPath = "";

    //file stuff
    private File selectedFile;
    private String sourceLine = "";

    private boolean running = false;

    //import classes
    private Display display;
    private MouseManager mouseManager;
    private KeyManager keyManager;

    //background color
    private backGroundColor chosenTheme = backGroundColor.BLACK;
    private Color themeColor,fontColor;

    //gfx stuff
    private BufferStrategy bs;
    private Graphics g;
    private int width = 800;
    private int height = 800;

    //string and line building [THE MEAT]
    private LinkedList<String> lines;
    private int lineIndex = 0;
    private int currentCharSelected = 0;
    private int currentCharIndex = currentCharSelected-1;
    private StringBuilder currentLine;

    private String test = "";

    public Editor(){
        display = new Display(width,height,"GLSL PRO  "+version);

        display.getFrame().setLayout(new FlowLayout());

        //creating our menu bar stuff
        JMenuBar menuBar = new JMenuBar();

        //adding file settings
        JMenu fileMenu = new JMenu("File");
        loadFile = new JMenuItem("Load");
        saveFile = new JMenuItem("Save");
        loadFile.addActionListener(this);
        saveFile.addActionListener(this);
        fileMenu.add(loadFile);
        fileMenu.add(saveFile);
        menuBar.add(fileMenu);

        //font settings
        JMenu  fontMenu = new JMenu("Font");
        incFontSize = new JMenuItem("Increase Font Size");
        decFontSize = new JMenuItem("Decrease Font Size");
        loadFont = new JMenuItem("Load Font");
        incFontSize.addActionListener(this);
        decFontSize.addActionListener(this);
        loadFont.addActionListener(this);
        fontMenu.add(incFontSize);
        fontMenu.add(decFontSize);
        fontMenu.add(loadFont);
        menuBar.add(fontMenu);

        //adding theme
        JMenu themeMenu = new JMenu("Theme");
        setBackgroundColorBlack = new JMenuItem("Background Black");
        setBackgroundColorWhite = new JMenuItem("Background White");
        setBackgroundColorGrey = new JMenuItem("Background Grey");
        setBackgroundColorLight = new JMenuItem("Background Light");

        setBackgroundColorLight.addActionListener(this);
        setBackgroundColorGrey.addActionListener(this);
        setBackgroundColorBlack.addActionListener(this);
        setBackgroundColorWhite.addActionListener(this);

        themeMenu.add(setBackgroundColorLight);
        themeMenu.add(setBackgroundColorGrey);
        themeMenu.add(setBackgroundColorWhite);
        themeMenu.add(setBackgroundColorBlack);
        menuBar.add(themeMenu);

        mouseManager = new MouseManager();
        display.getCanvas().addMouseWheelListener(mouseManager);
        display.getFrame().addMouseWheelListener(mouseManager);

        keyManager = new KeyManager();
        display.getFrame().addKeyListener(keyManager);
        display.getCanvas().addKeyListener(keyManager);

        display.getFrame().setJMenuBar(menuBar);
        display.getFrame().setVisible(true);

        lines = new LinkedList<>();
        lines.add("\n");
        currentLine = new StringBuilder(lines.get(lineIndex));

        running = true;
    }

    public void run(){

        while(running){
            //checking resize
            if(width!=display.getFrame().getWidth() || height!=display.getFrame().getHeight()) {
                width = display.getFrame().getWidth();
                height = display.getFrame().getHeight();

                display.getCanvas().setSize(new Dimension(width,height));

                println(""+display.getCanvas().getWidth());
            }
            //====================//
            if(keyManager.isReadyToAdd()){
                currentLine = new StringBuilder(lines.get(lineIndex));
                int keyCode = keyManager.getCurrentKeyPressed();

                Utils.print(""+keyCode+",");

                boolean type = true;
                for(int _blackedKey : keyManager.getBlackListedKeys()){
                    if(keyCode==_blackedKey){
                        type=false;
                    }
                }
                if(type){
                    boolean shiftHeld = keyManager.isShift_held();

                    switch(keyCode){
                        case 8://backspace
                           if(currentLine.isEmpty() || currentCharSelected == 0){
                               println("Line is Empty");
                               break;
                           }

                            /*currentLine.delete(currentCharSelected,currentLine.length()+currentCharSelected-1);
                            currentCharSelected--;
                            println("Cur:"+currentCharSelected); */
                            currentLine.deleteCharAt(currentCharSelected-1);
                            currentCharSelected-=1;
                            println("Cur:"+currentCharSelected+"| "+currentCharIndex+"|"+currentLine.length());
                           keyManager.setReadyToAdd(false);

                            correctEditToLine();
                            break;
                        case 10: //enter key
                            lineIndex+=1;
                            if(lineIndex>=lines.size()) {
                                lines.add(lineIndex, "\n");
                                currentCharSelected=0;
                            } else{
                                if(currentCharSelected>lines.get(lineIndex).length())
                                    currentCharSelected = lines.get(lineIndex).length();
                            }

                            println("Cur:"+currentCharSelected);
                            break;
                        case 37: //left key
                            if(currentCharSelected<=0){
                                println("at the end of line");
                                break;
                            }

                            currentCharSelected--;
                            currentCharIndex = currentCharSelected-1;
                            break;
                        case 40: //down key
                            if(lines.size()<=lineIndex+1){
                                println("No line there");
                                break;
                            }

                            lineIndex+=1;
                            currentLine = new StringBuilder(lines.get(lineIndex));
                            println(""+currentLine);
                            currentCharSelected = currentLine.length()-1;

                            println("Cur:"+currentCharSelected);
                            println(""+currentLine.toString()+" "+currentLine.length());
                            break;
                        case 39: //right key
                            if(currentCharSelected+1>=currentLine.length()){
                                println("at the end right of line");
                                break;
                            }

                            currentCharSelected++;
                            currentCharIndex = currentCharSelected+1;
                            break;
                        case 38: //up key
                            if(lineIndex<=0)
                                break;

                            lineIndex-=1;
                            currentLine = new StringBuilder(lines.get(lineIndex));
                            currentCharSelected = currentLine.length()-1;

                            correctEditToLine();

                            println("Cur:"+currentCharSelected);
                            println(""+currentLine.toString()+" "+currentLine.length());
                            break;
                        default:
                            char a = (char)keyCode;

                            if(shiftHeld) {
                                int[] specialShiftKeysCodes = {49, 50, 51, 52, 53, 54, 55, 56, 57, 48, 45, 61,
                                                               91,93,59,222,92,44,46,47};
                                String specialShiftKeys = "!@#$%^&*()_+{}:\"\\<>?";

                                for(int i=0; i<specialShiftKeysCodes.length;i++){
                                    if(keyCode==specialShiftKeysCodes[i]){
                                        a = specialShiftKeys.charAt(i);
                                        break;
                                    }
                                }
                            }

                            if(keyCode==222)
                                a = '\'';

                            if(keyManager.isCap() || shiftHeld)
                                currentLine.insert(currentCharSelected,Character.toUpperCase(a));
                           else
                                currentLine.insert(currentCharSelected,Character.toLowerCase(a));

                            currentCharSelected++;
                            currentCharIndex = currentCharSelected-1;
                            println("Cur:"+currentCharSelected+"| "+currentCharIndex+"|"+currentLine.length());
                            correctEditToLine();

                            break;
                    }
                }
                keyManager.setReadyToAdd(false);
            }
            render();
        }
    }

    private void render() {
        switch(chosenTheme){
            case BLACK:
                themeColor = new Color(38,38,38);
                fontColor = new Color(236,236,236);
                break;
            case GREY:
                themeColor = new Color(76, 75, 75);
                fontColor = new Color(135, 207, 255);
                break;
            case WHITE:
                themeColor = new Color(255, 255, 255);
                fontColor = new Color(0, 0, 0);
                break;
            case LIGHT:
                themeColor = new Color(208, 207, 207);
                fontColor = new Color(0, 0, 0);
                break;
        }
        bs = display.getCanvas().getBufferStrategy();

        if(bs==null){
            display.getCanvas().createBufferStrategy(3);
            return;
        }
        g = bs.getDrawGraphics();
        g.setColor(themeColor);
        g.fillRect(0,0,width,height);
        //draw
        if(font!=null)
            g.setFont(font);
        else
            g.setFont(new Font("Arial",Font.PLAIN,fontSize));

        g.setColor(Color.white);
        g.fillRect((currentLine.length()-1)*(fontSize/2)+12,10+lineIndex*fontSize,fontSize/4,fontSize+10);

        g.setColor(fontColor);

        sourceLine = "";

        for(String s : lines){
            sourceLine += s;
        }

        RenderFunctions.drawString(g,sourceLine,12,10-(mouseManager.getMouse_scroll_offset()* mouseManager.getMouse_scroll_speed()));

        //stop
        bs.show();
        g.dispose();
    }

    private void correctEditToLine(){
        lines.remove(lineIndex);
        lines.add(lineIndex, currentLine.toString());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==loadFile){
            println("Loaded From File!");
            JFileChooser fileChooser = new JFileChooser();

            //[TODO] When shipping change this back
            fileChooser.setCurrentDirectory(new File("C:\\Users\\Jake Paul\\Desktop"));

            int response = fileChooser.showOpenDialog(null); // select file to open

            if(response==JFileChooser.APPROVE_OPTION){
                selectedFile = new File(fileChooser.getSelectedFile().getAbsolutePath());
                println(selectedFile.getAbsolutePath());

                try {
                    Scanner myReader = new Scanner(selectedFile);
                    while (myReader.hasNextLine()) {
                        lines.add(myReader.nextLine()+'\n');
                        //sourceLine =sourceLine + myReader.nextLine() + '\n';
                    }

                    println(sourceLine);
                    myReader.close();
                } catch (FileNotFoundException j) {
                    System.out.println("An error occurred.");
                    j.printStackTrace();
                }
            }
        }
        if(e.getSource()==saveFile){
            println("Saved to File!");
        }

        if(e.getSource()==setBackgroundColorBlack)
            chosenTheme = backGroundColor.BLACK;

        if(e.getSource()==setBackgroundColorWhite)
            chosenTheme = backGroundColor.WHITE;

        if(e.getSource()==setBackgroundColorGrey)
            chosenTheme = backGroundColor.GREY;

        if(e.getSource()==setBackgroundColorLight)
            chosenTheme = backGroundColor.LIGHT;

        if(e.getSource()==decFontSize){
            fontSize-=4;

            if(fontPath=="")
                return;
            font = FontLoader.loadFont(fontPath,fontSize);
        }
        if(e.getSource()==incFontSize){
            fontSize+=4;

            if(fontPath=="")
                return;
            font = FontLoader.loadFont(fontPath,fontSize);
        }

        if(e.getSource()==loadFont){
            JFileChooser fileChooser = new JFileChooser();

            //[TODO] When shipping change this back
            fileChooser.setCurrentDirectory(new File("C:\\Users\\Jake Paul\\Desktop"));

            int response = fileChooser.showOpenDialog(null); // select file to open

            if(response==JFileChooser.APPROVE_OPTION) {
                File file = new File(fileChooser.getSelectedFile().getAbsolutePath());
                fontPath = file.getAbsolutePath();
                println(file.getAbsolutePath());
                font = FontLoader.loadFont(file.getAbsolutePath(),fontSize);
            }
        }
    }
}
