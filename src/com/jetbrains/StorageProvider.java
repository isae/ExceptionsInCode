package com.jetbrains;

/**
 * User: Xottab
 * Date: 22.07.2014
 */
public class StorageProvider {
    private static StorageProvider instance;

    StorageProvider getInstance() {
        if (instance == null) {
            instance = new StorageProvider();
        }
        return instance;
    }
}
