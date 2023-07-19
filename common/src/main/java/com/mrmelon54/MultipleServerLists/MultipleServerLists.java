package com.mrmelon54.MultipleServerLists;

public class MultipleServerLists {
    public static final String MOD_ID = "multiple_server_lists";

    public static void init() {
        System.out.println(ExampleExpectPlatform.getConfigDirectory().toAbsolutePath().normalize().toString());
    }
}
