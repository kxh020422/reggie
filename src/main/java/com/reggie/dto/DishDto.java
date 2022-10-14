package com.reggie.dto;


import com.reggie.pojo.Dish;
import com.reggie.pojo.DishFlavor;

import java.util.ArrayList;
import java.util.List;

public class DishDto extends Dish {

    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;

    public List<DishFlavor> getFlavors() {
        return flavors;
    }

    public void setFlavors(List<DishFlavor> flavors) {
        this.flavors = flavors;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Integer getCopies() {
        return copies;
    }

    public void setCopies(Integer copies) {
        this.copies = copies;
    }

    public DishDto() {
    }

    public DishDto(List<DishFlavor> flavors, String categoryName, Integer copies) {
        this.flavors = flavors;
        this.categoryName = categoryName;
        this.copies = copies;
    }
}
