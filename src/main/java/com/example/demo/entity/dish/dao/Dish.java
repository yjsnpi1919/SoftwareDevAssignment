package com.example.demo.entity.dish.dao;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;

@Entity
@Data
public class Dish {

    @Id
    private Integer dishId;

    private Integer canteenId;

    private String dishName;

    private String dishDescription;

    private String dishIcon;

    private BigDecimal dishPrice;

    private Integer dishStatus;


}