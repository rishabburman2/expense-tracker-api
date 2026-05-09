package com.expensetracker.expensetracker.repository;

import com.expensetracker.expensetracker.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, String>{
    
    List<Expense> findByUserId(String userId);

    // @Query lets you write JPQL for complex queries. JPQL is like SQL but uses Java class/field names instead of table/column names
    @Query("SELECT e FROM Expense e WHERE e.user.id = :userId " + "AND YEAR(e.expenseDate) = :year " + "AND MONTH(e.expenseDate) = :month")

    
    List<Expense> findByUserIdAndMonth(
        @Param("userId") String userId,         // @Param binds the method parameter to the :userId placeholder in the query above
        @Param("year") int year,
        @Param("month") int month
    );


    // returns a list of [Category, total_amount] pairs
    // will be used by ReportService to build category breakdown report
    @Query("SELECT e.category, SUM(e.amount) FROM Expense e " + "WHERE e.user.id = :userId GROUP BY e.category")
    List<Object[]> sumByCategory(@Param("userId") String userId);

    // Groups expenses by month and sums the total for each month
    @Query(value="SELECT DATE_TRUNC('month', e.expense_date), SUM(e.amount) " +
               "FROM expenses e WHERE e.user_id = :userId " +
               "GROUP BY DATE_TRUNC('month', e.expense_date) " +
               "ORDER BY DATE_TRUNC('month', e.expense_date) DESC", 
            nativeQuery = true)
    List<Object[]> sumByMonth(@Param("userId") String userId);

}
