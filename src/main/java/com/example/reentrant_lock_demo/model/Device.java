package com.example.reentrant_lock_demo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Device {

    private String deviceId;
    private String name;
    private boolean enabled;
}
