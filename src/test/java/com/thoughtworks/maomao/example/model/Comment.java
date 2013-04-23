package com.thoughtworks.maomao.example.model;

public class Comment {
    private Book book;
    private String content;
    private String author;

    public Book getBook() {
        return book;
    }

    public String getContent() {
        return content;
    }

    public String getAuthor() {
        return author;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
