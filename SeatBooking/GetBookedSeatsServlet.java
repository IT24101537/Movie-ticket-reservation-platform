package com.example.SeatBooking;

import java.io.*;
import java.util.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/GetBookedSeatsServlet")
public class GetBookedSeatsServlet extends HttpServlet {
    private static final String FILE_PATH = System.getProperty("java.io.tmpdir") + File.separator + "seats.txt";
    private static final String DATA_SEPARATOR = "\\|";
    private static final String SEAT_SEPARATOR = ",";

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String movieTitle = request.getParameter("movieTitle");
        if (movieTitle == null || movieTitle.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        Set<String> bookedSeats = new HashSet<>();
        File file = new File(FILE_PATH);

        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] data = line.split(DATA_SEPARATOR);
                    if (data.length == 2 && data[0].equalsIgnoreCase(movieTitle)) {
                        bookedSeats.addAll(Arrays.asList(data[1].split(SEAT_SEPARATOR)));
                        break;
                    }
                }
            }
        }

        response.setContentType("application/json");
        response.getWriter().write(toJsonArray(bookedSeats));
    }

    private String toJsonArray(Set<String> seats) {
        StringBuilder sb = new StringBuilder("[");
        Iterator<String> it = seats.iterator();
        while (it.hasNext()) {
            sb.append("\"").append(it.next()).append("\"");
            if (it.hasNext()) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }
}
