package com.example.payment;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/EditPaymentServlet")
public class EditPaymentServlet extends HttpServlet {
    private static final String FILE_PATH = "/WEB-INF/payments.txt";

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String indexStr = request.getParameter("index");
        String paymentMethod = request.getParameter("paymentMethod");
        String amount = request.getParameter("amount");
        String details = request.getParameter("details");

        if (indexStr == null || indexStr.isEmpty() || paymentMethod == null || amount == null || details == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request");
            return;
        }

        int index = Integer.parseInt(indexStr);
        String filePath = getServletContext().getRealPath(FILE_PATH);
        File file = new File(filePath);
        List<String> lines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int currentIndex = 0;
            while ((line = reader.readLine()) != null) {
                if (currentIndex == index) {
                    lines.add(paymentMethod + "|" + amount + "|" + details);
                } else {
                    lines.add(line);
                }
                currentIndex++;
            }
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        }

        response.sendRedirect("admin_payments.jsp");
    }
}
