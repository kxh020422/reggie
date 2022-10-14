package com.reggie.dto;



import com.reggie.pojo.Setmeal;
import com.reggie.pojo.SetmealDish;

import java.util.List;

public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;

    public List<SetmealDish> getSetmealDishes() {
        return setmealDishes;
    }

    public void setSetmealDishes(List<SetmealDish> setmealDishes) {
        this.setmealDishes = setmealDishes;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public SetmealDto() {
    }

    public SetmealDto(List<SetmealDish> setmealDishes, String categoryName) {
        this.setmealDishes = setmealDishes;
        this.categoryName = categoryName;
    }

    @Override
    public String toString() {
        return "SetmealDto{" +
                "setmealDishes=" + setmealDishes +
                ", categoryName='" + categoryName + '\'' +
                '}';
    }
}
