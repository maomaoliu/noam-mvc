package com.thoughtworks.maomao.example.model;

import java.util.List;

public class Book {
    private Integer id;
    private String name;
    private String author;
    private List<Comment> comments;

    public Book() {
        name = "";
        author = "";
    }

    public Book(String name, String author) {
        this.name = name;
        this.author = author;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
