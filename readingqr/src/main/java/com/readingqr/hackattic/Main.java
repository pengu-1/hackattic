package com.readingqr.hackattic;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        ReadingQr read = new ReadingQr();
        try {
            read.readQr();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}