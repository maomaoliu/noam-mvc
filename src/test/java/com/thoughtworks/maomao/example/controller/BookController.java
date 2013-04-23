package com.thoughtworks.maomao.example.controller;


import com.thoughtworks.maomao.annotation.Controller;
import com.thoughtworks.maomao.annotations.Glue;
import com.thoughtworks.maomao.core.NoamController;
import com.thoughtworks.maomao.example.model.Book;
import com.thoughtworks.maomao.example.service.BookService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@Controller(model = Book.class)
public class BookController extends NoamController {

    @Glue
    private BookService bookService;

    @Override
    public List index(HttpServletRequest req, HttpServletResponse resp, Map<String, Object> params) {
        return bookService.getAllBooks();
    }

    @Override
    public Book show(Integer id, HttpServletRequest req, HttpServletResponse resp, Map<String, Object> params) {
        return bookService.getBook(id);
    }

    @Override
    public void delete(Integer id, HttpServletRequest req, HttpServletResponse resp, Map<String, Object> params) {
        bookService.deleteBook(id);
    }

    @Override
    public Object create(HttpServletRequest req, HttpServletResponse resp, Map<String, Object> params) {
        return new Book();
    }

    @Override
    public String doSave(Object object) {
        Book book = (Book) object;
        book = bookService.addBook(book);
        return "book?id="+book.getId();
    }

    public void my(HttpServletRequest req, HttpServletResponse resp, Map<String, Object> params) {
        params.put("book_name", "GOOD NIGHT~");
    }

    public void setBookService(BookService bookService) {
        this.bookService = bookService;
    }
}
