package com.karim.ater.myexpenses.Helpers;

public class CategoryData extends CategoryItem {

    private float totalCost;
    private int count;
    private CategoryItem categoryItem;



    public CategoryData(CategoryItem categoryItem, float totalCost, int count) {
        super(categoryItem);
        this.totalCost = totalCost;
        this.count = count;
    }

    public CategoryData() {

    }

    public float getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(float totalCost) {
        this.totalCost = totalCost;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
