package com.devinhe.svm_server;

import com.devinhe.svm_server.models.ModelType;

/**
 * Created by devin on 4/15/16.
 * lol java
 */
public class PostSenderParams {
    public ModelType modelType;
    public String features;
    public String serverIP;
    public int n;

    public PostSenderParams(ModelType model, String features, String serverIP, int n) {
        this.modelType = model;
        this.features = features;
        this.serverIP = serverIP;
        this.n = n;
    }
}
