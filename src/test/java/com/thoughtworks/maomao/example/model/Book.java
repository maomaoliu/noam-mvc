package com.thoughtworks.maomao.example.model;

public class Book {
    private Integer id;
    private String name;
    private String author;
    private Comment comment;

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

    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        comment.setBook(this);
        this.comment = comment;
    }
}
