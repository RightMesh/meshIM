package io.left.meshenger.Services;

import io.left.meshenger.Activities.IActivity;

interface IMeshConnectionManagerService {
    void send(in String message);

    void registerMainActivityCallback(in IActivity callback);

    void sendHello();

    void configure();
}