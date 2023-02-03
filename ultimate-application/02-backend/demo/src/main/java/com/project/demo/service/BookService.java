package com.project.demo.service;

import com.project.demo.dao.BookRepository;
import com.project.demo.dao.CheckoutRepository;
import com.project.demo.dao.HistoryRepository;
import com.project.demo.entity.Book;
import com.project.demo.entity.Checkout;
import com.project.demo.entity.History;
import com.project.demo.responsemodels.ShelfCurrentLoansReponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
@AllArgsConstructor
public class BookService {

    private BookRepository bookRepository;
    private CheckoutRepository checkoutRepository;

    private HistoryRepository historyRepository;

    public Book checkoutBook(String userEmail, Long bookId) throws Exception{
        Optional<Book> book = bookRepository.findById(bookId);
        Checkout validateCheckout= checkoutRepository.findByUserEmailAndBookId(userEmail, bookId);
        if (!book.isPresent() || validateCheckout != null || book.get().getCopiesAvailable() <= 0){
            throw new Exception("Book doesn't exist or already checked out by user.");
        }
        book.get().setCopiesAvailable(book.get().getCopiesAvailable() - 1);
        bookRepository.save(book.get());

        Checkout checkout = new Checkout(userEmail, LocalDate.now().toString(), LocalDate.now().plusDays(7).toString(), book.get().getId());
        checkoutRepository.save(checkout);

        return book.get();
    }

    public Boolean checkoutBookByUser(String userEmail, Long bookId){
        Checkout validateCheckout = checkoutRepository.findByUserEmailAndBookId(userEmail, bookId);
        if (validateCheckout != null){
            return true;
        } else {
            return false;
        }
    }

    public int currentLoansCount(String userEmail){
        return checkoutRepository.findBooksByUserEmail(userEmail).size();
    }

    public List<ShelfCurrentLoansReponse> currentLoans(String userEmail) throws Exception {
        List<ShelfCurrentLoansReponse> shelfCurrentLoansReponses = new ArrayList<>();
        List<Checkout> checkoutList = checkoutRepository.findBooksByUserEmail(userEmail);
        List<Long> bookIdList = new ArrayList<>();
        for(Checkout i: checkoutList){
            bookIdList.add(i.getBookId());
        }
        List<Book> books = bookRepository.findBooksByBookIds(bookIdList);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (Book book: books){
            Optional<Checkout> checkout
                    = checkoutList.stream().filter(x -> x.getBookId() == book.getId()).findFirst();
            if (checkout.isPresent()){
                Date d1 = sdf.parse(checkout.get().getReturnDate());
                Date d2 = sdf.parse(LocalDate.now().toString());
                TimeUnit time = TimeUnit.DAYS;
                long diffInTime = time.convert(d1.getTime() - d2.getTime(), TimeUnit.MILLISECONDS);
                shelfCurrentLoansReponses.add(new ShelfCurrentLoansReponse(book, (int) diffInTime));
            }
        }
        return shelfCurrentLoansReponses;
    }

    public void returnBook (String userEmail, Long bookId) throws Exception {
        Optional<Book> book = bookRepository.findById(bookId);
        Checkout validateCheckout = checkoutRepository.findByUserEmailAndBookId(userEmail, bookId);
        if (!book.isPresent() || validateCheckout == null){
            throw new Exception("Book does not exist or not checked out by user");
        }
        book.get().setCopiesAvailable(book.get().getCopiesAvailable() + 1);
        bookRepository.save(book.get());
        checkoutRepository.deleteById(validateCheckout.getId());

        History history = new History(userEmail, validateCheckout.getCheckoutDate(), LocalDate.now().toString(), book.get().getTitle(), book.get().getAuthor(), book.get().getDescription(),book.get().getImg());
        historyRepository.save(history);
    }

    public void renewLoan(String userEmail, Long bookId) throws Exception{
        Checkout validateCheckout = checkoutRepository.findByUserEmailAndBookId(userEmail, bookId);
        if (validateCheckout == null) {
            throw new Exception("Book is not exist or not checked out by user");
        }
        SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date d1 = sdFormat.parse(validateCheckout.getReturnDate());
        Date d2 = sdFormat.parse(LocalDate.now().toString());
        if (d1.compareTo(d2) > 0 || d1.compareTo(d2) == 0){
            validateCheckout.setReturnDate(LocalDate.now().plusDays(7).toString());
            checkoutRepository.save(validateCheckout);
        }
    }


}
