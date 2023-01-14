package gfx;

import java.awt.*;
import java.util.LinkedList;

import static utils.Utils.println;

class Types{
    //default data types in glsl
    public static String types_default[] = {"float", "int", "bool", "struct",
                                    "vec2","vec3","vec4", "mat2", "mat3", "mat4"};
    //where we are going to place user types
    public static LinkedList<String> types_user = new LinkedList<>();
}

class Line{
    private String types[] = Types.types_default;

    private String line;
    private StringBuilder line_color_default,line_color_user;
    public int x, y;

    public Line(String line, int x, int y){
        set_line(line);
        this.x=x;
        this.y=y;
    }

    public void render(Graphics g){
        //drawing default line
        g.drawString(line,x,y);

        //drawing default data types
        g.setColor(Color.cyan);
        if(line_color_default!=null)
            g.drawString(line_color_default.toString(),x,y);

        //drawing user types
        g.setColor(Color.yellow);
        if(line_color_user!=null)
            g.drawString(line_color_user.toString(),x,y);

        //resetting color
        g.setColor(Color.white);
    }

    String get_line(){
        return line;
    }

    void set_line(String str){
        LinkedList<String> user_types = Types.types_user;
        line = str;

        //creating an empty string of spaces to the length of our current line so we can use String.insert() better==|
        String __t = "";
        for(char c : str.toCharArray()){
            __t+=" ";
        }
        //===================================|

        line_color_default = new StringBuilder(__t);
        line_color_user = new StringBuilder(__t);

        //checking basic types
        for(int  t=0; t<types.length;t++) {
            String curr_str = types[t];

            //if we do not have this data type then move to the next one
            if(!line.contains(curr_str))
                continue;

            //else we loop through the line and try and find the index that we have our data type
            for (int i = 0; i < line.length(); i++) {
                int adjusted_type_length = i + curr_str.length();
                //if current char does not equal the first char of our type or if our data types length is greater than
                //current placement + its size then continue
                if (line.charAt(i) != curr_str.charAt(0) || adjusted_type_length > line.length())
                    continue;

                //if the section we take out does not match to the data type then continue
                if (!line.substring(i, adjusted_type_length).contains(curr_str))
                    continue;

                boolean run = false;

                char a = '\0';
                //if we know a char is to our right then we have to check if it equals a space or a '('
                //if it doesn't then we know that we do not have a match and should not run the coloring part
                if (line.length() > adjusted_type_length) {
                    a = line.charAt(adjusted_type_length);
                    if (a == ' ' || a == '(')
                        run = true;
                } else
                    run = true;
                //=====================|

                if (!run)
                    continue;

                //checking user_types
                if(a == ' '){
                    StringBuilder new_user_type = new StringBuilder("");
                    for(int j=adjusted_type_length+1; j<line.length();j++){
                        char b = line.charAt(j);
                        if(b==' ' || b==';' || b=='.') {
                            if(!user_types.contains(new_user_type.toString())) {
                                user_types.add(new_user_type.toString());
                            }
                            break;
                        }
                        new_user_type.append(line.charAt(j));
                    }
                }

                //adding color
                line_color_default.delete(i,curr_str.length()+i);
                line_color_default.insert(i, curr_str);
            }
        }

        //checking the user created types
        for(String s : user_types) {
            if (!line.contains(s))
                continue;

            for(int i=0;i<line.length();i++){
                char c = line.charAt(i);
                int adjust_type_length = i+s.length();

                if(s.charAt(0)!=c || adjust_type_length >= line.length())
                    continue;

                String check_line = line.substring(i,adjust_type_length);

                if(!check_line.contains(s))
                    continue;

                char a = line.charAt(adjust_type_length);

                if(a==' ' || a==';' || a==',' || a=='.' || a==')' || a=='('){
                    println(""+line.charAt(adjust_type_length));

                    println("We found a match: "+check_line);
                    line_color_user.delete(i,s.length()+i);
                    line_color_user.insert(i, s);
                }
            }
        }
    }
};

public abstract class RenderFunctions {

    private static LinkedList<Line> render_lines = new LinkedList<>();

    public static void drawString(Graphics g, String text, int x, int y){
        int lineHeight = g.getFontMetrics().getHeight();
        int i = 0;

        for (String src_line : text.split("\n")) {
            y+=lineHeight;

            //if this line does not exist then we have to add a new line to our list
            if(render_lines.size()<=i || render_lines.get(i)==null)
                render_lines.add(new Line(src_line,x,y));

            Line curr_line = render_lines.get(i);

            //if we have a change then we can add it to the line list
            if(curr_line.get_line().length()!=src_line.length() || curr_line.get_line().compareTo(src_line)>0)
                curr_line.set_line(src_line);

            curr_line.y = y;
            curr_line.render(g);

            //rendering line number
            g.drawString("["+(i+1)+"]",4,y);
            i++;
        }
    }
}
