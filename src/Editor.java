import gfx.Display;
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

enum background_color{
    BLACK,
    GREY,
    WHITE,
    LIGHT
}

public class Editor implements ActionListener {
    //UI Stuff
    private final JMenuItem menu_load_file, menu_save_file;
    private final JMenuItem menu_set_background_color_black,
                      menu_set_background_color_grey,
                      menu_set_background_color_white,
                      menu_set_background_color_light;
    private final int font_size = 24;
    private final Font font = new Font("Monospaced",Font.PLAIN,font_size);


    //file stuff
    private  File file_selected;
    private String source_line = "";

    private boolean running = false;

    //import classes
    private Display display;
    private MouseManager mouse_manager;
    private KeyManager key_manager;

    //background color
    private background_color theme_chosen = background_color.BLACK;
    private Color theme_color,font_color;

    //gfx stuff
    private BufferStrategy gfx_bs;
    private Graphics gfx_g;
    private int window_width = 800;
    private int window_height = 800;

    //string and line building [THE MEAT]
    private LinkedList<String> lines;
    private int lines_index = 0;
    private int lines_current_char_selected = 0;
    private StringBuilder lines_current;

    public Editor(){
        //random
        String version = "Version[0.61]";
        display = new Display(window_width,window_height,"GLSL PRO  "+ version);

        display.getFrame().setLayout(new FlowLayout());

        //creating our menu bar stuff
        JMenuBar menu_bar = new JMenuBar();

        //adding file settings
        JMenu fileMenu = new JMenu("File");
        menu_load_file = new JMenuItem("Load");
        menu_save_file = new JMenuItem("Save");
        menu_load_file.addActionListener(this);
        menu_save_file.addActionListener(this);
        fileMenu.add(menu_load_file);
        fileMenu.add(menu_save_file);
        menu_bar.add(fileMenu);

        //adding theme
        JMenu themeMenu = new JMenu("Theme");
        menu_set_background_color_black = new JMenuItem("Background Black");
        menu_set_background_color_white = new JMenuItem("Background White");
        menu_set_background_color_grey = new JMenuItem("Background Grey");
        menu_set_background_color_light = new JMenuItem("Background Light");

        menu_set_background_color_light.addActionListener(this);
        menu_set_background_color_grey.addActionListener(this);
        menu_set_background_color_black.addActionListener(this);
        menu_set_background_color_white.addActionListener(this);

        themeMenu.add(menu_set_background_color_light);
        themeMenu.add(menu_set_background_color_grey);
        themeMenu.add(menu_set_background_color_white);
        themeMenu.add(menu_set_background_color_black);
        menu_bar.add(themeMenu);

        mouse_manager = new MouseManager();
        display.getCanvas().addMouseWheelListener(mouse_manager);
        display.getFrame().addMouseWheelListener(mouse_manager);

        key_manager = new KeyManager();
        display.getFrame().addKeyListener(key_manager);
        display.getCanvas().addKeyListener(key_manager);

        display.getFrame().setJMenuBar(menu_bar);
        display.getFrame().setVisible(true);

        lines = new LinkedList<>();
        lines.add("\n");
        lines_current = new StringBuilder(lines.get(lines_index));

        running = true;
    }

    public void run(){

        while(running){
            //checking resize=====///
            if(window_width!=display.getFrame().getWidth() || window_height!=display.getFrame().getHeight()) {
                window_width = display.getFrame().getWidth();
                window_height = display.getFrame().getHeight();

                display.getCanvas().setSize(new Dimension(window_width,window_height));

                println(""+display.getCanvas().getWidth());
            }
            //====================//

            //if we are ready to add a new char run this code
            if(key_manager.isKey_ready_to_add()){
                lines_current = new StringBuilder(lines.get(lines_index));
                int key_code = key_manager.getKey_current_pressed();

                Utils.print(""+key_code+",");

                boolean type_to_string = true;
                //checking ot see if we pressed a blacklisted key
                for(int _blackedKey : key_manager.getKey_blacklist()){
                    if (key_code == _blackedKey) {
                        type_to_string = false;
                        break;
                    }
                }

                if(type_to_string){
                    boolean shift_held = key_manager.isShift_held();

                    switch(key_code){
                        //checking special keys
                        case 8://backspace
                           if(lines_current.isEmpty() || lines_current_char_selected == 0){
                               if(!lines.get(lines_index).isEmpty() && lines_index-1>=0){
                                   String line = lines.get(lines_index-1);
                                   String line_new = line.substring(0,line.length()-1) + lines_current.toString();

                                   lines_current_char_selected = line.length()-1;

                                   lines.set(lines_index-1,line_new);
                                   lines.remove(lines_index);



                                   lines_index-=1;
                               }
                               break;
                           }

                           lines_current.deleteCharAt(lines_current_char_selected-1);
                           lines_current_char_selected-=1;
                           key_manager.setKey_ready_to_add(false);

                            line_correct_edit();
                            break;
                        case 10: //enter key
                            String str = lines_current.substring(lines_current_char_selected,lines_current.length());
                            lines_current.delete(lines_current_char_selected,lines_current.length());
                            lines_current.append('\n');

                            line_correct_edit();

                            lines_index+=1;
                            lines.add(lines_index, str);
                            lines_current_char_selected=0;
                            break;
                        case 37: //left key
                            if(lines_current_char_selected<=0)
                                break;

                            lines_current_char_selected--;
                            break;
                        case 40: //down key
                            if(lines.size()<=lines_index+1)
                                break;

                            lines_index+=1;
                            lines_current = new StringBuilder(lines.get(lines_index));
                            lines_current_char_selected = lines_current.length()-1;
                            break;
                        case 39: //right key
                            if(lines_current_char_selected+1>=lines_current.length())
                                break;

                            lines_current_char_selected++;
                            break;
                        case 38: //up key
                            if(lines_index<=0)
                                break;

                            lines_index-=1;
                            lines_current = new StringBuilder(lines.get(lines_index));
                            lines_current_char_selected = lines_current.length()-1;

                            line_correct_edit();
                            break;
                        default: //now after all the special keys are out of the way we can add the char to the string
                            char a = (char)key_code;

                            //checking the special cases when shift is being held
                            if(shift_held) {
                                int[] shift_special_key_codes = {49, 50, 51, 52, 53, 54, 55, 56, 57, 48, 45, 61,
                                                               91,93,59,222,92,44,46,47};
                                String shift_special_chars = "!@#$%^&*()_+{}:\"\\<>?";

                                //checking if our key code matches are special shift key codes
                                for(int i=0; i<shift_special_key_codes.length;i++){
                                    if(key_code==shift_special_key_codes[i]){
                                        a = shift_special_chars.charAt(i);
                                        break;
                                    }
                                }
                            }

                            if(key_code==222)
                                a = '\'';

                            //checking if we should capitalize it
                            if(key_manager.isKey_cap() || shift_held)
                                a = Character.toUpperCase(a);
                           else
                                a = Character.toLowerCase(a);

                           //checking special cases with chars
                           switch(a){
                               case '(': // add on to the right
                                   lines_current.insert(lines_current_char_selected,a);
                                   lines_current_char_selected++;

                                   lines_current.insert(lines_current_char_selected,')');
                                   break;
                               case '{':
                                   lines_current.insert(lines_current_char_selected,a);
                                   lines_current_char_selected++;

                                   lines_current.insert(lines_current_char_selected,'}');
                                   break;
                               case ')':
                                   line_check_char_right(')');
                                   break;
                               case '}':
                                   line_check_char_right('}');
                                   break;
                               default: //if we don't see any of these special cases then we can just add our char
                                   lines_current.insert(lines_current_char_selected,a);
                                   lines_current_char_selected++;
                                   break;
                           }

                            line_correct_edit();
                            break;
                    }
                }
                key_manager.setKey_ready_to_add(false);
            }
            render();
        }
    }

    private void render() {
        //adjusting the colors to the theme we have selected
        switch(theme_chosen){
            case BLACK:
                theme_color = new Color(38,38,38);
                font_color = new Color(236,236,236);
                break;
            case GREY:
                theme_color = new Color(76, 75, 75);
                font_color = new Color(135, 207, 255);
                break;
            case WHITE:
                theme_color = new Color(255, 255, 255);
                font_color = new Color(0, 0, 0);
                break;
            case LIGHT:
                theme_color = new Color(208, 207, 207);
                font_color = new Color(0, 0, 0);
                break;
        }
        gfx_bs = display.getCanvas().getBufferStrategy();

        if(gfx_bs==null){
            display.getCanvas().createBufferStrategy(3);
            return;
        }
        gfx_g = gfx_bs.getDrawGraphics();
        gfx_g.setColor(theme_color);
        gfx_g.fillRect(0,0,window_width,window_height);

        //draw
        gfx_g.setColor(Color.white);;

        int y_offset = ((33 * lines_index)+22)-(mouse_manager.getMouse_scroll_offset()* mouse_manager.getMouse_scroll_speed());

        gfx_g.fillRect((lines_current_char_selected) * (font_size-10) + 64,y_offset,font_size/8,18);

        gfx_g.setColor(font_color);

        source_line = "";

        for(String s : lines){
            source_line += s;
        }

        gfx_g.setFont(font);
        RenderFunctions.drawString(gfx_g,source_line,64,-1*(mouse_manager.getMouse_scroll_offset()* mouse_manager.getMouse_scroll_speed()));

        //stop
        gfx_bs.show();
        gfx_g.dispose();
    }

    //if we are either at the end of the line or the char to the right of us is not (a) then add (a) to the right
    private void line_check_char_right(char a){
        if(lines_current.length()-1==lines_current_char_selected ||
                lines_current.length()-1>lines_current_char_selected && lines_current.charAt(lines_current_char_selected)!=a){

            lines_current.insert(lines_current_char_selected,a);
            lines_current_char_selected++;
            return;
        }
        lines_current_char_selected++;
    }

    //replaces current line in our list
    private void line_correct_edit(){
        lines.remove(lines_index);
        lines.add(lines_index, lines_current.toString());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==menu_load_file){
            println("Loaded From File!");
            JFileChooser file_chooser = new JFileChooser();

            //[TODO] When shipping change this back
            file_chooser.setCurrentDirectory(new File("C:\\Users\\Jake Paul\\Desktop"));

            int response = file_chooser.showOpenDialog(null); // select file to open

            if(response==JFileChooser.APPROVE_OPTION){
                file_selected = new File(file_chooser.getSelectedFile().getAbsolutePath());
                println(file_selected.getAbsolutePath());

                try {
                    Scanner myReader = new Scanner(file_selected);
                    while (myReader.hasNextLine()) {
                        lines.add(myReader.nextLine()+'\n');
                    }

                    println(source_line);
                    myReader.close();
                } catch (FileNotFoundException j) {
                    System.out.println("An error occurred.");
                    j.printStackTrace();
                }
            }
        }
        if(e.getSource()==menu_save_file){
            println("Saved to File!");
            println(""+source_line);
        }

        if(e.getSource()==menu_set_background_color_black)
            theme_chosen = background_color.BLACK;

        if(e.getSource()==menu_set_background_color_white)
            theme_chosen = background_color.WHITE;

        if(e.getSource()==menu_set_background_color_grey)
            theme_chosen = background_color.GREY;

        if(e.getSource()==menu_set_background_color_light)
            theme_chosen = background_color.LIGHT;
    }
}
