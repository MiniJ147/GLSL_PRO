package gfx;

import java.awt.*;
import java.util.LinkedList;

import static utils.Utils.println;

class Types{
    public static String types[] = {"float", "int", "bool", "struct",
                                    "vec2","vec3","vec4", "mat2", "mat3", "mat4"};
    public static LinkedList<String> user_types = new LinkedList<>();
}

class Line{
    private String types[] = Types.types;

    private String line;
    private StringBuilder color_line_type,color_line_user;
    public int x, y;

    public Line(String line, int x, int y){
        setLine(line);
        this.x=x;
        this.y=y;
    }

    public void render(Graphics g){
        g.drawString(line,x,y);
        g.setColor(Color.cyan);
        if(color_line_type!=null)
            g.drawString(color_line_type.toString(),x,y);

        g.setColor(Color.yellow);
        if(color_line_user!=null)
            g.drawString(color_line_user.toString(),x,y);

        g.setColor(Color.white);
    }

    String getLine(){
        return line;
    }

    void setLine(String str){
        println("Setting Line");
        LinkedList<String> user_types = Types.user_types;
        line = str;

        String __t = "";
        for(char c : str.toCharArray()){
            __t+=" ";
        }

        color_line_type = new StringBuilder(__t);
        color_line_user = new StringBuilder(__t);

        //checking basic types
        for(int t=0; t<types.length;t++) {
            String curr_str = types[t];
            if(line.contains(curr_str)) {
                for (int i = 0; i < line.length(); i++) {
                    int adjusted_type_length = i + curr_str.length();

                    if (line.charAt(i) != curr_str.charAt(0) || adjusted_type_length > line.length())
                        continue;

                    if (!line.substring(i, adjusted_type_length).contains(curr_str))
                        continue;

                    boolean run = false;

                    char a = '\0';
                    if (line.length() > adjusted_type_length) {
                        a = line.charAt(adjusted_type_length);
                        if (a == ' ' || a == '(') {
                            run = true;
                        }
                    } else
                        run = true;

                    if (!run)
                        continue;

                    //[TODO]Jake Optimize code if it becomes a problem
                    //checking user_types [ROOM FOR OPTIMIZATION]
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
                    color_line_type.delete(i,curr_str.length()+i);
                    color_line_type.insert(i, curr_str);
                    println(color_line_type.toString());
                }
            }
        }

        //checking the user created types
        for(String s : user_types){
            if(!line.contains(s))
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
                    color_line_user.delete(i,s.length()+i);
                    color_line_user.insert(i, s);
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

            if(render_lines.size()<=i || render_lines.get(i)==null){
                render_lines.add(new Line(src_line,x,y));
            }
            Line curr_line = render_lines.get(i);

            if(curr_line.getLine().length()!=src_line.length() || curr_line.getLine().compareTo(src_line)>0)
                curr_line.setLine(src_line);

            curr_line.y = y;
            curr_line.render(g);

            //rendering line number
            g.drawString("["+(i+1)+"]",4,y);
            i++;
        }
    }
}
