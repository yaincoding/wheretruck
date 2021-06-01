package com.gamakdragons.wheretruck.food.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Food {
    
    private String id;
    private String name;
    private int cost;
    private String description;
    private byte[] image;
    private String truckId;

}