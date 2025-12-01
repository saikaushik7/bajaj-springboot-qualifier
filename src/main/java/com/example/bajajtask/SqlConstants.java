package com.example.bajajtask;

public class SqlConstants {

    public static final String FINAL_QUERY = """
        WITH qualified AS (
            SELECT DISTINCT e.emp_id,
                   e.first_name,
                   e.last_name,
                   e.dob,
                   e.department
            FROM employee e
            JOIN payments p ON p.emp_id = e.emp_id
            WHERE p.amount > 70000
        ),
        ranked AS (
            SELECT q.*,
                   ROW_NUMBER() OVER (PARTITION BY q.department ORDER BY q.emp_id) AS rn
            FROM qualified q
        )
        SELECT d.department_name AS DEPARTMENT_NAME,
               AVG(EXTRACT(YEAR FROM AGE(CURRENT_DATE, r.dob))) AS AVERAGE_AGE,
               STRING_AGG(
                   r.first_name || ' ' || r.last_name,
                   ', '
                   ORDER BY r.rn
               ) FILTER (WHERE r.rn <= 10) AS EMPLOYEE_LIST
        FROM ranked r
        JOIN department d ON d.department_id = r.department
        GROUP BY d.department_id, d.department_name
        ORDER BY d.department_id DESC;
        """;
}
