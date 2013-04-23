package com.thoughtworks.maomao.example.controller;


import com.thoughtworks.maomao.annotation.Controller;
import com.thoughtworks.maomao.annotations.Glue;
import com.thoughtworks.maomao.example.model.Book;
import com.thoughtworks.maomao.example.service.BookService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@Controller(model = Book.class)
public class BookController{

    @Glue
    private BookService bookService;

    public void index(HttpServletRequest req, HttpServletResponse resp, Map<String, Object> params) {
        List books = bookService.getAllBooks();
        params.put("books", books);
    }

    public void show(HttpServletRequest req, HttpServletResponse resp, Map<String, Object> params) {
        int id = Integer.parseInt(req.getParameter("id"));
        Book book = bookService.getBook(id);
        params.put("book", book);
    }

    public String delete(HttpServletRequest req, HttpServletResponse resp, Map<String, Object> params) {
        int id = Integer.parseInt(req.getParameter("id"));
        bookService.deleteBook(id);
        return "book?method=index";
    }

    public void create(HttpServletRequest req, HttpServletResponse resp, Map<String, Object> params) {
        Book book = new Book();
        params.put("book", book);
    }

    public String createPost(HttpServletRequest req, HttpServletResponse resp, Map<String, Object> params) {
        Book book = (Book) params.get("book");
        book = bookService.addBook(book);
        return "book?method=show&id="+book.getId();
    }

    public void my(HttpServletRequest req, HttpServletResponse resp, Map<String, Object> params) {
        params.put("book_name", "GOOD NIGHT~");
    }

    public void setBookService(BookService bookService) {
        this.bookService = bookService;
    }
}
