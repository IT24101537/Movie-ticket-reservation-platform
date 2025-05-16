package com.example.SeatBooking;

import java.io.*;
import java.util.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/GetBookedSeatsServlet")
public class GetBookedSeatsServlet extends HttpServlet {
    private static final String FILE_PATH = "/WEB-INF/seats.txt";

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String movieTitle = request.getParameter("movieTitle");
        List<String> bookedSeats = new ArrayList<>();

        if (movieTitle == null || movieTitle.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Movie title is required.");
            return;
        }

        String filePath = getServletContext().getRealPath(FILE_PATH);
        File file = new File(filePath);

        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] data = line.split("\\|");
                    if (data.length == 2 && data[0].equalsIgnoreCase(movieTitle)) {
                        bookedSeats.addAll(Arrays.asList(data[1].split(",")));
                    }
                }
            } catch (IOException e) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error reading seat data.");
                return;
            }
        }

        String jsonResponse = "[\"" + String.join("\",\"", bookedSeats) + "\"]";
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(jsonResponse);
    }
}
