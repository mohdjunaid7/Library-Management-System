package com.library.service;

import com.library.entity.Book;
import com.library.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Book getById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Book not found: " + id));
    }

    public List<Book> search(String query) {
        if (query == null || query.isBlank()) {
            return getAllBooks();
        }
        return bookRepository
                .findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrGenreContainingIgnoreCase(
                        query, query, query);
    }

    public Book addBook(Book book) {
        if (bookRepository.existsByIsbn(book.getIsbn())) {
            throw new IllegalArgumentException("A book with this ISBN already exists");
        }
        book.setAvailableCopies(book.getTotalCopies());
        return bookRepository.save(book);
    }

    public Book updateBook(Long id, Book updated) {
        Book existing = getById(id);
        int borrowedCopies = existing.getTotalCopies() - existing.getAvailableCopies();

        existing.setTitle(updated.getTitle());
        existing.setAuthor(updated.getAuthor());
        existing.setIsbn(updated.getIsbn());
        existing.setGenre(updated.getGenre());
        existing.setTotalCopies(updated.getTotalCopies());
        // keep available copies consistent with however many are currently borrowed
        existing.setAvailableCopies(Math.max(0, updated.getTotalCopies() - borrowedCopies));
        return bookRepository.save(existing);
    }

    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }
}
