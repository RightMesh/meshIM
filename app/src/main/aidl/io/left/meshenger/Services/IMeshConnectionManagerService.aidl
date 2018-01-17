package io.left.meshenger.Services;

import io.left.meshenger.Activities.IMainActivity;

interface IMeshConnectionManagerService {
    void send(in String message);

    void registerMainActivityCallback(in IMainActivity callback);
}