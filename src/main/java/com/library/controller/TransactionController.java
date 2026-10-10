package com.library.controller;

import com.library.entity.User;
import com.library.service.BookService;
import com.library.service.TransactionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class TransactionController {

    @Autowired private TransactionService transactionService;
    @Autowired private BookService bookService;

    // ---------- Member: borrow a book ----------
    @PostMapping("/member/borrow/{bookId}")
    public String borrowBook(@PathVariable Long bookId, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        try {
            transactionService.issueBook(bookId, user.getId());
        } catch (IllegalStateException | IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("books", bookService.getAllBooks());
            return "books";
        }
        return "redirect:/member/dashboard";
    }

    // ---------- Member: return a book ----------
    @PostMapping("/member/return/{transactionId}")
    public String returnBook(@PathVariable Long transactionId) {
        transactionService.returnBook(transactionId);
        return "redirect:/member/dashboard";
    }

    // ---------- Librarian: view / manage all transactions ----------
    @GetMapping("/librarian/transactions")
    public String allTransactions(Model model) {
        model.addAttribute("transactions", transactionService.getAllTransactions());
        return "librarian-transactions";
    }

    // ---------- Librarian: issue a book on behalf of a member ----------
    @PostMapping("/librarian/transactions/issue")
    public String librarianIssue(@RequestParam Long bookId, @RequestParam Long memberId, Model model) {
        try {
            transactionService.issueBook(bookId, memberId);
        } catch (IllegalStateException | IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
        }
        return "redirect:/librarian/transactions";
    }

    @PostMapping("/librarian/transactions/return/{transactionId}")
    public String librarianReturn(@PathVariable Long transactionId) {
        transactionService.returnBook(transactionId);
        return "redirect:/librarian/transactions";
    }
}
