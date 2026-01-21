package com.maknom.eco.guard.controller;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class SyncEvent<T> {
   private String type;
   private String timestamp;
   private T data;
}
