"# My notes" 

chapter 1
    - java methods are functions declared in a class - unless the method is static, you call it on an object of that class
    - program execution starts with static main method
    - eight primitive types, including char, bool, and lots of number types
    - similar to C and JS
    - four types of switch expressions
    - Math class
    - String is not primitive
    - multiline string literals
    - System.out object displays, and use a scanner with System.in to read input
    - arrays & collections

    public class ClassName {
        public static void main(String[] args) {
            //general method structure for main
        }
    }

    - classes and methods are generally public in java
    - package: set of related classes - group related classes in a file
    - comment with // or /* */ for multiline

    - you need a compiler to run
    javac program_path.java //compile
    java program_name   //run

    - new is a keyword that constructs a new object instance, or use factory method as an alternative

    - jshell allows you to try code without compiling and running the program. run by typing jshell in a terminal window (type an expression and it returns)
    - shift + tab + v if you need it to fill in the type for you
    - hit tab to pull up list of potential methods you can invoke 


    types:

    INTEGERS
    - byte : 1 byte, -128 to 127
    - short, 2 bytes, -32,768 to 32,767
    - int, 4 bytes +- 2 billion
    - long, 8 bytes, very long number      //written followed by an L

    0x prefix for hexadecimal, 0b prefix for binary
    if you need commas for visual help, use underscores, compiler ignores them
    ex. 1_000_000 for 1 million

    FLOATS
    - float, 4 bytes, about 6 to 7 decimal places
    - double, 8 bytes, about 15 decimal digits
    double is the standard nowadays
    float has suffix F, if it doesn't have a suffix it's a double
    Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NaN
    - foundoff errors


    CHAR
    - single quotes, single caracter
    - can use hexadecimal here to get other unicode symbols too
    \n, \r, \t, \b
    \ to escape ' or backslash


    BOOLEAN
    - false or true, no 0/1 equivalent


    variables:
    - strongly typed
    - if declaring an object, can use var keyword to avoid repetition of className

    identifiers are the names of variables, methods, or class
    they need to begin with a letter followed by any letters, digits, most symbols, but don't use $
    case sensitive

    class names uppercase first letter, var/methods lowercase first letter (just convention)
    java likes camel case

    "final" keyword denotes a value that cannot be changed once assigned (constant)
    use all uppercase for these

    use enumeration to assign a series of related constants
    enum Weekday {MONDAY, TUESDAY, WEDNESDAY}; defines MONDAY = 0, TUESDAY = 1, etc
    Weekday is now a type

    normal operators
    ^ for xor
    ?: for conditional
    you may assign during another expression ex. (var++)==-1 but different depending if you use var++ or ++var


    integer division truncates if you don't declare as float
    dividing int by zero gives exception, dividing float by zero gives infinite value

    Math.pow, Math.sqrt, Math.min, Math.max, Math.PI, Math.E, etc, static methods
    use methods like Math.multiplyExact(a,b) to safeguard against roundoff errors etc

    numbers are converted to a common type before operated on

    (int) x for casting
    Math.round() if you want to actually round instead

    ==, >=, etc to test for equality among primitive types
    time < 12 ? "am" : "pm" operator

    there are ways to use bigger numbers

    STRINGS
    - + to concatenate
    concatenating with a different type turns both into strings
    String.join() to combine strings with a delimiter or String.split() to separate
    StringBuilder could be more efficient
    substring method
    use .equals() not == for strings, unless you are checking memory location or if null
    or equalsIgnoreCase()

    can compare unicode values
    
    Integer.toString(n) and Integer.parseInt(str)

    lots of other string methods

    Java strings are immutable


    triple quotes for block strings """ can be multiline """


    IN/OUT
    var in = new Scanner(System.in);
    String name = in.nextLine();
     need import java.util.Scanner
     to read a password, don't use Scanner since it's visible in terminal, use Console class
    
    java mypackage.MainClass < input.txt > output.txt
    now System.in reads from input.txt and System.out writes to output.txt
    printf to format to a certain number of decimal places - lots of different formatting tags


    OTHER
    - switch statements
    - while, do-while
    - for
    break and continue (break jumps out of the loop, continue jumps to end of that iteration)
    can break out of multiple loops if loops are labeled


    SCOPE
    - local in the loop/block if declared in
    - scope of a parameter is the whole method
    - no local variables with same name if scopes overlap


    ARRAYS
    
    Array like String[] - initialized with one size, and can't try to access an element that doesn't exist
    array constructed with "new" is autofilled with zeroes, false, or null

    if you don't know the length or it might change, use ArrayList
    ArrayList<String>
    .add()
    can add and remove anywhere in arraylist
    .get() or .set(), not []
    can't use primitive like int, must use wrapper class like Integer

    must copy an array/arraylist if you want to set it to the same thing, or else it will be a reference and edit both

    can overload methods like you do with constructors




