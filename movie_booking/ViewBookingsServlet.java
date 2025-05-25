package com.example.movie_booking;

import java.io.*;
import java.util.*;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/admin/ViewBookingsServlet")
public class ViewBookingsServlet extends HttpServlet {
    private static final String FILE_PATH = "/WEB-INF/bookings.txt";

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<String[]> bookings = new ArrayList<>();
        String filePath = getServletContext().getRealPath(FILE_PATH);

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    String[] booking = line.split("\\|");
                    if (booking.length >= 4) {
                        bookings.add(booking);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        request.setAttribute("bookings", bookings);
        request.getRequestDispatcher("view_bookings.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Handle booking cancellation
        String movieTitle = request.getParameter("movieTitle");
        String bookingDate = request.getParameter("bookingDate");
        String screen = request.getParameter("screen");
        String time = request.getParameter("time");

        String filePath = getServletContext().getRealPath(FILE_PATH);
        List<String> updatedBookings = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    String[] booking = line.split("\\|");
                    if (booking.length >= 4 &&
                            !(booking[0].equals(movieTitle) &&
                                    booking[1].equals(bookingDate) &&
                                    booking[2].equals(screen) &&
                                    booking[3].equals(time))) {
                        updatedBookings.add(line);
                    }
                }
            }
        }

        // Write back the updated bookings
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (String booking : updatedBookings) {
                writer.write(booking);
                writer.newLine();
            }
        }

        response.sendRedirect("ViewBookingsServlet");
    }
}