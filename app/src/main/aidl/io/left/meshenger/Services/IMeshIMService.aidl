package io.left.meshenger.Services;

import io.left.meshenger.Activities.IActivity;

interface IMeshIMService {
    void send(in String message);

    void registerMainActivityCallback(in IActivity callback);
}