package com.example.movie_booking;

import java.io.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/BookTicketServlet")
public class BookTicketServlet extends HttpServlet {
    private static final String FILE_PATH = "/WEB-INF/bookings.txt";

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Get booking details from form
        String movieTitle = request.getParameter("movieTitle");
        String selectedDate = request.getParameter("selectedDate");
        String selectedScreen = request.getParameter("selectedScreen");
        String selectedTime = request.getParameter("selectedTime");

        // Format booking details
        String bookingDetails = movieTitle + "|" + selectedDate + "|" + selectedScreen + "|" + selectedTime;

        // Get absolute path to bookings.txt
        String filePath = getServletContext().getRealPath(FILE_PATH);

        // Append booking details to file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(bookingDetails);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Redirect to seat selection page
        response.sendRedirect("seat_selection.jsp?title=" + movieTitle);
    }
}
