package com.payu.client.controller;

import com.payu.client.model.Book;
import com.payu.client.model.BookType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.ws.rs.PathParam;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Controller
@RequestMapping(path = "/ui/books",
        consumes = {org.springframework.http.MediaType.APPLICATION_JSON_VALUE, org.springframework.http.MediaType.ALL_VALUE},
        produces = {org.springframework.http.MediaType.APPLICATION_JSON_VALUE})
public class BookCollectorController {

    private static final String BASE_URL= "http://localhost:8080/book/collector";

    @GetMapping
    public String getAllBooks(Model model) {
        try {
            Client client = ClientBuilder.newClient();
            WebTarget target = client.target(BASE_URL);
            Response response = target.path("/list").request(MediaType.APPLICATION_JSON).get();
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
            Client client = ClientBuilder.newClient();
            WebTarget target = client.target(BASE_URL);
            Response response = target.path("/find/" + bookName).request(MediaType.APPLICATION_JSON).get();
            List<Book> books = response.readEntity(new GenericType<List<Book>>() {});

            model.addAttribute("bookName", bookName);
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
            Client client = ClientBuilder.newClient();
            WebTarget target = client.target(BASE_URL);
            Response response = target.path("/add").request(MediaType.APPLICATION_JSON).post(Entity.json(book));
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
            Client client = ClientBuilder.newClient();
            WebTarget target = client.target(BASE_URL);
            target.path("/delete/" + bookId).request(MediaType.APPLICATION_JSON).delete();

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
            Client client = ClientBuilder.newClient();
            WebTarget target = client.target(BASE_URL);
            Response response = target.path("/get/" + bookId).request(MediaType.APPLICATION_JSON).get();
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
            Client client = ClientBuilder.newClient();
            WebTarget target = client.target("http://localhost:8080/book/collector");
            Response response = target.path("/update").request(MediaType.APPLICATION_JSON).post(Entity.json(book));
            Book bookResponse = response.readEntity(new GenericType<Book>() {});

            redirectAttributes.addFlashAttribute("message", "Book (ID: " + bookResponse.getId()
                    + ") has been updated successfully!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }
        return "redirect:/ui/books";
    }
}
