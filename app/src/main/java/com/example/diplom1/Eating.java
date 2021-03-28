package com.example.diplom1;

//Model for ListOfEating
public class Eating {
    String name;
    int count;
    float calories;
    float proteins;
    float fats;
    float carbohydrates;

    public Eating(String name, int count, float calories, float proteins, float fats, float carbohydrates) {
        this.name = name;
        this.count = count;
        this.calories = calories;
        this.proteins = proteins;
        this.fats = fats;
        this.carbohydrates = carbohydrates;
    }

    public Eating( ) {
    }

    public String getName() {
        return name;
    }

    public int getCount() {
        return count;
    }

    public float getCalories() {
        return calories;
    }

    public float getProteins() {
        return proteins;
    }

    public float getFats() {
        return fats;
    }

    public float getCarbohydrates() {
        return carbohydrates;
    }
}
