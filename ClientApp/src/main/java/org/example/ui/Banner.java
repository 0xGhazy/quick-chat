package org.example.ui;

public class Banner {

    public static void printMainBanner()
    {
        ColoredTerminal c = new ColoredTerminal();
        System.out.println(c.colored("\n\t\t888b     d888 d8b          d8b      8888888b.  d8b                                       888", COLOR.BLUE));
        System.out.println(c.colored("\t\t8888b   d8888 Y8P          Y8P      888  \"Y88b Y8P                                       888", COLOR.BLUE));
        System.out.println(c.colored("\t\t88888b.d88888                       888    888                                           888", COLOR.BLUE));
        System.out.println(c.colored("\t\t888Y88888P888 888 88888b.  888      888    888 888 .d8888b   .d8888b .d88b.  888d888 .d88888", COLOR.BLUE));
        System.out.println(c.colored("\t\t888 Y888P 888 888 888 \"88b 888      888    888 888 88K      d88P\"   d88\"\"88b 888P\"  d88\" 888 ", COLOR.BLUE));
        System.out.println(c.colored("\t\t888  Y8P  888 888 888  888 888      888    888 888 \"Y8888b. 888     888  888 888    888  888 ", COLOR.BLUE));
        System.out.println(c.colored("\t\t888   \"   888 888 888  888 888      888  .d88P 888      X88 Y88b.   Y88..88P 888    Y88b 888 ", COLOR.BLUE));
        System.out.println(c.colored("\t\t888       888 888 888  888 888      8888888P\"  888  88888P'  \"Y8888P \"Y88P\"  888     \"Y88888 \n", COLOR.BLUE));
    }
}
