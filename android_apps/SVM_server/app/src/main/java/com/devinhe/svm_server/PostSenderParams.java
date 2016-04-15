package com.devinhe.svm_server;

/**
 * Created by devin on 4/15/16.
 * lol java
 */
public class PostSenderParams {
    public MainActivity.MODEL modelType;
    public String features;
    public String serverIP;
    public int n;

    PostSenderParams(MainActivity.MODEL model, String features, String serverIP, int n) {
        this.modelType = model;
        this.features = features;
        this.serverIP = serverIP;
        this.n = n;
    }
}
