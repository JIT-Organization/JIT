package com.justintime.jit.service;

public interface CookLoadTrackerService {
    int getCookLoad(long cookId);
    void addLoad(long cookId, int minutes, long orderItemId);
    void removeLoad(long cookId, int minutes, long orderItemId);
}
