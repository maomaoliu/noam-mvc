package com.thoughtworks.maomao.test;

import com.thoughtworks.maomao.example.model.Book;
import com.thoughtworks.maomao.example.service.BookServiceImpl;
import org.eclipse.jetty.client.api.ContentProvider;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.api.Response;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class SmokeTest extends AbstractWebTest {

    @Test
    public void should_get_books_info() throws Exception {

        ContentResponse response = client
                .GET("http://localhost:11090/noam-mvc/book?method=index");

        assertEquals(200, response.getStatus());
        assertNotNull(response.getContent());
        List<Book> allBooks = new BookServiceImpl().getAllBooks();
        for (Book book : allBooks) {
            assertTrue(response.getContentAsString().contains(book.getName()));
            assertTrue(response.getContentAsString().contains(book.getAuthor()));
        }
    }

@Test
    public void should_delete_book() throws Exception {

        ContentResponse response = client
                .GET("http://localhost:11090/noam-mvc/book?id=2&method=delete");

        assertEquals(200, response.getStatus());
        assertNotNull(response.getContent());
        List<Book> allBooks = new BookServiceImpl().getAllBooks();
        for (Book book : allBooks) {
            assertTrue(response.getContentAsString().contains(book.getName()));
            assertTrue(response.getContentAsString().contains(book.getAuthor()));
        }
    }

    @Test
    public void should_post_book() throws Exception {

        BookServiceImpl bookService = new BookServiceImpl();
        int bookNumber =  bookService.getAllBooks().size();

        ContentResponse response = client
                .POST("http://localhost:11090/noam-mvc/book?method=create")
                .param("book.name", "How to build mvc framework?")
                .param("book.author", "Lei")
                .param("book.comment.content", "Good book.")
                .param("book.comment.author", "Lu")
                .send();

        assertEquals(200, response.getStatus());
        assertNotNull(response.getContent());
        Book book = bookService.getBook(++bookNumber);
        assertTrue(response.getContentAsString().contains(book.getName()));
        assertTrue(response.getContentAsString().contains(book.getAuthor()));
        assertTrue(response.getContentAsString().contains(book.getComment().getContent()));
        assertTrue(response.getContentAsString().contains(book.getComment().getAuthor()));
    }
}
