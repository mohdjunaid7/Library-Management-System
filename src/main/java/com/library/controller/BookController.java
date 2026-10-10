package com.library.controller;

import com.library.entity.Book;
import com.library.entity.NotificationType;
import com.library.service.BookService;
import com.library.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class BookController {

    @Autowired private BookService bookService;
    @Autowired private NotificationService notificationService;

    // ---------- Search (used by both librarian and member) ----------
    @GetMapping("/books")
    public String searchBooks(@RequestParam(required = false) String query, Model model) {
        model.addAttribute("books", bookService.search(query));
        model.addAttribute("query", query);
        return "books";
    }

    // ---------- Librarian: manage catalog ----------
    @GetMapping("/librarian/books")
    public String manageBooks(Model model) {
        model.addAttribute("books", bookService.getAllBooks());
        return "librarian-books";
    }

    @GetMapping("/librarian/books/new")
    public String newBookForm(Model model) {
        model.addAttribute("book", new Book());
        return "book-form";
    }

    @GetMapping("/librarian/books/edit/{id}")
    public String editBookForm(@PathVariable Long id, Model model) {
        model.addAttribute("book", bookService.getById(id));
        return "book-form";
    }

    @PostMapping("/librarian/books/save")
    public String saveBook(@ModelAttribute Book book, Model model) {
        try {
            if (book.getId() == null) {
                bookService.addBook(book);
                notificationService.sendToAllMembers(
                        "New arrival: \"" + book.getTitle() + "\" by " + book.getAuthor() + " is now available!",
                        NotificationType.NEW_ARRIVAL);
            } else {
                bookService.updateBook(book.getId(), book);
            }
            return "redirect:/librarian/books";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("book", book);
            return "book-form";
        }
    }

    @GetMapping("/librarian/books/delete/{id}")
    public String deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return "redirect:/librarian/books";
    }
}
