package com.example.movie_booking;

import java.io.*;
import java.util.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/admin/bookings")
public class AdminBookingServlet extends HttpServlet {
    private static final String FILE_PATH = "/WEB-INF/bookings.txt";

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<String[]> bookings = new ArrayList<>();
        String filePath = getServletContext().getRealPath(FILE_PATH);

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] details = line.split("\\|"); // Expecting: title|date|screen|time
                bookings.add(details);
            }
        }

        request.setAttribute("bookings", bookings);
        request.getRequestDispatcher("/admin_bookings.jsp").forward(request, response);
    }
}
