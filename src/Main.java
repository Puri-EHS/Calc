import java.util.Scanner;

class Main {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        for (boolean done = false; !done && input.hasNext(); ) {
            String line = input.nextLine();
            Ast ast = new Parser(line).parse();
            String output = ast == null? "Parse error" : ast.toString();
            System.out.println(output);
        }
        input.close();
    }
}