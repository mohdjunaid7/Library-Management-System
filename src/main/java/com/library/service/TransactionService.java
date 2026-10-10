package com.library.service;

import com.library.entity.*;
import com.library.repository.BookRepository;
import com.library.repository.TransactionRepository;
import com.library.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class TransactionService {

    private static final int LOAN_PERIOD_DAYS = 14;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    /** Member (or librarian on their behalf) borrows a book. */
    public Transaction issueBook(Long bookId, Long memberId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found"));
        User member = userRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        if (!book.isAvailable()) {
            throw new IllegalStateException("No copies of this book are currently available");
        }

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);

        LocalDate issueDate = LocalDate.now();
        LocalDate dueDate = issueDate.plusDays(LOAN_PERIOD_DAYS);
        Transaction transaction = new Transaction(book, member, issueDate, dueDate);
        transaction = transactionRepository.save(transaction);

        notificationService.sendToMember(memberId,
                "You borrowed \"" + book.getTitle() + "\". Please return it by " + dueDate + ".",
                NotificationType.DUE_DATE);

        return transaction;
    }

    /** Member returns a borrowed book. */
    public Transaction returnBook(Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));

        if (transaction.getReturnDate() != null) {
            throw new IllegalStateException("This book has already been returned");
        }

        transaction.setReturnDate(LocalDate.now());
        transaction.setStatus(TransactionStatus.RETURNED);
        transactionRepository.save(transaction);

        Book book = transaction.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);

        return transaction;
    }

    public List<Transaction> getHistoryForMember(Long memberId) {
        return transactionRepository.findByMemberIdOrderByIssueDateDesc(memberId);
    }

    public List<Transaction> getAllTransactions() {
        refreshOverdueStatuses();
        return transactionRepository.findAllByOrderByIssueDateDesc();
    }

    /** Marks any still-borrowed transactions past their due date as OVERDUE. */
    private void refreshOverdueStatuses() {
        List<Transaction> issued = transactionRepository.findByStatus(TransactionStatus.ISSUED);
        LocalDate today = LocalDate.now();
        for (Transaction t : issued) {
            if (t.getDueDate() != null && t.getDueDate().isBefore(today)) {
                t.setStatus(TransactionStatus.OVERDUE);
                transactionRepository.save(t);
            }
        }
    }
}
