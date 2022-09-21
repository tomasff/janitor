package com.tomff.janitor.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

public class WebTable {
    public static List<List<WebElement>> getRows(WebElement root) {
        int numRows = root.findElements(By.xpath("tbody/tr")).size();
        int numColumns = root.findElements(By.xpath("tbody/tr[1]/td")).size();

        List<List<WebElement>> rows = new ArrayList<>();

        for (int currentRow = 0; currentRow < numRows; currentRow++) {
            rows.add(getRow(root, currentRow, numColumns));
        }

        return rows;
    }

    public static List<List<WebElement>> getRowsUsingHeader(WebElement root) {
        int numRows = root.findElements(By.xpath("tbody/tr")).size();
        int numColumns = root.findElements(By.xpath("tbody/tr[1]/th")).size();

        List<List<WebElement>> rows = new ArrayList<>();

        // Ignore header row
        for (int currentRow = 1; currentRow < numRows; currentRow++) {
            rows.add(getRow(root, currentRow, numColumns));
        }

        return rows;
    }
    private static List<WebElement> getRow(WebElement root, int row, int numColumns) {
        List<WebElement> cells = new ArrayList<>();

        for (int currentColumn = 0; currentColumn < numColumns; currentColumn++) {
            cells.add(getCell(root, row, currentColumn));
        }

        return cells;
    }
    private static WebElement getCell(WebElement root, int row, int column) {
        return root.findElement(By.xpath("tbody/tr[" + (row + 1) + "]/td[" + (column + 1) + "]"));
    }
}
