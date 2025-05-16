package com.example.SeatBooking;

import java.io.*;
import java.util.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/SeatBookingServlet")
public class SeatBookingServlet extends HttpServlet {
    private static final String FILE_PATH = "/WEB-INF/seats.txt";

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String movieTitle = request.getParameter("movieTitle");
        String selectedSeats = request.getParameter("seats");

        if (movieTitle == null || selectedSeats == null || movieTitle.trim().isEmpty() || selectedSeats.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid seat selection.");
            return;
        }

        String filePath = getServletContext().getRealPath(FILE_PATH);
        File file = new File(filePath);
        Map<String, List<String>> seatData = new HashMap<>();

        // Read existing seat bookings
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] data = line.split("\\|");
                    if (data.length == 2) {
                        seatData.put(data[0], new ArrayList<>(Arrays.asList(data[1].split(","))));
                    }
                }
            }
        }

        // Add new bookings
        seatData.computeIfAbsent(movieTitle, k -> new ArrayList<>()).addAll(Arrays.asList(selectedSeats.split(",")));

        // Write updated bookings
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, false))) {
            for (Map.Entry<String, List<String>> entry : seatData.entrySet()) {
                writer.write(entry.getKey() + "|" + String.join(",", entry.getValue()));
                writer.newLine();
            }
        }

        response.setStatus(HttpServletResponse.SC_OK);
    }
}
