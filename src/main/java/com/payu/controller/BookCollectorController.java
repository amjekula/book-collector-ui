package com.payu.controller;

import com.payu.model.Book;
import com.payu.model.BookType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.ws.rs.PathParam;
import javax.ws.rs.client.*;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Controller
@RequestMapping(path = "/ui/books",
        consumes = {org.springframework.http.MediaType.APPLICATION_JSON_VALUE, org.springframework.http.MediaType.ALL_VALUE},
        produces = {org.springframework.http.MediaType.APPLICATION_JSON_VALUE})
public class BookCollectorController {

    @Value(value = "${server.baseUrl}")
    private String baseUrl;

    @GetMapping
    public String getAllBooks(Model model) {
        try {
            Response response = requestBuilder("/list").get();
            List<Book> books = response.readEntity(new GenericType<List<Book>>() {});
            model.addAttribute("books", books);

        } catch (Exception e) {
            model.addAttribute("message", e.getMessage());
        }
        return "book_collector";
    }

    @GetMapping("/search")
    public String findBookByName(Model model, @PathParam("bookName") String bookName) {
        try {
            Response response = requestBuilder("/find/" + bookName.trim()).get();
            List<Book> books = response.readEntity(new GenericType<List<Book>>() {});

            model.addAttribute("bookName", bookName.trim());
            model.addAttribute("books", books);

        } catch (Exception e) {
            model.addAttribute("message", e.getMessage());
        }
        return "book_collector";
    }

    @GetMapping("/create")
    public String addBookForm(Model model) {
        Book book = new Book();
        model.addAttribute("book", book);
        model.addAttribute("bookTypes", BookType.values());

        model.addAttribute("pageTitle", "Create new Book");
        return "book_collector_form";
    }

    @PostMapping("/add")
    public String addBook(Book book, RedirectAttributes redirectAttributes) {
        try {
            Response response = requestBuilder("/add").post(Entity.json(book));
            Book bookResponse = response.readEntity(new GenericType<Book>() {});

            redirectAttributes.addFlashAttribute("message", "The Book (ID: " + bookResponse.getId()
                    + ") has been saved successfully!");

        } catch (Exception e) {
            redirectAttributes.addAttribute("message", e.getMessage());
        }
        return "redirect:/ui/books";
    }

    @GetMapping("/delete/{bookId}")
    public String deleteBook(@PathVariable("bookId") Long bookId, RedirectAttributes redirectAttributes) {
        try {
            requestBuilder("/delete/" + bookId).delete();

            redirectAttributes.addFlashAttribute("message", "The Book (ID: " + bookId
                    + ") has been deleted successfully!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }
        return "redirect:/ui/books";
    }

    @GetMapping("/get/{bookId}")
    public String getBook(@PathVariable("bookId") Long bookId, Model model, RedirectAttributes redirectAttributes) {
        try {
            Response response = requestBuilder("/get/" + bookId).get();
            Book book = response.readEntity(new GenericType<Book>() {});

            model.addAttribute("book", book);
            model.addAttribute("bookTypes", BookType.values());

            model.addAttribute("pageTitle", "Edit Book (ID: " + book.getId() + ")");
            return "book_collector_update_form";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return "redirect:/ui/books";
        }
    }

    @PostMapping("/edit")
    public String editBook(Book book, RedirectAttributes redirectAttributes) {
        try {
            Response response = requestBuilder("/update").post(Entity.json(book));
            Book bookResponse = response.readEntity(new GenericType<Book>() {});

            redirectAttributes.addFlashAttribute("message", "Book (ID: " + bookResponse.getId()
                    + ") has been updated successfully!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }
        return "redirect:/ui/books";
    }

    private Invocation.Builder requestBuilder(String url) {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(baseUrl);
        return target.path(url).request(MediaType.APPLICATION_JSON);
    }
}
