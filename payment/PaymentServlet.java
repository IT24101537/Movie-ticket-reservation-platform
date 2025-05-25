package com.example.payment;

import java.io.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

@WebServlet("/PaymentServlet")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 5 * 1024 * 1024)
public class PaymentServlet extends HttpServlet {
    private static final String FILE_PATH = "/WEB-INF/payments.txt";
    private static final String SLIP_UPLOAD_DIR = "/WEB-INF/uploads/";

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String paymentMethod = request.getParameter("paymentMethod");
        String amount = request.getParameter("amount");

        if (paymentMethod.equals("Direct Deposit")) {
            Part filePart = request.getPart("depositSlip");
            String fileName = filePart.getSubmittedFileName();
            String fileSavePath = getServletContext().getRealPath(SLIP_UPLOAD_DIR) + fileName;
            filePart.write(fileSavePath);

            savePayment("Direct Deposit", amount, "Slip: " + fileName);
        } else if (paymentMethod.equals("Card")) {
            String cardNumber = request.getParameter("cardNumber");
            String expiryDate = request.getParameter("expiryDate");
            String cvv = request.getParameter("cvv");

            if (cardNumber.length() == 16 && cvv.length() == 3) {
                savePayment("Card", amount, "Card No: **** **** **** " + cardNumber.substring(12));
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid card details.");
                return;
            }
        }

        response.setStatus(HttpServletResponse.SC_OK);
    }

    private void savePayment(String method, String amount, String details) throws IOException {
        String filePath = getServletContext().getRealPath(FILE_PATH);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(method + "|" + amount + "|" + details);
            writer.newLine();
        }
    }
}
