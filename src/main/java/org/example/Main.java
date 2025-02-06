package org.example;

import java.io.IOException;
import java.nio.file.*;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

public class Main {
    private static final String FILE_PATH = "numbers.txt";
    private static final ReentrantLock lock = new ReentrantLock();
    private static final Random random = new Random();

    public static void main(String[] args) {
        Thread evenWriter = new Thread(() -> writeNumbers(true));
        Thread oddWriter = new Thread(() -> writeNumbers(false));
        Thread reader = new Thread(Main::readNumbers);

        evenWriter.start();
        oddWriter.start();
        reader.start();
    }

    private static void writeNumbers(boolean isEven) {
        while (true) {
            int num = random.nextInt(100) * 2 + (isEven ? 0 : 1);
            lock.lock();
            try {
                Files.write(Paths.get(FILE_PATH), (num + " ").getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
            sleep(500);
        }
    }

    private static void readNumbers() {
        while (true) {
            lock.lock();
            try {
                if (Files.exists(Paths.get(FILE_PATH))) {
                    String content = Files.readString(Paths.get(FILE_PATH)).trim();
                    if (!content.isEmpty()) {
                        String[] numbers = content.split("\\s+");
                        System.out.println("Последнее число: " + numbers[numbers.length - 1]);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
            sleep(1000);
        }
    }

    private static void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
