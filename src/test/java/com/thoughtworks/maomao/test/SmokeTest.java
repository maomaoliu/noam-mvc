package com.thoughtworks.maomao.test;

import com.thoughtworks.maomao.example.model.Book;
import com.thoughtworks.maomao.example.service.BookServiceImpl;
import org.eclipse.jetty.client.api.ContentResponse;
import org.junit.Test;

import java.util.List;
import static org.junit.Assert.*;

public class SmokeTest extends AbstractWebTest {

    @Test
    public void should_get_books_info() throws Exception {

        ContentResponse response = client
                .GET("http://localhost:11090/noam-mvc/books");

        assertEquals(200, response.getStatus());
        assertNotNull(response.getContent());
        List<Book> allBooks = new BookServiceImpl().getAllBooks();
        for (Book book : allBooks) {
            assertTrue(response.getContentAsString().contains(book.getName()));
            assertTrue(response.getContentAsString().contains(book.getAuthor()));
        }
    }
}
