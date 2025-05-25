package com.example.SeatBooking;

import java.io.*;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/SeatBookingServlet")
public class SeatBookingServlet extends HttpServlet {
    private static final String FILE_PATH = System.getProperty("java.io.tmpdir") + File.separator + "seats.txt";
    private static final ReentrantLock fileLock = new ReentrantLock();
    private static final String DATA_SEPARATOR = "\\|";
    private static final String SEAT_SEPARATOR = ",";

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String movieTitle = request.getParameter("movieTitle");
        String selectedSeats = request.getParameter("seats");

        if (movieTitle == null || selectedSeats == null ||
                movieTitle.trim().isEmpty() || selectedSeats.trim().isEmpty()) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid request parameters.");
            return;
        }

        if (!selectedSeats.matches("^([A-Z]\\d+,?)+$")) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid seat format.");
            return;
        }

        try {
            boolean bookingSuccess = processSeatBooking(movieTitle, selectedSeats);
            if (bookingSuccess) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("Seats booked successfully");
            } else {
                sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Booking failed.");
            }
        } catch (IOException e) {
            sendErrorResponse(response, HttpServletResponse.SC_CONFLICT, e.getMessage());
        } catch (Exception e) {
            log("Error processing seat booking", e);
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "An error occurred while processing your request");
        }
    }

    private boolean processSeatBooking(String movieTitle, String selectedSeats) throws IOException {
        fileLock.lock();
        try {
            File file = new File(FILE_PATH);
            if (!file.exists()) {
                file.createNewFile();
            }

            Map<String, Set<String>> seatData = readExistingData(file);
            Set<String> existingSeats = seatData.getOrDefault(movieTitle, new HashSet<>());
            List<String> newSeats = Arrays.asList(selectedSeats.split(SEAT_SEPARATOR));

            for (String seat : newSeats) {
                if (existingSeats.contains(seat)) {
                    throw new IOException("Seat " + seat + " is already booked");
                }
            }

            seatData.computeIfAbsent(movieTitle, k -> new HashSet<>()).addAll(newSeats);
            return writeDataToFile(file, seatData);
        } finally {
            fileLock.unlock();
        }
    }

    private Map<String, Set<String>> readExistingData(File file) throws IOException {
        Map<String, Set<String>> seatData = new HashMap<>();
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] data = line.split(DATA_SEPARATOR);
                    if (data.length == 2) {
                        seatData.put(data[0], new HashSet<>(Arrays.asList(data[1].split(SEAT_SEPARATOR))));
                    }
                }
            }
        }
        return seatData;
    }

    private boolean writeDataToFile(File file, Map<String, Set<String>> seatData) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, false))) {
            for (Map.Entry<String, Set<String>> entry : seatData.entrySet()) {
                writer.write(entry.getKey() + "|" + String.join(SEAT_SEPARATOR, entry.getValue()));
                writer.newLine();
            }
            return true;
        }
    }

    private void sendErrorResponse(HttpServletResponse response, int statusCode, String message)
            throws IOException {
        response.setStatus(statusCode);
        response.setContentType("text/plain");
        response.getWriter().write(message);
    }
}
