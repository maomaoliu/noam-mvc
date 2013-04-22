package com.thoughtworks.maomao.example.service;

import com.thoughtworks.maomao.example.model.Book;

import java.util.List;

public interface BookService {
    Book getBook(Integer id);

    void addBook(Book book);

    void updateBook(Book book);

    void deleteBook(Integer id);

    List<Book> getAllBooks();

    Book save(Book book);
}
