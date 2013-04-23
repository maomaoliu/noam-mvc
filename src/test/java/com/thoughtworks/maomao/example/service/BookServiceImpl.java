package com.thoughtworks.maomao.example.service;

import com.thoughtworks.maomao.annotation.Service;
import com.thoughtworks.maomao.example.model.Book;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BookServiceImpl implements BookService {

    private static final Map<Integer, Book> books = new HashMap<Integer, Book>();

    static {
        Book book1 = new Book("Hello World", "maomao");
        Book book2 = new Book("Funny MVC", "luliu");
        book1.setId(1);
        book2.setId(2);
        books.put(book1.getId(), book1);
        books.put(book2.getId(), book2);
    }

    @Override
    public Book getBook(Integer id) {
        return books.get(id);
    }

    @Override
    public void updateBook(Book book) {
        books.put(book.getId(), book);
    }

    @Override
    public void deleteBook(Integer id) {
        books.remove(id);
    }

    @Override
    public List<Book> getAllBooks() {
        return new ArrayList<Book>(books.values());
    }

    @Override
    public Book addBook(Book book) {
        book.setId(books.size() + 1);
        books.put(book.getId(), book);
        return book;
    }
}
