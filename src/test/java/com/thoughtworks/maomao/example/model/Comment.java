package com.thoughtworks.maomao.example.model;

public class Comment {
    private Book book;
    private String content;

    public Comment(Book book, String content) {
        this.book = book;
        this.content = content;
    }

    public Book getBook() {
        return book;
    }

    public String getContent() {
        return content;
    }
}
