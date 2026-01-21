package com.maknom.eco.guard.controller;

import java.util.List;


public record SyncResponse (Boolean success, List<Long> syncedIds, List<Long> failedIds, String message) {

}
